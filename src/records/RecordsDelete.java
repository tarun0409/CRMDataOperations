package records;

import java.util.ArrayList;
import java.util.List;

import restapi.RestApiRequest;
import restapi.RestApiResponse;

public class RecordsDelete {
	
	private boolean allRecords;
	private boolean allModules;
	public RecordsDelete(boolean allModules, boolean allRecords)
	{
		this.allRecords = allRecords;
		this.allModules = allModules;
	}
	public void deleteAllRecords(String module) throws Exception
	{
		ArrayList<String> idParamList = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		int maxCount = 100;
		Integer page = 1;
		while(maxCount>0)
		{
			RestApiRequest getRecordsReq = new RestApiRequest(RestApiRequest.GET,module);
			getRecordsReq.addParam("page", page.toString());
			page++;
			getRecordsReq.addParam("per_page", "200");
			getRecordsReq.addParam("approved", "both");
			RestApiResponse response = getRecordsReq.executeRequest();
			if(response.getStatusCode()==200)
			{
				ids.addAll(ApiUtil.getIdsFromJSON(response.getResponse()));
			}
			else
			{
				break;
			}
			maxCount--;
		}
		int totalLen = ids.size();
		int iter = totalLen/99;
		int mod = totalLen%99;
		for(int i=1; i<=iter; i++)
		{
			int maxIndex = i*99;
			int minIndex = maxIndex-99;
			List<String> idList = ids.subList(minIndex, maxIndex);
			String idString = String.join(",", idList);
			idParamList.add(idString);
		}
		if(mod>0)
		{
			int minIndex = iter*99;
			int maxIndex = minIndex+mod;
			List<String> idList = ids.subList(minIndex, maxIndex);
			String idString = String.join(",", idList);
			idParamList.add(idString);
		}
		for(String idParam: idParamList)
		{
			RestApiRequest delRequest = new RestApiRequest(RestApiRequest.DELETE,module);
			delRequest.addParam("ids", idParam);
			delRequest.executeRequest();
		}
	}
	public void deleteRecords() throws Exception
	{
		if(this.allModules)
		{
			RestApiRequest getRecordsReq = new RestApiRequest(RestApiRequest.GET,"settings/modules");
			RestApiResponse response = getRecordsReq.executeRequest();
			ArrayList<String> moduleList = null;
			if(response.getStatusCode()==200)
			{
				moduleList = ApiUtil.getAllModulesApiNames(response.getResponse(), "deletable");
			}
			if(moduleList!=null)
			{
				for(String module: moduleList)
				{
					if(this.allRecords)
					{
						this.deleteAllRecords(module);
					}
				}
			}
		}
	}

}
