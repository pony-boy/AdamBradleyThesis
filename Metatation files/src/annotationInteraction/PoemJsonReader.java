package annotationInteraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;

public class PoemJsonReader {
	
	private static String poem_json_file_path = "res/poem_collection/";
	
	public static PoemJsonPojo retrievePoemContent(String poem_json_file_name){
		
		PoemJsonPojo poem_content = null;
		
		BufferedReader poem_json_file_reader = null;
		try {
			
			poem_json_file_reader = new BufferedReader(new InputStreamReader(new FileInputStream(poem_json_file_path + poem_json_file_name + ".json"), "UTF-8"));
			poem_content = new Gson().fromJson(poem_json_file_reader, PoemJsonPojo.class);
			
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			System.out.println("Exception when reading poem file \n");
			e.printStackTrace();
			
		}
		
		return poem_content;
		
	}

}
