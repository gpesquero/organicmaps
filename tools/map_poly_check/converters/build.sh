#!/bin/bash

g++ arg_parser.cpp common.cpp log.cpp poly2geojson.cpp -o poly2geojson

result=$?

echo -n "Building 'poly2geojson'... "

if [[ $result -eq 0 ]]
then
  echo "Ok!!"
else
  echo "failed!!"
fi
