package com.bookreader.app.epub.util;

import javax.swing.SizeRequirements;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;



public class TextWrapHTMLEditorKit extends HTMLEditorKit {
	
	private String SEPARATOR = System.getProperty("line.separator");
	

    @Override 
    public ViewFactory getViewFactory(){ 

        return new HTMLFactory(){
        	
            public View create(Element e){ 
               View v = super.create(e); 
               if(v instanceof InlineView){ 
                   return new InlineView(e){ 
                       public int getBreakWeight(int axis, float pos, float len) { 
                           //return GoodBreakWeight;
                           if (axis == View.X_AXIS) {
                               checkPainter();
                               int p0 = getStartOffset();
                               int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                               if (p1 == p0) {
                                   // can't even fit a single character
                                   return View.BadBreakWeight;
                               }
                               try {
                                   //if the view contains line break char return forced break
                                   if (getDocument().getText(p0, p1 - p0).indexOf(SEPARATOR) >= 0) {
                                       return View.ForcedBreakWeight;
                                   }
                               }
                               catch (BadLocationException ex) {
                                   //should never happen
                               }  

                           }
                           return super.getBreakWeight(axis, pos, len);
                       } 
                       public View breakView(int axis, int p0, float pos, float len) { 
                           if (axis == View.X_AXIS) {
                               checkPainter();
                               int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                               try {
                                   //if the view contains line break char break the view
                                   int index = getDocument().getText(p0, p1 - p0).indexOf(SEPARATOR);
                                   if (index >= 0) {
                                       GlyphView v = (GlyphView) createFragment(p0, p0 + index + 1);
                                       return v;
                                   }
                               }
                               catch (BadLocationException ex) {
                                   //should never happen
                               }

                           }
                           return super.breakView(axis, p0, pos, len);
                     } 
                   }; 
               } 
               else if (v instanceof ParagraphView) { 
                   return new ParagraphView(e) { 
                       protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
                           if (r == null) { 
                                 r = new SizeRequirements(); 
                           } 
                           float pref = layoutPool.getPreferredSpan(axis); 
                           float min = layoutPool.getMinimumSpan(axis); 
                           // Don't include insets, Box.getXXXSpan will include them. 
                             r.minimum = (int)min; 
                             r.preferred = Math.max(r.minimum, (int) pref); 
                             r.maximum = Integer.MAX_VALUE; 
                             r.alignment = 0.5f; 
                           return r; 
                         } 

                     }
                   
                   ; 
                 } 
               return v; 
             } 
         }; 
     } 
	
}
