#!/bin/bash

echo "Environment:"
env
echo "NEO4J bin:"
ls -l "$NEO4J_HOME"/bin
echo "NEO4J import:"
ls -l "$NEO4J_HOME"/import
cypher-shell -u neo4j -p password -f "$NEO4J_HOME"/bin/data-loader.cypher
echo "Test data import done"