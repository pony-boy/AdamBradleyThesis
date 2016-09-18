package annotationInteraction;

import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordPair {
	
	private String word1, word2;
	private String relation;
	private List<String> word1_locations, word2_locations;
	private int sonnet_id;
	
	// set these in CorpusViewerAntonymPairs
	
	private TextLayout word1_layout, word2_layout, x_layout;
	private Point2D word1_layout_start, word2_layout_start, x_layout_start;
	private Rectangle2D word1_bounds, word2_bounds, x_bounds;
	
	private List<Arc2D> word_pair_arcs;
	private List<Line2D> word_pair_lines;
	
	public WordPair(String word1, String word2, String relation, List<String> word1_locations, List<String> word2_locations, int sonnet_id){
		
		this.word1 = word1;
		this.word2 = word2;
		this.relation = relation;
		this.word1_locations = word1_locations;
		this.word2_locations = word2_locations;
		this.sonnet_id = sonnet_id;
		
		this.word_pair_arcs = new ArrayList<Arc2D>();
		this.word_pair_lines = new ArrayList<Line2D>();
		
		remove_s_locs();
		
	}
	
	private void remove_s_locs(){
		
		//System.out.println(sonnet_id  + ": " + word1 + " " + word2);
		
		String prev_line_index = null, prev_word_index = null;
		
		for(Iterator<String> iterator = word1_locations.iterator(); iterator.hasNext();){
			
			String[] word_loc = iterator.next().split("\\|");
			//System.out.println(prev_line_index + ", " + prev_word_index);
			
			if(prev_line_index != null && prev_word_index != null){
				
				if(word_loc[0].equals(prev_line_index) && word_loc[1].equals((Integer.parseInt(prev_word_index) + 1) + "")){
					
					iterator.remove();
					//System.out.println("word1 loc removed");
					
				}
				
			}

			prev_line_index = word_loc[0];
			prev_word_index = word_loc[1];

			
		}
		
		prev_line_index = null; prev_word_index = null;
		
		for(Iterator<String> iterator = word2_locations.iterator(); iterator.hasNext();){
			
			String[] word_loc = iterator.next().split("\\|");
			
			if(prev_line_index != null && prev_word_index != null){
				
				if(word_loc[0].equals(prev_line_index) && word_loc[1].equals((Integer.parseInt(prev_word_index) + 1) + "")){
						
					iterator.remove();
					//System.out.println("word2 loc removed");
					
				}
				
			}

			prev_line_index = word_loc[0];
			prev_word_index = word_loc[1];
			
		}
		
	}
	
	public String getWord1(){
		
		return word1;
		
	}
	
	public String getWord2(){
		
		return word2;
		
	}
	
	public String getRelation(){
		
		return relation;
		
	}
	
	public List<String> getWord1Locations(){
		
		return word1_locations;
		
	}
	
	public List<String> getWord2Locations(){
		
		return word2_locations;
		
	}
	
	public int getSonnetId(){
		
		return sonnet_id;
		
	}
	
	public void setWord1Bounds(Rectangle2D bounds){
		
		word1_bounds = bounds;
		
	}
	
	public void setWord2Bounds(Rectangle2D bounds){
		
		word2_bounds = bounds;
		
	}
	
	public Rectangle2D getWord1Bounds(){
		
		return word1_bounds;
		
	}
	
	public Rectangle2D getWord2Bounds(){
		
		return word2_bounds;
		
	}
	
	public void setXBounds(Rectangle2D bounds){
		
		x_bounds = bounds;
		
	}
	
	public Rectangle2D getXBounds(){
		
		return x_bounds;
		
	}
	
	public void setWord1Layout(TextLayout layout){
		
		word1_layout = layout;
		
	}
	
	public TextLayout getWord1Layout(){
		
		return word1_layout;
		
	}
	
	public void setWord2Layout(TextLayout layout){
		
		word2_layout = layout;
		
	}
	
	public TextLayout getWord2Layout(){
		
		return word2_layout;
		
	}
	
	public void setXLayout(TextLayout layout){
		
		x_layout = layout;
		
	}
	
	public TextLayout getXLayout(){
		
		return x_layout;
		
	}
	
	public void setWord1LayoutStart(Point2D layout){
		
		word1_layout_start = layout;
		
	}
	
	public Point2D getWord1LayoutStart(){
		
		return word1_layout_start;
		
	}
	
	public void setWord2LayoutStart(Point2D layout){
		
		word2_layout_start = layout;
		
	}
	
	public Point2D getWord2LayoutStart(){
		
		return word2_layout_start;
		
	}
	
	public void setXLayoutStart(Point2D layout){
		
		x_layout_start = layout;
		
	}
	
	public Point2D getXLayoutStart(){
		
		return x_layout_start;
		
	}
	
	public List<Line2D> getWordPairLines(){
		
		return word_pair_lines;
		
	}
	
	public void setWordPairLines(List<Line2D> layout){
		
		word_pair_lines = layout;
		
	}
	
	public List<Arc2D> getWordPairArcs(){
		
		return word_pair_arcs;
		
	}
	
	public void setWordPairArcs(List<Arc2D> layout){
		
		word_pair_arcs = layout;
		
	}
	
}
