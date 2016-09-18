package annotationInteraction;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class RunInterface {
	
	private static boolean is_new_worksheet = true;
	private static long worksheet_id = 0;
	
	private static String pattern_id = "70.0.5.3";
	public static String poet_name = "William Shakespeare # ";
	public static String poem_name = "Sonnet 129";
	
	private static JPanel closeReadingContainerPanel;
	private static JPanel distantReadingContainerPanel;
	private static JPanel containerPanel;
	private static JFrame mainFrame;
	
	private static TUIOConnectionManager tuio_data_manager = new TUIOConnectionManager(pattern_id);
	

	public static void main(String[] args) {
		
		//List<PoemCollectionByPoet> poem_collection = WorksheetManager.poem_collection;

		if(is_new_worksheet){
			
			WorksheetManager.createWorksheet(poet_name + poem_name);
			
			
		}
		else{
			
			WorksheetManager.retrieveWorksheet(worksheet_id);
			
		}

		tuio_data_manager.TuioConnect();
		
		
		try {

		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			
			e.printStackTrace();
			
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
            public void run() {

            	create_and_show_GUI(WorksheetManager.active_worksheets);
                
            }
        });

	}
	
	private static void create_and_show_GUI(List<Worksheet> active_worksheets){
		
		closeReadingContainerPanel = new JPanel();
		closeReadingContainerPanel.setLayout(new BorderLayout());
		closeReadingContainerPanel.setBackground(Color.WHITE);

		for(int i = 0; i < active_worksheets.size(); i++){
			
			/*WorksheetViewer worksheet_viewer = new WorksheetViewer(active_worksheets.get(i));
			active_worksheets.get(i).setWorksheetViewer(worksheet_viewer);
			containerPanel.add(worksheet_viewer, BorderLayout.WEST);*/
			
			WorksheetViewer2 worksheet_viewer = new WorksheetViewer2(active_worksheets.get(i));
			active_worksheets.get(i).setWorksheetViewer2(worksheet_viewer);
			closeReadingContainerPanel.add(worksheet_viewer.getViewerScrollPane(), BorderLayout.WEST);
			
			MetaViewer meta_viewer = new MetaViewer(active_worksheets.get(i));
			active_worksheets.get(i).setMetaViewer(meta_viewer);
			//JScrollPane meta_viewer_scroll_pane = new JScrollPane(meta_viewer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			closeReadingContainerPanel.add(meta_viewer, BorderLayout.CENTER);
			
		}
		
		distantReadingContainerPanel = new JPanel();
		distantReadingContainerPanel.setLayout(new BorderLayout());
		distantReadingContainerPanel.setBackground(Color.WHITE);
		
		CorpusViewerAntonymPairs antonym_viewer = new CorpusViewerAntonymPairs();
		SonnetWordPairs sonnet_129_pairs = antonym_viewer.getSonnetAntonymPairs(128);
		CorpusViewerSingleSonnet single_sonnet_viewer = new CorpusViewerSingleSonnet();
		single_sonnet_viewer.changeSonnetTo(129, sonnet_129_pairs.getAntonymPairs());
		distantReadingContainerPanel.add(single_sonnet_viewer.getViewerScrollPane(), BorderLayout.WEST);
		CorpusViewer corpus_viewer_panel = new CorpusViewer();
		distantReadingContainerPanel.add(corpus_viewer_panel.getViewerPanel(), BorderLayout.CENTER);
		
		//JScrollPane distantReadingScrollPane = new JScrollPane(distantReadingContainerPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		containerPanel = new JPanel();
		containerPanel.setLayout(new CardLayout());
		containerPanel.add(closeReadingContainerPanel, "closeReading");
		containerPanel.add(distantReadingContainerPanel, "distantReading");
		//containerPanel.add(distantReadingScrollPane, "distantReading");
		//TODO Remove after testing
		//CorpusViewerAntonymPairs antonym_viewer = new CorpusViewerAntonymPairs();
		//distantReadingContainerPanel.add(antonym_viewer.getViewerScrollPane());
		//SonnetWordPairs all_pairs = antonym_viewer.getSonnetAntonymPairs(153);
		//SingleSonnetViewer single_sonnet_viewer = new SingleSonnetViewer("William Shakespeare # Sonnet 154", false, all_pairs.getAntonymPairs());
		
		//CardLayout containerPanelLayout = (CardLayout)(containerPanel.getLayout());
		//containerPanelLayout.show(containerPanel, "distantReading");
		
		mainFrame = new JFrame("AnnotationInteraction-0.1");
		
		Dimension mainFrameMinimumSize = new Dimension(1200, 500);
		//mainFrame.setMinimumSize(mainFrameMinimumSize);
		mainFrame.setPreferredSize(mainFrameMinimumSize);
		mainFrame.setResizable(true);
		
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().setBackground(Color.WHITE);
	
		//JScrollPane scrollPane = new JScrollPane(containerPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainFrame.getContentPane().add(containerPanel, BorderLayout.CENTER);
		
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.pack();
		mainFrame.setVisible(true);
		
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        
		    	if(tuio_data_manager.isTUIOConnectionEstablished()){
		    		
		    		tuio_data_manager.TuioDisconnect();
		    	
		    	}
		    	
		    	List<Worksheet> active_worksheets = WorksheetManager.active_worksheets;
		    	
		    	for(int i = 0; i < active_worksheets.size(); i++){
		    		//TODO uncomment this after testing
		    		//active_worksheets.get(i).saveWorksheetTIF();
		    		active_worksheets.get(i).getQueryResponseExecutorService().shutdown();
		    		
		    	}
		    	
		    	mainFrame.setVisible(false);
		    	mainFrame.dispose();
		    	System.exit(0);
		    	
		    }
		    
		});
		
		
	}
	
	public static void switchCloseAndDistant(String switchTo){
		
		String panelToDisplay = switchTo;
		CardLayout containerPanelLayout = (CardLayout)(containerPanel.getLayout());
		containerPanelLayout.show(containerPanel, panelToDisplay);
		
		mainFrame.repaint();
		mainFrame.validate();
		
	}

}