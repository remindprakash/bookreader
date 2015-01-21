package com.bookreader.app.presentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.bookreader.app.config.AppConfig;
import com.bookreader.app.epub.viewer.Viewer;

public class ApplicationStartUp {
	
	static final Logger log = LoggerFactory.getLogger(ApplicationStartUp.class);
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Unable to set native look and feel", e);
		}

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				/*ApplicationContext ctx = 
					      new AnnotationConfigApplicationContext(AppConfig.class);	*/
				
				/*System.out.println(ctx.getMessage("find.person.fail", null, "Error reading resource", Locale.US));
				System.out.println(ctx.getMessage("find.person.fail", null, "Error reading resource", Locale.SIMPLIFIED_CHINESE));*/
				
				Viewer viewer = new Viewer();
				viewer.LoginViewer();
			}
		});
	}
}
