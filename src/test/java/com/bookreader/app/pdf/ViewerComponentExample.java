package com.bookreader.app.pdf;

import java.awt.BorderLayout;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;

import com.bookreader.ui.Reader.PDFViewer;

public class ViewerComponentExample {
	
	public static void main(String[] args) {
        
		//System.getProperties().put("org.icepdf.core.awtFontLoading", "true");
		
        // build a component controller
        SwingController controller = new SwingController();
        
        controller.setIsEmbeddedComponent(true);
        
        
        
        PropertiesManager properties = new PropertiesManager(
                System.getProperties(),new Properties(), 
                controller.getMessageBundle());
       
        
        /*properties.set(PropertiesManager.PROPERTY_SHOW_UTILITY_SAVE, "FALSE");
        properties.set(PropertiesManager.PROPERTY_SHOW_UTILITY_PRINT, "FALSE");
        properties.set(PropertiesManager.PROPERTY_SHOW_UTILITY_SEARCH, "FALSE");
        
        properties.set(PropertiesManager.PROPERTY_SHOW_UTILITYPANE_ANNOTATION, "FALSE");
        properties.set(PropertiesManager.PROPERTY_SHOW_UTILITYPANE_BOOKMARKS, "FALSE");
        
        properties.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, "FALSE");
        properties.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, "FALSE");
        properties.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, "FALSE");*/
        
     
        SwingViewBuilder factory = new SwingViewBuilder(controller, properties);

        JPanel viewerComponentPanel = factory.buildViewerPanel();

        // add interactive mouse link annotation support via callback
        controller.getDocumentViewController().setAnnotationCallback(
                new org.icepdf.ri.common.MyAnnotationCallback(
                        controller.getDocumentViewController()));

        JFrame applicationFrame = new JFrame();
        applicationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationFrame.add(viewerComponentPanel, BorderLayout.CENTER);

        // Now that the GUI is all in place, we can try openning a PDF
        
        InputStream pdfStream = PDFViewer.class.getResourceAsStream("/Bharathiar_Panchali_Sapatham.pdf");
        //InputStream pdfStream = PDFViewer.class.getResourceAsStream("/HadoopinAction.pdf");
        
        controller.openDocument(pdfStream, "", "");
        
        


        // show the component
        applicationFrame.pack();
        applicationFrame.setVisible(true);
    }

	
	
}
