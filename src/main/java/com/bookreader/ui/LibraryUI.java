package com.bookreader.ui;

import com.bookreader.ui.Reader.ReadPDF;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import com.javaswingcomponents.accordion.*;
import com.javaswingcomponents.accordion.TabOrientation;
import com.javaswingcomponents.accordion.listener.AccordionEvent;
import com.javaswingcomponents.accordion.listener.AccordionEventType;
import com.javaswingcomponents.accordion.listener.AccordionListener;
import com.javaswingcomponents.accordion.plaf.steel.SteelAccordionUI;
import com.javaswingcomponents.framework.painters.configurationbound.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.BorderLayout;
//import Ebook.Reader.ReadPDF;

public class LibraryUI
{
    public String[] columns;
    public String[][] data;
    public int col_count, row_count;
    public int id=0, location=1, bkname=2, arthur=3, pdfFile=4, img=5, desc=6; //index

    String username;
    public JLabel UserLib;
    public JButton refresh;
    public JSCAccordion accordion;
    public JPanel[] shelf;
    public String[] shelfname;
    public int shelf_count;
    public JLabel[][] book;
    public String[][] bookname;
    public int[] book_count;
    
    String path;
    
    public JPanel contentPanel; 
            public JPanel leftPanel,rightPanel;
    public JPanel GeneralPane, shelfPane, BookPane, ReaderPane;
    
    public ReadPDF pdf;
    public JLabel readArea;
    public Image image;
    
    public int i,j,x;
    
    LibraryUI()
    {
       UserLib = new JLabel();
       refresh = new JButton("Refresh");
       shelf = new JPanel[20];
       shelfname = new String[20];
       book = new JLabel[20][20];
       bookname = new String[20][20];
       book_count = new int[20];
       
       contentPanel = new JPanel(new GridLayout(1,2));        
       leftPanel = new JPanel(new BorderLayout());
       rightPanel = new JPanel(new GridLayout(1,1));
       GeneralPane = new JPanel(new FlowLayout());
       shelfPane = new JPanel(new FlowLayout());
       BookPane = new JPanel(new GridLayout(1,2));
       
       

    }
    
    
    public void PopulateData()
    {
       String line; 
       data = new String[100][7];
       try
       {
           File file = new File("D:/reader/daya's library/library.txt");
           FileReader fileReader  = new FileReader(file);
           BufferedReader in = new BufferedReader(fileReader);
           
           line = in.readLine();
           columns = line.split("#");                    
           col_count = columns.length;
           row_count = 0;
           
           while ((line = in.readLine()) != null) 
           {
               data[row_count] = line.split("#");
               row_count++;
           }
           fileReader.close();            
       }
       catch(IOException e)
       {
           e.printStackTrace();
       }
    }  
    
    
    public void InitPanes(String user)
    {
        System.gc();
        contentPanel.removeAll();
        leftPanel.removeAll();
        rightPanel.removeAll();
        
        accordion = new JSCAccordion();
        username = user;
       //Read library file line-by-line & create data structures 
       //PopulateData();
       //create left panel
        Init_LeftPane();
       //create right panel
        GeneralShelf_RightPane();
       
        contentPanel.add(rightPanel);
       
       
       //contentPanel.setRightComponent(rightPanel);
       //contentPanel.setOneTouchExpandable(true);
       //contentPanel.setDividerLocation(0.5);
       //contentPanel.setResizeWeight(0.5);
       //contentPanel.setContinuousLayout(true);
       // contentPanel.setOneTouchExpandable(true);
       
       contentPanel.revalidate();
       contentPanel.repaint();
       
    }
    
     
    public void Init_LeftPane()
    {
      leftPanel.removeAll();
      
      //add user library label
      JPanel topleft = new JPanel(new GridLayout(1,2));
      UserLib.setText(username+"'s Library");
      UserLib.setSize(10, 30);
      topleft.add(UserLib);
      //add refresh button
      refresh.setSize(10, 30);
      refresh.addMouseListener(new MouseAdapter()
                       {
                            @Override
                            public void mouseMoved(MouseEvent e) {}
                            public void mouseDragged(MouseEvent e) {}
                            public void mouseEntered(MouseEvent e) {}
                            public void mouseExited(MouseEvent e) {}
                            public void mousePressed(MouseEvent e) 
                            {
                              refresh_Library();   
                            }
                        });
      
      
      topleft.add(refresh);
      leftPanel.add(topleft,BorderLayout.PAGE_START);
     
        
      //------add shelf & book list------
      
      //get all shelf names
      String[] allLocs = new String[row_count+1]; //allLocs has General & all the other shelf names
      allLocs[0] = "General";      
      for(i=0;i<row_count;i++)
           allLocs[i+1] = data[i][location];
      Set<String> myset = new HashSet<String>(Arrays.asList(allLocs));
      String[] mystr = myset.toArray(new String[myset.size()]);
      Arrays.sort(mystr);
      
      shelf_count = 0;
      for(i=0;i<mystr.length;i++)
      {
         if(!mystr[i].startsWith("_"))
           shelfname[shelf_count++] = mystr[i];  
      }
      //now "shelfname" has General & all the shelf names
      //"shelf_count" has the number the shelves
  
      for(i=0;i<shelf_count;i++)
      {
          //create Shelf Panel
          shelf[i] = new JPanel(new GridLayout(20,1));
          shelf[i].setName(shelfname[i]);
          
          //add Books to the Shelf Panel
          x = 0;
          for(j=0;j<row_count;j++)
          {
              String shelfnm =  data[j][location];
              if(shelfnm.equals(shelfname[i]) || (i==0 && (shelfnm.equals("_newbooks") || shelfnm.equals("_general"))))
              {
                  bookname[i][x] = data[j][bkname];
                  book[i][x] = new JLabel(bookname[i][x]);
                  book[i][x].setName(data[j][id]);
                  
                  //defining Mouse Action for Book(x)
                  book[i][x].addMouseListener(new MouseAdapter()
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
                          System.out.println("+ "+book_id); 
                          Book_RightPane(book_id);
                      }
                  });                  
                  
                  //Adding Book(x) to the Shelf(i)
                  shelf[i].add(bookname[i][x], book[i][x]);
                  x++;
              }    
          }
          book_count[i] = x;
          
