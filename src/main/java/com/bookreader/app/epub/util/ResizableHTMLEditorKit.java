package com.bookreader.app.epub.util;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ObjectView;
import javax.swing.text.html.ParagraphView;

/*
 * An extended {@link HTMLEditorKit} that allow faster
 * rendering of large html files and allow zooming of content.
 * author Alessio Pollero
 * version 1.0
 * Obtained from http://stackoverflow.com/questions/680817/is-it-possible-to-zoom-scale-font-size-and-image-size-in-jeditorpane
 */

public class ResizableHTMLEditorKit extends HTMLEditorKit {
	
	
     ViewFactory factory = new MyViewFactory();

        @Override
        public ViewFactory getViewFactory() {
            return factory;
        }

        class MyViewFactory extends HTMLFactory {
            @Override
            public View create(Element elem) {
                AttributeSet attrs = elem.getAttributes();
                Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
                Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
                
                if (o instanceof HTML.Tag) {
                    HTML.Tag kind = (HTML.Tag) o;
                  
                    if (    (kind == HTML.Tag.LI) 	|| 
	                		(kind == HTML.Tag.DL) 	||
	                        (kind == HTML.Tag.DD) 	||
	                        (kind == HTML.Tag.BODY) ||
	                        (kind == HTML.Tag.HTML) ||
	                        (kind == HTML.Tag.CENTER) ||
	                        (kind == HTML.Tag.DIV) ||
	                        (kind == HTML.Tag.BLOCKQUOTE) ||
	                        (kind == HTML.Tag.PRE) ||
	                        (kind == HTML.Tag.PRE)) {
	                    return new HTMLBlockView(elem);
                    }
                    else if (kind == HTML.Tag.IMPLIED) {
                        String ws = (String) elem.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
                        if ((ws != null) && ws.equals("pre")) {
                            return super.create(elem);
                        }
                        return new HTMLParagraphView(elem);
                    } else if ((kind == HTML.Tag.P) ||
                            (kind == HTML.Tag.H1) ||
                            (kind == HTML.Tag.H2) ||
                            (kind == HTML.Tag.H3) ||
                            (kind == HTML.Tag.H4) ||
                            (kind == HTML.Tag.H5) ||
                            (kind == HTML.Tag.H6) ||
                            (kind == HTML.Tag.DT)) {
                        // paragraph
           
                        return new HTMLParagraphView(elem);
                    } else if (kind == HTML.Tag.OBJECT){
                    	return new HTMLObjectView(elem);
                    } else if (kind == HTML.Tag.IMG){
                    	return new HTMLImageView(elem);
                    }
                       
                        
                    
                }
                return super.create(elem);
            }

        }


        private class HTMLBlockView extends BlockView {

            public HTMLBlockView(Element elem) {
                super(elem,  View.Y_AXIS);
            }

            @Override
            protected void layout(int width, int height) {
                if (width<Integer.MAX_VALUE) {
                    super.layout(new Double(width / getZoomFactor()).intValue(),
                             new Double(height *
                                        getZoomFactor()).intValue());
                }
            }

            public double getZoomFactor() {
                Double scale = (Double) getDocument().getProperty("ZOOM_FACTOR");
                if (scale != null) {
                    return scale.doubleValue();
                }

                return 1;
            }

            @Override
            public void paint(Graphics g, Shape allocation) {
                Graphics2D g2d = (Graphics2D) g;
                double zoomFactor = getZoomFactor();
                AffineTransform old = g2d.getTransform();
                g2d.scale(zoomFactor, zoomFactor);
                super.paint(g2d, allocation);
                g2d.setTransform(old);
            }

            @Override
            public float getMinimumSpan(int axis) {
                float f = super.getMinimumSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public float getMaximumSpan(int axis) {
                float f = super.getMaximumSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public float getPreferredSpan(int axis) {
                float f = super.getPreferredSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
                double zoomFactor = getZoomFactor();
                Rectangle alloc;
                alloc = a.getBounds();
                Shape s = super.modelToView(pos, alloc, b);
                alloc = s.getBounds();
                alloc.x *= zoomFactor;
                alloc.y *= zoomFactor;
                alloc.width *= zoomFactor;
                alloc.height *= zoomFactor;

                return alloc;
            }

            @Override
            public int viewToModel(float x, float y, Shape a,
                                   Position.Bias[] bias) {
                double zoomFactor = getZoomFactor();
                Rectangle alloc = a.getBounds();
                x /= zoomFactor;
                y /= zoomFactor;
                alloc.x /= zoomFactor;
                alloc.y /= zoomFactor;
                alloc.width /= zoomFactor;
                alloc.height /= zoomFactor;

                return super.viewToModel(x, y, alloc, bias);
            }

        }
        
        
        
        
        private class HTMLObjectView extends ObjectView {

            public HTMLObjectView(Element elem) {
                super(elem);
            }



            public double getZoomFactor() {
                Double scale = (Double) getDocument().getProperty("ZOOM_FACTOR");
                if (scale != null) {
                    return scale.doubleValue();
                }

                return 1;
            }

            @Override
            public void paint(Graphics g, Shape allocation) {
                Graphics2D g2d = (Graphics2D) g;
                double zoomFactor = getZoomFactor();
                AffineTransform old = g2d.getTransform();
                g2d.scale(zoomFactor, zoomFactor);
                super.paint(g2d, allocation);
                g2d.setTransform(old);
            }

            @Override
            public float getMinimumSpan(int axis) {
                float f = super.getMinimumSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public float getMaximumSpan(int axis) {
                float f = super.getMaximumSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public float getPreferredSpan(int axis) {
                float f = super.getPreferredSpan(axis);
                f *= getZoomFactor();
                return f;
            }

            @Override
            public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
                double zoomFactor = getZoomFactor();
                Rectangle alloc;
                alloc = a.getBounds();
                Shape s = super.modelToView(pos, alloc, b);
                alloc = s.getBounds();
                alloc.x *= zoomFactor;
                alloc.y *= zoomFactor;
                alloc.width *= zoomFactor;
                alloc.height *= zoomFactor;

                return alloc;
            }

            @Override
            public int viewToModel(float x, float y, Shape a,
                                   Position.Bias[] bias) {
                double zoomFactor = getZoomFactor();
                Rectangle alloc = a.getBounds();
                x /= zoomFactor;
                y /= zoomFactor;
                alloc.x /= zoomFactor;
                alloc.y /= zoomFactor;
                alloc.width /= zoomFactor;
                alloc.height /= zoomFactor;

                return super.viewToModel(x, y, alloc, bias);
            }

        }
        
        
        private class HTMLImageView extends ImageView {

        	public HTMLImageView(Element elem) {
                super(elem);
            }
        	
        	
    	 @Override
         public void paint(Graphics g, Shape a) {
    		 Graphics2D g2d = (Graphics2D) g;
    		     		 
    		 Rectangle rect = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
    	
    		 Image img = getImage();
    		 
    		 Double scale = (Double) getDocument().getProperty("ZOOM_FACTOR");
             if (scale >1.0 ) {
            	 System.out.println(scale);
            	 g2d.drawImage(img,rect.x, rect.y, rect.width, rect.height, null);
             }
             else{
            	 g2d.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
             }
    		     		    		 
    	 }

        	
        	
        }
        
        
}
