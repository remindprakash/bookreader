package com.bookreader.services;

import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.*;

import org.icepdf.core.views.DocumentViewController;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.util.PropertiesManager;

public class ReaderService 
{

	   
    public JPanel ReaderPanel;
    SwingController controller;
    DocumentViewController viewController;
    String filepath;
    Dimension PanelDim;
    

    public ReaderService(String path, Dimension d)
    {
        filepath = path;
        PanelDim = d;
    }
    
    public void Read()    
    {   
        
        controller = new SwingController();

// Build a SwingViewFactory configured with the controller
SwingViewBuilder factory = new SwingViewBuilder(controller);

// Use the factory to build a JPanel that is pre-configured
//with a complete, active Viewer UI.
ReaderPanel = factory.buildViewerPanel();

// add copy keyboard command
ComponentKeyBinding.install(controller, ReaderPanel);

// add interactive mouse link annotation support via callback
controller.getDocumentViewController().setAnnotationCallback(
      new org.icepdf.ri.common.MyAnnotationCallback(
             controller.getDocumentViewController()));


// Open a PDF document to view
controller.openDocument(filepath);
 controller.setPageViewMode(DocumentViewControllerImpl.ONE_PAGE_VIEW,false);       

    }     
    
}

