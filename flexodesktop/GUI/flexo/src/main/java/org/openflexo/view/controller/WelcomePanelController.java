package org.openflexo.view.controller;

import java.io.File;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.module.Module;
import org.openflexo.view.controller.FlexoFIBController;

public class WelcomePanelController extends FlexoFIBController {

	public WelcomePanelController(FIBComponent component) 
	{
		super(component);
	}

	public void exit()
	{
		System.out.println("called exit");

	}

	public void openModule(Module module)
	{
		validateAndDispose();
		System.out.println("open module "+module);
	}

	public void openProject(File project, Module module)
	{
		validateAndDispose();
		System.out.println("open project "+project+" module "+module);
	}

	public void newProject(Module module)
	{
		validateAndDispose();
		System.out.println("new project module "+module);
	}


}
