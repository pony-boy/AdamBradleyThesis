package annotationInteraction;

import java.io.File;

public class QueryResponseCheckRunnable implements Runnable{

	private final String query_response_file_path;
	private final String query_response_file_name;
	private final String query_content;
	private final Worksheet worksheet;
	private boolean response_received;
	private final File query_response_file;
	
	public QueryResponseCheckRunnable(String query_response_file_path, String query_response_file_name, String query_content, Worksheet worksheet){
		
		this.query_response_file_path = query_response_file_path;
		this.query_response_file_name = query_response_file_name;
		this.query_content = query_content;
		this.worksheet = worksheet;
		
		this.response_received = false;
		this.query_response_file = new File(query_response_file_path + query_response_file_name + ".xml");
		
	}
	
	@Override
	public void run() {
		
		while(!response_received){
			
			//System.out.println("checking for " + query_response_file_name + " " + query_response_file.exists());
			if(query_response_file.exists()){
			
				System.out.println("response found " + query_response_file_name);
				worksheet.notifyResponseReceived(query_response_file_name, query_content);
				response_received = true;
			
			}
			else{
			
				try {
					
					Thread.sleep(2000);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

}
