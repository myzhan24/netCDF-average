package base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.VariableDS;
import ucar.nc2.dt.grid.GridDataset;
import ucar.ma2.*;
import ucar.ma2.ArrayFloat.D1;

@SuppressWarnings("deprecation")
public class average
{
	final static int latitude = 46; //Latitude
    final static int longitude = 72; //Longitude 
    final static int time = 12; //Time
    
    static ArrayFloat.D3 sumOfData;
    static ArrayFloat.D1 lonData;
    static ArrayFloat.D1 latData;
    static ArrayList<String> fileList;
    static int total;
    
	public static void addGeo2D(String fileName)
	{
		NetcdfDataset ncd = null;
		NetcdfFile dataFile = null;
		 try 
		 {
			// fileName= "test.nc";
		   ncd = NetcdfDataset.openDataset(fileName);
		   dataFile = NetcdfFile.open(fileName, null);
		   Variable dataVar = dataFile.findVariable("tgrnd");
		   GridDataset gds = new GridDataset(ncd);
		   VariableDS myvds = new VariableDS(ncd.getRootGroup(), dataVar, false);
		   
		//   CoordinateAxis mycoor = new CoordinateAxis(ncd, null, "latitude", DataType.INT, "dimensions", "degrees", "the latitude");
		 //  System.out.println(mycoor.getDescription());
		   
		// Create netCDF dimensions


       	Dimension lon = new Dimension("longitude", longitude );
       	Dimension lat = new Dimension("latitude", latitude );
       	Dimension tim = new Dimension("time",1);
	
       	// define variable dimensions
       	ArrayList<Dimension> dims =  new ArrayList<Dimension>();
       	dims.add(tim);
       	dims.add(lat);
       	dims.add(lon);
		   
		   System.out.println(gds.getDetailInfo());
		   
		 } 
		 catch(Exception e)
		 {
			 
		 }
		 finally 
		 {
			if(null!=ncd)
			try{
				ncd.close();
			}
			catch(Exception e){
				
			}
		 }
	}
    public static void readNetCDF(String fileName, String var)
    {
    	NetcdfFile dataFile = null;
		try
		{
			dataFile = NetcdfFile.open(fileName, null);
			System.out.println(dataFile.toString());
			// Retrieve the variable
			Variable dataVar = dataFile.findVariable(var);
			if (dataVar == null)
			{
				System.out.println("Cant find Variable data");
		        return;
		    }
		
			// Read dimension information on the variable
			int [] shape = dataVar.getShape();
			int[] origin = new int[3];
		
			ArrayFloat.D3 dataArray;
			dataArray = (ArrayFloat.D3) dataVar.read(origin, shape);
			
			//float[][][] dataArray = new float[shape[0]][shape[1]][shape[2]];
		
			
			for(int i = 0; i<shape[0];i++)	//time
			{
				for(int j =0; j < shape[1]; j++)
				{
					System.out.print("[");
					for(int k = 0; k < shape[2];k++)
					{
						System.out.print(""+dataArray.get(i,j,k)+", \t");
					}
					System.out.println("]");
				}
				System.out.println("");
			}
			
		} 
		catch (java.io.IOException e) 
		{
		        e.printStackTrace();
		        return;
		}  
		catch (IndexOutOfBoundsException e) 
		{
		        e.printStackTrace();
		} catch (InvalidRangeException e) {
			
			e.printStackTrace();
		} 
		finally 
		{
		   if (dataFile != null)
		   try 
		   {
		     dataFile.close();
		   } 
		   catch (IOException ioe) 
		   {
		     ioe.printStackTrace();
		   }
		}
    }
	public static void parseNetCDFCoordinates(String fileName)
	{
		NetcdfFile dataFile = null;
		try
		{
			dataFile = NetcdfFile.open(fileName, null);
			// Retrieve the variable
			Variable lonVar = dataFile.findVariable("longitude");
			Variable latVar = dataFile.findVariable("latitude");
			
			if (lonVar == null)
			{
				System.out.println("Cant find longitude data");
		        return;
		    }
			if (latVar == null)
			{
				System.out.println("Cant find latitude data");
		        return;
		    }
		
			// Read dimension information on the variable
			int[] origin = new int[1];
		
			latData = (ArrayFloat.D1) latVar.read(origin, latVar.getShape());	
			lonData = (ArrayFloat.D1) lonVar.read(origin, lonVar.getShape());
			
		} 
		catch (java.io.IOException e) 
		{
		        e.printStackTrace();
		        return;
		}  
		catch (IndexOutOfBoundsException e) 
		{
		        e.printStackTrace();
		} catch (InvalidRangeException e) {
			e.printStackTrace();
		} 
		finally 
		{
		   if (dataFile != null)
		   try 
		   {
		     dataFile.close();
		   } 
		   catch (IOException ioe) 
		   {
		     ioe.printStackTrace();
		   }
		}
	}
    public static void parseNetCDFVar(String fileName, String var)
	{
		NetcdfFile dataFile = null;
		try
		{
			dataFile = NetcdfFile.open(fileName, null);
			//System.out.println(dataFile.toString());
			// Retrieve the variable
			Variable dataVar = dataFile.findVariable(var);
			if (dataVar == null)
			{
				System.out.println("Cant find Variable data");
		        return;
		    }
		
			// Read dimension information on the variable
			int [] shape = dataVar.getShape();
			int[] origin = new int[3];
		
			ArrayFloat.D3 dataArray;
			dataArray = (ArrayFloat.D3) dataVar.read(origin, shape);
			
			
			//float[][][] dataArray = new float[shape[0]][shape[1]][shape[2]];
			
		
			total+=time;
			//System.out.println(shape[0]+" "+shape[1]+" "+shape[2]);
			for(int i = 0; i< shape[0];i++)	//time
			{
				for(int j =0; j < shape[1]; j++) //latitude
				{
					for(int k = 0; k < shape[2];k++) //longitude
					{
						sumOfData.set(0,j,k, sumOfData.get(0,j,k) + dataArray.get(i,j,k));
					}
				}
			}
		} 
		catch (java.io.IOException e) 
		{
		        e.printStackTrace();
		        return;
		}  
		catch (IndexOutOfBoundsException e) 
		{
		        e.printStackTrace();
		} catch (InvalidRangeException e) {
			e.printStackTrace();
		} 
		finally 
		{
		   if (dataFile != null)
		   try 
		   {
		     dataFile.close();
		   } 
		   catch (IOException ioe) 
		   {
		     ioe.printStackTrace();
		   }
		}
	}
	public static void writeNetCDF(String fileName, String var)
	{
		// Create the file.
        NetcdfFileWriteable dataFile = null;
        NetcdfFile existingDataFile = null;
        
        try {
        	dataFile = NetcdfFileWriteable.createNew(fileName, false);

        	// Create netCDF dimensions
        	// as well as the coordinate variables
        	Dimension lon = dataFile.addDimension("longitude", longitude );
        	Dimension lat = dataFile.addDimension("latitude", latitude );
        	Dimension tim = dataFile.addDimension("time",1);
        	
        	
        	dataFile.addVariable("latitude",DataType.FLOAT,new Dimension[] {lat});
        	dataFile.addVariable("longitude",DataType.FLOAT,new Dimension[] {lon});
        	//coordinate attributes
        	dataFile.addVariableAttribute("longitude", "units", "degrees_east");
            dataFile.addVariableAttribute("latitude", "units", "degrees_north");
            
        	//Dimension lon = new Dimension("longitude", longitude );
        	//Dimension lat = new Dimension("latitude", latitude );
        	//Dimension tim = new Dimension("time",1);
	
        	// define variable dimensions
        	ArrayList<Dimension> dims =  new ArrayList<Dimension>();
        	dims.add(tim);
        	dims.add(lat);
        	dims.add(lon);
        	
        	existingDataFile = NetcdfFile.open(fileList.get(0), null);
        	/*
        	for(Dimension d : existingDataFile.getDimensions())
        	{
        		if(d.getShortName().equals("time"))
        			dataFile.addDimension(d.getShortName(), 1 );	
        			
        		else
        			dataFile.addDimension(d.getShortName(), d.getLength() );		
        	}
             
*/
            // add the variable into the new netcdf
        	dataFile.addVariable(var, DataType.FLOAT, dims);
        	
        	//adds the variable attributes that are present in the given netCDF
        	Variable dataVar = existingDataFile.findVariable(var);
        	for(Attribute s:dataVar.getAttributes())
        	{
        		if(s.getDataType() == DataType.STRING)
        			dataFile.addVariableAttribute(var, s.getShortName(), s.getStringValue());
        		
        		else
        			dataFile.addVariableAttribute(var, s.getShortName(),s.getNumericValue());
        	}
        	
        	

        	// This is the data array we will write. It will just be filled
        	// with a progression of numbers for this example.
           // ArrayFloat.D3 dataOut = new ArrayFloat.D3(  tim.getLength(),lat.getLength(), lon.getLength());

            // Create some pretend data. If this wasn't an example program, we
            // would have some real data to write, for example, model output.
            /*int i,j;
           
            for (i=0; i<xDim.getLength(); i++) {
                 for (j=0; j<yDim.getLength(); j++) {
                     dataOut.set(i,j, i * NY + j);
                 }
            }*/
           // System.out.println(tim.getLength()+" "+lat.getLength()+" "+lon.getLength());
            /*for(int i=0; i < tim.getLength();i++)
            {
            	for(int j= 0 ; j < lat.getLength();j++)
            	{
            		for(int k = 0; k < lon.getLength();k++)
            		{
            			//System.out.println(i+" "+j+" "+k);
            			dataOut.set(i,j,k, sumOfData[j][k]);
            			//System.out.println(sumOfData[j][k]);
            		}
            	}
            }*/
            
            for(Dimension d : existingDataFile.getDimensions())
        	{
            	try
            	{
        			dataFile.addDimension(d.getShortName(), d.getLength() );	
            	}
            	catch(Exception e)
            	{
            		//System.out.println("duplicate dim: "+d.getShortName());
            	}
        	}
            
            // create the file
            dataFile.create();

            
            dataFile.write("latitude",latData);
            dataFile.write("longitude",lonData);
            
            // Write the pretend data to the file. Although netCDF supports
            // reading and writing subsets of data, in this case we write all
            // the data in one operation.
            
            //creating the data for latitude and longitude
            int[] origin = new int[3];
            dataFile.write(var,origin, sumOfData);
            
            System.out.println("NetCDF file created! \"test.nc\" contains the averages of variable: "+var+".");

            
            
            
            // The file is closed no matter what by putting inside a try/catch block.
            
        	}
        	
        	catch (IOException e) 
        	{
		       e.printStackTrace();
        	} 
        	catch (InvalidRangeException e) 
        	{
		       e.printStackTrace();
        	} 
        	finally 
        	{
        		if (null != dataFile)
        			try 
        			{
    					dataFile.close();
        			} 
        			catch (IOException ioe) 
        			{
        				ioe.printStackTrace();
        			}
        		if (null != existingDataFile)
        			try 
        			{
        				existingDataFile.close();
        			} 
        			catch (IOException ioe) 
        			{
        				ioe.printStackTrace();
        			}
        	}
	}
	
	public static void main(String args[])
    {
		fileList = new ArrayList<String>();
		
		String var = null;
		try
		{
			Scanner s = new Scanner(new File(args[0]));
			var=s.nextLine();
			while(s.hasNextLine())
			{
				fileList.add(s.nextLine());
			}
			s.close();
			
			if(fileList.size()==0)
				System.out.println("filelist.txt is empty");
		}
		catch(Exception e)
		{
			System.out.println("Could not read filelist.txt");
		}
		
		sumOfData = new ArrayFloat.D3(1, latitude, longitude);
		total = 0;
		for(String s : fileList)
		{
			parseNetCDFVar(s, var);
		}
		parseNetCDFCoordinates(fileList.get(0));
		//calculate the average of the variable across the total time period
		for(int i = 0; i < latitude;i++)
		{
			for(int j = 0; j < longitude; j++)
			{
				sumOfData.set(0,i,j, sumOfData.get(0,i,j)/((float) total));
				//System.out.print(sumOfData[i][j]+", \t");
			}
			//System.out.println();
		}
		
		
		writeNetCDF("test.nc",var);
		//addGeo2D(fileList.get(0));
		//readNetCDF("test.nc",var);
		
    }
}
