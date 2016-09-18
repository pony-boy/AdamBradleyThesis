package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class Line {
	
	private String line_content;
	private Rectangle2D raw_pixel_bounds;
	
	List<Word> words_in_line;
	
	public Line(String line, Rectangle2D bounds, List<Word> words){
		
		line_content = line;
		raw_pixel_bounds = bounds;
		words_in_line = words;
		
	}
	
	public String getLine(){
		
		return line_content;
		
	}
	
	public List<Word> getWords(){
		
		return words_in_line;
		
	}
	
	public Rectangle2D getRawPixelBounds(){
		
		return raw_pixel_bounds;
		
	}

}
