package com.example.demo.data

import com.example.demo.AddressEntity

object AddressData {

     val address1 = AddressEntity(
        street = "Street 1",
        number = 1
    ).apply {
        addressId = "09c05881-7dd7-4759-b7ea-b217d379f6da"
        dbVersion = 1
    }

    val address2 = AddressEntity(
        street = "Street 2",
        number = 2
    ).apply {
        addressId = "5f23d186-cbc0-4bd1-b411-3495e86f522e"
        dbVersion = 2
    }

    val address3 = AddressEntity(
        street = "Street 31",
        number = 3
    ).apply {
        addressId = "320714a4-e383-4ff0-bdd4-60589dca0de7"
        dbVersion = 3
    }

    val address4 = AddressEntity(
        street = "Street 4",
        number = 4
    ).apply {
        addressId = "e13ae553-d12a-4c1e-8223-a13140c9d89d"
        dbVersion = 4
    }

    val addresses = listOf(address1, address2, address3, address4)
}