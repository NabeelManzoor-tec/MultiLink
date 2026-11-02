package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object AuthManager {
    private const val TAG = "MultiLinkAuth"

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.CREATOR)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun initAuthListener(context: Context, scope: CoroutineScope) {
        _currentUser.value = Firebase.auth.currentUser
        Firebase.auth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
            auth.currentUser?.let { user ->
                scope.launch {
                    fetchOrInitUserInFirestore(context, user)
                }
            }
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun attemptAutoSignIn(
        context: Context,
        credentialManager: CredentialManager,
        onSuccess: (FirebaseUser) -> Unit,
        onUnauthenticated: () -> Unit,
        scope: CoroutineScope
    ) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            _currentUser.value = user
            onSuccess(user)
            return
        }

        val clientId = try {
            context.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.w(TAG, "default_web_client_id not found: ${e.message}")
            onUnauthenticated()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    authResult.user?.let { signedInUser ->
                        _currentUser.value = signedInUser
                        fetchOrInitUserInFirestore(context, signedInUser)
                        onSuccess(signedInUser)
                    } ?: onUnauthenticated()
                } else {
                    onUnauthenticated()
                }
            } catch (e: Exception) {
                Log.d(TAG, "Silent auto-sign-in skipped: ${e.message}")
                onUnauthenticated()
            }
        }
    }

    fun signInWithGoogle(
        activity: Activity,
        credentialManager: CredentialManager,
        chosenRole: UserRole,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit,
        onCancelled: () -> Unit,
        scope: CoroutineScope
    ) {
        val clientId = try {
            activity.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            val err = "Google Sign-In configuration missing: default_web_client_id"
            Log.e(TAG, err, e)
            onError(err)
            return
        }

        _isLoading.value = true
        _userRole.value = chosenRole

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    val authResult = Firebase.auth.signInWithCredential(authCredential).await()
                    val user = authResult.user
                    if (user != null) {
                        _currentUser.value = user
                        syncUserToFirestore(activity, user, chosenRole)
                        _isLoading.value = false
                        onSuccess(user)
                    } else {
                        _isLoading.value = false
                        onError("Failed to obtain signed-in Firebase user.")
                    }
                } else {
                    _isLoading.value = false
                    onError("Unexpected credential type received.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
                _isLoading.value = false
                onCancelled()
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign-In failed: ${e.message}", e)
                _isLoading.value = false
                onError(e.localizedMessage ?: "Sign-in failed. Please try again.")
            }
        }
    }

    fun signOut(
        context: Context,
        credentialManager: CredentialManager,
        onComplete: () -> Unit,
        scope: CoroutineScope
    ) {
        Firebase.auth.signOut()
        _currentUser.value = null
        scope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear credential state", e)
            } finally {
                onComplete()
            }
        }
    }

    private suspend fun syncUserToFirestore(context: Context, user: FirebaseUser, role: UserRole) {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(dbId)
            val userRef = db.collection("users").document(user.uid)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                val data = mapOf(
                    "userId" to user.uid,
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: "Creator"),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "role" to role.name,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                userRef.set(data).await()
            } else {
                val existingRole = snapshot.getString("role")
                if (existingRole != null) {
                    _userRole.value = if (existingRole == "CUSTOMER") UserRole.CUSTOMER else UserRole.CREATOR
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore sync note: ${e.message}")
        }
    }

    private suspend fun fetchOrInitUserInFirestore(context: Context, user: FirebaseUser) {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            val db = FirebaseFirestore.getInstance(dbId)
            val userDoc = db.collection("users").document(user.uid).get().await()
            if (userDoc.exists()) {
                val roleStr = userDoc.getString("role") ?: "CREATOR"
                _userRole.value = if (roleStr == "CUSTOMER") UserRole.CUSTOMER else UserRole.CREATOR
            }
        } catch (e: Exception) {
            Log.d(TAG, "Could not fetch user role: ${e.message}")
        }
    }
}
