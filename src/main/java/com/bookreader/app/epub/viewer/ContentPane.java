package com.bookreader.app.epub.viewer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
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
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.View; 

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bookreader.app.epub.util.DesktopUtil;
import com.bookreader.app.epub.util.ResizableHTMLEditorKit;
import com.bookreader.app.epub.util.ResourceLoader;
import com.bookreader.app.epub.util.TextWrapHTMLEditorKit;





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
    private double oldYMax;
    private double newYMax;
    private double percentage;
    
    private int pageCount;
    private int pageYAxis;
    
    
    
    
    
    
   
	public ContentPane(Navigator navigator) {
		super(new GridLayout(1, 0));
		
		
		this.scrollPane = (JScrollPane) add(new JScrollPane());
		
		this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		//this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		
		//Mouse Wheel Listener page up and down
		this.scrollPane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			    int notches = e.getWheelRotation();
			    
			    if(notches < 0){
			    	gotoPreviousPage();
			    }
			    else if(notches > 0){
			    	gotoNextPage();
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
		editorPane.setOpaque(true);
		
		//editorPane.setEditorKit( new HTMLEditorKit());
		editorPane.setEditorKit( new ResizableHTMLEditorKit());
		//editorPane.setEditorKit( new TextWrapHTMLEditorKit());
		
		//editorPane.setEditorKit( new PageableEditorKit());
		//Margin Setting
		editorPane.setMargin(new Insets(0,25,0,25));
		
		//Background Setting
		/*Color bgColor = new Color(45,125,0);
		editorPane.setBackground(bgColor);*/
		
		editorPane.addHyperlinkListener(this);
		
		editorPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent keyEvent) {
			
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				
				if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
					navigator.gotoNextSpineSection(ContentPane.this);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
					navigator.gotoPreviousSpineSection(ContentPane.this);
				}
				else if (keyEvent.getKeyCode() == KeyEvent.VK_PAGE_UP) {
					gotoPreviousPage();
				} else if ((keyEvent.getKeyCode() == KeyEvent.VK_PAGE_DOWN)) {
					gotoNextPage();
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
			
			
			editorPane.setContentType("text/html");
			
			StringWriter writer = new StringWriter();
			editorPane.getEditorKit().write(writer, document, 0, document.getLength());
			String s = writer.toString();
			
			
			Document doc = Jsoup.parse(s);
			
			
			String value = "";
			String src= "";
			Elements elements = doc.select("img[src]");
			for (org.jsoup.nodes.Element element : elements) {
				value = element.attributes().html();
				src = value.substring(value.indexOf("src=") + 5, value.length());
				src = src.substring(0, src.indexOf("\""));
				
				
				if(src.matches("^\\..*")){
					element.attr("src", "http:/" + src);
				}
				else{
					element.attr("src", "http:/../" + src);
				}
				
			}
			
			
			
			Elements links  = doc.select("img[href]");
			for (org.jsoup.nodes.Element element : links) {
				value = element.attributes().html();
				src = value.substring(value.indexOf("href=") + 6, value.length());
				src = src.substring(0, src.indexOf("\""));
				System.out.println(src);
				
				if(src.matches("^\\..*")){
					element.attr("href", "http:/" + src);
				}
				else{
					element.attr("href", "http:/../" + src);
				}
				
			}
			

			
			
			
			
			editorPane.setText(doc.html());
			
			editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(zoom));
			
			Hashtable cache = new Hashtable();
			
			URL u ;
			
			if (navigator.getCurrentSpinePos() == 0  && navigator.getBook().getCoverImage() !=null ) {
				editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(1.0));
				editorPane.getDocument().putProperty("imageCache", cache);

				u = new URL("http:/../"+navigator.getBook().getCoverImage().getHref());
				
				Image result=ResourceLoader.getImageCache().get("http:/../"+navigator.getBook().getCoverImage().getHref());
				
				result = result.getScaledInstance(scrollPane.getSize().width-100, scrollPane.getSize().height-50, Image.SCALE_SMOOTH);

				cache.put(u,result);
				
			} else {
		
					editorPane.getDocument().putProperty("imageCache", cache);
					
					Iterator it = ResourceLoader.getImageCache().entrySet().iterator();

					while (it.hasNext()) {

						Map.Entry pairs = (Map.Entry) it.next();
						
						u = new URL(""+pairs.getKey());
						cache.put(u,ResourceLoader.getImageCache().get(""+pairs.getKey()));
						//System.out.println(pairs.getKey() + " = "+ pairs.getValue());

					}
			}
			
			
			
			//editorPane.setDocument(document);
			 
			
			if(ResourceLoader.getCSSCache().size()!=0 || !ResourceLoader.getCSSCache().isEmpty()){
				((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(IOUtils.toString(ResourceLoader.getCSSCache().get("0"), "UTF-8"));
			}	
			
			if(ResourceLoader.getFontCache().size()!=0 || !ResourceLoader.getFontCache().isEmpty()){
				Font font=ResourceLoader.getFontCache().get("0");
				
				editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				editorPane.setFont(font);
			}
			
			
			
			
			
			Paper p=new Paper(); //by default LETTER
	        p.setImageableArea(0,0,p.getWidth(), p.getHeight());
	        
	       
	        
	       //initData(editorPane,p,new Insets(18,18,18,18));
			
			scrollToCurrentPosition(sectionPos);
			
			
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
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

	@Override
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

	public void pageCalculate(){
		double maxHeight = scrollPane.getVerticalScrollBar().getMaximum();
		double viewHeight = scrollPane.getViewport().getHeight();
	
		double count=(maxHeight/viewHeight);
		
		if(count%1 != 0.0){
			pageCount=(int) count + 1;
		}
		else{
			pageCount=(int) count;
		}
		System.out.println("Number Of pages"+pageCount);	
	}
	
	
	public String setZoomIn(){
		
		if(navigator.getCurrentSpinePos()==0){
			return "";
		}
		
		zoom=zoom+0.5;
		editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(zoom));
		setZoom();
		
		percentage= (newYMax-oldYMax)/oldYMax*100;
		
		//System.out.println(pageYAxis);
		
		pageYAxis=pageYAxis+(int) Math.round((pageYAxis*percentage)/100);
		
				
		scrollPane.getViewport().setViewPosition(
				new Point(0, pageYAxis));
		return "";
		
		
		//System.out.println(pageYAxis);
		//pageCalculate();
		
	}
	
	
	public String setZoomOut(){
		
		if(navigator.getCurrentSpinePos()==0){
			return "";
		}
		
		zoom=zoom-0.5;
		editorPane.getDocument().putProperty("ZOOM_FACTOR", new Double(zoom));
		setZoom();
		
		percentage= (newYMax-oldYMax)/oldYMax*100;
		if(percentage<0)
			percentage=-percentage;
		
		//System.out.println(pageYAxis);
		
		pageYAxis=pageYAxis-(int) Math.round((pageYAxis*percentage)/100);
				
		scrollPane.getViewport().setViewPosition(
				new Point(0, pageYAxis));
		return "";
		
		
		//System.out.println(pageYAxis);
		
		
		//pageCalculate();
	}
	
	
	public void setZoom(){
		
		oldYMax=scrollPane.getVerticalScrollBar().getMaximum();
		System.out.println(oldYMax);
		
		
		scrollPane.invalidate();
		scrollPane.validate();
		scrollPane.repaint();	
		
		editorPane.invalidate();
		editorPane.validate();
		editorPane.repaint();	
		
		getRootPane().invalidate();
        getRootPane().validate();
        getRootPane().repaint();
		
		
		newYMax=scrollPane.getVerticalScrollBar().getMaximum();
		System.out.println(newYMax);
		
		
		
	}
	
	
	
	public void gotoPreviousPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		if (viewPosition.getY() <= 0) {
			ContentPane.this.navigator.gotoPreviousSpineSection(-1, ContentPane.this);
			pageYAxis=Integer.MAX_VALUE;
			return;
		}
		
		int viewportHeight = scrollPane.getViewport().getHeight();
		
		if(pageYAxis > scrollPane.getVerticalScrollBar().getMaximum())
			pageYAxis=scrollPane.getVerticalScrollBar().getMaximum()-viewportHeight;
		
		pageYAxis = pageYAxis-viewportHeight;
		
		
 		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), pageYAxis));
 		
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
		pageYAxis = ((int) viewPosition.getY()) + viewportHeight;
		
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), pageYAxis));
		
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

	
	@Override
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
            @Override
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
        
       
        @Override
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
