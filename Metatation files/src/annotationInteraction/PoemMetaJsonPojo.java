package annotationInteraction;

import java.util.List;
import java.util.Map;

public class PoemMetaJsonPojo {

	public List<List<Map<String, String>>> poem_header;
	public List<List<List<Map<String, String>>>> stanzas;
	 
	public PoemMetaJsonPojo(List<List<Map<String, String>>> poem_header, List<List<List<Map<String, String>>>> poem_stanzas){
		
		this.poem_header = poem_header;
		stanzas = poem_stanzas;
		
	}
	
	public void setPoemHeader(List<List<Map<String, String>>> poem_header){
		
		this.poem_header = poem_header;
		
	}
	
}
