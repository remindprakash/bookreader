package com.bookreader.ui;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;

public class EBRApp extends JFrame implements ActionListener {
	
	static final Logger log = LoggerFactory.getLogger(EBRApp.class);
	
	
	MenuUI menu;
	LoginUI login;
	LibraryUI lib;
	

	public EBRApp() {
		login = new LoginUI();
		menu = new MenuUI();
		lib = new LibraryUI();
		initReader();
	}

	public void initReader() {
		setSize(1000, 800);
		setLocationRelativeTo(null);
		setTitle("E-Book Reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initMenu();
		initlogin();
		setResizable(true);
		add(lib.contentPanel, BorderLayout.CENTER);
	}

	public void initMenu() {
		menu.addAllMenu();
		add(menu.menuPanel, BorderLayout.NORTH);
		menu.loginmenu.addActionListener(this);
		menu.logoutmenu.addActionListener(this);
		menu.exit.addActionListener(this);

		// menu.dispSimpleMenu();
		menu.dispFullMenu();
		lib.contentPanel.setVisible(false);
	}

	public void initlogin() {
		login.placeComponents();
		login.loginButton.addActionListener(this);
		login.cancelButton.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();

		switch (s) {
		
		case "Login":
			this.setVisible(false);
			login.placeComponents();
			break;

		case "Log-in":
			
			if (login.validate().equalsIgnoreCase("success")) {
				// Not Valid User
				JOptionPane.showMessageDialog(this,
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
				this.setVisible(true);
			}
	
			break;

		case "Cancel":
			login.loginframe.dispose();
			break;

		case "Logout":
			this.dispose();
			new EBRApp();
			break;

		case "Exit":
			this.dispose();
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
