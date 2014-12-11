package com.bookreader.app.main;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateViewer {
	static final Logger log = LoggerFactory.getLogger(TemplateViewer.class);
	
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Unable to set native look and feel", e);
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TemplateViewer();
			}
		});
	}
}
