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
package org.openflexo.foundation.wkf.node;

import java.awt.Color;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.bindings.ComponentBindingDefinition;
import org.openflexo.foundation.help.ApplicationHelpEntryPoint;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.ComponentInstanceBinding;
import org.openflexo.foundation.ie.ComponentInstanceOwner;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.stats.OperationStatistics;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.validation.DeletionFixProposal;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.FlexoLevel;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.MetricsValue;
import org.openflexo.foundation.wkf.MetricsValue.MetricsValueOwner;
import org.openflexo.foundation.wkf.MetricsValueAdded;
import org.openflexo.foundation.wkf.MetricsValueRemoved;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.AddOperationMetricsValue;
import org.openflexo.foundation.wkf.action.DeleteMetricsValue;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.dm.ComponentTextColorChanged;
import org.openflexo.foundation.wkf.dm.OperationComponentHasBeenRemoved;
import org.openflexo.foundation.wkf.dm.OperationComponentHasBeenSet;
import org.openflexo.foundation.wkf.dm.TabOperationComponentHasBeenRemoved;
import org.openflexo.foundation.wkf.dm.TabOperationComponentHasBeenSet;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.InvalidEdgeException;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.toolbox.ToolBox;

/**
 * This represents a FlexoNode at the level 'Operation'
 * 
 * @author sguerin
 * 
 */
