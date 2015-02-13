/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bookreader.ui.Reader;



import java.awt.Dimension;
import java.util.ResourceBundle;
import javax.swing.*;


import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.util.PropertiesManager;


public class ReadPDF 
{
   
    public JPanel ReaderPanel;
    SwingController controller;
    String filepath;
    Dimension PanelDim;
    

    public ReadPDF(String path, Dimension d)
    {
        filepath = path;
        PanelDim = d;
    }
    
    public void InitiateReader()    
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

