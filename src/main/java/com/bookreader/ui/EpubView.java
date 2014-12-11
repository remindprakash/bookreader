package com.bookreader.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookreader.epub.util.FontLoader;
import com.bookreader.epub.viewer.BrowseBar;
import com.bookreader.epub.viewer.ContentPane;
import com.bookreader.epub.viewer.ToolBar;

public class EpubView {
	static final Logger log = LoggerFactory.getLogger(EpubView.class);
	
	private Navigator navigator = new Navigator();
	private BrowseBar browseBar;
	private ToolBar toolBar;
	
	public JPanel Viewer(InputStream bookStream) {
		
		Book book;
		try {
			book = (new EpubReader()).readEpub(bookStream);
			FontLoader.loadFontFromBook(book);
			navigator.gotoBook(book, this);
		
			ContentPane htmlPane = new ContentPane(navigator);
			JPanel contentPanel = new JPanel(new BorderLayout());
			
			//Menu Item i.e zoom,search,full screen
			//contentPanel.add(toolBar, BorderLayout.NORTH);
			
			//epub content
			contentPanel.add(htmlPane, BorderLayout.CENTER);
			this.browseBar = new BrowseBar(navigator, htmlPane);
			//SpineSlider and ButtonBar
			contentPanel.add(browseBar, BorderLayout.SOUTH);
			
			
			return contentPanel;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
