package annotationInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryDetails {
	
	long worksheet_id;
	long pen_stroke_generating_clusters_id;
	
	String query_id = "";
	String query_content = "";
	Map<PenStroke, List<String>> annotating_pen_strokes;
	PenStroke connector_pen_stroke = null;
	QueryType query_type; 
	
	public static enum QueryType{Explicit, Implicit};
	
	public QueryDetails(long worksheet_id, long pen_stroke_generating_clusters_id, Map<PenStroke, List<String>> annotating_pen_strokes){
		
		this.annotating_pen_strokes = annotating_pen_strokes;
		query_type = QueryType.Implicit;
		
		query_id += "(" + worksheet_id + "-" + pen_stroke_generating_clusters_id + ")#(";
		
		for(Map.Entry<PenStroke, List<String>> annotator_stroke_entry : annotating_pen_strokes.entrySet()){
			
			query_id += annotator_stroke_entry.getKey().getStrokeId() + "-";
			
			List<String> words_per_pen_stroke= annotator_stroke_entry.getValue();
			
			query_content += "[";
			for(int i = 0; i < words_per_pen_stroke.size(); i++){
				
				query_content += "(" + words_per_pen_stroke.get(i) + ") ";
				
			}
			query_content = query_content.substring(0, query_content.length() - 1) +"]#";
			
		}
		
		query_id = query_id.substring(0, query_id.length() - 1) + ")";
		query_content = query_content.substring(0, query_content.length() - 1);
		
		System.out.println("query id: " + query_id);
		System.out.println("query content: " + query_content);
		
	}
	
	public QueryDetails(long worksheet_id, long pen_stroke_generating_clusters_id, Map<PenStroke, List<String>> annotating_pen_strokes, PenStroke connector_pen_stroke){

		this.annotating_pen_strokes = annotating_pen_strokes;
		this.connector_pen_stroke = connector_pen_stroke;
		query_type = QueryType.Explicit;
		
		query_id += "(" + worksheet_id + "-" + pen_stroke_generating_clusters_id + ")#(";
		
		for(Map.Entry<PenStroke, List<String>> annotator_stroke_entry : annotating_pen_strokes.entrySet()){
			
			query_id += annotator_stroke_entry.getKey().getStrokeId() + "-";
			
			List<String> words_per_pen_stroke= annotator_stroke_entry.getValue();
			
			query_content += "[";
			for(int i = 0; i < words_per_pen_stroke.size(); i++){
				
				query_content += "(" + words_per_pen_stroke.get(i) + ") ";
				
			}
			query_content = query_content.substring(0, query_content.length() - 1) +"]#";
			
		}
		
		query_id = query_id.substring(0, query_id.length() - 1) + ")";
		query_id += "#(" + connector_pen_stroke.getStrokeId() + ")";
		query_content = query_content.substring(0, query_content.length() - 1);
		
		System.out.println("query id: " + query_id);
		System.out.println("query content: " + query_content);
		
	}
	
	public List<Long> get_annotator_pen_strokes(){
		
		List<Long> pen_stroke_ids = new ArrayList<Long>();
		
		for(PenStroke pen_stroke : annotating_pen_strokes.keySet()){
			
			pen_stroke_ids.add(pen_stroke.getStrokeId());
			
		}
		
		return pen_stroke_ids;
		
	}

}
