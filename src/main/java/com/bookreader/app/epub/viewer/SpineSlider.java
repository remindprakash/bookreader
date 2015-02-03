package com.bookreader.app.epub.viewer;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;

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

		private void updateToolTip() {
			String tooltip = "";
			if (navigator.getCurrentSpinePos() >= 0 && navigator.getBook() != null) {
				tooltip = "Page   " +String.valueOf(navigator.getCurrentSpinePos() + 1) + " of " + navigator.getBook().getSpine().size();		
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