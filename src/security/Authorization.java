package security;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import restapi.RestApiRequest;
import transput.Properties;

public class Authorization {
	
	private String authtoken;
	private Date lastCreatedTime;
	private static Authorization authObject;
	
	private Authorization() throws Exception
	{
		this.authtoken = getAuthTokenFromProperties();
		lastCreatedTime = new Date();
	}
	
	public static String getAuthHeaderInfo() throws Exception
	{
		JSONObject properties = Properties.getProperties();
		String authHeader = "";
		if(properties.has("oauth") && properties.getBoolean("oauth"))
		{
			authHeader = "Zoho-oauthtoken ";
		}
		return authHeader;
	}
	
	public static String getAuthToken() throws Exception
	{
		if(authObject==null)
		{
			authObject = new Authorization();
		}
		else
		{
			Date lastCreatedTime = authObject.lastCreatedTime;
			Date currentTime = new Date();
			long difference = currentTime.getTime() - lastCreatedTime.getTime();
			difference = difference/1000;
			if(difference>=3000)
			{
				authObject.authtoken = getAuthTokenFromProperties();
				authObject.lastCreatedTime = new Date();
			}
		}
		return authObject.authtoken;
	}
	
	private static String getAuthTokenFromProperties() throws Exception
	{
		JSONObject properties = Properties.getProperties();
		String authtoken = null;
		if(properties.has("authtoken"))
		{
			authtoken = properties.getString("authtoken");
		}
		if(properties.has("oauth"))
		{
			boolean oauth = properties.getBoolean("oauth");
			if(oauth)
			{
				URIBuilder url = new URIBuilder("https://accounts.zoho.com/oauth/v2/token");
				if(properties.has("clientId") && properties.has("clientSecret") && properties.has("refreshToken"))
				{
					String clientId = properties.getString("clientId");
					String clientSecret = properties.getString("clientSecret");
					String refreshToken = properties.getString("refreshToken");
					url.addParameter("refresh_token", refreshToken);
					url.addParameter("client_id", clientId);
					url.addParameter("client_secret", clientSecret);
					url.addParameter("grant_type", "refresh_token");
				}
				HttpClient client = HttpClientBuilder.create().build();
				HttpPost request = new HttpPost(url.build());
				HttpResponse httpResp = client.execute(request);
				int responseCode = httpResp.getStatusLine().getStatusCode();
				if(responseCode==200)
				{
					JSONObject postResponse = RestApiRequest.getResponseInJsonObject(httpResp);
					if(postResponse.has("access_token"))
					{
						authtoken = postResponse.getString("access_token");
					}
				}
				
			}
		}
		return authtoken;
	}

}