          //adding shelf to Accordion
          accordion.addTab(shelfname[i], shelf[i]);           
      }
        
        
      setAccordionLook();
      TabClickAction();
      leftPanel.add(accordion, BorderLayout.CENTER);
      
      
       //add left Panel to main
        leftPanel.revalidate();
        leftPanel.repaint();
        contentPanel.add(leftPanel);
       //contentPanel.setLeftComponent(leftPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    
    public void setAccordionLook()
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
                            System.out.println("+ "+shlf_name);
                            if(shlf_name.equals("General"))
                                GeneralShelf_RightPane();
                            else
                                Shelf_RightPane(shlf_name);
                        }
		}
	});
    }
    
     
    public BufferedImage scaleImage(int WIDTH, int HEIGHT, String filename) {
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
    
    
    public void GeneralShelf_RightPane()
    {
        //empty right Pane
        rightPanel.removeAll();
        GeneralPane.removeAll();
        
        JPanel[] general = new JPanel[40];
        path = "D:/reader/"+username+"'s library/files/"; 
        int genbook_count = 0;
        
        try
        {
        
        for(i=0;i<row_count;i++)
        {
            if(data[i][location].startsWith("_"))
            {
               //Create Panel for one book( includes image and button)
               general[genbook_count] = new JPanel(new BorderLayout());
               general[genbook_count].setName(data[i][bkname]);
                                  
               BufferedImage myPicture = scaleImage(100,160,path+data[i][location]+"/"+data[i][img]);
               JLabel picLabel = new JLabel(new ImageIcon(myPicture));
               
               JButton button = new JButton();
               button.setName(data[i][id]);
               
               switch(data[i][location])
               {
                   case "_general":
                       button.setText("Add to Shelf");
                       button.setName(data[i][id]);
                       button.addMouseListener(new MouseAdapter()
                       {
                            @Override
                            public void mouseMoved(MouseEvent e) {}
                            public void mouseDragged(MouseEvent e) {}
                            public void mouseEntered(MouseEvent e) {}
                            public void mouseExited(MouseEvent e) {}
                            public void mousePressed(MouseEvent e) 
                            {
                                JRadioButton[] rb = new JRadioButton[shelf_count];
                                ButtonGroup group = new ButtonGroup();
                                
                                //create Panel to add all the dialog box components
                                JPanel pane = new JPanel(new GridLayout(1,3));
                                
                                ///create a Panel to put Radio buttons
                                JPanel radioPanel = new JPanel();
                                radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));                                
                                for(i=0;i<shelf_count;i++)
                                {                                    
                                    rb[i] = new JRadioButton(shelfname[i]);
                                    group.add(rb[i]);
                                    radioPanel.add(rb[i]);
                                }                                 
                                rb[0].setSelected(true);
                                
                                
                                //adda radioPanel to dialog box Panel
                                  pane.add(radioPanel);
                                
                                //add arrow image to the dialog box  
                                BufferedImage myPicture = scaleImage(50,50,path+"icon/arrow.jpg");
                                JLabel picLabel = new JLabel(new ImageIcon(myPicture));
                                  pane.add(picLabel);
                                
                                JButton inbutton = new JButton("Add to shelf");
                                inbutton.setName(e.getComponent().getName());
                                inbutton.addMouseListener(new MouseAdapter()
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
                                        Book_RightPane(book_id);
                                            //continue here
                                    }
                                }); 
                                
                                
                                  pane.add(inbutton, BorderLayout.PAGE_END);
                                  pane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
                                
                                JDialog dialog = new JDialog();
                                dialog.setTitle("Select a Shelf");
                                dialog.add(pane);
                                dialog.pack();
                                dialog.setSize(500, 250);
                                dialog.setLocationRelativeTo(null); // Center on screen
                                dialog.setVisible(true);
                                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            }
                        });       
                        break;
                   case "_newbooks":
                       button.setText("Download");
                       break;
               }
               
               
               general[genbook_count].add(picLabel,BorderLayout.CENTER);
               general[genbook_count].add(button,BorderLayout.PAGE_END);
               GeneralPane.add(general[genbook_count]);
               
               genbook_count++;
            }
            
        }
        
        
        shelfPane.revalidate();
        shelfPane.repaint();
        rightPanel.add(GeneralPane);
        rightPanel.revalidate();
        rightPanel.repaint();
        
        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //add images 
        //add download/add to shelf button
        //add actione listener for each download/add to shelf button
        
        //on-clicking "Download"
          //remove the book record from download array
          //change flag for the book record in general_library array(indicating download to library shift)
          
          //move book,jpg,desc from _newbooks folder to _general folder
        
          //change "download" button to "Add to shelf" button
        
        //on-clicking "Add to Shelf"
          //select a shelf using radio button->ok button
          //set it as current_shelf string
          
          //remove book record from library.txt file
          //add book record in the corresponding shelf of shelf.txt file
        
          //move book,jpg,desc from _general folder to "shelf x" folder
        
          //call refresh
          
    }
   
   
    public void Shelf_RightPane(String shlf_name)
    {
        //empty right Pane
        rightPanel.removeAll();
        shelfPane.removeAll();
        
        shelfPane.add(new JLabel("\t"+shlf_name));
        for(i=0;i<row_count;i++)
        {
            if(data[i][location].equals(shlf_name))
            {
               BufferedImage myPicture = scaleImage(100,160,path+shlf_name+"/"+data[i][img]);
               JLabel picLabel = new JLabel(new ImageIcon(myPicture)); 
               picLabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
               picLabel.setName(data[i][id]);
               shelfPane.add(picLabel);     
               
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
                                Book_RightPane(book_id);
                            }
                        }); 
            }
        }
        
        shelfPane.revalidate();
        shelfPane.repaint();
        rightPanel.add(shelfPane);
        rightPanel.revalidate();
        rightPanel.repaint();
        //add just images
    }
    
    
    public void Book_RightPane(String book_id)
    {
         //empty right Pane
        rightPanel.removeAll();
        BookPane.removeAll();
        
        try 
        {
            //add image, read button and nutshell text
        for(i=0;i<row_count;i++)
        {
            if(data[i][id].equals(book_id))
            {
               JPanel jp = new JPanel(new FlowLayout());
               
               //create image
               BufferedImage myPicture = scaleImage(200,320,path+data[i][location]+"/"+data[i][img]);
               JLabel picLabel = new JLabel(new ImageIcon(myPicture)); 
               picLabel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
               
               //creat button
               JButton read = new JButton("Read");
               read.setName(path+data[i][location]+"/"+data[i][pdfFile]);               
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
                                String filepath = e.getComponent().getName();
                                System.out.println("+ "+filepath); 
                                Reader_RightPane(filepath);
                            }
                        });
               
               //add image and button to a Panel
               jp.add(picLabel);//,BorderLayout.CENTER); 
               jp.add(read);//,BorderLayout.CENTER);
                              
                //read Description file
                BufferedReader br = new BufferedReader(new FileReader(path+"desc.txt"));//data[i][location]+"/"+data[i][desc]));   
                StringBuilder sb = new StringBuilder();
                String line;

                
                while ((line = br.readLine()) != null) 
                {
                    sb.append(line);
                    sb.append("\n");
                }
                String description = sb.toString(); 
                description = "       "+data[i][bkname]+"\n             by\n      "+data[i][arthur]+"\n\n\n\n"+description;
                
                JTextArea jl = new JTextArea();
                jl.setLineWrap(true); //Set line wrap
                jl.setWrapStyleWord(true);
                
                jl.setSize(rightPanel.getWidth()/2, rightPanel.getHeight()/2);
                jl.setText(description);
                
                BookPane.add(jp);
                BookPane.add(jl);
               
            }
        }
        //repaint Panels
        BookPane.revalidate();
        BookPane.repaint();
        rightPanel.add(BookPane);
        rightPanel.revalidate();
        rightPanel.repaint();
        
    }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
        
    
    public void Reader_RightPane(String filepath)
    {
        //empty right Pane
        rightPanel.removeAll();
        pdf = new ReadPDF(filepath,rightPanel.getSize());
        pdf.InitiateReader();
        
        //repaint Panels
        rightPanel.add(pdf.ReaderPanel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    
    
    public void refresh_Library()
    {
        InitPanes(username);
        //remove left and right Pane
        
        //add left radioPanel based on shelf & Book array
        //add corresponding action listener
        
        //set current shelf as "Shelf 1"
        
        //display general library (4 in a row)
          //Add image and corresponding button(Download/Add to shelf)
    }
    
}