package annotationInteraction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class WorksheetXML {
	
	private static Document doc;
	private static Element doc_root;
	private static File xml_file;
	
	private static String work_sheet_index_file_path = "res/worksheets/", work_sheet_index_file_name = "worksheets_index.xml";
	
	public WorksheetXML(){
		
		doc = null;
		doc_root = null;
		xml_file = new File(work_sheet_index_file_path + work_sheet_index_file_name);
		
		if(xml_file.exists()){
			
			SAXBuilder doc_sax_builder = new SAXBuilder();
	    	try {
	    		
				doc = doc_sax_builder.build(xml_file);
				
			} catch (JDOMException | IOException e) {
				
				System.out.println("Exception when trying to open the worksheets index file \n");
				e.printStackTrace();
				
			}
	    	
	    	doc_root = doc.getRootElement();
			
		}
		else{
			
			doc = new Document();
	    	doc_root = new Element("worksheets");
	    	doc.setRootElement(doc_root);
			
		}
		
	}
	
	public long generateWorksheetId(){
		
		long new_worksheet_id = -1;
		List<Element> existing_worksheets = doc_root.getChildren();
		
		if(existing_worksheets.isEmpty()){
			
			new_worksheet_id = 0;
			
		}
		else{
			
			new_worksheet_id = Long.parseLong(existing_worksheets.get(existing_worksheets.size() - 1).getAttributeValue("worksheet_id")) + 1;
			
		}
		
		return new_worksheet_id;
		
	}
	
	public void createWorksheetEntry(Worksheet new_worksheet, String poem_json_file_name){
		
		Element worksheet = new Element("worksheet");
		worksheet.setAttribute("worksheet_id", "" + new_worksheet.getWorksheetId());
		
		Element anoto_pattern = new Element("anoto_pattern");
		anoto_pattern.setText(new_worksheet.getAnotoPatternId());
		
		Element poem_content = new Element("poem_content");
		poem_content.setText(poem_json_file_name);
		
		worksheet.addContent(anoto_pattern);
		worksheet.addContent(poem_content);
		
		doc_root.addContent(worksheet);
		
		save_worksheets_index_xml();
		
	}

	private void save_worksheets_index_xml(){
		
		XMLOutputter xml_file_generator = new XMLOutputter();    
    	
    	xml_file_generator.setFormat(Format.getPrettyFormat());  
    	try {
    		
			xml_file_generator.output(doc, new FileWriter(xml_file));
			
		} catch (IOException e) {
			
			System.out.println("Exception when saving the worksheets index file \n");
			e.printStackTrace();
			
		}
		
	}
	
	public Worksheet retrieveWorksheetEntry(long worksheet_id){
		
		Worksheet worksheet_retrieved = null;
		
		List<Element> worksheet_entries = doc_root.getChildren();
		
		for(int i = 0; i < worksheet_entries.size(); i++){
			
			Element worksheet_entry = worksheet_entries.get(i);
			
			if(Long.parseLong(worksheet_entry.getAttributeValue("worksheet_id")) == worksheet_id){
				
				String poem_json_file_name = worksheet_entry.getChild("poem_content").getText();
				String anoto_pattern_id = worksheet_entry.getChild("anoto_pattern").getText();
				
				CompositeGenerator composite_generator = new CompositeGenerator(PoemJsonReader.retrievePoemContent(poem_json_file_name));
				composite_generator.generateComposite(new TIFReaderWriter(WorksheetManager.anoto_pattern_file_path, anoto_pattern_id).readTIF());
				BufferedImage worksheet_tif = new TIFReaderWriter(WorksheetManager.worksheets_file_path, WorksheetManager.worksheets_file_name + worksheet_id).readTIF();
				Poem worksheet_content = composite_generator.getPoem();
				worksheet_retrieved = new Worksheet(worksheet_id, anoto_pattern_id, worksheet_content, worksheet_tif);
				
				break;
				
			}
			
		}
		
		return worksheet_retrieved;
		
	}
	
}
