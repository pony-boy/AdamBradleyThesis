package annotationInteraction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class CorpusViewerAntonymPairs {
	
	private static List<List<SonnetWordPairs>> all_sonnets_word_pairs;
	
	private static String font_name;
	private static int word_pair_font_style, word_pair_font_size;
	private static int sonnet_id_font_style, sonnet_id_font_size;
	private static Font word_pair_font, sonnet_id_font;
	
	private static int font_color_r, font_color_g, font_color_b, font_color_a;
	public static Color default_word_pair_color, default_sonnet_id_color, word_pair_color_on_click, word_pair_color_not_on_click;
	
	private static int top_margin_space, left_margin_space, between_sonnet_break_space, within_sonnet_break_space, line_break_space, word_break_space;
	
	private static int corpus_content_image_width = 1000, corpus_content_image_height = 40000;
	private static BufferedImage corpus_content_image, corpus_content_image_on_word_pair_click;
	
	private static WordPair word_pair_clicked;
	private static SonnetWordPairs sonnet_id_clicked;
	
	private static JPanel viewer_panel;
	private static JScrollPane viewer_scroll_pane;
	
	private static int corpus_content_image_start_x, corpus_content_image_start_y;
	
	private static Color[] word_pair_colors = {
		new Color(177, 199, 255, 153),
		new Color(196, 255, 195, 153).darker(),
		new Color(255, 175, 197, 153),
		new Color(255, 201, 255, 153),
		new Color(255, 220, 160, 153),
		new Color(231, 201, 255, 153),
		new Color(255, 255, 139, 153),
		new Color(151, 255, 176, 153),
		new Color(151, 255, 176, 153),
		new Color(255, 125, 74, 153),
		new Color(151, 153, 227, 153),
		new Color(225, 151, 25, 153),
		new Color(100, 201, 104, 153),
		new Color(255, 151, 255, 153),
		new Color(98, 199, 200, 153),
		new Color(255, 221, 0, 153),
		new Color(186, 255, 0, 153),
		new Color(202, 227, 226, 153).darker(),
		new Color(226, 199, 127, 153),
		new Color(225, 225, 173, 153),
	};
	
	public CorpusViewerAntonymPairs(){
		
		getAntonymPairs();
		
		corpus_content_image = new BufferedImage(corpus_content_image_width, corpus_content_image_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D text_content_g2d = corpus_content_image.createGraphics();

		text_content_g2d.setColor(Color.WHITE);
		text_content_g2d.fillRect(0, 0, corpus_content_image.getWidth(), corpus_content_image.getHeight());
		//Point2D corpus_end = get_antonym_pairs_layout(text_content_g2d, text_content_g2d.getFontRenderContext());
		Point2D corpus_end = get_antonym_pairs_layout_filtered(text_content_g2d, text_content_g2d.getFontRenderContext());
		corpus_content_image = corpus_content_image.getSubimage(0, 0, (int) Math.ceil(corpus_end.getX()), (int) Math.ceil(corpus_end.getY()));
		text_content_g2d.dispose();
		
		create_viewer_panel();
		
	}
	
	@SuppressWarnings("serial")
	private void create_viewer_panel(){
		
		viewer_panel = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				
				Font big_text_font = new Font("Palatino Linotype", Font.BOLD + Font.ITALIC, 19);
				String panel_header_text = "Shakespearean Sonnets Corpus: Antonyms";
				
				TextLayout panel_header = new TextLayout(panel_header_text, big_text_font, g2d.getFontRenderContext());
				int image_loc_in_panel_y = (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space + line_break_space);
				int image_loc_in_panel_x = left_margin_space;
				int response_type_id = 6;
				
				g2d.setColor(word_pair_colors[response_type_id + 10]);
				g2d.fillRect(image_loc_in_panel_x +  left_margin_space + (int)Math.floor(panel_header.getBounds().getX()), (int) Math.ceil(top_margin_space + panel_header.getBounds().getY() - 5), getWidth() - (left_margin_space + (int)Math.floor(panel_header.getBounds().getX())), (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space));
				g2d.setColor(Color.BLACK);
				panel_header.draw(g2d, image_loc_in_panel_x + left_margin_space, top_margin_space);
				
				BufferedImage panel_content = corpus_content_image;

				if(word_pair_clicked != null){
					
					if(corpus_content_image_on_word_pair_click != null){

						panel_content = corpus_content_image_on_word_pair_click;
						
					}
					
				}
				
				corpus_content_image_start_x = image_loc_in_panel_x;
				corpus_content_image_start_y = image_loc_in_panel_y;
				g2d.drawImage(panel_content, image_loc_in_panel_x, image_loc_in_panel_y, null);
	
			}
		};
		
		viewer_panel.setPreferredSize(new Dimension(corpus_content_image.getWidth() + 10, corpus_content_image.getHeight() + top_margin_space + 2 * line_break_space));
		viewer_scroll_pane = new JScrollPane(viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		viewer_panel.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent me){
        		
        		double corpus_content_image_end_x = corpus_content_image_start_x + corpus_content_image.getWidth();
        		double corpus_content_image_end_y = corpus_content_image_start_y + corpus_content_image.getHeight();
        		
        		Point2D point_on_viewer = me.getPoint();
        		
        		if((corpus_content_image_start_x <= point_on_viewer.getX() && point_on_viewer.getX() <= corpus_content_image_end_x) && (corpus_content_image_start_y <= point_on_viewer.getY() && point_on_viewer.getY() <= corpus_content_image_end_y)){
        			
        			Point2D point_clicked = new Point2D.Double(point_on_viewer.getX() - corpus_content_image_start_x, point_on_viewer.getY() - corpus_content_image_start_y);
        			sonnet_id_clicked = click_on_sonnet_id(point_clicked);
        			if(sonnet_id_clicked == null){
        				
        				word_pair_clicked = click_on_word_pair(point_clicked);
        				
        				if(word_pair_clicked != null){
        					
        					corpus_content_image_on_word_pair_click = new BufferedImage(corpus_content_image_width, corpus_content_image_height, BufferedImage.TYPE_INT_ARGB);
        					Graphics2D text_content_g2d = corpus_content_image_on_word_pair_click.createGraphics();

        					text_content_g2d.setColor(Color.WHITE);
        					text_content_g2d.fillRect(0, 0, corpus_content_image.getWidth(), corpus_content_image.getHeight());
        					//Point2D corpus_end = get_antonym_pairs_layout(text_content_g2d, text_content_g2d.getFontRenderContext());
        					Point2D corpus_end = get_antonym_pairs_layout_filtered(text_content_g2d, text_content_g2d.getFontRenderContext());
        					corpus_content_image_on_word_pair_click = corpus_content_image_on_word_pair_click.getSubimage(0, 0, (int) Math.ceil(corpus_end.getX()), (int) Math.ceil(corpus_end.getY()));
        					text_content_g2d.dispose();
        					
        				}
        				
        				viewer_panel.repaint();
        				viewer_scroll_pane.getVerticalScrollBar().setValue(viewer_scroll_pane.getVerticalScrollBar().getMinimum());
        				
        			}
        			else{
        				
        				//TODO call the card switch function and pass the sonnet to open
        				CorpusViewer.switchToSonnet(sonnet_id_clicked.getSonnetId() + 1, sonnet_id_clicked.getAntonymPairs());
        				sonnet_id_clicked = null;
        				
        			}
        			
        		}
        		
        	}
		
		});
		
	}
	
	private static WordPair click_on_word_pair(Point2D click_point){
		
		WordPair word_pair_clicked = null;
		
		for(int i = 0; i < all_sonnets_word_pairs.size(); i++){

			List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);
			for(int j = 0; j < sonnet_word_pairs.size(); j++){
				
				SonnetWordPairs word_pair = sonnet_word_pairs.get(j);
				List<WordPair> antonym_pairs = word_pair.getAntonymPairs();
				for(int k = 0; k < antonym_pairs.size(); k++){
					
					WordPair antonym_pair = antonym_pairs.get(k);
					Rectangle2D word1_bounds = antonym_pair.getWord1Bounds();
					Rectangle2D word2_bounds = antonym_pair.getWord2Bounds();
					
					double word_pair_start_x = word1_bounds.getX();
					double word_pair_end_x = word2_bounds.getX() + word2_bounds.getWidth();
					
					double word_pair_start_y = Math.min(word1_bounds.getY(), word2_bounds.getY());
					double word_pair_end_y = Math.max(word1_bounds.getY() + word1_bounds.getHeight(),  word2_bounds.getY() + word2_bounds.getHeight());
					
					if((word_pair_start_x <= click_point.getX() && click_point.getX() <= word_pair_end_x) && (word_pair_start_y <= click_point.getY() && click_point.getY() <= word_pair_end_y)){
						
						System.out.println("word pair clicked");
						System.out.println(antonym_pair.getWord1() + " X " + antonym_pair.getWord2());
						word_pair_clicked = antonym_pair;
						break;
						
					}

				}
				
				if(word_pair_clicked != null){
					
					break;
					
				}
				
			}
			
			if(word_pair_clicked != null){
				
				break;
				
			}
			
		}
		
		return word_pair_clicked;
		
	}

	private static SonnetWordPairs click_on_sonnet_id(Point2D click_point){
		
		SonnetWordPairs sonnet_clicked = null;
		
		for(int i = 0; i < all_sonnets_word_pairs.size(); i++){

			List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);
			for(int j = 0; j < sonnet_word_pairs.size(); j++){
				
				SonnetWordPairs word_pair = sonnet_word_pairs.get(j);
				Rectangle2D sonnet_id_bounds = word_pair.getSonnetIdBounds();
				
				if(sonnet_id_bounds.contains(click_point)){
					
					sonnet_clicked = word_pair;
					//System.out.println("Sonnet " + (word_pair.getSonnetId() + 1));
					
				}
			
			}
			
			if(sonnet_clicked != null){
				
				break;
				
			}
		
		}
		
		return sonnet_clicked;
		
	}
	
	private static void addSonnet129Pairs(){
		
		for(int i = 0; i < all_sonnets_word_pairs.size(); i++){

			List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);
			for(int j = 0; j < sonnet_word_pairs.size(); j++){
				
				SonnetWordPairs word_pair = sonnet_word_pairs.get(j);
				if(word_pair.getSonnetId() == 128){

					List<WordPair>antonyms = word_pair.getAntonymPairs();
					
					List<String> word1_locations = new ArrayList<String>();
					word1_locations.add("10|1");
					List<String> word2_locations = new ArrayList<String>();
					word2_locations.add("10|10");
					antonyms.add(new WordPair("bliss", "woe", "antonyms", word1_locations, word2_locations, word_pair.getSonnetId()));
					word1_locations = new ArrayList<String>();
					word1_locations.add("11|3");
					word2_locations = new ArrayList<String>();
					word2_locations.add("10|10");
					antonyms.add(new WordPair("joy", "woe", "antonyms", word1_locations, word2_locations, word_pair.getSonnetId()));
					word1_locations = new ArrayList<String>();
					word1_locations.add("10|1");
					antonyms.add(new WordPair("bliss", "hell", "antonyms", word1_locations, antonyms.get(0).getWord2Locations(), word_pair.getSonnetId()));
					
				}
			
			}
			
		}
		
	}
	
	private static void getAntonymPairs(){
		
		SonnetsRelatedWordPairsJsonReader read_word_pairs = new SonnetsRelatedWordPairsJsonReader();
		SonnetsRelatedWordPairsJsonPojo sonnets = read_word_pairs.getSonnetsRelatedWordPairsPojo();
		all_sonnets_word_pairs = sonnets.getWordPairs();
		//TODO Remove this after; this is only for the current case
		addSonnet129Pairs();
		
	}
	
	private static void initialize_text_attributes(){
		
		font_name = "Palatino Linotype";
		
		word_pair_font_style = Font.PLAIN;
		word_pair_font_size = 15;
		word_pair_font = new Font(font_name, word_pair_font_style, word_pair_font_size);
		
		sonnet_id_font_style = Font.ITALIC;
		sonnet_id_font_size = 17;
		sonnet_id_font = new Font(font_name, sonnet_id_font_style, sonnet_id_font_size);
		
		font_color_a = 255;
		
		font_color_r = font_color_g = font_color_b = 100;
		default_word_pair_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		font_color_r = font_color_g = font_color_b = 50;
		word_pair_color_on_click = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		font_color_r = font_color_g = font_color_b = 170;
		word_pair_color_not_on_click = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		
		font_color_r = font_color_g = font_color_b = 0;
		default_sonnet_id_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		
		top_margin_space = 30;
		left_margin_space = 20;

		line_break_space = 17;
		between_sonnet_break_space = line_break_space + 20;
		within_sonnet_break_space = line_break_space + 5;
		
		
	}
	
	private static Point2D get_antonym_pairs_layout(Graphics2D g2d, FontRenderContext fontRenderContext){
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		initialize_text_attributes();
		
		int top_offset = top_margin_space;
		double max_x = Double.NEGATIVE_INFINITY;
		Color word_pair_color = default_word_pair_color;
		boolean is_word_pair_clicked = (word_pair_clicked != null);
		
		for(int i = 0; i < all_sonnets_word_pairs.size(); i++){
			
			List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);

			for(int j = 0; j < sonnet_word_pairs.size(); j++){
				
				SonnetWordPairs word_pair = sonnet_word_pairs.get(j);

				g2d.setColor(default_sonnet_id_color);
				TextLayout sonnet_id_layout = new TextLayout("Sonnet " + (word_pair.getSonnetId() + 1), sonnet_id_font, fontRenderContext);
				sonnet_id_layout.draw(g2d, left_margin_space, top_offset);
				Rectangle2D layout_bounds = sonnet_id_layout.getBounds();
				Rectangle2D sonnet_id_bounds = new Rectangle2D.Double(left_margin_space + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				word_pair.setSonnetIdBounds(sonnet_id_bounds);
				
				top_offset += within_sonnet_break_space;
				
				List<WordPair> antonym_pairs = word_pair.getAntonymPairs();
				for(int k = 0; k < antonym_pairs.size(); k++){
					
					WordPair antonym_pair = antonym_pairs.get(k);
					String word1 = antonym_pair.getWord1();
					String word2 = antonym_pair.getWord2();
					
					if(is_word_pair_clicked){
						
						String clicked1 = word_pair_clicked.getWord1();
						String clicked2 = word_pair_clicked.getWord2();
						
						if((word1.equals(clicked1) || word1.equals(clicked2)) && (word2.equals(clicked1) || word2.equals(clicked2))){
							
							word_pair_color = word_pair_color_on_click;
							
						}
						else{
							
							word_pair_color = word_pair_color_not_on_click;
							
						}
						
					}
					
					g2d.setColor(word_pair_color);

					TextLayout layout = new TextLayout(word1, word_pair_font, fontRenderContext);
					layout.draw(g2d, left_margin_space, top_offset);
					layout_bounds = layout.getBounds();
					Rectangle2D word1_bounds = new Rectangle2D.Double(left_margin_space + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
					antonym_pair.setWord1Bounds(word1_bounds);
					antonym_pair.setWord1Layout(layout);
					antonym_pair.setWord1LayoutStart(new Point2D.Double(left_margin_space, top_offset));

					long left_offset = Math.round(word1_bounds.getX() + word1_bounds.getWidth() + word_break_space);
					
					layout = new TextLayout("   X", word_pair_font, fontRenderContext);
					layout.draw(g2d, left_offset, top_offset);
					layout_bounds = layout.getBounds();
					Rectangle2D x_bounds = new Rectangle2D.Double(left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
					antonym_pair.setXBounds(x_bounds);
					antonym_pair.setXLayout(layout);
					antonym_pair.setXLayoutStart(new Point2D.Double(left_offset, top_offset));
					
					left_offset += Math.round(layout_bounds.getX() + layout_bounds.getWidth()) + word_break_space;
					
					layout = new TextLayout("   " + word2, word_pair_font, fontRenderContext);
					layout.draw(g2d, left_offset, top_offset);
					layout_bounds = layout.getBounds();
					Rectangle2D word2_bounds = new Rectangle2D.Double(left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
					antonym_pair.setWord2Bounds(word2_bounds);
					antonym_pair.setWord2Layout(layout);
					antonym_pair.setWord2LayoutStart(new Point2D.Double(left_offset, top_offset));

					if(is_word_pair_clicked){
						
						if(word_pair_color.equals(word_pair_color_on_click)){
							
							repaint_word_pair(g2d, antonym_pair, word_pair_color);
							
						}
						
					}
					
					if(max_x < (word2_bounds.getX() + word2_bounds.getWidth())){
						
						max_x = word2_bounds.getX() + word2_bounds.getWidth();
						
					}
					
					top_offset += line_break_space;

				}
				
				top_offset += between_sonnet_break_space;
				
			}
			
		}
		
		return new Point2D.Double(max_x, top_offset); 
		
	}
	
	private static Point2D get_antonym_pairs_layout_filtered(Graphics2D g2d, FontRenderContext fontRenderContext){

		boolean is_word_pair_clicked = (word_pair_clicked != null);
		
		List<SonnetWordPairs> sonnet_word_pairs_to_display = new ArrayList<SonnetWordPairs>();
		
		if(is_word_pair_clicked){
			
			for(int i = 0; i < all_sonnets_word_pairs.size(); i++){
				
				List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);
				for(int j = 0; j < sonnet_word_pairs.size(); j++){
					
					SonnetWordPairs word_pair = sonnet_word_pairs.get(j);
		
					boolean has_word_pair_clicked = false;
					List<WordPair> antonym_pairs = word_pair.getAntonymPairs();
					for(int k = 0; k < antonym_pairs.size(); k++){
						
						WordPair antonym_pair = antonym_pairs.get(k);
						String word1 = antonym_pair.getWord1();
						String word2 = antonym_pair.getWord2();
							
						String clicked1 = word_pair_clicked.getWord1();
						String clicked2 = word_pair_clicked.getWord2();
							
						if((word1.equals(clicked1) || word1.equals(clicked2)) && (word2.equals(clicked1) || word2.equals(clicked2))){
								
								has_word_pair_clicked = true;
								break;
								
						}
					
					}
				
					if(has_word_pair_clicked){
						
						sonnet_word_pairs_to_display.add(word_pair);
						
					}
					
				}
				
			}
			
		}
		
		Point2D content_end = new Point2D.Double();
		
		if(!is_word_pair_clicked){
			
			content_end = get_antonym_pairs_layout(g2d, fontRenderContext);
			
		}
		else{
			
			content_end = display_sonnet_id_and_word_pairs(g2d, fontRenderContext, sonnet_word_pairs_to_display);
			
		}

		
		return content_end; 
		
	}
	
	private static Point2D display_sonnet_id_and_word_pairs(Graphics2D g2d, FontRenderContext fontRenderContext, List<SonnetWordPairs> sonnet_word_pairs){
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		initialize_text_attributes();
		
		int top_offset = top_margin_space;
		double max_x = Double.NEGATIVE_INFINITY;
		Color word_pair_color = default_word_pair_color;
		boolean is_word_pair_clicked = (word_pair_clicked != null);
		
		for(int j = 0; j < sonnet_word_pairs.size(); j++){
			
			SonnetWordPairs word_pair = sonnet_word_pairs.get(j);

			g2d.setColor(default_sonnet_id_color);
			TextLayout sonnet_id_layout = new TextLayout("Sonnet " + (word_pair.getSonnetId() + 1), sonnet_id_font, fontRenderContext);
			sonnet_id_layout.draw(g2d, left_margin_space, top_offset);
			Rectangle2D layout_bounds = sonnet_id_layout.getBounds();
			Rectangle2D sonnet_id_bounds = new Rectangle2D.Double(left_margin_space + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
			word_pair.setSonnetIdBounds(sonnet_id_bounds);
			
			top_offset += within_sonnet_break_space;
			
			List<WordPair> antonym_pairs = word_pair.getAntonymPairs();
			for(int k = 0; k < antonym_pairs.size(); k++){
				
				WordPair antonym_pair = antonym_pairs.get(k);
				String word1 = antonym_pair.getWord1();
				String word2 = antonym_pair.getWord2();
				
				if(is_word_pair_clicked){
					
					String clicked1 = word_pair_clicked.getWord1();
					String clicked2 = word_pair_clicked.getWord2();
					
					if((word1.equals(clicked1) || word1.equals(clicked2)) && (word2.equals(clicked1) || word2.equals(clicked2))){
						
						word_pair_color = word_pair_color_on_click;
						
					}
					else{
						
						word_pair_color = word_pair_color_not_on_click;
						
					}
					
				}
				
				g2d.setColor(word_pair_color);

				TextLayout layout = new TextLayout(word1, word_pair_font, fontRenderContext);
				layout.draw(g2d, left_margin_space, top_offset);
				layout_bounds = layout.getBounds();
				Rectangle2D word1_bounds = new Rectangle2D.Double(left_margin_space + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				antonym_pair.setWord1Bounds(word1_bounds);
				antonym_pair.setWord1Layout(layout);
				antonym_pair.setWord1LayoutStart(new Point2D.Double(left_margin_space, top_offset));

				long left_offset = Math.round(word1_bounds.getX() + word1_bounds.getWidth() + word_break_space);
				
				layout = new TextLayout("   X", word_pair_font, fontRenderContext);
				layout.draw(g2d, left_offset, top_offset);
				layout_bounds = layout.getBounds();
				Rectangle2D x_bounds = new Rectangle2D.Double(left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				antonym_pair.setXBounds(x_bounds);
				antonym_pair.setXLayout(layout);
				antonym_pair.setXLayoutStart(new Point2D.Double(left_offset, top_offset));
				
				left_offset += Math.round(layout_bounds.getX() + layout_bounds.getWidth()) + word_break_space;
				
				layout = new TextLayout("   " + word2, word_pair_font, fontRenderContext);
				layout.draw(g2d, left_offset, top_offset);
				layout_bounds = layout.getBounds();
				Rectangle2D word2_bounds = new Rectangle2D.Double(left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				antonym_pair.setWord2Bounds(word2_bounds);
				antonym_pair.setWord2Layout(layout);
				antonym_pair.setWord2LayoutStart(new Point2D.Double(left_offset, top_offset));

				if(is_word_pair_clicked){
					
					if(word_pair_color.equals(word_pair_color_on_click)){
						
						repaint_word_pair(g2d, antonym_pair, word_pair_color);
						
					}
					
				}
				
				if(max_x < (word2_bounds.getX() + word2_bounds.getWidth())){
					
					max_x = word2_bounds.getX() + word2_bounds.getWidth();
					
				}
				
				top_offset += line_break_space;

			}
			
			top_offset += between_sonnet_break_space;
			
		}
		
		return new Point2D.Double(max_x, top_offset);
		
	}
	
	private static void repaint_word_pair(Graphics2D g2d, WordPair antonym_pair, Color word_pair_color){
		
		int padding = 1;
		
		Rectangle2D word1_bounds = antonym_pair.getWord1Bounds();
		Rectangle2D word2_bounds = antonym_pair.getWord2Bounds();
		
		int word_pair_start_x = (int)Math.ceil(word1_bounds.getX()) - padding;
		int word_pair_end_x = (int)Math.ceil(word2_bounds.getX() + word2_bounds.getWidth()) + padding;
		
		int word_pair_start_y = (int)Math.ceil(Math.min(word1_bounds.getY(), word2_bounds.getY())) - padding;
		int word_pair_end_y = (int)Math.ceil(Math.max(word1_bounds.getY() + word1_bounds.getHeight(),  word2_bounds.getY() + word2_bounds.getHeight())) + padding;

		g2d.setColor(Color.WHITE);
		g2d.fillRect(word_pair_start_x, word_pair_start_y, word_pair_end_x - word_pair_start_x, word_pair_end_y - word_pair_start_y);
		
		//Color of bounding box
		g2d.setColor(new Color(144, 207, 212, 200));
		g2d.fillRect(word_pair_start_x, word_pair_start_y, word_pair_end_x - word_pair_start_x, word_pair_end_y - word_pair_start_y);
		
		g2d.setColor(word_pair_color);
		antonym_pair.getWord1Layout().draw(g2d, (int)antonym_pair.getWord1LayoutStart().getX(), (int)antonym_pair.getWord1LayoutStart().getY());
		antonym_pair.getXLayout().draw(g2d, (int)antonym_pair.getXLayoutStart().getX(), (int)antonym_pair.getXLayoutStart().getY());
		antonym_pair.getWord2Layout().draw(g2d, (int)antonym_pair.getWord2LayoutStart().getX(), (int)antonym_pair.getWord2LayoutStart().getY());
		
	}
	
	public JPanel getViewerPanel(){
		
		return viewer_panel;
		
	}
	
	public JScrollPane getViewerScrollPane(){
		
		return viewer_scroll_pane;
		
	}
	
	public SonnetWordPairs getSonnetAntonymPairs(int sonnet_id){
		
		SonnetWordPairs antonym_pairs = null;
		
		for(int i = 0; i < all_sonnets_word_pairs.size(); i++){

			List<SonnetWordPairs> sonnet_word_pairs = all_sonnets_word_pairs.get(i);
			for(int j = 0; j < sonnet_word_pairs.size(); j++){
				
				SonnetWordPairs word_pair = sonnet_word_pairs.get(j);
				if(sonnet_id == word_pair.getSonnetId()){
					
					antonym_pairs = word_pair;
					break;
					
				}
			
			}
			
			if(antonym_pairs != null){
				
				break;
				
			}
			
		}
		
		return antonym_pairs;
		
	}
	
	public static void wordPairClickedInOriginalSonnet(WordPair antonym_pair_clicked){
		
		word_pair_clicked = antonym_pair_clicked;
		
		corpus_content_image_on_word_pair_click = new BufferedImage(corpus_content_image_width, corpus_content_image_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D text_content_g2d = corpus_content_image_on_word_pair_click.createGraphics();

		text_content_g2d.setColor(Color.WHITE);
		text_content_g2d.fillRect(0, 0, corpus_content_image.getWidth(), corpus_content_image.getHeight());
		//Point2D corpus_end = get_antonym_pairs_layout(text_content_g2d, text_content_g2d.getFontRenderContext());
		Point2D corpus_end = get_antonym_pairs_layout_filtered(text_content_g2d, text_content_g2d.getFontRenderContext());
		corpus_content_image_on_word_pair_click = corpus_content_image_on_word_pair_click.getSubimage(0, 0, (int) Math.ceil(corpus_end.getX()), (int) Math.ceil(corpus_end.getY()));
		text_content_g2d.dispose();
		
		viewer_panel.repaint();
		viewer_panel.revalidate();
		
		viewer_scroll_pane.getVerticalScrollBar().setValue(viewer_scroll_pane.getVerticalScrollBar().getMinimum());
		
	}
	
	public static void wordPairUnClickedInOriginalSonnet(){
		
		word_pair_clicked = null;
		viewer_panel.repaint();
		viewer_panel.revalidate();
		
		viewer_scroll_pane.getVerticalScrollBar().setValue(viewer_scroll_pane.getVerticalScrollBar().getMinimum());
		
	}
	
}
