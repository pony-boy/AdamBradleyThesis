package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
	
	private static double time_threshold_for_normalization = 60;
	private static double x_penalize_factor = 1, y_penalize_factor = 1;
	private static double time_penalize_factor = 1, space_penalize_factor = 1;
	
	public static enum clusterMetric{Min, Max, Avg}; 
	public static enum spaceMetric{NoDiff, Min, Max, Avg};
	
	private List<PenStroke> pen_strokes;
	private Rectangle2D raw_pixel_cluster_bounds, viewer_cluster_bounds;
	
	public Cluster(PenStroke pen_stroke){
		
		pen_strokes = new ArrayList<PenStroke>();
		pen_strokes.add(pen_stroke);
		
		raw_pixel_cluster_bounds = new Rectangle2D.Double();
		viewer_cluster_bounds = new Rectangle2D.Double();
		
	}
	
	public Cluster(Cluster copy_from_cluster){
		
		pen_strokes = new ArrayList<PenStroke>();
		
		List<PenStroke> pen_strokes_in_cluster = copy_from_cluster.getPenStrokes();
		for(int i = 0 ; i < pen_strokes_in_cluster.size(); i++){
			
			pen_strokes.add(pen_strokes_in_cluster.get(i));
			
		}
		
		raw_pixel_cluster_bounds = copy_from_cluster.getClusterBounds();
		viewer_cluster_bounds = copy_from_cluster.getViewerClusterBounds();
		
	}
	
	public void addPenStroke(PenStroke pen_stroke){
		
		pen_strokes.add(pen_stroke);
		
	}
	
	public void addPenStrokes(List<PenStroke> list_of_pen_strokes){
		
		pen_strokes.addAll(list_of_pen_strokes);
		
	}
	
	public double getMetric(Cluster cluster_to_compare_with, clusterMetric metric_type, spaceMetric space_metric_type){
		
		List<PenStroke> pen_strokes_to_compare = cluster_to_compare_with.getPenStrokes();
		double min_metric = 2, max_metric = 0, avg_metric = 0;
		
		for(int i = 0; i < pen_strokes.size(); i++){
			
			for(int j = 0; j < pen_strokes_to_compare.size(); j++){
				
				double metric = get_normalized_metric(pen_strokes.get(i), pen_strokes_to_compare.get(j), space_metric_type);
				
				if(i == 0 && j == 0){
					
					min_metric = metric;
					max_metric = metric;
					
				}
				else{
					
					if(metric < min_metric){
						
						min_metric = metric;
						
					}
					
					if(metric > max_metric){
						
						max_metric = metric;
						
					}

				}
				
				avg_metric += metric;
				
			}
			
		}
		
		avg_metric /= (pen_strokes.size() * pen_strokes_to_compare.size());
		
		double metric;
		
		switch (metric_type){

		case Avg:
			metric = avg_metric;
			break;
			
		case Max:
			metric = max_metric;
			break;
			
		case Min:
		default:
			
			metric = min_metric;
			break;
			
		}
		
		return metric;
		
	}
	
	private double get_normalized_metric(PenStroke pen_stroke, PenStroke pen_stroke_to_compare, spaceMetric metric_type){
		
		double time_metric, space_metric;
		
		if(pen_stroke.getStrokeId() < pen_stroke_to_compare.getStrokeId()){
			
			time_metric = get_normalized_time_metric(pen_stroke.getEndTime(), pen_stroke_to_compare.getStartTime());
			space_metric = get_normalized_space_metric(pen_stroke.getStrokeBounds(), pen_stroke_to_compare.getStrokeBounds(), metric_type);
			
		}
		else{
			
			time_metric = get_normalized_time_metric(pen_stroke_to_compare.getEndTime(), pen_stroke.getStartTime());
			space_metric = get_normalized_space_metric(pen_stroke_to_compare.getStrokeBounds(), pen_stroke.getStrokeBounds(), metric_type);
			
		}
		
		double metric = Math.sqrt((time_penalize_factor * time_metric) + (space_penalize_factor * space_metric));
		
		return metric;
		
	}
	
	private double get_normalized_time_metric(LocalDateTime pen_stroke_end_time, LocalDateTime pen_stroke_to_compare_start_time){

		double time_between_strokes = Duration.between(pen_stroke_end_time, pen_stroke_to_compare_start_time).getSeconds();
		
		double time_metric;
		
		if(time_between_strokes > time_threshold_for_normalization){
			
			time_metric = 1.0;
			
		}
		else{
			
			time_metric = time_between_strokes / time_threshold_for_normalization;
			
		}
		
		time_metric = Math.pow(time_metric, 2);

		return time_metric;
		
		
	}
	
	private double get_normalized_space_metric(Rectangle2D pen_stroke_bounds, Rectangle2D pen_stroke_to_compare_bounds, spaceMetric metric_type){

		double delta_x_squared = 0, delta_y_squared = 0;
		
		if(!pen_stroke_bounds.intersects(pen_stroke_to_compare_bounds) && !(pen_stroke_bounds.contains(pen_stroke_to_compare_bounds) || pen_stroke_to_compare_bounds.contains(pen_stroke_bounds))){
			
			delta_x_squared = Math.min(Math.pow((pen_stroke_to_compare_bounds.getX() - (pen_stroke_bounds.getX() + pen_stroke_bounds.getWidth())) / PenPoint.raw_pixel_max_x, 2), Math.pow(((pen_stroke_to_compare_bounds.getX() + pen_stroke_to_compare_bounds.getWidth()) - pen_stroke_bounds.getX()) / PenPoint.raw_pixel_max_x, 2));
			delta_y_squared = Math.min(Math.pow((pen_stroke_to_compare_bounds.getY() - (pen_stroke_bounds.getY() + pen_stroke_bounds.getHeight())) / PenPoint.raw_pixel_max_y, 2), Math.pow(((pen_stroke_to_compare_bounds.getY() + pen_stroke_to_compare_bounds.getHeight()) - pen_stroke_bounds.getY()) / PenPoint.raw_pixel_max_y, 2));
			
		}
		else{
			
			double x_diff_1, x_diff_2, x_diff_3, x_diff_4;
			
			x_diff_1 = Math.abs(pen_stroke_bounds.getX() - pen_stroke_to_compare_bounds.getX());
			x_diff_2 = Math.abs(pen_stroke_bounds.getX() - (pen_stroke_to_compare_bounds.getX() + pen_stroke_to_compare_bounds.getWidth()));
			x_diff_3 = Math.abs((pen_stroke_bounds.getX() + pen_stroke_bounds.getWidth()) - pen_stroke_to_compare_bounds.getX());
			x_diff_4 = Math.abs((pen_stroke_bounds.getX() + pen_stroke_bounds.getWidth()) - (pen_stroke_to_compare_bounds.getX() + pen_stroke_to_compare_bounds.getWidth()));
			
			double y_diff_1, y_diff_2, y_diff_3, y_diff_4;
			
			y_diff_1 = Math.abs(pen_stroke_bounds.getY() - pen_stroke_to_compare_bounds.getY());
			y_diff_2 = Math.abs(pen_stroke_bounds.getY() - (pen_stroke_to_compare_bounds.getY() + pen_stroke_to_compare_bounds.getHeight()));
			y_diff_3 = Math.abs((pen_stroke_bounds.getY() + pen_stroke_bounds.getHeight()) - pen_stroke_to_compare_bounds.getY());
			y_diff_4 = Math.abs((pen_stroke_bounds.getY() + pen_stroke_bounds.getHeight()) - (pen_stroke_to_compare_bounds.getY() + pen_stroke_to_compare_bounds.getHeight()));
			
			
			switch (metric_type) {
			
			case Min:
				delta_x_squared = Math.pow(Math.min(Math.min(x_diff_1, x_diff_2), Math.min(x_diff_3, x_diff_4)), 2);
				delta_y_squared = Math.pow(Math.min(Math.min(y_diff_1, y_diff_2), Math.min(y_diff_3, y_diff_4)), 2);
				break;
				
			case Max:
				delta_x_squared = Math.pow(Math.max(Math.max(x_diff_1, x_diff_2), Math.max(x_diff_3, x_diff_4)), 2);
				delta_y_squared = Math.pow(Math.max(Math.max(y_diff_1, y_diff_2), Math.max(y_diff_3, y_diff_4)), 2);
				break;
				
			case Avg:
				delta_x_squared = (x_diff_1 + x_diff_2 + x_diff_3 + x_diff_4) / 4;
				delta_y_squared = (y_diff_1 + y_diff_2 + y_diff_3 + y_diff_4) / 4;
				break;

			case NoDiff:
			default:
				delta_x_squared = 0;
				delta_y_squared = 0;
				break;
				
			}
			
		}

		double space_metric = (x_penalize_factor * delta_x_squared) + (y_penalize_factor * delta_y_squared); 

		return space_metric;
		
	}
	
	public List<PenStroke> getPenStrokes(){
		
		return pen_strokes;
		
	}
	
	public void setClusterBounds(int viewer_resize_factor){
		
		double cluster_min_x = Double.POSITIVE_INFINITY, cluster_max_x = Double.NEGATIVE_INFINITY;
		double cluster_min_y = Double.POSITIVE_INFINITY, cluster_max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < pen_strokes.size(); i++){
			
			PenStroke pen_stroke = pen_strokes.get(i);
			Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds();
			double pen_stroke_min_x = pen_stroke_bounds.getX(), pen_stroke_max_x = pen_stroke_min_x + pen_stroke_bounds.getWidth();
			double pen_stroke_min_y = pen_stroke_bounds.getY(), pen_stroke_max_y = pen_stroke_min_y + pen_stroke_bounds.getHeight();
			
			if(pen_stroke_min_x < cluster_min_x){
				
				cluster_min_x = pen_stroke_min_x;
				
			}
			
			if(pen_stroke_max_x > cluster_max_x){
				
				
				cluster_max_x = pen_stroke_max_x;
			}
			
			if(pen_stroke_min_y < cluster_min_y){
				
				cluster_min_y = pen_stroke_min_y;
				
			}
			
			if(pen_stroke_max_y > cluster_max_y){
				
				
				cluster_max_y = pen_stroke_max_y;
			}
			
		}
		
		raw_pixel_cluster_bounds = new Rectangle2D.Double(cluster_min_x, cluster_min_y, cluster_max_x - cluster_min_x, cluster_max_y - cluster_min_y);
		viewer_cluster_bounds = new Rectangle2D.Double(raw_pixel_cluster_bounds.getX() / viewer_resize_factor, raw_pixel_cluster_bounds.getY() / viewer_resize_factor, raw_pixel_cluster_bounds.getWidth() / viewer_resize_factor, raw_pixel_cluster_bounds.getHeight() / viewer_resize_factor);
		
	}
	
	public Rectangle2D getClusterBounds(){
		
		return raw_pixel_cluster_bounds;
		
	}
	
	public Rectangle2D getViewerClusterBounds(){
		
		return viewer_cluster_bounds;
		
	}

}
