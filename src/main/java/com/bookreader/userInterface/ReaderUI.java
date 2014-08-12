package com.bookreader.userInterface;

import java.awt.*;

import com.bookreader.services.ReaderService;

import javax.swing.JPanel;

public class ReaderUI 
{
	String filepath;
	JPanel rightPanel;
	ReaderService reader;
	
	public ReaderUI(String Loc, JPanel r)
	{
		filepath = Loc;
		rightPanel = r;
	}
	
	public void readPDF()
	{
		createReaderService();
		InitiateReader();
		rightPanel.add(reader.ReaderPanel);
	}
	
	private void createReaderService()
	{
		reader = new ReaderService(filepath,rightPanel.getSize());
	}
	
	private void InitiateReader()
	{
		reader.Read();
		
	}
}
