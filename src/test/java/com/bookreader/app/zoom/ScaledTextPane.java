package com.bookreader.app.zoom;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.event.*;
import java.awt.Font;

public class ScaledTextPane extends JEditorPane {

    JComboBox zoomCombo = new JComboBox(new String[] {"50%", "75%",
                                        "100%", "150%", "200%"});
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScaledTextPane scaledTextPane = new ScaledTextPane();
        scaledTextPane.getDocument().putProperty("i18n", Boolean.FALSE);
        scaledTextPane.getDocument().putProperty("ZOOM_FACTOR", new Double(1.5));
        JScrollPane scroll = new JScrollPane(scaledTextPane);
        frame.getContentPane().add(scroll);
        frame.getContentPane().add(scaledTextPane.zoomCombo, BorderLayout.NORTH);

        frame.setSize(600, 200);
        frame.show();
    }

    public ScaledTextPane() {
        super();
        final SimpleAttributeSet attrs=new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs,16);
        setEditorKit(new ScaledEditorKit());
        StyledDocument doc=(StyledDocument)ScaledTextPane.this.getDocument();
        doc.setCharacterAttributes(0,1,attrs,true);
        try {
            StyleConstants.setFontFamily(attrs,"Lucida Sans");
            doc.insertString(0, "Lusida Sans font test\n", attrs);

            StyleConstants.setFontFamily(attrs,"Lucida Bright");
            doc.insertString(0, "Lucida Bright font test\n", attrs);

            StyleConstants.setFontFamily(attrs,"Lucida Sans Typewriter");
            doc.insertString(0, "Lucida Sans Typewriter font test\n", attrs);
        }
        catch (BadLocationException ex) {
        }

        zoomCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = (String) zoomCombo.getSelectedItem();
                s = s.substring(0, s.length() - 1);
                double scale = new Double(s).doubleValue() / 100;
                ScaledTextPane.this.getDocument().putProperty("ZOOM_FACTOR",new Double(scale));

               
            }
        });
        zoomCombo.setSelectedItem("150%");
        
        
         
        
    }

    

}
