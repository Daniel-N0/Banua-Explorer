package com.example.banuaexplorer.feature.destination.data.mapper

import com.example.banuaexplorer.feature.destination.data.local.entity.DestinationEntity
import com.example.banuaexplorer.feature.destination.data.remote.dto.DestinationDto
import com.example.banuaexplorer.feature.destination.domain.model.Destination

fun DestinationDto.toEntity(): DestinationEntity {
    return DestinationEntity(
        id = id,
        name = name,
        kabupaten = kabupaten,
        description = description,
        facilities = facilities,
        dutaPick = dutaPick,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        category = category,
        rating = rating,
        reviewCount = reviewCount
    )
}

fun DestinationEntity.toDomain(): Destination {
    return Destination(
        id = id,
        name = name,
        kabupaten = kabupaten,
        description = description,
        facilities = facilities,
        dutaPick = dutaPick,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        galleryUrls = emptyList()
    )
}