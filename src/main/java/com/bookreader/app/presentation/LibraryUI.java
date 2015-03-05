package com.bookreader.app.presentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookreader.app.epub.viewer.BrowseBar;
import com.bookreader.app.epub.viewer.ContentPane;
import com.bookreader.app.epub.viewer.NavigationBar;
import com.bookreader.app.epub.viewer.TableOfContentsPane;
import com.bookreader.app.epub.viewer.Viewer;
import com.bookreader.app.pdf.viewer.PDFViewer;


public class LibraryUI {
	
	static final Logger log = LoggerFactory.getLogger(LibraryUI.class);
	
	private JSplitPane mainSplitPane; 
	
	private JSplitPane leftSplitPane;
	private JPanel rightSplitPane;
	
	
	
	public LibraryUI(){
		
	}
	
	public JFrame createMainWindow() {
		final JFrame result = new JFrame();
		result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		result.setJMenuBar(new MenuUI());

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		//to be load to db
		JButton libraryButton=new JButton("Your library");
		leftSplitPane.add(libraryButton);
				
		
		
		rightSplitPane = new JPanel(new BorderLayout());
		
		//to be load to db
		JButton bookButton=new JButton("List of books");
		
		bookButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				result.dispose();
				InputStream bookStream=null;
				new PDFViewer(bookStream);
			}
		});
		
		
		rightSplitPane.add(bookButton);
		
		
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setResizeWeight(0.0);
		
		
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);
		mainPanel.setPreferredSize(new Dimension(100, 100));
		
		result.add(mainPanel);
		result.pack();
		result.setVisible(true);
		
		result.setMinimumSize(new Dimension(500, 600));
		
		return result;	
	}
	
}
