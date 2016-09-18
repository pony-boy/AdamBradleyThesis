package annotationInteraction;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

public class CorpusViewer {

	private static JPanel viewer_panel;
	private static CorpusViewerAntonymPairs antonym_pairs_card;
	private static CorpusViewerSingleSonnet sonnet_card;
	
	public CorpusViewer(){
		
		viewer_panel = new JPanel();
		viewer_panel.setLayout(new CardLayout());
		
		antonym_pairs_card = new CorpusViewerAntonymPairs();
		sonnet_card = new CorpusViewerSingleSonnet();
		
		viewer_panel.add(antonym_pairs_card.getViewerScrollPane(), "antonyms");
		viewer_panel.add(sonnet_card.getViewerScrollPane(), "sonnet");
		
	}
	
	public static void switchToSonnet(int sonnet_id, List<WordPair> antonyms){
		
		sonnet_card.changeSonnetTo(sonnet_id, antonyms);
		CardLayout containerPanelLayout = (CardLayout)(viewer_panel.getLayout());
		containerPanelLayout.show(viewer_panel, "sonnet");
		
	}
	
	public static void switchToAntonyms(){
		
		CardLayout containerPanelLayout = (CardLayout)(viewer_panel.getLayout());
		containerPanelLayout.show(viewer_panel, "antonyms");
		
	}
	
	public JPanel getViewerPanel(){
		
		return viewer_panel;
		
	}

}
