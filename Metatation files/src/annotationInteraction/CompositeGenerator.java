package annotationInteraction;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CompositeGenerator {

	private static float content_transparency = 0.5f;
	
	private static String font_name;
	private static int font_style, font_size;
	public static Font text_font;
	public static int max_font_descent;
	
	private static int font_color_r, font_color_g, font_color_b, font_color_a;
	public static Color text_color;
	
	public static int top_margin_space, left_margin_space, poem_header_break_space, stanza_break_space, line_break_space;
	
	private PoemJsonPojo poem_pojo;
	
	private Poem poem;
	
	public CompositeGenerator(PoemJsonPojo poem){
		
		poem_pojo = poem;
		
	}
	
	private static void initialize_layout(){
		
		font_name = "Palatino Linotype";
		font_style = Font.PLAIN;
		font_size = 150;
		text_font = new Font(font_name, font_style, font_size);
		
		font_color_r = font_color_g = font_color_b = 0;
		font_color_a = 230;
		text_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		
		top_margin_space = 300;
		left_margin_space = 300;
		
		line_break_space = 40;
		poem_header_break_space = stanza_break_space = line_break_space + 100;
		
	}
	
	public BufferedImage generateComposite(BufferedImage anoto_pattern){
		
		BufferedImage composite = new BufferedImage(anoto_pattern.getWidth(), anoto_pattern.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = composite.createGraphics();
	    g2d.drawImage(anoto_pattern, 0, 0, null);
	   
	    create_text_overlay(g2d);
	    
	    g2d.dispose();
	    
	    return composite;
		
	}
	
	private void create_text_overlay(Graphics2D g2d){
		
		Composite original_composite = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, content_transparency));
		
		initialize_layout();
		max_font_descent = g2d.getFontMetrics(text_font).getMaxDescent();
		get_poem_layout(g2d, g2d.getFontRenderContext());

		g2d.setComposite(original_composite);
		
	}

	private void get_poem_layout(Graphics2D g2d, FontRenderContext fontRenderContext) {
		
		int top_offset = top_margin_space;
		
		List<List<String>> pojo_poem_header = poem_pojo.getPoemHeader();
		Stanza poem_header = get_stanza_layout(g2d, fontRenderContext, pojo_poem_header, top_offset);
		top_offset += poem_header.getRawPixelBounds().getHeight() + poem_header_break_space;
		
		List<List<List<String>>> pojo_poem_stanzas = poem_pojo.getStanzas();
		List<Stanza> poem_stanzas = new ArrayList<Stanza>();
		
		double poem_stanzas_min_x = Double.POSITIVE_INFINITY, poem_stanzas_max_x = Double.NEGATIVE_INFINITY;
		double poem_stanzas_min_y = Double.POSITIVE_INFINITY, poem_stanzas_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < pojo_poem_stanzas.size(); i++){
			
			List<List<String>> pojo_poem_stanza = pojo_poem_stanzas.get(i);
			Stanza poem_stanza = get_stanza_layout(g2d, fontRenderContext, pojo_poem_stanza, top_offset);
		
			double poem_stanza_min_x = poem_stanza.getRawPixelBounds().getX();
			double poem_stanza_max_x = poem_stanza.getRawPixelBounds().getX() + poem_stanza.getRawPixelBounds().getWidth();
			
			if(i == 0){
				
				poem_stanzas_min_y = poem_stanza.getRawPixelBounds().getY();
				
				poem_stanzas_min_x = poem_stanza_min_x;
				poem_stanzas_max_x = poem_stanza_max_x;
				
			}
			else{
				
				if(i == pojo_poem_stanzas.size() - 1){
					
					poem_stanzas_max_y = poem_stanza.getRawPixelBounds().getY() + poem_stanza.getRawPixelBounds().getHeight();
					
				}
				
				if(poem_stanza_min_x < poem_stanzas_min_x){
					
					poem_stanzas_min_x = poem_stanza_min_x;
					
				}
				
				if(poem_stanza_max_x > poem_stanzas_max_x){
					
					poem_stanzas_max_x = poem_stanza_max_x;
					
				}
				
			}
			
			poem_stanzas.add(poem_stanza);
			top_offset += poem_stanza.getRawPixelBounds().getHeight() + stanza_break_space;
			
		}
		
		Rectangle2D poem_stanzas_bounds = new Rectangle2D.Double(poem_stanzas_min_x, poem_stanzas_min_y, poem_stanzas_max_x - poem_stanzas_min_x, poem_stanzas_max_y - poem_stanzas_min_y);
		//g2d.draw(poem_stanzas_bounds);
		poem = new Poem(poem_header, new PoemStanzas(poem_stanzas, poem_stanzas_bounds));
		
	}
	
	private Stanza get_stanza_layout(Graphics2D g2d, FontRenderContext fontRenderContext, List<List<String>> pojo_stanza, int top_offset){
		
		List<Line> lines_in_stanza = new ArrayList<Line>();
		
		double stanza_min_x = Double.POSITIVE_INFINITY, stanza_max_x = Double.NEGATIVE_INFINITY;
		double stanza_min_y = Double.POSITIVE_INFINITY, stanza_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < pojo_stanza.size(); i++){
			
			List<String> pojo_line = pojo_stanza.get(i);
			Line line_in_stanza = get_line_layout(g2d, fontRenderContext, pojo_line, top_offset);
			lines_in_stanza.add(line_in_stanza);
			
			double line_min_x = line_in_stanza.getRawPixelBounds().getX();
			double line_max_x = line_in_stanza.getRawPixelBounds().getWidth() + line_in_stanza.getRawPixelBounds().getX();
			
			if(i == 0){
				
				stanza_min_y = line_in_stanza.getRawPixelBounds().getY();
				
				stanza_min_x = line_min_x;
				stanza_max_x = line_max_x;
				
			}
			else{
				
				if(i == pojo_stanza.size() - 1){
					
					stanza_max_y = line_in_stanza.getRawPixelBounds().getY() + line_in_stanza.getRawPixelBounds().getHeight();
					
				}
				
				if(line_min_x < stanza_min_x){
					
					stanza_min_x = line_min_x;
					
				}
				
				if(line_max_x > stanza_max_x){
					
					stanza_max_x = line_max_x;
					
				}
				
			}
			
			top_offset += line_in_stanza.getRawPixelBounds().getHeight() + line_break_space;
			
		}
		
		Rectangle2D stanza_bounds = new Rectangle2D.Double(stanza_min_x, stanza_min_y, stanza_max_x - stanza_min_x, stanza_max_y - stanza_min_y);
		//g2d.draw(stanza_bounds);
		
		return new Stanza(lines_in_stanza, stanza_bounds);
		
	}
	
	private Line get_line_layout(Graphics2D g2d, FontRenderContext fontRenderContext, List<String> pojo_line, int top_offset){
		
		String line_content = "";
		List<Word> words_in_line = new ArrayList<Word>();
		int character_left_offset = left_margin_space;
		
		double line_bounds_min_x = Double.POSITIVE_INFINITY, line_bounds_max_x = Double.NEGATIVE_INFINITY, line_bounds_min_y = Double.POSITIVE_INFINITY, line_bounds_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < pojo_line.size(); i++){
			
			String pojo_word_in_line = pojo_line.get(i);
			line_content += pojo_word_in_line;
			List<String> pojo_char_in_word = get_characters_in_word(pojo_word_in_line);
			
			List<Rectangle2D> per_character_bounds = new ArrayList<Rectangle2D>();
			List<Point2D> per_character_text_layout_locations = new ArrayList<Point2D>();
			double word_bounds_min_x = Double.POSITIVE_INFINITY, word_bounds_max_x = Double.NEGATIVE_INFINITY, word_bounds_min_y = Double.POSITIVE_INFINITY, word_bounds_max_y = Double.NEGATIVE_INFINITY;
			
			for(int j = 0; j < pojo_char_in_word.size(); j++){
				
				g2d.setColor(text_color);
				TextLayout layout = new TextLayout(pojo_char_in_word.get(j), text_font, fontRenderContext);
				layout.draw(g2d, character_left_offset, top_offset);
				per_character_text_layout_locations.add(new Point2D.Double(character_left_offset, top_offset));
				
				g2d.setColor(Color.RED);
				Rectangle2D layout_bounds = layout.getBounds();
				Rectangle2D char_bounds = new Rectangle2D.Double(character_left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				//g2d.draw(char_bounds);
				per_character_bounds.add(char_bounds);
				
				double character_min_y = top_offset + layout_bounds.getY();
				double character_max_y = top_offset + layout_bounds.getY() + layout_bounds.getHeight();
				
				if(j == 0){
					
					word_bounds_min_x = character_left_offset + layout_bounds.getX();
					word_bounds_max_x = character_left_offset + layout_bounds.getX() + layout_bounds.getWidth();
					
					word_bounds_min_y = character_min_y;
					word_bounds_max_y = character_max_y;
					
				}
				else{
					
					if(j == pojo_char_in_word.size() - 1){
						
						word_bounds_max_x = character_left_offset + layout_bounds.getX() + layout_bounds.getWidth();
						
					}

					if(character_min_y < word_bounds_min_y){
						
						word_bounds_min_y = character_min_y;
						
					}
					
					if(character_max_y > word_bounds_max_y){
						
						word_bounds_max_y = character_max_y;
						
					}
					
				}
				
				character_left_offset += layout_bounds.getX() + layout_bounds.getWidth();
				
			}
			
			Rectangle2D word_bounds = new Rectangle2D.Double(word_bounds_min_x, word_bounds_min_y, word_bounds_max_x - word_bounds_min_x, word_bounds_max_y - word_bounds_min_y);
			g2d.draw(word_bounds);
			
			words_in_line.add(new Word(pojo_word_in_line, word_bounds, per_character_bounds, per_character_text_layout_locations));
			
			if(i == 0){
				
				line_bounds_min_x = word_bounds_min_x;
				line_bounds_max_x = word_bounds_max_x;
				
				line_bounds_min_y = word_bounds_min_y;
				line_bounds_max_y = word_bounds_max_y;
				
			}
			else{
				
				if(i == pojo_line.size() - 1){
					
					line_bounds_max_x = word_bounds_max_x;
					
				}
				
				if(word_bounds_min_y < line_bounds_min_y){
					
					line_bounds_min_y = word_bounds_min_y;
					
				}
				
				if(word_bounds_max_y > line_bounds_max_y){
					
					line_bounds_max_y = word_bounds_max_y;
					
				}
				
			}
			
		}
		
		Rectangle2D line_bounds = new Rectangle2D.Double(line_bounds_min_x, line_bounds_min_y, line_bounds_max_x - line_bounds_min_x, line_bounds_max_y - line_bounds_min_y);
		//g2d.draw(line_bounds);
		
		return new Line(line_content.trim(), line_bounds, words_in_line);
		
	}
	
	private List<String> get_characters_in_word(String word){
		
		List<String> characters_in_word = new ArrayList<String>();
		
		String word_trimmed = word.trim();
		int leading_space_in_word = word.length() - word_trimmed.length();

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
	
	public Poem getPoem(){
		
		return poem;
		
	}
	
	public static int getMaxFontDescent(){
		
		return max_font_descent;
		
	}
	
}
