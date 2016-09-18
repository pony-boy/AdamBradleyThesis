package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class QueryGenerator {
	
	private long worksheet_id;
	private Poem poem_content;

	public QueryGenerator(long worksheet_id, Poem worksheet_content){
		
		this.worksheet_id = worksheet_id;
		poem_content = worksheet_content;
		
	}
	
	public List<QueryDetails> generateQueriesAtTheEndOfPenStroke(ClusterGenerator pen_stroke_cluster_generator, List<PenStroke> all_pen_strokes_in_worksheet, PenStroke pen_stroke_generating_clusters){

		List<Map<PenStroke, List<String>>> annotator_pen_strokes_in_clusters = new ArrayList<Map<PenStroke, List<String>>>();
		List<Map<PenStroke, List<List<PenStroke>>>> connector_pen_strokes_in_clusters = new ArrayList<Map<PenStroke, List<List<PenStroke>>>>();
		
		List<Cluster> clusters = pen_stroke_cluster_generator.getClusterIterationAtStopIterationIndex().getClusters();
		
		System.out.println("clusters on last pen stroke: " + clusters.size());
		
		for(int i = 0; i < clusters.size(); i++){
			
			Cluster cluster = clusters.get(i);
			List<PenStroke> pen_strokes_in_cluster = cluster.getPenStrokes();

			Map<PenStroke, List<String>> annotator_pen_strokes_in_cluster = new HashMap<PenStroke, List<String>>();
			Map<PenStroke, List<List<PenStroke>>> connector_pen_strokes_in_cluster = new HashMap<PenStroke, List<List<PenStroke>>>();

			System.out.println("pen strokes in cluster " + i + ": " + pen_strokes_in_cluster.size());
			
			for(int j = 0; j < pen_strokes_in_cluster.size(); j++){
				
				List<String> words_annotated_by_penstroke = new ArrayList<String>();
				
				PenStroke pen_stroke = pen_strokes_in_cluster.get(j);
				//TODO check for space where cluster is made if not on text space then ignore
				ShapeRecognizer.penStrokeTypes pen_stroke_type = pen_stroke.getPenStrokeType();
				
				if(pen_stroke_type == ShapeRecognizer.penStrokeTypes.Ellipse){
					
					//System.out.println("cluster: " + i + " pen stroke " + j + " is ellipse");
					words_annotated_by_penstroke = words_marked_by_ellipse(pen_stroke);
					if(!words_annotated_by_penstroke.isEmpty()){
					
						annotator_pen_strokes_in_cluster.put(pen_stroke, words_annotated_by_penstroke);
						
					}
					
				}
				else if(pen_stroke_type == ShapeRecognizer.penStrokeTypes.Underline){
					
					//System.out.println("cluster: " + i + " pen stroke " + j + " is underline");
					words_annotated_by_penstroke = words_marked_by_underline(pen_stroke);
					if(!words_annotated_by_penstroke.isEmpty()){
						
						annotator_pen_strokes_in_cluster.put(pen_stroke, words_annotated_by_penstroke);
						
					}
					
				}
				else if(pen_stroke_type == ShapeRecognizer.penStrokeTypes.Connector){

					//System.out.println("cluster: " + i + " pen stroke " + j + " is connector");
					List<List<PenStroke>> pen_strokes_connected = words_marked_by_connector(clusters, pen_stroke, all_pen_strokes_in_worksheet);
					if(!pen_strokes_connected.isEmpty()){
						
						System.out.println("found connector in cluster to add");
						connector_pen_strokes_in_cluster.put(pen_stroke, pen_strokes_connected);
						
					}
					
					
				}
				else{
					
					continue;
					
				}
				
			}
			
			if(!annotator_pen_strokes_in_cluster.isEmpty()){
				
				annotator_pen_strokes_in_clusters.add(annotator_pen_strokes_in_cluster);
				
			}
			
			//System.out.println(connector_pen_strokes_in_cluster.isEmpty());
			
			if(!connector_pen_strokes_in_cluster.isEmpty()){
				
				//System.out.println("found connector in cluster to add");
				connector_pen_strokes_in_clusters.add(connector_pen_strokes_in_cluster);
				
			}
			
		}
		
		List<QueryDetails> all_queries = new ArrayList<QueryDetails>();
		
		List<Long> annotator_pen_strokes_to_be_ignored_ids = new ArrayList<Long>();
		
		// resolve all connectors first
		List<Map<PenStroke, List<QueryDetails>>> connector_queries_in_clusters = new ArrayList<Map<PenStroke, List<QueryDetails>>>();
		for(int i = 0; i < connector_pen_strokes_in_clusters.size(); i++){
			
			//System.out.println("cluster " + i);
			Map<PenStroke, List<QueryDetails>> connector_queries_in_cluster = new HashMap<PenStroke, List<QueryDetails>>();
			
			Map<PenStroke, List<List<PenStroke>>> connector_pen_strokes_in_cluster = connector_pen_strokes_in_clusters.get(i);
			Iterator<Entry<PenStroke, List<List<PenStroke>>>> connector_pen_strokes_iterator = connector_pen_strokes_in_cluster.entrySet().iterator();
			while(connector_pen_strokes_iterator.hasNext()){
				
				Map.Entry<PenStroke, List<List<PenStroke>>> connector_pen_stroke_entry = (Map.Entry<PenStroke, List<List<PenStroke>>>) connector_pen_strokes_iterator.next();
				PenStroke connector_pen_stroke = connector_pen_stroke_entry.getKey();
				//System.out.println("storke " + connector_pen_stroke.getStrokeId());
				List<List<PenStroke>> connected_pen_strokes = connector_pen_stroke_entry.getValue();
				List<QueryDetails> connector_pen_stroke_queries = generate_connector_queries(worksheet_id, pen_stroke_generating_clusters.getStrokeId(),connector_pen_stroke, connected_pen_strokes, annotator_pen_strokes_in_clusters);
				
				//System.out.println(connector_pen_stroke_queries.isEmpty());
				
				if(!connector_pen_stroke_queries.isEmpty()){
					
					connector_queries_in_cluster.put(connector_pen_stroke, connector_pen_stroke_queries);
					for(int m = 0; m < connector_pen_stroke_queries.size(); m++){
						
						QueryDetails connector_pen_stroke_query = connector_pen_stroke_queries.get(m);
						annotator_pen_strokes_to_be_ignored_ids.addAll(connector_pen_stroke_query.get_annotator_pen_strokes());
						all_queries.add(connector_pen_stroke_query);
						
					}
					
				}
				
				
			}
			
			if(!connector_queries_in_cluster.isEmpty()){
				
				connector_queries_in_clusters.add(connector_queries_in_cluster);
				
			}
			
		}
		
		// then resolve ellipses and underlines
		List<List<QueryDetails>> annotator_queries_in_clusters = new ArrayList<List<QueryDetails>>();
		
		//System.out.println("before ellipse resolve");
		for(int i = 0 ; i < annotator_pen_strokes_in_clusters.size(); i++){

			List<QueryDetails> annotator_queries_in_cluster = new ArrayList<QueryDetails>();
			
			Map<PenStroke, List<String>> ellipse_pen_strokes_in_cluster =  new HashMap<PenStroke, List<String>>();
			Map<PenStroke, List<String>> underline_pen_strokes_in_cluster =  new HashMap<PenStroke, List<String>>();
			
			Map<PenStroke, List<String>> annotator_pen_strokes_in_cluster = annotator_pen_strokes_in_clusters.get(i);
			Iterator<Entry<PenStroke, List<String>>> annotator_pen_strokes_in_cluster_iterator = annotator_pen_strokes_in_cluster.entrySet().iterator();
			while(annotator_pen_strokes_in_cluster_iterator.hasNext()){
				
				Map.Entry<PenStroke, List<String>> annotator_stroke_entry = (Map.Entry<PenStroke, List<String>>) annotator_pen_strokes_in_cluster_iterator.next();
				PenStroke annotator_pen_stroke = annotator_stroke_entry.getKey();
				long annotator_pen_stroke_id = annotator_pen_stroke.getStrokeId();
				boolean ignore_annotator_pen_stroke = false;
				for(int j = 0; j < annotator_pen_strokes_to_be_ignored_ids.size(); j++){
					
					if(annotator_pen_strokes_to_be_ignored_ids.get(j).longValue() == annotator_pen_stroke_id){
						
						ignore_annotator_pen_stroke = true;
						break;
						
					}
					
				}

				if(!ignore_annotator_pen_stroke){

					if(annotator_pen_stroke.getPenStrokeType() == ShapeRecognizer.penStrokeTypes.Ellipse){
						
						ellipse_pen_strokes_in_cluster.put(annotator_pen_stroke, annotator_stroke_entry.getValue());
						
					}
					else{
						
						underline_pen_strokes_in_cluster.put(annotator_pen_stroke, annotator_stroke_entry.getValue());
						
					}
					
				}

			}
			
			if(!ellipse_pen_strokes_in_cluster.isEmpty()){
				
				QueryDetails annotator_query = new QueryDetails(worksheet_id, pen_stroke_generating_clusters.getStrokeId(), ellipse_pen_strokes_in_cluster);
				annotator_queries_in_cluster.add(annotator_query);
				all_queries.add(annotator_query);
				
			}
			if(!underline_pen_strokes_in_cluster.isEmpty()){

				QueryDetails annotator_query = new QueryDetails(worksheet_id, pen_stroke_generating_clusters.getStrokeId(), underline_pen_strokes_in_cluster);
				annotator_queries_in_cluster.add(annotator_query);
				all_queries.add(annotator_query);
				
			}
			
			if(!annotator_queries_in_cluster.isEmpty()){
				
				annotator_queries_in_clusters.add(annotator_queries_in_cluster);
				
			}
			
		}
		
		return all_queries;
		
	}
	
	private List<QueryDetails> generate_connector_queries(long worksheet_id, long pen_stroke_generating_clusters_id, PenStroke connector_pen_stroke, List<List<PenStroke>> all_connected_pen_strokes, List<Map<PenStroke, List<String>>> annotator_pen_strokes_in_clusters){
		
		//System.out.println("called generate connector");
		
		List<QueryDetails> connector_queries = new ArrayList<QueryDetails>();
		
		// all sets of pen strokes connected by this connector pen stroke
		for(int i = 0; i < all_connected_pen_strokes.size(); i++){
			
			// one set of pen strokes connected by this connector pen stroke
			List<PenStroke> connected_pen_strokes = all_connected_pen_strokes.get(i);
			long connected_1_stroke_id = connected_pen_strokes.get(0).getStrokeId(), connected_2_stroke_id = connected_pen_strokes.get(1).getStrokeId();
			
			//System.out.println("looking for " + connected_1_stroke_id + ", " + connected_2_stroke_id + " in annotators");
			Map<PenStroke, List<String>> connected_pen_strokes_all_info = new HashMap<PenStroke, List<String>>();
			
			for(int j = 0; j < annotator_pen_strokes_in_clusters.size(); j++){
				
				Map<PenStroke, List<String>> annotator_pen_strokes_in_cluster = annotator_pen_strokes_in_clusters.get(j);
				Iterator<Entry<PenStroke, List<String>>>  annotator_pen_strokes_iterator = annotator_pen_strokes_in_cluster.entrySet().iterator();
				while(annotator_pen_strokes_iterator.hasNext()){
					
					Map.Entry<PenStroke, List<String>> annotator_pen_stroke_entry = (Map.Entry<PenStroke, List<String>>) annotator_pen_strokes_iterator.next();
					PenStroke annotator_pen_stroke = annotator_pen_stroke_entry.getKey();
					List<String> annotated_words = annotator_pen_stroke_entry.getValue();
					
					long annotator_pen_stroke_id = annotator_pen_stroke.getStrokeId();
					//System.out.println(annotator_pen_stroke_id);
					
					if(annotator_pen_stroke_id == connected_1_stroke_id || annotator_pen_stroke_id == connected_2_stroke_id){
						
						//System.out.println("found match");
						connected_pen_strokes_all_info.put(annotator_pen_stroke, annotated_words);
						
					}
					
				}
				
				/*if(connected_pen_strokes_all_info.size() == 2){
					
					break;
					
				}*/
				
			}
			
			if(connected_pen_strokes_all_info.size() == 2){
				
				//System.out.println("Connector query");
				QueryDetails connector_query = new QueryDetails(worksheet_id, pen_stroke_generating_clusters_id, connected_pen_strokes_all_info, connector_pen_stroke);
				connector_queries.add(connector_query);
				
			}
			
		}
		
		return connector_queries;
		
	}
	
	private List<String> words_marked_by_ellipse(PenStroke pen_stroke){

		//System.out.println(pen_stroke.getStrokeId());
		
		List<String> words_annotated = new ArrayList<String>();
		
		Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds();
		double pen_stroke_area = pen_stroke_bounds.getWidth() * pen_stroke_bounds.getHeight();
		
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			Stanza poem_stanza = poem_stanzas.get(i);
			Rectangle2D poem_stanza_bounds = poem_stanza.getRawPixelBounds();
			
			if(poem_stanza_bounds.intersects(pen_stroke_bounds)){
				
				List<Line> stanza_lines = poem_stanza.getLines();
				
				for(int j = 0; j < stanza_lines.size(); j++){
					
					boolean has_annotated_word = false;
				
					Line stanza_line = stanza_lines.get(j);
					Rectangle2D stanza_line_bounds = stanza_line.getRawPixelBounds();
					
					if(stanza_line_bounds.intersects(pen_stroke_bounds)){
						
						Rectangle2D intersection = stanza_line_bounds.createIntersection(pen_stroke_bounds);
						double intersection_area = intersection.getWidth() * intersection.getHeight();

						if(intersection_area / pen_stroke_area > 0.4){
							
							has_annotated_word = true;
							//System.out.println("intersects line '" + j + "' with greater than 0.4");
							List<Word> line_words = stanza_line.getWords();
							
							for(int k = 0; k < line_words.size(); k++){
								
								Word line_word = line_words.get(k);
								Rectangle2D line_word_bounds = line_word.getRawPixelBounds();
								
								if(line_word_bounds.intersects(pen_stroke_bounds)){
									
									intersection = line_word_bounds.createIntersection(pen_stroke_bounds);
									intersection_area = intersection.getWidth() * intersection.getHeight();
									double word_area = line_word_bounds.getWidth() * line_word_bounds.getHeight();
									
									//System.out.println("word area: " + word_area + " , intersection area: " + intersection_area + ", ratio: " + (intersection_area / pen_stroke_area));
									
									if(intersection_area / word_area > 0.5){
										
										words_annotated.add(i + "|" + j + "|" + k + "|" + line_word.getWord().trim().toLowerCase() + "|+|+|" + (intersection_area / pen_stroke_area));
										//System.out.println("intersects word '" + line_word.getWord() + "' with greater than 0.5");
										
									}
									else{
										
										words_annotated.add(i + "|" + j + "|" + k + "|" + line_word.getWord().trim().toLowerCase() + "|+|-|" + (intersection_area / pen_stroke_area));
										//System.out.println("intersects word '" + line_word.getWord() + "' with less than 0.5");

									}
								
								}
								
							}
							
						}
						else{
							
							if(words_annotated.isEmpty() || !has_annotated_word){
							
								//System.out.println("intersects line '" + j + "' with less than 0.4");
								List<Word> line_words = stanza_line.getWords();
								
								for(int k = 0; k < line_words.size(); k++){
									
									Word line_word = line_words.get(k);
									Rectangle2D line_word_bounds = line_word.getRawPixelBounds();
									
									if(line_word_bounds.intersects(pen_stroke_bounds)){
										
										intersection = line_word_bounds.createIntersection(pen_stroke_bounds);
										intersection_area = intersection.getWidth() * intersection.getHeight();
										double word_area = line_word_bounds.getWidth() * line_word_bounds.getHeight();
										
										//System.out.println("word area: " + word_area + " , intersection area: " + intersection_area + ", ratio: " + (intersection_area / pen_stroke_area));
										
										if(intersection_area / word_area > 0.5){
											
											words_annotated.add(i + "|" + j + "|" + k + "|" + line_word.getWord().trim().toLowerCase() + "|-|+|" + (intersection_area / pen_stroke_area));
											//System.out.println("intersects word '" + line_word.getWord() + "' with greater than 0.5");
											//System.out.println(i + "|" + j + "|" + k + "|" + line_word.getWord().trim().toLowerCase() + "|-|+|" + (intersection_area / pen_stroke_area));
											
										}
										else{
											
											words_annotated.add(i + "|" + j + "|" + k + "|" + line_word.getWord().trim().toLowerCase() + "|-|-|" + (intersection_area / pen_stroke_area));
											//System.out.println("intersects word '" + line_word.getWord() + "' with less than 0.5");
											
										}
									
									}
									
								}
								
							}
							
						}
						
					}
					
				}
				
				break;
				
			}
			
		}
		
		List<String> words_annotated_filtered = resolve_ellipse_plus_plus_words(words_annotated);
		if(!words_annotated_filtered.isEmpty()){
			
			return words_annotated_filtered;
			
		}
		else{
			
			words_annotated_filtered = resolve_ellipse_plus_minus_words(words_annotated);
			if(!words_annotated_filtered.isEmpty()){
				
				return get_max_area_word(words_annotated_filtered);
				
			}
			else{
				
				words_annotated_filtered = resolve_ellipse_minus_plus_words(words_annotated);
				if(!words_annotated_filtered.isEmpty()){
					
					return get_max_area_word(words_annotated_filtered);
					
				}
				else{
					
					words_annotated_filtered = resolve_ellipse_minus_minus_words(words_annotated);
					if(!words_annotated_filtered.isEmpty()){
						
						return get_max_area_word(words_annotated_filtered);
						
					}
					else{
						
						return words_annotated_filtered;
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private List<String> resolve_ellipse_plus_plus_words(List<String> words_in_pen_stroke){
		
		List<String> words = new ArrayList<String>();
		
		for(int i = 0; i < words_in_pen_stroke.size(); i++){
			
			String word_in_pen_stroke = words_in_pen_stroke.get(i);
			String[] word_split = word_in_pen_stroke.split("\\|");
			if(word_split[4].equals("+") && word_split[5].equals("+")){
				
				words.add(word_split[0] + "|" + word_split[1] + "|" + word_split[2] + "|" + word_split[3]);
				
			}
			
		}
		
		return words;

	}
	
	private List<String> resolve_ellipse_plus_minus_words(List<String> words_in_pen_stroke){
		
		List<String> words = new ArrayList<String>();
		
		for(int i = 0; i < words_in_pen_stroke.size(); i++){
			
			String word_in_pen_stroke = words_in_pen_stroke.get(i);
			String[] word_split = word_in_pen_stroke.split("\\|");
			if(word_split[4].equals("+") && word_split[5].equals("-")){
				
				words.add(word_in_pen_stroke);
				
			}
			
		}
		
		return words;

	}
	
	private List<String> resolve_ellipse_minus_plus_words(List<String> words_in_pen_stroke){
		
		List<String> words = new ArrayList<String>();
		
		for(int i = 0; i < words_in_pen_stroke.size(); i++){
			
			String word_in_pen_stroke = words_in_pen_stroke.get(i);
			String[] word_split = word_in_pen_stroke.split("\\|");
			if(word_split[4].equals("-") && word_split[5].equals("+")){
				
				words.add(word_in_pen_stroke);
				
			}
			
		}
		
		return words;

	}
	
	private List<String> resolve_ellipse_minus_minus_words(List<String> words_in_pen_stroke){
		
		List<String> words = new ArrayList<String>();
		
		for(int i = 0; i < words_in_pen_stroke.size(); i++){
			
			String word_in_pen_stroke = words_in_pen_stroke.get(i);
			String[] word_split = word_in_pen_stroke.split("\\|");
			if(word_split[4].equals("-") && word_split[5].equals("-")){
				
				words.add(word_in_pen_stroke);
				
			}
			
		}
		
		return words;

	}
	
	private List<String> get_max_area_word(List<String> words){
		List<String> max_area_words = new ArrayList<String>();
		
		double max_area = Double.NEGATIVE_INFINITY;
		String max_area_word = null;
		
		for(int i = 0 ; i < words.size(); i++){
			
			//System.out.println(words.get(i));
			String[] word_split = words.get(i).split("\\|");
			//System.out.println(word_split[0] + " " + word_split[1] + " " + word_split[2] + " " + word_split[3] + " " + word_split[4] + " " + word_split[5] + " " + word_split[6]);
			double word_area = Double.parseDouble(word_split[6]);
			
			if(word_area >= max_area){
				
				max_area = word_area;
				max_area_word = word_split[0] + "|" + word_split[1] + "|" + word_split[2] + "|" + word_split[3];
				
			}
			
		}
		
		if(max_area_word != null){
			
			max_area_words.add(max_area_word);
			
		}
		
		return max_area_words;
		
	}
	
	private List<String> words_marked_by_underline(PenStroke pen_stroke){
		
		List<String> words_annotated = new ArrayList<String>();
		
		Rectangle2D pen_stroke_bounds = pen_stroke.getStrokeBounds();
		//double pen_stroke_area = pen_stroke_bounds.getWidth() * pen_stroke_bounds.getHeight();
		double pen_start_x = pen_stroke_bounds.getX(), pen_end_x = pen_stroke_bounds.getWidth() + pen_stroke_bounds.getX();
		
		List<Stanza> poem_stanzas = poem_content.getPoemStanzas().getStanzas();
		
		for(int i = 0; i < poem_stanzas.size(); i++){
			
			Stanza poem_stanza = poem_stanzas.get(i);
			Rectangle2D poem_stanza_bounds = poem_stanza.getRawPixelBounds();
			poem_stanza_bounds = new Rectangle2D.Double(poem_stanza_bounds.getX(), poem_stanza_bounds.getY(), poem_stanza_bounds.getWidth(), poem_stanza_bounds.getHeight() + CompositeGenerator.line_break_space);
			
			if(poem_stanza_bounds.intersects(pen_stroke_bounds)){
				
				List<Line> lines_in_stanza = poem_stanza.getLines();
				for(int j = 0; j < lines_in_stanza.size(); j++){
					
					Line line_in_stanza = lines_in_stanza.get(j);
					Rectangle2D line_bounds = line_in_stanza.getRawPixelBounds();
					
					if(j == lines_in_stanza.size() - 1){
						
						line_bounds = new Rectangle2D.Double(line_bounds.getX(), line_bounds.getY(), line_bounds.getWidth(), line_bounds.getHeight() + CompositeGenerator.line_break_space);
						
					}
					else{
						
						double next_line_start_y = lines_in_stanza.get(j + 1).getRawPixelBounds().getY();
						double new_height = line_bounds.getHeight() + (next_line_start_y - (line_bounds.getY() + line_bounds.getHeight()));
						
						line_bounds = new Rectangle2D.Double(line_bounds.getX(), line_bounds.getY(), line_bounds.getWidth(), new_height);
						
					}
					
					if(line_bounds.intersects(pen_stroke_bounds)){
						
						//System.out.println("Intersects line " + j);
						
						List<Word> words_in_line = line_in_stanza.getWords();
						for(int k = 0 ; k < words_in_line.size(); k++){
							
							Word word_in_line = words_in_line.get(k);
							Rectangle2D word_bounds = word_in_line.getRawPixelBounds();
							double word_start_x = word_bounds.getX(), word_end_x = word_bounds.getWidth() + word_bounds.getX();
							
							if(check_if_overlap_for_underline(word_start_x, word_end_x, pen_start_x, pen_end_x)){
								
								//System.out.println("Overlaps word '" + word_in_line.getWord() + "'");
								words_annotated.add(i + "|" + j + "|" + k + "|" + word_in_line.getWord().trim().toLowerCase());
								
							}
							
						}
						
					}
					
				}
				
				break;
				
			}
			
		}
		
		return words_annotated;
		
	}
	
	private boolean check_if_overlap_for_underline(double word_start_x, double word_end_x, double pen_start_x, double pen_end_x){
		
		Map<Double, String> end_points_sorted = new TreeMap<Double, String>();
        end_points_sorted.put(word_start_x, "word_start");
        end_points_sorted.put(word_end_x, "word_end");
        end_points_sorted.put(pen_start_x, "pen_start");
        end_points_sorted.put(pen_end_x, "pen_end");

        boolean overlap = false;
        String previous_end_point = null;
        int end_points_compared = 0;
        for (Map.Entry<Double, String> entry : end_points_sorted.entrySet())
        {
        	if(end_points_compared < 2){
        	
        		if(previous_end_point != null){
            		
            		if(!entry.getValue().split("_")[0].equals(previous_end_point)){
            			
            			overlap = true;
            			
            		}
            		
            	}
            	else{
            		
            		previous_end_point = entry.getValue().split("_")[0];
            		
            	}
        		
        	}
        	else{
        		
        		break;
        		
        	}
        	
        	end_points_compared++;
        	
        }
        
        return overlap;
		
	}
	
	private List<List<PenStroke>> words_marked_by_connector(List<Cluster> pen_stroke_clusters, PenStroke connector_pen_stroke, List<PenStroke> all_pen_strokes_in_worksheet){
		
		long connector_pen_stroke_id = connector_pen_stroke.getStrokeId();
		List<List<PenStroke>> preceding_marks_to_look_up = find_marks_preceding_connector(connector_pen_stroke_id, all_pen_strokes_in_worksheet, connector_pen_stroke);
		/*
		for(int i = 0; i < preceding_marks_to_look_up.size(); i++){
			
			List<PenStroke> set_of_pen_strokes = preceding_marks_to_look_up.get(i);
			System.out.println("Connects stroke: ");
			for(int j = 0; j < set_of_pen_strokes.size(); j++){
				
				System.out.println(set_of_pen_strokes.get(j).getStrokeId());
				
			}
			
		}*/
		
		return preceding_marks_to_look_up;
		
	}
	
	private List<List<PenStroke>> find_marks_preceding_connector(long connector_pen_stroke_id, List<PenStroke> all_pen_strokes_in_worksheet, PenStroke connector){

		List<List<PenStroke>> pen_strokes_to_look_up = new ArrayList<List<PenStroke>>();
		
		PenStroke connected_1, connected_2;
		List<PenStroke> pair_of_pen_strokes = new ArrayList<PenStroke>();
		
		if(connector_pen_stroke_id == 0){

			/*
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 2);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			*/
			
		}
		else if(connector_pen_stroke_id == 1){
			
			/*
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id - 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 1);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 2);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			*/
			
		}
		else{
			
			/*
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 2);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id - 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id + 1);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			*/
			connected_1 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id - 1);
			connected_2 = get_pen_stroke_by_stroke_id(all_pen_strokes_in_worksheet, connector_pen_stroke_id - 2);
			pair_of_pen_strokes = one_set_of_pen_strokes_to_check(connected_1, connected_2, connector);
			
			if(!pair_of_pen_strokes.isEmpty()){
				
				pen_strokes_to_look_up.add(pair_of_pen_strokes);
				
			}
			
		}
		
		return pen_strokes_to_look_up;
		
	}

	private PenStroke get_pen_stroke_by_stroke_id(List<PenStroke> all_pen_strokes_in_worksheet, long pen_stroke_id){
		
		PenStroke pen_stroke_found = null;
		for(int i = 0 ; i < all_pen_strokes_in_worksheet.size(); i++){
			
			PenStroke pen_stroke = all_pen_strokes_in_worksheet.get(i);
			if(pen_stroke_id == pen_stroke.getStrokeId()){
				
				pen_stroke_found = pen_stroke;
				break;
				
			}
			
		}
		
		return pen_stroke_found;
		
	}
	
	private List<PenStroke> one_set_of_pen_strokes_to_check(PenStroke connected_1, PenStroke connected_2, PenStroke connector){
		
		List<PenStroke> connected_pen_strokes = new ArrayList<PenStroke>();
		
		if(connected_1 != null && connected_2 != null){
			
			ShapeRecognizer.penStrokeTypes connected_1_stroke_type = connected_1.getPenStrokeType();
			ShapeRecognizer.penStrokeTypes connected_2_stroke_type = connected_2.getPenStrokeType();
			
			if((connected_1_stroke_type != ShapeRecognizer.penStrokeTypes.Undefined) && (connected_2_stroke_type != ShapeRecognizer.penStrokeTypes.Undefined)){
				
				Rectangle2D connector_bounds = connector.getStrokeBounds();
				
				if(connector_bounds.intersects(connected_1.getStrokeBounds()) && connector_bounds.intersects(connected_2.getStrokeBounds())){
				
					connected_pen_strokes.add(connected_1);
					connected_pen_strokes.add(connected_2);
					
				}
				
			}
			
		}
		
		return connected_pen_strokes;
		
	}
	
}
