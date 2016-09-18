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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

public class DetailResponseViewer {
	
private static int poem_content_image_width = 1400, poem_content_image_height = 1000;
	
	private JPanel viewer_panel;
	private JScrollPane viewer_scroll_pane;
	private Poem poem_content;
	
	private static String font_name;
	private static int font_style, font_size;
	private static Font text_font;
	
	private static int font_color_r, font_color_g, font_color_b, font_color_a;
	public static Color default_text_color, text_highlighted_color, text_color_highlighted, text_color_on_click;
	
	private static int top_margin_space, left_margin_space, poem_header_break_space, stanza_break_space, line_break_space, word_break_space;
	
	private Poem poem_content_in_viewer;
	private BufferedImage poem_content_image;
	
	private String tile_type;
	private String query_id;
	private String query_content;
	private Map<String, List<String>> tile_content;
	
	private List<String> pen_stroke_words;
	private boolean tile_filtered_out = false;
	private boolean tile_disabled = false;
	
	private Map<String, WordDefinition> word_definition_xml;
	private Map<String, List<String>> word_to_phonemes_map;
	private Map<String, List<String>> phoneme_to_words_map;
	private List<String> phonemes_in_response = null;
	
	private Map<Integer, List<String>> color_to_word_map;
	private Map<Rectangle2D, String> phoneme_widget_bounds_to_phoneme_map;
	private Map<String, List<Rectangle2D>> phoneme_to_phoneme_widgets_map;
	private Map<String, Integer> phoneme_to_color_map;
	
	private int poem_content_image_start_x, poem_content_image_start_y;
	
	private String word_on_hover_previous = null;
	private String word_on_hover_current = null;
	
	private String phoneme_on_click_current = null;
	private String phoneme_on_click_previous = null;
	
	private BufferedImage poem_content_image_on_hover;
	private BufferedImage switch_to_distant_reading_icon;
	
	private Integer response_type_id;
	
	private Rectangle2D distantReadingIconBounds = null;
	
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
	
