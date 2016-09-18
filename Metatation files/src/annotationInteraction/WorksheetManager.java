package annotationInteraction;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorksheetManager {
	
	//private static String script_path = "C:/Users/Hrim/Documents/queryFramework-0.1/";
	private static String script_path = "queryFramework-0.1/";
	private static String script_name = "pre_process_poem_words.py";
	
	public static List<PoemCollectionByPoet> poem_collection = PoemCollectionJsonReader.retrievePoemCollection();
	
	public static String anoto_pattern_file_path = "res/anoto_patterns/";
	private static List<String> anoto_patterns = initialize_anoto_patterns_available();
	
	private static Map<String, Long> pattern_to_worksheet_id = new HashMap<String, Long>();
	
	private static WorksheetXML worksheet_index_xml = new WorksheetXML();
	public static String worksheets_file_path = "worksheets/", worksheets_file_name = "worksheet ";
	public static List<Worksheet> active_worksheets = new ArrayList<Worksheet>();

	private static List<String> initialize_anoto_patterns_available(){
		
		List<String> anoto_patterns = new ArrayList<String>();
		
		anoto_patterns.add("70.0.5.3");
		anoto_patterns.add("70.0.10.7");
		anoto_patterns.add("70.0.10.8");
		
		return anoto_patterns;
		
	}
	
	private static String register_new_active_worksheet(long worksheet_id){
		
		String anoto_pattern_to_use = null;
		
		for(int i = 0; i < anoto_patterns.size(); i++){
		
			String anoto_pattern = anoto_patterns.get(i);
			
			if (!pattern_to_worksheet_id.containsKey(anoto_pattern)){
				
				pattern_to_worksheet_id.put(anoto_pattern, worksheet_id);
				anoto_pattern_to_use = anoto_pattern;
				break;
				
			}
			
		}
		
		return anoto_pattern_to_use;
		
	}
	
	private static boolean register_existing_active_worksheet(String anoto_pattern, long worksheet_id){
		
		boolean worksheet_registered = false;
		
		if(!pattern_to_worksheet_id.containsKey(anoto_pattern)){
			
			pattern_to_worksheet_id.put(anoto_pattern, worksheet_id);
			worksheet_registered = true;
			
		}
		
		return worksheet_registered;
		
	}
	
	public static Worksheet createWorksheet(String poem_json_file_name){
		
		Worksheet new_worksheet = null;
		
		long worksheet_id = worksheet_index_xml.generateWorksheetId();
		
		String anoto_pattern_id = register_new_active_worksheet(worksheet_id);
		if(anoto_pattern_id != null){

			try {
				
				String command =  "python " + script_path + script_name + " \"" + poem_json_file_name + "\"";
				Runtime.getRuntime().exec(command);
				
			} catch (IOException e) {
				
				e.printStackTrace();
			
			}
			
			CompositeGenerator composite_generator = new CompositeGenerator(PoemJsonReader.retrievePoemContent(poem_json_file_name));
			BufferedImage worksheet_tif = composite_generator.generateComposite(new TIFReaderWriter(anoto_pattern_file_path, anoto_pattern_id).readTIF());
			//TODO undo this comment after testing
			//new TIFReaderWriter(worksheets_file_path, worksheets_file_name + worksheet_id).writeTIF(worksheet_tif);
			Poem worksheet_content = composite_generator.getPoem();
			new_worksheet = new Worksheet(worksheet_id, anoto_pattern_id, worksheet_content, worksheet_tif);
			//TODO undo this comment after testing
			//worksheet_index_xml.createWorksheetEntry(new_worksheet, poem_json_file_name);
			active_worksheets.add(new_worksheet);
			
		}
		
		return new_worksheet;
		
	}
	
	public static Worksheet retrieveWorksheet(long worksheet_id){
		
		Worksheet retrieved_worksheet = worksheet_index_xml.retrieveWorksheetEntry(worksheet_id);
		retrieved_worksheet.setPenStrokes();
		if(register_existing_active_worksheet(retrieved_worksheet.getAnotoPatternId(), worksheet_id)){
			
			active_worksheets.add(retrieved_worksheet);
			
		}
		else{
			
			retrieved_worksheet = null;
			
		}
		
		return retrieved_worksheet;
		
	}
	
	public static void updatePenStroke(PenPoint new_pen_point){
		
		for(int i = 0 ; i < active_worksheets.size(); i++){
			
			Worksheet active_worksheet = active_worksheets.get(i);
			
			if(active_worksheet.getAnotoPatternId().equals(new_pen_point.getPatternId())){
				
				active_worksheet.updatePenStroke(new_pen_point);
				break;
				
			}
			
		}
		
	}
	
	public static void newPenStroke(PenPoint new_pen_point){
		
		for(int i = 0 ; i < active_worksheets.size(); i++){
			
			Worksheet active_worksheet = active_worksheets.get(i);
			
			if(active_worksheet.getAnotoPatternId().equals(new_pen_point.getPatternId())){
				
				active_worksheet.newPenStroke(new_pen_point);
				break;
				
			}
			
		}
		
	}
	
	private void pre_process_poem_content(){
		
	}

}
