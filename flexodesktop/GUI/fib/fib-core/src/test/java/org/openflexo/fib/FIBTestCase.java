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
package org.openflexo.fib;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.BeforeClass;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBBrowserElement;
import org.openflexo.fib.model.FIBBrowserElement.FIBBrowserElementChildren;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBDropDownColumn;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabelColumn;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumberColumn;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBRadioButtonList;
import org.openflexo.fib.model.FIBTable;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBTextFieldColumn;
import org.openflexo.kvc.KeyValueLibrary;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Provides a JUnit 4 generic environment for FIB testing purposes
 * 
 */
public abstract class FIBTestCase {

	private static final Logger logger = FlexoLogger.getLogger(FIBTestCase.class.getPackage().getName());

	protected static FIBModelFactory factory;

	@BeforeClass
	public static void setUpClass() {
		try {
			factory = new FIBModelFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public FIBPanel newFIBPanel() {
		return factory.newInstance(FIBPanel.class);
	}

	public FIBLabel newFIBLabel() {
		return factory.newInstance(FIBLabel.class);
	}

	public FIBLabel newFIBLabel(String label) {
		FIBLabel returned = factory.newInstance(FIBLabel.class);
		returned.setLabel(label);
		return returned;
	}

	public FIBTextField newFIBTextField() {
		return factory.newInstance(FIBTextField.class);
	}

	public FIBBrowser newFIBBrowser() {
		return factory.newInstance(FIBBrowser.class);
	}

	public FIBBrowserElement newFIBBrowserElement() {
		return factory.newInstance(FIBBrowserElement.class);
	}

	public FIBBrowserElementChildren newFIBBrowserElementChildren() {
		return factory.newInstance(FIBBrowserElementChildren.class);
	}

	public FIBCheckboxList newFIBCheckboxList() {
		return factory.newInstance(FIBCheckboxList.class);
	}

	public FIBDropDown newFIBDropDown() {
		return factory.newInstance(FIBDropDown.class);
	}

	public FIBList newFIBList() {
		return factory.newInstance(FIBList.class);
	}

	public FIBNumber newFIBNumber() {
		return factory.newInstance(FIBNumber.class);
	}

	public FIBRadioButtonList newFIBRadioButtonList() {
		return factory.newInstance(FIBRadioButtonList.class);
	}

	public FIBNumberColumn newFIBNumberColumn() {
		return factory.newInstance(FIBNumberColumn.class);
	}

	public FIBLabelColumn newFIBLabelColumn() {
		return factory.newInstance(FIBLabelColumn.class);
	}

	public FIBDropDownColumn newFIBDropDownColumn() {
		return factory.newInstance(FIBDropDownColumn.class);
	}

	public FIBTable newFIBTable() {
		return factory.newInstance(FIBTable.class);
	}

	public FIBTextFieldColumn newFIBTextFieldColumn() {
		return factory.newInstance(FIBTextFieldColumn.class);
	}

	public FIBTextArea newFIBTextArea() {
		return factory.newInstance(FIBTextArea.class);
	}

	protected void log(String step) {
		logger.info("\n******************************************************************************\n" + step
				+ "\n******************************************************************************\n");
	}

	@After
	public void tearDown() throws Exception {
		KeyValueLibrary.clearCache();
	}
}
