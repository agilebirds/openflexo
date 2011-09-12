package org.openflexo.foundation.ontology.calc;

import java.io.File;
import java.util.Vector;

public class CalcFolder extends CalcLibraryObject {

	private final CalcLibrary calcLibrary;
	private final String name;
	private final CalcFolder parent;
	private final Vector<CalcFolder> children;
	private final Vector<OntologyCalc> calcs;
	
	public CalcFolder(String name, CalcFolder parentFolder, CalcLibrary calcLibrary)
	{
		this.calcLibrary = calcLibrary;
		this.name = name;
		this.parent = parentFolder;
		children = new Vector<CalcFolder>();
		calcs = new Vector<OntologyCalc>();
		if (parentFolder != null) {
			parentFolder.addToChildren(this);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public Vector<CalcFolder> getChildren()
	{
		return children;
	}

	public void addToChildren(CalcFolder aFolder)
	{
		children.add(aFolder);
	}

	public void removeFromChildren(CalcFolder aFolder)
	{
		children.remove(aFolder);
	}
	
	public Vector<OntologyCalc> getCalcs() 
	{
		return calcs;
	}
	public void addToCalcs(OntologyCalc aCalc)
	{
		calcs.add(aCalc);
	}

	public void removeFromCalcs(OntologyCalc aCalc)
	{
		calcs.remove(aCalc);
	}

	@Override
	public CalcLibrary getCalcLibrary() 
	{
		return calcLibrary;
	}

	@Override
	public String getInspectorName()
	{
		return null;
	}

	public File getExpectedPath()
	{
		if (parent != null) {
			return new File(parent.getExpectedPath(),name);
		}
		return calcLibrary.getResourceCenter().getNewCalcSandboxDirectory();
	}
}
