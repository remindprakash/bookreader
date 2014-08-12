package com.bookreader.ui.Reader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class PDFEncrypter
{
  private static SecretKey key;

  public static InputStream encrypt(InputStream paramInputStream)
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      key = KeyGenerator.getInstance("DES").generateKey();
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(new FileOutputStream("tmp.ser"));
      localObjectOutputStream.writeObject(key);
      localObjectOutputStream.close();
      DesEncrypter localDesEncrypter = new DesEncrypter(key);
      localDesEncrypter.encrypt(paramInputStream, localByteArrayOutputStream);
      localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    }
    catch (Exception localException)
    {
    }
    return localByteArrayInputStream;
  }

  public static InputStream decrypt(InputStream paramInputStream)
  {
    ByteArrayInputStream localByteArrayInputStream = null;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      if (key == null)
      {
        Object localObject = Thread.currentThread().getContextClassLoader().getResourceAsStream("tmp.ser");
        ObjectInputStream localObjectInputStream = new ObjectInputStream((InputStream)localObject);
        key = (SecretKey)localObjectInputStream.readObject();
      }
      Object localObject = new DesEncrypter(key);
      ((DesEncrypter)localObject).decrypt(paramInputStream, localByteArrayOutputStream);
      localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    }
    catch (Exception localException)
    {
    }
    return localByteArrayInputStream;
  }

  public static void writeToFile(InputStream paramInputStream)
  {
    byte[] arrayOfByte = new byte[1024];
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream("A1B2C3D4E5.dat");
      int i = 0;
      while ((i = paramInputStream.read(arrayOfByte)) >= 0)
        localFileOutputStream.write(arrayOfByte, 0, i);
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void writeToFile(InputStream paramInputStream, String paramString)
  {
    byte[] arrayOfByte = new byte[1024];
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      int i = 0;
      while ((i = paramInputStream.read(arrayOfByte)) >= 0)
        localFileOutputStream.write(arrayOfByte, 0, i);
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static String convertToText(InputStream paramInputStream)
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    StringBuilder localStringBuilder = new StringBuilder();
    try
    {
      String str = null;
      while ((str = localBufferedReader.readLine()) != null)
        localStringBuilder.append(new StringBuilder().append(str).append("\n").toString());
      paramInputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return localStringBuilder.toString();
  }

  public static void main(String[] paramArrayOfString)
  {
    FileInputStream localFileInputStream = null;
    try
    {
      localFileInputStream = new FileInputStream("d:\\PDF\\file (1).pdf");
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      localFileNotFoundException.printStackTrace();
    }
    writeToFile(encrypt(localFileInputStream));
  }
}