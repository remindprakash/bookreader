package com.bookreader.app.epub.viewer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SizeRequirements;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.ViewFactory; 
import javax.swing.text.View; 

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookreader.app.epub.util.DesktopUtil;
import com.bookreader.app.epub.util.ResizableHTMLEditorKit;
import com.bookreader.app.epub.util.ResourceLoader;
import com.bookreader.app.epub.util.TextWrapHTMLEditorKit;
import com.bookreader.app.mulipage.EditorPanePrinter;
import com.bookreader.app.zoom.ScaledTextPane;




/**
 * Displays a page
 * 
 */
public class ContentPane extends JPanel implements NavigationEventListener,
		HyperlinkListener, Pageable {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = LoggerFactory
			.getLogger(ContentPane.class);
	private Navigator navigator;
	private Resource currentResource;
	public JEditorPane editorPane;
	public JScrollPane scrollPane;
	private HTMLDocumentFactory htmlDocumentFactory;
	
	CardLayout cardLayout = new CardLayout();
	ArrayList<PagePanel> pages;
	PageFormat pageFormat;
	JEditorPane sourcePane;
	Paper paper;
	Insets margins;
	int pageWidth;
    int pageHeight;
    View rootView;
    public static int PAGE_SHIFT=20;
	
    private static Double zoom=1.0;
    
	public ContentPane(Navigator navigator) {
		super(new GridLayout(1, 0));
		
		
		this.scrollPane = (JScrollPane) add(new JScrollPane());
		
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		//this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.scrollPane.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					int newY = (int) (viewPosition.getY() + 10);
					scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
				}
			}
		});
		this.scrollPane.addMouseWheelListener(new MouseWheelListener() {
			
			private boolean gotoNextPage = false;
			private boolean gotoPreviousPage = false;

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			    int notches = e.getWheelRotation();
			    int increment = scrollPane.getVerticalScrollBar().getUnitIncrement(1);
			    if (notches < 0) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					if (viewPosition.getY() - increment < 0) {
						if (gotoPreviousPage) {
							gotoPreviousPage = false;
							ContentPane.this.navigator.gotoPreviousSpineSection(-1, ContentPane.this);
						} else {
							gotoPreviousPage = true;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), 0));
						}
					}
			    } else {
			    	// only move to the next page if we are exactly at the bottom of the current page
			    	Point viewPosition = scrollPane.getViewport().getViewPosition();
					int viewportHeight = scrollPane.getViewport().getHeight();
					int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
					if (viewPosition.getY() + viewportHeight + increment > scrollMax) {
						if (gotoNextPage) {
							gotoNextPage = false;
							ContentPane.this.navigator.gotoNextSpineSection(ContentPane.this);
						} else {
							gotoNextPage = true;
							int newY = scrollMax - viewportHeight;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
						}
					}
			    }
			  }
		});
		
		
		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		this.editorPane = createJEditorPane();
		
		scrollPane.setAutoscrolls(true);
		
		
		scrollPane.getViewport().add(editorPane);
		
		
		this.htmlDocumentFactory = new HTMLDocumentFactory(navigator, editorPane.getEditorKit());
		initBook(navigator.getBook());
		
		
		
		
		JButton zoomInButton = ViewerUtil.createButton("", "+");
		
		zoomInButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom=zoom+0.5;
				editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(zoom));
				System.out.println(zoom);
				
			}
		});
		
		add(zoomInButton, BorderLayout.NORTH);
		
		JButton zoomOutButton = ViewerUtil.createButton("", "-");
		
		zoomOutButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				zoom=zoom-0.5;
				editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(zoom));
				System.out.println(zoom);
			}
		});
		
		add(zoomOutButton, BorderLayout.SOUTH);
		
		
		
		
	}

	
	
	
	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		htmlDocumentFactory.init(book);
		displayPage(book.getCoverPage());
	}
	
	
	
	/**
	 * Whether the given searchString matches any of the possibleValues.
	 * 
	 * @param searchString
	 * @param possibleValues
	 * @return Whether the given searchString matches any of the possibleValues.
	 */
	private static boolean matchesAny(String searchString, String... possibleValues) {
		for (int i = 0; i < possibleValues.length; i++) {
			String attributeValue = possibleValues[i];
			if (StringUtils.isNotBlank(attributeValue) && (attributeValue.equals(searchString))) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Scrolls the editorPane to the startOffset of the current element in the elementIterator
	 * 
	 * @param requestFragmentId
	 * @param attributeValue
	 * @param editorPane
	 * @param elementIterator
	 * 
	 * @return whether it was a match and we jumped there.
	 */
	private static void scrollToElement(JEditorPane editorPane, HTMLDocument.Iterator elementIterator) {
		try {
			Rectangle rectangle = editorPane.modelToView(elementIterator.getStartOffset());
			if (rectangle == null) {
				return;
			}
			// the view is visible, scroll it to the
			// center of the current visible area.
			Rectangle visibleRectangle = editorPane.getVisibleRect();
			// r.y -= (vis.height / 2);
			rectangle.height = visibleRectangle.height;
			editorPane.scrollRectToVisible(rectangle);
		} catch (BadLocationException e) {
			log.error(e.getMessage());
		}
	}
	
	
	/**
	 * Scrolls the editorPane to the first anchor element whose id or name matches the given fragmentId.
	 * 
	 * @param fragmentId
	 */
	private void scrollToNamedAnchor(String fragmentId) {
		HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
		for (HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A); iter.isValid(); iter.next()) {
			AttributeSet attributes = iter.getAttributes();
			if (matchesAny(fragmentId, (String) attributes.getAttribute(HTML.Attribute.NAME),
					(String) attributes.getAttribute(HTML.Attribute.ID))) {
				scrollToElement(editorPane, iter);
				break;
			}
		}
	}

	private JEditorPane createJEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		
		editorPane.setBackground(Color.white);
		editorPane.setEditable(false);
		
		//editorPane.setEditorKit( new HTMLEditorKit());
		editorPane.setEditorKit( new ResizableHTMLEditorKit());
		//editorPane.setEditorKit( new TextWrapHTMLEditorKit());
		
		
		editorPane.addHyperlinkListener(this);
		
		
		
		
		
		editorPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
					navigator.gotoNextSpineSection(ContentPane.this);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
					navigator.gotoPreviousSpineSection(ContentPane.this);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
				ContentPane.this.gotoPreviousPage();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE
					|| (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)) {
					ContentPane.this.gotoNextPage();
				}
			}
		});
		
		
		return editorPane;
	}
	

	public void displayPage(Resource resource) {
		displayPage(resource, 0);
	}

	public void displayPage(Resource resource, int sectionPos) {
		if (resource == null) {
			return;
		}
		try {
			
			HTMLDocument document = htmlDocumentFactory.getDocument(resource);
			
			if (document == null) {
				return;
			}
			currentResource = resource;
			
			
			
			/*StringWriter writer = new StringWriter();
			editorPane.getEditorKit().write(writer, document, 0, document.getLength());
			String s = writer.toString();
			
			editorPane.setText(s);*/
			
		
			if(ResourceLoader.getCSSCache().size()!=0 || !ResourceLoader.getCSSCache().isEmpty()){
				((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(IOUtils.toString(ResourceLoader.getCSSCache().get("0"), "UTF-8"));
				
				//((HTMLDocument)editorPane.getDocument()).getStyleSheet().importStyleSheet(Viewer.class.getResource("/template.css"));
				
			}
			
			
			editorPane.setDocument(document);
				
			
				
			/*System.out.println("Current Font"+editorPane.getFont().getSize());
			
			Font f=editorPane.getFont();
			
			// create a new, smaller font from the current font
			 Font f2 = new Font(f.getFontName(), f.getStyle(), 60);
			 
			 // set the new font in the editing area
			 editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
			 editorPane.setFont(f2);
			 
			 System.out.println("New Font"+editorPane.getFont().getSize());*/
			
			
			
			
			if(ResourceLoader.getFontCache().size()!=0 || !ResourceLoader.getFontCache().isEmpty()){
				Font font=ResourceLoader.getFontCache().get("0");
				
				editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				editorPane.setFont(font);
			}
			
			
			
			Paper p=new Paper(); //by default LETTER
	        p.setImageableArea(0,0,p.getWidth(), p.getHeight());
	        
	       //editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(2.5));
			
	       //initData(editorPane,p,new Insets(18,18,18,18));
			
			scrollToCurrentPosition(sectionPos);
			
			editorPane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			
			scrollPane.setBorder(BorderFactory.createLineBorder(Color.RED));
			
			
			
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
	}
	
	public void SetZoomIn(){
		System.out.println("bharath");
		this.editorPane.getDocument().putProperty("i18n", Boolean.FALSE);
		this.editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(1.2));
	}
	

	private void scrollToCurrentPosition(int sectionPos) {
		if (sectionPos < 0) {
			editorPane.setCaretPosition(editorPane.getDocument().getLength());
		} else {
			editorPane.setCaretPosition(sectionPos);
		}
		if (sectionPos == 0) {
			scrollPane.getViewport().setViewPosition(new Point(0, 0));
		} else if (sectionPos < 0) {
			int viewportHeight = scrollPane.getViewport().getHeight();
			int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
			scrollPane.getViewport().setViewPosition(new Point(0, scrollMax - viewportHeight));
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
			return;
		}
        final URL url = event.getURL();
        if (url.getProtocol().toLowerCase().startsWith("http") && !"".equals(url.getHost())) {
            try {
                DesktopUtil.launchBrowser(event.getURL());
                return;
            } catch (DesktopUtil.BrowserLaunchException ex) {
                log.warn("Couldn't launch system web browser.", ex);
            }
        }
		String resourceHref = calculateTargetHref(event.getURL());
		if (resourceHref.startsWith("#")) {
			scrollToNamedAnchor(resourceHref.substring(1));
			return;
		}

		Resource resource = navigator.getBook().getResources().getByHref(resourceHref);
		if (resource == null) {
			log.error("Resource with url " + resourceHref + " not found");
		} else {
			navigator.gotoResource(resource, this);
		}
	}

	public void gotoPreviousPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		if (viewPosition.getY() <= 0) {
			navigator.gotoPreviousSpineSection(this);
			return;
		}
		int viewportHeight = scrollPane.getViewport().getHeight();
		int newY = (int) viewPosition.getY();
		newY -= viewportHeight;
		newY = Math.max(0, newY - viewportHeight);
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
		
		//((CardLayout) this.getLayout()).previous(this);
	}

	public void gotoNextPage() {
		
		
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		int viewportHeight = scrollPane.getViewport().getHeight();
		int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
		if (viewPosition.getY() + viewportHeight >= scrollMax) {
			navigator.gotoNextSpineSection(this);
			return;
		}
		int newY = ((int) viewPosition.getY()) + viewportHeight;
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
		
		//((CardLayout) this.getLayout()).next(this);
	}

	
	/**
	 * Transforms a link generated by a click on a link in a document to a resource href.
	 * Property handles http encoded spaces and such.
	 * 
	 * @param clickUrl
	 * @return a link generated by a click on a link transformed into a document to a resource href.
	 */
	private String calculateTargetHref(URL clickUrl) {
		String resourceHref = clickUrl.toString();
		try {
			resourceHref = URLDecoder.decode(resourceHref,
					Constants.CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		resourceHref = resourceHref.substring(ImageLoaderCache.IMAGE_URL_PREFIX
				.length());

		if (resourceHref.startsWith("#")) {
			return resourceHref;
		}
		if (currentResource != null
				&& StringUtils.isNotBlank(currentResource.getHref())) {
			int lastSlashPos = currentResource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				resourceHref = currentResource.getHref().substring(0,
						lastSlashPos + 1)
						+ resourceHref;
			}
		}
		return resourceHref;
	}

	
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		} else {
			if (navigationEvent.isResourceChanged()) {
			displayPage(navigationEvent.getCurrentResource(),
					navigationEvent.getCurrentSectionPos());
			} else if (navigationEvent.isSectionPosChanged()) {
				editorPane.setCaretPosition(navigationEvent.getCurrentSectionPos());
			}
			if (StringUtils.isNotBlank(navigationEvent.getCurrentFragmentId())) {
				scrollToNamedAnchor(navigationEvent.getCurrentFragmentId());
			}
		}
	}
	
	
	
	//Pageable
	
	
	
	

	public void initData(JEditorPane pane, Paper paper, Insets margins){
		
		
		
		
		
		paper.setSize(getSize().width-40, getSize().height-40);
		
		this.sourcePane=pane;
		
		
        this.paper=paper;
        this.margins=margins;
        this.pageWidth=(int)paper.getWidth();
        
        
        this.pageHeight=(int)paper.getHeight();
        pageFormat=new PageFormat();
        paper.setImageableArea(0,0,paper.getWidth(), paper.getHeight());
        pageFormat.setPaper(paper);

        doPagesLayout();
	}
	
	public void doPagesLayout() {
    	try{
    		setLayout(null);
            removeAll();
            setLayout(cardLayout);
           
            this.rootView=sourcePane.getUI().getRootView(sourcePane);
            
            sourcePane.setSize( this.pageWidth-margins.top-margins.bottom,Integer.MAX_VALUE);
            Dimension d=sourcePane.getPreferredSize();
            sourcePane.setSize( this.pageWidth-margins.top-margins.bottom, d.height);

            calculatePageInfo();
           
            
            //sourcePane.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            
            
            
            
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
        
    }
	
	protected void calculatePageInfo() {
        pages =new ArrayList<PagePanel>();
        int startY=0;
        int endPageY=getEndPageY(startY);
        while (startY+pageHeight-margins.top-margins.bottom<sourcePane.getHeight()) {
            Shape pageShape=getPageShape(startY, pageWidth-margins.left-margins.right,pageHeight-margins.top-margins.bottom, sourcePane);
            pages.add(new PagePanel(startY,endPageY, pageShape));
            startY=endPageY;
            endPageY=getEndPageY(startY);
        }
        Shape pageShape=getPageShape(startY, pageWidth-margins.left-margins.right,pageHeight-margins.top-margins.bottom, sourcePane);
        pages.add(new PagePanel(startY,endPageY, pageShape));

        int count=0;
        for (PagePanel pi: pages) {
            add(pi);
            pi.setLocation(PAGE_SHIFT, PAGE_SHIFT+count*(pageHeight+PAGE_SHIFT));
            count++;
        }
    }
	
	
	protected int getEndPageY(int startY) {
        int desiredY=startY+pageHeight-margins.top-margins.bottom;
        int realY=desiredY;
        for (int x=1; x<pageWidth; x++) {
            View v=getLeafViewAtPoint(new Point(x,realY), rootView);
            if (v!=null) {
                Rectangle alloc=getAllocation(v, sourcePane).getBounds();
                if (alloc.height>pageHeight-margins.top-margins.bottom) {
                    continue;
                }
                if (alloc.y+alloc.height>desiredY) {
                    realY=Math.min(realY, alloc.y);
                }
            }
        }
        
        return realY;
    }
	
	protected static Shape getAllocation(View v, JEditorPane edit) {
        Insets ins=edit.getInsets();
        View root=edit.getUI().getRootView(edit);
        View vParent=v.getParent();
        int x=ins.left;
        int y=ins.top;
        while(vParent!=null) {
            int i=vParent.getViewIndex(v.getStartOffset(), Position.Bias.Forward);
            Shape alloc=vParent.getChildAllocation(i, new Rectangle(0,0, Short.MAX_VALUE, Short.MAX_VALUE));
            x+=alloc.getBounds().x;
            y+=alloc.getBounds().y;

            vParent=vParent.getParent();
        }

        return new Rectangle(x,y, (int)v.getPreferredSpan(View.X_AXIS), (int)v.getPreferredSpan(View.Y_AXIS));
    }
	
	
	public static Shape getPageShape(int pageStartY, int pageWidth, int pageHeight, JEditorPane sourcePane) {
        Area result=new Area(new Rectangle(0, 0, pageWidth, pageHeight));
        View rootView=sourcePane.getUI().getRootView(sourcePane);
        Rectangle last=new Rectangle();
        for (int x=1; x<pageWidth; x++) {
            View v=getLeafViewAtPoint(new Point(x,pageStartY), rootView, sourcePane);
            if (v!=null) {
                Rectangle alloc=getAllocation(v, sourcePane).getBounds();
                if (alloc.y<pageStartY && alloc.y+alloc.height>pageStartY) {
                    if (!alloc.equals(last)) {
                        Rectangle r=new Rectangle(alloc);
                        r.y-=pageStartY;
                        result.subtract(new Area(r));
                    }
                }
                last=alloc;
            }
        }

        last=new Rectangle();
        for (int x=1; x<pageWidth; x++) {
            View v=getLeafViewAtPoint(new Point(x,pageStartY+pageHeight), rootView, sourcePane);
            if (v!=null) {
                Rectangle alloc=getAllocation(v, sourcePane).getBounds();
                if (alloc.y<pageStartY+pageHeight && alloc.y+alloc.height>pageStartY+pageHeight) {
                    if (!alloc.equals(last)) {
                        Rectangle r=new Rectangle(alloc);
                        r.y-=pageStartY;
                        result.subtract(new Area(r));
                    }
                }
                last=alloc;
            }
        }

        return result;
    }
	
	
	protected View getLeafViewAtPoint(Point p, View root) {
        return getLeafViewAtPoint(p, root, sourcePane);
    }
    
    public static View getLeafViewAtPoint(Point p, View root, JEditorPane sourcePane) {
        int pos=sourcePane.viewToModel(p);
        View v=sourcePane.getUI().getRootView(sourcePane);
        while (v.getViewCount()>0) {
            int i=v.getViewIndex(pos, Position.Bias.Forward);
            v=v.getView(i);
        }
        Shape alloc=getAllocation(root, sourcePane);
        if (alloc.contains(p)) {
            return v;
        }

        return null;
    }
    
    
    class PagePanel extends JPanel {
        int pageStartY;
        int pageEndY;
        Shape pageShape;
        boolean isPrinting=false;
        JPanel innerPage=new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                
                AffineTransform old=g2d.getTransform();
                
               
                
                Shape oldClip=g2d.getClip();

                Area newClip=new Area(oldClip);
                if (isPrinting) {
                    newClip=new Area(pageShape);
                }
                else {
                    newClip.intersect(new Area(pageShape));
                }
                g2d.setClip(newClip);

                g2d.translate(0, -pageStartY);

                sourcePane.paint(g2d);
                for (Component c:sourcePane.getComponents() ) {
                    AffineTransform tmp=g2d.getTransform();
                    g2d.translate(c.getX(), c.getY());
                    ((Container)c).getComponent(0).paint(g2d);
                    g2d.setTransform(tmp);
                }

                g2d.setTransform(old);
                g2d.setClip(oldClip); 
            }
            
        };

        public PagePanel() {
            this (0, 0, null);
        }
        
        public PagePanel(int pageStartY, int pageEndY, Shape pageShape) {
            this.pageStartY=pageStartY;
            this.pageEndY=pageEndY;
            this.pageShape=pageShape;

            setSize(pageWidth, pageHeight);
            setLayout(null);
            add(innerPage);
            innerPage.setBounds(margins.left,  margins.top, pageWidth-margins.left-margins.right, pageHeight-margins.top-margins.bottom);
            
            
        }
        
       
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.black);
            g.drawRect(0,0, getWidth()-2, getHeight()-2);
        }
       
    }
    
    
	//pageable methods
	
	@Override
	public int getNumberOfPages() {
		return pages.size();
	}



	@Override
	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		 return pageFormat;
	}



	@Override
	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {
		 return (Printable) this;
	}
	
	

}
