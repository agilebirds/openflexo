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
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.ExecutionContext;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionRedoFinalizer;
import org.openflexo.foundation.action.FlexoActionRedoInitializer;
import org.openflexo.foundation.action.FlexoActionUndoFinalizer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.NodeParameter;
import org.openflexo.foundation.param.NodeParameter.NodeSelectingConditional;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.action.CreateEdge;
import org.openflexo.foundation.wkf.action.CreateExecutionPetriGraph;
import org.openflexo.foundation.wkf.action.CreatePetriGraph;
import org.openflexo.foundation.wkf.action.CreatePreCondition;
import org.openflexo.foundation.wkf.edge.ContextualEdgeStarting;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.ChildNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.LoopSubProcessNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SWITCHOperator;
import org.openflexo.foundation.wkf.node.SelfExecutableActionNode;
import org.openflexo.foundation.wkf.node.SelfExecutableActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.node.SelfExecutableOperationNode;
import org.openflexo.foundation.wkf.node.SingleInstanceSubProcessNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.node.WSCallSubProcessNode;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.controller.WKFController;

public class CreateEdgeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateEdgeInitializer(WKFControllerActionInitializer actionInitializer) {
		super(CreateEdge.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	private class CreateEdgeExecutionContext extends ExecutionContext {
		protected CreatePreCondition createPreCondition;
		protected CreatePetriGraph createPetriGraph;
		protected CreateExecutionPetriGraph createExecutionPetriGraph;

		public CreateEdgeExecutionContext(CreateEdge action) {
			super(action);
		}
	}

	boolean nameWasEdited = false;

	@Override
	protected FlexoActionInitializer<CreateEdge> getDefaultInitializer() {
		return new FlexoActionInitializer<CreateEdge>() {
			@Override
			public boolean run(EventObject e, CreateEdge action) {
				CreateEdgeExecutionContext executionContext = new CreateEdgeExecutionContext(action);
				action.setExecutionContext(executionContext);

				boolean skipStartNodeCheck = false;
				boolean skipEndNodeCheck = false;
				if (action.getEndNode() instanceof PetriGraphNode) {
					if (action.getStartingNode() instanceof AbstractActivityNode
							&& ((AbstractActivityNode) action.getStartingNode()).isEndNode()) {
						FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
						return false;
					}
				}

				if (action.getEndNode() instanceof SubProcessNode && ((SubProcessNode) action.getEndNode()).getSubProcess() != null
						&& ((SubProcessNode) action.getEndNode()).getSubProcess().isImported()) {
					skipEndNodeCheck = true;
				} else if (action.getEndNode() instanceof SingleInstanceSubProcessNode || action.getEndNode() instanceof LoopSubProcessNode
						|| action.getEndNode() instanceof WSCallSubProcessNode) {
					final SubProcessNode node = (SubProcessNode) action.getEndNode();
					if (node.getSubProcess() == null || node.getPortMapRegistery() == null) {
						FlexoController.notify(FlexoLocalization.localizedForKey("no_process_defined_for_end_sub_process_node"));
						return false;
					}
					if (node.getPortMapRegistery().getAllNewPortmaps().size() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("no_new_portmap_defined"));
						return false;
					} else if (node.getPortMapRegistery().getAllNewPortmaps().size() == 1) {
						action.setEndNode(node.getPortMapRegistery().getAllNewPortmaps().firstElement());
					} else if (node.getPortMapRegistery().getAllNewPortmaps().size() > 1) {
						NodeParameter ports = new NodeParameter("port", "NEW PortMap", node.getPortMapRegistery().getAllNewPortmaps()
								.firstElement());
						ports.setNodeSelectingConditional(new NodeParameter.NodeSelectingConditional() {
							@Override
							public boolean isSelectable(AbstractNode aNode) {
								return aNode instanceof FlexoPortMap && ((FlexoPortMap) aNode).isNewPort()
										&& ((FlexoPortMap) aNode).getPortMapRegistery() == node.getPortMapRegistery();
							}
						});
						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
								FlexoLocalization.localizedForKey("select_a_new_portmap"),
								FlexoLocalization.localizedForKey("select_a_new_portmap"), ports);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							if (ports.getValue() != null) {
								action.setEndNode(ports.getValue());
							} else {
								return false;
							}
						} else {
							return false;
						}
					}
				}

				if (action.getStartingNode() instanceof SubProcessNode
						&& ((SubProcessNode) action.getStartingNode()).getSubProcess() != null
						&& ((SubProcessNode) action.getStartingNode()).getSubProcess().isImported()
						&& !((SubProcessNode) action.getStartingNode()).mightHaveOperationPetriGraph()) {
					skipStartNodeCheck = true;
				} else if (action.getStartingNode() instanceof SingleInstanceSubProcessNode
						|| action.getStartingNode() instanceof LoopSubProcessNode
						|| action.getStartingNode() instanceof WSCallSubProcessNode) {
					final SubProcessNode node = (SubProcessNode) action.getStartingNode();
					if (node.getSubProcess() == null || node.getPortMapRegistery() == null) {
						FlexoController.notify(FlexoLocalization.localizedForKey("no_process_defined_for_start_sub_process_node"));
						return false;
					}
					if (node.getPortMapRegistery().getAllOutPortmaps().size() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("no_out_portmap_defined"));
						return false;
					} else if (node.getPortMapRegistery().getAllOutPortmaps().size() == 1) {
						action.setStartingNode(node.getPortMapRegistery().getAllOutPortmaps().firstElement());
					} else if (node.getPortMapRegistery().getAllOutPortmaps().size() > 1) {
						NodeParameter ports = new NodeParameter("port", "OUT PortMap", node.getPortMapRegistery().getAllOutPortmaps()
								.firstElement());
						ports.setNodeSelectingConditional(new NodeParameter.NodeSelectingConditional() {
							@Override
							public boolean isSelectable(AbstractNode aNode) {
								return aNode instanceof FlexoPortMap && ((FlexoPortMap) aNode).isOutputPort()
										&& ((FlexoPortMap) aNode).getPortMapRegistery() == node.getPortMapRegistery();
							}
						});
						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(),
								FlexoLocalization.localizedForKey("select_an_out_portmap"),
								FlexoLocalization.localizedForKey("select_an_out_portmap"), ports);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							if (ports.getValue() != null) {
								action.setStartingNode(ports.getValue());
							} else {
								return false;
							}
						} else {
							return false;
						}
					}
				}

				// BEGIN Node to BEGIN Node not allowed
				if (action.getEndNode() instanceof EventNode && ((EventNode) action.getEndNode()).isStart()
						&& action.getStartingNode() instanceof EventNode && ((EventNode) action.getStartingNode()).isStart()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
					return false;
				}

				// END Node to END Node not allowed
				if (action.getEndNode() instanceof EventNode && ((EventNode) action.getEndNode()).isEnd()
						&& action.getStartingNode() instanceof EventNode && ((EventNode) action.getStartingNode()).isEnd()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
					return false;
				}

				// BEGIN Node to BEGIN Node not allowed
				if (action.getEndNode() instanceof FlexoNode && ((FlexoNode) action.getEndNode()).isBeginNode()
						&& action.getStartingNode() instanceof FlexoNode && ((FlexoNode) action.getStartingNode()).isBeginNode()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
					return false;
				}

				// END Node to END Node not allowed
				if (action.getEndNode() instanceof FlexoNode && ((FlexoNode) action.getEndNode()).isEndNode()
						&& action.getStartingNode() instanceof FlexoNode && ((FlexoNode) action.getStartingNode()).isEndNode()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
					return false;
				}

				if (action.getEndNode() instanceof FlexoNode && action.getEndNode() instanceof FatherNode
						&& action.getEndNodePreCondition() == null && !skipEndNodeCheck) {

					if (((FlexoNode) action.getEndNode()).isBeginNode() && !(action.getStartingNode() instanceof AbstractInPort)) {
						// This is a deduced edge construction
					} else {

						// Special test to avoid construction of a precondition if we already known
						// that the edge could not be valid
						if (action.getStartingNode() instanceof PetriGraphNode || action.getStartingNode() instanceof OperatorNode
								|| action.getStartingNode() instanceof AbstractInPort || action.getStartingNode() instanceof FlexoPortMap
								&& ((FlexoPortMap) action.getStartingNode()).isOutputPort()) {

							// In this case, we first have to decide what to to relating to the end precondition

							executionContext.createPreCondition = CreatePreCondition.actionType.makeNewEmbeddedAction(
									(FatherNode) action.getEndNode(), null, action);
							executionContext.createPreCondition.setAllowsToSelectPreconditionOnly(true);
							executionContext.createPreCondition.doAction();
							if (!executionContext.createPreCondition.hasActionExecutionSucceeded()) {
								return false;
							}
							action.setEndNodePreCondition(executionContext.createPreCondition.getNewPreCondition());
						}

						else {
							FlexoController.notify(FlexoLocalization.localizedForKey("invalid_edge_definition"));
							return false;
						}
					}
				}

				// We don't want this anymore
				/*if(action.getStartingNode() instanceof ActionNode){
					String postName = action.getStartingNode().getName();
					action.setNewEdgeName(postName);
				}*/

				if (action.getStartingNode() instanceof ContextualEdgeStarting) {
					// In this case, builded edge is relative to a particular context (conditional)
					if (action.getStartingNode() instanceof IFOperator) {
						final String POSITIVE_EVALUATION = FlexoLocalization.localizedForKey("edge_matches_positive_evaluation");
						final String NEGATIVE_EVALUATION = FlexoLocalization.localizedForKey("edge_matches_negative_evaluation");
						final String TRUE = FlexoLocalization.localizedForKey("yes");
						final String FALSE = FlexoLocalization.localizedForKey("no");
						final TextFieldParameter newEdgeNameParam = new TextFieldParameter("newEdgeNameParam", "label", TRUE) {
							@Override
							public void setValue(String value) {
								super.setValue(value);
								if (!value.equals(TRUE) && !value.equals(FALSE)) {
									nameWasEdited = true;
								}
							}
						};
						RadioButtonListParameter<String> outputContextParam = new RadioButtonListParameter<String>("outputContextParam",
								"output_context", POSITIVE_EVALUATION, POSITIVE_EVALUATION, NEGATIVE_EVALUATION) {
							@Override
							public void setValue(String value) {
								if (!nameWasEdited) {
									newEdgeNameParam.setValue(value.equals(POSITIVE_EVALUATION) ? TRUE : FALSE);
								}
								super.setValue(value);
							}
						};
						newEdgeNameParam.setDepends("outputContextParam");
						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
								action.getLocalizedName(), FlexoLocalization.localizedForKey("please_define_newly_created_edge"),
								newEdgeNameParam, outputContextParam);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							// sprint 1.3.4 : do not set the name for IF operator
							// sprint 1.3.6: come back to previous behaviour (bug 1007173 & 1007174)
							action.setNewEdgeName(newEdgeNameParam.getValue());
							action.setOutputContext(outputContextParam.getValue().equals(POSITIVE_EVALUATION) ? true : false);
							return true;
						} else {
							return false;
						}
					} else if (action.getStartingNode() instanceof SWITCHOperator) {

					}
				}

				if (action.getStartingNode() instanceof SelfExecutableActivityNode) {
					final SelfExecutableActivityNode activityNode = (SelfExecutableActivityNode) action.getStartingNode();
					if (!activityNode.hasExecutionPetriGraph()) {
						executionContext.createExecutionPetriGraph = CreateExecutionPetriGraph.actionType.makeNewEmbeddedAction(
								activityNode, null, action);
						executionContext.createExecutionPetriGraph.doAction();
						if (!executionContext.createExecutionPetriGraph.hasActionExecutionSucceeded()) {
							return false;
						}
					}
					// Start node has now a PG
					ActivityPetriGraph pg = activityNode.getExecutionPetriGraph();
					AbstractActivityNode endNode = null;
					if (pg.getAllEndNodes().size() == 0) {
						// TODO: use FlexoAction here !!!
						endNode = pg.createNewEndNode();
					} else if (pg.getAllEndNodes().size() == 1) {
						endNode = (AbstractActivityNode) pg.getAllEndNodes().firstElement();
					} else {
						endNode = _selectEndNode((SelfExecutableNode) activityNode, action);
						if (endNode == null) {
							if (!action.getIsGenericOutput()) {
								return false; // Cancelled
							}
						}
					}
					if (endNode != null) {
						// Define END node as EXPLICIT starting
						action.setStartingNode(endNode);
					}
				} else if (action.getStartingNode() instanceof AbstractActivityNode
						/*&& action.getEndNode().getParentPetriGraph() == action.getStartingNode().getParentPetriGraph()*/
						&& (((AbstractActivityNode) action.getStartingNode()).isNormalNode() || ((AbstractActivityNode) action
								.getStartingNode()).isBeginNode())
						&& ((AbstractActivityNode) action.getStartingNode()).mightHaveOperationPetriGraph() && !skipStartNodeCheck) {
					final AbstractActivityNode activityNode = (AbstractActivityNode) action.getStartingNode();
					if (!activityNode.hasContainedPetriGraph()) {
						executionContext.createPetriGraph = CreatePetriGraph.actionType.makeNewEmbeddedAction(activityNode, null, action);
						executionContext.createPetriGraph.doAction();
						if (!executionContext.createPetriGraph.hasActionExecutionSucceeded()) {
							return false;
						}
					}
					// Start node has now a PG
					OperationPetriGraph pg = activityNode.getOperationPetriGraph();
					OperationNode endNode = null;
					if (pg.getAllEndNodes().size() == 0) {
						// TODO: use FlexoAction here !!!
						endNode = pg.createNewEndNode();
					} else if (pg.getAllEndNodes().size() == 1) {
						endNode = (OperationNode) pg.getAllEndNodes().firstElement();
					} else /* > 1 */{
						endNode = selectEndNode(activityNode, action);
						if (endNode == null) {
							if (!action.getIsGenericOutput()) {
								return false; // Cancelled
							}
						}
					}
					if (endNode != null) {
						// Define END node as EXPLICIT starting
						action.setStartingNode(endNode);
					}
				}

				if (action.getStartingNode() instanceof SelfExecutableOperationNode
				/*&& action.getEndNode().getParentPetriGraph() == action.getStartingNode().getParentPetriGraph()*/) {
					final SelfExecutableOperationNode activityNode = (SelfExecutableOperationNode) action.getStartingNode();
					if (!activityNode.hasExecutionPetriGraph()) {
						executionContext.createExecutionPetriGraph = CreateExecutionPetriGraph.actionType.makeNewEmbeddedAction(
								activityNode, null, action);
						executionContext.createExecutionPetriGraph.doAction();
						if (!executionContext.createExecutionPetriGraph.hasActionExecutionSucceeded()) {
							return false;
						}
					}
					// Start node has now a PG
					OperationPetriGraph pg = activityNode.getExecutionPetriGraph();
					OperationNode endNode = null;
					if (pg.getAllEndNodes().size() == 0) {
						// TODO: use FlexoAction here !!!
						endNode = pg.createNewEndNode();
					} else if (pg.getAllEndNodes().size() == 1) {
						endNode = (OperationNode) pg.getAllEndNodes().firstElement();
					} else {
						endNode = _selectEndNode((SelfExecutableNode) activityNode, action);
						if (endNode == null) {
							if (!action.getIsGenericOutput()) {
								return false; // Cancelled
							}
						}
					}
					if (endNode != null) {
						// Define END node as EXPLICIT starting
						action.setStartingNode(endNode);
					}
				} else if (action.getStartingNode() instanceof OperationNode
				/*&& action.getEndNode().getParentPetriGraph() == action.getStartingNode().getParentPetriGraph()*/
				&& ((OperationNode) action.getStartingNode()).isNormalNode()
						&& ((OperationNode) action.getStartingNode()).mightHaveActionPetriGraph()) {
					final OperationNode operationNode = (OperationNode) action.getStartingNode();
					if (!operationNode.hasContainedPetriGraph()) {
						executionContext.createPetriGraph = CreatePetriGraph.actionType.makeNewEmbeddedAction(operationNode, null, action);
						executionContext.createPetriGraph.doAction();
						if (!executionContext.createPetriGraph.hasActionExecutionSucceeded()) {
							return false;
						}
					}
					// Start node has now a PG
					ActionPetriGraph pg = operationNode.getActionPetriGraph();
					ActionNode endNode = null;
					if (pg.getAllEndNodes().size() == 0) {
						// TODO: use FlexoAction here !!!
						endNode = pg.createNewEndNode();
					} else if (pg.getAllEndNodes().size() == 1) {
						endNode = (ActionNode) pg.getAllEndNodes().firstElement();
					} else /* > 1 */{
						endNode = selectEndNode(operationNode, action);
						if (endNode == null) {
							if (!action.getIsGenericOutput()) {
								return false; // Cancelled
							}
						}
					}
					if (endNode != null) {
						// Define END node as EXPLICIT starting
						action.setStartingNode(endNode);
					}
				}
				if (action.getStartingNode() instanceof SelfExecutableActionNode
				/* && action.getEndNode().getParentPetriGraph() == action.getStartingNode().getParentPetriGraph() */) {
					final SelfExecutableActionNode actionNode = (SelfExecutableActionNode) action.getStartingNode();
					if (!actionNode.hasExecutionPetriGraph()) {
						executionContext.createExecutionPetriGraph = CreateExecutionPetriGraph.actionType.makeNewEmbeddedAction(actionNode,
								null, action);
						executionContext.createExecutionPetriGraph.doAction();
						if (!executionContext.createExecutionPetriGraph.hasActionExecutionSucceeded()) {
							return false;
						}
					}
					// Start node has now a PG
					ActionPetriGraph pg = actionNode.getExecutionPetriGraph();
					ActionNode endNode = null;
					if (pg.getAllEndNodes().size() == 0) {
						// TODO: use FlexoAction here !!!
						endNode = pg.createNewEndNode();
					} else if (pg.getAllEndNodes().size() == 1) {
						endNode = (ActionNode) pg.getAllEndNodes().firstElement();
					} else {
						endNode = _selectEndNode((SelfExecutableNode) actionNode, action);
						if (endNode == null) {
							if (!action.getIsGenericOutput()) {
								return false; // Cancelled
							}
						}
					}
					if (endNode != null) {
						// Define END node as EXPLICIT starting
						action.setStartingNode(endNode);
					}
				}

				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<CreateEdge> getDefaultFinalizer() {
		return new FlexoActionFinalizer<CreateEdge>() {
			@Override
			public boolean run(EventObject e, CreateEdge action) {
				if (action.getExecutionContext() instanceof CreateEdgeExecutionContext) {
					CreateEdgeExecutionContext context = (CreateEdgeExecutionContext) action.getExecutionContext();
					if (context.createPreCondition != null && context.createPreCondition.getNewPreCondition() != null
							&& context.createPreCondition.getNewPreCondition().getIncomingPostConditions().size() == 1) {
						context.createPreCondition.getNewPreCondition().resetLocation();
					}
				}

				getController().getSelectionManager().setSelectedObject(action.getNewPostCondition());

				return true;
			}
		};
	}

	@Override
	protected FlexoActionUndoFinalizer<CreateEdge> getDefaultUndoFinalizer() {
		return new FlexoActionUndoFinalizer<CreateEdge>() {
			@Override
			public boolean run(EventObject e, CreateEdge action) {
				CreateEdgeExecutionContext executionContext = (CreateEdgeExecutionContext) action.getExecutionContext();
				if (executionContext.createPetriGraph != null) {
					executionContext.createPetriGraph.undoAction();
				}
				if (executionContext.createExecutionPetriGraph != null) {
					executionContext.createExecutionPetriGraph.undoAction();
				}
				if (executionContext.createPreCondition != null) {
					executionContext.createPreCondition.undoAction();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionRedoInitializer<CreateEdge> getDefaultRedoInitializer() {
		return new FlexoActionRedoInitializer<CreateEdge>() {
			@Override
			public boolean run(EventObject e, CreateEdge action) {
				CreateEdgeExecutionContext executionContext = (CreateEdgeExecutionContext) action.getExecutionContext();
				if (executionContext.createPreCondition != null) {
					executionContext.createPreCondition.redoAction();
					action.setEndNodePreCondition(executionContext.createPreCondition.getNewPreCondition());
				}
				if (executionContext.createPetriGraph != null) {
					executionContext.createPetriGraph.redoAction();
				}
				if (executionContext.createExecutionPetriGraph != null) {
					executionContext.createExecutionPetriGraph.redoAction();
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionRedoFinalizer<CreateEdge> getDefaultRedoFinalizer() {
		return new FlexoActionRedoFinalizer<CreateEdge>() {
			@Override
			public boolean run(EventObject e, CreateEdge action) {
				getControllerActionInitializer().getWKFController().getSelectionManager().setSelectedObject(action.getNewPostCondition());
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<CreateEdge> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<CreateEdge>() {
			@Override
			public boolean handleException(FlexoException exception, CreateEdge action) {
				if (exception instanceof CreateEdge.DisplayActionCannotBeBound) {
					FlexoController.notify(exception.getLocalizedMessage());
					return true;
				} else if (exception instanceof CreateEdge.InvalidEdgeDefinition) {
					FlexoController.notify(exception.getLocalizedMessage());
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.POSTCONDITION_ICON;
	}

	private OperationNode selectEndNode(final AbstractActivityNode activityNode, CreateEdge action) {
		/*OperationPetriGraph pg = activityNode.getOperationPetriGraph();
		String CHOOSE_EXISTING_END_NODE = FlexoLocalization.localizedForKey("choose_existing_end_node");
		String CREATE_NEW_END_NODE = FlexoLocalization.localizedForKey("create_new_end_node");
		String DEFINE_AS_GENERIC_OUTPUT = FlexoLocalization.localizedForKey("define_as_generic_output");
		Vector<String> availableChoices = new Vector<String>();
		availableChoices.add(CHOOSE_EXISTING_END_NODE);
		availableChoices.add(CREATE_NEW_END_NODE);
		availableChoices.add(DEFINE_AS_GENERIC_OUTPUT);
		String[] choices = availableChoices.toArray(new String[availableChoices.size()]);
		RadioButtonListParameter<String> choiceParam = new RadioButtonListParameter<String>("choice","choose_an_option",
				CHOOSE_EXISTING_END_NODE,choices);
		TextFieldParameter newEndNodeNameParam = new TextFieldParameter(
				"newEndNodeName", "new_end_node_name", pg.getProcess().findNextInitialName(FlexoLocalization.localizedForKey("end_node"), (FatherNode)action.getFocusedObject()));
		newEndNodeNameParam.setDepends("choice");
		newEndNodeNameParam.setConditional("choice="+'"'+CREATE_NEW_END_NODE+'"');
		NodeParameter existingNodeParameter = new NodeParameter("existingNode","existing_end_node",pg.getAllEndNodes().firstElement());
		existingNodeParameter.setRootObject(action.getFocusedObject());
		existingNodeParameter.setNodeSelectingConditional(new NodeSelectingConditional() {
			public boolean isSelectable(AbstractNode node) {
				return ((node instanceof ChildNode)
						&& (((ChildNode)node).isEndNode())
						&& (((ChildNode)node).getFather() == activityNode));
			}
		});
		existingNodeParameter.setDepends("choice");
		existingNodeParameter.setConditional("choice="+'"'+CHOOSE_EXISTING_END_NODE+'"');
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
				getProject(),
				null,
				FlexoLocalization.localizedForKey("multiple_outputs_are_defined_for_this_node"),
				FlexoLocalization.localizedForKey("what_would_you_like_to_do"),
				choiceParam,
				newEndNodeNameParam,
				existingNodeParameter);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			if (choiceParam.getValue().equals(CHOOSE_EXISTING_END_NODE)) {
				return ((OperationNode)existingNodeParameter.getValue());
			}
			else if (choiceParam.getValue().equals(CREATE_NEW_END_NODE)) {
				// TODO: use FlexoAction here !!!
				return pg.createNewEndNode(newEndNodeNameParam.getValue());
			}
			else {
				action.setIsGenericOutput(true);
				return null;
			}
		}
		else {
			return null;
		}*/
		return _selectEndNode(activityNode, action);
	}

	private ActionNode selectEndNode(final OperationNode operationNode, CreateEdge action) {
		return _selectEndNode(operationNode, action);
	}

	private <F extends FatherNode, C extends ChildNode> C _selectEndNode(final F fatherNode, CreateEdge action) {
		FlexoPetriGraph pg = fatherNode.getContainedPetriGraph();
		String CHOOSE_EXISTING_END_NODE = FlexoLocalization.localizedForKey("choose_existing_end_node");
		String CREATE_NEW_END_NODE = FlexoLocalization.localizedForKey("create_new_end_node");
		String DEFINE_AS_GENERIC_OUTPUT = FlexoLocalization.localizedForKey("define_as_generic_output");
		Vector<String> availableChoices = new Vector<String>();
		availableChoices.add(CHOOSE_EXISTING_END_NODE);
		availableChoices.add(CREATE_NEW_END_NODE);
		availableChoices.add(DEFINE_AS_GENERIC_OUTPUT);
		String[] choices = availableChoices.toArray(new String[availableChoices.size()]);
		RadioButtonListParameter<String> choiceParam = new RadioButtonListParameter<String>("choice", "choose_an_option",
				CHOOSE_EXISTING_END_NODE, choices);
		TextFieldParameter newEndNodeNameParam = new TextFieldParameter("newEndNodeName", "new_end_node_name", pg.getProcess()
				.findNextInitialName(FlexoLocalization.localizedForKey("end_node"), action.getFocusedObject()));
		newEndNodeNameParam.setDepends("choice");
		newEndNodeNameParam.setConditional("choice=" + '"' + CREATE_NEW_END_NODE + '"');
		NodeParameter existingNodeParameter = new NodeParameter("existingNode", "existing_end_node", pg.getAllEndNodes().firstElement());
		existingNodeParameter.setRootObject(action.getFocusedObject());
		existingNodeParameter.setNodeSelectingConditional(new NodeSelectingConditional() {
			@Override
			public boolean isSelectable(AbstractNode node) {
				return node instanceof ChildNode && ((ChildNode) node).isEndNode() && ((ChildNode) node).getFather() == fatherNode;
			}
		});
		existingNodeParameter.setDepends("choice");
		existingNodeParameter.setConditional("choice=" + '"' + CHOOSE_EXISTING_END_NODE + '"');
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
				FlexoLocalization.localizedForKey("multiple_outputs_are_defined_for_this_node"),
				FlexoLocalization.localizedForKey("what_would_you_like_to_do"), choiceParam, newEndNodeNameParam, existingNodeParameter);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			if (choiceParam.getValue().equals(CHOOSE_EXISTING_END_NODE)) {
				return (C) existingNodeParameter.getValue();
			} else if (choiceParam.getValue().equals(CREATE_NEW_END_NODE)) {
				// TODO: use FlexoAction here !!!
				return (C) pg.createNewEndNode(newEndNodeNameParam.getValue());
			} else {
				action.setIsGenericOutput(true);
				return null;
			}
		} else {
			return null;
		}
	}

	private <F extends SelfExecutableNode, P extends FlexoNode> P _selectEndNode(final F selfExec, CreateEdge action) {
		FlexoPetriGraph pg = selfExec.getExecutionPetriGraph();
		String CHOOSE_EXISTING_END_NODE = FlexoLocalization.localizedForKey("choose_existing_end_node");
		String CREATE_NEW_END_NODE = FlexoLocalization.localizedForKey("create_new_end_node");
		String DEFINE_AS_GENERIC_OUTPUT = FlexoLocalization.localizedForKey("define_as_generic_output");
		Vector<String> availableChoices = new Vector<String>();
		availableChoices.add(CHOOSE_EXISTING_END_NODE);
		availableChoices.add(CREATE_NEW_END_NODE);
		availableChoices.add(DEFINE_AS_GENERIC_OUTPUT);
		String[] choices = availableChoices.toArray(new String[availableChoices.size()]);
		RadioButtonListParameter<String> choiceParam = new RadioButtonListParameter<String>("choice", "choose_an_option",
				CHOOSE_EXISTING_END_NODE, choices);
		TextFieldParameter newEndNodeNameParam = new TextFieldParameter("newEndNodeName", "new_end_node_name", pg.getProcess()
				.findNextInitialName(FlexoLocalization.localizedForKey("end_node"), action.getFocusedObject()));
		newEndNodeNameParam.setDepends("choice");
		newEndNodeNameParam.setConditional("choice=" + '"' + CREATE_NEW_END_NODE + '"');
		NodeParameter existingNodeParameter = new NodeParameter("existingNode", "existing_end_node", pg.getAllEndNodes().firstElement());
		existingNodeParameter.setRootObject(action.getFocusedObject());
		existingNodeParameter.setNodeSelectingConditional(new NodeSelectingConditional() {
			@Override
			public boolean isSelectable(AbstractNode node) {
				return node instanceof FlexoNode && ((FlexoNode) node).isEndNode()
						&& ((FlexoNode) node).getParentPetriGraph().getContainer() == selfExec;
			}
		});
		existingNodeParameter.setDepends("choice");
		existingNodeParameter.setConditional("choice=" + '"' + CHOOSE_EXISTING_END_NODE + '"');
		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
				FlexoLocalization.localizedForKey("multiple_outputs_are_defined_for_this_node"),
				FlexoLocalization.localizedForKey("what_would_you_like_to_do"), choiceParam, newEndNodeNameParam, existingNodeParameter);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			if (choiceParam.getValue().equals(CHOOSE_EXISTING_END_NODE)) {
				return (P) existingNodeParameter.getValue();
			} else if (choiceParam.getValue().equals(CREATE_NEW_END_NODE)) {
				// TODO: use FlexoAction here !!!
				return (P) pg.createNewEndNode(newEndNodeNameParam.getValue());
			} else {
				action.setIsGenericOutput(true);
				return null;
			}
		} else {
			return null;
		}
	}
}
