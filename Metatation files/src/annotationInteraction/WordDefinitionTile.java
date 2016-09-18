package annotationInteraction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

public class WordDefinitionTile {
	
	public static int viewer_width = Tile.viewer_width;
	public static int viewer_height = Tile.viewer_height;
	
	private JPanel definition_viewer_panel;
	private JScrollPane definition_viewer_panel_scroll_pane;
	
	private WordDefinition word_definition;
	private boolean tile_filtered_out = false;
	
	private String word_pos_in_poem;
	
	public WordDefinitionTile(WordDefinition word_definition, String pos_in_poem){
		
		this.word_definition = word_definition;
		this.word_pos_in_poem = pos_in_poem;
		create_definition_viewer_panel();
		
	}
	
	@SuppressWarnings("serial")
	public void create_definition_viewer_panel(){
		
		definition_viewer_panel = new JPanel(){
			
			@Override
			public void paint(Graphics g){
					
				super.paint(g);
					
				Graphics2D g2d = (Graphics2D) g;
				
				/*
				if(tile_filtered_out){
					
					Insets panel_insets = this.getInsets();
					g2d.setColor(new Color(255, 255, 255, 200));
					g2d.fillRect(panel_insets.left, panel_insets.top, viewer_width - panel_insets.left - panel_insets.right, viewer_height - panel_insets.top - panel_insets.bottom);
					
				}
				*/
				
			}
		};
		
		definition_viewer_panel.setLayout(new BoxLayout(definition_viewer_panel, BoxLayout.Y_AXIS));
		
		String pos_in_poem = word_pos_in_poem;
		if(pos_in_poem != null){
			
			String pos_in_poem_text = "POS in poem: " + pos_in_poem + "\n";
			JTextArea pos_in_poem_viewer = new JTextArea(pos_in_poem_text);
			pos_in_poem_viewer.setFont(new Font("Consolas", Font.PLAIN, 15));
			pos_in_poem_viewer.setLineWrap(true);
			pos_in_poem_viewer.setWrapStyleWord(true);
			pos_in_poem_viewer.setEditable(false);

			JScrollPane pronunciation_viewer_scroll_pane = new JScrollPane(pos_in_poem_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			definition_viewer_panel.add(pronunciation_viewer_scroll_pane);
			
		}
		
		String etymology_text = word_definition.getEtymology();
		if(etymology_text != null){
			
			JTextArea etymology_viewer = new JTextArea(etymology_text);
			etymology_viewer.setFont(new Font("Consolas", Font.PLAIN, 15));
			etymology_viewer.setLineWrap(true);
			etymology_viewer.setWrapStyleWord(true);
			etymology_viewer.setEditable(false);

			JScrollPane pronunciation_viewer_scroll_pane = new JScrollPane(etymology_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			definition_viewer_panel.add(pronunciation_viewer_scroll_pane);
			
		}
		
		List<String> pronunciations = word_definition.getPronunciations();
		String pronunciation_text = "";
		if(!pronunciations.isEmpty()){
		
			for(int i = 0; i < pronunciations.size(); i++){
				
				pronunciation_text += pronunciations.get(i) + ", ";
				
			}
			pronunciation_text = pronunciation_text.substring(0, pronunciation_text.length() - 2);
			
			JTextArea pronunciation_viewer = new JTextArea(pronunciation_text);
			pronunciation_viewer.setFont(new Font("Consolas", Font.PLAIN, 15));
			pronunciation_viewer.setLineWrap(true);
			pronunciation_viewer.setWrapStyleWord(true);
			pronunciation_viewer.setEditable(false);

			JScrollPane pronunciation_viewer_scroll_pane = new JScrollPane(pronunciation_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			definition_viewer_panel.add(pronunciation_viewer_scroll_pane);
			
		}
		
		List<Integer> same_pos_as_pos_in_poem = new ArrayList<Integer>();
		List<WordEntry> word_entries = word_definition.getWordEntries();
		for(int i = 0; i < word_entries.size(); i++){
			
			WordEntry word_entry = word_entries.get(i);
			String word_entry_text  = "";
			
			String first_recorded_use = word_entry.getFirstRecordedUse();
			if(first_recorded_use != null){
				
				word_entry_text += "First Recorded Use: " + first_recorded_use + "\n";
				
			}
			
			String pos = word_entry.getPOS();
			if(pos != null){
				
				word_entry_text += "POS: " + pos + "\n";
				
				if(pos_in_poem != null){
					
					if(pos_in_poem.contains(pos)){
						
						same_pos_as_pos_in_poem.add(i);
						System.out.println("entry i: " + i + " same as pos in poem");
						
					}
					
				}
				
			}
			
			List<WordSense> word_senses = word_entry.getWordSenses();
			for(int j = 0; j < word_senses.size(); j++){
				
				WordSense word_sense = word_senses.get(j);
				String sense_id = word_sense.getSenseId();
				//String[] sense_id_split = sense_id.split(".");
				//TODO better formatting
				word_entry_text += sense_id;
				//int max_space_indent = 5;
				String definition = word_sense.getDefinition();
				if(definition != null){
					
					word_entry_text += "\n" + "  Definition: " + definition;
					
				}
				
				List<String> usage_notes = word_sense.getUsageNotes();
				if(!usage_notes.isEmpty()){
					
					word_entry_text += "\n" + "  Usage Notes: ";
					for(int k = 0; k < usage_notes.size(); k++){
						
						word_entry_text += usage_notes.get(k) + "; ";
						
					}
					word_entry_text = word_entry_text.substring(0, word_entry_text.length() - 2);
					
				}
				
				List<String> usage_examples = word_sense.getUsageExamples();
				if(!usage_examples.isEmpty()){
				
					word_entry_text += "\n" + "  Usage Examples: ";
					for(int k = 0; k < usage_examples.size(); k++){
						
						word_entry_text += usage_examples.get(k) + "; ";
						
					}
					word_entry_text = word_entry_text.substring(0, word_entry_text.length() - 2);
					
				}
				
				word_entry_text += "\n\n";
				
			}
			
			JTextArea word_entry_viewer = new JTextArea(word_entry_text);
			word_entry_viewer.setFont(new Font("Consolas", Font.PLAIN, 15));
			word_entry_viewer.setLineWrap(true);
			word_entry_viewer.setWrapStyleWord(true);
			word_entry_viewer.setEditable(false);
			int top_border_width = 1, bottom_border_width = 1;
			if(i == 0){
				top_border_width = 0;
			}
			if(i == word_entries.size() - 1){
				
				bottom_border_width = 0;
				
			}
			word_entry_viewer.setBorder(BorderFactory.createMatteBorder(top_border_width, 0, bottom_border_width, 0, Color.BLACK));
			JScrollPane word_entry_viewer_scroll_pane = new JScrollPane(word_entry_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			definition_viewer_panel.add(word_entry_viewer_scroll_pane);
			
		}
		
		definition_viewer_panel.setBackground(Color.WHITE);
		definition_viewer_panel.setMaximumSize(new Dimension(viewer_width, viewer_height));
		
		TitledBorder panel_border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 1), word_definition.getWord());
		panel_border.setTitleFont(new Font("Consolas", Font.PLAIN, 15));
		definition_viewer_panel.setBorder(panel_border);
		
		definition_viewer_panel_scroll_pane = new JScrollPane(definition_viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
	}
	
	public JScrollPane getWordDefinitionTileScrollPane(){
		
		return definition_viewer_panel_scroll_pane;
		
	}
	
	public void setTileFilteredOut(boolean is_tile_filtered_out){
		
		if(tile_filtered_out != is_tile_filtered_out){
		
			tile_filtered_out = is_tile_filtered_out;
			//definition_viewer_panel.repaint();
			
		}
		
	}
	
	public boolean getTileFilteredOut(){
		
		return tile_filtered_out;
		
	}
	
	public WordDefinition getWordDefinition(){
		
		return word_definition;
		
	}

}
