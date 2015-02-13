package com.bookreader.app.epub.viewer;

import java.awt.Dimension;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bookreader.app.epub.util.ResourceLoader;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

// package
class SpineSlider extends JSlider implements NavigationEventListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8436441824668551056L;
		private final Navigator navigator;
		
		public SpineSlider(Navigator navigator) {
			super(SwingConstants.HORIZONTAL);
			this.navigator = navigator;
			navigator.addNavigationEventListener(this);
			setPaintLabels(false);
			
			setPreferredSize(new Dimension(0, 50));
			
			addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent evt) {
					JSlider slider = (JSlider) evt.getSource();
					int value = slider.getValue();
					SpineSlider.this.navigator.gotoSpineSection(value, SpineSlider.this);
				}
			});
			initBook(navigator.getBook());
		}

		private void initBook(Book book) {
			if (book == null) {
				return;
			}
			super.setMinimum(0);
			super.setMaximum(book.getSpine().size() - 1);
			super.setValue(0);
			setPaintTicks(true);
			updateToolTip();
			
		}
		
		
		public String calculate(){
			
			if(navigator.getCurrentSpinePos() == 0 )
				return "0";
			
			Double result= (( (double) (navigator.getCurrentSpinePos() + 1) / (double) navigator.getBook().getSpine().size() ) * 100);
			
			return ""+result.intValue();
		}
		
		

		private void updateToolTip() {
			String tooltip = "";
			if (navigator.getCurrentSpinePos() >= 0 && navigator.getBook() != null) {
				tooltip = calculate()+"%    Page  " +String.valueOf(navigator.getCurrentSpinePos() + 1) + " of " + navigator.getBook().getSpine().size();		
			}
			setToolTipText(tooltip);
			
			Hashtable labelTable = new Hashtable();
			double i=navigator.getBook().getSpine().size()/2;
			
			labelTable.put( new Integer( (int) Math.round(i) ), new JLabel(tooltip) );
			
			setLabelTable( labelTable );
		
			setPaintLabels(true);
			
		}
		
		
	
		@Override
		public void navigationPerformed(NavigationEvent navigationEvent) {
			updateToolTip();
			if (this == navigationEvent.getSource()) {
				return;
			}

			if (navigationEvent.isBookChanged()) {
				initBook(navigationEvent.getCurrentBook());
			} else if (navigationEvent.isResourceChanged()) {
				setValue(navigationEvent.getCurrentSpinePos());
			}
		}

	}