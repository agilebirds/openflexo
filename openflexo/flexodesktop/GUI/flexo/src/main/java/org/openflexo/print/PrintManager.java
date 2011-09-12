/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.view.controller.FlexoController;


public class PrintManager {

    protected static final Logger logger = Logger.getLogger(PrintManager.class.getPackage().getName());

    private static final PrintManager _printManager= new PrintManager();
    
    private PrinterJob _printJob;
    private PageFormat _pageFormat;
    
    private PrintManager()
    {
        super();
        _printJob = PrinterJob.getPrinterJob();
        _pageFormat = _printJob.defaultPage();
    }
    
    public static PrintManager getPrintManager()
    {
        return _printManager;
    }
    
    public void printPrintable (Printable printable)
    {
        _printJob.validatePage(_pageFormat);
        _printJob.setPrintable(printable, _pageFormat);
        _print();
    }
    
    public void printPageable (Pageable pageable)
    {
        logger.info("REQUEST for PRINTING "+pageable);
        _printJob.validatePage(_pageFormat);
        _printJob.setPageable(pageable);
        _print();
    }
    
    private void _print()
    {
         if (_printJob.printDialog()) {
            try {
                logger.info("STARTING PRINTING");
               _printJob.print();
            } catch (PrinterAbortException ex) {
                FlexoController.showError("printing_cancelled");
            } catch (PrinterIOException ex) {
                FlexoController.showError("printing_io_error");
            } catch (PrinterException ex) {
                FlexoController.showError("printing_failed");
           }
        }
    }
    
    public PageFormat pageSetup()
    {
        _pageFormat = _printJob.pageDialog(_pageFormat);
        if (logger.isLoggable(Level.FINE)) debugPageFormat();
        return _pageFormat;
    }
    
    public String debugPageFormat()
    {
        String returned = "Page format ";
        returned += "Paper="+debugPaper(_pageFormat.getPaper())+" ";
        returned += "Orientation="+(_pageFormat.getOrientation()==PageFormat.PORTRAIT?"PORTRAIT":"")+(_pageFormat.getOrientation()==PageFormat.LANDSCAPE?"LANDSCAPE":"")+(_pageFormat.getOrientation()==PageFormat.REVERSE_LANDSCAPE?"REVERSE_LANDSCAPE":"")+" ";
        returned += "Dimensions=("+_pageFormat.getWidth()+"x"+_pageFormat.getHeight()
        +",["+_pageFormat.getImageableWidth()+"x"+_pageFormat.getImageableHeight()
        +","+_pageFormat.getImageableX()+","+_pageFormat.getImageableY()+"])";
        if (logger.isLoggable(Level.FINE))
            logger.fine(returned);
        return returned;
    }
    
    public String debugPaper(Paper paper)
    {
        return "Paper("+paper.getWidth()+"x"+paper.getHeight()
        +",["+paper.getImageableWidth()+"x"+paper.getImageableHeight()
        +","+paper.getImageableX()+","+paper.getImageableY()+"])";
    }

    public PageFormat getPageFormat() 
    {
        return _pageFormat;
    }
    
}
