package annotationInteraction;

import java.io.File;

public class QueryWordDefinitionResponseCheckRunnable implements Runnable{

	//private static final String word_definition_file_path = "C:/Users/Hrim/Documents/queryFramework-0.1/res/poem_collection/raw_json/";
	private static final String word_definition_file_path = "queryFramework-0.1/res/poem_collection/raw_json/";
	
	//C:\Users\100535571\Documents\annotationInteraction-0.1\queryFramework-0.1\res\poem_collection\raw_json
	private final String word_definition_file_name;
	private final String query_id;
	private final String query_content;
	private final Worksheet worksheet;
	private boolean response_received;
	private final File word_definition_file;
	
	public QueryWordDefinitionResponseCheckRunnable(String word_definition_file_name, String query_id, String query_content, Worksheet worksheet){
		
		this.word_definition_file_name = word_definition_file_name + ".xml";

		this.query_id = query_id;
		this.query_content = query_content;
		this.worksheet = worksheet;
		
		this.response_received = false;
		this.word_definition_file = new File(word_definition_file_path + this.word_definition_file_name);
		
	}
	
	@Override
	public void run() {
		
		while(!response_received){
			
			System.out.println("checking for " + word_definition_file_name + " " + word_definition_file.exists());
			if(word_definition_file.exists()){
			
				System.out.println("response found " + word_definition_file_name);
				worksheet.notifyDefinitionsAvailable(query_id, query_content);
				response_received = true;
			
			}
			else{
			
				try {
					
					Thread.sleep(30);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
}
