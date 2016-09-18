package annotationInteraction;

import java.awt.geom.Rectangle2D;

public class Poem {
	
	private Stanza poem_header;
	private PoemStanzas poem_stanzas;
	
	public Poem(Stanza header, PoemStanzas stanzas){
		
		poem_header = header;
		poem_stanzas = stanzas;
		
	}
	
	public Stanza getPoemHeader(){
		
		return poem_header;
		
	}
	
	public PoemStanzas getPoemStanzas(){
		
		return poem_stanzas;
		
	}

}
