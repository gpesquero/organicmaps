#include <cstring>
#include <fstream>
#include <iostream>

#include "arg_parser.hpp"

using namespace std;

ArgParser::ArgParser()
{
  mFlagQuiet = false;
}

bool ArgParser::parseArgs(int argc, char** argv)
{
  if (argc < 2)
  {
    mErrorMessage = "Not enough arguments";
    return false;
  }

  for(int i=1; i<argc; i++)
  {
    string arg(argv[i]);

    if (arg.rfind("--") == 0)
    {
      // Argument is an option
      if (arg.compare("--quiet") == 0)
      {
        mFlagQuiet = true;
      }
      else
      {
        mErrorMessage = "Unknown option '" + arg + "'";
        return false;
      }
    }
    else
    {
      mValues.push_back(arg);
    }
  }

  if (mValues.empty() || (mValues.size() > 2))
  {
    mErrorMessage = "Incorrect number of arguments";
    return false;
  }

  return true;
}

string ArgParser::getValue(int pos)
{
  if (mValues.size() <= pos)
    return "";
  else
    return mValues.at(pos);
}
