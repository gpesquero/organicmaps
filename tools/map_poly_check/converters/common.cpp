#include <cstring>
#include <fstream>
#include <iostream>

#include "common.hpp"
#include "log.hpp"

using namespace std;

string getOutputFileName(string inputFileName, string inExt, string outExt)
{
  // Output file name is based on input file name
  string outputFileName;

  int pos = inputFileName.find(inExt);

  if (pos == (inputFileName.length() - inExt.length()))
  {
    // Input file name ends with "inExt"
    // Replace "inExt" with "outExt"
    outputFileName = inputFileName.substr(0, pos) + outExt;
  }
  else
  {
    // Input file name does not end with "inExt"
    // Simply add "outExt" at the end of the input file name
    outputFileName = inputFileName + outExt;
  }

  return outputFileName;
}

bool openFiles(string inputFileName, string outputFileName,
               ifstream& inputFile, ofstream& outputFile, string& errorMessage)
{
  inputFile.open(inputFileName);

  if ((inputFile.rdstate() & std::ifstream::failbit) != 0)
  {
    errorMessage = string("Error opening input file '") + inputFileName;
    return false;
  }

  outputFile.open(outputFileName);

  if ((outputFile.rdstate() & std::ofstream::failbit) != 0)
  {
    errorMessage = string("Error creating output file '") + inputFileName;
    return false;
  }

  return true;
}

void writeGeoJsonHeaderLines(ofstream& outputFile)
{
  outputFile.write(GEOJSON_HEADER_STRING_LINE1, strlen(GEOJSON_HEADER_STRING_LINE1));
  outputFile.write(GEOJSON_HEADER_STRING_LINE2, strlen(GEOJSON_HEADER_STRING_LINE2));
  outputFile.write(GEOJSON_HEADER_STRING_LINE3, strlen(GEOJSON_HEADER_STRING_LINE3));
  outputFile.write(GEOJSON_HEADER_STRING_LINE4, strlen(GEOJSON_HEADER_STRING_LINE4));
  outputFile.write(GEOJSON_HEADER_STRING_LINE5, strlen(GEOJSON_HEADER_STRING_LINE5));
}

void writeGeoJsonEndLines(ofstream& outputFile)
{
  outputFile.write(GEOJSON_END_STRING_LINE1, strlen(GEOJSON_END_STRING_LINE1));
  outputFile.write(GEOJSON_END_STRING_LINE2, strlen(GEOJSON_END_STRING_LINE2));
  outputFile.write(GEOJSON_END_STRING_LINE3, strlen(GEOJSON_END_STRING_LINE3));
  outputFile.write(GEOJSON_END_STRING_LINE4, strlen(GEOJSON_END_STRING_LINE4));
  outputFile.write(GEOJSON_END_STRING_LINE5, strlen(GEOJSON_END_STRING_LINE5));
}
