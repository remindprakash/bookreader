package com.bookreader.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class EBRApp extends JFrame implements ActionListener
{
    int width,height;
    AppMenu menu;   
    LoginUI log;
    LibraryUI lib;
    String User = "---";
    
  public EBRApp()
  {    
       width = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
       height = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
       setSize((width*3)/4,(height*3)/4);
       setLocation(width/2-this.getSize().width/2, height/2-this.getSize().height/2);
       
       menu = new AppMenu();
       log = new LoginUI(); 
       lib = new LibraryUI();  
       
       log.loginframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       //menu.menuPanel.setBorder(BorderFactory.createLineBorder(Color.black));
       //lib.contentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
       lib.leftPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
       lib.rightPanel.setBorder(BorderFactory.createLineBorder(Color.green)); 
       

  }

  public void initReader()
  {
    setTitle("E-Book Reader"); 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    initMenu();
    initlogin();    
    setResizable(true);
    add(lib.contentPanel,BorderLayout.CENTER);
  }
  
  public void initMenu()
  {
      menu.addAllMenu();  
      add(menu.menuPanel,BorderLayout.NORTH);    
      menu.loginmenu.addActionListener(this);
      menu.logoutmenu.addActionListener(this);
      menu.exit.addActionListener(this);
            
      //menu.dispSimpleMenu();   
              menu.dispFullMenu();
              lib.contentPanel.setVisible(false);
  }
    
    public void initlogin()
    {
      log.initComponents(width,height);
      log.login.addActionListener(this);
      log.cancel.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent ae)
    {
      String s=ae.getActionCommand();
      
      switch(s)
      {
          case "Login":
            this.setVisible(false);
            log.initComponents(width,height);  
            break;
       
          case "Log-in":
            User = log.validate();
            if(User.equals(""))
            { 
              //Missing Field
              JOptionPane.showMessageDialog(this,"Missing username/password","Error",JOptionPane.ERROR_MESSAGE);
            }
            else if(User.equals("---"))
            {                
              //Wrong Username & Password  
              JOptionPane.showMessageDialog(this,"Invalid username/password","Error",JOptionPane.ERROR_MESSAGE);
            }
            else
            {    
              //Valid User
              log.loginframe.dispose();
              JOptionPane.showMessageDialog(this,"Welcome "+User+"!!!");
              menu.dispFullMenu();  
              lib.InitPanes(User);              
              lib.contentPanel.setVisible(true);
            }
            this.setVisible(true);
            break;
              
          case "Cancel":
            this.setVisible(true);
            log.loginframe.dispose();
            break;
          
          case "Logout":  
            this.dispose();
            EBRApp frame=new EBRApp();
            frame.initReader();
            break;
              
          case "Exit":
              dispose();
              break;
    }   
  }
    
    
}


