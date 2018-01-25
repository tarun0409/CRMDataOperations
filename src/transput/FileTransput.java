package transput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileTransput {
	
	public static String readFullFile(String inputFileName)
	{
		StringBuilder output = new StringBuilder();
		try
		{
			FileReader inputFileReader = new FileReader(inputFileName);
			BufferedReader bufferedReader = new BufferedReader(inputFileReader);
			String line = null;
			while((line = bufferedReader.readLine()) != null) 
			{
	           output.append(line);
	        }
			bufferedReader.close();
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
		}
		
		return output.toString();
	}

}
