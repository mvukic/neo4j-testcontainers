LOAD CSV WITH HEADERS FROM 'file:///address.csv' as row
CREATE (n: Address { personId: row.id, number: row.number, street: row.street });

LOAD CSV WITH HEADERS FROM 'file:///person.csv' as row
CREATE (n: Person { addressId: row.id, name: row.name });

LOAD CSV WITH HEADERS FROM 'file:///person.csv' as row
MATCH (p: Person { personId: row.id })
MATCH (a: Address { addressId: row.address })
CREATE (p)-[:LIVES_AT]->(a);