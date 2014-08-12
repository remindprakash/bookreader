package com.bookreader.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.bookreader.model.Book;
import com.bookreader.model.Shelf;
import com.bookreader.services.AccountSyncService;
import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.TabOrientation;
import com.javaswingcomponents.accordion.listener.AccordionEvent;
import com.javaswingcomponents.accordion.listener.AccordionEventType;
import com.javaswingcomponents.accordion.listener.AccordionListener;
import com.javaswingcomponents.accordion.plaf.steel.SteelAccordionUI;
import com.javaswingcomponents.framework.painters.configurationbound.GradientColorPainter;
import com.javaswingcomponents.framework.painters.configurationbound.GradientDirection;

public class LibraryFinal 
{

	/****************************************************
	  General Variables
	****************************************************/
	public JPanel contentPanel;
    public List<Shelf> shelf;
		
	
	/****************************************************
	  Left Panel Variables
	****************************************************/
	public JPanel leftPanel;

	//Information Panel
	public JPanel infoPanel;
    public String userID;
    public JLabel UserLib;
    public JButton refresh;
	public String token;
    
    //Shelf List Panel       
    public JSCAccordion accordion;
    
	
	/****************************************************
	  Right Panel Variables
	****************************************************/
	public JPanel rightPanel;
	
	
	
	LibraryFinal(String user)
    {
		contentPanel = new JPanel(new GridLayout(1,2));        
		userID = user;
    }
	
	
	
