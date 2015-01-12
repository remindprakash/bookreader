package com.bookreader.app.epub.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.bookreader.app.epub.search.SearchIndex;
import com.bookreader.app.epub.search.SearchResult;
import com.bookreader.app.epub.search.SearchResults;
import com.bookreader.app.epub.util.ResourceLoader;
import com.bookreader.app.epub.util.ToolsResourceUtil;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.NavigationHistory;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;


/**
 * A toolbar that contains the history back and forward buttons and the page title.
 * 
 * @author paul.siegmann
 *
 */
public class NavigationBar extends JPanel implements NavigationEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1166410773448311544L;
	
	private JTextField searchField;
	private final NavigationHistory navigationHistory;
	private Navigator navigator;
	private SearchIndex searchIndex = new SearchIndex();
	private String previousSearchTerm = null;
	private int searchResultIndex = -1;
	private SearchResults searchResults;
	
	public NavigationBar(Navigator navigator) {
		this.navigationHistory = new NavigationHistory(navigator);
		setBackground(Color.BLACK);
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		addHistoryButtons();
		//titleField = (JTextField) add(new JTextField());
		addSearchButtons();
		initBook(navigator.getBook());
	}

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		searchIndex.initBook(book);
	}

	private void addHistoryButtons() {
		Font historyButtonFont = new Font("SansSerif", Font.BOLD, 24);
		
		
		JButton libButton = ViewerUtil.createButton("liberary", "Library");
		libButton.setFont(historyButtonFont);
		libButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(libButton);
		
		JButton bookButton = ViewerUtil.createButton("bookmark", "BookMark");
		bookButton.setFont(historyButtonFont);
		bookButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(bookButton);
		
		
		JButton showButton = ViewerUtil.createButton("", "Show/Hide");
		showButton.setFont(historyButtonFont);
		showButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(showButton);
		
		
		JButton zoomInButton = ViewerUtil.createButton("", "+");
		zoomInButton.setFont(historyButtonFont);
		zoomInButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(zoomInButton);
		
		JButton zoomOutButton = ViewerUtil.createButton("", "-");
		zoomOutButton.setFont(historyButtonFont);
		zoomOutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(zoomOutButton);
		
		
		
		/*
		JButton previousButton = ViewerUtil.createButton("history-previous", "<=");
		previousButton.setFont(historyButtonFont);
			
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigationHistory.move(-1);
			}
		});
		
		add(previousButton);
		
		JButton nextButton = ViewerUtil.createButton("history-next", "=>");
		nextButton.setFont(historyButtonFont);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				navigationHistory.move(1);
			}
		});
		add(nextButton);
		*/
		
	}

	private void doSearch(int move) {
		String searchTerm = searchField.getText();
		if (searchTerm.equals(previousSearchTerm)) {
			searchResultIndex += move;
		} else {
			searchResults = searchIndex.doSearch(searchTerm);
			previousSearchTerm = searchTerm;
			searchResultIndex = 0;
		}
		if (searchResultIndex < 0) {
			searchResultIndex = searchResults.size() - 1;
		} else if (searchResultIndex >= searchResults.size()) {
			searchResultIndex = 0;
		}
		if (! searchResults.isEmpty()) {
			SearchResult searchResult = searchResults.getHits().get(searchResultIndex);
			navigator.gotoResource(searchResult.getResource(), searchResult.getPagePos(), NavigationBar.this);
		}
		
	}
	
	private void addSearchButtons() {
		JPanel searchForm = new JPanel(new BorderLayout());
		searchForm.setPreferredSize(new Dimension(200, 28));
		Font historyButtonFont = new Font("SansSerif", Font.BOLD, 20);
		JButton previousButton = ViewerUtil.createButton("search-previous", "<");
		previousButton.setFont(historyButtonFont);
//		previousButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
			
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doSearch(-1);
			}
		});
		
		searchForm.add(previousButton, BorderLayout.WEST);

		searchField = new JTextField();
//		JPanel searchInput = new JPanel();
//		searchInput.add(new JLabel(ViewerUtil.createImageIcon("search-icon")));
//		searchInput.add(searchField);
		searchField.setMinimumSize(new Dimension(100, 20));
		searchField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					doSearch(1);
				}
			}
		});
//		searchInput.setMinimumSize(new Dimension(140, 20));
		searchForm.add(searchField, BorderLayout.CENTER);
		JButton nextButton = ViewerUtil.createButton("search-next", ">");
		nextButton.setFont(historyButtonFont);
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doSearch(1);
			}
		});
		searchForm.add(nextButton, BorderLayout.EAST);
		add(searchForm);
		
		
		JButton fullScreenButton = ViewerUtil.createButton("fullscreen", "|||");
		fullScreenButton.setFont(historyButtonFont);
		fullScreenButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(fullScreenButton);
	}

	@Override
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		}
		if (navigationEvent.getCurrentResource() != null) {
			String title = ToolsResourceUtil.getTitle(navigationEvent.getCurrentResource());
			/*titleField.setText(title);
			titleField.setFont(FontLoader.getFontCache().get("0"));*/
		}
	}
}