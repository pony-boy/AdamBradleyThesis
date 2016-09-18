package annotationInteraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PoemCollectionJsonReader {
	
	private static String poem_collection_file_path = "res/poem_collection/";
	private static String poem_collection_file_name = "poem_collection.json";
	
	public static List<PoemCollectionByPoet> retrievePoemCollection(){
		
		List<PoemCollectionByPoet> poem_collection = new ArrayList<PoemCollectionByPoet>();
		
		BufferedReader poem_collection_file_reader = null;
		
		try {
			
			poem_collection_file_reader = new BufferedReader(new InputStreamReader(new FileInputStream(poem_collection_file_path + poem_collection_file_name), "UTF-8"));

			HashMap<String, List<List<String>>> poems_from_json = new Gson().fromJson(poem_collection_file_reader, new TypeToken<HashMap<String, List<List<String>>>>() {}.getType());
			for (Map.Entry<String, List<List<String>>> poet_entry : poems_from_json.entrySet()) {
				
				poem_collection.add(new PoemCollectionByPoet(poet_entry.getKey(), poet_entry.getValue()));

			}
		
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			System.out.println("Exception when reading poem collection \n");
			e.printStackTrace();
			
		}
		
		return poem_collection;
		
	}

}
