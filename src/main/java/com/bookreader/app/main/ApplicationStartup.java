package com.bookreader.app.main;

import javax.swing.JOptionPane;

import com.bookreader.userInterface.MainUI;
//import com.bookreader.ui.EBRApp;

public class ApplicationStartup 
{	
	public static void main(String[] args) 
	{
		System.out.println("Book reader application started");
		
	    try
	    {
	    	//EBRApp frame=new EBRApp();
	    	//frame.initReader();
	    	
	    	MainUI frame = new MainUI();
	    	frame.initiateApplication();
	    	
	    }
	  catch(Exception e)
	    {
	        JOptionPane.showMessageDialog(null, e.getMessage());}
	  }
	}
