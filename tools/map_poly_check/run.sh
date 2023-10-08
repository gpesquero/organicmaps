#!/bin/bash

echo "Running 'map_poly_check' script..."

# Check if <input path> argument exists...
if [ "$#" -lt 1 ]; then
  echo "Error: missing argument <source path>"
  exit 1
fi

# Check if input path exists...
if ! [ -d $1 ]; then
  echo "Input path <$1> does NOT exist!!"
  exit 1
fi

# Get list of poly files in input path...
polyCount=$(find $1 -maxdepth 1 -type f -name "*.poly" | wc -l)
echo "Found $polyCount poly files in <$1> directory"

# Convert poly files to geojson...
polyFiles="$1/*.poly"
okCount=0
failCount=0

for eachPolyFile in $polyFiles
do
  ./converters/poly2geojson "$eachPolyFile" --quiet
  if [ "$?" -eq 0 ]; then
    ((okCount++))
  else
    ((failCount++))
  fi
done

echo "Converted $((okCount+failCount)) poly file(s) to GeoJSON: $okCount Ok, $failCount failed"

# Run 'Map Poly Checker'...
./gradlew run --args=$1

