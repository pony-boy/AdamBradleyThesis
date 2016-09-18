package annotationInteraction;

import java.util.List;

public class WordDefinition {

	private String word;
	private List<String> pronunciations;
	private String etymology;
	private List<WordEntry> word_entries;
	
	public WordDefinition(String word, List<String> pronunciations, String etymology, List<WordEntry> word_entries){
		
		this.word = word;
		this.pronunciations = pronunciations;
		this.etymology = etymology;
		this.word_entries = word_entries;
		
	}
	
	public String getWord(){
		
		return word;
		
	}
	
	public List<String> getPronunciations(){
		
		return pronunciations;
		
	}
	
	public String getEtymology(){
		
		return etymology;
		
	}
	
	public List<WordEntry> getWordEntries(){
		
		return word_entries;
		
	}
	
}
