package annotationInteraction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.sun.media.jai.util.RWLock.UpgradeNotAllowed;

@SuppressWarnings("serial")
public class MetaViewer extends JPanel{

	private Worksheet worksheet;
	private JPanel meta_viewer_panel;
	private JScrollPane meta_viewer_scroll_pane;
	
	private List<Tile> response_tiles;
	private List<WordDefinitionTile> word_definition_tiles;
	
	private List<DetailResponseViewer> all_tile2s;
	private List<WordDefinitionViewer> word_definition_viewers;
	
	private static int space_between_tiles = 10;
	
	public MetaViewer(Worksheet worksheet){
		
		this.worksheet = worksheet;
		response_tiles = new ArrayList<Tile>();
		word_definition_tiles = new ArrayList<WordDefinitionTile>();
		
		all_tile2s = new ArrayList<DetailResponseViewer>();
		word_definition_viewers = new ArrayList<WordDefinitionViewer>();
		
		setPreferredSize(new Dimension(Tile.viewer_width + 25, WorksheetViewer.viewer_height));
		setLayout(new BorderLayout());
		create_meta_viewer_panel();
		add(meta_viewer_scroll_pane, BorderLayout.CENTER);
		
	}
	
	private void create_meta_viewer_panel(){
		
		meta_viewer_panel = new JPanel();
		//meta_viewer_panel.setBackground(Color.WHITE);
		meta_viewer_scroll_pane = new JScrollPane(meta_viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		meta_viewer_panel.setLayout(new BoxLayout(meta_viewer_panel, BoxLayout.PAGE_AXIS));
		//meta_viewer_panel.setBackground(Color.WHITE);
		
	}
	
	public void updateMetaViewer(QueryResponseReceived query_response){

		/*List<Tile> response_tiles = query_response.getResponseTiles();
		for(int j = 0; j < response_tiles.size(); j++){

			Tile response_tile = response_tiles.get(j);
			this.response_tiles.add(response_tile);
			meta_viewer_panel.add(response_tile.getResponseViewerScrollPane());
				
		}*/
		
		List<DetailResponseViewer> response_tiles = query_response.getTile2s();
		for(int j = 0; j < response_tiles.size(); j++){

			DetailResponseViewer response_tile = response_tiles.get(j);
			this.all_tile2s.add(response_tile);
			meta_viewer_panel.add(response_tile.getViewerScrollPane());
			meta_viewer_panel.add(Box.createRigidArea(new Dimension(meta_viewer_panel.getWidth(), space_between_tiles)));
				
		}

		meta_viewer_panel.revalidate();
		this.revalidate();
		
	}
	
	public void updateMetaViewer(List<WordDefinitionViewer> query_response){

		for(int i = 0; i < query_response.size(); i++){
		
			WordDefinitionViewer response_tile = query_response.get(i);
			this.word_definition_viewers.add(response_tile);
			meta_viewer_panel.add(response_tile.getDefintionViewerScrollPane());
			meta_viewer_panel.add(Box.createRigidArea(new Dimension(meta_viewer_panel.getWidth(), space_between_tiles)));
			
		}

		meta_viewer_panel.revalidate();
		this.revalidate();
		
	}
	
	/*public void updateMetaViewer(List<WordDefinitionTile> query_response){

		for(int i = 0; i < query_response.size(); i++){
		
			WordDefinitionTile response_tile = query_response.get(i);
			this.word_definition_tiles.add(response_tile);
			meta_viewer_panel.add(response_tile.getWordDefinitionTileScrollPane());
			
		}

		meta_viewer_panel.revalidate();
		this.revalidate();
		
	}*/
	
	public void updateMetaViewer(){
		
		meta_viewer_panel.removeAll();
		
		/*for(int i = 0 ; i < response_tiles.size(); i++){
			
			Tile response_tile = response_tiles.get(i);
			
			if(!response_tile.getTileFilteredOut()){
				
				meta_viewer_panel.add(response_tile.getResponseViewerPanel());
				
			}
			
		}*/
		
		for(int i = 0 ; i < all_tile2s.size(); i++){
			
			DetailResponseViewer response_tile = all_tile2s.get(i);
			
			if(!response_tile.getTileFilteredOut()){
				
				meta_viewer_panel.add(response_tile.getViewerScrollPane());
				meta_viewer_panel.add(Box.createRigidArea(new Dimension(meta_viewer_panel.getWidth(), space_between_tiles)));
				
			}
			
		}
		
		/*for(int i = 0; i < word_definition_tiles.size(); i++){
			
			WordDefinitionTile word_definition_tile = word_definition_tiles.get(i);
			
			if(!word_definition_tile.getTileFilteredOut()){
				
				meta_viewer_panel.add(word_definition_tile.getWordDefinitionTileScrollPane());
				
			}
			
		}*/
		
		for(int i = 0; i < word_definition_viewers.size(); i++){
			
			WordDefinitionViewer word_definition_tile = word_definition_viewers.get(i);
			
			if(!word_definition_tile.getTileFilteredOut()){
				
				meta_viewer_panel.add(word_definition_tile.getDefintionViewerScrollPane());
				meta_viewer_panel.add(Box.createRigidArea(new Dimension(meta_viewer_panel.getWidth(), space_between_tiles)));
				
			}
			
		}
		
		meta_viewer_panel.repaint();
		//meta_viewer_panel.revalidate();
		this.revalidate();
		
	}
	
}
