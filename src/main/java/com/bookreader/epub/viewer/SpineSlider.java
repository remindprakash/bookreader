package com.bookreader.epub.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
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
		
		JTextField posNumber;
		
		public SpineSlider(Navigator navigator) {
			super(JSlider.HORIZONTAL);
			this.navigator = navigator;
			navigator.addNavigationEventListener(this);
			setPaintLabels(false);
			posNumber=new JTextField(10);
			setPreferredSize(new Dimension(0, 50));
			
			addChangeListener(new ChangeListener() {
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
				tooltip = String.valueOf(navigator.getCurrentSpinePos() + 1) + " / " + navigator.getBook().getSpine().size();		
			}
			setToolTipText(tooltip);
			
			Hashtable labelTable = new Hashtable();
			
			labelTable.put( new Integer( 5 ), new JLabel(tooltip) );
			
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