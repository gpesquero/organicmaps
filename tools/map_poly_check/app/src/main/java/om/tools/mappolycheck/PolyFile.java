package om.tools.mappolycheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class PolyFile
{
  private static String GEOJSON_HEADER_STRING = 
    "{\n" +
    "  \"geometry\": {\n" +
    "    \"type\":\"MultiPolygon\",\n" +
    "    \"coordinates\":\n" +
    "      [[[\n";

  private static String GEOJSON_END_STRING = 
    "\n      ]]]\n" +
    "    },\n" +
    "  \"properties\":{},\n" +
    "  \"type\":\"Feature\"\n" +
    "}\n";

  public static boolean convertToGeoJson(String polyFileName, String geoJsonFileName)
  {
    //Log.debug("convertToGeoJson(): " + polyFileName + ", " + geoJsonFileName);

    BufferedReader reader;
    BufferedWriter writer;

    try
    {
      reader = new BufferedReader(new FileReader(polyFileName));
    }
    catch (FileNotFoundException e)
    {
      Log.error("Input poly file not found (" + e.getMessage() + ")");
      return false;
    }

    try
    {
      writer = new BufferedWriter(new FileWriter(geoJsonFileName));
    }
    catch (IOException e)
    {
      Log.error("Output geoJson file cannot be created (" + e.getMessage() + ")");

      try
      {
        reader.close();
      }
      catch (IOException e1)
      {
        return false;
      }
      return false;
    }

    int lineCount = 0;
    String inLine;

    try
    {
      while((inLine = reader.readLine()) != null)
      {
        //Log.data("inLine: " + inLine);

        if (lineCount == 0)
        {
          // First line is Poly file name. Write header of GeoJSON file.
          writer.write(GEOJSON_HEADER_STRING);
        }
        else if (lineCount == 1)
        {
          // Second line shall be "1"
          if (inLine.compareTo("1") != 0)
          {
            Log.warning("Second line of poly line is not '1'");
          }
        }
        else
        {
          // Other lines shall be coordinates or "END"

          if (inLine.compareTo("END") == 0)
          {
            // Reached end of file. Write end of GeoJSON file.
            writer.write(GEOJSON_END_STRING);

            // Do not continue to process the input file.
            break;
          }
          else
          {
            // This line should be a coordinate pair.
            // TAB + LON(double) + TAB + LAT(double)

            String[] fields = inLine.split("\t");

            //Log.info("Fields num: " + fields.length);

            for(int i = 0; i < fields.length; i++)
            {
              //Log.info("Field[" + i + "]: " + fields[i]);
            }

            if (fields.length != 3)
            {
              Log.error("Incorrect number of fields in coordinate string of " +
                        "input poly file in line " + lineCount);
              continue;
            }

            //Log.data("Coords: " + inLine);

            Double lon = Double.valueOf(fields[1]);
            Double lat = Double.valueOf(fields[2]);

            if (lineCount != 2)
            {
              // Add a comma "," at the end of the previous coordinate line
              writer.write(",\n");
            }

            String outLine = String.format(Locale.US,
                                           "        [%.06f,%.06f]", lon, lat);

            writer.write(outLine);

            //Log.data(outLine);
          }
        }

        lineCount++;
      }

      reader.close();
      writer.close();
    }
    catch (IOException e)
    {
      Log.error("IOException: " + e.getMessage());
      return false;
    }

    return true;
  }
}
