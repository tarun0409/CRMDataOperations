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

import security.Authorization;
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
		this.authHeader = Authorization.getAuthHeaderInfo();
		this.authtoken = Authorization.getAuthToken();
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
	public static JSONObject getResponseInJsonObject(HttpResponse response)
	{
		JSONObject resp = null;
		if(response.getStatusLine().getStatusCode()==204)
		{
			return null;
		}
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
	private RestApiResponse executeGet()
	{
		RestApiResponse response = new RestApiResponse();
		response.setHttpMethod(GET);
		HttpClient client = HttpClientBuilder.create().build();
		try
		{
			HttpGet request = new HttpGet(url.build());
			request.addHeader("Authorization", authHeader+authtoken);
			request.addHeader("X-ZOHO-SERVICE","crmautomation");
			System.out.println("\n\nEXECUTING GET URL: "+url.toString());
			HttpResponse httpResp = client.execute(request);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			response.setStatusCode(statusCode);
			response.setResponse(getResponseInJsonObject(httpResp));
			//response.logResponse();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;
		
	}
	private RestApiResponse executeDelete()
	{
		RestApiResponse response = new RestApiResponse();
		response.setHttpMethod(DELETE);
		HttpClient client = HttpClientBuilder.create().build();
		try
		{
			HttpDelete request = new HttpDelete(url.build());
			request.addHeader("Authorization", authHeader+authtoken);
			System.out.println("\n\nEXECUTING DELETE URL: "+url.toString());
			HttpResponse httpResp = client.execute(request);
			int statusCode = httpResp.getStatusLine().getStatusCode();
			response.setStatusCode(statusCode);
			response.setResponse(getResponseInJsonObject(httpResp));
			response.logResponse();
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}
		return response;
	}
	public RestApiResponse executeRequest()
	{
		if(httpMethod.equals(DELETE))
		{
			return executeDelete();
		}
		if(httpMethod.equals(GET))
		{
			return executeGet();
		}
		return null;
	}

}
