package com.bookreader.ui.Reader;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import org.icepdf.core.views.DocumentViewController;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;

public class PDFViewer
{
  public static void main(String[] paramArrayOfString)
  {
    InputStream localInputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("A1B2C3D4E5.dat");
    InputStream localInputStream2 = PDFEncrypter.decrypt(localInputStream1);
    localInputStream2 = PDFViewer.class.getResourceAsStream("/pdf-sample.pdf");
    SwingController localSwingController = new SwingController();
    PDFViewBuilder localPDFViewBuilder = new PDFViewBuilder(localSwingController);
    JPanel localJPanel = localPDFViewBuilder.buildViewerPanel();
    ComponentKeyBinding.install(localSwingController, localJPanel);
    localSwingController.getDocumentViewController().setAnnotationCallback(new MyAnnotationCallback(localSwingController.getDocumentViewController()));
    JFrame localJFrame = new JFrame();
    localJFrame.setDefaultCloseOperation(3);
   localJFrame.getContentPane().add(localJPanel);
    if (!isLockFileAvailable())
    {
      String str1 = JOptionPane.showInputDialog(localJFrame, "Enter the pass code:", "Pass code", -1);
      if ((str1 != null) && (!str1.equals("")))
        try
        {
          URL localURL = new URL(new StringBuilder().append("http://www.allareobjects.com/sampleweb/verify?token=").append(str1).toString());
          URLConnection localURLConnection = localURL.openConnection();
          InputStream localInputStream3 = localURLConnection.getInputStream();
          String str2 = convertToText(localInputStream3);
          if (str2.contains("invalid"))
          {
            JOptionPane.showMessageDialog(localJFrame, "Invalid token, book already delivered for this token \n Please request another copy");
            System.exit(0);
          }
          else
          {
           try
            {
              FileOutputStream localFileOutputStream = new FileOutputStream("lock.dat");
              localFileOutputStream.close();
            }
            catch (Exception localException2)
            {
              localException2.printStackTrace();
            }
            localSwingController.openDocument(localInputStream2, "Book", null);
            localJFrame.pack();
            localJFrame.setVisible(true);
          }
        }
        catch (Exception localException1)
        {
          localException1.printStackTrace();
        }
      System.exit(0);
    }
    else
    {
      localSwingController.openDocument(localInputStream2, "Book", null);
      localJFrame.pack();
      localJFrame.setVisible(true);
    }
  }

  private static boolean isLockFileAvailable()
  {
    FileInputStream localFileInputStream = null;
    boolean bool = false;
    try
    {
      localFileInputStream = new FileInputStream("lock.dat");
      bool = true;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    return bool;
  }

  public static String convertToText(InputStream paramInputStream)
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      String str = null;
      while ((str = localBufferedReader.readLine()) != null)
      {
        localStringBuilder.append(new StringBuilder().append(str).append("\n").toString());
      }
      //System.out.println(localStringBuilder);
      paramInputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localStringBuilder.toString();
  }
}