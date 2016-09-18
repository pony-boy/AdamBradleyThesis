package annotationInteraction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class CorpusViewerSingleSonnet {
	
	private int sonnet_being_displayed;
	
	private JPanel viewer_panel;
	private JScrollPane viewer_scroll_pane;
	
	List<SingleSonnetViewer> previously_displayed_sonnets;
	
	//TODO will have a SingleSonnetViewer and a back button to switch cards (single sonnet / antonnym pairs)
	public CorpusViewerSingleSonnet() {
		
		sonnet_being_displayed = -1;
		previously_displayed_sonnets = new ArrayList<SingleSonnetViewer>();
		
		viewer_panel = new JPanel();
		viewer_panel.setBackground(Color.WHITE);
		viewer_panel.setLayout(new BorderLayout());
	
		viewer_scroll_pane = new JScrollPane(viewer_panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
	}
	
	public JPanel getViewerPanel(){
		
		return viewer_panel;
		
	}
	
	public JScrollPane getViewerScrollPane(){
		
		return viewer_scroll_pane;
		
	}
	
	public void changeSonnetTo(int sonnet_id, List<WordPair> antonyms){

		boolean is_original_sonnet = false;
		if(sonnet_id == 129){
			
			is_original_sonnet = true;
			
		}
		
		if(sonnet_being_displayed != sonnet_id){
			
			viewer_panel.removeAll();
			
			SingleSonnetViewer sonnet_viewer = null;
			
			for(int i = 0; i < previously_displayed_sonnets.size(); i++){
				
				SingleSonnetViewer sonnet_display = previously_displayed_sonnets.get(i);
				if(sonnet_display.getSonnetId() == sonnet_id){
					
					sonnet_viewer = sonnet_display;
					break;
					
				}
				
			}
			
			if(sonnet_viewer == null){
				
				sonnet_viewer = new SingleSonnetViewer(sonnet_id, is_original_sonnet, antonyms);
				previously_displayed_sonnets.add(sonnet_viewer);
				
			}
			
			viewer_panel.add(sonnet_viewer.getViewerPanel(), BorderLayout.NORTH);
			
			Dimension sonnet_viewer_preferred_size = sonnet_viewer.getViewerPanel().getPreferredSize();
			viewer_panel.setPreferredSize(new Dimension(sonnet_viewer_preferred_size.width + 20, sonnet_viewer_preferred_size.height + 20));
			
			viewer_panel.repaint();
			viewer_panel.revalidate();
			
		}
		
	}

}
