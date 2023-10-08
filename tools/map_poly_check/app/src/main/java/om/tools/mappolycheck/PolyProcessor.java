package om.tools.mappolycheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class PolyProcessor {

  File mPath = null;

  Geometry mTotalGeometry = null;

  int mIntersectionsIndex = 0;
  int mInteriorRingsIndex = 0;

  GeometryFactory mGeoFactory = new GeometryFactory();

  public boolean setPath(String path)
  {
    mPath = new File(path);

    if (!mPath.exists())
    {
      Log.warning("Input path <" + path + "> does not exist. Quitting !!");
      return false;
    }

    if (!mPath.isDirectory())
    {
      Log.error("Input path <" + path + "> is not a directory. Quitting !!");
      return false;
    }

    File outPath = new File(path + "/out");

    if (outPath.exists())
    {
      // Delete all existing geojson files in "out" directory
      for(File file: outPath.listFiles()) 
        if (!file.isDirectory()) 
          file.delete();
    }
    else
    {
      // Create "out" directory inside of path
      if (!outPath.mkdirs())
      {
        Log.error("Error while creating out path <" + outPath.getAbsolutePath()
                  + ">");
        return false;
      }
    }

    return true;  
  }

  public ArrayList<File> getInputFilesList()
  {
    if (mPath == null)
    {
      Log.error("Invalid current path");
      return null;
    }

    File[] files = mPath.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name)
      {
        return name.toLowerCase().endsWith(".poly");
      }
    });

    return new ArrayList<File>(Arrays.asList(files));
  }

  public boolean parsePolyFiles(ArrayList<File> inputPolyFiles)
  {
    int numPolyFiles = inputPolyFiles.size();

    for(int fileIndex=0; fileIndex<numPolyFiles; fileIndex++)
    {
      File polyFile = inputPolyFiles.get(fileIndex);

      Log.info("File " + (fileIndex+1) + "/" + numPolyFiles + ": " + polyFile.getName());

      String geoJsonFileName = polyFile.getAbsolutePath().replace(".poly", ".geojson");

      PolyFile.convertToGeoJson(polyFile.getAbsolutePath(), geoJsonFileName);

      InputStream stream;

      try
      {
        stream = new FileInputStream(geoJsonFileName);
      }
      catch (FileNotFoundException e)
      {
        Log.error("GeoJSON input stream error: " + e.getMessage());
        return false;
      }

      GeometryJSON jsonGeometry = new GeometryJSON();

      Geometry newGeometry;

      try
      {
        newGeometry = jsonGeometry.read(stream);
        
      }
      catch (IOException e)
      {
        Log.error("GeoJSON read stream error: " + e.getMessage());
        return false;
      }

      if (!newGeometry.isValid())
      {
        Log.warning("New geometry is not valid");
        continue;
      }

      if (newGeometry.isEmpty())
      {
        Log.warning("New geometry is empty");
        continue;
      }

      if (mTotalGeometry == null)
      {
        mTotalGeometry = newGeometry;
        continue;
      }

      // Check for intersections...

      Geometry intersection = mTotalGeometry.intersection(newGeometry);

      if (!intersection.isValid())
      {
        Log.warning("Intersection geometry is not valid!!");
      }
      else if (!intersection.isEmpty())
      {
        if (intersection.getGeometryType().compareTo(Geometry.TYPENAME_MULTILINESTRING) == 0)
        {
          // These types of intersections are usual for touching geometries
          // Do nothing
        }
        else if (intersection.getGeometryType().compareTo(Geometry.TYPENAME_POLYGON) == 0)
        {
          Log.data("Detected intersection polygon");

          Polygon pol = (Polygon)intersection;

          addIntersectionPolygon(pol);
        }
        else if (intersection.getGeometryType().compareTo(Geometry.TYPENAME_GEOMETRYCOLLECTION) == 0)
        {
          GeometryCollection collection = (GeometryCollection)intersection;
          
          int numGeo = collection.getNumGeometries();

          //Log.debug("Intersection collection num geometries: " + numGeo);

          for(int i = 0; i < numGeo; i++)
          {
            Geometry geo = collection.getGeometryN(i);

            //Log.debug("Collection Geometry #" + i + " type: " + geo.getGeometryType());

            if (geo.getGeometryType().compareTo("Polygon") == 0)
            {
              Log.data("Detected intersection polygon");

              Polygon pol = (Polygon)geo;

              addIntersectionPolygon(pol);
            }
            else
            {
            }
          }
        }
        else
        {
          Log.warning("Unknown intersection geometry type <" + intersection.getGeometryType() + ">");
        }
      }

      // Create union of geometries...

      Geometry union = mTotalGeometry.union(newGeometry);

      if (!union.isValid())
      {
        Log.warning("Union geometry is not valid!!");
      }
      else if (union.isEmpty())
      {
        Log.warning("Union geometry is empty!!");
      }
      else if (union.getGeometryType().compareTo(Geometry.TYPENAME_POLYGON) == 0)
      {
        Polygon polUnion = (Polygon)union;

        checkForInteriorRings(polUnion);

        mTotalGeometry = removeInteriorRings(polUnion);
      }
      else if (union.getGeometryType().compareTo(Geometry.TYPENAME_MULTIPOLYGON) == 0)
      {
        MultiPolygon multiPolUnion = (MultiPolygon)union;

        int numGeo = multiPolUnion.getNumGeometries();

        Polygon[] newPolygons = new Polygon[numGeo];

        for(int i = 0; i < numGeo; i++)
        {
          Geometry geo = multiPolUnion.getGeometryN(i);

          if (!geo.isValid())
          {
            Log.warning("MultiPolUnio geometry #" + i + " is not valid");
            continue;
          }

          if (geo.isEmpty())
          {
            Log.warning("MultiPolUnio geometry #" + i + " is empty");
            continue;
          }

          if (geo.getGeometryType().compareTo(Geometry.TYPENAME_POLYGON) == 0)
          {
            Polygon pol = (Polygon)geo;

            checkForInteriorRings(pol);

            newPolygons[i] = removeInteriorRings(pol);            
          }
          else
          {
            Log.warning("Unknown multipolygon union geometry type <" + geo.getGeometryType() + ">");
          }
        }

        mTotalGeometry = mGeoFactory.createMultiPolygon(newPolygons);
      }
      else
      {
        Log.warning("Unknown union geometry type: " + union.getGeometryType());
      }
    }

    Log.info("File parsing finished !!");

    writeTotalGeometry();
    
    Log.info("Detected " + mIntersectionsIndex + " intersections");
    Log.info("Detected " + mInteriorRingsIndex + " interior rings");

    return true;
  }

  void checkForInteriorRings(Polygon polygon)
  {
    int numInteriorRings = polygon.getNumInteriorRing();

    if (numInteriorRings > 0)
    {
      Log.data("Detected <" + numInteriorRings + "> interior ring(s)");
    }

    for(int i=0; i<numInteriorRings; i++)
    {
      LinearRing interiorRing = polygon.getInteriorRingN(i);

      addInteriorRing(interiorRing);
    }
  }

  Polygon removeInteriorRings(Polygon polygon)
  {
    Polygon newPolygon = mGeoFactory.createPolygon(polygon.getExteriorRing());

    return newPolygon;
  }

  void addIntersectionPolygon(Polygon polygon)
  {
    String fileName = String.format("intersection_%05d", mIntersectionsIndex);

    writePolygon(fileName, polygon);

    mIntersectionsIndex++;
  }

  void addInteriorRing(LinearRing ring)
  {
    String fileName = String.format("interiorRing_%05d", mInteriorRingsIndex);

    Polygon polygon = new Polygon(ring, null, mGeoFactory);

    writePolygon(fileName, polygon);

    mInteriorRingsIndex++;
  }

  boolean writePolygon(String fileName, Polygon polygon)
  {
    GeometryJSON jsonGeo= new GeometryJSON();

    try
    {
      jsonGeo.write(polygon, mPath + "/out/" + fileName + ".geojson");
      Log.info("Written polygon <" + fileName + ">");
    }
    catch (IOException e)
    {
      Log.error("GeoJSON union write error: " + e.getMessage());
      return false;
    }

    return true;
  }

  boolean writeTotalGeometry()
  {
    GeometryJSON jsonGeo= new GeometryJSON();

    String totalGeoFileName = "/out/totalGeo.geojson";

    try
    {
      jsonGeo.write(mTotalGeometry, mPath + totalGeoFileName);

      Log.info("Written total geometry <" + totalGeoFileName + ">");
    }
    catch (IOException e)
    {
      Log.error("GeoJSON total geometry write error: " + e.getMessage());
      return false;
    }

    return true;
  }
}
