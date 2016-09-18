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

public class QueryResponseReceivedXML {

	private static Document doc;
	private static Element doc_root;
	private static File xml_file;
	
	private static String file_path = Worksheet.query_response_file_path;
	private String file_name;
	
	private long worksheet_id;
	private Worksheet worksheet;
	private String query_id;
	private String query_content;
	
	public QueryResponseReceivedXML(long worksheet_id, String query_id, String query_content, Worksheet worksheet){
		
		doc = null;
		doc_root = null;
		
		this.worksheet_id = worksheet_id;
		this.worksheet = worksheet;
		this.query_id = query_id;
		this.query_content = query_content;
		
		file_name = query_id + ".xml";
		xml_file = new File(file_path + file_name);
		
		//TODO do an open later on in a different function
		SAXBuilder doc_sax_builder = new SAXBuilder();
    	try {
    		
			doc = doc_sax_builder.build(xml_file);
			doc_root = doc.getRootElement();
			
		} catch (JDOMException | IOException e) {
			
			System.out.println("Exception when trying to open the query response file " + file_name + " \n");
			e.printStackTrace();
			
		}
		
		/*if(xml_file.exists()){
			
			
			
		}*/
		
	}
	
	public QueryResponseReceived retrieveQueryResponse(){
		
		Map<String, List<String>> word_repetitions = new HashMap<String, List<String>>();
		Map<String, List<String>> synonyms = new HashMap<String, List<String>>();
		Map<String, List<String>> antonyms = new HashMap<String, List<String>>();
		List<Map<String, List<String>>> sound = new ArrayList<Map<String, List<String>>>();
		List<Map<String, List<String>>> sound_phonemes  = new ArrayList<Map<String, List<String>>>();
		List<Map<String, List<String>>> sound_phoneme_to_words_maps  = new ArrayList<Map<String, List<String>>>();
		
		Element word_repetition_query = doc_root.getChild("word_repetition_query");
		if(word_repetition_query != null){
			
			word_repetitions = process_word_pairs(word_repetition_query);
			
		}
		
		Element sound_query = doc_root.getChild("sound_query");
		if(sound_query != null){
			
			Element alliteration_query = sound_query.getChild("alliteration");
			Map<String, List<String>> alliteration = new HashMap<String, List<String>>();
			Map<String, List<String>> alliteration_phonemes = new HashMap<String, List<String>>();
			Map<String, List<String>> alliteration_phoneme_to_words_map = new HashMap<String, List<String>>();
			if(alliteration_query != null){
				
				alliteration = process_word_pairs(alliteration_query);
				alliteration_phonemes = process_word_phonemes(alliteration_query);
				alliteration_phoneme_to_words_map = process_phonemes_to_word_maps(alliteration_query);
				
			}
			sound.add(alliteration);
			sound_phonemes.add(alliteration_phonemes);
			sound_phoneme_to_words_maps.add(alliteration_phoneme_to_words_map);
			
			Element assonance_query = sound_query.getChild("assonance");
			Map<String, List<String>> assonance = new HashMap<String, List<String>>();
			Map<String, List<String>> assonance_phonemes = new HashMap<String, List<String>>();
			Map<String, List<String>> assonance_phoneme_to_words_map = new HashMap<String, List<String>>();
			if(assonance_query != null){
				
				assonance = process_word_pairs(assonance_query);
				assonance_phonemes = process_word_phonemes(assonance_query);
				assonance_phoneme_to_words_map = process_phonemes_to_word_maps(assonance_query);
				
			}
			sound.add(assonance);
			sound_phonemes.add(assonance_phonemes);
			sound_phoneme_to_words_maps.add(assonance_phoneme_to_words_map);
			
			Element consonance_query = sound_query.getChild("consonance");
			Map<String, List<String>> consonance = new HashMap<String, List<String>>();
			Map<String, List<String>> consonance_phonemes = new HashMap<String, List<String>>();
			Map<String, List<String>> consonance_phoneme_to_words_map = new HashMap<String, List<String>>();
			if(consonance_query != null){
				
				consonance = process_word_pairs(consonance_query);
				consonance_phonemes = process_word_phonemes(consonance_query);
				consonance_phoneme_to_words_map = process_phonemes_to_word_maps(consonance_query);
				
			}
			sound.add(consonance);
			sound_phonemes.add(consonance_phonemes);
			sound_phoneme_to_words_maps.add(consonance_phoneme_to_words_map);
			
			Element end_rhyme_query = sound_query.getChild("end_rhyme");
			Map<String, List<String>> end_rhyme = new HashMap<String, List<String>>();
			if(end_rhyme_query != null){
				
				end_rhyme = process_word_pairs(end_rhyme_query);
				
			}
			sound.add(end_rhyme);
			sound_phonemes.add(null);
			sound_phoneme_to_words_maps.add(null);
			
		}
		
		Element synonyms_query = doc_root.getChild("synonyms_query");
		if(synonyms_query != null){
			
			synonyms = process_word_pairs(synonyms_query);
			
		}
		
		Element antonyms_query = doc_root.getChild("antonyms_query");
		if(antonyms_query != null){
			
			antonyms = process_word_pairs(antonyms_query);
			
		}
		
		return new QueryResponseReceived(worksheet_id, query_id, query_content, word_repetitions, sound, synonyms, antonyms, worksheet, sound_phonemes, sound_phoneme_to_words_maps);
		
	}
	
