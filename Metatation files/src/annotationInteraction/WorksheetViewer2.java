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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class WorksheetViewer2 {
	
	private static int poem_content_image_width = 1400, poem_content_image_height = 1000;
	
	private JPanel viewer_panel;
	private JScrollPane viewer_scroll_pane;
	
	private Worksheet worksheet;
	private Poem poem_content;
	
	private static String font_name;
	private static int font_style, font_size;
	private static Font text_font;
	
	private static int font_color_r, font_color_g, font_color_b, font_color_a;
	public static Color text_color, text_color_pen_stroke_words, text_color_on_hover, text_color_on_click;
	
	private static int top_margin_space, left_margin_space, poem_header_break_space, stanza_break_space, line_break_space;
	
	private Poem poem_content_in_viewer;
	private static BufferedImage poem_content_image;
	
	private Map<Long, List<String>> pen_stroke_id_words_annotated_map;
	private Map<Long, Rectangle2D> pen_stroke_id_bounds_map;
	
	private boolean pen_strokes_were_previously_highlighted = false;
	private List<Long> pen_strokes_previously_highlighted = new ArrayList<Long>();
	private List<Long> pen_strokes_currently_highlighted = new ArrayList<Long>();
	
	private boolean pen_stroke_was_previously_clicked = false;
	private Long pen_stroke_previously_clicked_id = Long.MIN_VALUE;
	private boolean pen_stroke_is_currently_clicked = false;
	private Long pen_stroke_currently_clicked_id = Long.MIN_VALUE;
	
	private Map<Long, List<Integer>> pen_stroke_id_query_types_map;
	private Map<Rectangle2D, Long> query_type_widget_pen_stroke_id_map;
	private Map<Rectangle2D, Color> query_type_widget_color_map;
	
	private Long query_type_widget_click_current = null;
	private Rectangle2D query_type_widget_click_current_bounds = null;
	private Long query_type_widget_click_previous = null;
	private Rectangle2D query_type_widget_click_previous_bounds = null;
	
	private static Color[] word_pair_colors = {
		
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
	
	public WorksheetViewer2(Worksheet worksheet){
		
		this.worksheet = worksheet;
		poem_content = worksheet.getWorksheetContent();
		pen_stroke_id_words_annotated_map = new HashMap<Long, List<String>>();
		pen_stroke_id_bounds_map = new HashMap<Long, Rectangle2D>();
		pen_stroke_id_query_types_map = new HashMap<Long, List<Integer>>();
		query_type_widget_pen_stroke_id_map = new HashMap<Rectangle2D, Long>();
		
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
		
		create_viewer_panel();
		
	}
	
	@SuppressWarnings("serial")
	private void create_viewer_panel(){
		
		viewer_panel = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				//g2d.drawImage(poem_content_image, (getWidth() - poem_content_image.getWidth()) / 2, (getHeight() - poem_content_image.getHeight()) / 2, null);
				g2d.drawImage(poem_content_image, (getWidth() - poem_content_image.getWidth()) / 2, 0, null);
				
			}
			
		};
		
		viewer_panel.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent me){
        		
        		//TODO change this to reflect whatever you decide for centering on y
        		double poem_content_start_x = (viewer_panel.getWidth() - poem_content_image.getWidth()) / 2.0;
        		//double poem_content_start_y = (viewer_panel.getHeight() - poem_content_image.getHeight()) / 2.0;
        		double poem_content_start_y = 0;
        		double poem_content_end_x = poem_content_start_x + poem_content_image.getWidth();
        		double poem_content_end_y = poem_content_start_y + poem_content_image.getHeight();
        		
        		Point2D point_on_viewer = me.getPoint();
        		if((point_on_viewer.getX() >= poem_content_start_x && point_on_viewer.getX() <= poem_content_end_x) && (point_on_viewer.getY() >= poem_content_start_y && point_on_viewer.getY() <= poem_content_end_y)){

        			if(!pen_stroke_id_bounds_map.isEmpty()){
        				
        				Point2D point_on_viewer_in_context = new Point2D.Double(point_on_viewer.getX() - poem_content_start_x, point_on_viewer.getY() - poem_content_start_y);
        				
        				boolean clicked_pen_stroke = false;
            			long pen_stroke_clicked_id = Long.MIN_VALUE;
        				
            			for(Map.Entry<Long, Rectangle2D> pen_stroke_entry : pen_stroke_id_bounds_map.entrySet()){
            				
        					Rectangle2D pen_stroke_bounds = pen_stroke_entry.getValue();
        					point_on_viewer_in_context = new Point2D.Double(point_on_viewer.getX() - poem_content_start_x, point_on_viewer.getY() - poem_content_start_y);
        					if(pen_stroke_bounds.contains(point_on_viewer_in_context)){
        						
        						//System.out.println("In pen stroke click check: ");
        						clicked_pen_stroke = true;
        						pen_stroke_clicked_id = pen_stroke_entry.getKey();
        						pen_stroke_is_currently_clicked = true;
        						pen_stroke_currently_clicked_id = pen_stroke_clicked_id;
        						//System.out.println("clicked pen stroke " + pen_stroke_currently_clicked_id);
        						break;
        						
        					}
        				
        				}
        				
        				boolean is_widget_clicked = false;
        				Long pen_stroke_id_clicked_widget = null;
        				for(Map.Entry<Rectangle2D, Long> query_type_widget_entry : query_type_widget_pen_stroke_id_map.entrySet()){
        					
        					Rectangle2D query_type_widget_bounds = query_type_widget_entry.getKey();
        					// && pen_stroke_currently_clicked_id == query_type_widget_entry.getValue()
        					if(query_type_widget_bounds.contains(point_on_viewer_in_context)){
        						
        						//System.out.println("In widget click check: ");
        						is_widget_clicked = true;
        						pen_stroke_id_clicked_widget = query_type_widget_entry.getValue();
        						//System.out.println("Clicked widget of pen stroke: " + pen_stroke_id_clicked_widget);
        						break;
        						
        					}
        					
        				}
 
        				
        				if(is_widget_clicked){
        					
        					//System.out.println("widget clicked so preserve pen stroke clicked");
        					clicked_pen_stroke = true;
        					
        				}
        				else{
        					
        					//System.out.println("widget not clicked");
        					//System.out.println("currently clicked pen stroke id " + pen_stroke_currently_clicked_id + " ; query widget clicked " + query_type_widget_click_current);
        					
        					//pen_stroke_currently_clicked_id != Long.MIN_VALUE && 
        					if(query_type_widget_click_current != null){
        						
        						//pen_stroke_previously_clicked_id = pen_stroke_currently_clicked_id;
        						//System.out.println("previously clicked widget " + query_type_widget_click_previous + " so preserve pen click");
        						clicked_pen_stroke = true;
        						
        					}
        					
        				}
        				
        				if(clicked_pen_stroke){
        					
        					//System.out.println("in pen stroke clciked");
        					//System.out.println("previously clciked pen stroke: " + pen_stroke_previously_clicked_id);
        					
        					if(pen_stroke_was_previously_clicked){ 
        						
        						if(pen_stroke_currently_clicked_id != pen_stroke_previously_clicked_id){
        							
        							worksheet.onPenClickOnPenStrokeInViewer(pen_stroke_currently_clicked_id, false);
        							
        							List<Long> pen_strokes_previously_clicked = get_pen_strokes_to_highlight(pen_stroke_previously_clicked_id);
        							List<Long> pen_strokes_currently_clicked = get_pen_strokes_to_highlight(pen_stroke_currently_clicked_id);
        							
        							boolean repaint = false;
        							if(pen_strokes_previously_clicked.size() == pen_strokes_currently_clicked.size()){
        								
        								for(int i = 0; i < pen_strokes_previously_clicked.size(); i++){
        									
        									if(pen_strokes_previously_clicked.get(i) != pen_strokes_currently_clicked.get(i)){
        										
        										repaint = true;
        										break;
        										
        									}
        									
        								}
        								
        							}
        							else{
        								
        								repaint = true;
        								
        							}
        							
        							if(repaint){
        								
        								List<Long> pen_strokes_to_repaint = pen_strokes_previously_clicked;
            							for(int j = 0; j < pen_strokes_to_repaint.size(); j++){
            							
            								List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_to_repaint.get(j));
                							if(pen_stroke_words != null){
                							
                								for(int i = 0; i < pen_stroke_words.size(); i++){
                    								
                    								repaint_words(pen_stroke_words, text_color_pen_stroke_words);
                    								//viewer_panel.repaint();
                    								
                    							}
                								
                							}
            								
            							}
        								
        							}
        							else{
        								
        								List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_stroke_previously_clicked_id);
            							if(pen_stroke_words != null){
            								
            								for(int i = 0; i < pen_stroke_words.size(); i++){
                								
                								repaint_words(pen_stroke_words, text_color_on_hover);
                								//viewer_panel.repaint();
                								
                							}
            								
            							}
        								
        							}
        							
        							List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_stroke_currently_clicked_id);
        							if(pen_stroke_words != null){
        								
        								for(int i = 0; i < pen_stroke_words.size(); i++){
            								
            								repaint_words(pen_stroke_words, text_color_on_click);
            								//viewer_panel.repaint();
            								
            							}
        								
        							}
        							
        							viewer_panel.repaint();
        							
        						}
        						
        					}
        					else{
        						
        						worksheet.onPenClickOnPenStrokeInViewer(pen_stroke_currently_clicked_id, false);
        						List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_stroke_currently_clicked_id);
        						if(pen_stroke_words != null){
        							
        							for(int i = 0; i < pen_stroke_words.size(); i++){
        								
        								repaint_words(pen_stroke_words, text_color_on_click);
        								
        							}
        							
        						}
        						
        					}
        					
        					pen_stroke_was_previously_clicked = true;
        					pen_stroke_previously_clicked_id = pen_stroke_currently_clicked_id;
        					
        					viewer_panel.repaint();
        					
        				}
        				else{
        					
        					//TODO add code to preserve click if click on query type widget of this pen stroke
        					
        					//System.out.println("pen stroke not clicked");
        					worksheet.onPenClickOnPenStrokeInViewer(Long.MIN_VALUE, true);
        					
        					pen_stroke_currently_clicked_id = Long.MIN_VALUE;
        					pen_stroke_is_currently_clicked = false;
        					
        					//System.out.println("previously clciked pen stroke: " + pen_stroke_previously_clicked_id);
        					if(pen_stroke_was_previously_clicked){
        						
        						//System.out.println("pen stroke was prevously clicked so need to repaint");
        						List<Long> pen_strokes_to_repaint = get_pen_strokes_to_highlight(pen_stroke_previously_clicked_id);
        						//System.out.println("no of pen strokes to repaint: " + pen_strokes_to_repaint.size());
    							for(int j = 0; j < pen_strokes_to_repaint.size(); j++){
    							
    								List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_to_repaint.get(j));
    								//System.out.println("no of pen stroke words to repaint: " + pen_stroke_words.size());
        							if(pen_stroke_words != null){
        								
        								for(int i = 0; i < pen_stroke_words.size(); i++){
            								
            								repaint_words(pen_stroke_words, text_color_pen_stroke_words);
            								viewer_panel.repaint();
            								
            							}
        								
        							}
    								
    							}
        						
        						pen_stroke_was_previously_clicked = false;
        						pen_stroke_previously_clicked_id = Long.MIN_VALUE;
        						//viewer_panel.repaint();
        						
        					}
        					
        					viewer_panel.repaint();
        					
        				}
        				
        				if(pen_stroke_currently_clicked_id != Long.MIN_VALUE){
        					
        					//TODO finish click on widget code to filter out tiles
        				//System.out.println("point clicked: " + point_on_viewer);
            				boolean widget_clicked = false;
            				for(Map.Entry<Rectangle2D, Long> query_type_widget_entry : query_type_widget_pen_stroke_id_map.entrySet()){
            					
            					Rectangle2D query_type_widget_bounds = query_type_widget_entry.getKey();
            					//System.out.println("testing for click on widget: " + query_type_widget_bounds);
            					if(query_type_widget_bounds.contains(point_on_viewer_in_context)){
            						
            						//System.out.println("query widget clicked ");
            						widget_clicked = true;
            						query_type_widget_click_current = query_type_widget_entry.getValue();
            						//System.out.println("pen stroke id of widget: " + query_type_widget_click_current);
            						query_type_widget_click_current_bounds = query_type_widget_entry.getKey();
            						//System.out.println("bounds of clciked widget: " + query_type_widget_click_current_bounds);
            						break;
            						
            					}
            					
            				}
            				
            				if(widget_clicked){
            					
            					//System.out.println("previous clciked widget: " + query_type_widget_click_previous + " current clciked widget: " + query_type_widget_click_current);
            					
            					if(query_type_widget_click_previous_bounds != null){
            						
            						if(query_type_widget_click_previous_bounds != query_type_widget_click_current_bounds){

            							//System.out.println("clicked widget not same as previous");
            							
            							//System.out.println("repaint previous: " + query_type_widget_click_previous);
            							Color widget_color = query_type_widget_color_map.get(query_type_widget_click_previous_bounds);
            							repaint_query_type_widgets(query_type_widget_click_previous_bounds, widget_color);
                					
            							//System.out.println("select current: " + query_type_widget_click_current_bounds);
                						widget_color = query_type_widget_color_map.get(query_type_widget_click_current_bounds);
                						draw_selected_query_type_widget(query_type_widget_click_current_bounds, widget_color);
                						
                						//TODO call worksheet to filter out response tiles by query type
                						
                						query_type_widget_click_previous = query_type_widget_click_current;
                						query_type_widget_click_previous_bounds = query_type_widget_click_current_bounds;

                						viewer_panel.repaint();
                						
                					}
            						
            					}
            					else{

            						//TODO call work sheet to filter out response tiles by query type
            						
            						//System.out.println("no previously clicked widget");
            						//System.out.println("select current: " + query_type_widget_click_current_bounds);
            						Color widget_color = query_type_widget_color_map.get(query_type_widget_click_current_bounds);
            						draw_selected_query_type_widget(query_type_widget_click_current_bounds, widget_color);
            						
            						query_type_widget_click_previous = query_type_widget_click_current;
            						query_type_widget_click_previous_bounds = query_type_widget_click_current_bounds;
            					
            						viewer_panel.repaint();
            						
            					}
            					
            				}
            				else{
            					
            					//System.out.println("Clciked outside widget");
            					
            					query_type_widget_click_current = null;
            					query_type_widget_click_current_bounds = null;
            					
            					if(query_type_widget_click_previous != null){
            					
            						//TODO call work sheet to undo filter by query types
            						//System.out.println("removing selection from previously clicked widget");
            						//System.out.println("repaint previous: " + query_type_widget_click_previous_bounds);
            						Color widget_color = query_type_widget_color_map.get(query_type_widget_click_previous_bounds);
            						repaint_query_type_widgets(query_type_widget_click_previous_bounds, widget_color);
            						
            						query_type_widget_click_previous = null;
            						query_type_widget_click_previous_bounds = null; 
            						
            						viewer_panel.repaint();
            						
            						
            					}
            					
            					viewer_panel.repaint();
            					
            				}
        					
        				}
        						
        			}

        		}
        		
        	}
        	
		});
		
		viewer_panel.addMouseMotionListener(new MouseAdapter() {
        	
        	public void mouseMoved(MouseEvent me){
        		
        		//TODO change this to reflect whatever you decide for centering on y
        		double poem_content_start_x = (viewer_panel.getWidth() - poem_content_image.getWidth()) / 2.0;
        		//double poem_content_start_y = (viewer_panel.getHeight() - poem_content_image.getHeight()) / 2.0;
        		double poem_content_start_y = 0;
        		double poem_content_end_x = poem_content_start_x + poem_content_image.getWidth();
        		double poem_content_end_y = poem_content_start_y + poem_content_image.getHeight();
        		
        		Point2D point_on_viewer = me.getPoint();
        		if((point_on_viewer.getX() >= poem_content_start_x && point_on_viewer.getX() <= poem_content_end_x) && (point_on_viewer.getY() >= poem_content_start_y && point_on_viewer.getY() <= poem_content_end_y)){

        			if(!pen_stroke_id_bounds_map.isEmpty()){
        				
        				boolean found_pen_stroke_to_highlight = false;
        				List<Long> pen_strokes_to_highlight = new ArrayList<Long>();
        				for(Map.Entry<Long, Rectangle2D> pen_stroke_entry : pen_stroke_id_bounds_map.entrySet()){
        				
        					Rectangle2D pen_stroke_bounds = pen_stroke_entry.getValue();
        					Point2D point_on_viewer_in_context = new Point2D.Double(point_on_viewer.getX() - poem_content_start_x, point_on_viewer.getY() - poem_content_start_y);
        					if(pen_stroke_bounds.contains(point_on_viewer_in_context)){

        						found_pen_stroke_to_highlight = true;
        						pen_strokes_to_highlight = get_pen_strokes_to_highlight(pen_stroke_entry.getKey());
        						pen_strokes_currently_highlighted = pen_strokes_to_highlight;
        						break;
        						
        					}
        					
        				}
        				
        				if(found_pen_stroke_to_highlight){
        					
        					if(pen_strokes_were_previously_highlighted){
        						
        						if(pen_strokes_previously_highlighted.size() == pen_strokes_currently_highlighted.size()){
        							
        							boolean same_as_previous = true;
        							for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
        								
        								if(pen_strokes_previously_highlighted.get(i) != pen_strokes_currently_highlighted.get(i)){
        									
        									same_as_previous = false;
        									break;
        									
        								}
        								
        							}
        							
        							if(!same_as_previous){
        								
        								boolean same_as_clicked = false;
        								for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
        									
        									if(pen_strokes_previously_highlighted.get(i) == pen_stroke_currently_clicked_id){
        										
        										same_as_clicked = true;
        										break;
        										
        									}
        									
        								}
        								
        								if(!same_as_clicked){
        									
        									for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
            									
            									repaint_words(pen_stroke_id_words_annotated_map.get(pen_strokes_previously_highlighted.get(i)), text_color_pen_stroke_words);
            									
            								}
        									
        								}
        							
        								for(int i = 0; i < pen_strokes_currently_highlighted.size(); i++){
        									
        									repaint_words(pen_stroke_id_words_annotated_map.get(pen_strokes_currently_highlighted.get(i)), text_color_on_hover);
        									
        								}
        		    					pen_strokes_were_previously_highlighted = true;
        		    					pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;
        		    					//viewer_panel.repaint();
        								
        							}
        							
        						}
        						else{

        							boolean same_as_clicked = false;
    								for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
    									
    									if(pen_strokes_previously_highlighted.get(i) == pen_stroke_currently_clicked_id){
    										
    										same_as_clicked = true;
    										break;
    										
    									}
    									
    								}
    								
    								if(!same_as_clicked){
    									
    									for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
        									
    										List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_previously_highlighted.get(i));
    										if(pen_stroke_words != null){
    											
    											repaint_words(pen_stroke_words, text_color_pen_stroke_words);
    											
    										}
        									
        								}
    									
    								}
    								for(int i = 0; i < pen_strokes_currently_highlighted.size(); i++){
    									
    									List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_currently_highlighted.get(i));
    									if(pen_stroke_words != null){
    									
    										repaint_words(pen_stroke_words, text_color_on_hover);
    										
    									}
    									
    								}
        							pen_strokes_were_previously_highlighted = true;
            	    				pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;
            	    				
            	    				//viewer_panel.repaint();

        						}
        						
        					}
        					else{
        						
        						boolean same_as_clicked = false;
        						List<Long> pen_strokes_currently_clicked_id = get_pen_strokes_to_highlight(pen_stroke_currently_clicked_id);
								for(int i = 0; i < pen_strokes_currently_highlighted.size(); i++){
									
									for(int j = 0; j < pen_strokes_currently_clicked_id.size(); j++){
										
										if(pen_strokes_currently_highlighted.get(i) == pen_strokes_currently_clicked_id.get(j)){
											
											same_as_clicked = true;
											break;
											
										}
										
									}
									
								}
								
								if(!same_as_clicked){
									
									for(int i = 0; i < pen_strokes_currently_highlighted.size(); i++){
										
										List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_currently_highlighted.get(i));
										if(pen_stroke_words != null){
										
											repaint_words(pen_stroke_words, text_color_on_hover);
											
										}
										
									}
									
								}
		
    							pen_strokes_were_previously_highlighted = true;
        	    				pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;
        	    				//viewer_panel.repaint();
        						
        					}
        					
        				}
        				else{
        					
        					found_pen_stroke_to_highlight = false;
        					pen_strokes_currently_highlighted = new ArrayList<Long>();
        					
        					if(pen_strokes_were_previously_highlighted){
        						
        						boolean same_as_clicked = false;
								for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
									
									if(pen_strokes_previously_highlighted.get(i) == pen_stroke_currently_clicked_id){
										
										same_as_clicked = true;
										break;
										
									}
									
								}
								
								if(!same_as_clicked){
									
									for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
    									
										List<String> pen_stroke_words = pen_stroke_id_words_annotated_map.get(pen_strokes_previously_highlighted.get(i));
										if(pen_stroke_words != null){
										
											repaint_words(pen_stroke_words, text_color_pen_stroke_words);
											
										}
    									
    								}
									
								}
        						
        						pen_strokes_were_previously_highlighted = false;
        						pen_strokes_previously_highlighted = new ArrayList<Long>();
        						//viewer_panel.repaint();
        						
        					}
        					
        				}
        				
        			}
        			
        		}
        		
        	}
        	
		});
		
		viewer_panel.setPreferredSize(new Dimension(poem_content_image.getWidth() + 10, poem_content_image.getHeight() + 10));
		viewer_scroll_pane = new JScrollPane(viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
	}
	
	private List<Long> get_pen_strokes_to_highlight(Long pen_stroke_id){
		
		List<PenStroke> pen_strokes_to_highlight = new ArrayList<PenStroke>();
		
		ClusterGenerator cluster_generator_last_pen_stroke = worksheet.getLastGeneratedClusters();
		if(cluster_generator_last_pen_stroke != null){
			
			List<Cluster> clusters = cluster_generator_last_pen_stroke.getClusterIterationAtStopIterationIndex().getClusters();
			
			boolean cluster_found = false;
			for(int j = 0; j < clusters.size(); j++){
				
				List<PenStroke> pen_strokes_in_cluster = clusters.get(j).getPenStrokes();
				
				if(!cluster_found){
				
					for(int k = 0; k < pen_strokes_in_cluster.size(); k++){
						
						if(pen_strokes_in_cluster.get(k).getStrokeId() == pen_stroke_id){
							
							pen_strokes_to_highlight = pen_strokes_in_cluster;
							cluster_found = true;
							break;
							
						}
						
					}
					
				}
				else{
					
					break;
					
				}
				
			}
			
		}
		
		List<Long> pen_stroke_ids_to_highlight = new ArrayList<Long>();
		
		if(!pen_strokes_to_highlight.isEmpty()){
			
			for(int i = 0; i < pen_strokes_to_highlight.size(); i++){
				
				pen_stroke_ids_to_highlight.add(pen_strokes_to_highlight.get(i).getStrokeId());
				
			}
			
		}
		
		return pen_stroke_ids_to_highlight;
		
	}
	
	private static void initialize_text_attributes(){
		
		font_name = "Palatino Linotype";
		font_style = Font.PLAIN;
		font_size = 17;
		text_font = new Font(font_name, font_style, font_size);
		
		font_color_r = font_color_g = font_color_b = 170;
		font_color_a = 255;
		text_color = new Color(font_color_r, font_color_g, font_color_b, font_color_a);
		text_color_pen_stroke_words = new Color(120, 120, 120, font_color_a);
		text_color_on_hover = new Color(80, 80, 80, font_color_a);
		text_color_on_click = new Color(0, 0, 0, font_color_a);
		
		top_margin_space = 20;
		left_margin_space = 20;
		
		line_break_space = 20;
		poem_header_break_space = stanza_break_space = line_break_space + 40;
		
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

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		List<Word> words_in_line = poem_line.getWords();
		int character_left_offset = left_margin_space;
		
		double line_bounds_min_x = Double.POSITIVE_INFINITY, line_bounds_max_x = Double.NEGATIVE_INFINITY, line_bounds_min_y = Double.POSITIVE_INFINITY, line_bounds_max_y = Double.NEGATIVE_INFINITY;
		
		List<Word> words_in_line_viewer = new ArrayList<Word>();
		for(int i = 0; i < words_in_line.size(); i++){
			
			List<Rectangle2D> character_bounds_viewer = new ArrayList<Rectangle2D>();
			List<Point2D> character_locations_viewer = new ArrayList<Point2D>();
			
			Word word_in_line = words_in_line.get(i);
			List<String> characters_in_word = get_characters_in_word(word_in_line.getWord());
			
			double word_bounds_min_x = Double.POSITIVE_INFINITY, word_bounds_max_x = Double.NEGATIVE_INFINITY, word_bounds_min_y = Double.POSITIVE_INFINITY, word_bounds_max_y = Double.NEGATIVE_INFINITY;
			
			for(int j = 0; j < characters_in_word.size(); j++){

				g2d.setColor(text_color);
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
			
			words_in_line_viewer.add(new Word(word_in_line.getWord(), word_bounds, character_bounds_viewer, character_locations_viewer));
			
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
	
	//This only repaints the pen stroke words
	public void updateViewer(List<QueryDetails> details_of_new_queries_generated){
		
		pen_stroke_id_words_annotated_map = worksheet.getPenStrokeIdAnnotatedWordMap();
		for(Map.Entry<Long, List<String>> pen_stroke_entry : pen_stroke_id_words_annotated_map.entrySet()){
			
			generate_pen_stroke_id_bounds_map_entry(pen_stroke_entry.getKey(), pen_stroke_entry.getValue());
			repaint_words(pen_stroke_entry.getValue(), text_color_pen_stroke_words);
			
		}
		
		/*for(int i = 0; i < details_of_new_queries_generated.size(); i++){
			
			List<Long> pen_strokes_generating_query = new ArrayList<Long>();
			
			String query_id = details_of_new_queries_generated.get(i).query_id;
			String[] query_id_split = query_id.split("#");
			String[] pen_strokes_in_query = query_id_split[1].substring(1, query_id_split[1].length() - 1).split("-");
			for(int j = 0; j < pen_strokes_in_query.length; j++){
				
				pen_strokes_generating_query.add(Long.parseLong(pen_strokes_in_query[j]));
				
			}

			String[] query_content_pen_stroke_split = details_of_new_queries_generated.get(i).query_content.split("#");
			for(int j = 0; j < pen_strokes_generating_query.size(); j++){
				
				Long pen_stroke_id = pen_strokes_generating_query.get(j);
				List<String> pen_stroke_words_in_map = pen_stroke_id_words_annotated_map.get(pen_stroke_id);
				if(pen_stroke_words_in_map == null){
				
					List<String> query_words_per_pen_stroke = new ArrayList<String>();
					String[] pen_stroke_words = query_content_pen_stroke_split[j].substring(1, query_content_pen_stroke_split[j].length() - 1).split(" ");
					for(int k = 0; k < pen_stroke_words.length; k++){
						
						query_words_per_pen_stroke.add(pen_stroke_words[k].substring(1, pen_stroke_words[k].length() - 1));
						
					}
					
					pen_stroke_id_words_annotated_map.put(pen_stroke_id, query_words_per_pen_stroke);
					
					generate_pen_stroke_id_bounds_map_entry(pen_stroke_id, query_words_per_pen_stroke);
					repaint_words(query_words_per_pen_stroke, text_color_pen_stroke_words);
					
				}
				
			}

			
		}*/
		
	}
	
	private void generate_pen_stroke_id_bounds_map_entry(Long pen_stroke_id, List<String> query_words_per_pen_stroke){
		
		double min_x = Double.POSITIVE_INFINITY, max_x = Double.NEGATIVE_INFINITY;
		double min_y = Double.POSITIVE_INFINITY, max_y = Double.NEGATIVE_INFINITY;
		for(int k = 0; k < query_words_per_pen_stroke.size(); k++){
			
			String[] query_word_info = query_words_per_pen_stroke.get(k).split("\\|");
			int stanza_index = Integer.parseInt(query_word_info[0]);
			int line_index = Integer.parseInt(query_word_info[1]);
			int word_index = Integer.parseInt(query_word_info[2]);
			
			Word query_word = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
			Rectangle2D word_bounds = query_word.getRawPixelBounds();
			
			if(word_bounds.getX() < min_x){
				
				min_x = word_bounds.getX();
				
			}
			
			if(word_bounds.getX() + word_bounds.getWidth() > max_x){
				
				max_x = word_bounds.getX() + word_bounds.getWidth();
				
			}
			
			if(word_bounds.getY() < min_y){
				
				min_y = word_bounds.getY();
				
			}
			
			if(word_bounds.getY() + word_bounds.getHeight() > max_y){
				
				max_y = word_bounds.getY() + word_bounds.getHeight();
				
			}
			
		}
		
		Rectangle2D pen_stroke_bounds = new Rectangle2D.Double(min_x, min_y, max_x - min_x, max_y - min_y);
		pen_stroke_id_bounds_map.put(pen_stroke_id, pen_stroke_bounds);

	}
	
	public void updateViewer(){
		
		//TODO add code to draw query type widgets for each pen stroke as obtained from worksheet
		pen_stroke_id_query_types_map = worksheet.getPenStrokeIdQueryTypesMap();
		
		pen_stroke_id_words_annotated_map = worksheet.getPenStrokeIdAnnotatedWordMap();
		pen_stroke_id_bounds_map = new HashMap<Long, Rectangle2D>();
		for(Map.Entry<Long, List<String>> pen_stroke_entry : pen_stroke_id_words_annotated_map.entrySet()){
			
			generate_pen_stroke_id_bounds_map_entry(pen_stroke_entry.getKey(), pen_stroke_entry.getValue());
			
		}
		
		query_type_widget_pen_stroke_id_map = new HashMap<Rectangle2D, Long>();
		query_type_widget_color_map = new HashMap<Rectangle2D, Color>();
		for(Map.Entry<Long, List<Integer>> pen_stroke_entry : pen_stroke_id_query_types_map.entrySet()){
			
			Rectangle2D pen_stroke_bounds = pen_stroke_id_bounds_map.get(pen_stroke_entry.getKey());
			draw_query_type_widgets(pen_stroke_entry.getKey(), pen_stroke_bounds, pen_stroke_entry.getValue());
			
		}
		
		viewer_panel.repaint();
		
	}
	
	//TODO draw query type widget
	private void draw_query_type_widgets(Long pen_stroke_id, Rectangle2D pen_stroke_bounds, List<Integer> query_types){
		
		System.out.println("widgets for pen stroke id: " + pen_stroke_id);
		
		double space_between_word_and_widgets = 3;
		double space_between_widgets = 5;
		double phoneme_widget_height = 10;
		double phoneme_widget_width = 10;
		
		Graphics2D g2d = poem_content_image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double phoneme_widgets_start_x = pen_stroke_bounds.getX();
		double phoneme_widgets_start_y = pen_stroke_bounds.getY() + pen_stroke_bounds.getHeight() + space_between_word_and_widgets;
		for(int i = 0; i < query_types.size(); i++){
			
			g2d.setColor(Color.WHITE);
			g2d.fillRect((int)Math.floor(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets - 1), (int)Math.floor(phoneme_widgets_start_y - 1), (int)Math.ceil(phoneme_widget_width + 2), (int)Math.ceil(phoneme_widget_height + 2));
			
			//System.out.println(i);
			Color phoneme_widget_color = word_pair_colors[query_types.get(i)];
			g2d.setColor(phoneme_widget_color);
			g2d.fillOval((int)Math.ceil(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets), (int)Math.ceil(phoneme_widgets_start_y), (int)Math.ceil(phoneme_widget_width), (int)Math.ceil(phoneme_widget_height));
			
			//TODO add code to store these widget locations for on click
			Rectangle2D query_type_widget_bounds = new Rectangle2D.Double((int)Math.ceil(phoneme_widgets_start_x + i * phoneme_widget_width + i * space_between_widgets), (int)Math.ceil(phoneme_widgets_start_y), (int)Math.ceil(phoneme_widget_width), (int)Math.ceil(phoneme_widget_height));
			query_type_widget_pen_stroke_id_map.put(query_type_widget_bounds, pen_stroke_id);
			query_type_widget_color_map.put(query_type_widget_bounds, phoneme_widget_color);
			
			//System.out.println("widget bounds: " + query_type_widget_bounds);
			
		}
		
		g2d.dispose();
		
	}
	
	//TODO repaint query type widget on deselection
	private void repaint_query_type_widgets(Rectangle2D phoneme_widget_bounds, Color phoneme_widget_color){
		
		Graphics2D g2d = poem_content_image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect((int)phoneme_widget_bounds.getX() - 1, (int)phoneme_widget_bounds.getY() - 1, (int)phoneme_widget_bounds.getWidth() + 2, (int)phoneme_widget_bounds.getHeight() + 2);
		
		g2d.setColor(phoneme_widget_color);
		g2d.fillOval((int)phoneme_widget_bounds.getX(), (int)phoneme_widget_bounds.getY(), (int)phoneme_widget_bounds.getWidth(), (int)phoneme_widget_bounds.getHeight());
		
		g2d.dispose();
		
		viewer_panel.repaint();
		
	}
	
	//TODO Draw query type widget on selection
	private void draw_selected_query_type_widget(Rectangle2D phoneme_widget_bounds, Color phoneme_widget_color){
		
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
	
	private void repaint_words(List<String> query_words_per_pen_stroke, Color text_color){
		
		Graphics2D text_content_g2d = (Graphics2D) poem_content_image.getGraphics();
		text_content_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(query_words_per_pen_stroke != null){
		
			for(int i = 0; i < query_words_per_pen_stroke.size(); i++){
				
				String[] query_word_info = query_words_per_pen_stroke.get(i).split("\\|");
				int stanza_index = Integer.parseInt(query_word_info[0]);
				int line_index = Integer.parseInt(query_word_info[1]);
				int word_index = Integer.parseInt(query_word_info[2]);
				
				Word query_word = poem_content_in_viewer.getPoemStanzas().getStanzas().get(stanza_index).getLines().get(line_index).getWords().get(word_index);
				Rectangle2D word_bounds = query_word.getRawPixelBounds();
				List<String> characters_in_word = query_word.getCharacters();
				List<Point2D> character_locations = query_word.getCharactersTextLayoutLocations();
				
				text_content_g2d.setColor(Color.WHITE);
				text_content_g2d.fillRect((int) Math.ceil(word_bounds.getX()), (int) Math.ceil(word_bounds.getY()), (int) Math.ceil(word_bounds.getWidth()), (int) Math.ceil(word_bounds.getHeight()));
				
				for(int j = 0; j < character_locations.size(); j++){
					
					text_content_g2d.setColor(text_color);
					Font font_to_use = text_font;
					if(text_color.equals(text_color_pen_stroke_words) || text_color.equals(text_color_on_hover) || text_color.equals(text_color_on_click)){
						
						font_to_use = new Font(text_font.getFontName(), Font.BOLD, text_font.getSize());
						
					}
					TextLayout layout = new TextLayout(characters_in_word.get(j), font_to_use, text_content_g2d.getFontRenderContext());
					Point2D character_location = character_locations.get(j);
					layout.draw(text_content_g2d, (float)character_location.getX(), (float)character_location.getY());
					
				}
				
			}
			
		}
		
		text_content_g2d.dispose();
		viewer_panel.repaint();
		
	}
	
	public JPanel getViewerPanel(){
		
		return viewer_panel;
		
	}
	
	public JScrollPane getViewerScrollPane(){
		
		return viewer_scroll_pane;
		
	}
	
	public static BufferedImage getPoemContentImage(){
		
		return poem_content_image;
		
	}

}
