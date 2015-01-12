package com.bookreader.app.presentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookreader.app.epub.util.ResourceLoader;
import com.bookreader.app.epub.viewer.BrowseBar;
import com.bookreader.app.epub.viewer.ContentPane;
import com.bookreader.app.epub.viewer.GuidePane;
import com.bookreader.app.epub.viewer.NavigationBar;
import com.bookreader.app.epub.viewer.TableOfContentsPane;
import com.bookreader.app.epub.viewer.ToolBar;

public class EpubUI {

	private static final long serialVersionUID = 1L;

	static final Logger log = LoggerFactory.getLogger(EpubUI.class);

	private Navigator navigator = new Navigator();
	private BrowseBar browseBar;
	private ToolBar toolBar;

	public JSplitPane mainSplitPane;
	public JSplitPane leftSplitPane;
	public JSplitPane rightSplitPane;
	public JPanel mainPanel;
	
	public EpubUI(){
		mainSplitPane= new JSplitPane();
		leftSplitPane= new JSplitPane();
		rightSplitPane= new JSplitPane();
		mainPanel= new JPanel(new BorderLayout());
	}
	
	public void InitPanes() {
		EpubUI EpubView = new EpubUI();
		InputStream bookStream = null;
		bookStream = LibraryUI.class.getResourceAsStream("/tirukkural.epub");

		EpubView.Viewer(bookStream);
	}

	public void Viewer(InputStream bookStream) {

		Book book;
		try {
			book = (new EpubReader()).readEpub(bookStream);
			ResourceLoader.loadResourceFromBook(book);
			navigator.gotoBook(book, this);

			tableOfContent(navigator);
			contentWindow(navigator);

			mainPanel = new JPanel(new BorderLayout());

			mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			mainSplitPane.setLeftComponent(leftSplitPane);
			mainSplitPane.setRightComponent(rightSplitPane);
			mainSplitPane.setOneTouchExpandable(true);
			mainSplitPane.setContinuousLayout(true);
			mainSplitPane.setResizeWeight(0.0);

			mainPanel.add(mainSplitPane, BorderLayout.CENTER);
			mainPanel.setPreferredSize(new Dimension(1000, 750));
			mainPanel.add(new NavigationBar(navigator), BorderLayout.NORTH);

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void contentWindow(Navigator navigator) {
		try {

			rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			rightSplitPane.setOneTouchExpandable(true);
			rightSplitPane.setContinuousLayout(true);
			rightSplitPane.setResizeWeight(1.0);

			ContentPane htmlPane = new ContentPane(navigator);
			JPanel contentPanel = new JPanel(new BorderLayout());

			// Menu Item i.e zoom,search,full screen
			// contentPanel.add(toolBar, BorderLayout.NORTH);

			// epub content
			contentPanel.add(htmlPane, BorderLayout.CENTER);
			this.browseBar = new BrowseBar(navigator, htmlPane);
			// SpineSlider and ButtonBar
			contentPanel.add(browseBar, BorderLayout.SOUTH);

			rightSplitPane.setLeftComponent(contentPanel);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void tableOfContent(Navigator navigator) {
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setTopComponent(new TableOfContentsPane(navigator));
		leftSplitPane.setBottomComponent(new GuidePane(navigator));
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setResizeWeight(0.8);
	}

}
