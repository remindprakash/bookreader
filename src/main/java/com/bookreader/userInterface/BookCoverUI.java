package com.bookreader.userInterface;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.bookreader.model.Book;

public class BookCoverUI 
{
	Book currentBook;
	JPanel rightPanel;
		
	JPanel bookPanel;
	String bookpath;
	
	public BookCoverUI(Book b, JPanel r) 
	{
		currentBook = b;
		rightPanel = r;
		bookPanel = new JPanel(new FlowLayout());
	}

	public void showBookCover()
	{
		addCoverComponents();
		
		rightPanel.add(bookPanel);
	}
	
	private void addCoverComponents()
	{
		bookpath = "D:/reader/daya's library/files/" + currentBook.shelf_id +"/";
		
		//Add image
        BufferedImage myPicture = scaleImage(200,320, bookpath +currentBook.coverImageUrl);
        JLabel picLabel = new JLabel(new ImageIcon(myPicture)); 
        picLabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        //Add read button
        JButton read = new JButton("Read");
        read.setName(bookpath + currentBook.downloadUrl);               
        read.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        read.addMouseListener(new MouseAdapter()
        {
        	@Override
            public void mouseMoved(MouseEvent e) {}
            public void mouseDragged(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) 
            {
        		//clearing right Panel
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                
            	String filepath = e.getComponent().getName();
            	System.out.println("+ "+filepath);
            	ReaderUI rui = new ReaderUI(filepath,rightPanel);
            	rui.readPDF();
            }
        });
        
        JTextArea desc = new JTextArea();
        desc.setLineWrap(true); //Set line wrap
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBackground(Color.LIGHT_GRAY);
        
        desc.setSize(rightPanel.getWidth()/2, rightPanel.getHeight()/2);
        desc.setText(currentBook.Description);
        
        //add image and button to a Panel
        bookPanel.add(picLabel);//,BorderLayout.CENTER); 
        bookPanel.add(read);//,BorderLayout.CENTER);
        bookPanel.add(desc);
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