package misc;

import records.RecordsDelete;


public class StartOperations {
	
	public static void main(String[] args) throws Exception
	{
		RecordsDelete del = new RecordsDelete(true, true, null);
		del.deleteRecords();
	}

}
