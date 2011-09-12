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
package org.openflexo.fib.editor.view.widget;

import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTree;

import org.openflexo.fib.editor.controller.FIBEditorController;
import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.FIBEditableViewDelegate;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBAddingNotification;
import org.openflexo.fib.model.FIBAttributeNotification;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBModelNotification;
import org.openflexo.fib.model.FIBRemovingNotification;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.logging.FlexoLogger;

public class FIBEditableBrowserWidget extends FIBBrowserWidget implements FIBEditableView<FIBBrowser,JTree> {

	private static final Logger logger = FlexoLogger.getLogger(FIBEditableBrowserWidget.class.getPackage().getName());

	private final FIBEditableViewDelegate<FIBBrowser,JTree> delegate;
	
	private final FIBEditorController editorController;
	
	@Override
	public FIBEditorController getEditorController() 
	{
		return editorController;
	}
	
	public FIBEditableBrowserWidget(FIBBrowser model, FIBEditorController editorController)
	{
		super(model,editorController.getController());
		this.editorController = editorController;
		
		delegate = new FIBEditableViewDelegate<FIBBrowser,JTree>(this);
		model.addObserver(this);
	}	
	
	@Override
	public void delete() 
	{
		delegate.delete();
		getComponent().deleteObserver(this);
		super.delete();
	}	
	
	public Vector<PlaceHolder> getPlaceHolders() 
	{
		return null;
	}
	
	public FIBEditableViewDelegate<FIBBrowser,JTree> getDelegate()
	{
		return delegate;
	}
	
	@Override
	public void update(Observable o, Object dataModification) 
	{
		 if (dataModification instanceof FIBAddingNotification
				 || dataModification instanceof FIBRemovingNotification) {
			 	updateBrowser();
		 }
		 else if (dataModification instanceof FIBAttributeNotification) {			 
				FIBAttributeNotification n = (FIBAttributeNotification)dataModification;
				/*if (n.getAttribute() == FIBBrowser.Parameters.iteratorClassName
						|| n.getAttribute() == FIBBrowser.Parameters.rowHeight
						|| n.getAttribute() == FIBBrowser.Parameters.visibleRowCount) {
					updateBrowser();
				}*/
				updateBrowser();

		 }
		 
		if (dataModification instanceof FIBModelNotification) {
			delegate.receivedModelNotifications(o, (FIBModelNotification)dataModification);
		}		
	}


}
