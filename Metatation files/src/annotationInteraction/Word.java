package annotationInteraction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Word {
	
	private String word_content;
	private Rectangle2D raw_pixel_bounds;
	
	private List<String> characters_in_word;
	private List<Rectangle2D> per_character_raw_pixel_bounds;
	private List<Point2D> per_character_text_layout_locations;
	
	private String pos_in_poem;
	private List<String> pronunciations;
	
	public Word(String word, Rectangle2D bounds, List<Rectangle2D> per_character_bounds){
		
		word_content = word;
		raw_pixel_bounds = bounds;
		
		characters_in_word = get_characters_in_word();
		per_character_raw_pixel_bounds = per_character_bounds;
		
		per_character_text_layout_locations = new ArrayList<Point2D>();
		
		pos_in_poem = null;
		pronunciations = null;
		
	}
	
	public Word(String word, Rectangle2D bounds, List<Rectangle2D> per_character_bounds, List<Point2D> per_character_locations){
		
		word_content = word;
		raw_pixel_bounds = bounds;
		
		characters_in_word = get_characters_in_word();
		per_character_raw_pixel_bounds = per_character_bounds;
		
		per_character_text_layout_locations = per_character_locations;
		
		pos_in_poem = null;
		pronunciations = null;
		
	}
	
	public Word(String word, Rectangle2D bounds, List<Rectangle2D> per_character_bounds, List<Point2D> per_character_locations, String pos){
		
		word_content = word;
		raw_pixel_bounds = bounds;
		
		characters_in_word = get_characters_in_word();
		per_character_raw_pixel_bounds = per_character_bounds;
		
		per_character_text_layout_locations = per_character_locations;
		
		pos_in_poem = pos;
		pronunciations = null;
		
	}
	
	private List<String> get_characters_in_word(){
		
		List<String> characters_in_word = new ArrayList<String>();
		
		String word_trimmed = word_content.trim();
		int leading_space_in_word = word_content.length() - word_trimmed.length();

		for(int i = 0; i < word_trimmed.length(); i++){
			String char_in_word = (i == 0) ? (leading_space_in_word > 0 ? (get_leading_whitespace(leading_space_in_word) + String.valueOf(word_trimmed.charAt(i))) : String.valueOf(word_trimmed.charAt(i))) : String.valueOf(word_trimmed.charAt(i));
			characters_in_word.add(char_in_word); 
			
		}
		
		return characters_in_word;
		
	}
	
	private String get_leading_whitespace(int no_of_leading_spaces){
		
		String leading_space = "";
		
		for(int i = 0; i < no_of_leading_spaces; i++){
			
			leading_space += " ";
			
		}
		
		return leading_space;
		
	}
	
	public String getWord(){
		
		return word_content;
		
	}
	
	public Rectangle2D getRawPixelBounds(){
		
		return raw_pixel_bounds;
		
	}
	
	public List<String> getCharacters(){
		
		return characters_in_word;
		
	}
	
	public List<Rectangle2D> getCharactersRawPixelBounds(){
		
		return per_character_raw_pixel_bounds;
		
	}
	
	public void setCharactersTextLayoutLocations(List<Point2D> locations){
		
		per_character_text_layout_locations = locations;
		
	}
	
	public List<Point2D> getCharactersTextLayoutLocations(){
		
		return per_character_text_layout_locations;
		
	}
	
	public void setPOS(String pos){
		
		pos_in_poem = pos;
		
	}
	
	public String getPOS(){
		
		return pos_in_poem;
		
	}
	
	public void setPronunciations(List<String> pronunciations){
		
		this.pronunciations = pronunciations;
		
	}
	
	public List<String> getPronunciations(){
		
		return pronunciations;
		
	}

}
