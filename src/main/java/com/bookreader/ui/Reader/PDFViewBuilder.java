package com.bookreader.ui.Reader;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

public class PDFViewBuilder extends SwingViewBuilder
{
  public PDFViewBuilder(SwingController paramSwingController)
  {
    super(paramSwingController);
  }

  public JMenuItem buildSaveAsFileMenuItem()
  {
    return null;
  }

  public JButton buildPrintButton()
  {
    return null;
  }

  public JMenuItem buildPrintMenuItem()
  {
    return null;
  }

  public JButton buildSaveAsFileButton()
  {
    return null;
  }
}