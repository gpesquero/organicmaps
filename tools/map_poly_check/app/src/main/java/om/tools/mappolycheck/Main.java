package om.tools.mappolycheck;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang3.time.StopWatch;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.Version;

public class Main
{
  private static String mAppVersion = "0.01";
  private static String mAppDate = "Oct 12th 2023";

  private static StopWatch mStopWatch = new StopWatch();

  public static void main(String[] args)
  {
    mStopWatch.start();

    Log.info("Starting CheckBorders (v" + mAppVersion + ", " + mAppDate +
             ")...");

    Version v = GeoTools.getVersion();

    Log.info("GeoTools version: " + v.toString());

    String path = checkArguments(args);

    if (path == null)
    {
      finish(-1);
      return;
    }

    Log.info("Input path: " + path);

    PolyProcessor processor = new PolyProcessor();

    if (!processor.setPath(path))
      finish(-1);

    ArrayList<File> inputFiles = processor.getInputFilesList();

    if (inputFiles == null)
    {
      finish(-1);
      return;
    }

    int numInputFiles = inputFiles.size();

    if (numInputFiles == 0)
    {
      Log.info("No Poly files were found");
      finish(-1);
    }

    Log.info("Number of input Poly files found: " + numInputFiles);

    if (!processor.parsePolyFiles(inputFiles))
      finish(-1);

    finish(0);
  }

  private static void finish(int exitCode) {

    mStopWatch.stop();

    Log.info("CheckBorders finished !! (exitCode: " + exitCode + ")");
    Log.info("Total elapsed time: " + mStopWatch.formatTime());

    System.exit(exitCode);
  }

  private static String checkArguments(String[] args)
  {
    if (args.length < 1)
    {
      Log.error("Not enough number of input arguments: <path>");
      return null;
    }

    return args[0];
  }
}
