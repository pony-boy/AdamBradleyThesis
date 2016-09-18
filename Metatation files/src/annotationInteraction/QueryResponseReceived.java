package annotationInteraction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class QueryResponseReceived {
	
	private long worksheet_id;
	private Worksheet worksheet;
	private String query_id;
	private String query_content;
	private List<Long>pen_strokes_generating_query;
	private Long pen_stroke_at_the_end_of_which_query_generated;
	private List<PenStroke> completed_pen_strokes;
	private Poem poem_content;
	 
	private Map<String, List<String>> word_repetitions;
	private List<Map<String, List<String>>> sound;
	private List<Map<String, List<String>>> sound_phonemes;
	private List<Map<String, List<String>>> sound_phoneme_to_words_map;
	private Map<String, List<String>> synonyms;
	private Map<String, List<String>> antonyms;
	
	private List<Tile> response_tiles;
	private List<DetailResponseViewer> all_tile2s;
	
	private List<Boolean> has_query_type;
	
	public QueryResponseReceived(long worksheet_id, String query_id, String query_content, Map<String, List<String>> word_repetitions, List<Map<String, List<String>>> sound, Map<String, List<String>> synonyms, Map<String, List<String>> antonyms, Worksheet worksheet, List<Map<String, List<String>>> sound_phonemes, List<Map<String, List<String>>> sound_phoneme_to_words_map){
		
		this.word_repetitions = word_repetitions;
		this.sound = sound;
		this.sound_phonemes = sound_phonemes;
		this.sound_phoneme_to_words_map = sound_phoneme_to_words_map;
		this.synonyms = synonyms;
		this.antonyms = antonyms;
		this.worksheet_id = worksheet_id;
		this.query_id = query_id;
		this.query_content = query_content;
		this.worksheet = worksheet;
		
		has_query_type = new ArrayList<Boolean>();
		
	}
	
	public Map<String, List<String>> getWordRepetitions(){
		
		return word_repetitions;
		
	}
	
	public Map<String, List<String>> getSynonyms(){
		
		return synonyms;
		
	}
	
	public Map<String, List<String>> getAntonyms(){
		
		return antonyms;
		
	}
	
	public List<Map<String, List<String>>> getSound(){
		
		return sound;
		
	}
	
	public List<Map<String, List<String>>> getSoundPhonemes(){
		
		return sound_phonemes;
		
	}
	
	public List<Map<String, List<String>>> getSoundPhonemeToWordsMap(){
		
		return sound_phoneme_to_words_map;
		
	}
	
	public Map<String, List<String>> getSoundByType(int sound_type){
		
		return sound.get(sound_type);
		
	}
	
	public Map<String, List<String>> getSoundPhonemesByType(int sound_type){
		
		return sound_phonemes.get(sound_type);
		
	}
	
	public Map<String, List<String>> getSoundPhonemeToWordsMapByType(int sound_type){
		
		return sound_phoneme_to_words_map.get(sound_type);
		
	}
	
	public long getWorksheetId(){
		
		return worksheet_id;
		
	}
	
	public String getQueryId(){
		
		return query_id;
		
	}
	
	public List<Long> getPenStrokesGeneratingQuery(){
		
		return pen_strokes_generating_query;
		
	}
	
	public Long getPenStrokeAtTheEndOfWhichQueryGenerated(){
		
		return pen_stroke_at_the_end_of_which_query_generated;
		
	}
	
	public List<PenStroke> getCompletedPenStrokes(){
		
		return completed_pen_strokes;
		
	}
	
	public Poem getPoemContent(){
		
		return poem_content;
		
	}
	
	public List<Tile> getResponseTiles(){
		
		return response_tiles;
		
	}
	
	public List<DetailResponseViewer> getTile2s(){
		
		return all_tile2s;
		
	}

	public void setPenStrokesGeneratingQuery(List<Long> pen_stroke_ids){
		
		pen_strokes_generating_query = pen_stroke_ids;
		
	}
	
	public void setPenStrokeAtTheEndOfWhichQueryGenerated(Long pen_stroke_id){
		
		pen_stroke_at_the_end_of_which_query_generated = pen_stroke_id;
		
	}
	
	public void setCompletedPenStrokes(List<PenStroke> pen_strokes){
		
		completed_pen_strokes = pen_strokes;
		
	}
	
	public void setPoemContent(Poem poem){
		
		poem_content = poem;
		
	}
	
	public void generateTiles(){
		
		response_tiles = new ArrayList<Tile>();
		all_tile2s = new ArrayList<DetailResponseViewer>();
		
		if(!word_repetitions.isEmpty()){
			
			response_tiles.add(new Tile(poem_content, completed_pen_strokes, word_repetitions, "word_repetititons", query_id, query_content));
			all_tile2s.add(new DetailResponseViewer(poem_content, word_repetitions, "word_repetititons", query_id, query_content, worksheet, null, null));
			
			has_query_type.add(true);
			
		}
		else{
			
			has_query_type.add(false);
			
		}
		
		if(!sound.isEmpty()){
		
			for(int i = 0; i < sound.size(); i++){
				
				Map<String, List<String>> sub_sound = sound.get(i);
				Map<String, List<String>> sub_sound_phonemes = sound_phonemes.get(i);
				Map<String, List<String>> sub_sound_phoneme_to_words_map = sound_phoneme_to_words_map.get(i);
				if(!sub_sound.isEmpty()){
				
					String type = "";
					switch(i){
						
					case 0:
						type = "alliteration";
						break;
					case 1:
						type = "assonance";
						break;
					case 2:
						type = "consonance";
						break;
					case 3:
						type = "perfect_rhyme";
						break;
					
					}
					response_tiles.add(new Tile(poem_content, completed_pen_strokes, sub_sound, type, query_id, query_content));
					all_tile2s.add(new DetailResponseViewer(poem_content, sub_sound, type, query_id, query_content, worksheet, sub_sound_phonemes, sub_sound_phoneme_to_words_map));
					
					has_query_type.add(true);
					
				}
				else{
					
					has_query_type.add(false);
					
				}
				
			}
			
		}
		else{
			
			for(int i = 0; i < 4; i++){
				
				has_query_type.add(false);
				
			}
			
		}
		
		if(!synonyms.isEmpty()){
			
			response_tiles.add(new Tile(poem_content, completed_pen_strokes, synonyms, "synonyms", query_id, query_content));
			all_tile2s.add(new DetailResponseViewer(poem_content, synonyms, "synonyms", query_id, query_content, worksheet, null, null));
			
			has_query_type.add(true);
			
		}
		else{
			
			has_query_type.add(false);
			
		}
		
		if(!antonyms.isEmpty()){
		
			response_tiles.add(new Tile(poem_content, completed_pen_strokes, antonyms, "antonyms", query_id, query_content));
			all_tile2s.add(new DetailResponseViewer(poem_content, antonyms, "antonyms", query_id, query_content, worksheet, null, null));
			
			has_query_type.add(true);
			
		}
		else{
			
			has_query_type.add(false);
			
		}
		
	}
	
	public List<Boolean> getQueryTypesReceived(){
		
		return has_query_type;
		
	}
	
}