public class OperationNode extends FatherNode implements ApplicationHelpEntryPoint, ChildNode, ComponentInstanceOwner,
		Comparable<OperationNode>, MetricsValueOwner, FlexoObserver {
	private static final Logger logger = Logger.getLogger(OperationNode.class.getPackage().getName());

	// protected String _woComponentName;
	// private OperationComponentDefinition _componentDefinition;
	private transient OperationComponentInstance _componentInstance;

	private transient TabComponentInstance _tabComponentInstance;

	protected transient Status _newStatus;

	private boolean _isSynchronized = false;

	private String _acronym;

	private FlexoCSS cssSheet;

	private transient OperationStatistics statistics;

	private Vector<MetricsValue> metricsValues;
	public static FlexoActionizer<AddOperationMetricsValue, OperationNode, WKFObject> addMetricsActionizer;
	public static FlexoActionizer<DeleteMetricsValue, MetricsValue, MetricsValue> deleteMetricsActionizer;

	/**
	 * Constructor used during deserialization
	 */
	public OperationNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public OperationNode(FlexoProcess process) {
		super(process);
		metricsValues = new Vector<MetricsValue>();
		// _petriGraph = new ActionPetriGraph(getProcess());
	}

	@Override
	public void setProcess(FlexoProcess p) {
		FlexoProcess old = getProcess();
		super.setProcess(p);
		if (old != null) {
			old.deleteObserver(this);
		}
		if (p != null) {
			p.addObserver(this);
		}
		if (_componentInstance != null) {
			_componentInstance.updateDependancies(old, p);
		}
		if (_tabComponentInstance != null) {
			_tabComponentInstance.updateDependancies(old, p);
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof FlexoProcess
				&& (dataModification instanceof NameChanged || dataModification instanceof WKFAttributeDataModification
						&& "businessDataType".equals(((WKFAttributeDataModification) dataModification).getAttributeName()))) { // Update
																																// business
																																// data
																																// accessing
																																// methods
																																// on
																																// screens
			updateProcessBusinessDataAccessingMethod();
		}
	}

	public static String DEFAULT_OPERATION_NODE_NAME() {
		return FlexoLocalization.localizedForKey("operation_default_name");
	}

	@Override
	public String getDefaultName() {
		if (isBeginNode() || isEndNode()) {
			return super.getDefaultName();
		} else {
			return DEFAULT_OPERATION_NODE_NAME();
		}
	}

	@Override
	public FlexoLevel getLevel() {
		return FlexoLevel.OPERATION;
	}

	@Override
	public OperationPetriGraph getParentPetriGraph() {
		return (OperationPetriGraph) super.getParentPetriGraph();
	}

	@Override
	public String getInspectorName() {
		if (getNodeType() == NodeType.BEGIN) {
			return Inspectors.WKF.BEGIN_OPERATION_NODE_INSPECTOR;
		} else if (getNodeType() == NodeType.END) {
			return Inspectors.WKF.END_OPERATION_NODE_INSPECTOR;
		} else {
			return Inspectors.WKF.OPERATION_NODE_INSPECTOR;
		}
	}

	public boolean mightHaveActionPetriGraph() {
		return getNodeType() == NodeType.NORMAL;
	}

	@Override
	public boolean hasContainedPetriGraph() {
		return mightHaveActionPetriGraph() && super.hasContainedPetriGraph();
	}

	public ActionPetriGraph getActionPetriGraph() {
		if (mightHaveActionPetriGraph()) {
			return (ActionPetriGraph) getContainedPetriGraph();
		} else {
			return null;
		}
	}

	public void setActionPetriGraph(ActionPetriGraph aPetriGraph) {
		setContainedPetriGraph(aPetriGraph);
	}

	@Override
	public AbstractActivityNode getFather() {
		return getAbstractActivityNode();
	}

	public boolean getIsSynchronized() {
		return _isSynchronized;
	}

	public void setIsSynchronized(boolean isSynchronized) {
		if (_isSynchronized != isSynchronized) {
			_isSynchronized = isSynchronized;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("isSynchronized", new Boolean(!_isSynchronized), new Boolean(_isSynchronized)));
		}
	}

	@Override
	public final void delete() {
		if (getComponentInstance() != null) {
			removeComponentInstance();
		}
		if (getTabOperationComponentInstance() != null) {
			removeTabComponentInstance();
		}
		// getParentPetriGraph().removeFromNodes(this);
		super.delete();
		deleteObservers();
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	/**
	 * Return a Vector of all embedded WKFObjects
	 * 
	 * @return a Vector of WKFObject instances
	 */
	@Override
	public Vector<WKFObject> getAllEmbeddedWKFObjects() {
		Vector<WKFObject> returned = super.getAllEmbeddedWKFObjects();
		returned.addAll(getMetricsValues());
		/*
		 * if (getComponentDefinition() != null) { returned.addAll(getComponentDefinition().getAllEmbeddedWKFObjects()); }
		 */
		return returned;
	}

	/**
	 * Returns all the action nodes embedded in this operation. This returns also action nodes embedded in LOOPOperator and
	 * SelfExecutableNode.
	 * 
	 * @return all the action nodes embedded in this operation.
	 */
	public Vector<ActionNode> getAllEmbeddedActionNodes() {
		if (getActionPetriGraph() != null) {
			return getActionPetriGraph().getAllEmbeddedActionNodes();
		}
		return new Vector<ActionNode>();
	}

	/**
	 * Returns a sorted vector of all the Action nodes that are in the underlying action petrigraph and the ones embedded by LOOPOperator
	 * and SelfExecutableNode. This is done recursively on all nodes.
	 * 
	 * @return a sorted vector of all the action nodes embedded in the underlying action petri graph.
	 */
	public Vector<ActionNode> getAllEmbeddedSortedOperationNodes() {
		if (getActionPetriGraph() != null) {
			return getActionPetriGraph().getAllEmbeddedSortedActionNodes();
		}
		return new Vector<ActionNode>();
	}

	public Vector<ActionNode> getAllActionNodes() {
		if (getActionPetriGraph() != null) {
			return getActionPetriGraph().getAllActionNodes();
		}
		return new Vector<ActionNode>();
	}

	public ActionNode getActionNodeNamed(String name) {
		for (ActionNode node : getAllActionNodes()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public Enumeration<ActionNode> getSortedActionNodes() {
		disableObserving();
		ActionNode[] o = FlexoIndexManager.sortArray(getAllActionNodes().toArray(new ActionNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Vector<OperatorNode> getAllOperatorNodes() {
		// TODO: optimize me later !!!
		Vector<OperatorNode> returned = new Vector<OperatorNode>();
		if (getActionPetriGraph() != null) {
			return getActionPetriGraph().getAllOperatorNodes();
		}
		return returned;
	}

	public Vector<EventNode> getAllEventNodes() {
		// TODO: optimize me later !!!
		Vector<EventNode> returned = new Vector<EventNode>();
		if (getActionPetriGraph() != null) {
			return getActionPetriGraph().getAllEventNodes();
		}
		return returned;
	}

	public ActionNode getActionNamed(String flexoNodeName) {
		Enumeration en = getAllActionNodes().elements();
		ActionNode temp = null;
		while (en.hasMoreElements()) {
			temp = (ActionNode) en.nextElement();
			if (temp.getNodeName() != null && temp.getNodeName().equals(flexoNodeName)) {
				return temp;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find Action Node named " + flexoNodeName);
		}
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		if (getFather() != null) {
			return getFather().getFullyQualifiedName() + "." + formattedString(getNodeName());
		} else {
			return "NULL." + formattedString(getNodeName());
		}
	}

	// ==========================================================================
	// =============================== OperationNode
	// ============================
	// ==========================================================================

	public String getWOComponentName() {
		if (getOperationComponent() != null) {
			return getOperationComponent().getComponentName();
		}
		return null;
	}

	public void setWOComponentName(String aComponentName) throws DuplicateResourceException, OperationAssociatedWithComponentSuccessfully {
		if (getWOComponentName() != null && getWOComponentName().equals(aComponentName)) {
			return;
		}
		if (_componentInstance == null && (aComponentName == null || aComponentName.trim().equals(""))) {
			return;
		}
		/*
		 * ComponentDefinition foundComponent = getProject().getFlexoComponentLibrary().getComponentNamed(aComponentName);
		 * OperationComponentDefinition newComponent = null; if (foundComponent instanceof OperationComponentDefinition) { newComponent =
		 * (OperationComponentDefinition) foundComponent; } else if (foundComponent != null) { if (logger.isLoggable(Level.WARNING))
		 * logger.warning("Found a component named " + aComponentName + " but this is not an OperationComponent. Aborting."); throw new
		 * DuplicateResourceException(aComponentName); } if (newComponent == null) { if (logger.isLoggable(Level.INFO))
		 * logger.info("Creating a new Component named:" + aComponentName); FlexoComponentFolder selectedFolder =
		 * getProject().getFlexoComponentLibrary().getRootFolder(); newComponent = new OperationComponentDefinition(aComponentName,
		 * getProject().getFlexoComponentLibrary(), selectedFolder, getProject()); } setOperationComponent(newComponent);
		 */
		logger.warning("This implementation is not correct: you should not use FlexoAction primitive from the model !");
		// TODO: Please implement this better later
		// Used editor will be null
		SetAndOpenOperationComponent action = SetAndOpenOperationComponent.actionType.makeNewAction(this, null);
		action.setNewComponentName(aComponentName);
		action.doAction();
		if (action.getThrownException() != null) {
			if (action.getThrownException() instanceof DuplicateResourceException) {
				throw (DuplicateResourceException) action.getThrownException();
			} else if (action.getThrownException() instanceof OperationAssociatedWithComponentSuccessfully) {
				throw (OperationAssociatedWithComponentSuccessfully) action.getThrownException();
			}
		}
	}

	public OperationComponentDefinition getOperationComponent() {
		if (_componentInstance != null) {
			return _componentInstance.getOperationComponentDefinition();
		}
		return null;
	}

	public void setOperationComponent(OperationComponentDefinition aComponentDefinition)
			throws OperationAssociatedWithComponentSuccessfully {
		if (_componentInstance != null && _componentInstance.getOperationComponentDefinition() == aComponentDefinition) {
			return;
		}
		Enumeration en = getAllActionNodes().elements();
		while (en.hasMoreElements()) {
			ActionNode a = (ActionNode) en.nextElement();
			if (a.getAssociatedButtonWidget() != null) {
				a.delete();
			}
		}
		if (_componentInstance != null && aComponentDefinition == null) {
			removeComponentInstance();
			if (getProcess() != null) {
				getProcess().notifyOperationChange(this);
			}
		}
		if (aComponentDefinition != null) {
			setComponentInstance(new OperationComponentInstance(aComponentDefinition, this));
		}
	}

	public OperationComponentInstance getComponentInstance() {
		return _componentInstance;
	}

	public void setComponentInstance(OperationComponentInstance componentInstance) throws OperationAssociatedWithComponentSuccessfully {
		componentInstance.setOperationNode(this);
		OperationComponentInstance old = _componentInstance;
		if (_componentInstance != null) {
			removeComponentInstance();
		}
		if (componentInstance.getComponentDefinition() != null || isCreatedByCloning() && componentInstance.getComponentName() != null) {
			// If we are created by cloning, we won't have a project and therefore, the component definition has not been resolved yet
			_componentInstance = componentInstance;
			_componentInstance.setOperationNode(this);

			updateProcessBusinessDataAccessingMethod();

			setChanged();
			if (isCreatedByCloning()) {
				return;
			}
			OperationComponentHasBeenSet dm = new OperationComponentHasBeenSet("operationComponent",
					componentInstance.getComponentDefinition(), this);
			notifyObservers(dm);
			notifyBindingsChanged();
			if (getProcess() != null) {
				getProcess().notifyOperationChange(this);
			}
			if (!isDeserializing() && !isCreatedByCloning()) {
				throw new OperationAssociatedWithComponentSuccessfully(dm, this, getOperationComponent(), old);
			}
		} else if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("ComponentInstance does not have a component definition for component named "
					+ componentInstance.getComponentName());
		}
	}

	public void removeComponentInstance() {
		if (_tabComponentInstance != null) {
			removeTabComponentInstance();
		}
		if (_componentInstance != null) {
			if (getComponentDefinition() != null) {
				// Remove generated method for accessing process business data on component
				boolean needRemove = true;
				for (OperationNode opNode : _componentInstance.getComponentDefinition().getAllOperationNodesLinkedToThisComponent()) {
					if (opNode != this && opNode.getProcess() == getProcess()) {
						needRemove = false;
						break;
					}
				}

				if (needRemove) {
					_componentInstance.getComponentDefinition().getComponentDMEntity().removeAccessingBusinessDataMethod(getProcess());
				}

				// Remove generated method for accessing process business data on all tabs used in component
				for (ComponentDefinition compDef : getWOComponent().getAllTabComponents(new Vector<TabComponentDefinition>())) {
					needRemove = true;
					for (OperationNode opNode : compDef.getAllOperationNodesLinkedToThisComponent()) {
						if (opNode != this && opNode.getProcess() == getProcess()) {
							needRemove = false;
							break;
						}
					}
					if (needRemove) {
						compDef.getComponentDMEntity().removeAccessingBusinessDataMethod(getProcess());
					}
				}
			}

			_componentInstance.delete();
		}
		ComponentInstance oldComponentInstance = _componentInstance;
		_componentInstance = null;
		setChanged();
		notifyObservers(new OperationComponentHasBeenRemoved("WOComponentName",
				oldComponentInstance != null ? oldComponentInstance.getComponentDefinition() : null, this));
		notifyBindingsChanged();
	}

	public void updateProcessBusinessDataAccessingMethod() {
		if (getOperationComponent() != null && getProcess().getFlexoID() > 0) {
			// Generate method for accessing process business data on component
			getOperationComponent().getComponentDMEntity().addOrUpdateAccessingBusinessDataMethod(getProcess());

			// Generate method for accessing process business data on all tabs used in component
			for (ComponentDefinition compDef : getAvailableTabs()) {
				compDef.getComponentDMEntity().addOrUpdateAccessingBusinessDataMethod(getProcess());
			}
		}
	}

	public Vector<TabComponentDefinition> getAvailableTabs() {
		if (getOperationComponent() == null || !getOperationComponent().getHasTabContainer()) {
			return EmptyVector.EMPTY_VECTOR(TabComponentDefinition.class);
		}
		Vector<TabComponentDefinition> v = new Vector<TabComponentDefinition>();
		for (IETabWidget tbc : getOperationComponent().getWOComponent().getAllTabWidget()) {
			if (tbc.isShownInOperation(this)) {
				v.add(tbc.getTabComponentDefinition());
			}
		}
		return v;

	}

	// TABS
	public String getTabComponentName() {
		if (getTabComponent() != null) {
			return getTabComponent().getComponentName();
		}
		return null;
	}

	public void setTabComponentName(String aComponentName) throws DuplicateResourceException, OperationAssociatedWithComponentSuccessfully {
		if (getTabComponentName() != null && getTabComponentName().equals(aComponentName)) {
			return;
		}
		if (_tabComponentInstance == null && (aComponentName == null || aComponentName.trim().equals(""))) {
			return;
		}
		ComponentDefinition foundComponent = getProject().getFlexoComponentLibrary().getComponentNamed(aComponentName);
		TabComponentDefinition newComponent = null;
		if (foundComponent instanceof TabComponentDefinition) {
			newComponent = (TabComponentDefinition) foundComponent;
		} else if (foundComponent != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Found a component named " + aComponentName + " but this is not a TabComponent. Aborting.");
			}
			throw new DuplicateResourceException(aComponentName);
		}
		if (newComponent == null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Creating a new Component named:" + aComponentName);
			}
			FlexoComponentFolder selectedFolder = getProject().getFlexoComponentLibrary().getRootFolder()
					.getFolderTyped(FolderType.TAB_FOLDER);
			newComponent = new TabComponentDefinition(aComponentName, getProject().getFlexoComponentLibrary(), selectedFolder, getProject());
		}
		setTabComponent(newComponent);
	}

	public TabComponentDefinition getTabComponent() {
		if (_tabComponentInstance != null) {
			return _tabComponentInstance.getComponentDefinition();
		}
		return null;
	}

	public void setTabComponent(TabComponentDefinition aComponentDefinition) {
		if (_tabComponentInstance != null && _tabComponentInstance.getComponentDefinition() == aComponentDefinition) {
			return;
		}
		if (_tabComponentInstance != null && aComponentDefinition == null) {
			removeTabComponentInstance();
		}
		if (aComponentDefinition != null) {
			setTabOperationComponentInstance(new TabComponentInstance(aComponentDefinition, this));
		}
	}

	public TabComponentInstance getTabOperationComponentInstance() {
		return _tabComponentInstance;
	}

	public void setTabOperationComponentInstance(TabComponentInstance tabComponentInstance) {
		if (_tabComponentInstance != null) {
			removeTabComponentInstance();
		}
		tabComponentInstance.setOperationNode(this);
		if (tabComponentInstance.getComponentDefinition() != null || isCreatedByCloning()) {
			_tabComponentInstance = tabComponentInstance;
			setChanged();
			TabOperationComponentHasBeenSet dm = new TabOperationComponentHasBeenSet("WOComponentName",
					tabComponentInstance.getComponentDefinition(), this);
			notifyObservers(dm);
		} else if (logger.isLoggable(Level.SEVERE)) {
			logger.severe("TabComponentInstance does not have a component definition for component named "
					+ tabComponentInstance.getComponentName());
		}
	}

	public void removeTabComponentInstance() {
		if (_tabComponentInstance != null) {
			_tabComponentInstance.delete();
		}
		ComponentInstance oldComponentInstance = _tabComponentInstance;
		_tabComponentInstance = null;
		setChanged();
		notifyObservers(new TabOperationComponentHasBeenRemoved("WOComponentName",
				oldComponentInstance != null ? oldComponentInstance.getComponentDefinition() : null, this));
	}

	@Override
	public Vector<MetricsValue> getMetricsValues() {
		return metricsValues;
	}

	public void setMetricsValues(Vector<MetricsValue> metricsValues) {
		this.metricsValues = metricsValues;
		setChanged();
	}

	@Override
	public void addToMetricsValues(MetricsValue value) {
		if (value.getMetricsDefinition() != null) {
			metricsValues.add(value);
			value.setOwner(this);
			setChanged();
			notifyObservers(new MetricsValueAdded(value, "metricsValues"));
		}
	}

	@Override
	public void removeFromMetricsValues(MetricsValue value) {
		metricsValues.remove(value);
		value.setOwner(null);
		setChanged();
		notifyObservers(new MetricsValueRemoved(value, "metricsValues"));
	}

	@Override
	public void updateMetricsValues() {
		getWorkflow().updateMetricsForOperation(this);
	}

	public void notifyBindingsChanged() {
		setChanged();
		notifyObservers(new WKFAttributeDataModification("componentInstance.bindings", null, null));
	}

	public void addMetrics() {
		if (addMetricsActionizer != null) {
			addMetricsActionizer.run(this, null);
		}
	}

	public void deleteMetrics(MetricsValue value) {
		if (deleteMetricsActionizer != null) {
			deleteMetricsActionizer.run(value, null);
		}
	}

	public boolean hasWOComponent() {
		if (_componentInstance != null && _componentInstance.getComponentDefinition() == null) {
			logger.severe("_componentInstance != null && _componentInstance.getComponentDefinition()==null in operation: " + getName());
		}
		return _componentInstance != null && _componentInstance.getComponentDefinition() != null;
	}

	public IEWOComponent getWOComponent() {
		return _componentInstance.getComponentDefinition().getWOComponent();
	}

	public OperationComponentDefinition getComponentDefinition() {
		if (_componentInstance == null) {
			return null;
		}
		return (OperationComponentDefinition) _componentInstance.getComponentDefinition();
	}

	public String getAcronym() {
		return _acronym;
	}

	public void setAcronym(String acronym) {
		String old = _acronym;
		_acronym = acronym;
		setChanged();
		notifyObservers(new WKFAttributeDataModification("acronym", old, acronym));
	}

	public static ActionNode createNewActionNodeForButton(IEHyperlinkWidget w, OperationNode node) {
		if (!w.getIsFlexoAction() && !w.getIsDisplayAction()) {
			return null;
		}
		if (node.getActionPetriGraph() == null) {
			new ActionPetriGraph(node);
		}
		ActionNode newNode = new ActionNode(node.getProcess());
		if (w.getIsFlexoAction()) {
			newNode.setActionType(ActionType.FLEXO_ACTION);
		} else {
			newNode.setActionType(ActionType.DISPLAY_ACTION);
		}
		newNode.setName(w.getNameOrCalculatedLabel());
		newNode.setAssociatedButtonWidget(w);
		node.getActionPetriGraph().addToNodes(newNode);
		return newNode;
	}

	public static void linkActionToBeginAndEndNode(ActionNode node) {
		linkActionToBeginNode(node);
		linkActionToEndNode(node);
	}

	public static void linkActionToBeginNode(ActionNode node) {
		if (node == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Tried to create a link with a null node");
			}
			return;
		}
		ActionNode begin = null;
		try {
			begin = (ActionNode) node.getOperationNode().getActionPetriGraph().getAllBeginNodes().firstElement();
		} catch (NoSuchElementException e1) {
			// The vector is empty
			begin = null;
		}
		if (begin != null) {
			try {
				/*
				 * FlexoPreCondition pc = null; if (node.getPreConditions().size() == 0) { pc = new FlexoPreCondition(node); } else pc =
				 * node.getPreConditions().firstElement();
				 */
				new TokenEdge(begin, node);
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Created link from begin to node");
				}
			} catch (InvalidEdgeException e) {
				e.printStackTrace();
			}
		} else if (logger.isLoggable(Level.INFO)) {
			logger.info("No begin node found");
		}
	}

	public static void linkActionToEndNode(ActionNode node) {
		if (node == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Tried to create a link with a null node");
			}
			return;
		}
		if (node.getActionType() == ActionType.DISPLAY_ACTION) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Tried to link a display action to begin and end nodes");
			}
			return;
		}
		ActionNode end = null;
		try {
			end = (ActionNode) node.getOperationNode().getActionPetriGraph().getAllEndNodes().firstElement();
		} catch (NoSuchElementException e1) {
			// The vector is empty
			end = null;
		}

		if (end != null) {
			try {
				FlexoPreCondition pc = null;
				if (end.getPreConditions().size() == 0) {
					pc = new FlexoPreCondition(end);
				} else {
					pc = end.getPreConditions().firstElement();
				}
				new TokenEdge(node, pc);
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("Created link from node to end");
				}
			} catch (InvalidEdgeException e) {
				e.printStackTrace();
			}
		} else if (logger.isLoggable(Level.INFO)) {
			logger.info("No end node found");
		}
	}

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class OperationMustHaveATab extends ValidationRule<OperationMustHaveATab, OperationNode> {
		public OperationMustHaveATab() {
			super(OperationNode.class, "operation_node_must_define_a_tab");
		}

		@Override
		public ValidationIssue<OperationMustHaveATab, OperationNode> applyValidation(OperationNode node) {
			if (node.isAccessible() && !(node instanceof SelfExecutableOperationNode) && node.getNodeType() == NodeType.NORMAL
					&& node.getComponentInstance() != null) {
				if (node.getComponentDefinition().getWOComponent().hasTabContainer()
						&& node.getOperationComponent().getWOComponent().hasAtLeastOneTabDefined() && node.getSelectedTabKey() == null) {
					return new ValidationError<OperationMustHaveATab, OperationNode>(this, node,
							"operation_($object.name)_must_define_a_tab");
				}
			}
			return null;
		}
	}

	public static class OperationMustHaveAWOComponent extends ValidationRule<OperationMustHaveAWOComponent, OperationNode> {
		public OperationMustHaveAWOComponent() {
			super(OperationNode.class, "operation_node_must_define_a_wo_component");
		}

		@Override
		public ValidationIssue<OperationMustHaveAWOComponent, OperationNode> applyValidation(OperationNode node) {
			if (!(node instanceof SelfExecutableOperationNode) && node.getNodeType() == NodeType.NORMAL
					&& node.getComponentInstance() == null) {
				ValidationError<OperationMustHaveAWOComponent, OperationNode> error = new ValidationError<OperationMustHaveAWOComponent, OperationNode>(
						this, node, "operation_($object.name)_defines_no_wo_component");
				error.addToFixProposals(new CreateAndAssignNewWOComponent());
				error.addToFixProposals(new DeletionFixProposal<OperationMustHaveAWOComponent, OperationNode>("delete_this_operation"));
				return error;
			}
			return null;
		}

		public class CreateAndAssignNewWOComponent extends ParameteredFixProposal<OperationMustHaveAWOComponent, OperationNode> {
			public CreateAndAssignNewWOComponent() {
				super("create_and_assign_new_wo_component_to_operation_($object.name)", "newWOName",
						"enter_a_name_for_the_new_wo_component", "");
			}

			@Override
			protected void fixAction() {
				String newWOName = (String) getValueForParameter("newWOName");
				String newName = newWOName;
				boolean isOK = false;
				int tryNb = 0;
				if (newName != null) {
					while (!isOK) {
						try {
							getObject().setWOComponentName(newName);
							isOK = true;
						} catch (DuplicateResourceException e) {
							tryNb++;
							newName = newWOName + "-" + tryNb;
						} catch (OperationAssociatedWithComponentSuccessfully e) {

						}
					}
				}
			}
		}
	}

	public static class OperationComponentActionsMustBeBoundToAnActionNode extends
			ValidationRule<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode> {

		public OperationComponentActionsMustBeBoundToAnActionNode() {
			super(OperationNode.class, "flexo_action_button_of_operation_component_must_have_a_corresponding_action_node");
		}

		@Override
		public ValidationIssue<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode> applyValidation(OperationNode node) {
			ValidationError<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode> err = null;
			if (node.getComponentInstance() == null) {
				return null;// If there are no component, then another rule will
			}
			// take care of this.
			Enumeration en = node.getOperationComponent().getWOComponent().getAvailableFlexoActions().elements();
			Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
			while (en.hasMoreElements()) {
				IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
				if (w.getIsMandatoryFlexoAction()
						&& (w.getHyperlinkType() == null || w.getHyperlinkType() != HyperlinkType.SEARCH
								&& w.getHyperlinkType() != HyperlinkType.CANCEL && w.getHyperlinkType() != HyperlinkType.MAILTO
								&& w.getHyperlinkType() != HyperlinkType.URL)) {
					v.add(w);
				}
			}

			en = node.getOperationComponent().getWOComponent().getAvailableDisplayActions().elements();
			while (en.hasMoreElements()) {
				IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
				if (w.getIsMandatoryFlexoAction()
						&& (w.getHyperlinkType() == null || w.getHyperlinkType() != HyperlinkType.SEARCH
								&& w.getHyperlinkType() != HyperlinkType.MAILTO && w.getHyperlinkType() != HyperlinkType.URL)) {
					v.add(w);
				}
			}

			en = node.getAllActionNodes().elements();
			while (en.hasMoreElements()) {
				ActionNode a = (ActionNode) en.nextElement();
				if (a.getAssociatedButtonWidget() != null) {
					v.remove(a.getAssociatedButtonWidget());
				}
			}
			if (v.size() > 0) {
				err = new ValidationError<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode>(this, node,
						"operation_must_bound_action_nodes_to_flexo_action_defined_in_the_associated_component_($object.name)");
				err.addToFixProposals(new RemoveMandatoryAttributeFromWidget());
				err.addToFixProposals(new AutomaticallyCreateActionNodes());
			}
			return err;
		}

		public class RemoveMandatoryAttributeFromWidget extends
				FixProposal<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode> {

			public RemoveMandatoryAttributeFromWidget() {
				super("modify_button_such_that_it_do_not_requires_a_flexo_action");
			}

			@Override
			protected void fixAction() {
				OperationNode node = getObject();
				if (node.getComponentInstance() == null) {
					return;// If there are no component, there are no errors;
				}
				Enumeration en = node.getOperationComponent().getWOComponent().getAllButtonInterface().elements();
				Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
				while (en.hasMoreElements()) {
					IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
					if (w.getIsFlexoAction() || w.getIsDisplayAction()) {
						v.add(w);
					}
				}

				Vector<ActionNode> actionNodesToRemove = new Vector<ActionNode>();
				en = node.getAllActionNodes().elements();
				while (en.hasMoreElements()) {
					ActionNode a = (ActionNode) en.nextElement();
					if (a.getAssociatedButtonWidget() != null) {
						v.remove(a.getAssociatedButtonWidget());
					} else {
						actionNodesToRemove.add(a);
					}
				}
				en = actionNodesToRemove.elements();
				while (en.hasMoreElements()) {
					ActionNode a = (ActionNode) en.nextElement();
					if (!a.isBeginNode() && !a.isEndNode()) {
						a.delete();
					}
				}
				en = v.elements();
				while (en.hasMoreElements()) {
					IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
					if (w.getIsMandatoryFlexoAction()) {
						w.setIsMandatoryFlexoAction(false);
					}
				}
			}

		}

		public class AutomaticallyCreateActionNodes extends FixProposal<OperationComponentActionsMustBeBoundToAnActionNode, OperationNode> {

			public AutomaticallyCreateActionNodes() {
				super("automatically_create_and_bound_new_action_nodes_(this_will_delete_other_action_nodes");
			}

			@Override
			protected void fixAction() {
				OperationNode node = getObject();
				if (node.getComponentInstance() == null) {
					return;// If there are no component, there are no errors;
				}
				Enumeration en = node.getOperationComponent().getWOComponent().getAllButtonInterface().elements();
				Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
				while (en.hasMoreElements()) {
					IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
					if (w.getIsFlexoAction() || w.getIsDisplayAction()) {
						v.add(w);
					}
				}

				Vector<ActionNode> actionNodesToRemove = new Vector<ActionNode>();
				en = node.getAllActionNodes().elements();
				while (en.hasMoreElements()) {
					ActionNode a = (ActionNode) en.nextElement();
					if (a.getAssociatedButtonWidget() != null) {
						v.remove(a.getAssociatedButtonWidget());
					} else {
						actionNodesToRemove.add(a);
					}
				}
				en = actionNodesToRemove.elements();
				while (en.hasMoreElements()) {
					ActionNode a = (ActionNode) en.nextElement();
					if (!a.isBeginNode() && !a.isEndNode()) {
						a.delete();
					}
				}
				en = v.elements();
				while (en.hasMoreElements()) {
					IEHyperlinkWidget w = (IEHyperlinkWidget) en.nextElement();
					ActionNode newAction = createNewActionNodeForButton(w, node);
					if (newAction != null) {
						linkActionToBeginAndEndNode(newAction);
					}
				}
			}

		}
	}

	protected boolean shouldBeSynchronized() {
		for (Enumeration en = getAllActionNodes().elements(); en.hasMoreElements();) {
			ActionNode nextAction = (ActionNode) en.nextElement();
			for (Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> en2 = nextAction.getOutgoingPostConditions().elements(); en2
					.hasMoreElements();) {
				FlexoPostCondition<?, ?> nextEdge = en2.nextElement();
				if (nextEdge instanceof TokenEdge) {
					TokenEdge edge = (TokenEdge) nextEdge;
					if (edge.getNextNode() != null && edge.getNextNode() instanceof ActionNode
							&& ((ActionNode) edge.getNextNode()).isEndNode()
							&& ((ActionNode) edge.getNextNode()).getOperationNode() == this) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getTypeName() {
		return "OPERATION";
	}

	/**
	 * An OperationNode containing at least one ActionNode linked to an END node should be synchronized.
	 * 
	 * @author sguerin
	 * @version $Id: OperationNode.java,v 1.9.2.19 2006/01/06 21:35:06 sguerin Exp $
	 */
	public static class OperationShouldBeSynchronized extends ValidationRule<OperationShouldBeSynchronized, OperationNode> {
		public OperationShouldBeSynchronized() {
			super(OperationNode.class, "operation_node_should_be_synchronized");
		}

		@Override
		public ValidationIssue<OperationShouldBeSynchronized, OperationNode> applyValidation(OperationNode node) {
			if (node.shouldBeSynchronized() && !node.getIsSynchronized()) {
				ValidationWarning<OperationShouldBeSynchronized, OperationNode> warning = new ValidationWarning<OperationShouldBeSynchronized, OperationNode>(
						this, node, "operation_($object.name)_should_be_synchronized");
				warning.addToFixProposals(new SynchronizeOperation());
				return warning;
			}
			return null;
		}

		public class SynchronizeOperation extends FixProposal<OperationShouldBeSynchronized, OperationNode> {
			public SynchronizeOperation() {
				super("set_operation_($object.name)_as_synchronized");
			}

			@Override
			protected void fixAction() {
				getObject().setIsSynchronized(true);
			}
		}
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "operation_node";
	}

	/**
	 * Overrides setNodeName
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractNode#setNodeName(java.lang.String)
	 */
	@Override
	public void setNodeName(String aName) {
		super.setNodeName(aName);
		if (getProcess() != null) {
			getProcess().notifyOperationChange(this);
		}
	}

	protected Color componentTextColor = FlexoColor.GRAY_COLOR;

	public Color getComponentTextColor() {
		return componentTextColor;
	}

	public void setComponentTextColor(Color ctc) {
		Color old = componentTextColor;
		this.componentTextColor = ctc;
		setChanged();
		notifyObservers(new ComponentTextColorChanged(old, componentTextColor));
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public FlexoCSS getCalculatedCssSheet() {
		if (cssSheet == null) {
			if (getAbstractActivityNode() != null) {
				return getAbstractActivityNode().getCalculatedCssSheet();
			} else {
				return getProject().getCssSheet();
			}
		} else {
			return cssSheet;
		}
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public FlexoCSS getCssSheet() {
		return cssSheet;
	}

	/**
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public void setCssSheet(FlexoCSS sheet) {
		FlexoCSS old = this.cssSheet;
		this.cssSheet = sheet;
		setChanged();
		notifyAttributeModification("cssSheet", old, cssSheet);
	}

	public static class MandatoryBindingsMustHaveAValue extends ValidationRule<MandatoryBindingsMustHaveAValue, OperationNode> {

		public MandatoryBindingsMustHaveAValue() {
			super(OperationNode.class, "mandatory_bindings_must_have_a_value");
		}

		@Override
		public ValidationIssue<MandatoryBindingsMustHaveAValue, OperationNode> applyValidation(OperationNode node) {
			if (node.getComponentInstance() == null) {
				return null;
			}
			Enumeration<ComponentBindingDefinition> en = node.getComponentInstance().getComponentDefinition().getBindingDefinitions()
					.elements();
			while (en.hasMoreElements()) {
				ComponentBindingDefinition bd = en.nextElement();
				if (!bd.getIsMandatory()) {
					continue;
				}
				ComponentInstanceBinding cib = node.getComponentInstance().getBinding(bd);
				if (cib.getBindingValue() == null) {
					return new ValidationError<MandatoryBindingsMustHaveAValue, OperationNode>(this, node,
							"some_bindings_have_no_value_in_operation_($object.name)");
				}
			}
			return null;
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType != CodeType.PROTOTYPE;
		}
	}

	public static class MandatoryBooleanBindingsMustHaveAValue extends
			ValidationRule<MandatoryBooleanBindingsMustHaveAValue, OperationNode> {

		public MandatoryBooleanBindingsMustHaveAValue() {
			super(OperationNode.class, "mandatory_boolean_bindings_must_have_a_value");
		}

		@Override
		public ValidationIssue<MandatoryBooleanBindingsMustHaveAValue, OperationNode> applyValidation(OperationNode node) {
			if (node.getComponentInstance() == null) {
				return null;
			}
			Enumeration<ComponentBindingDefinition> en = node.getComponentInstance().getComponentDefinition().getBindingDefinitions()
					.elements();
			while (en.hasMoreElements()) {
				ComponentBindingDefinition bd = en.nextElement();
				if (bd.getType() == null) {
					return new ValidationError<MandatoryBooleanBindingsMustHaveAValue, OperationNode>(this, node,
							"component_bindings_for_operation_($object.name)_have_no_type");
				}
				if (!bd.getIsMandatory() || !bd.getType().isBoolean() && !bd.getType().isBooleanPrimitive()) {
					continue;
				}
				ComponentInstanceBinding cib = node.getComponentInstance().getBinding(bd);
				if (cib == null || cib.getBindingValue() == null) {
					return new ValidationError<MandatoryBooleanBindingsMustHaveAValue, OperationNode>(this, node,
							"some_boolean_bindings_have_no_value_in_operation_($object.name)");
				}
			}
			return null;
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType == CodeType.PROTOTYPE;
		}
	}

	public ComponentBindingDefinition getBusinessDataBinding() {
		if (getComponentInstance() == null || getProcess().getBusinessDataType() == null) {
			return null;
		}
		for (ComponentInstanceBinding cib : getComponentInstance().getBindings()) {
			if (cib.getBindingDefinition().getType().getBaseEntity() == getProcess().getBusinessDataType()) {
				return cib.getBindingDefinition();
			}
		}
		return null;
	}

	public static class OperationMustDefineABindingOfBusinessDataType extends
			ValidationRule<OperationMustDefineABindingOfBusinessDataType, OperationNode> {

		public OperationMustDefineABindingOfBusinessDataType() {
			super(OperationNode.class, "operation_must_define_a_binding_of_business_data_type");
		}

		@Override
		public ValidationIssue<OperationMustDefineABindingOfBusinessDataType, OperationNode> applyValidation(OperationNode node) {
			if (node.getComponentInstance() == null || node.getProcess().getBusinessDataType() == null) {
				return null;
			}
			for (ComponentInstanceBinding cib : node.getComponentInstance().getBindings()) {
				if (cib.getBindingDefinition().getType() != null
						&& cib.getBindingDefinition().getType().getBaseEntity() == node.getProcess().getBusinessDataType()) {
					return null;
				}
			}
			return new ValidationError<OperationMustDefineABindingOfBusinessDataType, OperationNode>(this, node,
					"operation_must_define_a_binding_of_business_data_type");
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType != CodeType.PROTOTYPE;
		}
	}

	public ActionNode getActionNodeForButton(IEHyperlinkWidget button) {
		Enumeration en = getAllActionNodes().elements();
		while (en.hasMoreElements()) {
			ActionNode action = (ActionNode) en.nextElement();
			if (action.getAssociatedButtonWidget() == button
					&& (action.getActionType() == ActionType.DISPLAY_ACTION || action.getActionType() == ActionType.FLEXO_ACTION)) {
				return action;
			}
		}
		return null;
	}

	public String getSelectedTabKey() {
		if (getComponentDefinition().getWOComponent().getTabWidgetForTabComponent(getTabComponent()) != null) {
			return getComponentDefinition().getWOComponent().getTabWidgetForTabComponent(getTabComponent()).getTabKeyForGenerator();
		}
		/*
		 * if (getTabOperationComponentInstance() != null) { Enumeration en = ((IEPageComponent)
		 * getOperationComponent().getWOComponent()).topComponents(); while (en.hasMoreElements()) { IETopComponent tcc = (IETopComponent)
		 * en.nextElement(); if (tcc instanceof IESequenceTab) { IESequenceTab w = (IESequenceTab) tcc; Enumeration en1 =
		 * w.getAllTabs().elements(); while (en1.hasMoreElements()) { IETabWidget tab = (IETabWidget) en1.nextElement(); if
		 * (tab.getTabComponentDefinition().equals( getTabOperationComponentInstance().getComponentDefinition())) { if
		 * (logger.isLoggable(Level.INFO))
		 * logger.info("Selected tab for operation "+getName()+" is "+tab.getTabComponentDefinition().getName()); return
		 * tab.getTabKeyForGenerator(); } } } } if (logger.isLoggable(Level.WARNING))
		 * logger.warning("Selected tab for operation "+getName()+" could not be found. Expected: "+getTabComponentName()); }
		 */
		return null;
	}

	private Date _lastUpdate;

	/**
	 * This date is use to perform fine tuning resource dependancies computing
	 * 
	 * @return
	 */
	@Override
	public Date getLastUpdate() {
		if (_lastUpdate == null) {
			_lastUpdate = lastMemoryUpdate();
		}
		if (_lastUpdate == null) {
			_lastUpdate = super.getLastUpdate();
		}
		return _lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		_lastUpdate = lastUpdate;
	}

	@Override
	public void setIsModified() {
		if (ignoreNotifications()) {
			return;
		}
		_lastUpdate = null;
		super.setIsModified();
		// _lastUpdate = lastMemoryUpdate();
		// Do this to reset dependant resources cache, in order to get up-to-date
		// needsGeneration information on generated resources
		if (getXMLResourceData() != null && getXMLResourceData().getFlexoResource() != null) {
			getXMLResourceData().getFlexoResource().notifyResourceStatusChanged();
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Operation node " + getFullyQualifiedName() + " is set to be modified, date=" + _lastUpdate);
		}
	}

	public String getStaticDirectActionUrl() {
		if (getComponentInstance() == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(getProject().getPrefix()).append("DA/open").append(getComponentInstance().getComponentName());
		sb.append("?").append(ToolBox.serializeHashtable(getComponentInstance().getStaticBindingValues()));
		if (getComponentInstance().getComponentDefinition().getHasTabContainer()) {
			sb.append("&tab=");
			if (getTabOperationComponentInstance() != null) {
				sb.append(getComponentInstance().getComponentDefinition().getWOComponent().getTabWidgetForTabComponent(getTabComponent())
						.getTabKeyForGenerator());
			}
		}
		return sb.toString();
	}

	public boolean isUnderSubProcessNode() {
		return getFather() instanceof SubProcessNode;
	}

	public FlexoProcess getRelatedProcess() {
		if (isUnderSubProcessNode()) {
			return ((SubProcessNode) getFather()).getSubProcess();
		} else {
			return getProcess();
		}
	}

	/**
	 * Overrides compareTo
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(OperationNode o) {
		return this.getFullyQualifiedName().compareTo(o.getFullyQualifiedName());
	}

	public OperationStatistics getStatistics() {
		if (statistics == null) {
			statistics = new OperationStatistics(this);
		}
		return statistics;
	}

	@Override
	public ApplicationHelpEntryPoint getParentHelpEntry() {
		return getAbstractActivityNode() instanceof ApplicationHelpEntryPoint ? (ApplicationHelpEntryPoint) getAbstractActivityNode()
				: null;
	}

	@Override
	public List<ApplicationHelpEntryPoint> getChildsHelpObjects() {
		Vector<ApplicationHelpEntryPoint> reply = new Vector<ApplicationHelpEntryPoint>();
		return reply;
	}

	@Override
	public String getShortHelpLabel() {
		return getName();
	}

	@Override
	public String getTypedHelpLabel() {
		return "Operation : " + getName();
	}

}
