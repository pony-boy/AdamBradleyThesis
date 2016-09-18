package annotationInteraction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

public class Tile {

	public static int resize_factor = 6;
	
	public static int viewer_width = (int) Math.round(PenPoint.raw_pixel_max_x / resize_factor);
	public static int viewer_height = (int) Math.round(PenPoint.raw_pixel_max_y / resize_factor);
	
	private JPanel response_viewer_panel;
	private JScrollPane response_viewer_panel_scroll_pane;
	
	private Poem poem_content;
	private List<PenStroke> completed_pen_strokes;
	private Map<String, List<String>> response_content;
	private String response_type;
	
	private Map<String, Point2D> last_drawn_ellipse_for_word;
	
	private boolean tile_filtered_out = false;
	
	public Tile(Poem poem_content, List<PenStroke> completed_pen_strokes, Map<String, List<String>> response_content, String response_type, String query_id, String query_content){
		
		this.poem_content = poem_content;
		this.completed_pen_strokes = completed_pen_strokes;
		this.response_content = response_content;
		this.response_type = response_type;
		
		last_drawn_ellipse_for_word = new HashMap<String, Point2D>();
		
		create_response_viewer_panel();
		
	}
		
	private void create_response_viewer_panel(){
			
		response_viewer_panel = new JPanel(){
				
			@Override
			public void paintComponent(Graphics g){
					
				super.paintComponent(g);
					
				Graphics2D g2d = (Graphics2D) g;
				
				Stroke default_stroke = g2d.getStroke();
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, viewer_width, viewer_height);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				display_worksheet_content(g2d);
				
				double width = 1.5;
				Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
				Color stroke_color = new Color(70, 62, 237);
				g2d.setStroke(new_stroke);
				
				for(int i = 0; i < completed_pen_strokes.size(); i++){

					g2d.setColor(stroke_color);
					g2d.draw(completed_pen_strokes.get(i).getTileLinearStrokePath());
					//g2d.draw(completed_pen_strokes.get(i).getTileSplineStrokePath());
						
				}
				
				display_response_word_pairs(g2d);
				
				g2d.setStroke(default_stroke);
				/*
				if(tile_filtered_out){
					
					g2d.setColor(new Color(255, 255, 255, 200));
					g2d.fillRect(0, 0, viewer_width, viewer_height);
					
				}
				*/
			}
		};
		
