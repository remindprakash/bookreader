package com.bookreader.app.presentation;

import javax.swing.*;
import java.awt.*;

class MenuUI extends JMenuBar{
	
	private static final long serialVersionUID = 1L;
	
	JMenu file, view, help,sortby;
	JMenuItem logoutmenu, exit;
	
	

	public MenuUI() {
		
		file = new JMenu("File");
		view = new JMenu("View");
		help = new JMenu("Help");
		logoutmenu = new JMenuItem("Logout");
		exit = new JMenuItem("Exit");
		
		sortby=new JMenu("Sort by");

		addAllMenu();
	}

	public void addAllMenu() {
		add(file);
		file.add(logoutmenu);
		file.add(exit);

		add(view);
		sortByMenu(sortby);
		view.add(sortby);
		
		add(help);
	}

	private void sortByMenu(JMenuItem sortby){
		
		sortby.add(new JMenuItem("Recent"));
		sortby.add(new JMenuItem("Title"));
		sortby.add(new JMenuItem("Author"));
		sortby.add(new JMenuItem("Type"));
		
		
	}

	

}