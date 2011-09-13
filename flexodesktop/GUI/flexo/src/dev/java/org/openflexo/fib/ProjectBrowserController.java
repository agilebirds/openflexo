package org.openflexo.fib;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.controller.FlexoFIBController;

public class ProjectBrowserController extends FlexoFIBController {

	public ProjectBrowserController(FIBComponent component) 
	{
		super(component);
	}
	
	public Icon getIconForEventNode(EventNode event)
    {
        return WKFIconLibrary.getIconForEventNode(event);
    }

	
}
