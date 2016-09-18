package annotationInteraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class QueryWordDefinitionResponseXML {

	//private static String word_definition_file_path = "C:/Users/Hrim/Documents/queryFramework-0.1/res/poem_collection/raw_json/";
	private static String word_definition_file_path = "queryFramework-0.1/res/poem_collection/raw_json/";
	private String word_definition_file_name;
	
	private static Document doc;
	private static Element doc_root;
	private static File xml_file;
	
	private long worksheet_id;
	
	public QueryWordDefinitionResponseXML(long worksheet_id, String word_definition_file_name){
		
		doc = null;
		doc_root = null;
		
		this.worksheet_id = worksheet_id;
		
		this.word_definition_file_name = word_definition_file_name + ".xml";
		xml_file = new File(word_definition_file_path + this.word_definition_file_name);

	}
	
	public void openWordDefinitionXML(){
		
		SAXBuilder doc_sax_builder = new SAXBuilder();
    	try {
    		
			doc = doc_sax_builder.build(xml_file);
			doc_root = doc.getRootElement();
			
		} catch (JDOMException | IOException e) {
			
			System.out.println("Exception when trying to open the query response file " + this.word_definition_file_name + " \n");
			e.printStackTrace();
			
		}
		
	}
	
	public Map<String, WordDefinition> retrieveWordDefinitions(){
		
		openWordDefinitionXML();
		
		Map<String, WordDefinition> word_definitions = new HashMap<String, WordDefinition>();
		
		List<Element> word_elements = doc_root.getChildren();
		if(!word_elements.isEmpty()){
			
			for(int i = 0; i < word_elements.size(); i++){
				
				Element word_element = word_elements.get(i);
				
				Element pronunciations_element = word_element.getChild("pronunciations");
				List<String> pronunciations = new ArrayList<String>();
				
				if(pronunciations_element != null){
				
					List<Element> word_pronunciation_elements = pronunciations_element.getChildren();
					for(int m = 0; m < word_pronunciation_elements.size(); m++){
						
						pronunciations.add(word_pronunciation_elements.get(m).getTextTrim());
						
					}
					
				}
				
				String etymology = word_element.getChildTextTrim("etymology");
				
				List<Element> word_entry_elements = word_element.getChildren("entry");
				
				WordDefinition word_definition = null;
				List<WordEntry> word_entries = new ArrayList<WordEntry>();
				
				if(!word_entry_elements.isEmpty()){
					
					for(int j = 0; j < word_entry_elements.size(); j++){
						
						WordEntry word_entry = null;
						
						Element word_entry_element = word_entry_elements.get(j);
						
						String pos = word_entry_element.getChildTextTrim("pos");
						String first_recorded_use = word_entry_element.getChildTextTrim("first_recorded_use");
						
						List<Element> word_sense_elements = word_entry_element.getChild("senses").getChildren();
						if(!word_sense_elements.isEmpty()){
							
							List<WordSense> word_senses = new ArrayList<WordSense>();
							
							for(int k = 0; k < word_sense_elements.size(); k++){
								
								Element word_sense_element = word_sense_elements.get(k);
								String all_usage_notes = word_sense_element.getChildText("usage_note");
								String all_usage_examples = word_sense_element.getChildText("usage_example");
								
								List<String> usage_notes = new ArrayList<String>();
								if(all_usage_notes != null){

									String[] usage_notes_split = all_usage_notes.split("}");
									for(int r = 0; r < usage_notes_split.length; r++){
										
										usage_notes.add(usage_notes_split[r].substring(1, usage_notes_split[r].length()));
										
									}
									
								}
								
								List<String> usage_examples = new ArrayList<String>();
								if(all_usage_examples != null){

									String[] usage_examples_split = all_usage_examples.split("\\|");
									for(int r = 0; r < usage_examples_split.length; r++){
										
										if(usage_examples_split[r] != null && !usage_examples_split[r].isEmpty()){
										
											usage_examples.add(usage_examples_split[r]);
											
										}
										
									}
									
								}
								
								word_senses.add(new WordSense(word_sense_element.getAttributeValue("id"), word_sense_element.getChildText("definition"), usage_notes, usage_examples));
								
							}
							
							if(!word_senses.isEmpty()){
								
								word_entry = new WordEntry(pos, first_recorded_use, word_senses);
								
							}
							
						}
						
						if(word_entry != null){
							
							word_entries.add(word_entry);
							
						}
						
					}
					
				}
				
				if(!word_entries.isEmpty()){
					
					word_definition = new WordDefinition(word_element.getText().trim(), pronunciations, etymology, word_entries);
					word_definitions.put(word_element.getText().trim(), word_definition);
					
				}
				
			}
			
		}
	
		return word_definitions;
		
	}
	
}
