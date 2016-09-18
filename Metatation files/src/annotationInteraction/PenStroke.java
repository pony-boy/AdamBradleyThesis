package annotationInteraction;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PenStroke {
	
	private long pen_stroke_id;
	
	private String pattern_id;
	private long pen_id;
	
	private List<PenPoint> pen_points;
	private LocalDateTime start_time, end_time;
	
	private ShapeRecognizer.penStrokeTypes pen_stroke_type = ShapeRecognizer.penStrokeTypes.Undefined;
	
	//The following will not be stored in XML file but will be calculated when reading from the file
	
	private Rectangle2D raw_pixel_stroke_bounds;
	private Rectangle2D viewer_stroke_bounds;
	
	private Path2D raw_pixel_linear_stroke_path;
	private Path2D viewer_linear_stroke_path;
	private Path2D tile_linear_stroke_path;
	
	private CatmullRomSpline catmull_rom_spline= new CatmullRomSpline();
	private static int interpolation_points_per_segment = 20, curve_type = 1;
	private Path2D raw_pixel_spline_stroke_path;
	private Path2D viewer_spline_stroke_path;
	private Path2D tile_spline_stroke_path;
	private List<Point2D> raw_pixel_spline_stroke_points;
	
	private boolean is_in_text_space = false;
	
	public PenStroke(long pen_stroke_id){
		
		this.pen_stroke_id = pen_stroke_id;
		
		pattern_id = null;
		pen_id = Long.MIN_VALUE;
		
		pen_points = new ArrayList<PenPoint>();
		
		start_time = null;
		end_time = null;

		raw_pixel_stroke_bounds = new Rectangle2D.Double();
		viewer_stroke_bounds = new Rectangle2D.Double();
		
		raw_pixel_linear_stroke_path = new Path2D.Double();
		viewer_linear_stroke_path = new Path2D.Double();
		tile_linear_stroke_path = new Path2D.Double();
		
		raw_pixel_spline_stroke_path = new Path2D.Double();
		viewer_spline_stroke_path = new Path2D.Double();
		tile_spline_stroke_path = new Path2D.Double();
		
		raw_pixel_spline_stroke_points = new ArrayList<Point2D>();
		
	}

	public boolean isPenStrokeEmpty(){
		
		return pen_points.isEmpty();
		
	}
	
	public void addPenPoint(PenPoint pen_point){

		if(pen_points.isEmpty()){
			
			pattern_id = pen_point.getPatternId();
			pen_id = pen_point.getPenId();
			
			start_time = pen_point.getTime();
			
			pen_points.add(pen_point);
			
		}
		else{
			
			PenPoint previous_pen_point = pen_points.get(pen_points.size() - 1);
			
			if(previous_pen_point.getRawPixelX() == pen_point.getRawPixelX() && previous_pen_point.getRawPixelY() == pen_point.getRawPixelY()){
				
				previous_pen_point.setTime(pen_point.getTime());
				
			}
			else{
				
				pen_points.add(pen_point);
				
			}

		}
		
		end_time = pen_point.getTime();

	}
	
	public long getStrokeId(){
		
		return pen_stroke_id;
		
	}
	
	public String getPatternId(){
		
		return pattern_id;
		
	}
	
	public long getPenId(){
		
		return pen_id;
		
	}
	
	public List<PenPoint> getPenPoints(){
		
		return pen_points;
		
	}
	
	public LocalDateTime getStartTime(){
		
		return start_time;

	}
	
	public LocalDateTime getEndTime(){
		
		return end_time;
		
	}

	public long getDurationInSeconds(){
		
		return Duration.between(start_time, end_time).getSeconds();
		
	}
	
	public PenPoint getFirstPenPoint(){
		
		PenPoint first_pen_point = null;
		
		if(!pen_points.isEmpty()){
			
			first_pen_point = pen_points.get(0);
			
		}
		
		return first_pen_point;
		
	}
	
	public PenPoint getLastPenPoint(){
		
		PenPoint last_pen_point = null;
		
		if(!pen_points.isEmpty()){
			
			last_pen_point = pen_points.get(pen_points.size() - 1);
			
		}
		
		return last_pen_point;
		
	}

	public Rectangle2D getStrokeBounds(){

		return raw_pixel_stroke_bounds;
		
	}

	public Rectangle2D getViewerStrokeBounds(){

		return viewer_stroke_bounds;
		
	}
	
	public Path2D getLinearStrokePath(){
		
		return raw_pixel_linear_stroke_path;
		
	}
	
	public Path2D getViewerLinearStrokePath(){
		
		return viewer_linear_stroke_path;
		
	}
	
	public Path2D getTileLinearStrokePath(){
		
		return tile_linear_stroke_path;
		
	}

	public Path2D getSplineStrokePath(){
		
		return raw_pixel_spline_stroke_path;
		
	}

	public Path2D getViewerSplineStrokePath(){
		
		return viewer_spline_stroke_path;
		
	}
	
	public Path2D getTileSplineStrokePath(){
		
		return tile_spline_stroke_path;
		
	}
	
	public void setStrokeId(long stroke_id){
		
		pen_stroke_id = stroke_id;
		
	}
	
	public void setPatternId(String anoto_pattern_id){
		
		pattern_id = anoto_pattern_id;
		
	}
	
	public void setPenId(long pen_id){
		
		this.pen_id = pen_id;
		
	}
	
	public void setPenPoints(List<PenPoint> stroke_pen_points){
		
		pen_points = stroke_pen_points;
		
	}
	
	public void setStartTime(LocalDateTime start_time){
		
		this.start_time = start_time;
		
	}
	
	public void setEndTime(LocalDateTime end_time){
		
		this.end_time = end_time;
		
	}
	
	public void setStrokeBounds(int viewer_resize_factor){
		
		double min_x = Double.POSITIVE_INFINITY, max_x = Double.NEGATIVE_INFINITY;
		double min_y = Double.POSITIVE_INFINITY, max_y = Double.NEGATIVE_INFINITY;
		
		for(int i = 0; i < pen_points.size(); i++){
			
			PenPoint pen_point = pen_points.get(i);
			
			double pen_point_x = pen_point.getRawPixelX(), pen_point_y = pen_point.getRawPixelY();
			
			if(pen_point_x < min_x){
				
				min_x = pen_point_x;
				
			}
			
			if(pen_point_x > max_x){
				
				max_x = pen_point_x;
				
			}
			
			if(pen_point_y < min_y){
				
				min_y = pen_point_y;
				
			}
			
			if(pen_point_y > max_y){
				
				max_y = pen_point_y;
				
			}
			
		}
		
		raw_pixel_stroke_bounds = new Rectangle2D.Double(min_x, min_y, max_x - min_x, max_y - min_y);
		
		viewer_stroke_bounds = new Rectangle2D.Double(raw_pixel_stroke_bounds.getX() / viewer_resize_factor, raw_pixel_stroke_bounds.getY() / viewer_resize_factor, raw_pixel_stroke_bounds.getWidth() / viewer_resize_factor, raw_pixel_stroke_bounds.getHeight() / viewer_resize_factor);
		
	}
	
	public void setLinearStrokePath(int viewer_resize_factor, int tile_resize_factor){
		
		if(!pen_points.isEmpty()){
			
			raw_pixel_linear_stroke_path.reset();
			viewer_linear_stroke_path.reset();
			tile_linear_stroke_path.reset();
		
			raw_pixel_linear_stroke_path.moveTo(pen_points.get(0).getRawPixelX(), pen_points.get(0).getRawPixelY());
			viewer_linear_stroke_path.moveTo(pen_points.get(0).getRawPixelX() / viewer_resize_factor, pen_points.get(0).getRawPixelY() / viewer_resize_factor);
			tile_linear_stroke_path.moveTo(pen_points.get(0).getRawPixelX() / tile_resize_factor, pen_points.get(0).getRawPixelY() / tile_resize_factor);
			
			for(int i = 1; i < pen_points.size(); i++){

				raw_pixel_linear_stroke_path.lineTo(pen_points.get(i).getRawPixelX(), pen_points.get(i).getRawPixelY());
				viewer_linear_stroke_path.lineTo(pen_points.get(i).getRawPixelX() / viewer_resize_factor, pen_points.get(i).getRawPixelY() / viewer_resize_factor);
				tile_linear_stroke_path.lineTo(pen_points.get(i).getRawPixelX() / tile_resize_factor, pen_points.get(i).getRawPixelY() / tile_resize_factor);
				
			}
		
		}
		
	}
	
	public void setSplineStrokePath(int viewer_resize_factor, int tile_resize_factor){
		
		if(!pen_points.isEmpty()){
			
			raw_pixel_spline_stroke_path.reset();
			raw_pixel_spline_stroke_path = catmull_rom_spline.generateSplinePath(pen_points, interpolation_points_per_segment, curve_type);

			viewer_spline_stroke_path = new Path2D.Double();
			tile_spline_stroke_path = new Path2D.Double();
			
			List<Point2D> spline_points = catmull_rom_spline.getSplinePathPoints();
			raw_pixel_spline_stroke_points = spline_points;
			if(! spline_points.isEmpty()){
			
				viewer_spline_stroke_path.moveTo(spline_points.get(0).getX() / viewer_resize_factor, spline_points.get(0).getY() / viewer_resize_factor);
				tile_spline_stroke_path.moveTo(spline_points.get(0).getX() / tile_resize_factor, spline_points.get(0).getY() / tile_resize_factor);
				
				for(int i = 1; i < spline_points.size(); i++){

					viewer_spline_stroke_path.lineTo(spline_points.get(i).getX() / viewer_resize_factor, spline_points.get(i).getY() / viewer_resize_factor);
					tile_spline_stroke_path.lineTo(spline_points.get(i).getX() / tile_resize_factor, spline_points.get(i).getY() / tile_resize_factor);
					
				}
				
			}
			
		}
		
	}
	
	public ShapeRecognizer.penStrokeTypes getPenStrokeType(){
		
		return pen_stroke_type;
		
	}
	
	public void setPenStrokeType(ShapeRecognizer.penStrokeTypes pen_stroke_type){
		
		this.pen_stroke_type = pen_stroke_type;
		
	}
	
	public List<Point2D> getRawPixelSplineStrokePoints(){
		
		return raw_pixel_spline_stroke_points;
		
	}
	
	public void setIsInTextSpace(boolean is_in_text_space){
		
		this.is_in_text_space = is_in_text_space;
		
	}
	
	public boolean getIsInTextSpace(){
		
		return is_in_text_space;
		
	}
	
}

