package com.example.demo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PersonModel(
    @SerialName("personId")
    var personId: String,

    @SerialName("name")
    var name: String,

    @SerialName("addresses")
    val addresses: List<AddressModel>
)

@Serializable
data class AddressModel(
    @SerialName("addressId")
    val addressId: String,

    @SerialName("number")
    val number: Int,

    @SerialName("street")
    val street: String
)
