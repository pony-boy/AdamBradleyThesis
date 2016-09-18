package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class Stanza {
	
	private List<Line> lines_in_stanza;
	private Rectangle2D raw_pixel_bounds;
	
	public Stanza(List<Line> stanza_content, Rectangle2D bounds){
		
		lines_in_stanza = stanza_content;
		raw_pixel_bounds = bounds;
		
	}
	
	public List<Line> getLines(){
		
		return lines_in_stanza;
		
	}
	
	public Rectangle2D getRawPixelBounds(){
		
		return raw_pixel_bounds;
		
	}

}
