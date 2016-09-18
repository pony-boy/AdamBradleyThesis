package annotationInteraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.gson.Gson;

public class SonnetsRelatedWordPairsJsonReader {
	
	//private static String poem_file_details = "C:/Users/Hrim/Documents/MetaTationQueryTilesExtension/sonnets/sonnets_related_word_pairs_all_details_java_readable";
	private static String poem_file_details = "C:/Users/100535571/Documents/MetaTationQueryTilesExtension/sonnets/sonnets_related_word_pairs_all_details_java_readable";
	private SonnetsRelatedWordPairsJsonPojo poem_meta_json;
	
	public SonnetsRelatedWordPairsJsonReader (){
		
		BufferedReader poem_meta_json_reader = null;
		
		try {
			
			poem_meta_json_reader = new BufferedReader(new InputStreamReader(new FileInputStream(poem_file_details + ".json"), "UTF-8"));
			poem_meta_json = new Gson().fromJson(poem_meta_json_reader, SonnetsRelatedWordPairsJsonPojo.class);
		
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			System.out.println("Exception when reading poem meta json \n");
			e.printStackTrace();
			
		}

	}
	
	public SonnetsRelatedWordPairsJsonPojo getSonnetsRelatedWordPairsPojo(){
		
		return poem_meta_json;
		
	}

}
