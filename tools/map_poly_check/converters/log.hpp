#include <cstring>
#include <iostream>

using namespace std;

enum LogLevel
{
  LOG_INFO,
  LOG_ERROR

};

class Log
{
public:
  static void print(LogLevel level, string message);
  static void setQuiet(bool quiet) {mQuiet = quiet;};

private:
  Log();
  static inline bool mQuiet = false;
};