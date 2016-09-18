package annotationInteraction;

import java.time.LocalDateTime;
import java.time.ZoneId;

import TUIO.TuioBlob;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

public class TUIOEventListener  implements TuioListener{
	
	private String current_pattern_id;
	
	public TUIOEventListener(){
		
		super();
		
	}
	
	//TODO remove this constructor when you get your own driver running
	public TUIOEventListener(String pattern_id){
		
		super();
		
		current_pattern_id = pattern_id;
		
	}
		
	private PenPoint generate_pen_point(TuioCursor tuio_cursor){
		
		//TODO remove this initialization when you get your own driver running and instead use the value returned by the driver
		String pattern_id = current_pattern_id;
		long pen_id = tuio_cursor.getSessionID();
		
		double raw_x = tuio_cursor.getX(), raw_y = tuio_cursor.getY();
		
		LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));

		double pen_intensity = 1.0;
		
		return (new PenPoint(pattern_id, pen_id, raw_x, raw_y, time, pen_intensity));
		
	}

	//PenDown Event
	@Override
	public void addTuioCursor(TuioCursor tcur) {
		
		WorksheetManager.updatePenStroke(generate_pen_point(tcur));
		
	}

	//PenDrag Event
	@Override
	public void updateTuioCursor(TuioCursor tcur) {

		WorksheetManager.updatePenStroke(generate_pen_point(tcur));
		
	}

	//PenUp Event
	@Override
	public void removeTuioCursor(TuioCursor tcur) {

		WorksheetManager.newPenStroke(generate_pen_point(tcur));
		
	}
	
	/*
	 * These methods not applicable
	 */
	
	@Override
	public void addTuioObject(TuioObject tobj) {

	}

	@Override
	public void updateTuioObject(TuioObject tobj) {
		
	}

	@Override
	public void removeTuioObject(TuioObject tobj) {

	}

	@Override
	public void addTuioBlob(TuioBlob tblb) {

	}

	@Override
	public void updateTuioBlob(TuioBlob tblb) {

	}

	@Override
	public void removeTuioBlob(TuioBlob tblb) {

	}

	@Override
	public void refresh(TuioTime ftime) {
		
	}

}
