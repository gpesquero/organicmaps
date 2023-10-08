#include <iostream>
#include <vector>

using namespace std;

class ArgParser
{
public:
  ArgParser();
  bool parseArgs(int argc, char** argv);
  string getErrorMessage() {return mErrorMessage;};
  string getValue(int pos);
  int getValueCount() {return mValues.size();};
  bool isQuiet() {return mFlagQuiet;};

private:
  string mErrorMessage;
  bool mFlagQuiet;
  vector<string> mValues;
};
