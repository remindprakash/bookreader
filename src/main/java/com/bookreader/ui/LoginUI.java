package com.bookreader.ui;

import javax.swing.*;
import java.awt.*;


public class LoginUI
{
    public JPanel Loginpanel;
    public JLabel loginlbl,usernamelbl,passwordlbl;
    public JTextField usernametxt;
    public JPasswordField pwdtxt;
    public JButton login,cancel;
    public JFrame loginframe;
    
    LoginUI()
    {
        Loginpanel = new JPanel(new GridLayout(3,1));
        loginlbl = new JLabel();
        usernamelbl = new JLabel();
        passwordlbl = new JLabel();
        usernametxt = new JTextField(15);
        pwdtxt = new JPasswordField(15);
        login = new JButton("Log-in");
        cancel = new JButton("Cancel");             
        loginframe = new JFrame();
    }
    
    public void initComponents(int width,int height)
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
        
        loginframe.setSize(300,100);
        loginframe.setVisible(true);  
        loginframe.setLocation(width/2-loginframe.getSize().width/2, height/2-loginframe.getSize().height/2);
       
    }
    
    public String validate()
    {
        usernametxt.setText("a");
        pwdtxt.setText("a");
      String id = usernametxt.getText();
      String pwd = pwdtxt.getText();      
      if (id.equals("a") && pwd.equals("a")) 
          return "daya";
      else if (id.equals("") || pwd.equals("")) 
          return "";
      else
          return "---";
    }
    
    
}