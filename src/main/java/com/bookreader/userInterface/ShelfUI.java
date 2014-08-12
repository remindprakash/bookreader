package com.bookreader.userInterface;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.bookreader.model.Shelf;

public class ShelfUI 
{
	Shelf currentShelf;
	JPanel rightPanel;
	
	String shelfName;
	String shelfPath;
	
	JPanel shelfPanel;
	JPanel bookPanel[];
	
	public ShelfUI(Shelf s, JPanel r) 
	{
		currentShelf = s;
		rightPanel = r;
		shelfName = new String();
		shelfPanel = new JPanel(new FlowLayout());
	}

	public void showShelf()
	{
		getShelfName();
		addBooks();
		
		rightPanel.add(shelfPanel);
	}
	
	private void getShelfName()
	{
		shelfName = currentShelf.shelfName;
	}
	
	private void addBooks()
	{
		String book_imageURL, book_ID, book_belongsTo;
		try
        {
			shelfPath = "D:/reader/daya's library/files/";
			
			for (int i = 0; i < currentShelf.book.size(); i++) 
			{
				book_imageURL = currentShelf.book.get(i).coverImageUrl;
				book_ID = currentShelf.book.get(i).ID;
				book_belongsTo = currentShelf.book.get(i).shelf_id;
						
				
				JPanel tempBook = new JPanel(new BorderLayout());
					
				BufferedImage myPicture = scaleImage(100 , 160 , shelfPath + book_belongsTo + "/" + book_imageURL);
		        JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		        picLabel.setName(currentShelf.book.get(i).ID);
		        
		        //add on-click Action for the image
	            picLabel.addMouseListener(new MouseAdapter()
	            {
	            	@Override
	                public void mouseMoved(MouseEvent e) {}
	                public void mouseDragged(MouseEvent e) {}
	                public void mouseEntered(MouseEvent e) {}
	                public void mouseExited(MouseEvent e) {}
	                public void mousePressed(MouseEvent e) 
	                {
	                	String book_id = e.getComponent().getName();
	                    System.out.println("+ "+book_id); 

	            		//clearing right Panel
	                    rightPanel.removeAll();
	                    rightPanel.revalidate();
	                    rightPanel.repaint();
	                    
	                    //searching the book based on its ID
	                    int i;
	                    for (i = 0; i < currentShelf.book.size(); i++) 
	        			{
	        				if(currentShelf.book.get(i).ID.equals(book_id))
	        					break;
	        			}	
	                    
	            		BookCoverUI bui = new BookCoverUI(currentShelf.book.get(i),rightPanel);
	            		bui.showBookCover();
	                }
	            }); 
	               
	            tempBook.add(picLabel,BorderLayout.CENTER);
		        
		        if(shelfName.equals("General"))
				{   
		            JButton button = new JButton();
		            button.setName(book_ID);		            

		            switch(book_belongsTo)
		            {
		                   case "_general":
		                       button.setText("Add to Shelf");    
		                        break;
		                        
		                   case "_newbooks":
		                       button.setText("Download");
		                       break;
		            }
		            tempBook.add(button,BorderLayout.PAGE_END);		               
				}
		        
	            shelfPanel.add(tempBook);
			}
        
        
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
		}

	private BufferedImage scaleImage(int WIDTH, int HEIGHT, String filename) {
	    BufferedImage bi = null;
	    try {
	        ImageIcon ii = new ImageIcon(filename);//path to image
	        bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	        Graphics2D g2d = (Graphics2D) bi.createGraphics();
	        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
	        g2d.drawImage(ii.getImage(), 0, 0, WIDTH, HEIGHT, null);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    return bi;
	}
	    


}
