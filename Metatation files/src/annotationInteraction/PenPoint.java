package annotationInteraction;

import java.time.LocalDateTime;

public class PenPoint {

	public static double raw_pixel_max_x = 4958.0;
	public static double raw_pixel_max_y = 7017.0;
	private static double raw_min_x, raw_max_x, raw_min_y, raw_max_y;
	
	private String pattern_id;
	private long pen_id;

	private double raw_x, raw_y, intensity;
	private LocalDateTime time;
	private double raw_pixel_x, raw_pixel_y;
	
	public PenPoint(String pattern_id, long pen_id, double raw_x, double raw_y, LocalDateTime time){

		this.pattern_id = pattern_id;
		this.pen_id = pen_id;
		
		this.raw_x = raw_x;
		this.raw_y = raw_y;
		
		initialize_raw_pixel_x_y();
		
		this.time = time;
		this.intensity = 1.0;
		
	}

	public PenPoint(String pattern_id, long pen_id, double raw_x, double raw_y, LocalDateTime time, double intensity){

		this.pattern_id = pattern_id;
		this.pen_id = pen_id;

		this.raw_x = raw_x;
		this.raw_y = raw_y;
		
		initialize_raw_pixel_x_y();
		
		this.time = time;
		this.intensity = intensity;
		
	}
	
	private void initialize_raw_pixel_x_y(){
		
		initialize_raw_extents();
		raw_pixel_x = ((raw_x - raw_min_x) / (raw_max_x - raw_min_x)) * raw_pixel_max_x;
		raw_pixel_y = ((raw_y - raw_min_y) / (raw_max_y - raw_min_y)) * raw_pixel_max_y;
		
	}
	
	//TODO get these values for all 3 patterns
	private void initialize_raw_extents(){
		
		switch(pattern_id){
		
		case "70.0.5.3":
			/*
			 * Values for touch screen monitor
			raw_min_x = 0.1985755;
			raw_max_x = 0.8052115;
			raw_min_y = 0.19694227;
			raw_max_y = 0.81028885;
			*/
			
			/*
			 * Values for my personal laptop
			raw_min_x = 0.19823968;
			raw_max_x = 0.80565387;
			raw_min_y = 0.19727647;
			raw_max_y = 0.84729135;
			*/
			
			/*
			 * Values for single screen UOIT laptop 
			raw_min_x = 0.19891043;
			raw_max_x = 0.7938623;
			raw_min_y = 0.19811113;
			raw_max_y = 0.7976694;
			*/
			
			raw_min_x = 0.5455913;
			raw_max_x = 0.8912464;
			raw_min_y = 0.19969448;
			raw_max_y = 0.80685085;
			
			break;
			
		case "70.0.10.7":
			break;
			
		case "70.0.10.8":
			raw_min_x = 0.1947933;
			raw_max_x = 0.8109522;
			raw_min_y = 0.19372518;
			raw_max_y = 0.8200901;
			break;
			
		default:
			break;
			
		}
		
	}
	
	public String getPatternId(){
		
		return pattern_id;
		
	}
	
	public long getPenId(){
		
		return pen_id;
		
	}

	public double getRawX(){
		
		return raw_x;
		
	}
	
	public double getRawY(){
		
		return raw_y;
		
	}
	
	public double getRawPixelX(){
		
		//raw_pixel_x = ((raw_x - raw_min_x) / (raw_max_x - raw_min_x)) * raw_pixel_max_x;
		return raw_pixel_x;
		
	}
	
	public double getRawPixelY(){
		
		//raw_pixel_y = ((raw_y - raw_min_y) / (raw_max_y - raw_min_y)) * raw_pixel_max_y;
		return raw_pixel_y;
		
	}
	
	public LocalDateTime getTime(){
		
		return time;
		
	}
	
	public double getIntensity(){
		
		return intensity;
		
	}
	
	public void setTime(LocalDateTime time){
		
		this.time = time;
		
	}
	
}
