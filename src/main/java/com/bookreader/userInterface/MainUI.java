package com.bookreader.userInterface;

import javax.swing.*;


import java.awt.*;
import java.awt.event.*;


public class MainUI extends JFrame implements ActionListener
{
	int width, height;
    String User = "Daya";
	String token;
	
    LoginUI log;
    MenuUI menu;   
    LibraryFinal lib;
	
	public void initiateApplication()
	{		
		setFrameProperties();
		initLoginUI();
		initMenuUI();
		initLibraryUI();	
	}
	
	public void setFrameProperties()
	{
		width = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	    height = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	    
	    setSize((width*3)/4,(height*3)/4);
	    setLocation(width/2-this.getSize().width/2, height/2-this.getSize().height/2);
	    setTitle("E-Book Reader"); 
	    setResizable(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void initLoginUI()
	{
		log = new LoginUI();
		log.initLoginComponents();
		
		log.login.addActionListener(this); //login button in the login Dialog box 
	}
	
	public void initMenuUI()
	{
		menu = new MenuUI();
		menu.addMenuComponents();
		menu.dispFullMenu();
		
	    add(menu.menuPanel,BorderLayout.NORTH);  
	    menu.loginmenu.addActionListener(this); //login MenuItem from File Menu
	    menu.logoutmenu.addActionListener(this); //logout MenuItem from File Menu
	    menu.exit.addActionListener(this); //Exit MenuItem from File Menu  
	}

	public void initLibraryUI()
	{
		lib = new LibraryFinal(User);
		add(lib.contentPanel,BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent ae)
    {
		String s=ae.getActionCommand();
		
		if(s.equals("Login")) //To differentiate between LoginMenu(from File menu) and LoginButton(from Dialog Box)
		{
			JComponent jc = (JComponent)ae.getSource();
			s = jc.getName();
		}
		
	    switch(s)
	    { 
	    	case "LoginMenu":
	    		initiateApplication();
	    		break;
	    		
	    	case "LoginButton":
	    		token = log.validate();
	    		if(!token.isEmpty())
	    			showApplication();
	    		else
	    			initiateApplication();
	    		break;
	    		
	    	case "Logout": 
	    		this.dispose();
	    		new MainUI().initiateApplication();
	    		break;	
	    		
	    	case "Exit":
	    		System.exit(0); 
	    }
    }
	
	public void showApplication()
	{
		this.setVisible(true);
		menu.dispFullMenu();
		
		lib.initializeLibrary(); //InitPanes(User,token);  
        lib.contentPanel.setVisible(true);
	}
    



}
