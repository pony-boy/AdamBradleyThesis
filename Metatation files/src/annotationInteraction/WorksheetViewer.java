package annotationInteraction;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class WorksheetViewer extends JPanel{

	public static int resize_factor = 6;
	
	public static int viewer_width = (int) Math.round(PenPoint.raw_pixel_max_x / resize_factor);
	public static int viewer_height = (int) Math.round(PenPoint.raw_pixel_max_y / resize_factor);
	
	private JPanel stroke_viewer_panel;
	private JScrollPane stroke_viewer_panel_scroll_pane;
	
	private JPanel icons_panel;
	private JButton next_cluster_iteration, previous_cluster_iteration;
	private boolean cluster_iterations_visible = false;

	private Worksheet worksheet;
	private Poem worksheet_content_in_viewer;
	
	private int iteration_count = 0;
	private List<ClusterIteration> cluster_iterations;
	
	private boolean pen_stroke_was_previously_clicked = false;
	private long pen_stroke_previously_clicked_id = Long.MIN_VALUE;
	private boolean pen_stroke_is_currently_clicked = false;
	private long pen_stroke_currently_clicked_id = Long.MIN_VALUE;
	
	//private boolean are_pen_strokes_to_highlight = false;
	
	private boolean pen_strokes_were_previously_highlighted = false;
	private List<PenStroke> pen_strokes_previously_highlighted = new ArrayList<PenStroke>();
	private List<PenStroke> pen_strokes_currently_highlighted = new ArrayList<PenStroke>();
	
	
	public WorksheetViewer(Worksheet worksheet){

		this.worksheet = worksheet;
		worksheet_content_in_viewer = poem_content_in_worksheet_viewer();
		create_stroke_viewer_panel();
		
		if(!cluster_iterations_visible){
			
			setLayout(new BorderLayout());
	        add(stroke_viewer_panel_scroll_pane, BorderLayout.CENTER);
			
		}
		else{
		
			create_icons_panel();
			setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

	        add(stroke_viewer_panel_scroll_pane);
	        add(icons_panel);
			
		}
        
	}
	
	private void create_icons_panel(){
		
		icons_panel = new JPanel();
		icons_panel.setPreferredSize(new Dimension(viewer_width, 30));
		icons_panel.setLayout(new BoxLayout(icons_panel, BoxLayout.LINE_AXIS));

		icons_panel.setBackground(Color.WHITE);
		icons_panel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY));
		
		next_cluster_iteration = new JButton("next_iteration");
		previous_cluster_iteration = new JButton("previous_iteration");
		
		next_cluster_iteration.setEnabled(true);
		previous_cluster_iteration.setEnabled(false);

		next_cluster_iteration.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				if(iteration_count < cluster_iterations.size() - 1){

					iteration_count++;
					if(!previous_cluster_iteration.isEnabled()){
						
						previous_cluster_iteration.setEnabled(true);
						
					}
					updateStrokeViewer();
					
				}
				else{

					next_cluster_iteration.setEnabled(false);
					
				}
				
			}
			
		});
		
		previous_cluster_iteration.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(iteration_count > 0){
					
					iteration_count--;
					if(!next_cluster_iteration.isEnabled()){
						
						next_cluster_iteration.setEnabled(true);
						
					}
					updateStrokeViewer();
					
				}
				else{

					previous_cluster_iteration.setEnabled(false);
					
				}
				
			}
			
		});

		icons_panel.add(Box.createHorizontalGlue());
		icons_panel.add(previous_cluster_iteration);
		icons_panel.add(Box.createHorizontalGlue());
		icons_panel.add(next_cluster_iteration);
		icons_panel.add(Box.createHorizontalGlue());
		
	}
	
	private void create_stroke_viewer_panel(){
		
		stroke_viewer_panel = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g){
				
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D) g;
				
				Stroke default_stroke = g2d.getStroke();
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, viewer_width, viewer_height);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				//g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
				//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				//g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
				
				display_worksheet_content(g2d);
				
				
				double width = 1.5;
				Stroke new_stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
				g2d.setStroke(new_stroke);
				
				Color stroke_color = new Color(70, 62, 237);
				Color stroke_color_on_click = new Color(255, 0, 0, 200);
				
				List<PenStroke> completed_pen_strokes = worksheet.getPenStrokes();

				//TODO add code to filter out pen strokes too
				for(int i = 0; i < completed_pen_strokes.size(); i++){
					
					long pen_stroke_id = completed_pen_strokes.get(i).getStrokeId();
					
					boolean to_be_highlighted = false;
					if(!pen_strokes_currently_highlighted.isEmpty()){
						
						for(int j = 0; j < pen_strokes_currently_highlighted.size(); j++){
							
							if(pen_stroke_id == pen_strokes_currently_highlighted.get(j).getStrokeId()){
							
								to_be_highlighted = true;
								break;
								
							}
							
						}
						
					}
					
					if(to_be_highlighted){
						
						g2d.setColor(stroke_color_on_click);
						
					}
					else{
						
						g2d.setColor(stroke_color);
						
					}
					
					if(pen_stroke_currently_clicked_id == pen_stroke_id){
						
						g2d.setColor(stroke_color_on_click);
						
					}

					//g2d.draw(completed_pen_strokes.get(i).getViewerLinearStrokePath());
					g2d.draw(completed_pen_strokes.get(i).getViewerSplineStrokePath());
					
					//g2d.setColor(Color.BLACK);
					//g2d.draw(completed_pen_strokes.get(i).getViewerStrokeBounds());
						
				}
				
				
				if (!worksheet.getIncompletePenStroke().isPenStrokeEmpty()){
					
					g2d.setColor(stroke_color);
					g2d.draw(worksheet.getIncompletePenStroke().getViewerLinearStrokePath());
					
				}
				
				
				g2d.setStroke(default_stroke);
				
				/*
				if(!completed_pen_strokes.isEmpty()){
				
					ClusterGenerator cluster_generator_last_pen_stroke = worksheet.getLastGeneratedClusters();
					if(cluster_generator_last_pen_stroke != null){
						
						List<Cluster> clusters;
						
						if(!cluster_iterations_visible){
							
							clusters = cluster_generator_last_pen_stroke.getClusterIterationAtStopIterationIndex().getClusters();
							
						}
						else{
							
							cluster_iterations = cluster_generator_last_pen_stroke.getClusterIterationsForPenStroke();
							clusters = cluster_iterations.get(iteration_count).getClusters();
							
						}

						for(int i = 0; i < clusters.size(); i++){
							
							g2d.setColor(Color.BLACK);
							g2d.draw(clusters.get(i).getViewerClusterBounds());
							
						}
						
					}
					
				}*/
				
			}
			
		};
		
		stroke_viewer_panel.setPreferredSize(new Dimension(viewer_width, viewer_height));
        stroke_viewer_panel_scroll_pane = new JScrollPane(stroke_viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        stroke_viewer_panel.addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent me){
        		
        		Point2D point_on_stroke_viewer = me.getPoint();
        		if((point_on_stroke_viewer.getX() >= 0 && point_on_stroke_viewer.getX() <= viewer_width) && (point_on_stroke_viewer.getY() >= 0 && point_on_stroke_viewer.getY() <= viewer_height)){
        			
        			boolean clicked_pen_stroke = false;
        			long pen_stroke_clicked_id = Long.MIN_VALUE;
        			List<PenStroke> completed_pen_strokes = worksheet.getPenStrokes();

    				for(int i = 0; i < completed_pen_strokes.size(); i++){

    					PenStroke pen_stroke = completed_pen_strokes.get(i);
    					Rectangle2D stroke_bounds = pen_stroke.getViewerStrokeBounds();
    					
    					if(stroke_bounds.contains(point_on_stroke_viewer)){
    						
    						clicked_pen_stroke = true;
    						pen_stroke_clicked_id = pen_stroke.getStrokeId();
    						pen_stroke_currently_clicked_id = pen_stroke_clicked_id;
    						break;
    						
    					}
    						
    				}
    				
    				if(clicked_pen_stroke){
    					
    					pen_stroke_is_currently_clicked = true;
    					
    					if(pen_stroke_was_previously_clicked){
    						
    						if(pen_stroke_clicked_id != pen_stroke_previously_clicked_id){
    							
    							worksheet.onPenClickOnPenStrokeInViewer(pen_stroke_clicked_id, false);
    							stroke_viewer_panel.repaint();
    							
    						}
    						
    					}
    					else{
    						
    						worksheet.onPenClickOnPenStrokeInViewer(pen_stroke_clicked_id, false);
    						stroke_viewer_panel.repaint();
    						
    					}
    					
    					pen_stroke_was_previously_clicked = true;
    					pen_stroke_previously_clicked_id = pen_stroke_clicked_id;
    					
    				}
    				else{
    					
    					pen_stroke_currently_clicked_id = Long.MIN_VALUE;
    					pen_stroke_is_currently_clicked = false;
    					
    					if(pen_stroke_was_previously_clicked){
    						
    						pen_stroke_was_previously_clicked = false;
    						pen_stroke_previously_clicked_id = Long.MIN_VALUE;
        					worksheet.onPenClickOnPenStrokeInViewer(pen_stroke_previously_clicked_id, true);
    						
    					}
    					
    					stroke_viewer_panel.repaint();
    					
    				}
    				
        		}
        		
        	}
        	
		});
        
        stroke_viewer_panel.addMouseMotionListener(new MouseAdapter() {
        	
        	public void mouseMoved(MouseEvent me){
        		
        		Point2D point_on_stroke_viewer = me.getPoint();
        		if((point_on_stroke_viewer.getX() >= 0 && point_on_stroke_viewer.getX() <= viewer_width) && (point_on_stroke_viewer.getY() >= 0 && point_on_stroke_viewer.getY() <= viewer_height)){

        			List<PenStroke> completed_pen_strokes = worksheet.getPenStrokes();

        			boolean found_pen_strokes_to_highlight = false;
        			List<PenStroke> pen_strokes_to_highlight = new ArrayList<PenStroke>();
        			
    				for(int i = 0; i < completed_pen_strokes.size(); i++){

    					PenStroke pen_stroke = completed_pen_strokes.get(i);
    					Rectangle2D stroke_bounds = pen_stroke.getViewerStrokeBounds();
    					
    					if(stroke_bounds.contains(point_on_stroke_viewer)){

    						//System.out.println("in");
    						found_pen_strokes_to_highlight = true;
    						pen_strokes_to_highlight = get_pen_strokes_to_highlight(pen_stroke);
    						//System.out.println(pen_strokes_to_highlight.size());
    						pen_strokes_currently_highlighted = pen_strokes_to_highlight;
    						
    						break;
    						
    					}
    						
    				}
    				
    				if(found_pen_strokes_to_highlight){
    					
    					if(pen_strokes_were_previously_highlighted){
    						
    						if(pen_strokes_previously_highlighted.size() == pen_strokes_currently_highlighted.size()){
    							
    							boolean same_as_previous = true;
    							for(int i = 0; i < pen_strokes_previously_highlighted.size(); i++){
    								
    								if(pen_strokes_previously_highlighted.get(i).getStrokeId() != pen_strokes_currently_highlighted.get(i).getStrokeId()){
    									
    									same_as_previous = false;
    									break;
    									
    								}
    								
    							}
    							
    							if(!same_as_previous){
    								
    								stroke_viewer_panel.repaint();
    		    					pen_strokes_were_previously_highlighted = true;
    		    					pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;
    								
    							}
    							
    						}
    						else{
    							
    							stroke_viewer_panel.repaint();
    							pen_strokes_were_previously_highlighted = true;
        	    				pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;

    						}
    						
    					}
    					else{
    						
    						stroke_viewer_panel.repaint();
							pen_strokes_were_previously_highlighted = true;
    	    				pen_strokes_previously_highlighted = pen_strokes_currently_highlighted;
    						
    					}
    					
    				}
    				else{
    					
    					found_pen_strokes_to_highlight = false;
    					pen_strokes_currently_highlighted = new ArrayList<PenStroke>();
    					
    					if(pen_strokes_were_previously_highlighted){
    						
    						pen_strokes_were_previously_highlighted = false;
    						pen_strokes_previously_highlighted = new ArrayList<PenStroke>();
    						
    					}
    					
    					stroke_viewer_panel.repaint();
    					
    				}
    				
        		}
        		
        	}
        	
		});
        
	}
	
	private List<PenStroke> get_pen_strokes_to_highlight(PenStroke pen_stroke){
		
		List<PenStroke> pen_strokes_to_highlight = new ArrayList<PenStroke>();
		
		long pen_stroke_id = pen_stroke.getStrokeId();
		
		ClusterGenerator cluster_generator_last_pen_stroke = worksheet.getLastGeneratedClusters();
		if(cluster_generator_last_pen_stroke != null){
			
			List<Cluster> clusters = cluster_generator_last_pen_stroke.getClusterIterationAtStopIterationIndex().getClusters();
			
			for(int j = 0; j < clusters.size(); j++){
				
				List<PenStroke> pen_strokes_in_cluster = clusters.get(j).getPenStrokes();
				
				for(int k = 0; k < pen_strokes_in_cluster.size(); k++){
					
					if(pen_strokes_in_cluster.get(k).getStrokeId() == pen_stroke_id){
						
						pen_strokes_to_highlight = pen_strokes_in_cluster;
						break;
						
					}
					
				}
				
			}
			
		}
		
		return pen_strokes_to_highlight;
		
	}

	private Poem poem_content_in_worksheet_viewer(){

		Poem worksheet_content = worksheet.getWorksheetContent();
		Stanza poem_header_viewer = stanza_content_in_worksheet_viewer(worksheet_content.getPoemHeader());
		
		List<Stanza> poem_stanzas_in_viewer = new ArrayList<Stanza>();
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			poem_stanzas_in_viewer.add(stanza_content_in_worksheet_viewer(poem_stanzas.get(i)));
			
		}
		
		Rectangle2D stanza_in_poem_bounds = worksheet_content.getPoemStanzas().getRawPixelBounds();
		Rectangle2D stanza_bounds_viewer = new Rectangle2D.Double(stanza_in_poem_bounds.getX() / resize_factor, stanza_in_poem_bounds.getY() / resize_factor, stanza_in_poem_bounds.getWidth() / resize_factor, stanza_in_poem_bounds.getHeight() / resize_factor);

		return new Poem(poem_header_viewer, new PoemStanzas(poem_stanzas_in_viewer, stanza_bounds_viewer));
		
	}
	
	private Stanza stanza_content_in_worksheet_viewer(Stanza stanza_in_poem){
		
		List<Line> lines_in_stanza_viewer = new ArrayList<Line>();
		
		List<Line> lines_in_stanza = stanza_in_poem.getLines();
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			lines_in_stanza_viewer.add(line_content_in_worksheet_viewer(lines_in_stanza.get(i)));
			
		}
		
		Rectangle2D stanza_in_poem_bounds = stanza_in_poem.getRawPixelBounds();
		Rectangle2D stanza_bounds_viewer = new Rectangle2D.Double(stanza_in_poem_bounds.getX() / resize_factor, stanza_in_poem_bounds.getY() / resize_factor, stanza_in_poem_bounds.getWidth() / resize_factor, stanza_in_poem_bounds.getHeight() / resize_factor);
		
		return new Stanza(lines_in_stanza_viewer, stanza_bounds_viewer);
		
	}
	
	private Line line_content_in_worksheet_viewer(Line line_in_stanza){
		
		List<Word> words_in_line_viewer = new ArrayList<Word>();
		
		List<Word> words_in_line = line_in_stanza.getWords();
		for(int i = 0 ; i < words_in_line.size(); i++){
			
			Word word_in_line = words_in_line.get(i);
			List<Rectangle2D> characters_in_word_bounds = word_in_line.getCharactersRawPixelBounds(); 
			List<Point2D> characters_in_word_locations = word_in_line.getCharactersTextLayoutLocations();
			
			List<String> characters_in_word = word_in_line.getCharacters();
			List<Rectangle2D> characters_in_word_bounds_viewer = new ArrayList<Rectangle2D>();
			List<Point2D> characters_in_word_locations_viewer = new ArrayList<Point2D>();
			
			for(int j = 0; j < characters_in_word.size(); j++){
				
				Rectangle2D character_in_word_bounds = characters_in_word_bounds.get(j);
				Rectangle2D character_bounds_viewer = new Rectangle2D.Double(character_in_word_bounds.getX() / resize_factor, character_in_word_bounds.getY() / resize_factor, character_in_word_bounds.getWidth() / resize_factor, character_in_word_bounds.getHeight() / resize_factor);
				characters_in_word_bounds_viewer.add(character_bounds_viewer);
				
				Point2D character_location = characters_in_word_locations.get(j);
				characters_in_word_locations_viewer.add(new Point2D.Double(character_location.getX() / resize_factor, character_location.getY() / resize_factor));
				
			}
			
			Rectangle2D word_in_line_bounds = word_in_line.getRawPixelBounds();
			Rectangle2D word_bounds_viewer = new Rectangle2D.Double(word_in_line_bounds.getX() / resize_factor, word_in_line_bounds.getY() / resize_factor, word_in_line_bounds.getWidth() / resize_factor, word_in_line_bounds.getHeight() / resize_factor);
			words_in_line_viewer.add(new Word(word_in_line.getWord(), word_bounds_viewer, characters_in_word_bounds_viewer, characters_in_word_locations_viewer, word_in_line.getPOS()));
			
		}
		
		Rectangle2D line_in_stanza_bounds = line_in_stanza.getRawPixelBounds();
		Rectangle2D line_bounds_viewer = new Rectangle2D.Double(line_in_stanza_bounds.getX() / resize_factor, line_in_stanza_bounds.getY() / resize_factor, line_in_stanza_bounds.getWidth() / resize_factor, line_in_stanza_bounds.getHeight() / resize_factor);
		
		return new Line(line_in_stanza.getLine(), line_bounds_viewer, words_in_line_viewer);
		
	}
	
	private void display_worksheet_content(Graphics2D g2d){

		//get_poem_layout(g2d, g2d.getFontRenderContext());
		
		FontRenderContext fontRenderContext = g2d.getFontRenderContext();
		
		layout_stanza_content(g2d, fontRenderContext, worksheet_content_in_viewer.getPoemHeader());
		List<Stanza> poem_stanzas = worksheet_content_in_viewer.getPoemStanzas().getStanzas();
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

	/*
	private void get_poem_layout(Graphics2D g2d, FontRenderContext fontRenderContext) {
		
		int top_offset = Math.round(CompositeGenerator.top_margin_space / resize_factor);
		Poem worksheet_content = worksheet.getWorksheetContent();
		
		Stanza poem_header = get_stanza_layout(g2d, fontRenderContext, worksheet_content.getPoemHeader(), top_offset);
		top_offset += poem_header.getRawPixelBounds().getHeight() + Math.round(CompositeGenerator.poem_header_break_space / resize_factor);
		
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();
		
		double poem_stanzas_min_x = Double.POSITIVE_INFINITY, poem_stanzas_max_x = Double.NEGATIVE_INFINITY;
		double poem_stanzas_min_y = Double.POSITIVE_INFINITY, poem_stanzas_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < poem_stanzas.size(); i++){

			Stanza poem_stanza = get_stanza_layout(g2d, fontRenderContext, poem_stanzas.get(i), top_offset);
		
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
	
	private Stanza get_stanza_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Stanza poem_stanza, int top_offset){
		
		List<Line> lines_in_stanza = poem_stanza.getLines();
		
		double stanza_min_x = Double.POSITIVE_INFINITY, stanza_max_x = Double.NEGATIVE_INFINITY;
		double stanza_min_y = Double.POSITIVE_INFINITY, stanza_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			Line line_in_stanza = get_line_layout(g2d, fontRenderContext, lines_in_stanza.get(i), top_offset);
			
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
	
	private Line get_line_layout(Graphics2D g2d, FontRenderContext fontRenderContext, Line poem_line, int top_offset){

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
			//g2d.draw(word_bounds);
			
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

	public void updateStrokeViewer(){
		
		stroke_viewer_panel.repaint();
		
	}
	
	public boolean isPenStrokeClicked(){
		
		return pen_stroke_is_currently_clicked;
		
	}
	
}
