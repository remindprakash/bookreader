package com.bookreader.userInterface;

import com.bookreader.services.LoginService;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class LoginUI implements ActionListener
{

    public JFrame loginframe;	
	public JPanel Loginpanel;
    public JLabel loginlbl,usernamelbl,passwordlbl;
    public JTextField usernametxt;
    public JPasswordField pwdtxt;
    public JButton login,cancel;
    
    public String token;
    
    
	LoginUI()
	{            
        loginframe = new JFrame();
        Loginpanel = new JPanel(new GridLayout(3,1));
        loginlbl = new JLabel();
        usernamelbl = new JLabel();
        passwordlbl = new JLabel();
        usernametxt = new JTextField(15); //username Textbox
        pwdtxt = new JPasswordField(15);  //Password Textbox
        login = new JButton("Login");
        login.setName("LoginButton");
        cancel = new JButton("Cancel"); 
		
        token = null;
	}
	
	public void initLoginComponents()
	{
		setFrameProperties();
		addLoginComponents();
		addToListener();
	}
	
	private void setFrameProperties()
	{
		int width = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	    int height = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	    
	    loginframe.setSize(300,100);
        loginframe.setLocation(width/2-loginframe.getSize().width/2, height/2-loginframe.getSize().height/2);
        loginframe.setTitle("Reader Access"); 
        loginframe.setVisible(true);  	    
        loginframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void addLoginComponents()
	{

        usernamelbl.setText("Username:");
        passwordlbl.setText("Password:");
        Loginpanel.add(usernamelbl);
        Loginpanel.add(usernametxt);
        Loginpanel.add(passwordlbl);
        Loginpanel.add(pwdtxt);
        Loginpanel.add(login);
        Loginpanel.add(cancel);
        loginframe.add(Loginpanel);
	}
	
	private void addToListener()
	{
	    cancel.addActionListener(this);
	}
	
	public String validate()
	{
		String id = usernametxt.getText();
		@SuppressWarnings("deprecation")
		String pwd = pwdtxt.getText();      
		
		LoginService log = new LoginService(id,pwd);
		String response = log.authenticate();
		
		loginframe.dispose();
		
		if(response.equals("Failed to login Invalid credentials"))
		{
			JOptionPane.showMessageDialog(loginframe,response,"Error",JOptionPane.ERROR_MESSAGE);
			return null;
		}
		else
		{
			token = response;
			System.out.println("Token: "+ token);
			JOptionPane.showMessageDialog(loginframe,"Welcome back "+id+"!!!");
			return token;
		}
	}


	public void actionPerformed(ActionEvent ae) 
	{
		String s=ae.getActionCommand();
		
	    switch(s)
	    {
	    	case "Login":
	    		validate();
	    		break;
	    		
	    	case "Cancel":
	    		loginframe.setVisible(true);
            	loginframe.dispose();
            	break;
	    }		
	}

}
