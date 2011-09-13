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
package org.openflexo.wkf.controller;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.selection.FlexoClipboard;
import org.openflexo.selection.PastingGraphicalContext;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.processeditor.ProcessEditorConstants;
import org.openflexo.xmlcode.XMLSerializable;


import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleCompound;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFArtefact;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddPort;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.edge.WKFEdge;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.LOOPOperator;
import org.openflexo.foundation.wkf.node.NodeCompound;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.localization.FlexoLocalization;

/**
 * WKFClipboard is intented to be the object working with the
 * WKFSelectionManager and storing copied, cutted and pasted objects. Handled
 * objects are instances implementing
 * {@link org.openflexo.selection.SelectableView}.
 *
 * @author sguerin
 */
public class WKFClipboard extends FlexoClipboard {

	private static final Logger logger = Logger.getLogger(WKFClipboard.class.getPackage().getName());

	protected WKFSelectionManager _wkfSelectionManager;

	private FlexoModelObject _clipboardData;

	private FlexoLevel currentCopyLevel = null;

	public WKFClipboard(WKFSelectionManager aSelectionManager, JMenuItem copyMenuItem, JMenuItem pasteMenuItem, JMenuItem cutMenuItem) {
		super(aSelectionManager, copyMenuItem, pasteMenuItem, cutMenuItem);
		_wkfSelectionManager = aSelectionManager;
		resetClipboard();
	}

	public WKFSelectionManager getSelectionManager() {
		return _wkfSelectionManager;
	}

	public WKFController getWKFController() {
		return getSelectionManager().getWKFController();
	}

