package com.bookreader.app.presentation;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bookreader.app.epub.viewer.Viewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginUI {
	
	static final Logger log = LoggerFactory.getLogger(LoginUI.class);
	
	public JPanel loginpanel;
	public JLabel userLabel, passwordLabel;
	public JTextField userText;
	public JPasswordField passwordText;
	public JButton loginButton, cancelButton;
	public JFrame loginframe;

	public LoginUI() {
		loginpanel = new JPanel();
		userLabel = new JLabel("User");
		userText = new JTextField(15);

		passwordLabel = new JLabel("Password");
		passwordText = new JPasswordField(15);

		loginButton = new JButton("Log-in");
		cancelButton = new JButton("Cancel");
		
		loginframe= new JFrame();
	}

	public void placeComponents() {

		loginframe.setSize(300, 250);
		loginframe.setTitle("Login");
		loginframe.setLocationRelativeTo(null);
		loginframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginframe.setVisible(true);

		loginpanel.setLayout(null);

		userLabel.setBounds(10, 40, 80, 25);
		loginpanel.add(userLabel);

		userText.setBounds(100, 40, 160, 25);
		loginpanel.add(userText);

		passwordLabel.setBounds(10, 80, 80, 25);
		loginpanel.add(passwordLabel);

		passwordText.setBounds(100, 80, 160, 25);
		loginpanel.add(passwordText);

		loginButton.setBounds(100, 120, 70, 25);
		loginpanel.add(loginButton);

		cancelButton.setBounds(180, 120, 70, 25);
		loginpanel.add(cancelButton);

		loginframe.add(loginpanel);
		
		loginButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (validate().equalsIgnoreCase("success")) {
					// Not Valid User
					JOptionPane.showMessageDialog(loginframe,
							"Invalid username/password", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					// Valid User
					loginframe.dispose();
					//JOptionPane.showMessageDialog(this, "Welcome admin !!!");
					Viewer obj=new Viewer();
					obj.InitPanes();
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				loginframe.dispose();
			}
		});
		
		

	}

	//To be change to restService (Minnool Application)
	public String validate() {
		try{
			 char[] passWord = passwordText.getPassword();
			 String password = new String(passWord);
			if(userText.getText().equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")){
				return "success";
			}
			else{
				return "fail";
			}
		}
		catch(Exception e){
			log.error("Unable to Process Login Funcion", e);
		}
		return "fail";
	}

}