MATCH (n) DETACH DELETE n;

LOAD CSV WITH HEADERS FROM 'file:///address.csv' as row
CREATE (n: Address { addressId: row.id, number: toInteger(row.number), street: row.street, dbVersion: toInteger(1) });

LOAD CSV WITH HEADERS FROM 'file:///person.csv' as row
CREATE (n: Person { personId: row.id, name: row.name, dbVersion: toInteger(1) });

LOAD CSV WITH HEADERS FROM 'file:///LIVES_AT.csv' as row
MATCH (p: Person { personId: row.personId })
MATCH (a: Address { addressId: row.addressId })
MERGE (p)-[:LIVES_AT]->(a);