package annotationInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SonnetsRelatedWordPairsJsonPojo {
	
	//private List<Map<String, List<String>>> sonnets_words; //word pairs only - sonnets_related_word_pairs_java_readable.json
	//private List<Map<String, List<List<List<String>>>>> sonnets_locs; // word locations only - sonnets_related_word_pairs_java_readable_locs.json
	 
	private List<Map<String, Map<String, List<Map<String, List<String>>>>>> sonnets;
	
	public List<Map<String, Map<String, List<Map<String, List<String>>>>>> getSonnetWordPairs(){
		
		return sonnets;
		
	}
	
	public void setSonnetWordPairs(List<Map<String, Map<String, List<Map<String, List<String>>>>>> word_pairs){
		
		sonnets = word_pairs;
		
	}
	
	public List<List<SonnetWordPairs>> getWordPairs(){
		
		List<List<SonnetWordPairs>> all_sonnet_word_pairs = new ArrayList<List<SonnetWordPairs>>();
		
		for(int i = 0; i < sonnets.size(); i++){
		
			List<SonnetWordPairs> sonnet_word_pairs = new ArrayList<SonnetWordPairs>();
			
			List<WordPair> antonyms = new ArrayList<WordPair>();
			Map<String, Map<String, List<Map<String, List<String>>>>> sonnet = sonnets.get(i);

			for(Map.Entry<String, Map<String, List<Map<String, List<String>>>>> relation_entry : sonnet.entrySet()){
				
				String relation_type = relation_entry.getKey();
				Map<String, List<Map<String, List<String>>>> word_pairs = relation_entry.getValue();
				
				if(relation_type.equals("antonyms")){
					
					for(Map.Entry<String, List<Map<String, List<String>>>> word_pair_entry : word_pairs.entrySet()){
						
						String word_pair = word_pair_entry.getKey();
						String[] words = word_pair.split("\\|");
						List<Map<String, List<String>>> word_pair_locations = word_pair_entry.getValue();
						
						for(int j = 0; j < word_pair_locations.size(); j++){
							
							Map<String, List<String>> word_locations = word_pair_locations.get(j);
							List<String> word_raw_locations = word_locations.get("raw_locations");
							
						}
						
						WordPair new_word_pair = new WordPair(words[0], words[1], relation_type, word_pair_locations.get(0).get("raw_locations"), word_pair_locations.get(1).get("raw_locations"), i);
						antonyms.add(new_word_pair);
						
					}
					
					SonnetWordPairs sonnet_word_pair = new SonnetWordPairs(i, antonyms); 
					sonnet_word_pairs.add(sonnet_word_pair);
					
				}
				
			} 
			
			all_sonnet_word_pairs.add(sonnet_word_pairs);
			
		}
		
		return all_sonnet_word_pairs;
		
	}
	
	/*public List<SonnetWordPairs> getWordPairs(){
		
		List<SonnetWordPairs> all_sonnet_word_pairs = new ArrayList<SonnetWordPairs>();
		
		for(int i = 0; i < sonnets.size(); i++){
			
			List<WordPair> antonyms = new ArrayList<WordPair>();
			Map<String, Map<String, List<Map<String, List<String>>>>> sonnet = sonnets.get(i);

			for(Map.Entry<String, Map<String, List<Map<String, List<String>>>>> relation_entry : sonnet.entrySet()){
				
				String relation_type = relation_entry.getKey();
				Map<String, List<Map<String, List<String>>>> word_pairs = relation_entry.getValue();
				
				if(relation_type.equals("antonyms")){
					
					for(Map.Entry<String, List<Map<String, List<String>>>> word_pair_entry : word_pairs.entrySet()){
						
						String word_pair = word_pair_entry.getKey();
						String[] words = word_pair.split("\\|");
						List<Map<String, List<String>>> word_pair_locations = word_pair_entry.getValue();
						
						for(int j = 0; j < word_pair_locations.size(); j++){
							
							Map<String, List<String>> word_locations = word_pair_locations.get(j);
							List<String> word_raw_locations = word_locations.get("raw_locations");
							
						}
						
						WordPair new_word_pair = new WordPair(words[0], words[1], relation_type, word_pair_locations.get(0).get("raw_locations"), word_pair_locations.get(1).get("raw_locations"), i);
						antonyms.add(new_word_pair);
						
					}
					
				}
				
			} 
			
			SonnetWordPairs sonnet_word_pair = new SonnetWordPairs(i, antonyms); 
			all_sonnet_word_pairs.add(sonnet_word_pair);
			
		}
		
		return all_sonnet_word_pairs;
		
	}
	
	*/
	
}
