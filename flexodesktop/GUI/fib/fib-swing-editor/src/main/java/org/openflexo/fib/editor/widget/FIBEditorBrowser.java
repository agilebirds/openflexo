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
package org.openflexo.fib.editor.widget;

import java.util.logging.Logger;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.FileResource;

/**
 * Browser for FIBEditor elements
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class FIBEditorBrowser extends DefaultFIBCustomComponent<FIBComponent> {

	protected static final Logger logger = Logger.getLogger(FIBEditorBrowser.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/Browser.fib");

	private final FIBEditorController editorController;

	public FIBEditorBrowser(FIBComponent fibComponent, FIBEditorController editorController) {
		super(FIB_FILE, fibComponent, FlexoLocalization.getMainLocalizer());
		this.editorController = editorController;
	}

	@Override
	protected FIBBrowserController makeFIBController(FIBComponent browserComponent, LocalizedDelegate parentLocalizer) {
		return new FIBBrowserController(browserComponent, editorController);
	}

	@Override
	public FIBBrowserController getController() {
		return (FIBBrowserController) super.getController();
	}

	@Override
	public Class<FIBComponent> getRepresentedType() {
		return FIBComponent.class;
	}

	@Override
	public void delete() {
	}

}
