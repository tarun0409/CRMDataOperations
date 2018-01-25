package misc;

import org.json.JSONObject;

import restapi.RestApiRequest;
import transput.Properties;

public class StartOperations {
	
	public static void main(String[] args) throws Exception
	{
		JSONObject properties = Properties.getProperties();
		System.out.println(properties.toString());
		String domain = null;
		String authtoken = null;
		String apiUrl = "Leads";
		boolean oauth = false;
		if(properties.has("domain"))
		{
			domain = properties.getString("domain");
		}
		if(properties.has("authtoken"))
		{
			authtoken = properties.getString("authtoken");
		}
		//RestApiRequest request = new RestApiRequest(RestApiRequest.DELETE,domain,apiUrl,oauth,authtoken);
//		request.addParam("ids", "1000000036020");
//		request.executeRequest();
	}

}
