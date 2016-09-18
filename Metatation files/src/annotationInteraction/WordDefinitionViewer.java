package annotationInteraction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class WordDefinitionViewer {
	
	private JPanel definition_viewer;
	private JScrollPane definition_viewer_scroll_pane;
	
	private JButton etymology_viewer = null;
	private JButton pronunciation_viewer = null;
	private JPanel word_entry_viewer = null;
	private JScrollPane word_entry_scroll_pane = null;
	
	private WordDefinition word_definition;
	private boolean tile_filtered_out = false;
	
	private String word_pos_in_poem;
	
	private static Color[] word_pair_colors = {
		new Color(177, 199, 255, 153),
		new Color(196, 255, 195, 153),
		new Color(255, 175, 197, 153),
		new Color(255, 201, 255, 153),
		new Color(255, 220, 160, 153),
		new Color(231, 201, 255, 153),
		new Color(255, 255, 139, 153),
		new Color(151, 255, 176, 153),
		new Color(151, 255, 176, 153),
		new Color(255, 125, 74, 153),
		new Color(151, 153, 227, 153),
		new Color(225, 151, 25, 153),
		new Color(100, 201, 104, 153),
		new Color(255, 151, 255, 153),
		new Color(98, 199, 200, 153),
		new Color(255, 221, 0, 153),
		new Color(186, 255, 0, 153),
		new Color(202, 227, 226, 153).darker(),
		new Color(226, 199, 127, 153),
		new Color(225, 225, 173, 153),
	};
	
	public WordDefinitionViewer(WordDefinition word_definition, String pos_in_poem){
		
		this.word_definition = word_definition;
		this.word_pos_in_poem = pos_in_poem;
		create_definition_viewer();
		
	}
	
	private void create_definition_viewer(){
		
		definition_viewer = new JPanel();
		
		definition_viewer.setBackground(Color.WHITE);
		definition_viewer.setLayout(new BoxLayout(definition_viewer,  BoxLayout.PAGE_AXIS));
		definition_viewer.setBorder(null);
		
		String defintion_viwer_header_color = "rgb(" + word_pair_colors[7 + 10].getRed() + ", " + word_pair_colors[7 + 10].getGreen() + ", " + word_pair_colors[7 + 10].getBlue() + ", " + word_pair_colors[7 + 10].getAlpha() + ")";
		String definition_viewer_text = "<html><head style = \" background-color:" + defintion_viwer_header_color + "\"></head>";
		definition_viewer_text += "<body style = \"font-family: 'Palatino Linotype'; font-size: 17px; padding: 20px; margin: 2px; background-color:#FFFFFF\">";
		
		definition_viewer_text += "<h1 style = \"font-family: 'Palatino Linotype'; font-style: italic; background-color:" + defintion_viwer_header_color + "\">" + word_definition.getWord() + "</h1>";
		definition_viewer_text = create_etymology_viewer(definition_viewer_text);
		definition_viewer_text = create_pronunciation_viewer(definition_viewer_text);
		create_word_entries_viewer(definition_viewer_text);
		
		definition_viewer_scroll_pane = new JScrollPane(definition_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		definition_viewer_scroll_pane.setBackground(Color.WHITE);
	}
	
	private String create_etymology_viewer(String etymology_text){
		
		String etymology = word_definition.getEtymology();
		if(etymology != null){
			
			etymology_viewer = new JButton();
			
			//String etymology_text = "<html><head></head>";
			//etymology_text += "<body style = \"font-family: 'Palatino Linotype'; font-size: 17px; padding: 20px; border: 1px solid grey; margin: 2px\">";
			etymology_text += "<h2 style = \"font-style: italic\">" + "Origin" + "</h2>";
			etymology_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin: 2px\">" + etymology + "</p>";
			//etymology_text += "</body></html>";
			
			etymology_viewer.setText(etymology_text);
			etymology_viewer.setHorizontalAlignment(SwingConstants.LEADING);
			
			etymology_viewer.setBorder(null);
			etymology_viewer.setBorderPainted(false);
			etymology_viewer.setMargin(new Insets(0,0,0,0));
			etymology_viewer.setFocusPainted(false);
			//etymology_viewer.setContentAreaFilled(false);
			etymology_viewer.setVisible(true);
			etymology_viewer.setEnabled(true);
			
			//definition_viewer.add(etymology_viewer);
			
		}
		
		return etymology_text;
		
	}
	
	private String create_pronunciation_viewer(String pronunciation_text){
		
		List<String> pronunciations = word_definition.getPronunciations();
		if(!pronunciations.isEmpty()){
			
			pronunciation_viewer = new JButton();
			
			//String pronunciation_text = "<html><head></head>";
			//pronunciation_text += "<body style = \"font-family: 'Palatino Linotype'; font-size: 17px; padding: 20px; border: 1px solid grey; margin: 2px\">";
			pronunciation_text += "<h2 style = \"font-style: italic\">" + "Pronuciations" + "</h2>";
			pronunciation_text += "<p style = \"font-family: 'Palatino Linotype'; font-size: 10px; margin: 2px\">";

			for(int i = 0; i < pronunciations.size(); i++){
				
				pronunciation_text += pronunciations.get(i) + ", ";
				
			}
			pronunciation_text = pronunciation_text.substring(0, pronunciation_text.length() - 2);
			pronunciation_text += "</p>"; //</body></html>";
			
			pronunciation_viewer.setText(pronunciation_text);
			pronunciation_viewer.setHorizontalAlignment(SwingConstants.LEADING);
			
			pronunciation_viewer.setBorder(null);
			pronunciation_viewer.setBorderPainted(false);
			pronunciation_viewer.setMargin(new Insets(0,0,0,0));
			pronunciation_viewer.setFocusPainted(false);
			//pronunciation_viewer.setContentAreaFilled(false);
			pronunciation_viewer.setVisible(true);
			pronunciation_viewer.setEnabled(true);
			
			//definition_viewer.add(pronunciation_viewer);
			
		}
		
		return pronunciation_text;
		
	}
	
	private void create_word_entries_viewer(String definition_viewer_text){
	
		word_entry_viewer = new JPanel();
		word_entry_viewer.setLayout(new BoxLayout(word_entry_viewer, BoxLayout.PAGE_AXIS));
		word_entry_viewer.setPreferredSize(definition_viewer.getPreferredSize());
		
		List<WordEntryViewer> word_entry_viewers = new ArrayList<WordEntryViewer>();
		
		List<WordEntry> word_entries = word_definition.getWordEntries();
		Integer pos_index_word_entry = null;

		for(int i = 0; i < word_entries.size(); i++){
			
			WordEntry word_entry = word_entries.get(i);
			word_entry_viewers.add(new WordEntryViewer(word_entry, WordDefinitionViewer.this));
			
			String pos = word_entry.getPOS();
			if(pos != null && word_pos_in_poem != null){

				if(word_pos_in_poem.contains(pos)){

					pos_index_word_entry = i;
					word_entry_viewers.get(i).setEntryDetails(true);
					
				}
				
			}
			
		}
		
		if(pos_index_word_entry != null){
			
			WordEntryViewer entry_viewer = word_entry_viewers.get(pos_index_word_entry);
			
			definition_viewer_text += entry_viewer.entry_viewer_header_text + entry_viewer.entry_viewer_body_text;
			//word_entry_viewer.add(word_entry_viewers.get(pos_index_word_entry).getEntryViewerPanel());
			
		}
		
		for(int i = 0; i < word_entry_viewers.size(); i++){
			
			if(pos_index_word_entry != null){
				
				if(pos_index_word_entry == i){
					
					continue;
					
				}
				
			}
			WordEntryViewer entry_viewer = word_entry_viewers.get(i);
			definition_viewer_text += entry_viewer.entry_viewer_header_text + entry_viewer.entry_viewer_body_text;
			//word_entry_viewer.add(word_entry_viewers.get(i).getEntryViewerPanel());
			
		}

		definition_viewer_text += "</body></html>";		
		
		JButton definition_viewer_button = new JButton();
		
		definition_viewer_button.setText(definition_viewer_text);
		definition_viewer_button.setHorizontalAlignment(SwingConstants.LEADING);
		definition_viewer_button.setVerticalAlignment(SwingConstants.TOP);
		
		definition_viewer_button.setBorder(null);
		definition_viewer_button.setBorderPainted(false);
		definition_viewer_button.setMargin(new Insets(0,0,0,0));
		definition_viewer_button.setFocusPainted(false);
		definition_viewer_button.setContentAreaFilled(false);
		definition_viewer_button.setVisible(true);
		definition_viewer_button.setOpaque(true);
		definition_viewer_button.setBackground(Color.WHITE);
		
		//word_entry_scroll_pane = new JScrollPane(word_entry_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
		
		word_entry_scroll_pane = new JScrollPane(definition_viewer_button, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
		word_entry_scroll_pane.setBackground(Color.WHITE);
		word_entry_scroll_pane.setBorder(null);
		definition_viewer.add(word_entry_scroll_pane);
		
	}
	
	public void updateDefintionViewer(){
		
		definition_viewer.repaint();
		
	}
	
	public Dimension getDefinitionViewerSize(){
		
		return definition_viewer.getPreferredSize();
		
	}
	
	public JScrollPane getDefintionViewerScrollPane(){
		
		return definition_viewer_scroll_pane;
		
	}
	
	public void setTileFilteredOut(boolean is_tile_filtered_out){
		
		if(tile_filtered_out != is_tile_filtered_out){
		
			tile_filtered_out = is_tile_filtered_out;
			
		}
		
	}
	
	public boolean getTileFilteredOut(){
		
		return tile_filtered_out;
		
	}

}