	public void initializeLibrary()
	{
		instantiateMainVariables();
		synchronizeLibrary();
		initiateLeftPanel();
		//contentPanel.add(leftPanel);		
		
		initiateRightPanel();
		//contentPanel.add(rightPanel);
		
		JSplitPane splitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(leftPanel), new JScrollPane(rightPanel));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);
		splitPane.setAutoscrolls(true);
		contentPanel.add(splitPane);
	}
	
	private void instantiateMainVariables()
	{
		leftPanel = new JPanel(new BorderLayout());
	    rightPanel = new JPanel(new GridLayout(1,1));
	    shelf = new ArrayList<Shelf>();
	}
	
	/**********************************************************************************
	  									Left Panel Functions
	**********************************************************************************/
	private void initiateLeftPanel()
	{
		createInfoPanel();
		leftPanel.add(infoPanel,BorderLayout.PAGE_START);
				
		createShelfListPanel();
		leftPanel.add(accordion, BorderLayout.CENTER);
	}
	
	//----------------------------------------Info Panel------------------------------------
	public void setUserName(String usr)
	{
		userID = usr;
	}
	
	private void createInfoPanel()
	{
		infoPanel = new JPanel(new FlowLayout());
		
		UserLib = new JLabel(userID+"'s Library");
		//UserLib.setSize(10, 30);
		
		refresh = new JButton("Refresh");
		//refresh.setSize(10, 30);
		refresh.addMouseListener(new MouseAdapter()
        {
             @Override
             public void mouseMoved(MouseEvent e) {}
             public void mouseDragged(MouseEvent e) {}
             public void mouseEntered(MouseEvent e) {}
             public void mouseExited(MouseEvent e) {}
             public void mousePressed(MouseEvent e) 
             {
            	 contentPanel.removeAll();
            	 contentPanel.revalidate();
            	 contentPanel.repaint();
            	 initializeLibrary();   
             }
         });
		
		infoPanel.add(UserLib);
		infoPanel.add(refresh);
	}
	
	//----------------------------------------Shelf List Panel------------------------------------
	private void createShelfListPanel()
	{
		accordion = new JSCAccordion();
		
		addElementsToAccordion();
		setAccordionLook();
		TabClickAction();
	}
	
	private void addElementsToAccordion()
	{
		for (int i = 0; i < shelf.size(); i++) 
		{
			//creating a panel for Shelf in Accordion List
			JPanel tempShelf = new JPanel(new GridLayout(shelf.get(i).book.size(),1));
			JScrollPane scrollShelf =  new JScrollPane(tempShelf);
			scrollShelf.setName(shelf.get(i).shelfName); //shelf identified by shelf name
			
			for (int j = 0; j < shelf.get(i).book.size(); j++) 
			{
				//creating book label that comes under a shelf name
				JLabel tempBook = new JLabel(shelf.get(i).book.get(j).Title);
				tempBook.setName(shelf.get(i).book.get(j).ID); //book identified by Book ID
								
				//defining Mouse Action for the book Label
				tempBook.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseMoved(MouseEvent e) { }
                    public void mouseDragged(MouseEvent e) {}
                    public void mouseEntered(MouseEvent e) 
                    {
                          e.getComponent().setForeground(Color.red);
                    }
                    public void mouseExited(MouseEvent e) 
                    {                            
                          e.getComponent().setForeground(Color.black);
                    }
                    public void mousePressed(MouseEvent e) 
                    {
                        String book_id = e.getComponent().getName();
                        System.out.println("\tBook " + book_id + " is clicked"); 
                        bookCover_RightPane(book_id);
                    }
                });                  
                
				//adding book label to the shelf Panel
				tempShelf.add(shelf.get(i).book.get(j).Title,tempBook);
			}
	        //adding shelf to Accordion
			accordion.addTab(shelf.get(i).shelfName, scrollShelf); 
		}
	}
		 
	private void setAccordionLook()
    {
      accordion.setTabOrientation(TabOrientation.VERTICAL);
      accordion.setDrawShadow(false);
      accordion.setTabHeight(30);
      accordion.setAutoscrolls(true);
      
      //we know the default UI for the accordion is the SteelAccordionUI
      SteelAccordionUI steelAccordionUI = (SteelAccordionUI) accordion.getUI();  

      //set all the paddings to zero
      steelAccordionUI.setHorizontalBackgroundPadding(0);
      steelAccordionUI.setVerticalBackgroundPadding(0);
      steelAccordionUI.setHorizontalTabPadding(0);
      steelAccordionUI.setVerticalTabPadding(0);
      steelAccordionUI.setTabPadding(0);
              
      GradientColorPainter backgroundPainter = new GradientColorPainter();  //GradientColorPainter      
      backgroundPainter.setGradientDirection(GradientDirection.HORIZONTAL);  //paint gradient horizontally      
      backgroundPainter.setStartColor(new Color(255,255,255));  //start with white     
      backgroundPainter.setEndColor(new Color(214,213,228));  //end with grey          
      accordion.setBackgroundPainter(backgroundPainter);  //apply the painter to the accordion
    }
    
	private void TabClickAction() 
    {
        accordion.addAccordionListener(new AccordionListener() 
        {			
		public void accordionChanged(AccordionEvent accordionEvent) 
                {
			if(accordionEvent.getEventType().equals(AccordionEventType.TAB_SELECTED))
                        {				
                            String shlf_name = accordionEvent.getContents().getName();
                            System.out.println("Shelf " + shlf_name + " is clicked");
                            Shelf_RightPane(shlf_name);
                        }
		}
	});
    }
	
	
	/**********************************************************************************
	  									Right Panel Functions
	**********************************************************************************/
	private void initiateRightPanel()
	{
		Shelf_RightPane("General");
	}
	
	private void Shelf_RightPane(String shelf_name)
	{
		//clearing right Panel
        rightPanel.removeAll();
        rightPanel.revalidate();
        rightPanel.repaint();
        
        
		//creating new temp Shelf
		Shelf currentShelf = new Shelf();
		
		//searching a shelf based on the given shelf name
		for (int i = 0; i < shelf.size(); i++) 
		{
			if(shelf.get(i).shelfName.equals(shelf_name))
				currentShelf = shelf.get(i);
		}

		//passing the shelf to ShelfUI
		ShelfUI sui = new ShelfUI(currentShelf,rightPanel);
		sui.showShelf();
	}

	private void bookCover_RightPane(String book_id)
	{
		//clearing right Panel
        rightPanel.removeAll();
        rightPanel.revalidate();
        rightPanel.repaint();
        
        
		//creating new temp Shelf
		Book currentBook = new Book();
		
		//searching a shelf based on the given shelf name
		for (int i = 0; i < shelf.size(); i++) 
		{
			for (int j = 0; j < shelf.get(i).book.size(); j++) 
			{
				if(shelf.get(i).book.get(j).ID.equals(book_id))
					currentBook = shelf.get(i).book.get(j);
			}				
		}

		//passing the shelf to ShelfUI
		BookCoverUI bui = new BookCoverUI(currentBook,rightPanel);
		bui.showBookCover();
	}

	/**********************************************************************************
	  									General Functions
	**********************************************************************************/
	private void synchronizeLibrary()
	{
		AccountSyncService sync = new AccountSyncService(userID,token);
		shelf = sync.checkUpdates();
		
		for (int i = 0; i < shelf.size(); i++) 
		{
			shelf.get(i).PrintShlef();
		}
		
		
	}
	
}
