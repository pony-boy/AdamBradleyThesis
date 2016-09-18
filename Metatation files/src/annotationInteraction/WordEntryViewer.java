package annotationInteraction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class WordEntryViewer {
	
	private WordDefinitionViewer definition_viewer;
	
	private WordEntry word_entry;
	
	private JPanel entry_viewer;
	private JButton entry_details;
	
	private JScrollPane entry_viewer_scroll_pane;
	
	public String entry_viewer_header_text = "";
	public String entry_viewer_body_text = "";
	
	private boolean show_entry_details = true;
	
	private static int body_text_size = 10;
	
	public WordEntryViewer(WordEntry word_entry, WordDefinitionViewer defintion_viewer){
		
		this.word_entry = word_entry;
		this.definition_viewer = defintion_viewer;
		create_entry_viewer();
		
	}
	
	private void create_entry_viewer(){
		
		entry_viewer = new JPanel();
		
		entry_viewer.setLayout(new BorderLayout());
		entry_viewer.setBackground(Color.WHITE);
		
		create_entry_details();
		entry_viewer.add(entry_details, BorderLayout.WEST);
		
		entry_viewer_scroll_pane = new JScrollPane(entry_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
	}
	
	private String generate_entry_text(){
		
		String entry_text = "<html><head></head>";
		entry_text += "<body style = \"font-family: 'Palatino Linotype'; font-size: 18px; padding: 20px; margin: 2px\">";
		
		entry_text += get_header_text_for_entry_details();
		
		if(show_entry_details){
			
			entry_text += get_body_text_for_entry_details();
			
		}
		
		entry_text += "</body></html>";
		
		return entry_text;
		
	}
	
	private void create_entry_details(){
		
		entry_details = new JButton();

		entry_details.setText(generate_entry_text());
		entry_details.setHorizontalAlignment(SwingConstants.LEADING);
		
		entry_details.setBackground(Color.WHITE);
		entry_details.setBorder(null);
		entry_details.setBorderPainted(false);
		entry_details.setMargin(new Insets(0,0,0,0));
		entry_details.setFocusPainted(false);
		entry_details.setContentAreaFilled(false);
		entry_details.setVisible(true);
		entry_details.setEnabled(true);
		
		entry_details.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(show_entry_details){
					
					show_entry_details = false;
					
				}
				else{
					
					show_entry_details = true;
					
				}
				
				entry_details.setText(generate_entry_text());
				entry_details.repaint();
				entry_viewer.revalidate();
				
			}
		});
		
	}
	
	private String get_header_text_for_entry_details(){
		
		String header_text = "<h2 style = \"font-style: italic\">"; //TODO add font color here
		switch(word_entry.getPOS()){
		
		case "PRP": 
			header_text += "Pronoun";
			break;
		case "NN":
			header_text += "Noun";
			break;
		case "RB":
			header_text += "Adverb";
			break;
		case "VB":
			header_text += "Verb";
			break;
		case "JJ":
			header_text += "Adjective";
			break;
		case "IN":
			header_text += "Preposition";
			break;
		case "CC|IN":
			header_text += "Conjunction";
			break;
		case "UH":
			header_text += "Interjection";
			break;
			
		}
		
		header_text += "</h2>";
		
		
		entry_viewer_header_text = header_text;
		
		return header_text;
		
	}
	
	private String get_body_text_for_entry_details(){
		
		String body_text = "";
		
		String first_recorded_use = word_entry.getFirstRecordedUse();
		if(first_recorded_use != null){
			
			String first_recorded_use_text = "<h3 style = \"font-style: italic; font-family: 'Palatino Linotype'\">" + "First recorded use" + "</h3>";
			first_recorded_use_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin-top: 8px\">" + first_recorded_use + "</p>";
			body_text += first_recorded_use_text;
			
		}
		
		List<WordSense> word_senses = word_entry.getWordSenses();
		if(!word_senses.isEmpty()){
			
			String word_senses_text = "<h3 style = \"font-style: italic; margin-top: 8px; font-family: 'Palatino Linotype'\">" + "Senses" + "</h3>";
			
			int default_left_margin_for_sense = 5;
			Integer sense_index = null;
			for(int i = 0; i < word_senses.size(); i++){
				
				WordSense word_sense = word_senses.get(i);
				String[] sense_id_split = word_sense.getSenseId().split("\\.");

				String sense_text = "";
				if(sense_index == null || sense_index != Integer.parseInt(sense_id_split[0])){
					
					sense_index = Integer.parseInt(sense_id_split[0]);
					sense_text += "<h3 style = \"margin-top: 8px\">" + "Sense " + sense_id_split[0] + "</h3>";
					
				}
				int left_margin_for_sense_definition = default_left_margin_for_sense * sense_id_split.length;
				
				//TODO better formating for these
				
				String word_definition = word_sense.getDefinition();
				if(word_definition != null){
					
					sense_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin-left: " + (left_margin_for_sense_definition + 2) + "px; margin-bottom: 2px; margin-top: 8px\">" + word_definition + "</p>";
					
				}
				
				List<String> usage_notes = word_sense.getUsageNotes();
				if(!usage_notes.isEmpty()){
					
					sense_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin-left: " + (left_margin_for_sense_definition + 2 + 2) + "px; margin-bottom: 2px\">" + "Usage notes: ";
					for(int k = 0; k < usage_notes.size(); k++){
						
						sense_text += usage_notes.get(k) + "; ";
						
					}
					sense_text = sense_text.substring(0, sense_text.length() - 2);
					sense_text += "</p>";
					
				}
				
				List<String> usage_examples = word_sense.getUsageExamples();
				if(!usage_examples.isEmpty()){
					
					sense_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin-left: " + (left_margin_for_sense_definition + 2 + 2) + "px; margin-bottom: 2px\">" + "Usage examples: ";
					for(int k = 0; k < usage_examples.size(); k++){
						
						sense_text += usage_examples.get(k) + "; ";
						
					}
					sense_text = sense_text.substring(0, sense_text.length() - 2);
					sense_text += "</p>";
					
				}
				
				word_senses_text += sense_text;
				
			}
			
			body_text += word_senses_text;
			
		}
		
		entry_viewer_body_text = body_text;
		
		return body_text;
		
	}
	
	public JPanel getEntryViewerPanel(){
		
		return entry_viewer;
		
	}
	
	public JScrollPane getEntryViewerScrollPane(){
		
		return entry_viewer_scroll_pane;
		
	}
	
	public void setEntryDetails(boolean show_entry_details){
		
		this.show_entry_details = show_entry_details;
		entry_details.setText(generate_entry_text());
		entry_details.setText(generate_entry_text());
		entry_details.repaint();
		entry_viewer.revalidate();
		
	}

}
