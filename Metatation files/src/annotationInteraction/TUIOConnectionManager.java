package annotationInteraction;

import TUIO.TuioClient;

public class TUIOConnectionManager {
	
private static int TUIO_PORT = 3333;
	
	private static TuioClient tuio_client;
	private static TUIOEventListener tuio_event_listener;
	private static boolean tuio_connection_secured = false;
	
	public TUIOConnectionManager(){
		
		tuio_client = new TuioClient(TUIO_PORT);
		tuio_event_listener = new TUIOEventListener();
		tuio_client.addTuioListener(tuio_event_listener);
		
	}
	
	//TODO remove this constructor when you get your own driver running
	public TUIOConnectionManager(String current_pattern_id){
		
		tuio_client = new TuioClient(TUIO_PORT);
		tuio_event_listener = new TUIOEventListener(current_pattern_id);
		tuio_client.addTuioListener(tuio_event_listener);
		
	}
	
	public void TuioConnect(){

		tuio_client.connect();
		tuio_connection_secured = true;
		
	}
	
	public void TuioDisconnect(){

		tuio_client.disconnect();
		
	}
	
	public boolean isTUIOConnectionEstablished(){
		
		return tuio_connection_secured;
		
	}

}
