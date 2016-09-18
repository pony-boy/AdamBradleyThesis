package annotationInteraction;

import java.util.List;

public class WordEntry {

	private String pos;
	private String first_recorded_use;
	private List<WordSense> word_senses;
	
	public WordEntry(String pos, String first_recorded_use, List<WordSense> word_senses){
		
		this.pos = pos;
		this.first_recorded_use = first_recorded_use;
		this.word_senses = word_senses;
		
	}
	
	public String getPOS(){
		
		return pos;
		
	}
	
	public String getFirstRecordedUse(){
		
		return first_recorded_use;
		
	}
	
	public List<WordSense> getWordSenses(){
		
		return word_senses;
		
	}
	
}
