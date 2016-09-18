package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ShapeRecognizer {
	
	private static Poem poem_content;
	
	public static enum penStrokeTypes{Ellipse, Underline, Connector, Undefined};
	public static enum XYPatterns{XUpYUp, XUpYDown, XDownYUp, XDownYDown, Ignore};
	public static List<List<XYPatterns>> ellipsePatterns = new ArrayList<List<XYPatterns>>();
	
	public ShapeRecognizer(Poem worksheet_content) {
		
		initialize_ellipse_pattern_template();

		poem_content = worksheet_content;
		
	}
	
	public static void initialize_ellipse_pattern_template(){
		
		List<XYPatterns> ellipsePattern;
		
		/*
		 * ellipses drawn from right to left
		 */
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePatterns.add(ellipsePattern);
		
		/*
		 * ellipses drawn from left to right
		 */
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePatterns.add(ellipsePattern);
		
		ellipsePattern = new ArrayList<XYPatterns>();
		ellipsePattern.add(XYPatterns.XDownYUp);
		ellipsePattern.add(XYPatterns.XUpYUp);
		ellipsePattern.add(XYPatterns.XUpYDown);
		ellipsePattern.add(XYPatterns.XDownYDown);
		ellipsePatterns.add(ellipsePattern);
		
	}
	
	public static boolean perform_stroke_type_check(PenStroke pen_stroke){
		
		boolean perform_check = false;
		
		Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds();
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			Stanza poem_stanza = poem_stanzas.get(i);
			Rectangle2D poem_stanza_bounds = poem_stanza.getRawPixelBounds();
			poem_stanza_bounds = new Rectangle2D.Double(poem_stanza_bounds.getX(), poem_stanza_bounds.getY(), poem_stanza_bounds.getWidth(), poem_stanza_bounds.getHeight() + CompositeGenerator.line_break_space);
			
			if(pen_stroke_bounds.intersects(poem_stanza_bounds)){

				perform_check = true;
				break;
				
			}
			
		}
		
		return perform_check;
		
	}
	
	public static penStrokeTypes get_pen_stroke_type(PenStroke pen_stroke){
		
		if(pen_stroke.getIsInTextSpace()){
		//if(perform_stroke_type_check(pen_stroke)){
			
			List<PenPoint> pen_points = pen_stroke.getPenPoints();
			
			double average_change_in_x = 0, average_change_in_y = 0, total_comparisons = 0;
			
			List<XYPatterns> pattern_order = new ArrayList<XYPatterns>();
			List<Integer> pattern_order_counts = new ArrayList<Integer>();
			int pen_points_step_size = 3;
			PenPoint current_pen_point = pen_points.get(0);
			
			for(int i = pen_points_step_size; i < pen_points.size(); i += pen_points_step_size){

				PenPoint next_pen_point = pen_points.get(i);
				
				double dx = next_pen_point.getRawX() - current_pen_point.getRawX();
				double dy = next_pen_point.getRawY() - current_pen_point.getRawY();
				
				XYPatterns current_x_y_pattern;
				
				if(dx > 0 && dy > 0){
					
					//System.out.println(i + ", x > 0, y > 0");
					current_x_y_pattern = XYPatterns.XUpYUp;
					
				}
				else if(dx > 0 && dy < 0){
					
					//System.out.println(i + ", x > 0, y < 0");
					current_x_y_pattern = XYPatterns.XUpYDown;
					
				}
				else if(dx < 0 && dy < 0){
					
					//System.out.println(i + ", x < 0, y < 0");
					current_x_y_pattern = XYPatterns.XDownYDown;
					
				}
				else if(dx < 0 && dy > 0){

					//System.out.println(i + ", x < 0, y > 0");
					current_x_y_pattern = XYPatterns.XDownYUp;

				}
				else{
					
					continue;
					
				}
				
				if(i != pen_points_step_size){
					
					if(pattern_order.isEmpty()){
						
						pattern_order.add(current_x_y_pattern);
						pattern_order_counts.add(1);
						
					}
					else if(pattern_order.get(pattern_order.size() - 1) != current_x_y_pattern){
						
						pattern_order.add(current_x_y_pattern);
						pattern_order_counts.add(1);
						
					}
					else{
						
						pattern_order_counts.set(pattern_order_counts.size() - 1, pattern_order_counts.get(pattern_order_counts.size() - 1) + 1);
						
					}
					
				}

				current_pen_point = next_pen_point;
				
				total_comparisons += 1;
				average_change_in_x += Math.abs(dx);
				average_change_in_y += Math.abs(dy);
				
			}
			
			average_change_in_x /= total_comparisons;
			average_change_in_y /= total_comparisons;

			if((average_change_in_x / average_change_in_y) > 15 || ((average_change_in_x / average_change_in_y) > 5 && is_between_lines(pen_stroke))){

				return penStrokeTypes.Underline;
				
			}
			else if(is_ellipse(pattern_order)){
				
				return penStrokeTypes.Ellipse;
				
			}
			else{
				
				return penStrokeTypes.Connector;
				
			}
			
		}
		else{
			
			System.out.println("pen stroke not in text space");
			return penStrokeTypes.Undefined;
			
		}
		
	}
	
	private static boolean is_ellipse(List<XYPatterns> pattern_order){
		
		boolean ellipse_match = false;

		if(pattern_order.size() > 3){
			
			List<XYPatterns> pattern_to_check = pattern_order.subList(0, 4);
			
			for(int i = 0; i < ellipsePatterns.size(); i++){
				
				if(ellipse_pattern_comparator(pattern_to_check, ellipsePatterns.get(i))){
					
					ellipse_match = true;
					break;
					
				}
				
			}
			
		}
		
		return ellipse_match;
		
	}
	
	private static boolean ellipse_pattern_comparator(List<XYPatterns> pattern_in_stroke, List<XYPatterns> pattern_in_template){
		
		boolean pattern_matches = true;
		
		for(int i = 0; i < pattern_in_stroke.size(); i++){

			if(pattern_in_stroke.get(i) != pattern_in_template.get(i)){
				
				pattern_matches = false;
				break;
				
			}
			
		}
		
		return pattern_matches;
		
	}
	
	private static boolean is_between_lines(PenStroke pen_stroke){
		
		boolean is_between_lines = false;
		
		Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds(); 
		double pen_stroke_area = pen_stroke_bounds.getWidth() * pen_stroke_bounds.getHeight();
		
		String[] hanging_words = {"f", "g", "j" , "p", "q", "y", ",", ";"};
		int max_font_descent = CompositeGenerator.getMaxFontDescent();
		
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		
		for(int i = 0 ; i < poem_stanzas.size(); i++){
			
			Stanza poem_stanza = poem_stanzas.get(i);
			Rectangle2D poem_stanza_bounds = poem_stanza.getRawPixelBounds();
			poem_stanza_bounds = new Rectangle2D.Double(poem_stanza_bounds.getX(), poem_stanza_bounds.getY(), poem_stanza_bounds.getWidth(), poem_stanza_bounds.getHeight() + CompositeGenerator.line_break_space);
			
			if(poem_stanza_bounds.intersects(pen_stroke_bounds)){
				
				List<Line> lines_in_stanza = poem_stanza.getLines();
				for(int j = 0 ; j < lines_in_stanza.size(); j++){
				
					Line line_in_stanza = lines_in_stanza.get(j);
					Rectangle2D line_bounds = line_in_stanza.getRawPixelBounds();
					
					boolean descent = false;
					List<Word> words_in_line = line_in_stanza.getWords();
					for(int k = 0; k < words_in_line.size(); k++){
						
						String word_content = words_in_line.get(k).getWord();
						for(int m = 0; m < hanging_words.length; m++){
							
							if(word_content.contains(hanging_words[m])){
								
								descent = true;
								break;
								
							}
							
						}
						
					}
					
					if(j == lines_in_stanza.size() - 1){
						
						double start_y = line_bounds.getY() + line_bounds.getHeight(), end_y = line_bounds.getY() + line_bounds.getHeight() + CompositeGenerator.line_break_space;
						
						if(descent){
							
							start_y -= max_font_descent;
							
						}

						line_bounds = new Rectangle2D.Double(line_bounds.getX(), start_y, line_bounds.getWidth(), end_y - start_y);
						
					}
					else{
						
						Rectangle2D next_line_bounds = lines_in_stanza.get(j + 1).getRawPixelBounds();
						double start_y = line_bounds.getY() + line_bounds.getHeight(), end_y = next_line_bounds.getY();
						
						if(descent){
							
							start_y -= max_font_descent;
							
						}

						line_bounds = new Rectangle2D.Double(line_bounds.getX(), start_y, line_bounds.getWidth(), end_y - start_y);
						
					}
					
					//System.out.println("line: " + line_bounds.getY() + ", " + (line_bounds.getY() + line_bounds.getHeight()));
					//System.out.println("pen stroke: " + pen_stroke_bounds.getY() + ", " + (pen_stroke_bounds.getY() + pen_stroke_bounds.getHeight()));
					
					if(line_bounds.intersects(pen_stroke_bounds)){
						
						Rectangle2D intersection = line_bounds.createIntersection(pen_stroke_bounds);
						double intersection_area = intersection.getWidth() * intersection.getHeight();
						
						//System.out.println("ratio: " + (intersection_area / pen_stroke_area));
						
						if((intersection_area / pen_stroke_area) > 0.25){
						
							is_between_lines = true;
							break;
							
						}
						
					}
					
				}
				
				break;
				
			}
			
		}
		
		return is_between_lines;
		
	}
	
}
