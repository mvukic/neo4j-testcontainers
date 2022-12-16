package com.example.demo.data

import com.example.demo.PersonEntity

object PersonData {

    val person1 = PersonEntity(
        name = "Origen Vedastus"
    ).apply {
        personId = "1046c0c1-7240-43c1-a6aa-fef84ac63abb"
        dbVersion = 1
        addresses = mutableListOf(AddressData.address1)
    }

    val person2 = PersonEntity(
        name = "Pharamond Valentinian"
    ).apply {
        personId = "4aec6a45-58c3-473a-b4b9-9ee479c05785"
        dbVersion = 1
        addresses = mutableListOf(AddressData.address2)
    }

    val person3 = PersonEntity(
        name = "Pliny Julitta"
    ).apply {
        personId = "75e29665-69c6-4053-a195-2a26dd44ea32"
        dbVersion = 1
        addresses = mutableListOf(AddressData.address3, AddressData.address4)
    }

    val people = listOf(person1, person2, person3)

}