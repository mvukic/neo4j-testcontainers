package com.example.demo

import org.springframework.data.annotation.Version
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import java.util.*

@Node("Person")
class PersonEntity(
    @Property("name")
    var name: String
) {

    @Id
    @Property("personId")
    var personId: String = UUID.randomUUID().toString()

    @Version
    @Property("dbVersion")
    var dbVersion: Long = 0

    @Relationship(type = "LIVES_AT", direction = Relationship.Direction.OUTGOING)
    var addresses: MutableList<AddressEntity> = mutableListOf()

    fun toModel() = PersonModel(
        personId, name, addresses.map { it.toModel() }
    )
}

@Node("Address")
class AddressEntity(
    @Property("number")
    var number: Int,

    @Property("street")
    var street: String
) {

    @Id
    @Property("addressId")
    var addressId: String = UUID.randomUUID().toString()

    @Version
    @Property("dbVersion")
    var dbVersion: Long = 0

    fun toModel() = AddressModel(
        addressId, number, street
    )

}