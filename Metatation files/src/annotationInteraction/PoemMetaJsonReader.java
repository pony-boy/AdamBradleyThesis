package annotationInteraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PoemMetaJsonReader {
	
	public PoemMetaJsonReader(String poem_file_details, Poem poem_content){
		
		BufferedReader poem_meta_json_reader = null;
		
		try {
			
			poem_meta_json_reader = new BufferedReader(new InputStreamReader(new FileInputStream(poem_file_details + ".json"), "UTF-8"));
			PoemMetaJsonPojo poem_meta_json = new Gson().fromJson(poem_meta_json_reader, PoemMetaJsonPojo.class);

			set_stanza_meta(poem_content.getPoemHeader(), poem_meta_json.poem_header);
			
			List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
			for(int i = 0; i < poem_stanzas.size(); i++){
				
				set_stanza_meta(poem_stanzas.get(i), poem_meta_json.stanzas.get(i));
				
			}
		
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			System.out.println("Exception when reading poem meta json \n");
			e.printStackTrace();
			
		}

	}
	
	private void set_stanza_meta(Stanza stanza_in_poem, List<List<Map<String, String>>> poem_stanza_meta){
		
		List<Line> lines_in_stanza = stanza_in_poem.getLines();
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			List<Word> words_in_line = lines_in_stanza.get(i).getWords();
			
			for(int j = 0; j < words_in_line.size(); j++){
				
				words_in_line.get(j).setPOS(poem_stanza_meta.get(i).get(j).get("POS"));
				
			}
			
		}
		
	}
	
}