	private Map<String, List<String>> process_word_pairs(Element word_pairs_parent){
		
		Map<String, List<String>> word_pairs = new HashMap<String, List<String>>();
		List<Element> word_pairs_elements = word_pairs_parent.getChildren();
		
		if(!word_pairs_elements.isEmpty()){
			
			for(int i = 0; i < word_pairs_elements.size(); i++){
				
				String word_pair = word_pairs_elements.get(i).getText();
				String[] word_pair_split = word_pair.split("#");
				
				List<String> word_value = word_pairs.get(word_pair_split[0]);
				if(word_value == null){
				
					word_value = new ArrayList<String>();
					
				}
				
				word_value.add(word_pair_split[1]);
				word_pairs.put(word_pair_split[0], word_value);
				
			}
			
		}
		
		return word_pairs;
		
	}
	
	private Map<String, List<String>> process_phonemes_to_word_maps(Element word_pairs_parent){
		
		Map<String, List<String>> phoneme_word_pairs = new HashMap<String, List<String>>();
		
		List<Element> word_pairs_elements = word_pairs_parent.getChildren();
		
		if(!word_pairs_elements.isEmpty()){
			
			for(int i = 0; i < word_pairs_elements.size(); i++){
				
				String word_pair = word_pairs_elements.get(i).getText();
				String[] word_pair_split = word_pair.split("#");
				
				String phonemes = word_pairs_elements.get(i).getAttributeValue("phonemes");
				String[] phonemes_split = phonemes.split(", ");
				
				for(int j = 0; j < phonemes_split.length; j++){
					
					String phoneme = phonemes_split[j];
					List<String> previous_phoneme_words = phoneme_word_pairs.get(phoneme);
					if(previous_phoneme_words == null){
						
						previous_phoneme_words = new ArrayList<String>();
						
					}
					
					for(int k = 0; k < word_pair_split.length; k++){
						
						String word = word_pair_split[k];
						if(!previous_phoneme_words.contains(word)){
							
							previous_phoneme_words.add(word);
							
						}
						
					}
					
					phoneme_word_pairs.put(phoneme, previous_phoneme_words);
					
				}
			
			}
			
		}
		
		return phoneme_word_pairs;
		
	}
	
	private Map<String, List<String>> process_word_phonemes(Element word_pairs_parent){
		
		Map<String, List<String>> word_phoneme_pairs = new HashMap<String, List<String>>();
		List<Element> word_pairs_elements = word_pairs_parent.getChildren();
		
		if(!word_pairs_elements.isEmpty()){
			
			for(int i = 0; i < word_pairs_elements.size(); i++){
				
				String word_pair = word_pairs_elements.get(i).getText();
				String[] word_pair_split = word_pair.split("#");
				
				String phonemes = word_pairs_elements.get(i).getAttributeValue("phonemes");
				String[] phonemes_split = phonemes.split(", ");
				
				for(int j = 0; j < word_pair_split.length; j++){
					
					String word = word_pair_split[j];
					List<String> previous_word_phonemes = word_phoneme_pairs.get(word);
					if(previous_word_phonemes == null){
						
						previous_word_phonemes = new ArrayList<String>();
						
					}
					
					for(int k = 0; k < phonemes_split.length; k++){
						
						String phoneme = phonemes_split[k];
						if(!previous_word_phonemes.contains(phoneme)){
							
							previous_word_phonemes.add(phoneme);
							
						}
						
					}
					
					word_phoneme_pairs.put(word, previous_word_phonemes);
					
				}
				
			}
			
		}
		
		return word_phoneme_pairs;
		
	}
	
}
