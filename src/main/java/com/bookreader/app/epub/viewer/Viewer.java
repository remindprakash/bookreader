package com.bookreader.app.epub.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.BookProcessor;
import nl.siegmann.epublib.epub.BookProcessorPipeline;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bookreader.app.config.AppConfig;
import com.bookreader.app.epub.util.ResourceLoader;
import com.bookreader.app.presentation.LibraryUI;
import com.bookreader.app.presentation.LoginUI;



public class Viewer {
	
	static final Logger log = LoggerFactory.getLogger(Viewer.class);
	private JFrame mainWindow;
	private BrowseBar browseBar;
	private JSplitPane mainSplitPane; 
	
	private JSplitPane leftSplitPane;
	private JPanel rightSplitPane;
	
	private Navigator navigator = new Navigator();
	private NavigationHistory browserHistory;
	private BookProcessorPipeline epubCleaner = new BookProcessorPipeline(Collections.<BookProcessor>emptyList());
	private LoginUI login;

	public Viewer(){
		
	}
	
	public Viewer(InputStream bookStream) {
		mainWindow = createMainWindow();
		Book book;
		try {
			book = (new EpubReader()).readEpub(bookStream);
			
			book.getMetadata();
			
			ResourceLoader.loadResourceFromBook(book);
			gotoBook(book);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void LoginViewer(){
		try{
			login = new LoginUI();
			login.placeComponents();
		}
		catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	

	private JFrame createMainWindow() {
		JFrame result = new JFrame();
		result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		result.setJMenuBar(createMenuBar());

		JPanel mainPanel = new JPanel(new BorderLayout());
		
		leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		leftSplitPane.setTopComponent(new TableOfContentsPane(navigator));
		leftSplitPane.setBottomComponent(new GuidePane(navigator));
		leftSplitPane.setOneTouchExpandable(true);
		leftSplitPane.setContinuousLayout(true);
		leftSplitPane.setResizeWeight(0.8);
		
		rightSplitPane = new JPanel(new BorderLayout());
		ContentPane htmlPane = new ContentPane(navigator);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(htmlPane, BorderLayout.CENTER);
		this.browseBar = new BrowseBar(navigator, htmlPane);
		contentPanel.add(browseBar, BorderLayout.SOUTH);
		rightSplitPane.add(contentPanel);
		
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane.setLeftComponent(leftSplitPane);
		mainSplitPane.setRightComponent(rightSplitPane);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setResizeWeight(0.0);
		
		mainPanel.add(mainSplitPane, BorderLayout.CENTER);
		mainPanel.setPreferredSize(new Dimension(1000, 750));
		
		mainPanel.add(new NavigationBar(navigator,htmlPane), BorderLayout.NORTH);

		result.add(mainPanel);
		result.pack();
		setLayout(Layout.TocContent);
		result.setVisible(true);
		
		return result;	
	}
	
	
	
	private void gotoBook(Book book) {
		mainWindow.setTitle(book.getTitle());
		navigator.gotoBook(book, this);
	}

	private static String getText(String text) {
		return text;
	}
	
	private static JFileChooser createFileChooser(File startDir) {
		if (startDir == null) {
			startDir = new File(System.getProperty("user.home"));
			if (! startDir.exists()) {
				startDir = null;
			}
		}
		JFileChooser fileChooser = new JFileChooser(startDir);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileFilter(new FileNameExtensionFilter("EPub files", "epub"));
				     
		return fileChooser;
	}
	
	private JMenuBar createMenuBar() {
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(getText("File"));
		menuBar.add(fileMenu);
		JMenuItem openFileMenuItem = new JMenuItem(getText("Open"));
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		openFileMenuItem.addActionListener(new ActionListener() {

			private File previousDir;
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = createFileChooser(previousDir);
				int returnVal = fileChooser.showOpenDialog(mainWindow);
				if(returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				if (! selectedFile.isDirectory()) {
					previousDir = selectedFile.getParentFile();
				}
				try {
					Book book = (new EpubReader()).readEpub(new FileInputStream(selectedFile));
					gotoBook(book);
				} catch (Exception e1) {
					log.error(e1.getMessage(), e1);
				}
			}
		});
		fileMenu.add(openFileMenuItem);

		
		//Save Function
		
		/*
		JMenuItem saveFileMenuItem = new JMenuItem(getText("Save as ..."));
		saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
		saveFileMenuItem.addActionListener(new ActionListener() {

			private File previousDir;
			
			public void actionPerformed(ActionEvent e) {
				if (navigator.getBook() == null) {
					return;
				}
				JFileChooser fileChooser = createFileChooser(previousDir);
				int returnVal = fileChooser.showOpenDialog(mainWindow);
				if(returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				if (! selectedFile.isDirectory()) {
					previousDir = selectedFile.getParentFile();
				}
				try {
					(new EpubWriter()).write(navigator.getBook(), new FileOutputStream(selectedFile));
				} catch (Exception e1) {
					log.error(e1.getMessage(), e1);
				}
			}
		});
		fileMenu.add(saveFileMenuItem);
		*/
		
		JMenuItem reloadMenuItem = new JMenuItem(getText("Reload"));
		reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		reloadMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gotoBook(navigator.getBook());
			}
		});
		fileMenu.add(reloadMenuItem);
		
		JMenuItem logOutMenuItem = new JMenuItem(getText("LogOut"));
		logOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK));
		logOutMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mainWindow.dispose();
				LoginViewer();
				
			}
		});
		fileMenu.add(logOutMenuItem);
		

		JMenuItem exitMenuItem = new JMenuItem(getText("Exit"));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		exitMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		
		JMenu viewMenu = new JMenu(getText("View"));
		menuBar.add(viewMenu);
		
		JMenuItem viewTocContentMenuItem = new JMenuItem(getText("TOCContent"), ViewerUtil.createImageIcon("layout-toc-content"));
		viewTocContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK));
		viewTocContentMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setLayout(Layout.TocContent);
			}
		});
		viewMenu.add(viewTocContentMenuItem);

		JMenuItem viewContentMenuItem = new JMenuItem(getText("Content"), ViewerUtil.createImageIcon("layout-content"));
		viewContentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK));
		viewContentMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setLayout(Layout.Content);
			}
		});
		viewMenu.add(viewContentMenuItem);

		
		JMenu helpMenu = new JMenu(getText("Help"));
		menuBar.add(helpMenu);
		JMenuItem aboutMenuItem = new JMenuItem(getText("About"));
		aboutMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new AboutDialog(Viewer.this.mainWindow);
			}
		});
		helpMenu.add(aboutMenuItem);

		return menuBar;
	}

	private enum Layout {
		TocContent,
		Content
	}

	private class LayoutX {
		private boolean tocPaneVisible;
		private boolean contentPaneVisible;
		private boolean metaPaneVisible;
		
	}
	private void setLayout(Layout layout) {
		switch (layout) {
			case Content:
				mainSplitPane.setDividerLocation(0.0d);
				break;
			case TocContent:
				mainSplitPane.setDividerLocation(0.2d);
				break;
		}
	}
	
	
	public void InitPanes() {
		InputStream bookStream = null;
		bookStream = Viewer.class.getResourceAsStream("/tirukkural.epub");
		new Viewer(bookStream);
	}
	
}
