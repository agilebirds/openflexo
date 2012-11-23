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
package org.openflexo.wkf.controller.action;

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.ExecutionContext;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.NodeParameter;
import org.openflexo.foundation.param.NodeParameter.NodeSelectingConditional;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.action.CreateExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.CreateNode;
import org.openflexo.foundation.wkf.action.CreatePetriGraph;
import org.openflexo.foundation.wkf.action.CreatePreCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CreatePreconditionInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreatePreconditionInitializer(WKFControllerActionInitializer actionInitializer) {
		super(CreatePreCondition.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	private class CreatePreConditionExecutionContext extends ExecutionContext {
		// Actions that may be embbeded in initializer
		protected CreatePetriGraph createPg = null;
		protected CreateExecutionPetriGraph createExecutionPetriGraph;

		protected CreateNode createBeginNodeAction = null;

		public CreatePreConditionExecutionContext(CreatePreCondition action) {
			super(action);
		}
	}

	@Override
	protected FlexoActionInitializer<CreatePreCondition> getDefaultInitializer() {
		return new FlexoActionInitializer<CreatePreCondition>() {
			@Override
			public boolean run(EventObject e, final CreatePreCondition action) {
				CreatePreConditionExecutionContext executionContext = new CreatePreConditionExecutionContext(action);
				action.setExecutionContext(executionContext);

				if ((action.getFocusedObject() instanceof FatherNode || action.getFocusedObject() instanceof SelfExecutableNode)
						&& !(action.getFocusedObject() instanceof OperationNode && action.getFocusedObject().isBeginNode())
						&& !(action.getFocusedObject() instanceof OperationNode && action.getFocusedObject().isEndNode())) {

					FlexoPetriGraph pg = null;
					if (action.getFocusedObject() instanceof SelfExecutableNode) {
						pg = ((SelfExecutableNode) action.getFocusedObject()).getExecutionPetriGraph();
					} else if (action.getFocusedObject() instanceof FatherNode) {
						pg = ((FatherNode) action.getFocusedObject()).getContainedPetriGraph();
					}

					if (pg == null) {
						if (action.getFocusedObject() instanceof SelfExecutableNode) {
							executionContext.createExecutionPetriGraph = CreateExecutionPetriGraph.actionType.makeNewEmbeddedAction(
									action.getFocusedObject(), null, action);
							executionContext.createExecutionPetriGraph.doAction();
							pg = executionContext.createExecutionPetriGraph.getNewPetriGraph();
							if (!executionContext.createExecutionPetriGraph.hasActionExecutionSucceeded()) {
								return false;
							}
							pg = ((SelfExecutableNode) action.getFocusedObject()).getExecutionPetriGraph();
						} else if (action.getFocusedObject() instanceof FatherNode) {
							pg = ((FatherNode) action.getFocusedObject()).getContainedPetriGraph();
							executionContext.createPg = CreatePetriGraph.actionType.makeNewEmbeddedAction(
									(FatherNode) action.getFocusedObject(), null, action);
							executionContext.createPg.doAction();
							pg = executionContext.createPg.getNewPetriGraph();
							if (!executionContext.createPg.hasActionExecutionSucceeded()) {
								return false;
							}
						}
					}
					if (action.getAttachedBeginNode() == null && pg != null) {
						Vector<FlexoNode> unboundBeginNodes = pg.getUnboundBeginNodes();
						Vector<FlexoNode> alreadyBoundBeginNodes = pg.getBoundBeginNodes();

						FlexoNode firstUnboundBeginNode = unboundBeginNodes.size() > 0 ? unboundBeginNodes.firstElement() : null;
						FlexoNode firstBoundBeginNode = alreadyBoundBeginNodes.size() > 0 ? alreadyBoundBeginNodes.firstElement() : null;

						boolean hasUnboundBeginNodes = unboundBeginNodes.size() > 0;
						boolean hasAlreadyBoundBeginNodes = alreadyBoundBeginNodes.size() > 0;
						;
						if (unboundBeginNodes.size() != 1 || hasAlreadyBoundBeginNodes) {
							String CREATE_NEW_BEGIN_NODE = FlexoLocalization.localizedForKey("create_new_begin_node");
							String CHOOSE_EXISTING_UNBOUND_BEGIN_NODE = FlexoLocalization
									.localizedForKey("choose_existing_and_unbound_begin_node");
							String CHOOSE_EXISTING_ALREADY_BOUND_BEGIN_NODE = FlexoLocalization
									.localizedForKey("choose_existing_and_already_bound_begin_node");
							Vector<String> availableChoices = new Vector<String>();
							availableChoices.add(CREATE_NEW_BEGIN_NODE);
							if (hasUnboundBeginNodes) {
								availableChoices.add(CHOOSE_EXISTING_UNBOUND_BEGIN_NODE);
							}
							if (hasAlreadyBoundBeginNodes && action.allowsToSelectPreconditionOnly()) {
								availableChoices.add(CHOOSE_EXISTING_ALREADY_BOUND_BEGIN_NODE);
							}

							String[] choices = availableChoices.toArray(new String[availableChoices.size()]);
							RadioButtonListParameter<String> choiceParam = new RadioButtonListParameter<String>("choice",
									"choose_an_option",
									firstUnboundBeginNode != null ? CHOOSE_EXISTING_UNBOUND_BEGIN_NODE : firstBoundBeginNode != null
											&& action.allowsToSelectPreconditionOnly() ? CHOOSE_EXISTING_ALREADY_BOUND_BEGIN_NODE
											: CREATE_NEW_BEGIN_NODE, choices);
							String nodeNameProposal;
							if (action.getFocusedObject() instanceof AbstractActivityNode) {
								nodeNameProposal = pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("begin_node"),
										(AbstractActivityNode) action.getFocusedObject());
							} else if (action.getFocusedObject() instanceof OperationNode) {
								nodeNameProposal = pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("begin_node"),
										(OperationNode) action.getFocusedObject());
							} else {
								if (logger.isLoggable(Level.WARNING)) {
									logger.warning("Unknown father node type: " + action.getFocusedObject().getClassNameKey());
								}
								nodeNameProposal = FlexoLocalization.localizedForKey("begin_node");
							}
							TextFieldParameter newBeginNodeNameParam = new TextFieldParameter("newBeginNodeName", "new_begin_node_name",
									nodeNameProposal);
							newBeginNodeNameParam.setDepends("choice");
							newBeginNodeNameParam.setConditional("choice=" + '"' + CREATE_NEW_BEGIN_NODE + '"');
							NodeParameter unboundNodeParameter = new NodeParameter("unboundNode", "used_begin_node", firstUnboundBeginNode);
							unboundNodeParameter.setRootObject(action.getFocusedObject());
							unboundNodeParameter.setNodeSelectingConditional(new NodeSelectingConditional() {
								@Override
								public boolean isSelectable(AbstractNode node) {
									return node instanceof FlexoNode && ((FlexoNode) node).isBeginNode()
											&& ((FlexoNode) node).getParentPetriGraph() != null
											&& ((FlexoNode) node).getParentPetriGraph().getContainer() == action.getFocusedObject()
											&& ((FlexoNode) node).getAttachedPreCondition() == null;
								}
							});
							unboundNodeParameter.setDepends("choice");
							unboundNodeParameter.setConditional("choice=" + '"' + CHOOSE_EXISTING_UNBOUND_BEGIN_NODE + '"');
							NodeParameter alreadyBoundNodeParameter = new NodeParameter("alreadyBoundNode", "used_begin_node",
									firstBoundBeginNode);
							alreadyBoundNodeParameter.setRootObject(action.getFocusedObject());
							alreadyBoundNodeParameter.setNodeSelectingConditional(new NodeSelectingConditional() {
								@Override
								public boolean isSelectable(AbstractNode node) {
									return node instanceof FlexoNode && ((FlexoNode) node).isBeginNode()
											&& ((FlexoNode) node).getParentPetriGraph() != null
											&& ((FlexoNode) node).getParentPetriGraph().getContainer() == action.getFocusedObject()
											&& ((FlexoNode) node).getAttachedPreCondition() != null;
								}
							});
							alreadyBoundNodeParameter.setDepends("choice");
							alreadyBoundNodeParameter.setConditional("choice=" + '"' + CHOOSE_EXISTING_ALREADY_BOUND_BEGIN_NODE + '"');
							AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
									action.getLocalizedName(), FlexoLocalization.localizedForKey("what_would_you_like_to_do"), choiceParam,
									newBeginNodeNameParam, unboundNodeParameter, alreadyBoundNodeParameter);
							if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
								if (choiceParam.getValue().equals(CHOOSE_EXISTING_UNBOUND_BEGIN_NODE)) {
									action.setAttachedBeginNode((FlexoNode) unboundNodeParameter.getValue());
								} else if (choiceParam.getValue().equals(CHOOSE_EXISTING_ALREADY_BOUND_BEGIN_NODE)
										&& alreadyBoundNodeParameter.getValue() != null) {
									// In this case the pre-condition is already existing, just select it
									action.setSelectedPreCondition(((FlexoNode) alreadyBoundNodeParameter.getValue())
											.getAttachedPreCondition());
								} else if (choiceParam.getValue().equals(CREATE_NEW_BEGIN_NODE)) {
									if (action.getFocusedObject() instanceof AbstractActivityNode
											|| action.getFocusedObject() instanceof OperationNode
											|| action.getFocusedObject() instanceof SelfExecutableNode) {
										if (action.getFocusedObject() instanceof SelfExecutableNode) {
											if (action.getFocusedObject() instanceof SelfExecutableActivityNode) {
												executionContext.createBeginNodeAction = CreateNode.createActivityBeginNode
														.makeNewEmbeddedAction(action.getFocusedObject(), null, action);
											} else if (action.getFocusedObject() instanceof SelfExecutableOperationNode) {
												executionContext.createBeginNodeAction = CreateNode.createOperationBeginNode
														.makeNewEmbeddedAction(action.getFocusedObject(), null, action);
											} else {
												executionContext.createBeginNodeAction = CreateNode.createActionBeginNode
														.makeNewEmbeddedAction(action.getFocusedObject(), null, action);
											}
										} else {
											if (action.getFocusedObject() instanceof AbstractActivityNode) {
												executionContext.createBeginNodeAction = CreateNode.createOperationBeginNode
														.makeNewEmbeddedAction(action.getFocusedObject(), null, action);
											} else {
												executionContext.createBeginNodeAction = CreateNode.createActionBeginNode
														.makeNewEmbeddedAction(action.getFocusedObject(), null, action);
											}
										}
										executionContext.createBeginNodeAction.setNewNodeName(newBeginNodeNameParam.getValue());
										executionContext.createBeginNodeAction.doAction();
										if (!executionContext.createBeginNodeAction.hasActionExecutionSucceeded()) {
											if (executionContext.createPg != null) {
												executionContext.createPg.undoAction();

											}
											return false;
										}
										action.setAttachedBeginNode((FlexoNode) executionContext.createBeginNodeAction.getNewNode());
									}
								}
								return true;
							} else {
								return false;
							}
						} else {
							action.setAttachedBeginNode(unboundBeginNodes.firstElement());
							return true;
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreatePreCondition> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreatePreCondition>() {
			@Override
			public boolean run(EventObject e, CreatePreCondition action) {
				if (!action.isEmbedded()) {
					getControllerActionInitializer().getWKFController().getSelectionManager()
							.setSelectedObject(action.getNewPreCondition());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionUndoFinalizer<CreatePreCondition> getDefaultUndoFinalizer() {
		return new FlexoActionUndoFinalizer<CreatePreCondition>() {
			@Override
			public boolean run(EventObject e, CreatePreCondition action) {
				CreatePreConditionExecutionContext executionContext = (CreatePreConditionExecutionContext) action.getExecutionContext();
				if (executionContext.createBeginNodeAction != null) {
					executionContext.createBeginNodeAction.undoAction();
				}
				if (executionContext.createPg != null) {
					executionContext.createPg.undoAction();
				}
				if (executionContext.createExecutionPetriGraph != null) {
					executionContext.createExecutionPetriGraph.undoAction();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionRedoInitializer<CreatePreCondition> getDefaultRedoInitializer() {
		return new FlexoActionRedoInitializer<CreatePreCondition>() {
			@Override
			public boolean run(EventObject e, CreatePreCondition action) {
				CreatePreConditionExecutionContext executionContext = (CreatePreConditionExecutionContext) action.getExecutionContext();
				if (executionContext.createPg != null) {
					executionContext.createPg.redoAction();
				}
				if (executionContext.createExecutionPetriGraph != null) {
					executionContext.createExecutionPetriGraph.redoAction();
				}
				if (executionContext.createBeginNodeAction != null) {
					executionContext.createBeginNodeAction.redoAction();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreatePreCondition> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreatePreCondition>() {
			@Override
			public boolean handleException(FlexoException exception, CreatePreCondition action) {
				if (exception instanceof InvalidArgumentException) {
					FlexoController.notify(exception.getLocalizedMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PRECONDITION_ICON;
	}

}
