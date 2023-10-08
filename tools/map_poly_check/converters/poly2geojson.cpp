/////////////////////////////////////////////////////////////////////
// poly2geojson.cpp

#include <cstring>
#include <fstream>
#include <iostream>

#include "arg_parser.hpp"
#include "common.hpp"
#include "log.hpp"

using namespace std;

bool convertPolyFile(ifstream& inputFile, ofstream& outputFile,
                     string& inputFileName, string& errorMessage);

void showUsageMessage()
{
  cout << "Usage: 'poly2geojson [options] polyInputFile [geoJsonOutputFile]'" << "\n"
       << "Options:" << "\n"
       << "  --quiet: do not print any output message" << "\n";
}

// Main with arguments.
int main(int argc, char** argv)
{
  ArgParser parser = ArgParser();

  if (!parser.parseArgs(argc, argv))
  {
    Log::print(LOG_ERROR, string("Error parsing args: ") +
               parser.getErrorMessage() + "\n");
    showUsageMessage();
    return -1;
  }

  string inputFileName = parser.getValue(0);

  string outputFileName;
  
  if (parser.getValueCount() == 2)
    outputFileName = parser.getValue(1);
  else
    outputFileName = getOutputFileName(inputFileName,
                                       string(POLY_EXTENSION),
                                       string(GEOJON_EXTENSION));

  Log::setQuiet(parser.isQuiet());

  string logMessage(string("Converting '") + inputFileName + "' to '" +
            outputFileName + "'... ");

  ifstream inputFile;
  ofstream outputFile;

  string errorMessage;

  if (!openFiles(inputFileName, outputFileName, inputFile, outputFile,
                 errorMessage))
  {
    Log::print(LOG_ERROR, logMessage + errorMessage + "\n");
    return -1;
  }

  if (!convertPolyFile(inputFile, outputFile, inputFileName, errorMessage))
  {
    Log::print(LOG_ERROR, logMessage + errorMessage + "\n");
    return -1;
  }

  inputFile.close();
  outputFile.close();

  Log::print(LOG_INFO, logMessage + "Ok!!\n");
}

bool convertPolyFile(ifstream& inputFile, ofstream& outputFile,
                     string& inputFileName, string& errorMessage)
{
  string inputLine;
  int lineCount = 0;
  int endCount = 0;

  while (getline(inputFile, inputLine))
  {
    lineCount++;

    if (lineCount == 1)
    {
      // This is the first line, which is the name; Ignore it...
      writeGeoJsonHeaderLines(outputFile);
    }
    else if (lineCount == 2)
    {
      // Second line. Should be the string "1"...
      if (inputLine.compare("1") != 0)
      {
        errorMessage = string("Second line of input poly file '") +
                   inputFileName + "' is not '1'\n";
        return false;
      }
    }
    else
    {
      // Check if we've reached end of line...
      if (inputLine.compare("END") == 0)
      {
        endCount++;
      }
      else
      {
        // This should be a coordinate line, with the following format:
        // TAB + LONGITUDE + TAB + LATITUDE

        int firstTabPos = inputLine.find('\t');
        
        if (firstTabPos < 0)
        {
          // First tab not found
          errorMessage = string("Missing first tab in line ") +
                     std::to_string(lineCount) + "of input poly file '" +
                     inputFileName + "'\n";
          return false;
        }
        else if (firstTabPos != 0)
        {
          // First tab not found in first position
          errorMessage = string("First tab of line ") +
                                std::to_string(lineCount) +
                                "of input poly file '" + inputFileName +
                                "' not found in first position\n";
          return false;
        }

        int secondTabPos = inputLine.find('\t', 1);

        if (secondTabPos < 0)
        {
          // Second tab not found
          errorMessage = string("Missing second tab in line ") +
                                std::to_string(lineCount) +
                                "of input poly file '" + inputFileName + "'\n";
          return false;
        }

        string lonString = inputLine.substr(1, secondTabPos - 1);
        double lonValue = stod(lonString);

        string latString = inputLine.substr(secondTabPos+1);
        double latValue = stod(latString);

        if (lineCount != 3)
        {
          // We're not in the first coord line.
          // Add a comma ',' at the end of the previous coord line.
          char commaString[] = ",\n";
          outputFile.write(commaString, strlen(commaString));
        }

        char buf[100];
        snprintf(buf, 100, "        [%.6f,%.6f]", lonValue, latValue);
        outputFile.write(buf, strlen(buf));
      }
    }
  }

  if (endCount != 2)
  {
    // Missing END lines
    errorMessage = string("Missing 2 'END' lines in file '") +
                          inputFileName + "'\n";
    return false;
  }
  else
  {
    writeGeoJsonEndLines(outputFile);
  }

  return true;
}
