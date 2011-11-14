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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.ch.DefaultInspectorHelpDelegate;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement.ControlGraphFactory;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorTabbedPanel;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.InspectorSelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.selection.SelectionManager;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.ProgrammingLanguage;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoInspectorController;
import org.openflexo.view.listener.FlexoActionButton;

public class FlexoControlGraphController extends FlexoInspectorController {

	private static final Logger logger = Logger.getLogger(FlexoControlGraphController.class.getPackage().getName());

	public static final Font HEADER_FONT = new Font("Verdana", Font.BOLD, 14);
	public static final Font SUB_TITLE_FONT = new Font("Verdana", Font.ITALIC, 10);

	protected InspectorTabbedPanel _inspectorPanel;
	protected WKFController _controller;
	protected ExecutableWorkflowElement _currentlyInspectedObject;
	protected boolean isMultipleSelection = false;

	private ControlFlowGraphViewer _viewer;

	private ProgrammingLanguage selectedLanguage = ControlGraphFactory.DEFAULT_LANGUAGE;

	private boolean selectedInterprocedural = ControlGraphFactory.DEFAULT_INTERPROCEDURAL;

	protected FlexoControlGraphController(WKFController controller) {
		super(controller.new FlexoControllerInspectorDelegate(), new DefaultInspectorHelpDelegate(DocResourceManager.instance()));
		// if (getDocTabModel() != null)
		_controller = controller;
		_inspectorPanel = createInspectorTabbedPanel();
		loadInspectors(Inspectors.WKF_CG);
		updateSelection(_controller.getWKFSelectionManager());
	}

	private void updateSelection(SelectionManager sm) {
		if (sm.getSelectionSize() == 0) {
			update(sm, new EmptySelection());
		} else if ((sm.getSelectionSize() == 1) && (sm.getSelection().firstElement() instanceof InspectableObject)
				&& (sm.getSelection().firstElement() instanceof ExecutableWorkflowElement)) {
			ExecutableWorkflowElement objectToInspect = (ExecutableWorkflowElement) sm.getSelection().firstElement();
			objectToInspect.setInterproceduralForControlGraphComputation(selectedInterprocedural);
			objectToInspect.setProgrammingLanguageForControlGraphComputation(selectedLanguage);
			update(sm, new UniqueSelection((InspectableObject) objectToInspect, sm.getInspectionContext()));
		} else if (sm.getSelectionSize() > 1) {
			update(sm, new MultipleSelection());
		}
	}

	@Override
	public void update(Observable observable, Object selection) {
		isMultipleSelection = false;
		if (selection instanceof InspectorSelection) {
			if (selection instanceof EmptySelection) {
				_currentlyInspectedObject = null;
			} else if (selection instanceof MultipleSelection) {
				_currentlyInspectedObject = null;
				isMultipleSelection = true;
			} else if (selection instanceof UniqueSelection) {
				if (((UniqueSelection) selection).getInspectedObject() instanceof ExecutableWorkflowElement) {
					_currentlyInspectedObject = (ExecutableWorkflowElement) ((UniqueSelection) selection).getInspectedObject();
					_currentlyInspectedObject.setInterproceduralForControlGraphComputation(selectedInterprocedural);
					_currentlyInspectedObject.setProgrammingLanguageForControlGraphComputation(selectedLanguage);
				} else {
					_currentlyInspectedObject = null;
				}
			}
		}
		getViewer().getHeader().update();
		super.update(observable, selection);
	}

	public InspectorTabbedPanel getDocInspectorPanel() {
		return _inspectorPanel;
	}

	/*private TabModel _docTabModel;

	private TabModel getDocTabModel()
	{
		if (_docTabModel == null) {
			try {
				InspectorModel docInspectorModel = importInspectorFile(Inspectors.getDocInspectorFile(getInspectorDirectory()));
				_docTabModel = docInspectorModel.getTabs().elements().nextElement();
			} catch (FileNotFoundException e) {
				logger.warning("File NOT FOUND: "+Inspectors.getDocInspectorFile(getInspectorDirectory()));
			}

		}
		return _docTabModel;
	}*/

	// We override here default behaviour by using other inspectors in the context of
	// Control Graph visualization
	@Override
	public String getInspectorName(InspectableObject object, Hashtable<String, Object> inspectionContext) {
		if (object instanceof FlexoProcess) {
			return Inspectors.WKF_CG.FLEXO_PROCESS_CONTROL_FLOW_GRAPH_INSPECTOR;
		} else if (object instanceof PetriGraphNode) {
			return Inspectors.WKF_CG.FLEXO_NODE_CONTROL_FLOW_GRAPH_INSPECTOR;
		} else if (object instanceof OperatorNode) {
			return Inspectors.WKF_CG.OPERATOR_NODE_CONTROL_FLOW_GRAPH_INSPECTOR;
		} else if (object instanceof FlexoPreCondition) {
			return Inspectors.WKF_CG.PRE_CONDITION_CONTROL_FLOW_GRAPH_INSPECTOR;
		} else if (object instanceof FlexoPostCondition) {
			return Inspectors.WKF_CG.EDGE_CONTROL_FLOW_GRAPH_INSPECTOR;
		} else {
			return null;
		}
	}

	public ControlFlowGraphViewer getViewer() {
		if (_viewer == null) {
			_viewer = new ControlFlowGraphViewer();
		}
		return _viewer;
	}

