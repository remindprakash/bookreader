package com.bookreader.app.epub.util;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.FlowView;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.View;
/*
 * An extended {@link HTMLEditorKit} that allow faster
 * rendering of large html files and allow zooming of content.
 * author Alessio Pollero
 * version 1.0
 * Obtained from http://stackoverflow.com/questions/680817/is-it-possible-to-zoom-scale-font-size-and-image-size-in-jeditorpane
 */
class HTMLParagraphView extends ParagraphView {
    
     public static int MAX_VIEW_SIZE=100;

        public HTMLParagraphView(Element elem) {
            super(elem);
            strategy = new HTMLParagraphView.HTMLFlowStrategy();
        }

        public static class HTMLFlowStrategy extends FlowStrategy {
            protected View createView(FlowView fv, int startOffset, int spanLeft, int rowIndex) {
                View res=super.createView(fv, startOffset, spanLeft, rowIndex);
                if (res.getEndOffset()-res.getStartOffset()> MAX_VIEW_SIZE) {
                    res = res.createFragment(startOffset, startOffset+ MAX_VIEW_SIZE);
                }
                return res;
            }

        }
        public int getResizeWeight(int axis) {
            return 0;
        }
        
        
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
