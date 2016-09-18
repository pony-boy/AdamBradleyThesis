package annotationInteraction;

import java.util.List;

public class WordSense {
	
	private String sense_id;
	private String definition;
	private List<String> usage_examples;
	private List<String> usage_notes;
	
	public WordSense(String sense_id, String definition, List<String> usage_notes, List<String> usage_examples){
		
		this.sense_id = sense_id;
		this.definition = definition;
		this.usage_notes = usage_notes;
		this.usage_examples = usage_examples;
		
	}

	public String getSenseId(){
		
		return sense_id;
		
	}
	
	public String getDefinition(){
		
		return definition;
		
	}
	
	public List<String> getUsageNotes(){
		
		return usage_notes;
		
	}

	public List<String> getUsageExamples(){
		
		return usage_examples;
		
	}
	
}
