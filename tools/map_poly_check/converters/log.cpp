#include "log.hpp"

void Log::print(LogLevel level, string message)
{
  bool printLog;

  switch (level)
  {
  case LOG_ERROR:
    printLog = true;
    break;
  
  default:
    if (mQuiet)
      printLog = false;
    else
      printLog = true;
    break;
  }

  if (printLog)
    cout << message;
}
