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
package org.openflexo.inspector;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.InspectorEntry;
import org.openflexo.foundation.viewpoint.TextFieldInspectorEntry;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;


public class FIBInspector extends FIBPanel {

	static final Logger logger = Logger.getLogger(FIBInspector.class.getPackage().getName());

	private boolean superInspectorWereAppened = false;
	
	protected void appendSuperInspectors(MainInspectorController controller)
	{
		if (getDataType() == null) return;
		if (getDataType() instanceof Class) {
			FIBInspector superInspector = controller.inspectorForClass(((Class)getDataType()).getSuperclass());
			if (superInspector != null) {
				superInspector.appendSuperInspectors(controller);
				appendSuperInspector(superInspector);
			}
		}
	}

	@Override
	public String toString() 
	{
		return "Inspector["+getDataType()+"]";
	}
	
	protected void appendSuperInspector(FIBInspector superInspector)
	{
		if (!superInspectorWereAppened) {
			//System.out.println("Append "+superInspector+" to "+this);
			/*try {
				System.out.println("Clone container:\n"+XMLCoder.encodeObjectWithMapping(superInspector, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
				System.out.println("Found this:\n"+XMLCoder.encodeObjectWithMapping((XMLSerializable)Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()), FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			append((FIBPanel)Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()));
			superInspectorWereAppened = true;
		}
	}

	public FIBTabPanel getTabPanel()
	{
		return (FIBTabPanel)getSubComponents().firstElement();
	}
	
	public String getXMLRepresentation()
	{
		try {
			return XMLCoder.encodeObjectWithMapping(this, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance());
		} catch (InvalidObjectSpecificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AccessorInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateSerializationIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error ???";
	}
	
	/**
	 * This method looks after object's EditionPattern references
	 * to know if we need to structurally change inspector by adding or
	 * removing tabs, which all correspond to one and only one EditionPattern
	 * 
	 * @param object
	 * @return
	 */
	protected boolean updateEditionPatternReferences(FlexoModelObject object)
	{
		boolean needsChanges = false;
		
		if (object.getEditionPatternReferences() == null) {
			needsChanges = (currentEditionPatterns.size() > 0);
		}
		else {
			if (currentEditionPatterns.size() != object.getEditionPatternReferences().size()) {
				needsChanges = true;
			}
			else {
				for (int i=0; i<currentEditionPatterns.size(); i++) {
					if (currentEditionPatterns.get(i) != object.getEditionPatternReferences().get(i).getEditionPattern()) {
						needsChanges = true;
						break;
					}
				}
			}
		}
		
		if (!needsChanges) {
			// No changes detected, i can return
			return false;
		}
		
		for (FIBTab tab : tabsForEP.values()) {
			getTabPanel().removeFromSubComponents(tab);
		}
				
		currentEditionPatterns.clear();
		tabsForEP.clear();
		
		if (object.getEditionPatternReferences() != null) {
			for (EditionPatternReference ref : object.getEditionPatternReferences()) {
				EditionPatternInspector inspector = ref.getEditionPattern().getInspector();
				logger.info("OK, je rajoute un tab "+ref.getEditionPattern().getInspector().getInspectorTitle());
				FIBTab newTab = makeFIBTab(ref.getEditionPattern());
				currentEditionPatterns.add(ref.getEditionPattern());
				tabsForEP.put(ref.getEditionPattern(),newTab);
				getTabPanel().addToSubComponents(newTab);
			}
		}
		
		return true;

	}
 			
	private FIBTab makeFIBTab(EditionPattern ep)
	{
		FIBTab newTab = new FIBTab();
		newTab.setTitle(ep.getInspector().getInspectorTitle());
		newTab.setIndex(-1);
		newTab.setLayout(Layout.twocols);
		int index = 0;
		for (InspectorEntry entry : ep.getInspector().getEntries()) {
			FIBLabel label = new FIBLabel();
			label.setLabel(entry.getLabel());
			newTab.addToSubComponents(label,new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false, index++));
			if (entry instanceof TextFieldInspectorEntry) {
				FIBTextField tf = new FIBTextField();
				newTab.addToSubComponents(tf,new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index++));
			}
			else {
				FIBLabel unknown = new FIBLabel();
				unknown.setLabel("???");
				newTab.addToSubComponents(unknown,new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index++));
			}
		}
		return newTab;
	}
	
	private Vector<EditionPattern> currentEditionPatterns = new Vector<EditionPattern>();
	private Hashtable<EditionPattern,FIBTab> tabsForEP = new Hashtable<EditionPattern, FIBTab>();
	
}
