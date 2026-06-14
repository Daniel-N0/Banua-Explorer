package com.example.banuaexplorer.feature.destination.data.mapper // Sesuaikan dengan nama package-mu

import com.example.banuaexplorer.feature.destination.data.local.entity.FavoriteEntity
import com.example.banuaexplorer.feature.destination.domain.model.Destination

// 1. Menerjemahkan dari UI (Destination) ke Database (FavoriteEntity)
// Digunakan saat tombol Love ditekan (Insert/Save)
fun Destination.toFavoriteEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = this.id,
        name = this.name,
        kabupaten = this.kabupaten,
        description = this.description,
        category = this.category,
        imageUrl = this.imageUrl,
        latitude = this.latitude,
        longitude = this.longitude,
        dutaPick = this.dutaPick,
        facilities = this.facilities,
        rating = this.rating,
        reviewCount = this.reviewCount
    )
}

// 2. Menerjemahkan dari Database (FavoriteEntity) ke UI (Destination)
// Digunakan saat menampilkan daftar favorit di FavoriteScreen
fun FavoriteEntity.toDomainModel(): Destination {
    return Destination(
        id = this.id,
        name = this.name,
        kabupaten = this.kabupaten,
        description = this.description,
        category = this.category,
        imageUrl = this.imageUrl,
        latitude = this.latitude,
        longitude = this.longitude,
        dutaPick = this.dutaPick,
        facilities = this.facilities,
        rating = this.rating,
        reviewCount = this.reviewCount,
        galleryUrls = emptyList()
    )
}

// 3. (Opsional tapi berguna) Menerjemahkan List sekaligus
fun List<FavoriteEntity>.toDomainModelList(): List<Destination> {
    return this.map { it.toDomainModel() }
}