package annotationInteraction;

import java.util.List;

public class PoemJsonPojo {

	private List<List<String>> poem_header;
	private List<List<List<String>>> stanzas;
	
	public List<List<String>> getPoemHeader(){
		
		return poem_header;
		
	}
	
	public void setPoemHeader(List<List<String>> header){
		
		poem_header = header;
		
	}
	
	public List<List<List<String>>> getStanzas(){
		
		return stanzas;
		
	}
	
	public void setStanzas(List<List<List<String>>> poem_stanzas){
		
		stanzas = poem_stanzas;
		
	}

	
}
