package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomerOrder
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

@Composable
fun CustomerHubScreen(
    userEmail: String,
    userName: String,
    onViewCreatorLandingPage: () -> Unit,
    onSwitchToCreatorMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("LIBRARY") } // "LIBRARY", "SESSIONS", "PAYMENTS", "DISCOVER"
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var savedCards by remember { mutableStateOf(listOf("Visa ending in •••• 4242 (Exp 08/28)", "Mastercard ending in •••• 8821 (Exp 11/27)")) }

    val sampleOrders = remember {
        listOf(
            CustomerOrder(
                id = "ORD-9481",
                itemTitle = "The Digital Frontier: Cyberpunk 3D Toolkit 2026",
                itemCategory = "PRODUCT",
                price = 39.00,
                creatorHandle = "alexa_aura",
                creatorName = "Alexa Rivers",
                status = "COMPLETED",
                downloadUrl = "https://downloads.multilink.bio/pack-2026.zip"
            ),
            CustomerOrder(
                id = "ORD-8204",
                itemTitle = "1-on-1 Creative Direction & Portfolio Review",
                itemCategory = "SERVICE",
                price = 150.00,
                creatorHandle = "alexa_aura",
                creatorName = "Alexa Rivers",
                status = "CONFIRMED",
                sessionDate = "Tomorrow at 2:00 PM (Google Meet)"
            ),
            CustomerOrder(
                id = "ORD-5192",
                itemTitle = "Support Tip • Matcha Fund",
                itemCategory = "TIP",
                price = 10.00,
                creatorHandle = "alexa_aura",
                creatorName = "Alexa Rivers",
                status = "COMPLETED"
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Customer Welcome Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_welcome_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.2f))
                            .border(1.5.dp, BrandPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (userName.isNotBlank()) userName else "Client / Customer",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CUSTOMER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandSecondary.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = if (userEmail.isNotBlank()) userEmail else "Signed in with Google Account",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = "3 orders & bookings active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = BrandSuccess,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Customer Navigation Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == "LIBRARY",
                    onClick = { selectedTab = "LIBRARY" },
                    label = { Text("My Library & Products") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandWarning.copy(alpha = 0.25f),
                        selectedLabelColor = BrandWarning,
                        containerColor = BrandDarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "SESSIONS",
                    onClick = { selectedTab = "SESSIONS" },
                    label = { Text("Booked Calls") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary.copy(alpha = 0.25f),
                        selectedLabelColor = BrandPrimary,
                        containerColor = BrandDarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "PAYMENTS",
                    onClick = { selectedTab = "PAYMENTS" },
                    label = { Text("Payment Methods & Tips") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandSuccess.copy(alpha = 0.25f),
                        selectedLabelColor = BrandSuccess,
                        containerColor = BrandDarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "DISCOVER",
                    onClick = { selectedTab = "DISCOVER" },
                    label = { Text("Explore Creators") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandSecondary.copy(alpha = 0.25f),
                        selectedLabelColor = BrandSecondary,
                        containerColor = BrandDarkSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        when (selectedTab) {
            "LIBRARY" -> {
                item {
                    Text(
                        text = "Purchased Digital Products & Assets",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(sampleOrders.filter { it.itemCategory == "PRODUCT" }) { order ->
                    CustomerProductCard(order = order)
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Creator Tip Receipts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(sampleOrders.filter { it.itemCategory == "TIP" }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BrandSuccess.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Coffee, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.itemTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                                Text("To: @${order.creatorHandle} (${order.creatorName})", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text("$${order.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandSuccess)
                        }
                    }
                }
            }

            "SESSIONS" -> {
                item {
                    Text(
                        text = "Upcoming 1-on-1 Strategy & Consultation Calls",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(sampleOrders.filter { it.itemCategory == "SERVICE" }) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.itemTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "CONFIRMED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSuccess,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BrandSuccess.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Host: ${order.creatorName} (@${order.creatorHandle})",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Text(
                                text = "Scheduled: ${order.sessionDate}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Opening Google Meet room...", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Join Video Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = onViewCreatorLandingPage,
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkSurfaceVariant),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Creator Bio", fontSize = 12.sp, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }

            "PAYMENTS" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Saved Payment Methods",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Used for 1-tap tipping & digital checkout",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { showAddPaymentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandDarkBackground)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Method", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                        }
                    }
                }

                // Method 1: Google Pay
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Google Pay", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DEFAULT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BrandSuccess,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BrandSuccess.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text("Linked to Google Play Billing Account", fontSize = 11.sp, color = TextSecondary)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = BrandSuccess, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Saved Cards
                items(savedCards) { cardTitle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandSecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cardTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text("Verified for Tips & Purchases", fontSize = 11.sp, color = TextSecondary)
                            }
                            TextButton(
                                onClick = {
                                    savedCards = savedCards.filter { it != cardTitle }
                                    Toast.makeText(context, "Card removed", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Remove", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Recent Tip Receipts
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Recent Tips & Contributions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Coffee, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Tip to @alexa_aura", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                        Text("Goal: Unreal Rig Fund • Google Pay", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                Text("+$10.00", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = BrandSuccess)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Transaction ID: #TIP-98217", fontSize = 10.sp, color = TextMuted)
                                Text("0% Platform Fee", fontSize = 10.sp, color = BrandSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "DISCOVER" -> {
                item {
                    Text(
                        text = "Featured Creators on MultiLink",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewCreatorLandingPage() },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, BrandSecondary, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.creator_avatar_default_1793630325308),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Alexa Rivers", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandSecondary, modifier = Modifier.size(14.dp))
                                }
                                Text("@alexa_aura", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.Medium)
                                Text("50+ Cyberpunk Shaders & 1-on-1 Mentorship", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                            }

                            Button(
                                onClick = onViewCreatorLandingPage,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Visit Page", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDarkBackground)
                            }
                        }
                    }
                }
            }
        }

        // Creator Switch Callout
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchToCreatorMode() }
                    .testTag("switch_to_creator_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B2B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Are you a Creator?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Switch to Creator Studio to build your bio link & sell products",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onSwitchToCreatorMode,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Creator Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showAddPaymentDialog) {
        AddCustomerCardDialog(
            onDismiss = { showAddPaymentDialog = false },
            onAddCard = { newCard ->
                savedCards = savedCards + newCard
                showAddPaymentDialog = false
                Toast.makeText(context, "Card added successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AddCustomerCardDialog(
    onDismiss: () -> Unit,
    onAddCard: (String) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvc by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = BrandSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Payment Method", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Add a credit or debit card for 1-tap creator tips and digital product downloads.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { cardHolder = it },
                    label = { Text("Name on Card") },
                    placeholder = { Text("e.g. Alexa Rivers") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 19) cardNumber = it },
                    label = { Text("Card Number") },
                    placeholder = { Text("4242 •••• •••• 4242") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cardExpiry,
                        onValueChange = { if (it.length <= 5) cardExpiry = it },
                        label = { Text("Exp (MM/YY)") },
                        placeholder = { Text("12/28") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = cardCvc,
                        onValueChange = { if (it.length <= 4) cardCvc = it },
                        label = { Text("CVC") },
                        placeholder = { Text("123") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Encrypted via PCI-DSS compliant tokenization", fontSize = 10.sp, color = TextMuted)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val last4 = if (cardNumber.length >= 4) cardNumber.takeLast(4) else "4242"
                    val cardType = if (cardNumber.startsWith("4")) "Visa" else "Mastercard"
                    val exp = if (cardExpiry.isNotBlank()) cardExpiry else "12/28"
                    onAddCard("$cardType ending in •••• $last4 (Exp $exp)")
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess)
            ) {
                Text("Save Card", fontWeight = FontWeight.Bold, color = BrandDarkBackground)
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
fun CustomerProductCard(order: CustomerOrder) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BrandDarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.digital_product_mockup_1793630338547),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.itemTitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "By @${order.creatorHandle} • Order #${order.id}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "$${order.price.toInt()} Paid • Lifetime Access",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandSuccess,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = {
                    Toast.makeText(context, "Downloading asset package...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandWarning.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Download, contentDescription = "Download Asset", tint = BrandWarning, modifier = Modifier.size(18.dp))
            }
        }
    }
}
