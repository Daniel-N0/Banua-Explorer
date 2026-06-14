package com.example.banuaexplorer.feature.destination.presentation.viewmodel // Sesuaikan dengan package-mu

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
// Pastikan import User dan AuthUseCase sesuai dengan folder tempat kamu menyimpannya
import com.example.banuaexplorer.feature.auth.domain.model.User
import com.example.banuaexplorer.feature.auth.domain.usecase.AuthUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.fold
import kotlin.text.isBlank
import kotlin.text.isNotEmpty

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    // --- STATE UNTUK UI ---

    // Status loading (untuk menampilkan indikator muter-muter saat nunggu Firebase)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Pesan error (jika password salah, email salah, dsb)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Status sukses login
    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess: StateFlow<Boolean> = _isLoginSuccess.asStateFlow()

    // --- FUNGSI LOGIN ---
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Reset pesan error sebelumnya

            // Panggil Mandor (UseCase) untuk kerja
            val result = authUseCase.login(email, password)

            result.fold(
                onSuccess = {
                    // Jika sukses, ubah status jadi true agar UI tahu harus pindah halaman
                    _isLoginSuccess.value = true
                },
                onFailure = { error ->
                    // Jika gagal, tangkap pesan errornya untuk ditampilkan di UI (misal Toast/Snackbar)
                    _errorMessage.value = error.message ?: "Terjadi kesalahan saat login"
                }
            )

            _isLoading.value = false
        }
    }

    // Fungsi reset state (dipanggil saat sudah pindah halaman agar tidak error saat logout)
    fun resetState() {
        _errorMessage.value = null
        _isLoginSuccess.value = false
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authUseCase.register(name, email, password)

            result.fold(
                onSuccess = {
                    _isLoginSuccess.value = true // Kita anggap sukses daftar langsung login
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Gagal mendaftar akun"
                }
            )
            _isLoading.value = false
        }
    }

    // --- CCTV PEMANTAU SESI (AUTO-LOGIN) ---
    // --- CCTV PEMANTAU SESI (AUTO-LOGIN) YANG LEBIH KUAT ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // init block ini akan langsung berjalan detik pertama ViewModel diciptakan
    init {
        viewModelScope.launch {
            authUseCase.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authUseCase.logout() // Menyuruh Firebase untuk menghapus sesi
        }
    }

    // --- FUNGSI LUPA PASSWORD ---
    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Ketik email kamu dulu di kolom atas ya!")
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Cek inbox/spam email kamu sekarang! Link reset udah dikirim.")
                } else {
                    onResult(false, task.exception?.message ?: "Gagal mengirim email reset")
                }
            }
    }

    // --- FUNGSI UPLOAD FOTO KE CLOUDINARY ---
    fun uploadProfilePhoto(imageUri: Uri, onResult: (Boolean, String) -> Unit) {
        MediaManager.get().upload(imageUri).unsigned("banua_profil")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val secureUrl = resultData?.get("secure_url") as? String ?: ""

                    // --- TAMBAHAN BARU: SIMPAN KE FIREBASE AUTH ---
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && secureUrl.isNotEmpty()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(secureUrl)) // Masukkan link fotonya
                            .build()

                        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onResult(true, secureUrl) // Sukses upload & sukses simpan di Firebase
                            } else {
                                onResult(false, "Gagal menyimpan foto ke profil")
                            }
                        }
                    } else {
                        onResult(true, secureUrl) // Jaga-jaga kalau user null
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onResult(false, error?.description ?: "Upload Gagal")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}