	public class ControlFlowGraphViewer extends FlexoDialog {
		protected Logger logger = FlexoLogger.getLogger(ControlFlowGraphViewer.class.getPackage().getName());

		public ControlFlowGraphViewer() {
			super(_controller.getFlexoFrame(), FlexoLocalization.localizedForKey("flexo_model_execution"), false);

			_header = new ViewHeader();

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(_header, BorderLayout.NORTH);
			getContentPane().add(getDocInspectorPanel(), BorderLayout.CENTER);
			setPreferredSize(new Dimension(700, 600));
			validate();
			pack();
		}

		private final ViewHeader _header;

		protected class ViewHeader extends JPanel {
			JLabel icon;
			JLabel title;
			JLabel subTitle;
			JPanel controlPanel;
			Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

			private final JComboBox languageSelector;
			private final JCheckBox interproceduralSelector;

			protected ViewHeader() {
				super(new BorderLayout());
				icon = new JLabel(CGIconLibrary.CG_MEDIUM_ICON);
				icon.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
				add(icon, BorderLayout.WEST);
				title = new JLabel(getTitleText(), SwingConstants.LEFT);
				// title.setVerticalAlignment(JLabel.BOTTOM);
				title.setFont(HEADER_FONT);
				title.setForeground(Color.BLACK);
				title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
				subTitle = new JLabel(getSubTitleText(), SwingConstants.LEFT);
				subTitle.setFont(SUB_TITLE_FONT);
				subTitle.setForeground(Color.GRAY);
				subTitle.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
				subTitle.setVerticalAlignment(SwingConstants.BOTTOM);

				JPanel labelsPanel = new JPanel(new VerticalLayout());
				labelsPanel.add(title);
				labelsPanel.add(subTitle);
				add(labelsPanel, BorderLayout.CENTER);

				languageSelector = new JComboBox(ProgrammingLanguage.values());
				languageSelector.setSelectedItem(selectedLanguage);
				languageSelector.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectorChanged();
					}
				});
				languageSelector.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

				interproceduralSelector = new JCheckBox(FlexoLocalization.localizedForKey("interprocedural"), true);
				interproceduralSelector.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectorChanged();
					}
				});
				interproceduralSelector.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

				JPanel selectorsPanel = new JPanel(new VerticalLayout());
				selectorsPanel.add(languageSelector);
				selectorsPanel.add(interproceduralSelector);
				add(selectorsPanel, BorderLayout.EAST);

				update();
			}

			private void selectorChanged() {
				selectedInterprocedural = interproceduralSelector.isSelected();
				selectedLanguage = (ProgrammingLanguage) languageSelector.getSelectedItem();
				updateSelection(_controller.getWKFSelectionManager());
				_inspectorPanel.currentTabPanel.updateFromModel();
			}

			private String getTitleText() {
				if (_currentlyInspectedObject == null) {
					if (isMultipleSelection) {
						return FlexoLocalization.localizedForKey("multiple_selection");
					} else {
						return FlexoLocalization.localizedForKey("no_selected_object");
					}
				}
				return _currentlyInspectedObject.getExecutableElementName();
			}

			private String getSubTitleText() {
				if (_currentlyInspectedObject == null) {
					return FlexoLocalization.localizedForKey("please_select_one_executable_workflow_element");
				}
				return FlexoLocalization.localizedForKey("please_press_refresh_button_to_get_up_to_date_control_flow_graph");
			}

			protected void update() {
				title.setText(getTitleText());
				subTitle.setText(getSubTitleText());
				for (FlexoActionButton button : actionButtons) {
					button.update();
				}
			}

		}

		protected ViewHeader getHeader() {
			return _header;
		}

		private FlexoModelObject getFocusedObject() {
			return _controller.getSelectionManager().getFocusedObject();
		}

		private void refresh() {
			if (getFocusedObject() instanceof FlexoNode) {
				if (((FlexoNode) getFocusedObject()).getActivation() != null) {
					((FlexoNode) getFocusedObject()).getActivation().refresh();
				}
				if (((FlexoNode) getFocusedObject()).getDesactivation() != null) {
					((FlexoNode) getFocusedObject()).getDesactivation().refresh();
				}
			} else if (getFocusedObject() instanceof FlexoPostCondition) {
				if (((FlexoPostCondition) getFocusedObject()).getExecution() != null) {
					((FlexoPostCondition) getFocusedObject()).getExecution().refresh();
				}
			} else if (getFocusedObject() instanceof FlexoPreCondition) {
				if (((FlexoPreCondition) getFocusedObject()).getExecution() != null) {
					((FlexoPreCondition) getFocusedObject()).getExecution().refresh();
				}
			} else if (getFocusedObject() instanceof FlexoProcess) {
				if (((FlexoProcess) getFocusedObject()).getExecution() != null) {
					((FlexoProcess) getFocusedObject()).getExecution().refresh();
				}
			} else if (getFocusedObject() instanceof OperatorNode) {
				if (((OperatorNode) getFocusedObject()).getExecution() != null) {
					((OperatorNode) getFocusedObject()).getExecution().refresh();
				}
			} else if (getFocusedObject() instanceof EventNode) {
				if (((EventNode) getFocusedObject()).getExecution() != null) {
					((EventNode) getFocusedObject()).getExecution().refresh();
				}
			}
		}

		@Override
		public void setVisible(boolean b) {
			if (b) {
				refresh();
			}
			super.setVisible(b);
		}

	}

}
