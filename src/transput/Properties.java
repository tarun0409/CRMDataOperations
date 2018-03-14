package transput;

import org.json.JSONException;
import org.json.JSONObject;

public class Properties {
	
	public static JSONObject properties;
	public static JSONObject getProperties()
	{
		if(properties!=null)
		{
			return properties;
		}
		String propertiesStr = FileTransput.readFullFile("src/data/config.properties");
		JSONObject pJson = null;
		try
		{
			pJson = new JSONObject(propertiesStr);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
		properties = pJson;
		return pJson;
		
	}

}
