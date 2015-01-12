package com.bookreader.app.presentation;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class EBRApp implements ActionListener {
	
	static final Logger log = LoggerFactory.getLogger(EBRApp.class);
	
	private final JFrame mainWindow;
	
	private MenuUI menu;
	private LoginUI login;
	private LibraryUI lib;
	private EpubUI epub;

	public EBRApp() {
		mainWindow = new JFrame();
		login = new LoginUI();
		menu = new MenuUI();
		lib = new LibraryUI();
		
		initReader();
	}

	public void initReader() {
		
		mainWindow.setSize(1000, 800);
		mainWindow.setLocationRelativeTo(null);
		mainWindow.setTitle("E-Book Reader");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setResizable(true);
		
		//Menu Item 
		menu.addAllMenu();
		menu.loginmenu.addActionListener(this);
		menu.logoutmenu.addActionListener(this);
		menu.exit.addActionListener(this);
		menu.dispFullMenu();
		mainWindow.add(menu.menuPanel, BorderLayout.NORTH);
		
		//Login page 
		login.placeComponents();
		login.loginButton.addActionListener(this);
		login.cancelButton.addActionListener(this);
		
		
		//Library Item 
		lib.contentPanel.setVisible(false);
		mainWindow.add(lib.contentPanel, BorderLayout.CENTER);
	}

	

	

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();

		switch (s) {
		
		case "Login":
			mainWindow.setVisible(false);
			login.placeComponents();
			break;

		case "Log-in":
			
			if (login.validate().equalsIgnoreCase("success")) {
				// Not Valid User
				JOptionPane.showMessageDialog(mainWindow,
						"Invalid username/password", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			else {
				// Valid User
				login.loginframe.dispose();
				//JOptionPane.showMessageDialog(this, "Welcome admin !!!");
				menu.dispFullMenu();
				lib.InitPanes("admin");
				lib.contentPanel.setVisible(true);
				mainWindow.setVisible(true);
			}
	
			break;

		case "Cancel":
			login.loginframe.dispose();
			break;

		case "Logout":
			mainWindow.dispose();
			new EBRApp();
			break;

		case "Exit":
			mainWindow.dispose();
			break;
		}
	}
	
	
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Unable to set native look and feel", e);
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new EBRApp();
			}
		});
	}
}
