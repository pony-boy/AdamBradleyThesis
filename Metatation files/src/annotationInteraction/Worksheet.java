package annotationInteraction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worksheet {
	
	//private static String script_path = "C:/Users/Hrim/Documents/queryFramework-0.1/";
	private static String script_path = "queryFramework-0.1/";
	private static String script_name = "new_query_receiver.py";
	//public static String query_response_file_path = "C:/Users/Hrim/Documents/queryFramework-0.1/query_responses/";
	public static String query_response_file_path = "queryFramework-0.1/query_responses/";
	//private static String poem_meta_json_file_path = "C:/Users/Hrim/Documents/Eclipse/MS/textProcessing-0.1/res/poem_collection/meta_json/";
	private static String poem_meta_json_file_path = "res/poem_collection/";
	
	private long worksheet_id;
	
	private static String worksheet_tif_file_path = "worksheets/";
	private String worksheet_tif_file_name = "worksheet ";
	private BufferedImage worksheet_tif;
	private TIFReaderWriter worksheet_tif_reader_writer;
	
	private String anoto_pattern_id;
	private Poem worksheet_content;
	
	private List<PenStroke> completed_pen_strokes;
	private PenStroke incomplete_pen_stroke;
	private List<ClusterGenerator> clusters_per_completed_pen_stroke;
	
	private PenStrokeXML pen_stroke_xml;
	
	private WorksheetViewer worksheet_viewer;
	private WorksheetViewer2 worksheet_viewer_2;
	private MetaViewer meta_viewer;
	
	private ShapeRecognizer pen_stroke_type_recognizer;
	private QueryGenerator pen_stroke_query_client;
	
	private List<List<QueryDetails>> queries_per_completed_pen_stroke;
	
	private ExecutorService query_response_executor_service;
	private Map<Long, List<QueryResponseReceived>> query_response_pen_stroke_ids_map;
	private List<QueryResponseReceived> recent_query_responses;
	
	private Poem worksheet_content_in_tile;
	
	private QueryWordDefinitionResponseXML word_definition_xml_reader;
	private Map<String, WordDefinition> word_definitions_xml;
	private boolean word_definitions_xml_read;
	private List<WordDefinitionTile> word_definitions_fetched;
	private List<WordDefinitionViewer> word_defintion_viewers_fetched;
	
	private Map<Long, List<WordDefinitionTile>> word_definitions_pen_stroke_id_map;
	private Map<Long, List<WordDefinitionViewer>> word_definition_viewers_pen_stroke_id_map;
	
	private Map<Long, List<String>> pen_stroke_id_annotated_words_map;
	private Map<Long, List<Integer>> pen_stroke_id_query_response_types_map;
	
	public Worksheet(long id, String pattern_id, Poem content, BufferedImage tif){
		
		worksheet_id = id;
		
		anoto_pattern_id = pattern_id;
		worksheet_content = content;
		
		worksheet_tif = tif;
		worksheet_tif_file_name += "" + worksheet_id;
		worksheet_tif_reader_writer = new TIFReaderWriter(worksheet_tif_file_path, worksheet_tif_file_name);
		
		completed_pen_strokes = new ArrayList<PenStroke>();
		incomplete_pen_stroke = new PenStroke(0);
		clusters_per_completed_pen_stroke = new ArrayList<ClusterGenerator>();
		
		pen_stroke_xml = new PenStrokeXML(worksheet_id);
		
		worksheet_viewer = null;
		worksheet_viewer_2 = null;
		
		pen_stroke_type_recognizer = new ShapeRecognizer(worksheet_content);
		pen_stroke_query_client = new QueryGenerator(worksheet_id, worksheet_content);
		
		queries_per_completed_pen_stroke = new ArrayList<List<QueryDetails>>();
		query_response_executor_service = Executors.newCachedThreadPool();
		
		query_response_pen_stroke_ids_map = new HashMap<Long, List<QueryResponseReceived>>();
		recent_query_responses = new ArrayList<QueryResponseReceived>();
		
		worksheet_content_in_tile = poem_content_in_tile_viewer();
		
		List<Line> header_lines = worksheet_content.getPoemHeader().getLines();
		String word_definition_file_name = header_lines.get(1).getLine() + " # " + header_lines.get(0).getLine() + " - Processed";
		word_definition_xml_reader = new QueryWordDefinitionResponseXML(worksheet_id, word_definition_file_name);
		
		word_definitions_xml = new HashMap<String, WordDefinition>();
		word_definitions_xml_read = false;
		
		word_definitions_pen_stroke_id_map = new HashMap<Long, List<WordDefinitionTile>>();
		word_definitions_fetched = new ArrayList<WordDefinitionTile>();
		
		word_definition_viewers_pen_stroke_id_map = new HashMap<Long, List<WordDefinitionViewer>>();
		word_defintion_viewers_fetched = new ArrayList<WordDefinitionViewer>();
		
		pen_stroke_id_query_response_types_map = new HashMap<Long, List<Integer>>();
		pen_stroke_id_annotated_words_map = new HashMap<Long, List<String>>();
		
		new POSTagger(worksheet_content);
		//new PoemMetaJsonReader(poem_meta_json_file_path + header_lines.get(1).getLine() + " # " + header_lines.get(0).getLine(), worksheet_content);
		
	}
	
	public long getWorksheetId(){
		
		return worksheet_id;
		
	}
	
	public String getAnotoPatternId(){
		
		return anoto_pattern_id;
		
	}
	
	public Poem getWorksheetContent(){
		
		return worksheet_content;
		
	}
	
	public List<PenStroke> getPenStrokes(){
		
		return completed_pen_strokes;
		
	}
	
	public PenStroke getIncompletePenStroke(){
		
		return incomplete_pen_stroke;
		
	}
	
	public List<ClusterGenerator> getClusterGenerators(){
		
		return clusters_per_completed_pen_stroke;
		
	}
	
	public ClusterGenerator getLastGeneratedClusters(){
		
		if(clusters_per_completed_pen_stroke.size() > 0){
			
			return clusters_per_completed_pen_stroke.get(clusters_per_completed_pen_stroke.size() - 1);
			
		}
		else{
			
			return null;
			
		}
		
	}
	
	public void setPenStrokes(){

		completed_pen_strokes = pen_stroke_xml.retrievePenStrokeXML();
		incomplete_pen_stroke = new PenStroke(completed_pen_strokes.size());
		
	}
	
	public void setWorksheetViewer(WorksheetViewer viewer){
		
		worksheet_viewer = viewer;
		
	}
	
	public void setWorksheetViewer2(WorksheetViewer2 viewer){
		
		worksheet_viewer_2 = viewer;
		
	}
	
	public void setMetaViewer(MetaViewer viewer){
		
		meta_viewer = viewer;
		
	}
	
	public void updatePenStroke(PenPoint new_pen_point){

		incomplete_pen_stroke.addPenPoint(new_pen_point);
		incomplete_pen_stroke.setLinearStrokePath(WorksheetViewer.resize_factor, Tile.resize_factor);
		if(worksheet_viewer != null){
		
			worksheet_viewer.updateStrokeViewer();
			
		}
		
	}

	public void newPenStroke(PenPoint new_pen_point){
		
		incomplete_pen_stroke.setStrokeBounds(WorksheetViewer.resize_factor);
		incomplete_pen_stroke.setLinearStrokePath(WorksheetViewer.resize_factor, Tile.resize_factor);
		incomplete_pen_stroke.setSplineStrokePath(WorksheetViewer.resize_factor, Tile.resize_factor);
		
		set_is_pen_stroke_on_text(incomplete_pen_stroke);
		completed_pen_strokes.add(incomplete_pen_stroke);
		//TODO uncomment this after testing
		pen_stroke_xml.updatePenStrokeXML(incomplete_pen_stroke);
		List<PenStroke> completed_pen_strokes_to_process = get_pen_strokes_to_cluster(completed_pen_strokes);
		ClusterGenerator pen_stroke_cluster_generator = new ClusterGenerator(completed_pen_strokes_to_process, WorksheetViewer.resize_factor);
		clusters_per_completed_pen_stroke.add(pen_stroke_cluster_generator);
		incomplete_pen_stroke.setPenStrokeType(ShapeRecognizer.get_pen_stroke_type(incomplete_pen_stroke));
		System.out.println(incomplete_pen_stroke.getPenStrokeType());
		List<QueryDetails> pen_stroke_queries = pen_stroke_query_client.generateQueriesAtTheEndOfPenStroke(getLastGeneratedClusters(), completed_pen_strokes_to_process, incomplete_pen_stroke);
		System.out.println(pen_stroke_queries.size());
		for(int i = 0; i < pen_stroke_queries.size(); i++){
			
			QueryDetails pen_stroke_query = pen_stroke_queries.get(i);
			
			String[] query_id_split = pen_stroke_query.query_id.split("#");
			String[] pen_strokes_in_query_id = pen_stroke_query.query_id.split("#")[1].substring(1, query_id_split[1].length() - 1).split("-");
			String[] query_content_pen_stroke_split = pen_stroke_query.query_content.split("#");
			for(int j = 0 ; j < pen_strokes_in_query_id.length; j++){
				
				Long pen_stroke_id = Long.parseLong(pen_strokes_in_query_id[j]);
				
				String[] pen_stroke_words = query_content_pen_stroke_split[j].substring(1, query_content_pen_stroke_split[j].length() - 1).split(" ");
				
				List<String> previous_annotated_words = pen_stroke_id_annotated_words_map.get(pen_stroke_id);
				if(previous_annotated_words == null){
					
					previous_annotated_words = new ArrayList<String>();
					
				}
				
				for(int k = 0; k < pen_stroke_words.length; k++){
					
					String pen_stroke_word = pen_stroke_words[k].substring(1, pen_stroke_words[k].length() - 1);
					if(!previous_annotated_words.contains(pen_stroke_word)){
						
						previous_annotated_words.add(pen_stroke_word);
						System.out.println(pen_stroke_word);
						
					}
					
				}
				
				pen_stroke_id_annotated_words_map.put(pen_stroke_id, previous_annotated_words);
				
			}
			
		}
		
		if(!word_definitions_xml_read){
			
			//TODO use query content here to eliminate repeated word definition queries
			start_word_definiton_queries(pen_stroke_queries);
			
		}
		else{
			
			for(int i = 0; i < pen_stroke_queries.size(); i++){
				
				QueryDetails pen_stroke_query = pen_stroke_queries.get(i);
				process_word_definitions(pen_stroke_query.query_id, pen_stroke_query.query_content);
				
			}
			
		}
		
		pen_stroke_queries = eliminate_similar_queries(pen_stroke_queries);
		queries_per_completed_pen_stroke.add(pen_stroke_queries);
		start_queries(pen_stroke_queries);
		
		incomplete_pen_stroke = new PenStroke(completed_pen_strokes.size());
		if(worksheet_viewer != null){
		
			worksheet_viewer.updateStrokeViewer();
			
		}
		else{
			
			worksheet_viewer_2.updateViewer(pen_stroke_queries);
			
		}
		
	}
	
	private List<QueryDetails> eliminate_similar_queries(List<QueryDetails> new_queries){
		
		List<QueryDetails> pen_stroke_queries =  new_queries;
		
		Iterator<QueryDetails> pen_stroke_queries_iterator = pen_stroke_queries.iterator();
		
		while(pen_stroke_queries_iterator.hasNext()){
			
			String new_query_id = pen_stroke_queries_iterator.next().query_id;
			String[] new_query_id_split = new_query_id.split("#");
			boolean same_as_prior_query = false;
			
			for(int i = 0; i < queries_per_completed_pen_stroke.size(); i++){
				
				List<QueryDetails> queries_per_pen_stroke = queries_per_completed_pen_stroke.get(i);
				
				for(int j = 0; j < queries_per_pen_stroke.size(); j++){
					
					String query_id = queries_per_pen_stroke.get(j).query_id;
					String[] query_id_split = query_id.split("#"); 
					
					if(query_id_split.length == new_query_id_split.length){
						
						boolean all_same_elements = true;
						for(int m = 1; m < query_id_split.length; m++){
							
							if(!query_id_split[m].equals(new_query_id_split[m])){
								
								all_same_elements = false;
								break;
								
							}
							
						}
						
						if(all_same_elements){
							
							same_as_prior_query = true;
							System.out.println(new_query_id + " not issued");
							pen_stroke_queries_iterator.remove();
							break;
							
						}
						
					}
					
				}
				
				if(same_as_prior_query){
					
					break;
					
				}
				
			}
			
		}
		
		return pen_stroke_queries;
		
	}
	
	private void start_queries(List<QueryDetails> queries){

		Process query_process;
		Runtime runtime = Runtime.getRuntime();
		
		List<Line> header_lines = worksheet_content.getPoemHeader().getLines();
		String poem_details = header_lines.get(1).getLine() + " # " + header_lines.get(0).getLine();
		
		for(int i = 0; i < queries.size(); i++){
			
			QueryDetails query = queries.get(i);
			try {
				
				String command =  "python " + script_path + script_name + " \"" + poem_details + "\" \"" + query.query_id + "\" \"" + query.query_content +"\"";
				query_process = runtime.exec(command);
				System.out.println(command);
				
				String query_response_file_name = query.query_id;
				Runnable query_response_checker = new QueryResponseCheckRunnable(query_response_file_path, query_response_file_name, query.query_content, Worksheet.this);
				query_response_executor_service.execute(query_response_checker);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void start_word_definiton_queries(List<QueryDetails> queries){
		
		List<Line> header_lines = worksheet_content.getPoemHeader().getLines();
		String word_definition_file_name = header_lines.get(1).getLine() + " # " + header_lines.get(0).getLine() + " - Processed";
		
		for(int i = 0; i < queries.size(); i++){
		
			QueryDetails query = queries.get(i);
			Runnable word_definition_response_checker = new QueryWordDefinitionResponseCheckRunnable(word_definition_file_name, query.query_id, query.query_content, Worksheet.this);
			query_response_executor_service.execute(word_definition_response_checker);
			
		}
		
	}
	
	private void process_word_definitions(String query_id, String query_content){
		
		//System.out.println("In process word definitons");
		String[] query_id_split = query_id.split("#");
		
		//Long query_generated_at_the_end_of_pen_stroke_id = Long.parseLong("" + query_id_split[0].split("-")[1].charAt(0));
		
		String[] pen_strokes_in_query_id = query_id_split[1].substring(1, query_id_split[1].length() - 1).split("-");
		List<Long> pen_strokes_generating_query = new ArrayList<Long>();
		for(int i = 0 ; i < pen_strokes_in_query_id.length; i++){
			
			pen_strokes_generating_query.add(Long.parseLong(pen_strokes_in_query_id[i]));
			
		}
		
		boolean query_previously_parsed = false;
		if(query_id_split.length == 3){
			
			query_previously_parsed = true;
			//pen_strokes_generating_query.add(Long.parseLong(query_id_split[2].substring(1, query_id_split[2].length() - 1)));
			
		}
		
		if(!query_previously_parsed){
		
			System.out.println("not parsed previously");
			String[] query_content_pen_stroke_split = query_content.split("#");
			for(int i = 0; i < pen_strokes_generating_query.size(); i++){
				
				Long pen_stroke_id = pen_strokes_generating_query.get(i);
				//List<WordDefinitionTile> word_definition_tiles = word_definitions_pen_stroke_id_map.get(pen_stroke_id);
				List<WordDefinitionViewer> word_definition_viewers = word_definition_viewers_pen_stroke_id_map.get(pen_stroke_id);
				if(word_definition_viewers == null){
				//if(word_definition_tiles == null){
					
					//System.out.println("missing entries for pen stroke");
					//word_definition_tiles = new ArrayList<WordDefinitionTile>();
					word_definition_viewers = new ArrayList<WordDefinitionViewer>();
					
					String[] pen_stroke_words = query_content_pen_stroke_split[i].substring(1, query_content_pen_stroke_split[i].length() - 1).split(" ");
					for(int j = 0; j < pen_stroke_words.length; j ++){
						
						String[] pen_stroke_word_info = pen_stroke_words[j].substring(1, pen_stroke_words[j].length() - 1).split("\\|");
						String pen_stroke_word = pen_stroke_word_info[3];
						String pen_stroke_word_pos_in_poem = worksheet_content.getPoemStanzas().getStanzas().get(Integer.parseInt(pen_stroke_word_info[0])).getLines().get(Integer.parseInt(pen_stroke_word_info[1])).getWords().get(Integer.parseInt(pen_stroke_word_info[2])).getPOS();
						//System.out.println(pen_stroke_word);
						//TODO check to see if previously definition of this word fetched
						/*WordDefinitionTile definition_tile_generated = null;
						boolean has_been_fetched = false;
						for(int m = 0; m < word_definitions_fetched.size(); m++){
							
							WordDefinitionTile word_definition_tile = word_definitions_fetched.get(m);
							if(word_definition_tile.getWordDefinition().getWord().equals(pen_stroke_word)){
								
								has_been_fetched = true;
								definition_tile_generated = word_definition_tile;
								break;
								
							}
							
						}
						
						if(has_been_fetched){
							
						}*/
						
						WordDefinition pen_stroke_word_definition = word_definitions_xml.get(pen_stroke_word);
						
						if(pen_stroke_word_definition != null){
							
							//System.out.println("found definitons for word");
							//TODO set filter of the tile here if clicked
							//word_definition_tiles.add(new WordDefinitionTile(pen_stroke_word_definition, pen_stroke_word_pos_in_poem));
							
							word_definition_viewers.add(new WordDefinitionViewer(pen_stroke_word_definition, pen_stroke_word_pos_in_poem));
							
						}
						
					}
					
					//if(!word_definition_tiles.isEmpty()){
					if(!word_definition_viewers.isEmpty()){
						
						//word_definitions_pen_stroke_id_map.put(pen_stroke_id, word_definition_tiles);
						//meta_viewer.updateMetaViewer(word_definition_tiles);
						
						word_definition_viewers_pen_stroke_id_map.put(pen_stroke_id, word_definition_viewers);
						meta_viewer.updateMetaViewer(word_definition_viewers);
						
						//TODO send info to worksheet viewer that def query retrieved
						
						List<Integer> previous_query_response_types = pen_stroke_id_query_response_types_map.get(pen_stroke_id); 
						if(previous_query_response_types == null){
							
							previous_query_response_types = new ArrayList<Integer>();
							
						}
						
						if(!previous_query_response_types.contains(7)){
							
							previous_query_response_types.add(7);
							
						}
						
						pen_stroke_id_query_response_types_map.put(pen_stroke_id, previous_query_response_types);
						
					}
					
				}
				
			}
			
		}
		
		worksheet_viewer_2.updateViewer();
		
	}
	
	public void saveWorksheetTIF(){
		
		Graphics2D g2d = worksheet_tif.createGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		
		double width = 1.2;
		g2d.setStroke(new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g2d.setColor(Color.BLUE);
		
		for(int i = 0; i < completed_pen_strokes.size(); i++){
			
		    g2d.draw(completed_pen_strokes.get(i).getLinearStrokePath());
		  //g2d.draw(completed_pen_strokes.get(i).getSplineStrokePath());
			
		}
	    
	    worksheet_tif_reader_writer.writeTIF(worksheet_tif);
		
	}
	
	//TODO set properties of word here
	public void notifyDefinitionsAvailable(String query_id, String query_content){
		
		if(word_definitions_xml.isEmpty()){
		
			word_definitions_xml = word_definition_xml_reader.retrieveWordDefinitions();
			word_definitions_xml_read = true;

		}
		process_word_definitions(query_id, query_content);
		
	}
	
	public void notifyResponseReceived(String query_id, String query_content){
		
		processQueryResponse(query_id, query_content);
		
	}
	
	//TODO generate tile
	public void processQueryResponse(String query_id, String query_content){
		
		//System.out.println("query response received");
		
		String[] query_id_split = query_id.split("#");
		
		Long query_generated_at_the_end_of_pen_stroke_id = Long.parseLong("" + query_id_split[0].split("-")[1].charAt(0));
		
		String[] pen_strokes_in_query_id = query_id_split[1].substring(1, query_id_split[1].length() - 1).split("-");
		List<Long> pen_strokes_generating_query = new ArrayList<Long>();
		for(int i = 0 ; i < pen_strokes_in_query_id.length; i++){
			
			pen_strokes_generating_query.add(Long.parseLong(pen_strokes_in_query_id[i]));
			
		}
		
		if(query_id_split.length == 3){
			
			pen_strokes_generating_query.add(Long.parseLong(query_id_split[2].substring(1, query_id_split[2].length() - 1)));
			
		}
		
		QueryResponseReceived query_response = new QueryResponseReceivedXML(worksheet_id, query_id, query_content, Worksheet.this).retrieveQueryResponse();
		query_response.setPenStrokesGeneratingQuery(pen_strokes_generating_query);
		query_response.setPenStrokeAtTheEndOfWhichQueryGenerated(query_generated_at_the_end_of_pen_stroke_id);
		List<PenStroke> completed_pen_strokes_on_query = new ArrayList<PenStroke>();
		for(int i = 0; i < query_generated_at_the_end_of_pen_stroke_id.intValue() + 1; i++){
			
			completed_pen_strokes_on_query.add(completed_pen_strokes.get(i));
			
		}
		//query_response.setCompletedPenStrokes(completed_pen_strokes.subList(0, query_generated_at_the_end_of_pen_stroke_id.intValue() + 1));
		query_response.setCompletedPenStrokes(completed_pen_strokes_on_query);
		query_response.setPoemContent(worksheet_content_in_tile);
		//TODO set filter here if pen clicked
		query_response.generateTiles();
		
		for(int i = 0 ; i < pen_strokes_generating_query.size(); i++){
			
			Long pen_stroke_id = pen_strokes_generating_query.get(i);
			List<QueryResponseReceived> previous_query_response = query_response_pen_stroke_ids_map.get(pen_stroke_id);
			if(previous_query_response == null){
				
				previous_query_response = new ArrayList<QueryResponseReceived>();
				//System.out.println("creating entry in map " + pen_stroke_id);
				
			}
			
			previous_query_response.add(query_response);
		
			//System.out.println("value added to entry");
			query_response_pen_stroke_ids_map.put(pen_stroke_id, previous_query_response);
			//System.out.println("entry added to map");
			
			List<Integer> previous_query_response_types = pen_stroke_id_query_response_types_map.get(pen_stroke_id); 
			if(previous_query_response_types == null){
				
				previous_query_response_types = new ArrayList<Integer>();
				
			}
			
			List<Boolean> query_response_types = query_response.getQueryTypesReceived();
			System.out.println(query_response_types.size());
			for(int m = 0; m < query_response_types.size(); m++){
				
				if(query_response_types.get(m)){
					
					if(!previous_query_response_types.contains(m)){
						
						previous_query_response_types.add(m);
						
					}
					
				}
				
			}
			
			pen_stroke_id_query_response_types_map.put(pen_stroke_id, previous_query_response_types);
			
		}
		
		recent_query_responses = new ArrayList<QueryResponseReceived>();
		recent_query_responses.add(query_response);
		meta_viewer.updateMetaViewer(query_response);
		
		//TODO call to worksheet viewer to notify of new QueryResponseReceived
		worksheet_viewer_2.updateViewer();
		
	}
	
	public List<QueryResponseReceived> getQueryResponses(){
		
		return recent_query_responses;
		
	}
	
	public ExecutorService getQueryResponseExecutorService(){
		
		return query_response_executor_service;
		
	}
	
	private Poem poem_content_in_tile_viewer(){

		int resize_factor = Tile.resize_factor;
		Stanza poem_header_viewer = stanza_content_in_tile_viewer(worksheet_content.getPoemHeader(), resize_factor);
		
		List<Stanza> poem_stanzas_in_viewer = new ArrayList<Stanza>();
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			poem_stanzas_in_viewer.add(stanza_content_in_tile_viewer(poem_stanzas.get(i), resize_factor));
			
		}
		
		Rectangle2D stanza_in_poem_bounds = worksheet_content.getPoemStanzas().getRawPixelBounds();
		Rectangle2D stanza_bounds_viewer = new Rectangle2D.Double(stanza_in_poem_bounds.getX() / resize_factor, stanza_in_poem_bounds.getY() / resize_factor, stanza_in_poem_bounds.getWidth() / resize_factor, stanza_in_poem_bounds.getHeight() / resize_factor);

		return new Poem(poem_header_viewer, new PoemStanzas(poem_stanzas_in_viewer, stanza_bounds_viewer));
		
	}
	
	private Stanza stanza_content_in_tile_viewer(Stanza stanza_in_poem, int resize_factor){
		
		List<Line> lines_in_stanza_viewer = new ArrayList<Line>();
		
		List<Line> lines_in_stanza = stanza_in_poem.getLines();
		for(int i = 0; i < lines_in_stanza.size(); i++){
			
			lines_in_stanza_viewer.add(line_content_in_tile_viewer(lines_in_stanza.get(i), resize_factor));
			
		}
		
		Rectangle2D stanza_in_poem_bounds = stanza_in_poem.getRawPixelBounds();
		Rectangle2D stanza_bounds_viewer = new Rectangle2D.Double(stanza_in_poem_bounds.getX() / resize_factor, stanza_in_poem_bounds.getY() / resize_factor, stanza_in_poem_bounds.getWidth() / resize_factor, stanza_in_poem_bounds.getHeight() / resize_factor);
		
		return new Stanza(lines_in_stanza_viewer, stanza_bounds_viewer);
		
	}
	
	private Line line_content_in_tile_viewer(Line line_in_stanza, int resize_factor){
		
		List<Word> words_in_line_viewer = new ArrayList<Word>();
		
		List<Word> words_in_line = line_in_stanza.getWords();
		for(int i = 0 ; i < words_in_line.size(); i++){
			
			Word word_in_line = words_in_line.get(i);
			List<Rectangle2D> characters_in_word_bounds = word_in_line.getCharactersRawPixelBounds(); 
			List<Point2D> characters_in_word_locations = word_in_line.getCharactersTextLayoutLocations();
			
			List<String> characters_in_word = word_in_line.getCharacters();
			List<Rectangle2D> characters_in_word_bounds_viewer = new ArrayList<Rectangle2D>();
			List<Point2D> characters_in_word_locations_viewer = new ArrayList<Point2D>();
			
			for(int j = 0; j < characters_in_word.size(); j++){
				
				Rectangle2D character_in_word_bounds = characters_in_word_bounds.get(j);
				Rectangle2D character_bounds_viewer = new Rectangle2D.Double(character_in_word_bounds.getX() / resize_factor, character_in_word_bounds.getY() / resize_factor, character_in_word_bounds.getWidth() / resize_factor, character_in_word_bounds.getHeight() / resize_factor);
				characters_in_word_bounds_viewer.add(character_bounds_viewer);
				
				Point2D character_location = characters_in_word_locations.get(j);
				characters_in_word_locations_viewer.add(new Point2D.Double(character_location.getX() / resize_factor, character_location.getY() / resize_factor));
				
			}
			
			Rectangle2D word_in_line_bounds = word_in_line.getRawPixelBounds();
			Rectangle2D word_bounds_viewer = new Rectangle2D.Double(word_in_line_bounds.getX() / resize_factor, word_in_line_bounds.getY() / resize_factor, word_in_line_bounds.getWidth() / resize_factor, word_in_line_bounds.getHeight() / resize_factor);
			words_in_line_viewer.add(new Word(word_in_line.getWord(), word_bounds_viewer, characters_in_word_bounds_viewer, characters_in_word_locations_viewer, word_in_line.getPOS()));
			
		}
		
		Rectangle2D line_in_stanza_bounds = line_in_stanza.getRawPixelBounds();
		Rectangle2D line_bounds_viewer = new Rectangle2D.Double(line_in_stanza_bounds.getX() / resize_factor, line_in_stanza_bounds.getY() / resize_factor, line_in_stanza_bounds.getWidth() / resize_factor, line_in_stanza_bounds.getHeight() / resize_factor);
		
		return new Line(line_in_stanza.getLine(), line_bounds_viewer, words_in_line_viewer);
		
	}
	
	//TODO add code to check if pen stroke hover happened and send cluster coordinates to viewer
	
	//TODO add code to check if pen stroke click through pen tap on paper happened
	
	public void onPenClickOnPenStrokeInViewer(Long pen_stroke_clicked_id, boolean ignore_id){
		
		List<QueryResponseReceived> response_already_processed = new ArrayList<QueryResponseReceived>();
		
		for (Map.Entry<Long, List<QueryResponseReceived>> pen_stroke_entry : query_response_pen_stroke_ids_map.entrySet()) {
			
			List<QueryResponseReceived> query_responses = pen_stroke_entry.getValue();
			
			if(ignore_id){
				
				filter_response_tiles(query_responses, false, response_already_processed);
				
			}
			else{
				
				if(pen_stroke_clicked_id == pen_stroke_entry.getKey()){
					
					response_already_processed.addAll(filter_response_tiles(query_responses, false, response_already_processed));
					
				}
				else{
					
					//response_already_processed.addAll(filter_response_tiles(query_responses, true, response_already_processed));
					filter_response_tiles(query_responses, true, response_already_processed);
					
				}
				
			}

		}
		
		//TODO add code to filter out a response only once
		
		/*for (Map.Entry<Long, List<WordDefinitionTile>> pen_stroke_entry : word_definitions_pen_stroke_id_map.entrySet()) {
			
			List<WordDefinitionTile> word_definition_tiles = pen_stroke_entry.getValue();
			
			if(ignore_id){
				
				filter_word_definition_tiles(word_definition_tiles, false);
				
			}
			else{
				
				if(pen_stroke_clicked_id == pen_stroke_entry.getKey()){
					
					filter_word_definition_tiles(word_definition_tiles, false);
					
				}
				else{
					
					filter_word_definition_tiles(word_definition_tiles, true);
					
				}
				
			}
			
		}*/
		
		for (Map.Entry<Long, List<WordDefinitionViewer>> pen_stroke_entry : word_definition_viewers_pen_stroke_id_map.entrySet()) {
			
			List<WordDefinitionViewer> word_definition_tiles = pen_stroke_entry.getValue();
			
			if(ignore_id){
				
				filter_word_definition_tiles(word_definition_tiles, false);
				
			}
			else{
				
				if(pen_stroke_clicked_id == pen_stroke_entry.getKey()){
					
					filter_word_definition_tiles(word_definition_tiles, false);
					
				}
				else{
					
					filter_word_definition_tiles(word_definition_tiles, true);
					
				}
				
			}
			
		}
		
		meta_viewer.updateMetaViewer();
		
	}
	
	private void filter_word_definition_tiles(List<WordDefinitionViewer> query_responses, boolean is_filtered_out){
		
		for(int i = 0 ; i < query_responses.size(); i++){
			
			query_responses.get(i).setTileFilteredOut(is_filtered_out);
			
		}
		
	}

	/*private void filter_word_definition_tiles(List<WordDefinitionTile> query_responses, boolean is_filtered_out){
		
		for(int i = 0 ; i < query_responses.size(); i++){
			
			query_responses.get(i).setTileFilteredOut(is_filtered_out);
			
		}
		
	}*/
	
	private List<QueryResponseReceived> filter_response_tiles(List<QueryResponseReceived> query_responses, boolean is_filtered_out, List<QueryResponseReceived> query_responses_to_skip){
		
		List<QueryResponseReceived> query_responses_processed = new ArrayList<QueryResponseReceived>();
		
		for(int i = 0 ; i < query_responses.size(); i++){
			
			QueryResponseReceived query_response = query_responses.get(i);
			
			boolean ignore = false;
			for(int j = 0; j < query_responses_to_skip.size(); j++){
				
				if(query_response.getQueryId().equals(query_responses_to_skip.get(j).getQueryId())){
					
					ignore = true;
					break;
					
				}
				
			}
			
			if(!ignore){
			
				String[] query_id_split = query_response.getQueryId().split("#");
				if(query_id_split.length == 3){
					
					query_responses_processed.add(query_response);
					
				}
				else{
					
					String[] pen_strokes_in_query = query_id_split[1].substring(1, query_id_split[1].length() - 1).split("-");
					if(pen_strokes_in_query.length > 1){
						
						query_responses_processed.add(query_response);
						
					}
					
				}
				/*List<Tile> response_tiles = query_response.getResponseTiles();
				
				for(int j = 0; j < response_tiles.size(); j++){
					
					response_tiles.get(j).setTileFilteredOut(is_filtered_out);
					
				}*/
				
				List<DetailResponseViewer> response_tiles = query_response.getTile2s();
				
				for(int j = 0; j < response_tiles.size(); j++){
					
					response_tiles.get(j).setTileFilteredOut(is_filtered_out);
					
				}
				
			}
			
		}
		
		return query_responses_processed;
		
	}
	
	private void set_is_pen_stroke_on_text(PenStroke pen_stroke){
		
		boolean is_valid = false;
		
		Stanza poem_header = worksheet_content.getPoemHeader();
		List<Stanza> poem_stanzas = worksheet_content.getPoemStanzas().getStanzas();

		Rectangle2D header_bounds = poem_header.getRawPixelBounds();
		Rectangle2D header_bounds_to_compare = new Rectangle2D.Double(header_bounds.getX(), header_bounds.getY(), header_bounds.getWidth(), header_bounds.getHeight() + CompositeGenerator.line_break_space);
		
		Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds();
	
		if(header_bounds_to_compare.intersects(pen_stroke_bounds)){
			
			is_valid = true;
			pen_stroke.setIsInTextSpace(is_valid);
			
		}
		else{
		
			for(int j = 0; j < poem_stanzas.size(); j++){
				
				Rectangle2D poem_stanza_bounds = poem_stanzas.get(j).getRawPixelBounds();
				Rectangle2D poem_stanza_bounds_to_compare = new Rectangle2D.Double(poem_stanza_bounds.getX(), poem_stanza_bounds.getY(), poem_stanza_bounds.getWidth(), poem_stanza_bounds.getHeight() + CompositeGenerator.line_break_space);
				
				if(poem_stanza_bounds_to_compare.intersects(pen_stroke_bounds)){
					
					is_valid = true;
					pen_stroke.setIsInTextSpace(is_valid);
					break;
					
				}
				
			}
			
		}
		
	}
	
	private List<PenStroke> get_pen_strokes_to_cluster(List<PenStroke> pen_strokes){
		
		List<PenStroke> valid_pen_strokes = new ArrayList<PenStroke>();
		
		for(int i = 0; i < pen_strokes.size(); i++){
			
			PenStroke pen_stroke = pen_strokes.get(i);
			if(pen_stroke.getIsInTextSpace()){
				
				valid_pen_strokes.add(pen_stroke);
				
			}
			
		}
		
		return valid_pen_strokes;
		
	}
	
	public List<List<QueryDetails>> getDetailsOfQueriesGenerated(){
		
		return queries_per_completed_pen_stroke;
		
	}
	
	public Map<String, WordDefinition> getWordDefinitions(){
		
		return word_definition_xml_reader.retrieveWordDefinitions();
		
	}
	
	public Map<Long, List<Integer>> getPenStrokeIdQueryTypesMap(){
		
		return pen_stroke_id_query_response_types_map;
		
	}
	
	public Map<Long, List<String>> getPenStrokeIdAnnotatedWordMap(){
		
		return pen_stroke_id_annotated_words_map;
		
	}
	
}
