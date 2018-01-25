package restapi;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestApiResponse {
	
	private int statusCode;
	private JSONObject response;
	private String httpMethod;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public JSONObject getResponse() {
		return response;
	}
	public void setResponse(JSONObject response) {
		this.response = response;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	
	public void logResponse()
	{
		JSONObject resp = getResponse();
		ArrayList<String> successIds = new ArrayList<String>();
		if(resp.has("data"))
		{
			try
			{
				JSONArray records = resp.getJSONArray("data");
				for(int i=0; i<records.length(); i++)
				{
					JSONObject record = records.getJSONObject(i);
					String recordId = null;
					if(record.has("details"))
					{
						JSONObject details = record.getJSONObject("details");
						recordId = details.has("id")?details.getString("id"):null;
					}
					if(record.has("code") && record.getString("code").equals("SUCCESS"))
					{
						successIds.add(recordId);
					}
					else
					{
						System.out.println(record.toString());
					}
				}
				if(successIds.size()>0)
				{
					for(String id : successIds)
					{
						System.out.println(httpMethod+" "+id+" successful");
					}
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			
		}
		else
		{
			System.out.println(resp.toString());
		}
	}
	
	public void handleGetResponse(String action, String module) throws Exception
	{
		ArrayList<String> ids = new ArrayList<String>();
		JSONObject resp = getResponse();
		if(resp!=null && resp.has("data"))
		{
			JSONArray data = resp.getJSONArray("data");
			for(int i=0; i<data.length(); i++)
			{
				JSONObject record = data.getJSONObject(i);
				if(record.has("id"))
				{
					ids.add(record.getString("id"));
				}
			}
		}
		if(action.equals("delete"))
		{
			RestApiRequest req = new RestApiRequest(RestApiRequest.DELETE, module);
			String idString = String.join(",", ids);
			req.addParam("ids", idString);
			req.executeRequest();
		}
	}

}