	@Override
	public boolean performSelectionPaste() {
		if (_isPasteEnabled) {
			/*
			 * if (getWKFController().getFlexoPaletteWindow().hasFocus()) {
			 * //JTabbedPane tabs =
			 * getWKFController().getFlexoPaletteWindow().currentTabbedPane;
			 * //String selectedTabName =
			 * tabs.getComponent(tabs.getSelectedIndex()).getName(); String
			 * selectedTabName =
			 * getWKFController().getFlexoPaletteWindow().getPalette
			 * ().getCurrentPalettePanel().getName(); File dir = new
			 * File(((WKFPalette
			 * )getWKFController().getFlexoPaletteWindow().getPalette
			 * ()).paletteDirectory(), selectedTabName); String exportedName =
			 * WKFController
			 * .askForString("Please enter a name for the exported element.");
			 * exportClipboardToPalette(dir, exportedName);
			 * getWKFController().recreatePalette(); return true; } else {
			 */
			return super.performSelectionPaste();
			// }
		} else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Sorry, PASTE disabled");
			return false;
		}
	}

	@Override
	protected void performSelectionPaste(FlexoModelObject pastingContext, PastingGraphicalContext graphicalContext) 
	{
		logger.info("performSelectionPaste() with context "+pastingContext);

		JComponent targetContainer = null;
		Point2D location = null;
		if (graphicalContext != null) {
			targetContainer = graphicalContext.targetContainer;
			location = graphicalContext.precisePastingLocation;
			if (location==null) {
				location = graphicalContext.pastingLocation;
			}
		}
		if (location==null)
			location = new Point2D.Double();

		if (pastingContext instanceof PetriGraphNode) {
			pastingContext = ((PetriGraphNode) pastingContext).getParentPetriGraph();
		} else if (pastingContext instanceof WKFArtefact) {
			pastingContext = ((WKFArtefact) pastingContext).getParentPetriGraph();
		}
		if (isTargetValidForPasting(pastingContext)) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("Paste is legal");
			getSelectionManager().resetSelection();

			XMLSerializable pasted = null;
			try {
				pasted = _clipboardData.cloneUsingXMLMapping();
			} catch (Exception ex) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Unexpected exception raised during pasting: " + ex.getMessage());
				ex.printStackTrace();
				return;
			}

			if (pasted instanceof NodeCompound && pastingContext instanceof WKFObject) {
				Vector<WKFObject> newSelection = pasteNodeCompound((WKFObject)pastingContext, (NodeCompound)pasted, location);
				getWKFController().getSelectionManager().setSelectedObjects(newSelection);
			}
			
			else if (pasted instanceof RoleCompound && pastingContext instanceof RoleList) {
				Vector<Role> newSelection = pasteRoleCompound((RoleList)pastingContext, (RoleCompound)pasted, location);
				getWKFController().getSelectionManager().setSelectedObjects(newSelection);
			}

		} else {
			FlexoController.notify(FlexoLocalization.localizedForKey("cannot_paste_at_this_place_wrong_level"));
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Paste is NOT legal");
				logger.info("pastingContext="+pastingContext+" of "+pastingContext.getClass().getSimpleName());
				logger.info("_clipboardData="+_clipboardData);
				logger.info("xml="+(_clipboardData!=null?_clipboardData.getXMLRepresentation():null));
			}
		}
	}

	protected Vector<WKFObject> pasteNodeCompound(WKFObject pastingContext, NodeCompound pastedCompound, Point2D location) 
	{

		WKFObject container = pastingContext;
		FlexoProcess process = container.getProcess();

		// We paste as a selection of nodes inside a NodeCompound
		NodeCompound compound = pastedCompound;
		String context = ProcessEditorConstants.BASIC_PROCESS_EDITOR;
		compound.setLocation(location, context);

		Vector<WKFObject> newSelection = new Vector<WKFObject>();
		FGERectangle bounds = new FGERectangle(new FGEPoint(location), new FGEDimension(0, 0), Filling.FILLED);
		for (Enumeration<WKFObject> e = compound.getAllEmbeddedWKFObjects().elements(); e.hasMoreElements();) {
			WKFObject node = e.nextElement();
			if (node == compound)
				continue;
			bounds.width = Math.max(bounds.width,node.getX(context)+node.getWidth(context)-bounds.x);
			bounds.height = Math.max(bounds.height,node.getY(context)+node.getHeight(context)-bounds.y);
		}
		// GPO: Not sure it is required, so I leave it but commented
		/*bounds.width+=20; // We add this for the borders of the GR
			bounds.height+=10;*/
		FGERectangle processBounds = new FGERectangle(0, 0, process.getWidth(context), process.getHeight(context), Filling.FILLED);
		processBounds = processBounds.rectangleUnion(bounds);
		process.setWidth(processBounds.width, context);
		process.setHeight(processBounds.height, context);
		for (Enumeration<WKFObject> e = compound.getAllEmbeddedWKFObjects().elements(); e.hasMoreElements();) {
			WKFObject node = e.nextElement();
			if (node == compound)
				continue;
			insertWKFObject(node, container);
			newSelection.add(node);
		}
		
		return newSelection;

	}

	protected Vector<Role> pasteRoleCompound(RoleList pastingContext, RoleCompound pastedCompound, Point2D location) 
	{
		pastedCompound.setLocation(location, RepresentableFlexoModelObject.DEFAULT);
		
		Vector<Role> newSelection = new Vector<Role>();
		FGERectangle bounds = new FGERectangle(new FGEPoint(location), new FGEDimension(0, 0), Filling.FILLED);
		for (Enumeration<Role> e = pastedCompound.getRoles().elements(); e.hasMoreElements();) {
			Role role = e.nextElement();
			bounds.width = Math.max(bounds.width,role.getX(RepresentableFlexoModelObject.DEFAULT)+role.getWidth(RepresentableFlexoModelObject.DEFAULT)-bounds.x);
			bounds.height = Math.max(bounds.height,role.getY(RepresentableFlexoModelObject.DEFAULT)+role.getHeight(RepresentableFlexoModelObject.DEFAULT)-bounds.y);
		}
		FGERectangle roleListBounds = new FGERectangle(0, 0, pastingContext.getWidth(RepresentableFlexoModelObject.DEFAULT), pastingContext.getHeight(RepresentableFlexoModelObject.DEFAULT), Filling.FILLED);
		roleListBounds = roleListBounds.rectangleUnion(bounds);
		pastingContext.setWidth(roleListBounds.width, RepresentableFlexoModelObject.DEFAULT);
		pastingContext.setHeight(roleListBounds.height, RepresentableFlexoModelObject.DEFAULT);
		for (Enumeration<Role> e = pastedCompound.getRoles().elements(); e.hasMoreElements();) {
			Role role = e.nextElement();
			insertRole(role, pastingContext);
			newSelection.add(role);
		}
		
		return newSelection;

	}

	private void insertRole(Role toInsert, RoleList roleList)
	{
		logger.info("Insert role "+toInsert);
		try {
			roleList.addToRoles(toInsert);
		}
		catch (DuplicateRoleException e) {
			int i=1;
			String baseName = toInsert.getName();
			boolean ok = false;
			while (!ok) {
				try {
					toInsert.setName(baseName+"-"+i);
					roleList.addToRoles(toInsert);
					ok = true;
				} catch (DuplicateRoleException e1) {
					i++;
				}
			}
		}
	}
	
	public void insertWKFObject(WKFObject toInsert, WKFObject container) 
	{
		if (toInsert instanceof FlexoPort) {
			FlexoPort port = (FlexoPort) toInsert;
			if (container instanceof PortRegistery) {
				AddPort action = null;
				if (port instanceof NewPort) {
					action = AddPort.createNewPort.makeNewAction(container, null, getWKFController().getEditor());
				}
				else if (port instanceof DeletePort) {
					action = AddPort.createDeletePort.makeNewAction(container, null, getWKFController().getEditor());
				}
				else if (port instanceof InPort) {
					action = AddPort.createInPort.makeNewAction(container, null, getWKFController().getEditor());
				}
				else if (port instanceof OutPort) {
					action = AddPort.createOutPort.makeNewAction(container, null, getWKFController().getEditor());
				}
				else if (port instanceof InOutPort) {
					action = AddPort.createInOutPort.makeNewAction(container, null, getWKFController().getEditor());
				}
				action.setNewPortName((port).getDefaultName());
				action.setEditNodeLabel(false);
				action.doAction();
			}
		} else {
			FlexoPetriGraph pg = null;
			if (container instanceof SelfExecutableNode)
				pg = ((SelfExecutableNode) container).getExecutionPetriGraph();
			if (container instanceof FlexoProcess)
				pg = ((FlexoProcess) container).getActivityPetriGraph();
			if (container instanceof AbstractActivityNode)
				pg = ((AbstractActivityNode) container).getOperationPetriGraph();
			if (container instanceof OperationNode)
				pg = ((OperationNode) container).getActionPetriGraph();
			if (container instanceof LOOPOperator)
				pg = ((LOOPOperator) container).getExecutionPetriGraph();
			if (container instanceof FlexoPetriGraph)
				pg = (FlexoPetriGraph) container;
			if (container instanceof WKFGroup)
				pg = ((WKFGroup) container).getParentPetriGraph();
			if (pg!=null) {
				DropWKFElement action = DropWKFElement.actionType.makeNewAction(pg, null, getWKFController().getEditor());
				action.setObject(toInsert);
				action.setResetNodeName(false);
				action.setEditNodeLabel(false);
				action.setLeaveSubProcessNodeUnchanged(true);
				if (container instanceof WKFGroup)
					action.setGroup((WKFGroup) container);
				action.doAction();
			}
		}
	}

	@Override
	protected boolean isCurrentSelectionValidForCopy(Vector<FlexoModelObject> currentlySelectedObjects) {
		return (getSelectionManager().getSelectionSize() > 0);
	}

	protected void resetClipboard() {
		currentCopyLevel = null;
		_clipboardData = null;
	}

	public FlexoModelObject getClipboardData() {
		return _clipboardData;
	}

	/**
	 * Selection procedure for copy
	 */
	@Override
	protected boolean performCopyOfSelection(Vector<FlexoModelObject> currentlySelectedObjects)
	{
		logger.info("performCopyOfSelection() for "+currentlySelectedObjects);

		// TODO reimplement all of this

		resetClipboard();

		if (currentlySelectedObjects.size() > 0) {

			if (currentlySelectedObjects.firstElement() instanceof PetriGraphNode
					|| currentlySelectedObjects.firstElement() instanceof WKFEdge<?,?>) {

				Vector<PetriGraphNode> selectedNodes = new Vector<PetriGraphNode>();
				Vector<WKFArtefact> selectedAnnotations = new Vector<WKFArtefact>();

				FlexoProcess process = null;
				for (Enumeration<FlexoModelObject> e = currentlySelectedObjects.elements(); e.hasMoreElements();) {
					FlexoModelObject next = e.nextElement();
					if (logger.isLoggable(Level.FINE))
						logger.fine("Selected: " + next);
					if (next instanceof PetriGraphNode ) {
						PetriGraphNode node = (PetriGraphNode) next;
						process = node.getProcess();
						selectedNodes.add(node);
					} else if (next instanceof WKFArtefact) {
						WKFArtefact annotation = (WKFArtefact) next;
						process = annotation.getProcess();
						selectedAnnotations.add(annotation);
					}
				}

				currentCopyLevel = getSelectionManager().getSelectionLevel();
				if (selectedNodes.size() + selectedAnnotations.size() > 0) { // We make
					// a
					// NodeCompound
					_clipboardData = new NodeCompound(process, selectedNodes, selectedAnnotations);
				}
			}
			else if (currentlySelectedObjects.firstElement() instanceof Role) {

				FlexoProject project = ((Role)currentlySelectedObjects.firstElement()).getProject();
				Vector<Role> rolesToBeCopied = new Vector<Role>();
				for (FlexoModelObject o : currentlySelectedObjects) {
					if (o instanceof Role) {
						rolesToBeCopied.add((Role)o);
					}
				}
				_clipboardData = new RoleCompound(project.getFlexoWorkflow(),rolesToBeCopied);
				currentCopyLevel = FlexoLevel.WORKFLOW;

			}
		}

		if (_clipboardData != null) {
			logger.fine("Storing in clipboard: " + _clipboardData.getXMLRepresentation());
		}
		else {
			logger.warning("null value in clipboard ! ");
		}
		return true;
	}

	protected boolean isTargetValidForPasting(FlexoModelObject pastingContext) 
	{
		if (pastingContext == null)
			return false;
		if (_clipboardData instanceof NodeCompound) {
			if (pastingContext instanceof PortRegistery) {
				return ((NodeCompound)_clipboardData).getLevel() == FlexoLevel.PORT;
			} else {
				FlexoPetriGraph targetPetriGraph = null;
				if (pastingContext instanceof FlexoProcess)
					targetPetriGraph = ((FlexoProcess) pastingContext).getActivityPetriGraph();
				else if (pastingContext instanceof FlexoPetriGraph)
					targetPetriGraph = (FlexoPetriGraph) pastingContext;
				else if (pastingContext instanceof WKFGroup)
					targetPetriGraph = ((WKFGroup)pastingContext).getParentPetriGraph();
				else {
					if (logger.isLoggable(Level.INFO))
						logger.info("Cannot paste into " + pastingContext.getClass().getName());
				}
				if (targetPetriGraph != null) {
					return targetPetriGraph.acceptsObject((NodeCompound)_clipboardData);
				}
			}
		}
		else if (_clipboardData instanceof RoleCompound) {
			if (pastingContext instanceof RoleList) return true;
			else if (pastingContext instanceof Role) return true;
		}
		return false;
	}

	protected void exportClipboardToPalette(File paletteDirectory, String newPaletteElementName)
	{
		if (_clipboardData instanceof NodeCompound) {
			NodeCompound compound = (NodeCompound)_clipboardData;
			if (compound.isSingleNode()) {
				if (!newPaletteElementName.endsWith(".xml")) {
					newPaletteElementName = newPaletteElementName + ".xml";
					try {
						FileUtils.saveToFile(newPaletteElementName, compound.getFirstNode().getXMLRepresentation(), paletteDirectory);
					} catch (Exception e) {
						if (logger.isLoggable(Level.WARNING))
							logger.warning("Error occurs while saving node in the palette\n" + e.getMessage());
					}
				}
			} else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("Sorry, but you can't export more than one top level node in the palette");
			}
		}
	}
}
