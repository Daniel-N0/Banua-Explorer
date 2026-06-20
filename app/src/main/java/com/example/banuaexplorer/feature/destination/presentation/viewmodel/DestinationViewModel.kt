package com.example.banuaexplorer.feature.destination.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banuaexplorer.datastore.LanguagePreference
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Partner
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.example.banuaexplorer.feature.destination.domain.usecase.DestinationUseCase
import com.example.banuaexplorer.network.RetrofitClient // Pastikan import ini sesuai
import com.example.banuaexplorer.network.RouteRequest   // Pastikan import ini sesuai
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val photoUri: String? = null
)

class DestinationViewModel(private val useCase: DestinationUseCase) : ViewModel() {
    private val _isEnglish = MutableStateFlow(false)
    val isEnglish: StateFlow<Boolean> = _isEnglish.asStateFlow()
    // ==========================================
    // 1. STATE LOKASI & PETA
    // ==========================================
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    private val _isLocationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted: StateFlow<Boolean> = _isLocationPermissionGranted.asStateFlow()

    private val _selectedMapDestination = MutableStateFlow<Destination?>(null)
    val selectedMapDestination: StateFlow<Destination?> = _selectedMapDestination.asStateFlow()

    // --- STATE PENCARIAN & FILTER ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRegion = MutableStateFlow("Kalimantan Selatan")
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // --- STATE RUTE ORS (BARU: BEST PRACTICE) ---
    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private val _isLoadingRoute = MutableStateFlow(false)
    val isLoadingRoute: StateFlow<Boolean> = _isLoadingRoute.asStateFlow()

    // --- STATE JARAK DAN DURASI RUTE ---
    private val _routeDistance = MutableStateFlow(0.0)
    val routeDistance: StateFlow<Double> = _routeDistance.asStateFlow()

    private val _routeDuration = MutableStateFlow(0.0)
    val routeDuration: StateFlow<Double> = _routeDuration.asStateFlow()


    // ==========================================
    // 2. STATE DATA UTAMA
    // ==========================================
    private val _destinations = MutableStateFlow<List<Destination>>(emptyList())
    val destinations: StateFlow<List<Destination>> = _destinations.asStateFlow()

