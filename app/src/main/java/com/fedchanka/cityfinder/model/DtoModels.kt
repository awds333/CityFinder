package com.fedchanka.cityfinder.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReverseGeocodeResponseDto {
    @SerialName("addresses")
    val addresses: List<AddressHolderDto>? = null
}

@Serializable
class AddressHolderDto {
    @SerialName("address")
    val address: AddressDto? = null
}

@Serializable
class AddressDto {
    @SerialName("municipality")
    val municipality: String? = null
}