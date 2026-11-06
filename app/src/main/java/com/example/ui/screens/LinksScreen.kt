package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import com.example.ui.components.AvatarPresets
import com.example.ui.components.CreatorAvatarView
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreatorProfile
import com.example.data.model.LinkCategory
import com.example.data.model.LinkItem
import com.example.ui.components.IconResolver
import com.example.ui.components.ThemeSelectorRow
import com.example.ui.theme.BrandDarkBackground
import com.example.ui.theme.BrandDarkBorder
import com.example.ui.theme.BrandDarkSurface
import com.example.ui.theme.BrandDarkSurfaceVariant
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.theme.BrandTertiary
import com.example.ui.theme.BrandWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LinksScreen(
    profile: CreatorProfile?,
    links: List<LinkItem>,
    onUpdateProfile: (CreatorProfile) -> Unit,
    onAddLink: (LinkItem) -> Unit,
    onUpdateLink: (LinkItem) -> Unit,
    onDeleteLink: (LinkItem) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    onSelectTheme: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<LinkCategory?>(null) }

    val filteredLinks = remember(links, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) links
        else links.filter { it.category == selectedCategoryFilter }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Profile Banner Header
                profile?.let { prof ->
                    ProfileHeaderCard(
                        profile = prof,
                        onEditClicked = { showEditProfileDialog = true },
                        onShareClicked = {
                            val cleanHandle = prof.handle.trim().removePrefix("@").lowercase().ifEmpty { "nabeeljutt" }
                            val liveUrl = "https://gen-lang-client-0468598937.web.app/?handle=$cleanHandle"
                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out my MultiLink bio page: $liveUrl")
                                type = "text/plain"
                            }
                            context.startActivity(android.content.Intent.createChooser(sendIntent, "Share MultiLink"))
                        }
                    )
                }
            }

            item {
                // Theme Customizer Row
                profile?.let { prof ->
                    ThemeSelectorRow(
                        selectedThemeId = prof.themeId,
                        onSelectTheme = onSelectTheme
                    )
                }
            }

            item {
                // Filter Categories
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Consolidated Links (${filteredLinks.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "Drag or Toggle",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("All (${links.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary.copy(alpha = 0.25f),
                                selectedLabelColor = BrandPrimary,
                                containerColor = BrandDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                        FilterChip(
                            selected = selectedCategoryFilter == LinkCategory.SOCIAL,
                            onClick = { selectedCategoryFilter = LinkCategory.SOCIAL },
                            label = { Text("Socials") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandSecondary.copy(alpha = 0.25f),
                                selectedLabelColor = BrandSecondary,
                                containerColor = BrandDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                        FilterChip(
                            selected = selectedCategoryFilter == LinkCategory.PRODUCT,
                            onClick = { selectedCategoryFilter = LinkCategory.PRODUCT },
                            label = { Text("Products") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandWarning.copy(alpha = 0.25f),
                                selectedLabelColor = BrandWarning,
                                containerColor = BrandDarkSurface,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }

            // Links List
            items(filteredLinks, key = { it.id }) { link ->
                LinkManageCard(
                    link = link,
                    onToggleActive = { onToggleActive(link.id, it) },
                    onDelete = { onDeleteLink(link) },
                    onCopyUrl = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Link URL", link.url))
                        Toast.makeText(context, "URL copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_link_fab"),
            containerColor = BrandPrimary,
            contentColor = TextPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Link")
        }
    }

    // Add Link Dialog
    if (showAddDialog) {
        AddLinkDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newLink ->
                onAddLink(newLink)
                showAddDialog = false
            }
        )
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && profile != null) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                onUpdateProfile(updated)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun ProfileHeaderCard(
    profile: CreatorProfile,
    onEditClicked: () -> Unit,
    onShareClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("creator_profile_header"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, BrandSecondary, CircleShape)
                            .clickable { onEditClicked() }
                            .testTag("avatar_profile_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        CreatorAvatarView(
                            avatarPath = profile.avatarDrawableName,
                            contentDescription = "Creator Avatar",
                            modifier = Modifier.fillMaxSize()
                        )

                        // Camera edit overlay badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                                .border(1.5.dp, BrandDarkSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change Profile Picture",
                                tint = TextPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (profile.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Creator",
                                    tint = BrandSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = "@${profile.handle}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = profile.location,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onEditClicked,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandDarkSurfaceVariant)
                            .testTag("edit_profile_button")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onShareClicked,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandDarkSurfaceVariant)
                            .testTag("share_profile_button")
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Profile",
                            tint = BrandSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.headline,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrandSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.bio,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // URL badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandDarkBackground)
                    .border(1.dp, BrandDarkBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "https://${profile.customDomain}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSuccess,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LinkManageCard(
    link: LinkItem,
    onToggleActive: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onCopyUrl: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("link_card_${link.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (link.isActive) BrandDarkSurface else BrandDarkSurface.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (link.isFeatured) BrandPrimary.copy(alpha = 0.6f) else BrandDarkBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (link.category) {
                            LinkCategory.SOCIAL -> BrandSecondary.copy(alpha = 0.15f)
                            LinkCategory.PRODUCT -> BrandWarning.copy(alpha = 0.15f)
                            LinkCategory.SERVICE -> BrandPrimary.copy(alpha = 0.15f)
                            LinkCategory.TIP -> BrandSuccess.copy(alpha = 0.15f)
                            LinkCategory.AFFILIATE -> BrandTertiary.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = IconResolver.getIcon(link.iconKey),
                    contentDescription = null,
                    tint = when (link.category) {
                        LinkCategory.SOCIAL -> BrandSecondary
                        LinkCategory.PRODUCT -> BrandWarning
                        LinkCategory.SERVICE -> BrandPrimary
                        LinkCategory.TIP -> BrandSuccess
                        LinkCategory.AFFILIATE -> BrandTertiary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = link.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (link.isActive) TextPrimary else TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (link.badge.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = link.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BrandPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }

                if (link.subtitle.isNotEmpty()) {
                    Text(
                        text = link.subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "${link.totalClicks} clicks",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandSecondary
                    )

                    if (link.price.isNotEmpty()) {
                        Text(
                            text = " • ${link.price}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandSuccess
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onCopyUrl,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy Link",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Link",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Switch(
                    checked = link.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = BrandPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BrandDarkSurfaceVariant
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AddLinkDialog(
    onDismiss: () -> Unit,
    onConfirm: (LinkItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("https://") }
    var subtitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(LinkCategory.SOCIAL) }
    var iconKey by remember { mutableStateOf("youtube") }
    var badge by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Creator Link",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Link Title (e.g. YouTube Channel)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BrandPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_link_title_input")
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Destination URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BrandPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_link_url_input")
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle / Description") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BrandPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = category == LinkCategory.SOCIAL,
                        onClick = { category = LinkCategory.SOCIAL; iconKey = "youtube" },
                        label = { Text("Social", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = category == LinkCategory.PRODUCT,
                        onClick = { category = LinkCategory.PRODUCT; iconKey = "store"; price = "$29.00" },
                        label = { Text("Product", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = category == LinkCategory.SERVICE,
                        onClick = { category = LinkCategory.SERVICE; iconKey = "calendar"; price = "$100/hr" },
                        label = { Text("Service", fontSize = 11.sp) }
                    )
                }

                if (category == LinkCategory.PRODUCT || category == LinkCategory.SERVICE) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price / Rate (e.g. $29.00 or $150/hr)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            LinkItem(
                                title = title,
                                url = url,
                                category = category,
                                iconKey = iconKey,
                                subtitle = subtitle,
                                price = price,
                                badge = badge,
                                isActive = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.testTag("confirm_add_link_button")
            ) {
                Text("Add Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = BrandDarkSurface
    )
}

@Composable
fun EditProfileDialog(
    currentProfile: CreatorProfile,
    onDismiss: () -> Unit,
    onSave: (CreatorProfile) -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile.displayName) }
    var handle by remember { mutableStateOf(currentProfile.handle) }
    var headline by remember { mutableStateOf(currentProfile.headline) }
    var bio by remember { mutableStateOf(currentProfile.bio) }
    var location by remember { mutableStateOf(currentProfile.location) }
    var avatarDrawableName by remember { mutableStateOf(currentProfile.avatarDrawableName) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarDrawableName = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Creator Profile",
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Picture Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandDarkSurfaceVariant)
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Profile Picture",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Avatar Preview with Glow
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, BrandSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CreatorAvatarView(
                                avatarPath = avatarDrawableName,
                                contentDescription = "Avatar Preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Upload from Gallery Button (Photo Picker)
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("upload_avatar_button")
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = BrandDarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Choose from Gallery",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDarkBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Or choose a Creator Persona:",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AvatarPresets.presets.forEach { preset ->
                                val isSelected = avatarDrawableName == preset.id
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { avatarDrawableName = preset.id }
                                        .padding(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) BrandPrimary else BrandDarkBorder,
                                                shape = CircleShape
                                            )
                                    ) {
                                        Image(
                                            painter = painterResource(id = preset.resId),
                                            contentDescription = preset.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = preset.title.split(" ").first(),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BrandPrimary else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = handle,
                        onValueChange = { handle = it },
                        label = { Text("Handle (@username)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        label = { Text("Headline / Tagline") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio (up to 200 chars)") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            displayName = displayName,
                            handle = handle,
                            headline = headline,
                            bio = bio,
                            location = location,
                            avatarDrawableName = avatarDrawableName,
                            customDomain = "gen-lang-client-0468598937.web.app/?handle=${handle.trim().removePrefix("@").lowercase().ifEmpty { "nabeeljutt" }}"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = BrandDarkSurface
    )
}