    // Flow gabungan: Otomatis memfilter 'destinations' berdasarkan 'searchQuery'
    val filteredDestinations: StateFlow<List<Destination>> = combine(
        destinations,
        _searchQuery
    ) { destList, query ->
        if (query.isBlank()) {
            destList
        } else {
            destList.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _partners = MutableStateFlow<List<Partner>>(emptyList())
    val partners: StateFlow<List<Partner>> = _partners.asStateFlow()

    private val _ambassadors = MutableStateFlow<List<Ambassador>>(emptyList())
    val ambassadors: StateFlow<List<Ambassador>> = _ambassadors.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    val favoriteDestinations: StateFlow<List<Destination>> =
        useCase.getLocalFavorites()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _selectedDestination = MutableStateFlow<Destination?>(null)
    val selectedDestination: StateFlow<Destination?> = _selectedDestination.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedAmbassador = MutableStateFlow<Ambassador?>(null)
    val selectedAmbassador: StateFlow<Ambassador?> = _selectedAmbassador.asStateFlow()

    fun selectAmbassador(ambassador: Ambassador) {
        _selectedAmbassador.value = ambassador
    }


    init {
        observeLocalData()

        refreshAllData()
        getAmbassadorsFromFirebase()
        loadCurrentUserProfile()
    }

    // ==========================================
    // 3. FUNGSI LOGIKA (PETA & RUTE)
    // ==========================================
    fun updateUserLocation(lat: Double, lng: Double) {
        _userLocation.value = LatLng(lat, lng)
    }

    fun clearMapState() {
        _selectedMapDestination.value = null
        _routePoints.value = emptyList()
        _routeDistance.value = 0.0
        _routeDuration.value = 0.0
    }

    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _isLocationPermissionGranted.value = isGranted
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onRegionSelect(region: String) {
        _selectedRegion.value = region
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun selectDestinationForMap(destination: Destination) {
        _selectedMapDestination.value = destination
    }

    fun clearSelectedMapDestination() {
        _selectedMapDestination.value = null
    }

    fun clearRoute() {
        _routePoints.value = emptyList()
    }

    private var lastRouteDestinationId: String? = null
    /**
     * Mengambil rute dari ORS secara asinkron di Background Thread (Dispatchers.IO)
     * Ini mencegah UI Lag/Freeze saat menunggu balasan dari server.
     */
    fun fetchRoute(apiKey: String, start: LatLng, end: LatLng) {
        viewModelScope.launch {
            _isLoadingRoute.value = true
            try {
                // withContext(Dispatchers.IO) memaksa proses jaringan berjalan di background thread
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.getRoute(
                        apiKey = apiKey,
                        request = RouteRequest(
                            coordinates = listOf(
                                listOf(start.longitude, start.latitude),
                                listOf(end.longitude, end.latitude)
                            )
                        )
                    )
                }

                val points = response.features.firstOrNull()
                    ?.geometry
                    ?.coordinates
                    ?.map { LatLng(it[1], it[0]) } // Konversi [Lng, Lat] ke LatLng
                    ?: emptyList()

                _routePoints.value = points

                response.features.firstOrNull()?.let { feature ->

                    _routeDistance.value =
                        feature.properties.summary.distance

                    _routeDuration.value =
                        feature.properties.summary.duration

                    Log.d(
                        "ROUTE_VM",
                        "Distance = ${feature.properties.summary.distance} meter"
                    )

                    Log.d(
                        "ROUTE_VM",
                        "Duration = ${feature.properties.summary.duration} detik"
                    )

                }
                Log.d("ROUTE_VM", "Rute berhasil diambil. Jumlah titik: ${points.size}")

            } catch (e: Exception) {
                Log.e("ROUTE_VM_ERROR", "Gagal mengambil rute: ${e.message}")
                _routePoints.value = emptyList()
            } finally {
                _isLoadingRoute.value = false
            }
        }
    }


    fun navigateToDestination(
        apiKey: String,
        destination: Destination
    ) {
        val currentLocation = _userLocation.value ?: return

        _selectedMapDestination.value = destination

        fetchRoute(
            apiKey = apiKey,
            start = currentLocation,
            end = LatLng(
                destination.latitude,
                destination.longitude
            )
        )
    }


    // ==========================================
    // 4. FUNGSI LOGIKA (DATA UTAMA)
    // ==========================================
    private fun observeLocalData() {
        viewModelScope.launch {
            useCase.getDestinations()
                .catch { }
                .collect { _destinations.value = it }
        }

        viewModelScope.launch {
            useCase.getPartners()
                .catch { }
                .collect { _partners.value = it }
        }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            useCase.refreshDestinations()
            useCase.refreshPartners()
            _isLoading.value = false
        }
    }

    fun getDestinationsByCategory(category: String): StateFlow<List<Destination>> {
        return destinations.map { list ->
            list.filter { it.category.equals(category, ignoreCase = true) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun selectDestination(destination: Destination) {
        _selectedDestination.value = destination
    }

    fun loadReviewsForDestination(destId: String) {
        viewModelScope.launch {
            useCase.getReviews(destId)
                .catch { }
                .collect { reviewList -> _reviews.value = reviewList }
        }
    }

    fun saveReview(review: Review) {
        viewModelScope.launch { useCase.addReview(review) }
    }


    fun toggleFavorite(destination: Destination) {
        viewModelScope.launch {

            val currentFavorites = favoriteDestinations.value

            Log.d("FAVORITE", "Current size = ${currentFavorites.size}")

            if (currentFavorites.any { it.id == destination.id }) {
                Log.d("FAVORITE", "REMOVE ${destination.name}")
                useCase.removeFavorite(destination)
            } else {
                Log.d("FAVORITE", "SAVE ${destination.name}")
                useCase.saveFavorite(destination)
            }
        }
    }

    /**
     * Catatan Best Practice: Ke depannya, pemanggilan FirebaseFirestore ini
     * sebaiknya dipindahkan ke dalam class Repository agar ViewModel benar-benar bersih.
     */
    fun getAmbassadorsFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("ambassadors")
            .get()
            .addOnSuccessListener { result ->
                val ambassadorList = mutableListOf<Ambassador>()
                for (document in result) {
                    val ambassador = Ambassador(
                        id = document.id, // <--- UBAH JADI INI! Ini ngambil Document ID aslinya ("duta-001")
                        name = document.getString("name") ?: "Nama Duta",
                        bio = document.getString("bio") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        followers = document.getLong("followers")?.toInt() ?: 0,
                        kabupaten = document.getString("kabupaten") ?: ""
                    )
                    ambassadorList.add(ambassador)
                }
                _ambassadors.value = ambassadorList
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    fun updateProfile(name: String, email: String, phone: String, city: String, photoUri: String?) {
        _userProfile.value = _userProfile.value.copy(
            name = name, email = email, phone = phone, city = city, photoUri = photoUri
        )
    }

    fun getCurrentUserProfile(): UserProfile {
        val currentUser = FirebaseAuth.getInstance().currentUser

        return UserProfile(
            name = currentUser?.displayName ?: "",
            email = currentUser?.email ?: "",
            phone = "",
            city = "",
            photoUri = currentUser?.photoUrl?.toString()
        )
    }

    fun saveProfile(
        name: String,
        email: String,
        phone: String,
        city: String,
        photoUri: String?
    ) {

        val user = FirebaseAuth.getInstance().currentUser ?: return

        viewModelScope.launch {

            val request = userProfileChangeRequest {
                displayName = name

                this.photoUri = photoUri?.let {
                    Uri.parse(it)
                }
            }

            user.updateProfile(request)
                .addOnSuccessListener {
                    loadCurrentUserProfile()
                }

            _userProfile.value = UserProfile(
                name = name,
                email = user.email.orEmpty(),
                phone = phone,
                city = city,
                photoUri = photoUri
            )
        }
    }

    fun loadCurrentUserProfile() {

        val user = FirebaseAuth.getInstance().currentUser ?: return

        _userProfile.value = UserProfile(
            name = user.displayName.orEmpty(),
            email = user.email.orEmpty(),
            phone = "",
            city = "",
            photoUri = user.photoUrl?.toString()
        )
    }

    // =========================================
    // FITUR BREAD REVIEW
    // =========================================

    fun getReviews(destinationId: String): kotlinx.coroutines.flow.Flow<List<com.example.banuaexplorer.feature.destination.domain.model.Review>> {
        return useCase.getReviews(destId = destinationId)
    }

    fun addReview(review: com.example.banuaexplorer.feature.destination.domain.model.Review) {
        viewModelScope.launch {
            useCase.addReview(review)
        }
    }

    fun deleteReview(review: com.example.banuaexplorer.feature.destination.domain.model.Review) {
        viewModelScope.launch {
            useCase.deleteReview(review)
        }
    }
}