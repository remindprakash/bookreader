package com.bookreader.epub.viewer;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nl.siegmann.epublib.browsersupport.Navigator;

public class BrowseBar extends JPanel {
	
	private static final long serialVersionUID = -5745389338067538254L;
	
	private JLabel headerLabel;
	
	public BrowseBar(Navigator navigator, ContentPane chapterPane) {
		super(new BorderLayout());
		
		add(new SpineSlider(navigator), BorderLayout.NORTH);
		add(new ButtonBar(navigator, chapterPane), BorderLayout.CENTER);
		
		
	}
}
