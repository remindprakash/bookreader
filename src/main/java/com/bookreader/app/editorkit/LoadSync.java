package com.bookreader.app.editorkit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class LoadSync {

  public static void main(String args[]) {
    final String filename = "E:/book-Workspace/bookreader/src/main/resources/Chap01.xhtml";
    JFrame frame = new JFrame("Loading/Saving Example");
    Container content = frame.getContentPane();

    final JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(editorPane);
    content.add(scrollPane, BorderLayout.CENTER);

    editorPane.setEditorKit(new HTMLEditorKit());

    JPanel panel = new JPanel();

    // Setup actions
    Action loadAction = new AbstractAction() {
      {
        putValue(Action.NAME, "Load");
      }

      public void actionPerformed(ActionEvent e) {
        doLoadCommand(editorPane, filename);
      }
    };
    JButton loadButton = new JButton(loadAction);
    panel.add(loadButton);

    content.add(panel, BorderLayout.SOUTH);

    frame.setSize(250, 150);
    frame.setVisible(true);
  }

  public static void doLoadCommand(JTextComponent textComponent,
      String filename) {
    FileReader reader = null;
    try {
      System.out.println("Loading");
      reader = new FileReader(filename);

      // Create empty HTMLDocument to read into
      HTMLEditorKit htmlKit = new HTMLEditorKit();
      HTMLDocument htmlDoc = (HTMLDocument) htmlKit
          .createDefaultDocument();
      // Create parser (javax.swing.text.html.parser.ParserDelegator)
      HTMLEditorKit.Parser parser = new ParserDelegator();
      // Get parser callback from document
      HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
      // Load it (true means to ignore character set)
      parser.parse(reader, callback, true);
      // Replace document
      textComponent.setDocument(htmlDoc);
      
      try {
		htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "<br/>", 0, 0, null);
		
		htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), "bharath", 0, 0, null);
		
		
	} catch (BadLocationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      System.out.println("Loaded");

    } catch (IOException exception) {
      System.out.println("Load oops");
      exception.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignoredException) {
        }
      }
    }
  }
}