	public DetailResponseViewer(Poem poem_content, Map<String, List<String>> response_content, String response_type, String query_id, String query_content, Worksheet worksheet, Map<String, List<String>> word_to_phonemes_map, Map<String, List<String>> phoneme_to_words_map){
		
		word_definition_xml = worksheet.getWordDefinitions();
		this.poem_content = poem_content;
		tile_type = response_type;
		this.query_id = query_id;
		this.query_content = query_content;
		tile_content = response_content;
		
		this.word_to_phonemes_map = word_to_phonemes_map;
		this.phoneme_to_words_map = phoneme_to_words_map;
		
		try {
			
			switch_to_distant_reading_icon = ImageIO.read(new File("res/icons/search-26.png"));
			
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		if(phoneme_to_words_map != null){
			
			phonemes_in_response = new ArrayList<String>();
			phoneme_to_color_map = new HashMap<String, Integer>();
			
			for(Entry<String, List<String>> phoneme_entry : phoneme_to_words_map.entrySet()){
				
				String phoneme = phoneme_entry.getKey();
				phonemes_in_response.add(phoneme);
				phoneme_to_color_map.put(phoneme, phonemes_in_response.indexOf(phoneme) + 1);
				
				/*System.out.println("phoneme: " + phoneme_entry.getKey());
				System.out.println("words: ");
				List<String> words = phoneme_entry.getValue();
				for(int i = 0; i < words.size(); i++){
					
					System.out.println(words.get(i));
					
				}*/
				
			}
			
		}
		/*
		if(word_to_phonemes_map != null){
		
			for(Entry<String, List<String>> word_entry : word_to_phonemes_map.entrySet()){
				
				System.out.println("word: " + word_entry.getKey());
				System.out.println("phonemes: ");
				List<String> phonemes = word_entry.getValue();
				for(int i = 0; i < phonemes.size(); i++){
					
					System.out.println(phonemes.get(i));
					
				}
				
			}
			
		}*/
		
		color_to_word_map = new HashMap<Integer, List<String>>();
		phoneme_widget_bounds_to_phoneme_map = new HashMap<Rectangle2D, String>();
		phoneme_to_phoneme_widgets_map = new HashMap<String, List<Rectangle2D>>();

		initialize_pen_stroke_words();
		
		poem_content_image = new BufferedImage(poem_content_image_width, poem_content_image_height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D text_content_g2d = poem_content_image.createGraphics();

		text_content_g2d.setColor(Color.WHITE);
		text_content_g2d.fillRect(0, 0, poem_content_image.getWidth(), poem_content_image.getHeight());
		get_poem_layout(text_content_g2d, text_content_g2d.getFontRenderContext());
		
		Rectangle2D poem_header_bounds = poem_content_in_viewer.getPoemHeader().getRawPixelBounds();
		Rectangle2D poem_stanzas_bounds = poem_content_in_viewer.getPoemStanzas().getRawPixelBounds();
		int end_x = (int)Math.ceil(Math.max(poem_header_bounds.getX() + poem_header_bounds.getWidth(), poem_stanzas_bounds.getX() + poem_stanzas_bounds.getWidth()) + left_margin_space);
		int end_y = (int)Math.ceil(poem_stanzas_bounds.getY() + poem_stanzas_bounds.getHeight() + top_margin_space);
		poem_content_image = poem_content_image.getSubimage(0, 0, end_x, end_y);
		text_content_g2d.dispose();
		
		display_response_word_pairs(poem_content_image.createGraphics());
		
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
				String panel_header_text = tile_type.contains("_") ? tile_type.replace("_", " ") : tile_type;
				panel_header_text = panel_header_text.substring(0, 1).toUpperCase() + panel_header_text.substring(1);
				
				TextLayout panel_header = new TextLayout(panel_header_text, big_text_font, g2d.getFontRenderContext());
				int image_loc_in_panel = (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space + line_break_space);
				
				g2d.setColor(word_pair_colors[response_type_id + 10]);
				System.out.println(response_type_id + " " + (response_type_id + 10) + word_pair_colors[response_type_id + 10]);
				g2d.fillRect(left_margin_space + (int)Math.floor(panel_header.getBounds().getX()), (int) Math.ceil(top_margin_space + panel_header.getBounds().getY() - 5), getWidth() - (left_margin_space + (int)Math.floor(panel_header.getBounds().getX())), (int) Math.ceil(panel_header.getBounds().getHeight() + panel_header.getBounds().getY() + top_margin_space));
				
				g2d.setColor(Color.BLACK);
				panel_header.draw(g2d, left_margin_space, top_margin_space);
				
				if(switch_to_distant_reading_icon != null){
					
					Rectangle2D panel_header_bounds = panel_header.getBounds();
					g2d.drawImage(switch_to_distant_reading_icon, (int) Math.round(panel_header_bounds.getX() + panel_header_bounds.getWidth()) +  2 * left_margin_space, (int) Math.round(panel_header_bounds.getY() + top_margin_space), null);
					
					distantReadingIconBounds = new Rectangle2D.Double(Math.round(panel_header_bounds.getX() + panel_header_bounds.getWidth()) +  2 * left_margin_space, Math.round(panel_header_bounds.getY() + top_margin_space), switch_to_distant_reading_icon.getWidth(), switch_to_distant_reading_icon.getHeight());
					
				}
				
				poem_content_image_start_x = left_margin_space;
				poem_content_image_start_y = image_loc_in_panel;
				//g2d.drawImage(poem_content_image, (getWidth() - poem_content_image.getWidth()) / 2, image_loc_in_panel, null);
				BufferedImage panel_content = poem_content_image;
				if(word_on_hover_current != null){
					
					if(poem_content_image_on_hover != null){
						
						panel_content = poem_content_image_on_hover;
						
					}
					
				}
				
				g2d.drawImage(panel_content, poem_content_image_start_x, poem_content_image_start_y, null);
				
				if(tile_disabled){
					
					g2d.setColor(new Color(255, 255, 255, 100));
					g2d.drawRect(0, 0, getWidth(), getHeight());
					
				}
				
			}
			
		};
		
		viewer_panel.setPreferredSize(new Dimension(poem_content_image.getWidth() + 10, poem_content_image.getHeight() + top_margin_space + 2 * line_break_space));
		viewer_scroll_pane = new JScrollPane(viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		viewer_panel.addMouseMotionListener(new MouseAdapter() {
        	
        	public void mouseMoved(MouseEvent me){

        		double poem_content_image_end_x = poem_content_image_start_x + poem_content_image.getWidth();
        		double poem_content_image_end_y = poem_content_image_start_y + poem_content_image.getHeight();
        		
        		Point2D point_on_viewer = me.getPoint();
        		
        		if((point_on_viewer.getX() >= poem_content_image_start_x && point_on_viewer.getX() <= poem_content_image_end_x) && (point_on_viewer.getY() >= poem_content_image_start_y && point_on_viewer.getY() <= poem_content_image_end_y)){

        			Point2D point_on_poem_image = new Point2D.Double(point_on_viewer.getX() - poem_content_image_start_x, point_on_viewer.getY() - poem_content_image_start_y);
        			String word_on_hover = get_word_on_hover(point_on_poem_image);
        			if(word_on_hover != null){

        				word_on_hover_current = word_on_hover;
        				
        				if(!word_on_hover.equals(word_on_hover_previous)){

        					word_on_hover_previous = word_on_hover;
        					get_poem_content_image_on_hover(word_on_hover);
        					viewer_panel.repaint();
        					
        				}
        				
        			}
        			else{
        				
        				word_on_hover_current = null;
        				
        				if(word_on_hover_previous != null){
        					
        					word_on_hover_previous = null;
        					
        				}
        				
        				viewer_panel.repaint();
        				
        			}
        			
        		}
        	}
		});
		
		viewer_panel.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent me){

        		double poem_content_image_end_x = poem_content_image_start_x + poem_content_image.getWidth();
        		double poem_content_image_end_y = poem_content_image_start_y + poem_content_image.getHeight();
        		
        		Point2D point_on_viewer = me.getPoint();
        		
        		if(distantReadingIconBounds.contains(point_on_viewer)){
        			
        			RunInterface.switchCloseAndDistant("distantReading");
        			
        		}
        		
        		if((point_on_viewer.getX() >= poem_content_image_start_x && point_on_viewer.getX() <= poem_content_image_end_x) && (point_on_viewer.getY() >= poem_content_image_start_y && point_on_viewer.getY() <= poem_content_image_end_y)){

        			Point2D point_on_poem_image = new Point2D.Double(point_on_viewer.getX() - poem_content_image_start_x, point_on_viewer.getY() - poem_content_image_start_y);
        		
        			boolean phoneme_widget_on_click = false;
        			for(Entry<Rectangle2D, String> phoneme_widget_entry : phoneme_widget_bounds_to_phoneme_map.entrySet()){
        				
        				Rectangle2D phoneme_widget_bounds = phoneme_widget_entry.getKey();
        				
        				if(phoneme_widget_bounds.contains(point_on_poem_image)){
        				
        					//System.out.println("found phoneme widget");
        					phoneme_widget_on_click = true;
        					phoneme_on_click_current = phoneme_widget_entry.getValue();
        					break;
        					
        				}
        				
        			}
        			
        			if(phoneme_widget_on_click){
        				
        				if(phoneme_on_click_previous != null){
        					
        					if(!phoneme_on_click_current.equals(phoneme_on_click_previous)){
            					
            					Color phoneme_widget_color = word_pair_colors[phoneme_to_color_map.get(phoneme_on_click_previous)];
            					List<Rectangle2D> phoneme_widgets_to_highlight = phoneme_to_phoneme_widgets_map.get(phoneme_on_click_previous);
            					for(int i = 0; i < phoneme_widgets_to_highlight.size(); i++){
            						
            						Rectangle2D phoneme_widget_bounds = phoneme_widgets_to_highlight.get(i);
            						repaint_phoneme_widgets(phoneme_widget_bounds, phoneme_widget_color);
            						
            					}
            					List<String> words_to_highlight = phoneme_to_words_map.get(phoneme_on_click_previous);
            					for(int i = 0; i < words_to_highlight.size(); i++){
            						
            						String word_to_highlight = words_to_highlight.get(i);
            						repaint_word(word_to_highlight, word_pair_colors[0]);
            						
            					}
            				
            					phoneme_widget_color = word_pair_colors[phoneme_to_color_map.get(phoneme_on_click_current)];
            					phoneme_widgets_to_highlight = phoneme_to_phoneme_widgets_map.get(phoneme_on_click_current);
            					for(int i = 0; i < phoneme_widgets_to_highlight.size(); i++){
            						
            						Rectangle2D phoneme_widget_bounds = phoneme_widgets_to_highlight.get(i);
            						draw_selected_phoneme_widget(phoneme_widget_bounds, phoneme_widget_color);
            						
            					}
            					words_to_highlight = phoneme_to_words_map.get(phoneme_on_click_current);
            					for(int i = 0; i < words_to_highlight.size(); i++){
            						
            						String word_to_highlight = words_to_highlight.get(i);
            						repaint_word(word_to_highlight, phoneme_widget_color);
            						
            					}

            					phoneme_on_click_previous = phoneme_on_click_current;
            					viewer_panel.repaint();
            					
            				}
        					
        				}
        				else{
        					
        					Color phoneme_widget_color = word_pair_colors[phoneme_to_color_map.get(phoneme_on_click_current)];
        					List<Rectangle2D> phoneme_widgets_to_highlight = phoneme_to_phoneme_widgets_map.get(phoneme_on_click_current);
        					for(int i = 0; i < phoneme_widgets_to_highlight.size(); i++){
        						
        						Rectangle2D phoneme_widget_bounds = phoneme_widgets_to_highlight.get(i);
        						draw_selected_phoneme_widget(phoneme_widget_bounds, phoneme_widget_color);
        						
        					}
        					List<String> words_to_highlight = phoneme_to_words_map.get(phoneme_on_click_current);
        					for(int i = 0; i < words_to_highlight.size(); i++){
        						
        						String word_to_highlight = words_to_highlight.get(i);
        						repaint_word(word_to_highlight, phoneme_widget_color);
        						
        					}

        					phoneme_on_click_previous = phoneme_on_click_current;
        					viewer_panel.repaint();
        					
        				}
        				
        			}
        			else{
        				
        				phoneme_on_click_current = null;
        				
        				if(phoneme_on_click_previous != null){
        					
        					Color phoneme_widget_color = word_pair_colors[phoneme_to_color_map.get(phoneme_on_click_previous)];
        					List<Rectangle2D> phoneme_widgets_to_highlight = phoneme_to_phoneme_widgets_map.get(phoneme_on_click_previous);
        					for(int i = 0; i < phoneme_widgets_to_highlight.size(); i++){
        						
        						Rectangle2D phoneme_widget_bounds = phoneme_widgets_to_highlight.get(i);
        						//TODO call to repaint phoneme widget wihtout highlights
        						repaint_phoneme_widgets(phoneme_widget_bounds, phoneme_widget_color);
        						
        					}
        					
        					List<String> words_to_highlight = phoneme_to_words_map.get(phoneme_on_click_previous);
        					for(int i = 0; i < words_to_highlight.size(); i++){
        						
        						String word_to_highlight = words_to_highlight.get(i);
        						repaint_word(word_to_highlight, word_pair_colors[0]);
        						
        					}
        					
        					phoneme_on_click_previous = null;
        					viewer_panel.repaint();
        					
        				}
        				
        			}
        			
        		}
        	}
        	
		});
		
	}
	
	private void get_poem_content_image_on_hover(String word){

		poem_content_image_on_hover = null;
		
		BufferedImage tooltip = get_word_pronunciation_tooltip(word);
		if(tooltip != null){
		
			String[] word_split = word.split("\\|");
			int stanza_index = Integer.parseInt(word_split[0]);
			int line_index = Integer.parseInt(word_split[1]);
			int word_index = Integer.parseInt(word_split[2]);
			
			poem_content_image_on_hover = new BufferedImage(poem_content_image.getWidth(), poem_content_image.getHeight(), poem_content_image.getType());
			Graphics2D g2d = poem_content_image_on_hover.createGraphics();
			g2d.drawImage(poem_content_image, 0, 0, null);
			
			Line line_details = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index);
			Word word_details = line_details.getWords().get(word_index);
			Rectangle2D word_bounds = word_details.getRawPixelBounds();
			
			int space_between_word_and_tooltip = 5;
			int tooltip_start_x = (int)Math.ceil(word_bounds.getX() + (word_bounds.getWidth() / 2) - (tooltip.getWidth() / 2));
			int tooltip_start_y = (int)Math.ceil(line_details.getRawPixelBounds().getY() - tooltip.getHeight() - space_between_word_and_tooltip);
			if(tooltip_start_x < 0){
				
				tooltip_start_x = 0;
				
			}
			g2d.drawImage(tooltip, tooltip_start_x, tooltip_start_y, null);
			
		}

		
	}
	
	private BufferedImage get_word_pronunciation_tooltip(String word){

		BufferedImage tooltip = null;
		
		String[] word_split = word.split("\\|");
		int stanza_index = Integer.parseInt(word_split[0]);
		int line_index = Integer.parseInt(word_split[1]);
		int word_index = Integer.parseInt(word_split[2]);
		
		Word word_details = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
		
		List<String> phonemes_to_highlight = new ArrayList<String>();
		if(word_to_phonemes_map != null){
		
			phonemes_to_highlight = word_to_phonemes_map.get(stanza_index+ "|" + line_index + "|" + word_index + "|" + word_split[3].trim().toLowerCase());

		}
		
		List<String> pronunciations = word_details.getPronunciations();
		if(pronunciations != null){
			
			if(!pronunciations.isEmpty()){

				tooltip = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = tooltip.createGraphics();
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, tooltip.getWidth(), tooltip.getHeight());
				
				Font tooltip_text_font = new Font("Palatino Linotype", Font.BOLD, 12);
		
				double min_y = Double.POSITIVE_INFINITY, max_y = Double.NEGATIVE_INFINITY;
				double min_x = Double.POSITIVE_INFINITY, max_x = Double.NEGATIVE_INFINITY;
				
				int default_top_offset = 20, top_offset = default_top_offset;
				int default_left_offset = 10;
				int space_between_lines = 5;
				
				Rectangle2D tooltip_content_bounds;
				
				for(int i = 0; i < pronunciations.size(); i++){
					
					String pronunciation = pronunciations.get(i);
					
					Rectangle2D pronunciation_bounds = display_word_pronunciation(g2d, default_left_offset, top_offset, tooltip_text_font, pronunciation, phonemes_to_highlight);
				
					min_x = Math.min(min_x, pronunciation_bounds.getX());
					max_x = Math.max(max_x, pronunciation_bounds.getX() + pronunciation_bounds.getWidth());
					
					if(i == 0){
						
						min_y = pronunciation_bounds.getY();
						max_y = pronunciation_bounds.getY()  + pronunciation_bounds.getHeight();
						
					}
					
					if(i == pronunciations.size() - 1){
						
						max_y = pronunciation_bounds.getY() + pronunciation_bounds.getHeight();
						
					}
					
					top_offset += space_between_lines + pronunciation_bounds.getY() + pronunciation_bounds.getHeight();
					
				}
				
				tooltip_content_bounds = new Rectangle2D.Double(min_x, min_y, max_x - min_x, max_y - min_y); 

				int tooltip_width = (int)Math.ceil(tooltip_content_bounds.getX() + tooltip_content_bounds.getWidth() + default_left_offset);
				int tooltip_height = (int)Math.ceil(tooltip_content_bounds.getHeight() + default_top_offset);
				
				g2d.setColor(default_text_color);
				g2d.drawRect(0, 0, tooltip_width - 2, tooltip_height - 2);
				
				tooltip = tooltip.getSubimage(0, 0, tooltip_width, tooltip_height);
				
				
			}
			
		}
		
		return tooltip;
		
	}
	
	private Rectangle2D display_word_pronunciation(Graphics2D g2d, int default_left_offset, int top_offset, Font text_font, String pronunciation, List<String> phonemes_to_highlight){

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontRenderContext frc = g2d.getFontRenderContext();
		
		int left_offset = default_left_offset;
		double min_y = Double.POSITIVE_INFINITY, max_y = Double.NEGATIVE_INFINITY;
		double min_x = Double.POSITIVE_INFINITY, max_x = Double.NEGATIVE_INFINITY;
		
		Color default_color = default_text_color.darker();
		//Color highlight_color = Color.red.darker();
		
		boolean has_phonemes_to_highlight = (phonemes_to_highlight != null);
		
		List<List<String>> phonemes = new ArrayList<List<String>>();
		String[] syllables = pronunciation.split("\\)");
		for(int i = 0; i < syllables.length; i++){
			
			String syllable = syllables[i];
			syllable = syllable.substring(1, syllable.length());
			
			List<String> phonemes_in_syllable = new ArrayList<String>();
			
			String[] syllable_split = syllable.split("\\.");
			for(int j = 0; j < syllable_split.length; j++){
				
				phonemes_in_syllable.add(syllable_split[j]);
				
			}
			
			phonemes.add(phonemes_in_syllable);
			
		}
		
		for(int j = 0; j < phonemes.size(); j++){
			
			List<String> phonemes_in_syllable = phonemes.get(j);
			for(int i = 0; i < phonemes_in_syllable.size(); i++){
				
				String phoneme = phonemes_in_syllable.get(i);
			
				g2d.setColor(default_color);
				if(has_phonemes_to_highlight){
					
					String phoneme_to_check = phoneme.trim();
					if(phoneme.matches("[A-Z].*[0-2]")){
						
						phoneme_to_check = phoneme_to_check.substring(0, phoneme_to_check.length() - 1);
						
					}
					
					if(phonemes_to_highlight.contains(phoneme_to_check) && phonemes_in_response.contains(phoneme_to_check)){
						
						Color phoneme_color = word_pair_colors[phoneme_to_color_map.get(phoneme_to_check)];
						phoneme_color = new Color(phoneme_color.getRed(), phoneme_color.getGreen(), phoneme_color.getBlue());
						g2d.setColor(phoneme_color);
						//g2d.fillRect((int)(phoneme_min_x), (int)(phoneme_min_y), (int)Math.ceil(phoneme_bounds.getWidth()), (int)Math.ceil(phoneme_bounds.getHeight()));

					}
					
				}
				
				if(i !=  0){
					
					phoneme = " " + phoneme;
					
				}
				if(j != 0 && i == 0){
					
					phoneme = "   " + phoneme;
					
				}
				
				TextLayout phoneme_layout = new TextLayout(phoneme, text_font, frc);
				phoneme_layout.draw(g2d, left_offset, top_offset);
				
				Rectangle2D phoneme_bounds = phoneme_layout.getBounds();
				double phoneme_min_x = left_offset + phoneme_bounds.getX(), phoneme_max_x = left_offset + phoneme_bounds.getX() + phoneme_bounds.getWidth();
				double phoneme_min_y  = top_offset + phoneme_bounds.getY(), phoneme_max_y = top_offset + phoneme_bounds.getY() + phoneme_bounds.getHeight();
				
				if(i == 0 && j == 0){
					
					min_x = phoneme_min_x;
					max_x = phoneme_max_x;
					
					min_y = phoneme_min_y;
					max_y = phoneme_max_y;
					
				}
				else{
					
					if(j == phonemes.size() - 1 && i == phonemes_in_syllable.size() - 1){
						
						max_x = phoneme_max_x;
						
					}
					
					min_y = Math.min(min_y, phoneme_min_y);
					max_y = Math.max(max_y, phoneme_max_y);
					
				}
			
				left_offset = (int)Math.ceil(phoneme_max_x);
				
			}
			
		}
		
		return new Rectangle2D.Double(min_x, min_y, max_x - min_x, max_y - min_y);
		
	}
	
	private String get_word_on_hover(Point2D point_on_viewer){
		
		String is_hover_on_word = null;
		
		if(is_hover_on_word == null){
			
			List<Stanza> poem_stanzas = poem_content_in_viewer.getPoemStanzas().getStanzas();
			for(int i = 0; i < poem_stanzas.size(); i++){
				
				is_hover_on_word = hover_on_line(point_on_viewer, poem_stanzas.get(i));
				if(is_hover_on_word != null){
					
					is_hover_on_word = i + "|" + is_hover_on_word;
					break;
					
				}
				
			}
			
		}
		
		return is_hover_on_word;
		
	}
	
	private String hover_on_line(Point2D point_on_viewer, Stanza poem_stanza){
		
		String is_hover_on_word = null;
		
		if(poem_stanza.getRawPixelBounds().contains(point_on_viewer)){
			
			List<Line> stanza_lines = poem_stanza.getLines();
			for(int i = 0; i < stanza_lines.size(); i++){
				
				String line_word = hover_on_word(point_on_viewer, stanza_lines.get(i));
				if(line_word != null){
					
					is_hover_on_word = i + "|" + line_word;
					break;
					
				}
				
			}
			
		}
		
		return is_hover_on_word;
	}
	
	private String hover_on_word(Point2D point_on_viewer, Line stanza_line){
		
		String is_hover_on_word = null;
		if(stanza_line.getRawPixelBounds().contains(point_on_viewer)){
			
			List<Word> line_words = stanza_line.getWords();
			for(int i = 0; i < line_words.size(); i++){
				
				Word line_word = line_words.get(i);
				if(line_word.getRawPixelBounds().contains(point_on_viewer)){
					
					is_hover_on_word = i + "|" + line_word.getWord();
					break;
				}
				
			}
			
		}
		
		return is_hover_on_word;
		
	}
	
	private void initialize_pen_stroke_words(){
		
		List<String> all_pen_stroke_words = new ArrayList<String>();
		
		String[] query_content_pen_stroke_split = query_content.split("#");
		for(int i = 0; i < query_content_pen_stroke_split.length; i++){
			
			String[] pen_stroke_words = query_content_pen_stroke_split[i].substring(1, query_content_pen_stroke_split[i].length() - 1).split(" ");
			for(int j = 0; j < pen_stroke_words.length; j++){
				
				String pen_stroke_word = pen_stroke_words[j].substring(1, pen_stroke_words[j].length() - 1);
				all_pen_stroke_words.add(pen_stroke_word);
				
			}
			
		}
		
		pen_stroke_words = all_pen_stroke_words;
		
	}
	
	//TODO check to see if right coloring happens add just the word to the map and not full location wise
	private Integer get_color_for_word(String word_key, List<String> word_values){
		
		System.out.println("****************Here for " + word_key);
		
		List<String> words = new ArrayList<String>();
		words.add(word_key.split("\\|")[3]);
		for(int i = 0; i < word_values.size(); i++){
			
			words.add(word_values.get(i).split("\\|")[3]);
			
		}
		//TODO commented this -- uncomment if problems
		//words.addAll(word_values);
		
		Integer color_index = 0;
		boolean word_found = false;
		
		if(!color_to_word_map.isEmpty()){
			
			for(Map.Entry<Integer, List<String>> color_entry : color_to_word_map.entrySet()){
			    
				color_index = color_entry.getKey();
				List<String> words_mapped = color_entry.getValue();
				
				for(int i = 0; i < words.size(); i++){
					
					String word = words.get(i);
					System.out.println("words to test: " + word);
					for(int j = 0; j < words_mapped.size(); j++){
						
						System.out.println("mapped_words: " + words_mapped.get(j));
						if(word.equals(words_mapped.get(j))){
							
							word_found = true;
							break;
							
						}
						
					}
					
					if(word_found){
						
						break;
						
					}
					
				}
				
				if(word_found){
					
					break;
					
				}
				
			}
			
		}
		
		//System.out.println(word_found);
		
		if(word_found){
			
			List<String> previously_mapped_values = color_to_word_map.get(color_index);
			for(int i = 0; i < words.size(); i++){
				
				String word = words.get(i);
				if(!previously_mapped_values.contains(word)){
					
					previously_mapped_values.add(word);
					
				}
				
			}
			
			color_to_word_map.put(color_index, previously_mapped_values);
			
		}
		else{
			
			if(!color_to_word_map.isEmpty()){
				
				color_index += 1;
				
				
			}

			color_to_word_map.put(color_index, words);
			
		}
		
		//System.out.println(color_index);
		
		return color_index;
		
	}
	
	private void display_response_word_pairs(Graphics2D g2d){

		if(tile_type.equals("word_repetititons")){
			
			response_type_id = 0;
			
	        for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				Color word_set_color = word_pair_colors[get_color_for_word(word_key, word_values)];
				display_word_pairs_color_only(word_key, word_values, word_set_color);
				
			}
			
		}
		else if(tile_type.equals("perfect_rhyme")){
			
			response_type_id = 4;
			/*for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				Color word_set_color = word_pair_colors[get_color_for_word(word_key, word_values)];
				display_word_pairs_color_only(word_key, word_values, word_set_color);
				
			}*/
			
			List<Color> word_set_colors = new ArrayList<Color>();

			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				
				Color word_set_color = word_pair_colors[get_color_for_word(word_key, word_values)];
				word_set_colors.add(word_set_color);
				display_arcs_between_word_pairs(word_key, word_values, word_set_color);
				
			}
			
			int word_entry_index = 0;
			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				display_word_pairs_color_only(word_key, word_values, word_set_colors.get(word_entry_index));
				
				word_entry_index++;
				
			}
			
		}
		else if(tile_type.equals("synonyms")){
			
			response_type_id = 5;
			
			List<Color> word_set_colors = new ArrayList<Color>();

			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				
				Color word_set_color = word_pair_colors[get_color_for_word(word_key, word_values)];
				word_set_colors.add(word_set_color);
				display_arcs_between_word_pairs(word_key, word_values, word_set_color);
				
			}
			
			int word_entry_index = 0;
			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				display_word_pairs_color_only(word_key, word_values, word_set_colors.get(word_entry_index));
				
				word_entry_index++;
				
			}
			
		}
		else if(tile_type.equals("antonyms")){
			
			response_type_id = 6;
			
			List<Color> word_set_colors = new ArrayList<Color>();

			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				
				Color word_set_color = word_pair_colors[0];
				word_set_colors.add(word_set_color);
				display_arcs_between_word_pairs(word_key, word_values, word_set_color);
				
			}
			
			int word_entry_index = 0;
			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				display_word_pairs_color_only(word_key, word_values, word_set_colors.get(word_entry_index));
				
				word_entry_index++;
				
			}
			
			
		}
		else if(tile_type.equals("assonance") || tile_type.equals("consonance") || tile_type.equals("alliteration")){

			for(Map.Entry<String, List<String>> word_entry : tile_content.entrySet()){
			    
				String word_key = word_entry.getKey();
				List<String> word_values = word_entry.getValue();
				//Color word_set_color = word_pair_colors[get_color_for_word(word_key, word_values)];
				Color word_set_color = word_pair_colors[0];
				display_word_pairs_color_only(word_key, word_values, word_set_color);
				//TODO call to display phoneme widget
				display_phoneme_widgets_for_word(word_key, word_to_phonemes_map.get(word_key));
				for(int i = 0; i < word_values.size(); i++){
					
					String word_value = word_values.get(i);
					display_phoneme_widgets_for_word(word_value, word_to_phonemes_map.get(word_value));
					
				}
				
			}
			
			if(tile_type.equals("assonance")){
				
				response_type_id = 2;
				
			}
			else if(tile_type.equals("consonance")){
				
				response_type_id = 3;
				
			}
			else{
				
				response_type_id = 1;
				
			}
			
		}
		
	}
	
	
	
	private void display_arcs_between_word_pairs(String word_key, List<String> word_values, Color word_set_color){

		Color color_to_use = new Color(word_set_color.getRed(), word_set_color.getGreen(), word_set_color.getBlue());
		
		Graphics2D g2d = (Graphics2D) poem_content_image.getGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Stroke default_stroke = g2d.getStroke();
		double width = 2.0;
		Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		g2d.setStroke(new_stroke);
		
		String[] word_key_split = word_key.split("\\|");
		int word_key_stanza_index = Integer.parseInt(word_key_split[0]);
		int word_key_line_index = Integer.parseInt(word_key_split[1]);
		int word_key_word_index = Integer.parseInt(word_key_split[2]);
		
		Rectangle2D word_key_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(word_key_stanza_index).getLines().get(word_key_line_index).getWords().get(word_key_word_index).getRawPixelBounds();
		
		for(int i = 0; i < word_values.size(); i++){
			
			String word_value = word_values.get(i);
			String[] word_value_split = word_value.split("\\|");
			int word_value_stanza_index = Integer.parseInt(word_value_split[0]);
			int word_value_line_index = Integer.parseInt(word_value_split[1]);
			int word_value_word_index = Integer.parseInt(word_value_split[2]);
			
			Rectangle2D word_value_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(word_value_stanza_index).getLines().get(word_value_line_index).getWords().get(word_value_word_index).getRawPixelBounds();
			
			g2d.setColor(color_to_use);
			
			double p1_x = word_key_bounds.getCenterX(), p2_x = word_value_bounds.getCenterX();
			double p1_y = word_key_bounds.getCenterY(), p2_y = word_value_bounds.getCenterY();
			
			if(p1_y - p2_y == 0 || word_key_line_index == word_value_line_index){
				
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
				
				g2d.drawArc((int)top_left_x, (int) ((int)p1_y - height / 2), (int)Math.abs(p1_x - p2_x), (int)height, 0, 180);
				
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
				
				g2d.drawArc((int)(p1_x + height/ 2), (int) top_left_y, (int)height,  (int)Math.abs(p1_y - p2_y), 0, 180);
				
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

				g2d.draw(new Line2D.Double(p1_x, p1_y, p2_x, p2_y));
			}
		
		}
		g2d.setStroke(default_stroke);
		g2d.dispose();
		
	}
	
	//TODO display phoneme widgets
	private void display_phoneme_widgets_for_word(String word_details, List<String> phonemes_as_widgets){
		
		double space_between_word_and_widgets = 7;
		double space_between_widgets = 5;
		double phoneme_widget_height = 10;
		double phoneme_widget_width = 10;
		
		Graphics2D g2d = poem_content_image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		String[] word_details_split = word_details.split("\\|");
		int stanza_index = Integer.parseInt(word_details_split[0]);
		int line_index = Integer.parseInt(word_details_split[1]);
		int word_index = Integer.parseInt(word_details_split[2]);
		Word word = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
		Rectangle2D word_bounds = word.getRawPixelBounds();
		Rectangle2D line_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getRawPixelBounds();
		
		double phoneme_widgets_start_x = word_bounds.getX();
		//double phoneme_widgets_start_y = word_bounds.getY() + word_bounds.getHeight() + space_between_word_and_widgets;
		double phoneme_widgets_start_y = line_bounds.getY() + line_bounds.getHeight() + space_between_word_and_widgets;
		for(int i = 0; i < phonemes_as_widgets.size(); i++){
			
			String phoneme = phonemes_as_widgets.get(i);
			if(phonemes_in_response.contains(phoneme)){
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect((int)Math.floor(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets - 1), (int)Math.floor(phoneme_widgets_start_y - 1), (int)Math.ceil(phoneme_widget_width + 2), (int)Math.ceil(phoneme_widget_height + 2));
				
				Color phoneme_widget_color = word_pair_colors[phoneme_to_color_map.get(phoneme)];
				g2d.setColor(phoneme_widget_color);
				g2d.fillOval((int)Math.ceil(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets), (int)Math.ceil(phoneme_widgets_start_y), (int)Math.ceil(phoneme_widget_width), (int)Math.ceil(phoneme_widget_height));
				
				phoneme_widget_bounds_to_phoneme_map.put(new Rectangle2D.Double((int)Math.ceil(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets), (int)Math.ceil(phoneme_widgets_start_y), (int)Math.ceil(phoneme_widget_width), (int)Math.ceil(phoneme_widget_height)), phoneme);
				
				List<Rectangle2D> previous_phoneme_widgets = phoneme_to_phoneme_widgets_map.get(phoneme);
				if(previous_phoneme_widgets == null){
					
					previous_phoneme_widgets = new ArrayList<Rectangle2D>();
					
				}
				
				previous_phoneme_widgets.add(new Rectangle2D.Double((int)Math.ceil(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets), (int)Math.ceil(phoneme_widgets_start_y), (int)Math.ceil(phoneme_widget_width), (int)Math.ceil(phoneme_widget_height)));
				phoneme_to_phoneme_widgets_map.put(phoneme, previous_phoneme_widgets);
				
			}
			
		}
		
		g2d.dispose();
	
	}
	
	//TODO Repaint phoneme widget on click release
	private void repaint_phoneme_widgets(Rectangle2D phoneme_widget_bounds, Color phoneme_widget_color){
		
		Graphics2D g2d = poem_content_image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int)phoneme_widget_bounds.getX() - 1, (int)phoneme_widget_bounds.getY() - 1, (int)phoneme_widget_bounds.getWidth() + 2, (int)phoneme_widget_bounds.getHeight() + 2);
		
		g2d.setColor(phoneme_widget_color);
		g2d.fillOval((int)phoneme_widget_bounds.getX(), (int)phoneme_widget_bounds.getY(), (int)phoneme_widget_bounds.getWidth(), (int)phoneme_widget_bounds.getHeight());
		
		g2d.dispose();
		
	}
	
	//TODO Draw phoneme widget on selection
	private void draw_selected_phoneme_widget(Rectangle2D phoneme_widget_bounds, Color phoneme_widget_color){
		
		Graphics2D g2d = poem_content_image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.WHITE);
		g2d.drawOval((int)phoneme_widget_bounds.getX(), (int)phoneme_widget_bounds.getY(), (int)phoneme_widget_bounds.getWidth(), (int)phoneme_widget_bounds.getHeight());
		
		g2d.setColor(phoneme_widget_color);
		g2d.drawOval((int)phoneme_widget_bounds.getX(), (int)phoneme_widget_bounds.getY(), (int)phoneme_widget_bounds.getWidth(), (int)phoneme_widget_bounds.getHeight());
		
		g2d.setColor(new Color(phoneme_widget_color.getRed(), phoneme_widget_color.getGreen(), phoneme_widget_color.getBlue()).darker());
		g2d.drawOval((int)phoneme_widget_bounds.getX(), (int)phoneme_widget_bounds.getY(), (int)phoneme_widget_bounds.getWidth(), (int)phoneme_widget_bounds.getHeight());
		
		g2d.dispose();
		
	}
	
	private void display_word_pairs_color_only(String word_key, List<String> word_values, Color word_set_color){

		Color color_to_use = new Color(word_set_color.getRed(), word_set_color.getGreen(), word_set_color.getBlue(), word_set_color.getAlpha() + 50);
		
		repaint_word(word_key, color_to_use);
		
		for(int i = 0; i < word_values.size(); i++){
			
			String word_value = word_values.get(i);
			repaint_word(word_value, color_to_use);
			
		}
		
	}
	
	private boolean is_pen_stroke_word(String word_to_test){
		
		boolean is_pen_stroke_word = false;
		
		for(int i = 0; i < pen_stroke_words.size(); i++){
			
			if(pen_stroke_words.get(i).equals(word_to_test)){
				
				is_pen_stroke_word = true;
				break;
				
			}
			
		}
		
		return is_pen_stroke_word;
		
	}
	
	private void repaint_word(String word_to_repaint, Color bounding_box_color_to_use){
		
		Graphics2D text_content_g2d = (Graphics2D) poem_content_image.getGraphics();
		
		text_content_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Stroke default_stroke = text_content_g2d.getStroke();
		double width = 1.5;
		Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
		
		String[] word_to_repaint_info = word_to_repaint.split("\\|");
		int stanza_index = Integer.parseInt(word_to_repaint_info[0]);
		int line_index = Integer.parseInt(word_to_repaint_info[1]);
		int word_index = Integer.parseInt(word_to_repaint_info[2]);
		
		Word query_word = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
		Rectangle2D word_bounds = query_word.getRawPixelBounds();
		List<String> characters_in_word = query_word.getCharacters();
		List<Point2D> character_locations = query_word.getCharactersTextLayoutLocations();
		
		Rectangle2D line_bounds = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getRawPixelBounds();
		
		double word_start_y = line_bounds.getY();
		double word_end_y = line_bounds.getY() + line_bounds.getHeight();
		
		int extra_padding = 3;
		text_content_g2d.setColor(Color.WHITE);
		//text_content_g2d.fillRect((int) Math.floor(word_bounds.getX()) - extra_padding, (int) Math.floor(word_bounds.getY()) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_bounds.getHeight()) + 2 * extra_padding);
		text_content_g2d.fillRect((int) Math.floor(word_bounds.getX()) - extra_padding, (int) Math.floor(word_start_y) - extra_padding, (int) Math.ceil(word_bounds.getWidth()) + 2 * extra_padding, (int) Math.ceil(word_end_y - word_start_y) + 2 * extra_padding);
		
		text_content_g2d.setColor(bounding_box_color_to_use);
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
		
		Color text_color_to_use = text_highlighted_color;
		for(int j = 0; j < character_locations.size(); j++){
			
			text_content_g2d.setColor(text_color_to_use);
			TextLayout layout = new TextLayout(characters_in_word.get(j), text_font, text_content_g2d.getFontRenderContext());
			Point2D character_location = character_locations.get(j);
			layout.draw(text_content_g2d, (float)character_location.getX(), (float)character_location.getY());
			
		}
		
		text_content_g2d.dispose();
		
	}
	
	private static void initialize_text_attributes(){
		
		font_name = "Palatino Linotype";
		font_style = Font.PLAIN;
		font_size = 17;
		text_font = new Font(font_name, font_style, font_size);
		
		font_color_r = font_color_g = font_color_b = 170;
		font_color_a = 255;
		default_text_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		text_highlighted_color = new Color(100, 100, 100, font_color_a);
		//text_color_highlighted = new Color(50, 50, 50, font_color_a);
		//text_color_on_click = new Color(0, 104, 139, font_color_a);
		
		top_margin_space = 30;
		left_margin_space = 20;
		
		//line_break_space = 40;
		line_break_space = 30;
		//poem_header_break_space = stanza_break_space = line_break_space + 50;
		poem_header_break_space = stanza_break_space = line_break_space + 20;
		word_break_space = 5;
		
	}
	
	private void get_poem_layout(Graphics2D g2d, FontRenderContext fontRenderContext) {

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		initialize_text_attributes();
		
		int top_offset = top_margin_space;
		Poem worksheet_content = poem_content;
		
		Stanza poem_header = get_stanza_layout(g2d, fontRenderContext, worksheet_content.getPoemHeader(), top_offset, -1);
		top_offset += poem_header.getRawPixelBounds().getHeight() + poem_header_break_space;
		//g2d.draw(poem_header.getRawPixelBounds());
		
		List<Stanza> poem_stanzas_viewer = new ArrayList<Stanza>();
		
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();
		
		double poem_stanzas_min_x = Double.POSITIVE_INFINITY, poem_stanzas_max_x = Double.NEGATIVE_INFINITY;
		double poem_stanzas_min_y = Double.POSITIVE_INFINITY, poem_stanzas_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < poem_stanzas.size(); i++){

			Stanza poem_stanza = get_stanza_layout(g2d, fontRenderContext, poem_stanzas.get(i), top_offset, i);
		
			double poem_stanza_min_x = poem_stanza.getRawPixelBounds().getX();
			double poem_stanza_max_x = poem_stanza.getRawPixelBounds().getX() + poem_stanza.getRawPixelBounds().getWidth();
			
			if(i == 0){
				
				poem_stanzas_min_y = poem_stanza.getRawPixelBounds().getY();
				poem_stanzas_max_y = poem_stanza.getRawPixelBounds().getY() + poem_stanza.getRawPixelBounds().getHeight();
				
				poem_stanzas_min_x = poem_stanza_min_x;
				poem_stanzas_max_x = poem_stanza_max_x;
				
			}
			else{
				
				if(i == poem_stanzas.size() - 1){
					
					poem_stanzas_max_y = poem_stanza.getRawPixelBounds().getY() + poem_stanza.getRawPixelBounds().getHeight();
					
				}
				
				if(poem_stanza_min_x < poem_stanzas_min_x){
					
					poem_stanzas_min_x = poem_stanza_min_x;
					
				}
				
				if(poem_stanza_max_x > poem_stanzas_max_x){
					
					poem_stanzas_max_x = poem_stanza_max_x;
					
				}
				
			}
			
			top_offset += poem_stanza.getRawPixelBounds().getHeight() + stanza_break_space;
			
			poem_stanzas_viewer.add(poem_stanza);
			
		}
		
		Rectangle2D poem_stanzas_bounds = new Rectangle2D.Double(poem_stanzas_min_x, poem_stanzas_min_y, poem_stanzas_max_x - poem_stanzas_min_x, poem_stanzas_max_y - poem_stanzas_min_y);
		//g2d.draw(poem_stanzas_bounds);
		poem_content_in_viewer = new Poem(poem_header, new PoemStanzas(poem_stanzas_viewer, poem_stanzas_bounds));
		
		
	}
	
	private Stanza get_stanza_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Stanza poem_stanza, int top_offset, int stanza_index){
		
		List<Line> lines_in_stanza_viewer = new ArrayList<Line>();
		
		List<Line> lines_in_stanza = poem_stanza.getLines();
		double stanza_min_x = Double.POSITIVE_INFINITY, stanza_max_x = Double.NEGATIVE_INFINITY;
		double stanza_min_y = Double.POSITIVE_INFINITY, stanza_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			Line line_in_stanza = get_line_layout(g2d, fontRenderContext, lines_in_stanza.get(i), top_offset, stanza_index, i);
			
			double line_min_x = line_in_stanza.getRawPixelBounds().getX();
			double line_max_x = line_in_stanza.getRawPixelBounds().getWidth() + line_in_stanza.getRawPixelBounds().getX();
			
			if(i == 0){
				
				stanza_min_y = line_in_stanza.getRawPixelBounds().getY();
				
				stanza_min_x = line_min_x;
				stanza_max_x = line_max_x;
				
			}
			else{
				
				if(i == lines_in_stanza.size() - 1){
					
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
			
			lines_in_stanza_viewer.add(line_in_stanza);
			
		}
		
		Rectangle2D stanza_bounds = new Rectangle2D.Double(stanza_min_x, stanza_min_y, stanza_max_x - stanza_min_x, stanza_max_y - stanza_min_y);
		//g2d.draw(stanza_bounds);
		
		return new Stanza(lines_in_stanza_viewer, stanza_bounds);
		
	}

	private Line get_line_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Line poem_line, int top_offset, int stanza_index, int line_index){

		List<Word> words_in_line = poem_line.getWords();
		int character_left_offset = left_margin_space;
		
		double line_bounds_min_x = Double.POSITIVE_INFINITY, line_bounds_max_x = Double.NEGATIVE_INFINITY, line_bounds_min_y = Double.POSITIVE_INFINITY, line_bounds_max_y = Double.NEGATIVE_INFINITY;
		
		List<Word> words_in_line_viewer = new ArrayList<Word>();
		for(int i = 0; i < words_in_line.size(); i++){
			
			List<Rectangle2D> character_bounds_viewer = new ArrayList<Rectangle2D>();
			List<Point2D> character_locations_viewer = new ArrayList<Point2D>();
			
			Word word_in_line = words_in_line.get(i);
			List<String> characters_in_word = get_characters_in_word(word_in_line.getWord());
			
			Color text_color_to_use = default_text_color;
			if(is_pen_stroke_word(stanza_index + "|" + line_index + "|" + i + "|" + word_in_line.getWord().trim())){
				
				text_color_to_use = text_highlighted_color;
				
			}
			
			double word_bounds_min_x = Double.POSITIVE_INFINITY, word_bounds_max_x = Double.NEGATIVE_INFINITY, word_bounds_min_y = Double.POSITIVE_INFINITY, word_bounds_max_y = Double.NEGATIVE_INFINITY;
			
			for(int j = 0; j < characters_in_word.size(); j++){

				g2d.setColor(text_color_to_use);
				TextLayout layout = new TextLayout(characters_in_word.get(j), text_font, fontRenderContext);
				layout.draw(g2d, character_left_offset, top_offset);
				character_locations_viewer.add(new Point2D.Double(character_left_offset, top_offset));
				
				
				g2d.setColor(new Color(255, 0, 0, 230));
				Rectangle2D layout_bounds = layout.getBounds();
				Rectangle2D char_bounds = new Rectangle2D.Double(character_left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
				character_bounds_viewer.add(char_bounds);
				//g2d.draw(char_bounds);
				
				
				double character_min_y = top_offset + layout_bounds.getY();
				double character_max_y = top_offset + layout_bounds.getY() + layout_bounds.getHeight();
				
				if(j == 0){
					
					word_bounds_min_x = character_left_offset + layout_bounds.getX();
					word_bounds_max_x = character_left_offset + layout_bounds.getX() + layout_bounds.getWidth();
					
					word_bounds_min_y = character_min_y;
					word_bounds_max_y = character_max_y;
					
				}
				else{
					
					if(j == characters_in_word.size() - 1){
						
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
			character_left_offset += word_break_space;
			
			Word word_in_line_viewer = new Word(word_in_line.getWord(), word_bounds, character_bounds_viewer, character_locations_viewer);
			if(!word_definition_xml.isEmpty()){
				
				//System.out.println("can access definition xml");
				WordDefinition word_definition = word_definition_xml.get(word_in_line.getWord().toLowerCase().trim());
				if(word_definition != null){

					word_in_line_viewer.setPronunciations(word_definition.getPronunciations());
					
				}
				
			}
			words_in_line_viewer.add(word_in_line_viewer);
			
			if(i == 0){
				
				line_bounds_min_x = word_bounds_min_x;
				line_bounds_max_x = word_bounds_max_x;
				
				line_bounds_min_y = word_bounds_min_y;
				line_bounds_max_y = word_bounds_max_y;
				
			}
			else{
				
				if(i == words_in_line.size() - 1){
					
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
		
		return new Line(poem_line.getLine(), line_bounds, words_in_line_viewer);
		
	}
	
	private List<String> get_characters_in_word(String word){
		
		String extra_padding = "";
		List<String> characters_in_word = new ArrayList<String>();
		
		String word_trimmed = word.trim();
		int leading_space_in_word = word.length() - word_trimmed.length();

		for(int i = 0; i < word_trimmed.length(); i++){
			String char_in_word = (i == 0) ? (leading_space_in_word > 0 ? (get_leading_whitespace(leading_space_in_word) + String.valueOf(word_trimmed.charAt(i) + extra_padding)) : (String.valueOf(word_trimmed.charAt(i)) + extra_padding)) : (String.valueOf(word_trimmed.charAt(i) + extra_padding));
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
	
	public void setTileFilteredOut(boolean is_tile_filtered_out){
		
		if(tile_filtered_out != is_tile_filtered_out){
		
			tile_filtered_out = is_tile_filtered_out;
			
		}
		
	}
	
	public boolean getTileFilteredOut(){
		
		return tile_filtered_out;
		
	}

	public void setTileDisabled(boolean is_tile_disbaled){
		
		tile_disabled = is_tile_disbaled;
		
	}
	
	public boolean getTileDisabled(){
		
		return tile_disabled;
		
	}

}
