package com.bookreader.userInterface;

import java.awt.GridLayout;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MenuUI 
{
	JPanel menuPanel;
    JMenuBar menubar;
    JMenu file,library,view,help;
    public JMenuItem loginmenu,logoutmenu,exit;
    
    MenuUI()
    {
       menuPanel = new JPanel(new GridLayout(1,1));
       menubar = new JMenuBar();
       file = new JMenu("File");
       library = new JMenu("My Library");
       view = new JMenu("View");
       help = new JMenu("Help");
       loginmenu = new JMenuItem("Login");
       loginmenu.setName("LoginMenu");
       logoutmenu = new JMenuItem("Logout");
       exit = new JMenuItem("Exit");
    }
    
    
    public void addMenuComponents()
    {
       menubar.add(file);
          file.add(loginmenu);
          file.add(logoutmenu);
          file.add(exit);
       menubar.add(library);
       menubar.add(view);
       menubar.add(help);  
       
       menuPanel.add(menubar);  
    }
    
    
    public void dispSimpleMenu()
    {
        file.setVisible(true);
          loginmenu.setVisible(true);
          logoutmenu.setVisible(false);
          exit.setVisible(true);
        library.setVisible(false);
        view.setVisible(false);
        help.setVisible(false);  
        
        menuPanel.revalidate();
        menuPanel.repaint();
    }
    
    
    public void dispFullMenu()
    {
         file.setVisible(true);
          loginmenu.setVisible(false);
          logoutmenu.setVisible(true);
          exit.setVisible(true);
        library.setVisible(true);
        view.setVisible(true);
        help.setVisible(true); 
        
        menuPanel.revalidate();
        menuPanel.repaint();          
    }
 
}