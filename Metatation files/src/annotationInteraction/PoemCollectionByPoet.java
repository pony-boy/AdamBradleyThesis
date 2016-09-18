package annotationInteraction;

import java.util.List;

public class PoemCollectionByPoet {
	
	private String poet;
	private List<List<String>> poems;
	
	public PoemCollectionByPoet(String poet, List<List<String>> poems){
		
		this.poet = poet;
		this.poems = poems;
		
	}
	
	public String getPoet(){
		
		return poet;
		
	}
	
	public List<List<String>> getPoems(){
		
		return poems;
		
	}

}