		response_viewer_panel.setPreferredSize(new Dimension(viewer_width, viewer_height));
		TitledBorder panel_border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), response_type.substring(0, 1).toUpperCase() + response_type.substring(1));
		//panel_border.setTitleFont(new Font(panel_border.getTitleFont().getFontName(), Font.PLAIN, 12));
		panel_border.setTitleFont(new Font("Consolas", Font.PLAIN, 15));
		response_viewer_panel.setBorder(panel_border);
        response_viewer_panel_scroll_pane = new JScrollPane(response_viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
	}
	
	private void display_worksheet_content(Graphics2D g2d){

		//get_poem_layout(g2d, g2d.getFontRenderContext());
		
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();
		
		layout_stanza_content(g2d, fontRenderContext, poem_content.getPoemHeader());
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			layout_stanza_content(g2d, fontRenderContext, poem_stanzas.get(i));
			
		}
		

	}
	
	private void layout_stanza_content(Graphics2D g2d, FontRenderContext fontRenderContext, Stanza stanza_in_poem){
		
		List<Line> lines_in_stanza = stanza_in_poem.getLines();
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			layout_line_content(g2d, fontRenderContext, lines_in_stanza.get(i));
			
		}
		
	}
	
	private void layout_line_content(Graphics2D g2d, FontRenderContext fontRenderContext, Line line_in_stanza){
		
		Font text_font = CompositeGenerator.text_font;
		text_font = new Font(text_font.getFontName(), text_font.getStyle(), Math.round(text_font.getSize() / resize_factor));
		Color text_color = new Color(10, 10, 10, 250);
		
		List<Word> words_in_line = line_in_stanza.getWords();
		for(int i = 0 ; i < words_in_line.size(); i++){
			
			Word word_in_line = words_in_line.get(i);
			List<String> characters_in_word = word_in_line.getCharacters();
			//List<Rectangle2D> characters_in_word_bounds = word_in_line.getCharactersRawPixelBounds(); 
			List<Point2D> characters_in_word_locations = word_in_line.getCharactersTextLayoutLocations();
			
			for(int j = 0; j < characters_in_word.size(); j++){
				
				g2d.setColor(text_color);
				TextLayout layout = new TextLayout(characters_in_word.get(j), text_font, fontRenderContext);
				Point2D character_location = characters_in_word_locations.get(j);
				layout.draw(g2d, (float)character_location.getX(), (float)character_location.getY());

				//g2d.draw(characters_in_word_bounds.get(j));
				
			}
			
			//g2d.draw(word_in_line.getRawPixelBounds());
	
		}
		
	}
	
	private void display_response_word_pairs(Graphics2D g2d){
		
		last_drawn_ellipse_for_word = new HashMap<String, Point2D>();
		
		Iterator<Entry<String, List<String>>> map_iterator = response_content.entrySet().iterator();
		while(map_iterator.hasNext()){
			
			Map.Entry<String, List<String>> word_entry = (Map.Entry<String, List<String>>) map_iterator.next();
			highlight_words_in_response(g2d, word_entry);
			//query_points_words_in_response(g2d, word_entry);
			
		}
		
	}
	
	private void query_points_words_in_response(Graphics2D g2d, Map.Entry<String, List<String>> word_entry){
		
		
		
		String word_key = word_entry.getKey();
		String[] word_key_split = word_key.split("\\|");
		
		System.out.println();
		
		int word_key_stanza_index = Integer.parseInt(word_key_split[0]);
		int word_key_line_index = Integer.parseInt(word_key_split[1]);
		int word_key_word_index = Integer.parseInt(word_key_split[2]);
		
		Rectangle2D word_key_bounds = poem_content.getPoemStanzas().getStanzas().get(word_key_stanza_index).getLines().get(word_key_line_index).getWords().get(word_key_word_index).getRawPixelBounds();
		
		
		
		//g2d.setColor(Color.BLACK);
		//g2d.draw(word_key_bounds);
		
		double ellipse_radius = 4;
		double space_between_ellipses = 2;
		double space_between_line_ellipse = 2;
		
		//g2d.setColor(new Color(255, 161, 97, 200));
		//g2d.fill(response_ellipse);
		
		List<String> word_values = word_entry.getValue();
		for(int i = 0; i < word_values.size(); i++){
			
			Point2D location_of_ellipse;
			
			Point2D last_pair_connecting_point = last_drawn_ellipse_for_word.get(word_key);
			
			if(last_pair_connecting_point != null){
				
				location_of_ellipse = new Point2D.Double(last_pair_connecting_point.getX() + 2 * ellipse_radius + space_between_ellipses, last_pair_connecting_point.getY());
				
			}
			else{
				
				location_of_ellipse = new Point2D.Double(word_key_bounds.getX(), word_key_bounds.getY() + word_key_bounds.getHeight() + space_between_line_ellipse);
				
			}
			
			//location_of_ellipse = new Point2D.Double(word_key_bounds.getX(), word_key_bounds.getY() + word_key_bounds.getHeight());
			
			last_drawn_ellipse_for_word.put(word_key, location_of_ellipse);
			Ellipse2D response_ellipse = new Ellipse2D.Double(location_of_ellipse.getX(), location_of_ellipse.getY(), 2 * ellipse_radius, 2 * ellipse_radius);
			
			double hue = Math.random();
			int rgb = Color.HSBtoRGB((float)hue,1.0f,1.0f);
			g2d.setColor(new Color(rgb));
			g2d.fill(response_ellipse);
			
			
			String[] word_value_split = word_values.get(i).split("\\|");
			int word_value_stanza_index = Integer.parseInt(word_value_split[0]);
			int word_value_line_index = Integer.parseInt(word_value_split[1]);
			int word_value_word_index = Integer.parseInt(word_value_split[2]);
			
			Rectangle2D word_value_bounds = poem_content.getPoemStanzas().getStanzas().get(word_value_stanza_index).getLines().get(word_value_line_index).getWords().get(word_value_word_index).getRawPixelBounds();

			//g2d.setColor(Color.BLACK);
			//g2d.draw(word_value_bounds);
			
			last_pair_connecting_point = last_drawn_ellipse_for_word.get(word_values.get(i));

			if(last_pair_connecting_point != null){
				
				location_of_ellipse = new Point2D.Double(last_pair_connecting_point.getX() + 2 * ellipse_radius + space_between_ellipses, last_pair_connecting_point.getY());
				
			}
			else{
				
				location_of_ellipse = new Point2D.Double(word_value_bounds.getX(), word_value_bounds.getY() + word_value_bounds.getHeight() + space_between_line_ellipse);
				
			}
			
			last_drawn_ellipse_for_word.put(word_values.get(i), location_of_ellipse);
			Ellipse2D other_response_ellipse = new Ellipse2D.Double(location_of_ellipse.getX(), location_of_ellipse.getY(), 2 * ellipse_radius, 2 * ellipse_radius);
			
			//g2d.setColor(Color.RED);
			//g2d.draw(word_value_bounds);
			
			g2d.setColor(new Color(rgb));
			g2d.fill(other_response_ellipse);
			
			//g2d.setColor(new Color(97, 161, 97, 200));
			//g2d.fill(response_ellipse);
			
		}
		
	}
	
	private void highlight_words_in_response(Graphics2D g2d, Map.Entry<String, List<String>> word_entry){
		
		String word_key = word_entry.getKey();
		String[] word_key_split = word_key.split("\\|");
		
		int word_key_stanza_index = Integer.parseInt(word_key_split[0]);
		int word_key_line_index = Integer.parseInt(word_key_split[1]);
		int word_key_word_index = Integer.parseInt(word_key_split[2]);
		
		Rectangle2D word_key_bounds = poem_content.getPoemStanzas().getStanzas().get(word_key_stanza_index).getLines().get(word_key_line_index).getWords().get(word_key_word_index).getRawPixelBounds();
		Ellipse2D word_key_ellipse = new Ellipse2D.Double(word_key_bounds.getX(), word_key_bounds.getY(), word_key_bounds.getWidth() + 5, word_key_bounds.getHeight() + 5);
	
		g2d.setColor(new Color(97, 161, 97, 200));
		g2d.draw(word_key_ellipse);
		
		List<String> word_values = word_entry.getValue();
		for(int i = 0; i < word_values.size(); i++){
			
			String[] word_value_split = word_values.get(i).split("\\|");
			int word_value_stanza_index = Integer.parseInt(word_value_split[0]);
			int word_value_line_index = Integer.parseInt(word_value_split[1]);
			int word_value_word_index = Integer.parseInt(word_value_split[2]);
			
			Rectangle2D word_value_bounds = poem_content.getPoemStanzas().getStanzas().get(word_value_stanza_index).getLines().get(word_value_line_index).getWords().get(word_value_word_index).getRawPixelBounds();
			Ellipse2D word_value_ellipse = new Ellipse2D.Double(word_value_bounds.getX(), word_value_bounds.getY(), word_value_bounds.getWidth() + 5, word_value_bounds.getHeight() + 5);
			
			g2d.setColor(new Color(97, 161, 97, 200));
			g2d.draw(word_value_ellipse);
			
			double p1_x = word_key_bounds.getCenterX(), p2_x = word_value_bounds.getCenterX();
			double p1_y = word_key_bounds.getCenterY(), p2_y = word_value_bounds.getCenterY();
			
			if(p1_y - p2_y == 0){
				
				double height = Math.abs(p1_x - p2_x);
				double top_left_x;
				if(p1_x < p2_x){
					
					top_left_x = p1_x;
					
				}
				else{
					
					top_left_x = p2_x;
					
				}
				
				g2d.drawArc((int)top_left_x, (int) ((int)p1_y - height / 2), (int)Math.abs(p1_x - p2_x), (int)height, 0, 180);
				
			}
			if(p1_x - p2_x == 0){
				
				double height = Math.abs(p1_y - p2_y);
				double top_left_y;
				if(p1_y < p2_y){
					
					top_left_y = p1_y;
					
				}
				else{
					
					top_left_y = p2_y;
					
				}
				
				g2d.drawArc((int)(p1_x + height/ 2), (int) top_left_y, (int)height,  (int)Math.abs(p1_y - p2_y), 0, 180);
				
			}
			if(p1_x != p2_x && p1_y != p2_y){
				
				if(p1_x > p2_x){
					
					double temp_x = p2_x;
					p2_x = p1_x;
					p1_x = temp_x;
					
					double temp_y = p2_y;
					p2_y = p1_y;
					p1_y = temp_y;
					
				}
				
				/*double mid_x = (p1_x + p2_x) / 2, mid_y = (p1_y + p2_y) / 2;
				double r = Math.sqrt((p1_x - mid_x)*(p1_x - mid_x) + (p1_y - mid_y)*(p1_y - mid_y));
				double x = mid_x - r;
				double y = mid_y - r;
				double width = 2 * r;
				double height = 2 * r;
				double startAngle = (180/Math.PI) * Math.atan2(p1_y - mid_y, p1_x - mid_x);
				double endAngle = (180/Math.PI) * Math.atan2(p2_y - mid_y, p2_x - mid_x);
				g2d.setColor(Color.MAGENTA);
				g2d.drawArc((int)Math.ceil(x), (int)Math.ceil(y), (int)Math.ceil(width), (int)Math.ceil(height), (int)Math.ceil(startAngle), (int)Math.ceil(endAngle));
				g2d.drawArc((int)Math.ceil(x), (int)Math.ceil(y), (int)Math.ceil(width), (int)Math.ceil(height), (int)Math.ceil(startAngle), 180);
				*/
				g2d.draw(new Line2D.Double(p1_x, p1_y, p2_x, p2_y));
				/*double mid_x = (p1_x + p2_x) / 2, mid_y = (p1_y + p2_y) / 2;
				double dist_to_mid = Math.sqrt(Math.pow(p1_x - mid_x, 2) + Math.pow(p1_y - mid_y, 2));
				double half_height = 10;
				double hypo = Math.sqrt(Math.pow(dist_to_mid, 2) + Math.pow(half_height, 2));*/
				/*double p2_x_trans = p2_x - p1_x;
				double p2_y_trans = p2_y - p1_y;
				
				
				
				g2d.setColor(Color.BLACK);
				//g2d.draw(new Line2D.Double(0, 0, p2_x_trans, p2_y_trans));
				
				//double angle = Math.atan(p2_y_trans / p2_x_trans);//0.5112307122777896
				double angle = Math.atan2(p2_y_trans, p2_x_trans); //0.5112307122777896
				System.out.println(angle);
				double p2_x_rot, p2_y_rot;
				if(angle > 0){
				
					//angle = -angle;
					p2_x_rot = p2_x_trans * Math.cos(Math.toRadians(angle)) - p2_y_trans * Math.sin(Math.toRadians(angle));
					p2_y_rot = p2_x_trans * Math.sin(Math.toRadians(angle)) + p2_y_trans * Math.cos(Math.toRadians(angle));
					
					p2_x_rot = p2_x_trans * Math.cos(angle) + p2_y_trans * Math.sin(angle);
					p2_y_rot = (-1) * p2_x_trans * Math.sin(angle) + p2_y_trans * Math.cos(angle);
					
				}
				else{
					
					//angle = -angle;
					p2_x_rot = p2_x_trans * Math.cos(Math.toRadians(angle)) + p2_y_trans * Math.sin(Math.toRadians(angle));
					p2_y_rot = (-1) * p2_x_trans * Math.sin(Math.toRadians(angle)) + p2_y_trans * Math.cos(Math.toRadians(angle));
					
					p2_x_rot = p2_x_trans * Math.cos(angle) - p2_y_trans * Math.sin(angle);
					p2_y_rot = (1) * p2_x_trans * Math.sin(angle) + p2_y_trans * Math.cos(angle);
					
				}
				
				
				g2d.setColor(Color.BLUE);
				g2d.draw(new Line2D.Double(0, 0, p2_x_rot, p2_y_rot));
				
				double height = p2_x_rot, width = p2_x_rot;
				double top_left_x = 0, top_left_y = height / 2;
				double bottom_left_x = 0, bottom_left_y = -height / 2;
				double top_right_x = width, top_right_y = height / 2;
				double bottom_right_x = width, bottom_right_y = -height / 2;
				
				//angle = Math.atan2(p2_y_rot, p2_x_rot);
				//double top_left_x_rot = top_left_x * Math.cos(Math.toRadians(angle)) + top_left_y * Math.sin(Math.toRadians(angle));
				//double top_left_y_rot = top_left_y * Math.cos(Math.toRadians(angle)) - top_left_x * Math.sin(Math.toRadians(angle));
				
				
				double p2_x_rot_back, p2_y_rot_back;
				double top_left_x_rot, top_left_y_rot;
				double top_right_x_rot, top_right_y_rot;
				double bottom_left_x_rot, bottom_left_y_rot;
				double bottom_right_x_rot, bottom_right_y_rot;
				//angle = Math.toRadians(360) - angle;
				if(angle > 0){

					p2_x_rot_back = p2_x_rot * Math.cos(angle) - p2_y_rot * Math.sin(angle);
					p2_y_rot_back = (1) * p2_x_rot * Math.sin(angle) + p2_y_rot * Math.cos(angle);
					
					top_left_x_rot = top_left_x * Math.cos(angle) - top_left_y  * Math.sin(angle);
					top_left_y_rot = (1) * top_left_x * Math.sin(angle) + top_left_y * Math.cos(angle);
					
					bottom_left_x_rot = bottom_left_x * Math.cos(angle) - bottom_left_y  * Math.sin(angle);
					bottom_left_y_rot = (1) * bottom_left_x * Math.sin(angle) + bottom_left_y * Math.cos(angle);
					
					top_right_x_rot = top_right_x * Math.cos(angle) - top_right_y  * Math.sin(angle);
					top_right_y_rot = (1) * top_right_x * Math.sin(angle) + top_right_y * Math.cos(angle);
					
					bottom_right_x_rot = bottom_right_x * Math.cos(angle) - bottom_right_y  * Math.sin(angle);
					bottom_right_y_rot = (1) * bottom_right_x * Math.sin(angle) + bottom_right_y * Math.cos(angle);
					
				}
				else{

					p2_x_rot_back = p2_x_rot * Math.cos(angle) + p2_y_rot * Math.sin(angle);
					p2_y_rot_back = (-1) * p2_x_rot * Math.sin(angle) + p2_y_rot * Math.cos(angle);
					
					top_left_x_rot = top_left_x * Math.cos(angle) + top_left_y  * Math.sin(angle);
					top_left_y_rot = (-1) * top_left_x * Math.sin(angle) + top_left_y * Math.cos(angle);
					
					bottom_left_x_rot = bottom_left_x * Math.cos(angle) + bottom_left_y  * Math.sin(angle);
					bottom_left_y_rot = (-1) * bottom_left_x * Math.sin(angle) + bottom_left_y * Math.cos(angle);
					
					top_right_x_rot = top_right_x * Math.cos(angle) + top_right_y  * Math.sin(angle);
					top_right_y_rot = (-1) * top_right_x * Math.sin(angle) + top_right_y * Math.cos(angle);
					
					bottom_right_x_rot = bottom_right_x * Math.cos(angle) + bottom_right_y  * Math.sin(angle);
					bottom_right_y_rot = (-1) * bottom_right_x * Math.sin(angle) + bottom_right_y * Math.cos(angle);
					
				}
				
				g2d.setColor(Color.RED);
				g2d.draw(new Line2D.Double(0, 0, p2_x_rot_back, p2_y_rot_back));
				
				double p2_x_trans_back = p2_x_rot_back + p1_x;
				double p2_y_trans_back = p2_y_rot_back + p1_y;
				g2d.setColor(Color.GREEN);
				g2d.draw(new Line2D.Double(p1_x, p1_y, p2_x_trans_back, p2_y_trans_back));
				
				double top_left_x_trans = top_left_x_rot + p1_x;
				double top_left_y_trans = top_left_y_rot + p1_y;
				
				double top_right_x_trans = top_right_x_rot + p1_x;
				double top_right_y_trans = top_right_x_rot + p1_y;
				
				double bottom_left_x_trans = bottom_left_x_rot + p1_x;
				double bottom_left_y_trans = bottom_left_y_rot + p1_y;
				
				double bottom_right_x_trans = bottom_right_x_rot + p1_x;
				double bottom_right_y_trans = bottom_right_x_rot + p1_y;
				
				g2d.setColor(Color.RED);
				g2d.fillOval((int)top_left_x_trans - 10, (int)top_left_y_trans - 10, 10, 10);
				
				g2d.setColor(Color.BLUE);
				g2d.fillOval((int)top_right_x_trans - 10, (int)top_right_y_trans - 10, 10, 10);
				
				g2d.setColor(Color.GREEN);
				g2d.fillOval((int)bottom_left_x_trans - 10, (int)bottom_left_y_trans - 10, 10, 10);
				
				g2d.setColor(Color.MAGENTA);
				g2d.fillOval((int)bottom_right_x_trans - 10, (int)bottom_right_y_trans - 10, 10, 10);
				
				System.out.println((int)Math.toDegrees(-angle));
				width = Math.sqrt((p1_x - p2_x) * (p1_x - p2_x) + (p1_y - p2_y) * (p1_y - p2_y));
				g2d.drawArc((int)bottom_left_x_trans, (int) bottom_left_y_trans, (int)width, (int)width, (int)Math.toDegrees(-angle), 180);
				
				*/
			}
			
			/*if(p1_x > p2_x){
				
				double temp = p1_x;
				p1_x = p2_x;
				p2_x = temp;
				
			}
			if(p1_y > p2_y){
				
				double temp = p1_y;
				p1_y = p2_y;
				p2_y = temp;
				
			}*/
			
			//double x_trans?!?!?!-you can
			double p1_x_trans = 0, p1_y_trans = 0;
			double p2_x_trans = p2_x - p1_x, p2_y_trans = p2_y - p1_y;
			double angle = 45 * Math.PI / 180;//- Math.atan2(p2_y_trans, p2_x_trans);
			double v = p2_x * Math.cos(angle) - p2_y * Math.sin(angle);
			double p2_x_new = v;
			double c1_x = v / 3, c1_y = 20;
			double c2_x = (2 * v) / 3, c2_y = 20;
			double c1_x_rotated_back_x = c1_x * Math.cos(angle) + c1_y * Math.sin(angle);
			double c2_x_rotated_back_x = c2_x * Math.cos(angle) + c2_y * Math.sin(angle);
			double c1_x_trans_back = c1_x_rotated_back_x + p1_x;
			double c2_x_trans_back = c2_x_rotated_back_x + p1_x;
			double c1_y_rotated_back_y = c1_y * Math.cos(angle) - c1_x * Math.sin(angle);
			double c2_y_rotated_back_y = c2_y * Math.cos(angle) - c2_x * Math.sin(angle);
			double c1_y_trans_back = c1_y_rotated_back_y + p1_y;
			double c2_y_trans_back = c2_y_rotated_back_y + p1_y;
			//new AffineTransform().r
			//.getRotateInstance(Math.toRadians(angle), p1_x, p1_y).transform(c1_x, 0, pt, 0, 1);
			//how is this going to work out? You haven't been doing anything at all
			//double mid_x = (p1_x + p2_x) / 2, mid_y = (p1_y + p2_y) / 2;
			//double arc_radius = 150.0;
			//QuadCurve2D.Double connecting_arc = new QuadCurve2D.Double(p1_x, p1_y, mid_x + arc_radius, mid_y + arc_radius, p2_x, p2_y);
			CubicCurve2D.Double connecting_arc = new CubicCurve2D.Double(p1_x, p1_y, c1_x_trans_back, c1_y_trans_back, c2_x_trans_back, c2_y_trans_back, p2_x, p2_y);
			Line2D.Double connecting_line = new Line2D.Double(word_key_bounds.getCenterX(), word_key_bounds.getCenterY(), word_value_bounds.getCenterX(), word_value_bounds.getCenterY());
			//double hue = Math.random();
			//int rgb = Color.HSBtoRGB((float)hue,1.0f,1.0f);
			//g2d.setColor(new Color(rgb));
			//g2d.draw(connecting_arc);
			
			//g2d.drawArc((int)p1_x, (int)p1_y - 50 / 2, (int)Math.abs(p1_x - p2_x), 50, 0, 180);
			
			
		}
		
	}

	/*
	private void get_poem_layout(Graphics2D g2d, FontRenderContext fontRenderContext) {
		
		int top_offset = Math.round(CompositeGenerator.top_margin_space / resize_factor);
		Poem worksheet_content = poem_content;
		
		Stanza poem_header = get_stanza_layout(g2d, fontRenderContext, worksheet_content.getPoemHeader(), top_offset, -1);
		top_offset += poem_header.getRawPixelBounds().getHeight() + Math.round(CompositeGenerator.poem_header_break_space / resize_factor);
		
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();
		
		double poem_stanzas_min_x = Double.POSITIVE_INFINITY, poem_stanzas_max_x = Double.NEGATIVE_INFINITY;
		double poem_stanzas_min_y = Double.POSITIVE_INFINITY, poem_stanzas_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < poem_stanzas.size(); i++){

			Stanza poem_stanza = get_stanza_layout(g2d, fontRenderContext, poem_stanzas.get(i), top_offset, i);
		
			double poem_stanza_min_x = poem_stanza.getRawPixelBounds().getX();
			double poem_stanza_max_x = poem_stanza.getRawPixelBounds().getX() + poem_stanza.getRawPixelBounds().getWidth();
			
			if(i == 0){
				
				poem_stanzas_min_y = poem_stanza.getRawPixelBounds().getY();
				
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
			
			top_offset += poem_stanza.getRawPixelBounds().getHeight() + Math.round(CompositeGenerator.stanza_break_space / resize_factor);
			
		}
		
		Rectangle2D poem_stanzas_bounds = new Rectangle2D.Double(poem_stanzas_min_x, poem_stanzas_min_y, poem_stanzas_max_x - poem_stanzas_min_x, poem_stanzas_max_y - poem_stanzas_min_y);
		//g2d.draw(poem_stanzas_bounds);
		new Poem(poem_header, new PoemStanzas(poem_stanzas, poem_stanzas_bounds));
		
	}
	
	private Stanza get_stanza_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Stanza poem_stanza, int top_offset, int stanza_index){
		
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
			
			top_offset += line_in_stanza.getRawPixelBounds().getHeight() + Math.round(CompositeGenerator.line_break_space / resize_factor);
			
		}
		
		Rectangle2D stanza_bounds = new Rectangle2D.Double(stanza_min_x, stanza_min_y, stanza_max_x - stanza_min_x, stanza_max_y - stanza_min_y);
		//g2d.draw(stanza_bounds);
		
		return new Stanza(poem_stanza.getLines(), stanza_bounds);
		
	}
	
	private Line get_line_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Line poem_line, int top_offset, int stanza_index, int line_index){

		List<Word> words_in_line = poem_line.getWords();
		int character_left_offset = Math.round(CompositeGenerator.left_margin_space / resize_factor);
		
		Font text_font = CompositeGenerator.text_font;
		text_font = new Font(text_font.getFontName(), text_font.getStyle(), Math.round(text_font.getSize() / resize_factor));
		Color text_color = new Color(10, 10, 10, 250);
		
		double line_bounds_min_x = Double.POSITIVE_INFINITY, line_bounds_max_x = Double.NEGATIVE_INFINITY, line_bounds_min_y = Double.POSITIVE_INFINITY, line_bounds_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < words_in_line.size(); i++){
			
			Word word_in_line = words_in_line.get(i);
			List<String> characters_in_word = get_characters_in_word(word_in_line.getWord());
			
			double word_bounds_min_x = Double.POSITIVE_INFINITY, word_bounds_max_x = Double.NEGATIVE_INFINITY, word_bounds_min_y = Double.POSITIVE_INFINITY, word_bounds_max_y = Double.NEGATIVE_INFINITY;
			
			for(int j = 0; j < characters_in_word.size(); j++){
				
				g2d.setColor(text_color);
				TextLayout layout = new TextLayout(characters_in_word.get(j), text_font, fontRenderContext);
				layout.draw(g2d, character_left_offset, top_offset);
				
				
				g2d.setColor(new Color(255, 0, 0, 230));
				Rectangle2D layout_bounds = layout.getBounds();
				Rectangle2D char_bounds = new Rectangle2D.Double(character_left_offset + layout_bounds.getX(), top_offset + layout_bounds.getY(), layout_bounds.getWidth(), layout_bounds.getHeight());
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
			//TODO draw if words one of response
			if(is_in_response(word_in_line.getWord(), stanza_index, line_index, i)){
			
				g2d.draw(word_bounds);
				
			}
			
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
		
		return new Line(poem_line.getLine(), line_bounds, words_in_line);
		
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
		
	}*/
	
	private boolean is_in_response(String word, int stanza_index, int line_index, int word_index){
		
		boolean found = false;
		
		Iterator<Entry<String, List<String>>> map_iterator = response_content.entrySet().iterator();
		while(map_iterator.hasNext()){
			
			Map.Entry<String, List<String>> word_entry = (Map.Entry<String, List<String>>) map_iterator.next();
			String word_key = word_entry.getKey();
			//System.out.println(word_key + " " + word_key.split("\\|").length);
			String[] word_key_split = word_key.split("\\|");
			if(Integer.parseInt(word_key_split[0]) == stanza_index && Integer.parseInt(word_key_split[1]) == line_index && Integer.parseInt(word_key_split[2]) == word_index){
				
				found = true;
				break;
				
			}
			else{
			
				List<String> word_value = word_entry.getValue();
				for(int i = 0; i < word_value.size(); i++){
					
					String[] word_value_split = word_value.get(i).split("\\|");
					if(Integer.parseInt(word_value_split[0]) == stanza_index && Integer.parseInt(word_value_split[1]) == line_index && Integer.parseInt(word_value_split[2]) == word_index){
						
						found = true;
						break;
						
					}
					
				}
				
				if(found){
					
					break;
					
				}
				
			}
			
		}
		
		//System.out.println("tile match found");
		
		return found;
		
	}
	
	public JPanel getResponseViewerPanel(){
		
		return response_viewer_panel;
		
	}
	
	public JScrollPane getResponseViewerScrollPane(){
		
		return response_viewer_panel_scroll_pane;
		
	}
	
	public void setTileFilteredOut(boolean is_tile_filtered_out){
		
		if(tile_filtered_out != is_tile_filtered_out){
		
			tile_filtered_out = is_tile_filtered_out;
			//response_viewer_panel.repaint();
			
		}
		
	}
	
	public boolean getTileFilteredOut(){
		
		return tile_filtered_out;
		
	}
	
}
