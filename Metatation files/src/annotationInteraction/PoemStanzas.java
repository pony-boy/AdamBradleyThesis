package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class PoemStanzas {
	
	private List<Stanza> stanzas_in_poem;
	private Rectangle2D raw_pixel_bounds;
	
	public PoemStanzas(List<Stanza> stanzas, Rectangle2D bounds){
		
		stanzas_in_poem = stanzas;
		raw_pixel_bounds = bounds;
		
	}
	
	public List<Stanza> getStanzas(){
		
		return stanzas_in_poem;
		
	}
	
	public Rectangle2D getRawPixelBounds(){
		
		return raw_pixel_bounds;
		
	}

}
