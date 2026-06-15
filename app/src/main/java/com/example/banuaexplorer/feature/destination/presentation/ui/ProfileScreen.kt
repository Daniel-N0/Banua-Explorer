package com.example.banuaexplorer.feature.destination.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate // <-- IMPORT SAKTI
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource // <-- IMPORT INI
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat // <-- IMPORT SAKTI
import coil.compose.AsyncImage
import com.example.banuaexplorer.R // <-- IMPORT R RESOURCE LU
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel

@Composable
fun ProfileScreen(
    viewModel: DestinationViewModel,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    isDarkMode: Boolean = false,
    onDarkModeChange: (Boolean) -> Unit = {},
    onPhotoSelected: (Uri) -> Unit = {},
    userName: String = "Petualang",
    userEmail: String = "",
    profilePictureUrl: String = ""
) {

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Membaca bahasa sistem secara otomatis buat nentuin posisi On/Off Switch
    var isEnglish by remember {
        mutableStateOf(AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("en"))
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            onPhotoSelected(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // --- SUDAH PAKAI STRING RESOURCE ---
            Text(
                text = stringResource(id = R.string.my_profile),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(model = selectedImageUri, contentDescription = "Foto Profil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                        profilePictureUrl.isNotBlank() -> {
                            AsyncImage(model = profilePictureUrl, contentDescription = "Foto Profil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                        else -> {
                            Icon(Icons.Default.Person, contentDescription = "Avatar", modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 4.dp, end = 4.dp).size(32.dp).clickable {
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.padding(6.dp).size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            Text(text = userEmail, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(modifier = Modifier.padding(vertical = 8.dp)) {

                // --- SUDAH PAKAI STRING RESOURCE SEMUA ---
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    title = stringResource(id = R.string.edit_profile),
                    showArrow = true,
                    onClick = onEditProfileClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Language,
                    title = stringResource(id = R.string.language),
                    trailingText = stringResource(id = R.string.english_label),
                    isSwitch = true,
                    switchState = isEnglish,
                    onSwitchChange = { isEng ->
                        isEnglish = isEng

                        // Eksekusi ganti bahasa tingkat OS Android!
                        val localeCode = if (isEng) "en" else "id"
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(localeCode)
                        )
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(id = R.string.dark_mode),
                    isSwitch = true,
                    switchState = isDarkMode,
                    onSwitchChange = onDarkModeChange
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().clickable { onLogoutClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Keluar", tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))

                // --- SUDAH PAKAI STRING RESOURCE ---
                Text(
                    text = stringResource(id = R.string.logout),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    showArrow: Boolean = false,
    isSwitch: Boolean = false,
    switchState: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isSwitch) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            trailingText?.let {
                Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (isSwitch) {
                Switch(
                    checked = switchState, // Menggunakan state dari argumen
                    onCheckedChange = onSwitchChange, // Lempar balik ke atas
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.surface,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            } else if (showArrow) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Detail", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            }
        }
    }
}