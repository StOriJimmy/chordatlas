#!/bin/bash

for r in jutils campskeleton siteplan chordatlas;
   do 
       cd /tmp/$r; \
       git pull; \
       mvn install; \
done

cd /tmp/chordatlas
mvn package

mkdir -p /output
cp /tmp/chordatlas/target/*.jar /output
