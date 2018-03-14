package records;

import java.util.ArrayList;
import java.util.List;

import restapi.RestApiRequest;
import restapi.RestApiResponse;

public class RecordsDelete {
	
	private boolean allRecords;
	private boolean allModules;
	private String[] modules;
	public RecordsDelete(boolean allModules, boolean allRecords, String[] modules)
	{
		this.allRecords = allRecords;
		this.allModules = allModules;
		if(!allModules)
		{
			this.modules = modules;
		}
	}
	public void deleteAllRecords(String module) throws Exception
	{
		ArrayList<String> idParamList = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		int maxCount = 700;
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
				int sc = response.getStatusCode();
				System.out.println("\n\nResponse code: "+sc);
				if(sc!=204)
				{
					System.out.println("\n\n"+response.getResponse().toString()+"\n\n\n");
				}
				break;
			}
			maxCount--;
		}
//		System.out.println("\n\nSystem going to sleep for sometime\n\n");
//		Thread.sleep(5000);
		int totalLen = ids.size();
		int iter = totalLen/99;
		int mod = totalLen%99;
		ArrayList<Integer> lens = new ArrayList<Integer>();
		for(int i=1; i<=iter; i++)
		{
			int maxIndex = i*99;
			int minIndex = maxIndex-99;
			List<String> idList = ids.subList(minIndex, maxIndex);
			String idString = String.join(",", idList);
			idParamList.add(idString);
			lens.add(idList.size());
		}
		if(mod>0)
		{
			int minIndex = iter*99;
			int maxIndex = minIndex+mod;
			List<String> idList = ids.subList(minIndex, maxIndex);
			String idString = String.join(",", idList);
			idParamList.add(idString);
			lens.add(idList.size());
		}
		int deleted = 0;
		int delIndex = 0;
		for(String idParam: idParamList)
		{
			RestApiRequest delRequest = new RestApiRequest(RestApiRequest.DELETE,module);
			delRequest.addParam("ids", idParam);
			RestApiResponse resp = delRequest.executeRequest();
			if(resp.getStatusCode()==200 && delIndex<lens.size())
			{
				deleted+=lens.get(delIndex);
				System.out.println("\n\n"+deleted+" out of "+totalLen+" deleted");
				delIndex++;
			}
			else
			{
				System.out.println("\n\nThere was a problem while trying to delete the records!");
			}
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
				int inventoryCount = 0;
				boolean productsDone = false;
				for(String module: moduleList)
				{
					if(module.equals("Products"))
					{
						continue;
					}
					if(this.allRecords)
					{
						this.deleteAllRecords(module);
					}
					if(module.equals("Quotes") || module.equals("Sales_Orders") || module.equals("Purchase_Orders") || module.equals("Invoices"))
					{
						inventoryCount++;
					}
					if(inventoryCount==4 && !productsDone)
					{
						this.deleteAllRecords("Products");
						productsDone=true;
					}
				}
			}
		}
		else if(this.modules!=null)
		{
			for(int i=0; i<this.modules.length; i++)
			{
				String module = modules[i];
				if(this.allRecords)
				{
					this.deleteAllRecords(module);
				}
			}
			
		}
	}

}
