package com.bookreader.ui.Reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class DesEncrypter
{
  Cipher ecipher;
  Cipher dcipher;
  byte[] buf = new byte[1024];

  DesEncrypter(SecretKey paramSecretKey)
  {
    byte[] arrayOfByte = { -114, 18, 57, -100, 7, 114, 111, 90 };
    IvParameterSpec localIvParameterSpec = new IvParameterSpec(arrayOfByte);
    try
    {
      this.ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      this.dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
      this.ecipher.init(1, paramSecretKey, localIvParameterSpec);
      this.dcipher.init(2, paramSecretKey, localIvParameterSpec);
    }
    catch (InvalidAlgorithmParameterException localInvalidAlgorithmParameterException)
    {
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
    }
  }

  public void encrypt(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    try
    {
      paramOutputStream = new CipherOutputStream(paramOutputStream, this.ecipher);
      int i = 0;
      while ((i = paramInputStream.read(this.buf)) >= 0)
        paramOutputStream.write(this.buf, 0, i);
      paramOutputStream.close();
    }
    catch (IOException localIOException)
    {
    }
  }

  public void decrypt(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    try
    {
      paramInputStream = new CipherInputStream(paramInputStream, this.dcipher);
      int i = 0;
      while ((i = paramInputStream.read(this.buf)) >= 0)
        paramOutputStream.write(this.buf, 0, i);
      paramOutputStream.close();
    }
    catch (IOException localIOException)
    {
    }
  }
}