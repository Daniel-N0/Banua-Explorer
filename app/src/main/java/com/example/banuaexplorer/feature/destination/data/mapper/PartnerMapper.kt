package com.example.banuaexplorer.feature.destination.data.mapper

// Import yang benar (pastikan lokasinya sama dengan foldermu)
import com.example.banuaexplorer.feature.destination.data.local.entity.PartnerEntity
import com.example.banuaexplorer.feature.destination.data.remote.dto.PartnerDto
import com.example.banuaexplorer.feature.destination.domain.model.Partner

fun PartnerDto.toEntity(): PartnerEntity {
    return PartnerEntity(
        id = id,
        name = name,
        type = type,
        imageUrl = imageUrl,
        description = description,
        website = website,
        phone = phone,
        email = email
    )
}

fun PartnerEntity.toDomain(): Partner {
    return Partner(
        id = id,
        name = name,
        type = type,
        imageUrl = imageUrl,
        description = description,
        website = website,
        phone = phone,
        email = email
    )
}