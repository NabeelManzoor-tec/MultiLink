package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.auth.AuthManager
import com.example.data.model.UserRole
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.firebase.auth.FirebaseUser

@Composable
fun AuthScreen(
    onAuthSuccess: (FirebaseUser, UserRole) -> Unit,
    onContinueAsGuest: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedRole by remember { mutableStateOf(UserRole.CREATOR) }
    var isSigningIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandDarkBackground)
            .testTag("auth_screen")
    ) {
        // Ambient background gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            BrandPrimary.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(BrandDarkSurface)
                    .border(2.dp, BrandSecondary, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_multilink_app_icon_1793630259886),
                    contentDescription = "MultiLink Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to MultiLink",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = "Consolidate bio links, monetize digital products & track engagement in real-time",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selector Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_role_selector_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "I am joining as a:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Creator Option
                        val isCreator = selectedRole == UserRole.CREATOR
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isCreator) BrandPrimary.copy(alpha = 0.2f) else BrandDarkSurfaceVariant)
                                .border(
                                    width = if (isCreator) 2.dp else 1.dp,
                                    color = if (isCreator) BrandPrimary else BrandDarkBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedRole = UserRole.CREATOR }
                                .padding(12.dp)
                                .testTag("role_creator_option")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isCreator) BrandPrimary else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isCreator) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Creator",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCreator) TextPrimary else TextSecondary
                                )
                                Text(
                                    text = "Build landing page & sell products",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        // Customer Option
                        val isCustomer = selectedRole == UserRole.CUSTOMER
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isCustomer) BrandSecondary.copy(alpha = 0.2f) else BrandDarkSurfaceVariant)
                                .border(
                                    width = if (isCustomer) 2.dp else 1.dp,
                                    color = if (isCustomer) BrandSecondary else BrandDarkBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedRole = UserRole.CUSTOMER }
                                .padding(12.dp)
                                .testTag("role_customer_option")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = if (isCustomer) BrandSecondary else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isCustomer) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Customer",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCustomer) TextPrimary else TextSecondary
                                )
                                Text(
                                    text = "Buy assets & book sessions",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error display if any
            errorMessage?.let { error ->
                Text(
                    text = error,
                    fontSize = 12.sp,
                    color = Color(0xFFF87171),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            // Google Sign-In / Sign-Up Button
            Button(
                onClick = {
                    isSigningIn = true
                    errorMessage = null
                    (context as? Activity)?.let { act ->
                        AuthManager.signInWithGoogle(
                            activity = act,
                            credentialManager = credentialManager,
                            chosenRole = selectedRole,
                            onSuccess = { user ->
                                isSigningIn = false
                                onAuthSuccess(user, selectedRole)
                            },
                            onError = { err ->
                                isSigningIn = false
                                errorMessage = err
                            },
                            onCancelled = {
                                isSigningIn = false
                            },
                            scope = coroutineScope
                        )
                    }
                },
                enabled = !isSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("google_sign_in_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextPrimary)
            ) {
                if (isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = BrandDarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "G",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (selectedRole == UserRole.CREATOR) "Sign In / Sign Up as Creator" else "Sign In / Sign Up as Customer",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDarkBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guest / Quick Preview exploration
            OutlinedButton(
                onClick = {
                    onContinueAsGuest(selectedRole)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("guest_mode_button"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Text(
                    text = "Explore MultiLink as Guest (${if (selectedRole == UserRole.CREATOR) "Creator Mode" else "Customer Mode"})",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
