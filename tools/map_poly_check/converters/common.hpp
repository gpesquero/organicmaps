#include <iostream>

using namespace std;

#define POLY_EXTENSION   ".poly"
#define GEOJON_EXTENSION ".geojson"

#define GEOJSON_HEADER_STRING_LINE1 "{\n"
#define GEOJSON_HEADER_STRING_LINE2 "  \"geometry\": {\n"
#define GEOJSON_HEADER_STRING_LINE3 "    \"type\":\"MultiPolygon\",\n"
#define GEOJSON_HEADER_STRING_LINE4 "    \"coordinates\":\n"
#define GEOJSON_HEADER_STRING_LINE5 "      [[[\n"

#define GEOJSON_END_STRING_LINE1    "\n      ]]]\n"
#define GEOJSON_END_STRING_LINE2    "    },\n"
#define GEOJSON_END_STRING_LINE3    "  \"properties\":{},\n"
#define GEOJSON_END_STRING_LINE4    "  \"type\":\"Feature\"\n"
#define GEOJSON_END_STRING_LINE5    "}\n"

/*
struct Params
{
  string appName;
  string inputFileName;
  string outputFileName;

};
*/

/*
bool parseArguments(int argc, char** argv,
                    string& inputFileName, string& outputFileName);
*/
//bool parseArguments(int argc, char** argv, Params& params);

string getOutputFileName(string inputFileName, string inExt, string outExt);

bool openFiles(string inputFileName, string outputFileName,
               ifstream& inputFile, ofstream& outputFile, string& errorMessage);

void writeGeoJsonHeaderLines(ofstream& outputFile);
void writeGeoJsonEndLines(ofstream& outputFile);
