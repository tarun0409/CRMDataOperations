package records;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiUtil {
	
	public static ArrayList<String> getIdsFromJSON(JSONObject obj) throws Exception
	{
		ArrayList<String> ids = new ArrayList<String>();
		if(obj!=null && obj.has("data"))
		{
			JSONArray dataArr = obj.getJSONArray("data");
			for(int i=0; i<dataArr.length(); i++)
			{
				JSONObject dataObj = dataArr.getJSONObject(i);
				if(dataObj.has("id"))
				{
					String id = dataObj.getString("id");
					ids.add(id);
				}
				else if(dataObj.has("code") && dataObj.getString("code").equals("SUCCESS") && dataObj.has("details"))
				{
					JSONObject details = dataObj.getJSONObject("details");
					if(details.has("id"))
					{
						String id = details.getString("id");
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}
	
	public static ArrayList<String> getAllModulesApiNames(JSONObject obj, String moduleCriteriaCheck) throws Exception
	{
		ArrayList<String> apiNames = new ArrayList<String>();
		if(obj!=null && obj.has("modules"))
		{
			JSONArray modArr = obj.getJSONArray("modules");
			for(int i=0; i<modArr.length(); i++)
			{
				JSONObject module = modArr.getJSONObject(i);
				if(moduleCriteriaCheck!=null && module.has(moduleCriteriaCheck) && !module.getBoolean(moduleCriteriaCheck))
				{
					continue;
				}
				if(module.has("api_name"))
				{
					String apiName = module.getString("api_name");
					apiNames.add(apiName);
				}
			}
		}
		return apiNames;
	}

}
