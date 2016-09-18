package annotationInteraction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class SingleSonnetViewer {
	
	private static int poem_content_image_width = 1400, poem_content_image_height = 4000;
	
	private static String font_name;
	private static int font_style, font_size;
	private static Font text_font;
	
	private static int font_color_r, font_color_g, font_color_b, font_color_a;
	public static Color default_text_color, text_highlighted_color, text_color_highlighted, text_color_on_click, highlight_color;
	
	private static int top_margin_space, left_margin_space, poem_header_break_space, stanza_break_space, line_break_space, word_break_space;
	
	private int poem_content_image_start_x, poem_content_image_start_y;
	
	private Poem poem_content_in_viewer;
	private BufferedImage poem_content_image;
	private BufferedImage switch_to_antonym_pairs_list_icon;
	private BufferedImage switch_to_close_reading_icon;
	private Rectangle2D closeReadingIconBounds = null;
	
	private JPanel viewer_panel;
	private JScrollPane viewer_scroll_pane;
	
	private PoemJsonPojo poem_pojo;
	
	private boolean is_original_sonnet;
	
	private List<WordPair> antonym_pairs;
	private int sonnet_id;
	
	private int word_pair_clicked_id = -1;
	private WordPair word_pair_clicked = null;
	
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

	public SingleSonnetViewer(int sonnet_id, boolean is_original_sonnet, List<WordPair> antonyms){
		
		new PoemJsonReader();
		poem_pojo = PoemJsonReader.retrievePoemContent("William Shakespeare # Sonnet " + sonnet_id);
		this.is_original_sonnet = is_original_sonnet; 
		antonym_pairs = antonyms;
		
		try {
			
			switch_to_antonym_pairs_list_icon = ImageIO.read(new File("res/icons/menu-52.png"));
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		try {
			
			switch_to_close_reading_icon = ImageIO.read(new File("res/icons/back-52.png"));
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		initialize_text_attributes();
		poem_content_image = new BufferedImage(poem_content_image_width, poem_content_image_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D text_content_g2d = poem_content_image.createGraphics();

		text_content_g2d.setColor(Color.WHITE);
		text_content_g2d.fillRect(0, 0, poem_content_image.getWidth(), poem_content_image.getHeight());
		get_poem_layout(text_content_g2d, text_content_g2d.getFontRenderContext());
		
		Rectangle2D poem_header_bounds = poem_content_in_viewer.getPoemHeader().getRawPixelBounds();
		System.out.println(poem_header_bounds);
		Rectangle2D poem_stanzas_bounds = poem_content_in_viewer.getPoemStanzas().getRawPixelBounds();
		System.out.println(poem_stanzas_bounds);
		int end_x = (int)Math.ceil(Math.max(poem_header_bounds.getX() + poem_header_bounds.getWidth(), poem_stanzas_bounds.getX() + poem_stanzas_bounds.getWidth()) + left_margin_space);
		int end_y = (int)Math.ceil(poem_stanzas_bounds.getY() + poem_stanzas_bounds.getHeight() + top_margin_space);
		System.out.println(end_x + " " + end_y);
		System.out.println(poem_content_image.getWidth() + " " + poem_content_image.getHeight());
		poem_content_image = poem_content_image.getSubimage(0, 0, end_x, end_y);
		text_content_g2d.dispose();

		display_antonym_pairs();
		
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
				String panel_header_text = "Antonyms";

				TextLayout panel_header = new TextLayout(panel_header_text, big_text_font, g2d.getFontRenderContext());
				int image_loc_in_panel_y = (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space + line_break_space);
				int image_loc_in_panel_x = 0;
				int response_type_id = 6;
				
				//TODO add peeking worksheet viewer panel image
				
				if(is_original_sonnet){
					
					if(switch_to_close_reading_icon != null){
						
						BufferedImage worksheet_viewer_image = WorksheetViewer2.getPoemContentImage();
						int display_space_w = 200;
						int display_space_h = poem_content_image_height;
						int worksheet_viewer_image_w = worksheet_viewer_image.getWidth();
						int worksheet_viewer_image_h = worksheet_viewer_image.getHeight();
						//g2d.drawImage(worksheet_viewer_image.getSubimage(worksheet_viewer_image_w - display_space_w, worksheet_viewer_image_h - display_space_h, display_space_w, display_space_h), left_margin_space, image_loc_in_panel_y + (poem_content_image.getHeight() / 2), null);
						
						g2d.setColor(Color.GRAY);
						g2d.drawLine(display_space_w + 2, 0, display_space_w + 2, viewer_panel.getHeight() - 1);
						g2d.drawImage(worksheet_viewer_image.getSubimage(worksheet_viewer_image_w - display_space_w, 0, display_space_w, Math.min(worksheet_viewer_image_h, display_space_h)), 0, top_margin_space, null);
						
						closeReadingIconBounds = new Rectangle2D.Double(0, 0, display_space_w, display_space_h);
						
						//g2d.drawImage(switch_to_close_reading_icon, left_margin_space, image_loc_in_panel_y + (poem_content_image.getHeight() / 2), null);
						//closeReadingIconBounds = new Rectangle2D.Double(left_margin_space, image_loc_in_panel_y + (poem_content_image.getHeight() / 2), switch_to_close_reading_icon.getWidth(), switch_to_close_reading_icon.getHeight());
						
					}
					
					image_loc_in_panel_x = (int)Math.ceil(closeReadingIconBounds.getWidth());
					//image_loc_in_panel_x = left_margin_space + switch_to_close_reading_icon.getWidth();
					
					g2d.setColor(word_pair_colors[response_type_id + 10]);
					System.out.println(response_type_id + " " + (response_type_id + 10) + word_pair_colors[response_type_id + 10]);
					//g2d.fillRect(left_margin_space + (int)Math.floor(panel_header.getBounds().getX()), (int) Math.ceil(top_margin_space + panel_header.getBounds().getY() - 5), getWidth() - (left_margin_space + (int)Math.floor(panel_header.getBounds().getX())), (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space));
					g2d.fillRect(image_loc_in_panel_x +  left_margin_space + (int)Math.floor(panel_header.getBounds().getX()), (int) Math.ceil(top_margin_space + panel_header.getBounds().getY() - 5), getWidth() - (left_margin_space + (int)Math.floor(panel_header.getBounds().getX())), (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space));
					
					g2d.setColor(Color.BLACK);
					panel_header.draw(g2d, image_loc_in_panel_x + left_margin_space, top_margin_space);
					
				}
				else{

					if(switch_to_antonym_pairs_list_icon != null){
						
						g2d.drawImage(switch_to_antonym_pairs_list_icon, 2 * left_margin_space, top_margin_space, null);
						
					}
					
				}

				poem_content_image_start_x = image_loc_in_panel_x + left_margin_space;
				poem_content_image_start_y = image_loc_in_panel_y;
				BufferedImage panel_content = poem_content_image;
				g2d.drawImage(panel_content, poem_content_image_start_x, poem_content_image_start_y, null);
				
			}
			
		};
		
		//TODO change this to control left pane size
		int padding = 250;
		viewer_panel.setPreferredSize(new Dimension(poem_content_image.getWidth() + padding, poem_content_image.getHeight() + top_margin_space + 2 * line_break_space));
		viewer_scroll_pane = new JScrollPane(viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		viewer_panel.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent me){
        		
        		Point2D point_on_viewer = me.getPoint();
        		
        		if(is_original_sonnet){
        			
        			if(closeReadingIconBounds.contains(point_on_viewer)){
        				
        				RunInterface.switchCloseAndDistant("closeReading");
        				
        			}
        			else{

        				int poem_content_image_end_x = poem_content_image_start_x + poem_content_image_width;
        				int poem_content_image_end_y = poem_content_image_start_y + poem_content_image_height;
        				
        				if((poem_content_image_start_x <= point_on_viewer.getX() && point_on_viewer.getX() <= poem_content_image_end_x) && (poem_content_image_start_y <= point_on_viewer.getY() && point_on_viewer.getY() <= poem_content_image_end_y)){

        					int current_word_pair_clicked_id = word_pair_clicked(point_on_viewer);
            				if(current_word_pair_clicked_id != -1){
            					
            					WordPair current_word_pair_clicked = antonym_pairs.get(current_word_pair_clicked_id);
            					if(!current_word_pair_clicked.equals(word_pair_clicked)){
            						
            						display_arcs_between_word_pairs(current_word_pair_clicked, highlight_color.darker());
            						List<String> word1_locs = current_word_pair_clicked.getWord1Locations();
            						for(int i = 0; i < word1_locs.size(); i++){
            							
            							repaint_word(word1_locs.get(i), highlight_color);
            							
            						}
            						List<String> word2_locs = current_word_pair_clicked.getWord2Locations();
            						for(int i = 0; i < word2_locs.size(); i++){
            							
            							repaint_word(word2_locs.get(i), highlight_color);
            							
            						}
            						
            						word_pair_clicked_id = current_word_pair_clicked_id;
                    				word_pair_clicked = current_word_pair_clicked;
                    				System.out.println("word_pair_clicked: " + current_word_pair_clicked.getWord1() + " X " + current_word_pair_clicked.getWord2());
                    				CorpusViewerAntonymPairs.wordPairClickedInOriginalSonnet(current_word_pair_clicked);
                    				
                    				viewer_panel.repaint();
            						
            					}
            				
            				}
            				else{
            					
            					display_arcs_between_word_pairs(word_pair_clicked, Color.WHITE);
            					display_arcs_between_word_pairs(word_pair_clicked, highlight_color);
            					List<String> word1_locs = word_pair_clicked.getWord1Locations();
        						for(int i = 0; i < word1_locs.size(); i++){
        							
        							repaint_word(word1_locs.get(i), highlight_color);
        							
        						}
        						List<String> word2_locs = word_pair_clicked.getWord2Locations();
        						for(int i = 0; i < word2_locs.size(); i++){
        							
        							repaint_word(word2_locs.get(i), highlight_color);
        							
        						}
            					word_pair_clicked_id = -1;
            					word_pair_clicked = null;
            					CorpusViewerAntonymPairs.wordPairUnClickedInOriginalSonnet();
            					
            					viewer_panel.repaint();
            					
            				}
        					
        				}
       
        			}
        			
        		}
        		else{
        			
        			double switch_button_image_start_x = 2 * left_margin_space;
            		double switch_button_image_start_y = top_margin_space;
            		double switch_button_image_end_x = switch_button_image_start_x + switch_to_antonym_pairs_list_icon.getWidth();
            		double switch_button_image_end_y = switch_button_image_start_y + switch_to_antonym_pairs_list_icon.getHeight();
        		
        			if((switch_button_image_start_x <= point_on_viewer.getX() && point_on_viewer.getX() <= switch_button_image_end_x) && (switch_button_image_start_y <= point_on_viewer.getY() && point_on_viewer.getY() <= switch_button_image_end_y)){

            			CorpusViewer.switchToAntonyms();
            			
            		}
        			
        			
        		}
        		
        	}
        
		});
		
	}
	
	private int word_pair_clicked(Point2D point_clicked){
		
		int word_pair_clicked_index = -1;
		
		Point2D point_clicked_in_image = new Point2D.Double(point_clicked.getX() - poem_content_image_start_x, point_clicked.getY() - poem_content_image_start_y);
		
		for(int i = 0; i < antonym_pairs.size(); i++){
			
			WordPair antonym_pair = antonym_pairs.get(i);
			List<Arc2D> word_pair_arcs = antonym_pair.getWordPairArcs();
			List<Line2D> word_pair_lines = antonym_pair.getWordPairLines();
			
			if(!word_pair_lines.isEmpty()){

				for(int j = 0; j < word_pair_lines.size(); j++){
					
					//if(word_pair_lines.get(j).contains(point_clicked_in_image)){
					if(word_pair_lines.get(j).intersects(new Rectangle2D.Double(point_clicked_in_image.getX(), point_clicked_in_image.getY(), 2, 2))){
						
						word_pair_clicked_index = i;
						break;
						
					}
					
				}
				
			}
			else if(!word_pair_arcs.isEmpty()){

				for(int j = 0; j < word_pair_arcs.size(); j++){
					
					//if(word_pair_arcs.get(j).contains(point_clicked_in_image)){
					if(word_pair_arcs.get(j).intersects(new Rectangle2D.Double(point_clicked_in_image.getX(), point_clicked_in_image.getY(), 2, 2))){
								
						word_pair_clicked_index = i;
						break;
						
					}
					
				}
				
			}
			
			if(word_pair_clicked_index != -1){
				
				break;
				
			}
			
		}
		
		return word_pair_clicked_index;
		
	}
	
	private void display_antonym_pairs(){
		
		for(int i = 0; i < antonym_pairs.size(); i++){
			
			WordPair antonym_pair = antonym_pairs.get(i);
			display_arcs_between_word_pairs(antonym_pair, highlight_color);
			
		}
		
		for(int i = 0; i < antonym_pairs.size(); i ++){
			
			WordPair antonym_pair = antonym_pairs.get(i);
			
			List<String> word1_locs = antonym_pair.getWord1Locations();
			for(int m = 0; m < word1_locs.size(); m++){
				
				repaint_word(word1_locs.get(m), highlight_color);
				
			}
			
			List<String> word2_locs = antonym_pair.getWord2Locations();
			for(int m = 0; m < word2_locs.size(); m++){
				
				repaint_word(word2_locs.get(m), highlight_color);
				
			}
			
		}
		
	}
	
	private void repaint_word(String word_to_repaint, Color bounding_box_color_to_use){
		
		Graphics2D text_content_g2d = (Graphics2D) poem_content_image.getGraphics();
		
		text_content_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Stroke default_stroke = text_content_g2d.getStroke();
		double width = 1.5;
		Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		
		String[] word_to_repaint_info = word_to_repaint.split("\\|");
		int stanza_index = 0;
		int line_index = Integer.parseInt(word_to_repaint_info[0]);
		int word_index = Integer.parseInt(word_to_repaint_info[1]);
		
		Word query_word = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
		System.out.println(query_word.getWord());
		Rectangle2D word_bounds = query_word.getRawPixelBounds();
		List<String> characters_in_word = query_word.getCharacters();
		List<Point2D> character_locations = query_word.getCharactersTextLayoutLocations();
		
		Rectangle2D line_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getRawPixelBounds();
		
		double word_start_y = line_bounds.getY();
		double word_end_y = line_bounds.getY() + line_bounds.getHeight();
		
		int extra_padding = 1;
		text_content_g2d.setColor(Color.WHITE);
		//text_content_g2d.fillRect((int) Math.floor(word_bounds.getX()) - extra_padding, (int) Math.floor(word_bounds.getY()) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_bounds.getHeight()) + 2 * extra_padding);
		text_content_g2d.fillRect((int) Math.floor(word_bounds.getX()) - extra_padding, (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
		
		text_content_g2d.setColor(bounding_box_color_to_use);
		/*
		if(is_pen_stroke_word(word_to_repaint)){
			
			//text_content_g2d.fillRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_bounds.getY()) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_bounds.getHeight()) + 2 * extra_padding);
			text_content_g2d.fillRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
			
			text_content_g2d.setStroke(new_stroke);
			text_content_g2d.setColor(bounding_box_color_to_use.darker());
			//text_content_g2d.drawRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_bounds.getY()) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_bounds.getHeight()) + 2 * extra_padding);
			text_content_g2d.drawRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
			
			text_content_g2d.setStroke(default_stroke);
			
		}
		else{
		
			//text_content_g2d.fillRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_bounds.getY()) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_bounds.getHeight()) + 2 * extra_padding);
			text_content_g2d.fillRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
			
			
		}
		*/
		text_content_g2d.fillRect((int) Math.floor(word_bounds.getX() - extra_padding), (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
		
		
		Color text_color_to_use = default_text_color;//text_highlighted_color;
		//System.out.println("char locs");
		//System.out.println(character_locations.size());
		for(int j = 0; j < character_locations.size(); j++){
			
			text_content_g2d.setColor(text_color_to_use);
			TextLayout layout = new TextLayout(characters_in_word.get(j), text_font, text_content_g2d.getFontRenderContext());
			Point2D character_location = character_locations.get(j);
			layout.draw(text_content_g2d, (float)character_location.getX(), (float)character_location.getY());
			
		}
		
		text_content_g2d.dispose();
		
	}
	
	private void display_arcs_between_word_pairs(WordPair antonym_pair, Color word_set_color){

		Color color_to_use = new Color(word_set_color.getRed(), word_set_color.getGreen(), word_set_color.getBlue(), word_set_color.getAlpha());
		Color click_color = new Color(word_set_color.getRGB()).darker();
		
		Graphics2D g2d = (Graphics2D) poem_content_image.getGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Stroke default_stroke = g2d.getStroke();
		double width = 2.0;
		Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		g2d.setStroke(new_stroke);
		
		List<String> word1_locs = antonym_pair.getWord1Locations();
		List<String> word2_locs = antonym_pair.getWord2Locations();
		
		List<Arc2D> word_pair_arcs = new ArrayList<Arc2D>();
		List<Line2D> word_pair_lines = new ArrayList<Line2D>();
		
		for(int i = 0; i < word1_locs.size(); i++){
			
			System.out.println(word1_locs.get(i));
			String[] word1_loc_split = word1_locs.get(i).split("\\|");
			int word1_loc_line_index = Integer.parseInt(word1_loc_split[0]);
			int word1_loc_word_index = Integer.parseInt(word1_loc_split[1]);
			Rectangle2D word1_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(0).getLines().get(word1_loc_line_index).getWords().get(word1_loc_word_index).getRawPixelBounds();
			
			
			for(int j = 0; j < word2_locs.size(); j++){
				
				System.out.println(word2_locs.get(j));
				String[] word2_loc_split = word2_locs.get(j).split("\\|");
				int word2_loc_line_index = Integer.parseInt(word2_loc_split[0]);
				int word2_loc_word_index = Integer.parseInt(word2_loc_split[1]);
				Rectangle2D word2_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(0).getLines().get(word2_loc_line_index).getWords().get(word2_loc_word_index).getRawPixelBounds();
				
				g2d.setColor(color_to_use);
				
				double p1_x = word1_bounds.getCenterX(), p2_x = word2_bounds.getCenterX();
				double p1_y = word1_bounds.getCenterY(), p2_y = word2_bounds.getCenterY();
				
				if(p1_y - p2_y == 0 || word1_loc_line_index == word2_loc_line_index){
					
					double height = Math.abs(p1_x - p2_x) * 0.5;
					
					if(height >= stanza_break_space){
						
						height = stanza_break_space - 2;
						
					}
					
					double top_left_x;
					if(p1_x < p2_x){
						
						top_left_x = p1_x;
						
					}
					else{
						
						top_left_x = p2_x;
						
					}

					Arc2D word_pair_arc = new Arc2D.Double(top_left_x, (p1_y - height / 2),Math.abs(p1_x - p2_x), height, 0, 180, Arc2D.OPEN);
					word_pair_arcs.add(word_pair_arc);
					g2d.draw(word_pair_arc);
					//g2d.drawArc((int)top_left_x, (int) ((int)p1_y - height / 2), (int)Math.abs(p1_x - p2_x), (int)height, 0, 180);
					
				}
				else if(p1_x - p2_x == 0){
					
					double height = Math.abs(p1_y - p2_y) * 0.25;
					
					double top_left_y;
					if(p1_y < p2_y){
						
						top_left_y = p1_y;
						
					}
					else{
						
						top_left_y = p2_y;
						
					}
					
					Arc2D word_pair_arc = new Arc2D.Double((p1_x + height/ 2), top_left_y, height, Math.abs(p1_y - p2_y), 0, 180, Arc2D.OPEN);
					word_pair_arcs.add(word_pair_arc);
					g2d.draw(word_pair_arc);
					//g2d.drawArc((int)(p1_x + height/ 2), (int) top_left_y, (int)height,  (int)Math.abs(p1_y - p2_y), 0, 180);
					
				}
				else if(p1_x != p2_x && p1_y != p2_y){
					
					if(p1_x > p2_x){
						
						double temp_x = p2_x;
						p2_x = p1_x;
						p1_x = temp_x;
						
						double temp_y = p2_y;
						p2_y = p1_y;
						p1_y = temp_y;
						
					}

					Line2D word_pair_line = new Line2D.Double(p1_x, p1_y, p2_x, p2_y);
					word_pair_lines.add(word_pair_line);
					g2d.draw(word_pair_line);
				}
				
				
			}
			
		}
		
		antonym_pair.setWordPairArcs(word_pair_arcs);
		antonym_pair.setWordPairLines(word_pair_lines);
	
		g2d.setStroke(default_stroke);
		g2d.dispose();
		
	}
	
	private static void initialize_text_attributes(){
		
		font_name = "Palatino Linotype";
		font_style = Font.PLAIN;
		font_size = 17;
		text_font = new Font(font_name, font_style, font_size);
		
		font_color_r = font_color_g = font_color_b = 0;
		font_color_a = 255;
		default_text_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		text_highlighted_color = new Color(255, 255, 255, font_color_a);
		
		highlight_color = new Color(144, 207, 212, 200);
		
		top_margin_space = 30;
		left_margin_space = 20;
		
		//line_break_space = 40;
		line_break_space = 30;
		//poem_header_break_space = stanza_break_space = line_break_space + 50;
		poem_header_break_space = stanza_break_space = line_break_space + 20;
		word_break_space = 5;
		
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
				poem_stanzas_max_y = poem_stanza.getRawPixelBounds().getY() + poem_stanza.getRawPixelBounds().getHeight();
				
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
		poem_content_in_viewer = new Poem(poem_header, new PoemStanzas(poem_stanzas, poem_stanzas_bounds));
		
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
			
			g2d.setColor(default_text_color);
			
			for(int d = 0; d < antonym_pairs.size(); d++){
				
				WordPair antonym_pair = antonym_pairs.get(d);
				String word = pojo_word_in_line.trim();
				if(word.equalsIgnoreCase(antonym_pair.getWord1()) || word.equalsIgnoreCase(antonym_pair.getWord2())){
					
					g2d.setColor(text_highlighted_color);
					break;
					
				}
				
			}
			
			line_content += pojo_word_in_line;
			List<String> pojo_char_in_word = get_characters_in_word(pojo_word_in_line);
			
			List<Rectangle2D> per_character_bounds = new ArrayList<Rectangle2D>();
			List<Point2D> per_character_text_layout_locations = new ArrayList<Point2D>();
			double word_bounds_min_x = Double.POSITIVE_INFINITY, word_bounds_max_x = Double.NEGATIVE_INFINITY, word_bounds_min_y = Double.POSITIVE_INFINITY, word_bounds_max_y = Double.NEGATIVE_INFINITY;
			
			for(int j = 0; j < pojo_char_in_word.size(); j++){
				
				TextLayout layout = new TextLayout(pojo_char_in_word.get(j), text_font, fontRenderContext);
				layout.draw(g2d, character_left_offset, top_offset);
				per_character_text_layout_locations.add(new Point2D.Double(character_left_offset, top_offset));
				
				//g2d.setColor(Color.RED);
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
			//g2d.draw(word_bounds);
			
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
	
	public JPanel getViewerPanel(){
		
		return viewer_panel;
		
	}
	
	public JScrollPane getViewerScrollPane(){
		
		return viewer_scroll_pane;
		
	}
	
	public int getSonnetId(){
		
		return sonnet_id;
		
	}
	
}
