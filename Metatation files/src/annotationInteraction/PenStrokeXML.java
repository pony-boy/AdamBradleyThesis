package annotationInteraction;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class PenStrokeXML {
	
	private static Document doc;
	private static Element doc_root;
	private static File xml_file;
	
	private static String file_path = "res/worksheets/";
	private String file_name = "pen strokes worksheet ";
	
	public PenStrokeXML(long worksheet_id){
		
		doc = null;
		doc_root = null;
		
		file_name += worksheet_id + ".xml";
		xml_file = new File(file_path + file_name);
		
		if(xml_file.exists()){
			
			SAXBuilder doc_sax_builder = new SAXBuilder();
	    	try {
	    		
				doc = doc_sax_builder.build(xml_file);
				
			} catch (JDOMException | IOException e) {
				
				System.out.println("Exception when trying to open the pen strokes file \n");
				e.printStackTrace();
				
			}
	    	
	    	doc_root = doc.getRootElement();
			
		}
		else{
			
			doc = new Document();
	    	doc_root = new Element("pen_strokes");
	    	doc.setRootElement(doc_root);
			
		}
		
	}
	
	public void updatePenStrokeXML(PenStroke new_penstroke){
		
		Element pen_stroke = new Element("pen_stroke");
		pen_stroke.setAttribute("stroke_id", "" + new_penstroke.getStrokeId());
		pen_stroke.setAttribute("pattern_id", new_penstroke.getPatternId());
		pen_stroke.setAttribute("pen_id", "" + new_penstroke.getPenId());
		
		Element pen_stroke_time = new Element("time");
		pen_stroke_time.setAttribute("start_time", new_penstroke.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME));
		pen_stroke_time.setAttribute("end_time", new_penstroke.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME));
		
		Element pen_stroke_bounding_box = new Element("bounding_box");
		Rectangle2D stroke_bounds = new_penstroke.getStrokeBounds();
		pen_stroke_bounding_box.setAttribute("x", "" + stroke_bounds.getX());
		pen_stroke_bounding_box.setAttribute("y", "" + stroke_bounds.getY());
		pen_stroke_bounding_box.setAttribute("w", "" + stroke_bounds.getWidth());
		pen_stroke_bounding_box.setAttribute("h", "" + stroke_bounds.getHeight());
		
		Element pen_stroke_pen_points = new Element("pen_points");
		List<PenPoint> pen_points = new_penstroke.getPenPoints();
		for(int i = 0; i < pen_points.size(); i++){
			
			Element pen_stroke_pen_point = new Element("pen_point");
			PenPoint pen_point = pen_points.get(i);
			pen_stroke_pen_point.setAttribute("x", "" + pen_point.getRawX());
			pen_stroke_pen_point.setAttribute("y", "" + pen_point.getRawY());
			pen_stroke_pen_point.setAttribute("time", pen_point.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
			
			pen_stroke_pen_points.addContent(pen_stroke_pen_point);
			
		}

		pen_stroke.addContent(pen_stroke_bounding_box);
		pen_stroke.addContent(pen_stroke_time);
		pen_stroke.addContent(pen_stroke_pen_points);
		
		doc_root.addContent(pen_stroke);
		
		save_pen_stroke_xml();
		
	}
	
	private void save_pen_stroke_xml(){
		
		XMLOutputter xml_file_generator = new XMLOutputter();    
    	
    	xml_file_generator.setFormat(Format.getPrettyFormat());  
    	try {
    		
			xml_file_generator.output(doc, new FileWriter(xml_file));
			
		} catch (IOException e) {
			
			System.out.println("Exception when saving the pen stroke file \n");
			e.printStackTrace();
			
		}
		
	}
	
	public List<PenStroke> retrievePenStrokeXML(){
		
		List<PenStroke> pen_strokes = new ArrayList<PenStroke>();
		
		List<Element> pen_stroke_entries = doc_root.getChildren();
		
		for(int i = 0; i < pen_stroke_entries.size(); i++){
			
			Element pen_stroke_entry = pen_stroke_entries.get(i);
			
			PenStroke pen_stroke = new PenStroke(Long.parseLong(pen_stroke_entry.getAttributeValue("stroke_id")));
			String pattern_id = pen_stroke_entry.getAttributeValue("pattern_id");
			long pen_id = Long.parseLong(pen_stroke_entry.getAttributeValue("pen_id"));
			
			List<Element> pen_point_entries = pen_stroke_entry.getChild("pen_points").getChildren();
			for(int j = 0; j < pen_point_entries.size(); j++){
				
				Element pen_point_entry = pen_point_entries.get(j);
				pen_stroke.addPenPoint(new PenPoint(pattern_id, pen_id, Double.parseDouble(pen_point_entry.getAttributeValue("x")), Double.parseDouble(pen_point_entry.getAttributeValue("y")), LocalDateTime.parse(pen_point_entry.getAttributeValue("time"), DateTimeFormatter.ISO_DATE_TIME)));
				
			}
			
			pen_stroke.setStrokeBounds(WorksheetViewer.resize_factor);
			pen_stroke.setLinearStrokePath(WorksheetViewer.resize_factor, Tile.resize_factor);
			//pen_stroke.setSplineStrokePath(WorksheetViewer.resize_factor);
			
			pen_strokes.add(pen_stroke);
			
		}
		
		return pen_strokes;
		
	}

}
