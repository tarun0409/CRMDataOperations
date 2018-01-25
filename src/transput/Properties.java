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
		String properties = FileTransput.readFullFile("src/data/config.properties");
		JSONObject pJson = null;
		try
		{
			pJson = new JSONObject(properties);
		}
		catch(JSONException je)
		{
			je.printStackTrace();
		}
		return pJson;
		
	}

}
