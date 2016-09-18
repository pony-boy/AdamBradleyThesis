package annotationInteraction;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/*
 *  This code is based on:
 *  http://stackoverflow.com/questions/9489736/catmull-rom-curve-with-no-cusps-and-no-self-intersections/19283471#19283471
 *  https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
 */

public class CatmullRomSpline {

	private List<Point2D> spline_path_points;
	private Path2D spline_path;
	
	public CatmullRomSpline(){
		
		spline_path_points = new ArrayList<Point2D>();
		spline_path = new Path2D.Double();
		
	}
	
	public Path2D generateSplinePath(List<PenPoint> control_points, int interpolation_points_per_segment, int curve_type){
		
		if(!control_points.isEmpty()){
			
			spline_path.reset();
			generate_spline_path_points(control_points, interpolation_points_per_segment, curve_type);
			
			Point2D path_point = spline_path_points.get(0);
			
			spline_path.moveTo(path_point.getX(), path_point.getY());
			
			for(int i = 1; i < spline_path_points.size(); i++){
				
				path_point = spline_path_points.get(i);
				
				spline_path.lineTo(path_point.getX(), path_point.getY());
				
			}
			
		}
		
		return spline_path;
		
	}
	
	private void generate_spline_path_points(List<PenPoint> control_points, int interpolation_points_per_segment, int curve_type){

		if(!control_points.isEmpty()){
			
			spline_path_points.clear();
			
			List<Point2D> points_on_curve = new ArrayList<Point2D>();
			
			for(int i  = 0; i < control_points.size(); i++){
				
				points_on_curve.add(new Point2D.Double(control_points.get(i).getRawPixelX(), control_points.get(i).getRawPixelY()));
				
			}
			
			if(points_on_curve.size() < 3){
				
				spline_path_points = new ArrayList<Point2D>(points_on_curve);
				
			}
			else{

				double dx,dy;
				dx = points_on_curve.get(1).getX() - points_on_curve.get(0).getX();
				dy = points_on_curve.get(1).getY() - points_on_curve.get(0).getY();
				
				double extra_start_control_point_x = points_on_curve.get(0).getX() - dx;
				double extra_start_control_point_y = points_on_curve.get(0).getY() - dy;
				Point2D extra_start_control_point = new Point2D.Double(extra_start_control_point_x, extra_start_control_point_y);

				int total_points = points_on_curve.size() - 1;
				dx = points_on_curve.get(total_points).getX() - points_on_curve.get(total_points - 1).getX();
				dy = points_on_curve.get(total_points).getY() - points_on_curve.get(total_points - 1).getY();
				
				double extra_end_control_point_x = points_on_curve.get(total_points).getX() + dx;
				double extra_end_control_point_y = points_on_curve.get(total_points).getY() + dy;
				Point2D extra_end_control_point = new Point2D.Double(extra_end_control_point_x, extra_end_control_point_y);

				points_on_curve.add(0, extra_start_control_point);
				points_on_curve.add(extra_end_control_point);
				
				for(int i = 0; i < points_on_curve.size() - 3; i++){
					
					List<Point2D> points_between_i_plus_one_and_i_plus_two = get_interpolation_points(points_on_curve, i, interpolation_points_per_segment, curve_type);
					
					if(spline_path_points.size() > 0){
						
						points_between_i_plus_one_and_i_plus_two.remove(0);
						
					}
					
					spline_path_points.addAll(points_between_i_plus_one_and_i_plus_two);
					
				}

			}
			
		}

	}
	
	private List<Point2D> get_interpolation_points(List<Point2D> points_on_curve, int index, int interpolation_points_per_segment, int curve_type){
		
		List<Point2D> curve_segment_points = new ArrayList<Point2D>();
		
		double[] control_points_x = new double[4], control_points_y = new double[4], control_points_t = new double[4];

	    for (int i = 0; i < 4; i++) {
	    	
	    	control_points_x[i] = points_on_curve.get(index + i).getX();
	    	control_points_y[i] = points_on_curve.get(index + i).getY();
	    	control_points_t[i] = i;
	        
	    }
	    
	    double t_start, t_end;
	    
	    if(!(curve_type == 0)){
	    	
	    	double dx, dy;
	    	double t_i_plus_one = 0;
	    	
	    	for(int i = 1; i < 4; i++){
	    		
	    		dx = control_points_x[i] - control_points_x[i - 1];
	    		dy = control_points_y[i] - control_points_y[i - 1];
	    		
	    		if(curve_type == 1){
	    			
	    			t_i_plus_one += Math.pow(dx * dx + dy * dy, 0.25);
	    			
	    		}
	    		else{
	    			
	    			t_i_plus_one += Math.pow(dx * dx + dy * dy, 0.5);
	    			
	    		}
	    		
	    		control_points_t[i] = t_i_plus_one; 
	    		
	    	}
	    	
	    }
	    
	    t_start = control_points_t[1];
    	t_end = control_points_t[2];
    	
    	curve_segment_points.add(points_on_curve.get(index + 1));
    	
    	int interpolation_segments = interpolation_points_per_segment - 1;
    	double x_i, y_i, t_start_end_diff = t_end - t_start;
    	
    	for(int i = 1; i < interpolation_segments; i++){
    		
    		x_i = interpolate(control_points_x, control_points_t, t_start + (i * t_start_end_diff) / interpolation_segments);
    		y_i = interpolate(control_points_y, control_points_t, t_start + (i * t_start_end_diff) / interpolation_segments);
    		curve_segment_points.add(new Point2D.Double(x_i, y_i));
    		
    	}

    	curve_segment_points.add(points_on_curve.get(index + 2));
		
		return curve_segment_points;
		
	}
	
	private double interpolate(double points[], double t[], double tension){
		
		double l_01, l_12, l_23;
		double l_012, l_123;
		double c_12;
		
		l_01 = ((t[1] - tension) / (t[1] - t[0])) * points[0] + ((tension - t[0]) / (t[1] - t[0])) * points[1];
		l_12 = ((t[2] - tension) / (t[2] - t[1])) * points[1] + ((tension - t[1]) / (t[2] - t[1])) * points[2];
		l_23 = ((t[3] - tension) / (t[3] - t[2])) * points[2] + ((tension - t[2]) / (t[3] - t[2])) * points[3];
		
		l_012 = ((t[2] - tension) / (t[2] - t[0])) * l_01 + ((tension - t[0]) / (t[2] - t[0])) * l_12;
		l_123 = ((t[3] - tension) / (t[3] - t[1])) * l_12 + ((tension - t[1]) / (t[3] - t[1])) * l_23;
		
		c_12 = ((t[2] - tension) / (t[2] - t[1])) * l_012 + ((tension - t[1]) / (t[2] - t[1])) * l_123;

	    return c_12;
		
	}
	
	public Path2D getSplinePath(){
		
		return spline_path;
		
	}
	
	public List<Point2D> getSplinePathPoints(){
		
		return spline_path_points;
		
	}

}