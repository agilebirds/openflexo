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
package org.openflexo.fib.controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;


public class FIBDialog extends JDialog {

	private static final Logger logger = Logger.getLogger(FIBController.class.getPackage().getName());

	private static FIBDialog _visibleDialog = null;
	   
	private FIBView view;
	
	private FIBDialog(JFrame frame, boolean modal, FIBComponent fibComponent)
	{
		super(frame,fibComponent.getParameter("title"),modal);
		view = FIBController.makeView(fibComponent);
		getContentPane().add(view.getResultingJComponent());
		validate();
		pack();
	}
	
	public FIBController getController()
	{
		return view.getController();
	}
	
	public Status getStatus()
	{
		return getController().getStatus();
	}


    private void showDialog()
    {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
        if (_visibleDialog == null) {
            _visibleDialog = this;
            setVisible(true);
            toFront();
        } else {
            logger.warning("An other dialog box is already opened");
            //_waitingDialog.add(this);
        }
    }

    @Override
	public void dispose()
    {
    	super.dispose();
    	if (_visibleDialog == this) {
    		_visibleDialog = null;
    	}
    }

    @Override
    public void setVisible(boolean b) 
    {
    	super.setVisible(b);
    	if (!b) {
        	if (_visibleDialog == this) {
        		_visibleDialog = null;
        	}
    	}
    }

	public static FIBDialog instanciateComponent(File componentFile, Object data, JFrame frame, boolean modal)
	{
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(componentFile);
		if (fibComponent == null) {
			logger.warning("FileNotFoundException: "+componentFile.getAbsolutePath());
			return null;
		}
		return instanciateComponent(fibComponent, data, frame, modal);
	}

	public static FIBDialog instanciateComponent(String fibResourcePath, Object data, JFrame frame, boolean modal) {
		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibResourcePath);
		if (fibComponent == null) {
			logger.warning("ResourceNotFoundException: " + fibResourcePath);
			return null;
		}
		return instanciateComponent(fibComponent, data, frame, modal);
	}

	private static FIBDialog instanciateComponent(FIBComponent fibComponent, Object data, JFrame frame, boolean modal) {
		FIBDialog dialog = new FIBDialog(frame, modal, fibComponent);
		dialog.getController().setDataObject(data);
		dialog.showDialog();	
		return dialog;
	}

}
