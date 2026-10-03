package com.vittiq.android.ui.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.theme.AmberGold
import com.vittiq.android.theme.BrightSnow
import com.vittiq.android.theme.CardBorderSubtle
import com.vittiq.android.theme.CharcoalBlue
import com.vittiq.android.theme.InkBlack
import com.vittiq.android.theme.OceanMist
import com.vittiq.android.theme.OceanMistSoft
import com.vittiq.android.theme.SurfaceWhite
import java.io.File

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onUpdateProfile: (UserProfile) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    // Decode avatar bitmap if image exists on disk
    val avatarBitmap = remember(uiState.userProfile?.avatarPath) {
        uiState.userProfile?.avatarPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    // Compute display name
    val displayName = remember(uiState.userProfile) {
        val fName = uiState.userProfile?.firstName?.trim().orEmpty()
        val lName = uiState.userProfile?.lastName?.trim().orEmpty()
        when {
            fName.isNotEmpty() && lName.isNotEmpty() -> "$fName $lName"
            fName.isNotEmpty() -> fName
            lName.isNotEmpty() -> lName
            else -> "Kunal Khandelwal"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrightSnow),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Avatar & Identity Block (with Edit Actions)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular Avatar with Edit Badge
                Box(
                    modifier = Modifier.size(108.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Ring
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(AmberGold)
                            .clickable { showEditDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(CharcoalBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap,
                                    contentDescription = "Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = uiState.userProfile?.avatarInitial ?: "K",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                            }
                        }
                    }

                    // Edit Badge Icon at bottom-end of avatar circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(AmberGold)
                            .border(2.dp, BrightSnow, CircleShape)
                            .clickable { showEditDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Picture",
                            tint = InkBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display Name + Edit Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Name",
                            tint = CharcoalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Handle
                Text(
                    text = uiState.userProfile?.handle ?: "@kunal.khandelwal",
                    fontSize = 14.sp,
                    color = CharcoalBlue
                )
            }
        }

        // 2. Sync Status Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(OceanMistSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = OceanMist,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Login and sync coming soon",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkBlack
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your profile is ready. Secure sign-in and connected account syncing will arrive in the next update.",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = CharcoalBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Two side-by-side status sub-cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusSubCard(
                            label = "Login",
                            status = "Coming soon",
                            modifier = Modifier.weight(1f)
                        )
                        StatusSubCard(
                            label = "Sync",
                            status = "Coming soon",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Highlight Info Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8FAF6))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Keep this screen handy for when secure login and connected account syncing are released.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = CharcoalBlue
                        )
                    }
                }
            }
        }

        // 3. Primary Action Button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { /* Sign in handler */ },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = InkBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        text = "Sign in to unlock sync",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Available in the next release",
                    fontSize = 12.sp,
                    color = CharcoalBlue,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        EditProfileDialog(
            userProfile = uiState.userProfile,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProfile ->
                onUpdateProfile(updatedProfile)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf(userProfile?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userProfile?.lastName ?: "") }
    var handle by remember { mutableStateOf(userProfile?.handle ?: "") }
    var currentAvatarPath by remember { mutableStateOf(userProfile?.avatarPath) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveAvatarToInternalStorage(context, it)
            if (savedPath != null) {
                currentAvatarPath = savedPath
            }
        }
    }

    val previewBitmap = remember(currentAvatarPath) {
        currentAvatarPath?.let { path ->
            try {
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    val previewInitial = remember(firstName) {
        firstName.trim().take(1).uppercase().ifEmpty {
            userProfile?.avatarInitial ?: "K"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar preview with tap to choose picture
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(AmberGold)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(CharcoalBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            if (previewBitmap != null) {
                                Image(
                                    bitmap = previewBitmap,
                                    contentDescription = "Preview Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = previewInitial,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SurfaceWhite
                                )
                            }
                        }
                    }

                    // Camera badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(AmberGold)
                            .border(2.dp, SurfaceWhite, CircleShape)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Pick photo",
                            tint = InkBlack,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Choose photo / Remove photo buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                        Text(
                            text = if (currentAvatarPath == null) "Add Photo" else "Change Photo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = InkBlack
                        )
                    }
                    if (currentAvatarPath != null) {
                        TextButton(onClick = { currentAvatarPath = null }) {
                            Text(
                                text = "Remove Photo",
                                fontSize = 13.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        focusedLabelColor = InkBlack
                    )
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        focusedLabelColor = InkBlack
                    )
                )

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Handle / Username") },
                    placeholder = { Text("@username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        focusedLabelColor = InkBlack
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fName = firstName.trim().ifEmpty { userProfile?.firstName ?: "User" }
                    val lName = lastName.trim()
                    val rawHandle = handle.trim().ifEmpty { userProfile?.handle ?: "@user" }
                    val finalHandle = if (rawHandle.startsWith("@")) rawHandle else "@$rawHandle"
                    val initial = fName.take(1).uppercase()

                    val updated = userProfile?.copy(
                        firstName = fName,
                        lastName = lName,
                        handle = finalHandle,
                        avatarInitial = initial,
                        avatarPath = currentAvatarPath
                    ) ?: UserProfile(
                        id = 1,
                        firstName = fName,
                        lastName = lName,
                        handle = finalHandle,
                        avatarInitial = initial,
                        avatarPath = currentAvatarPath
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGold,
                    contentColor = InkBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CharcoalBlue)
            }
        },
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(20.dp)
    )
}

private fun saveAvatarToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val avatarsDir = File(context.filesDir, "avatars")
        if (!avatarsDir.exists()) {
            avatarsDir.mkdirs()
        } else {
            avatarsDir.listFiles()?.forEach { it.delete() }
        }
        val file = File(avatarsDir, "avatar_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun StatusSubCard(
    label: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BrightSnow)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CharcoalBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = status,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = InkBlack
        )
    }
}
