package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.ui.theme.BanuaGreen

@Composable
fun ProfileScreen(viewModel: DestinationViewModel, onBackClick: () -> Unit = {}, onEditProfileClick: () -> Unit = {}, onLanguageClick: () -> Unit = {}, onAccountSettingClick: () -> Unit = {}, onLogoutClick: () -> Unit = {}, isDarkMode: Boolean = false, onDarkModeChange: (Boolean) -> Unit = {}) {
    val profile by viewModel.userProfile.collectAsState()

    // State untuk Switch Mode Gelap

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 24.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = BanuaGreen,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Profil Saya",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BanuaGreen
            )
        }

        // --- INFO PROFIL (Foto, Nama, Email) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Box untuk Avatar & Icon Edit
            Box(contentAlignment = Alignment.BottomEnd) {
                // Lingkaran Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (profile.photoUri != null) {
                        AsyncImage(
                            model = profile.photoUri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                    }
                }

                // Badge Icon Edit Hijau
                Surface(
                    color = BanuaGreen,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 4.dp, end = 4.dp)
                        .size(32.dp)
                        .clickable {
                            onEditProfileClick()
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                            .clickable {
                                onEditProfileClick()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Teks Nama & Email
            Text(
                text = profile.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF001F1F)
            )
            Text(
                text = profile.email,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- MENU CARD ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    title = "Edit Profil",
                    showArrow = true,
                    onClick = onEditProfileClick
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Language, // Icon Bola Dunia yang bener!
                    title = "Ganti Bahasa",
                    trailingText = "Indonesia",
                    showArrow = true,
                    onClick = onLanguageClick
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.DarkMode, // Icon Bulan Sabit yang pas!
                    title = "Tampilan/Mode",
                    isSwitch = true,
                    switchState = isDarkMode,
                    onSwitchChange = onDarkModeChange
                )
                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "Pengaturan Akun",
                    showArrow = true,
                    onClick = onAccountSettingClick
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TOMBOL KELUAR (LOGOUT) ---
        Surface(
            color = Color(0xFFFFF0F0), // Merah super muda
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogoutClick() }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Keluar",
                    tint = Color(0xFFE53935) // Merah tegas
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Keluar",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE53935),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// --- KOMPONEN BANTUAN UNTUK ITEM MENU ---
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
            .then(
                if (!isSwitch)
                    Modifier.clickable { onClick() }
                else
                    Modifier
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Bagian Kiri: Icon background bulat + Teks
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF006666).copy(alpha = 0.08f)), // Hijau sangat transparan
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BanuaGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color(0xFF001F1F)
            )
        }

        // Bagian Kanan: Teks Tambahan / Switch / Panah Kanan
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            if (isSwitch) {
                Switch(
                    checked = switchState,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF006666) // Hijau kalau nyala
                    ),
                    modifier = Modifier.height(24.dp)
                )
            } else if (showArrow) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Detail",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}