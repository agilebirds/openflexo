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
package org.openflexo.fib.editor.test;

import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;

/**
 * Dialog allowing to automatically ask and edit parameters
 *
 * @author sguerin
 *
 */
public class TestFIB extends JPanel
{

	static final Logger logger = Logger.getLogger(TestFIB.class.getPackage().getName());

	/*  private TabModelView paramsPanel;
    private InspectableObject _inspected;

    public FIBPanel(TabModel model, InspectableObject inspected)
    {
    	super();
    	_inspected = inspected;
    	setLayout(new BorderLayout());
    	paramsPanel = new TabModelView(model,null,FIBController.instance());
    	paramsPanel.performObserverSwitch(inspected);
    	paramsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    	paramsPanel.valueChange(inspected);
    	add(paramsPanel,BorderLayout.CENTER);
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
        		paramsPanel.requestFocusInFirstWidget();
        	}
        });
   }

    public DenaliWidget getInspectorWidgetForParameter (ParameterDefinition parameterDefinition)
    {
    	return paramsPanel.getInspectorWidgetFor(parameterDefinition.getName());
    }

    public void update()
    {
        paramsPanel.valueChange(_inspected);
        paramsPanel.performObserverSwitch(_inspected);
    }

    public void setBackground(Color aColor)
    {
        super.setBackground(aColor);
        if (paramsPanel != null)
            paramsPanel.setBackground(aColor);
    }

	 */

	public static void main(String[] args)
	{
		try {
			ToolBox.setPlatform();
			FlexoLoggingManager.initialize();
			FlexoLoggingManager.setKeepLogTrace(true);
			FlexoLoggingManager.setLogCount(-1);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JFrame frame = new JFrame();

		FileResource fibFile = new FileResource("TestFIB/Test.fib");
		System.out.println("Fib: "+fibFile.getAbsolutePath());

		FIBComponent fibComponent = FIBLibrary.instance().retrieveFIBComponent(fibFile);

		Coucou coucou = new Coucou();
		
		FIBView testFibPanel = FIBController.makeView(fibComponent);
		testFibPanel.getController().setDataObject(coucou);

		frame.getContentPane().add(testFibPanel.getResultingJComponent());
		frame.setVisible(true);

		/*TabModel tabModel = null;

		try {
			tabModel = FIBController.instance().importInspectorFile(fibFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FIBPanel testFib = new FIBPanel(tabModel, new Coucou());

		frame.getContentPane().add(testFib);
		frame.setVisible(true);*/
	}

}
