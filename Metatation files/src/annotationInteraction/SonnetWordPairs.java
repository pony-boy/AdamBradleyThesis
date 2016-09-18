package annotationInteraction;

import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class SonnetWordPairs {
	
	private int sonnet_id;
	private List<WordPair> antonyms;
	//private List<WordPair> synonyms;
	
	private Rectangle2D sonnet_id_bounds;
	private TextLayout sonnet_id_layout;
	
	public SonnetWordPairs(int sonnet_id, List<WordPair> antonyms){
		
		this.sonnet_id = sonnet_id;
		this.antonyms = antonyms;
		
	}
	
	public int getSonnetId(){
		
		return sonnet_id;
		
	}

	public List<WordPair> getAntonymPairs(){
		
		return antonyms;
		
	}
	
	public void setSonnetIdBounds(Rectangle2D bounds){
		
		sonnet_id_bounds = bounds;
		
	}
	
	public Rectangle2D getSonnetIdBounds(){
		
		
		return sonnet_id_bounds;
		
	}
	
	public void setSonnetIdLayout(TextLayout layout){
		
		sonnet_id_layout = layout;
		
	}
	
	public TextLayout getSonnetIdLayout(){
		
		return sonnet_id_layout;
		
	}
	
}
