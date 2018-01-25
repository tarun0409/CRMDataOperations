package restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import transput.Properties;

public class RestApiRequest {
	
	private String authtoken;
	private URIBuilder url;
	private String authHeader;
	//private JSONObject requestBody;
	private String httpMethod;
	
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	
	public RestApiRequest(String httpMethod, String apiUrl) throws Exception
	{
		JSONObject properties = Properties.getProperties();
		if(properties.has("oauth"))
		{
			boolean oauth = properties.getBoolean("oauth");
			this.authHeader = oauth ? "Zoho-oauthtoken " : "Zoho-authtoken ";
			if(!oauth && properties.has("authtoken"))
			{
				this.authtoken = properties.getString("authtoken");
			}
		}
		this.httpMethod = httpMethod;
		if(properties.has("domain"))
		{
			String domainName = properties.getString("domain");
			this.url = new URIBuilder(domainName+"/crm/v2/"+apiUrl);
		}
		
	}
	public void addParam(String key, String value)
	{
		url.setParameter(key, value);
	}
//	public void setRequestBody(JSONObject requestBody)
//	{
//		this.requestBody = requestBody;
//	}
	private JSONObject getResponseInJsonObject(HttpResponse response)
	{
		JSONObject resp = null;
		try
		{
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			resp = new JSONObject(result.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return resp;
	}
	private void executeGet()
	{
		RestApiResponse response = new RestApiResponse();
		response.setHttpMethod(GET);
		HttpClient client = HttpClientBuilder.create().build();
		try
		{
			HttpGet request = new HttpGet(url.build());
			request.addHeader("Authorization", authHeader+authtoken);
			HttpResponse httpResp = client.execute(request);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			response.setStatusCode(statusCode);
			response.setResponse(getResponseInJsonObject(httpResp));
			response.logResponse();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void executeDelete()
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				RestApiResponse response = new RestApiResponse();
				response.setHttpMethod(DELETE);
				HttpClient client = HttpClientBuilder.create().build();
				try
				{
					HttpDelete request = new HttpDelete(url.build());
					request.addHeader("Authorization", authHeader+authtoken);
					HttpResponse httpResp = client.execute(request);
					int statusCode = httpResp.getStatusLine().getStatusCode();
					response.setStatusCode(statusCode);
					response.setResponse(getResponseInJsonObject(httpResp));
				}
				catch(Exception ce)
				{
					ce.printStackTrace();
				}
				response.logResponse();
			}
			
		}).start();
	}
	public void executeRequest()
	{
		if(httpMethod.equals(DELETE))
		{
			executeDelete();
		}
		if(httpMethod.equals(GET))
		{
			executeGet();
		}
	}

}
