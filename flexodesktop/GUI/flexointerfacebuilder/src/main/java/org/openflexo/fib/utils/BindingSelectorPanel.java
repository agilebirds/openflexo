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
package org.openflexo.fib.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.Function;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.Typed;
import org.openflexo.antar.expr.BindingValue;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.utils.BindingSelector.EditionMode;
import org.openflexo.fib.utils.table.AbstractModel;
import org.openflexo.fib.utils.table.BindingValueColumn;
import org.openflexo.fib.utils.table.IconColumn;
import org.openflexo.fib.utils.table.StringColumn;
import org.openflexo.fib.utils.table.TabularPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.ButtonsControlPanel;
import org.openflexo.swing.MouseOverButton;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * This class encodes the panel representing a BindingValue<br>
 * Such a panel is always used in a context of a BindingSelector and thus always provides access to its {@link BindingSelector}
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class BindingSelectorPanel extends AbstractBindingSelectorPanel implements ListSelectionListener {

	static final Logger logger = Logger.getLogger(BindingSelectorPanel.class.getPackage().getName());

	final BindingSelector bindingSelector;

	protected JPanel browserPanel;

	ButtonsControlPanel _controlPanel;

	JButton connectButton;
	JButton cancelButton;
	JButton resetButton;
	JButton expressionButton;

	JButton createsButton;

	private Map<BindingPathElement, Hashtable<Type, BindingColumnListModel>> _listModels;

	Vector<FilteredJList> _lists;

	protected int defaultVisibleColCount = 3;

	protected final EmptyColumnListModel EMPTY_MODEL = new EmptyColumnListModel();

	private BindingColumnListModel _rootBindingColumnListModel = null;

	JLabel currentTypeLabel;
	private JLabel searchedTypeLabel;
	private JTextArea bindingValueRepresentation;
	protected BindingColumnElement currentFocused = null;

	protected BindingSelectorPanel(BindingSelector bindingSelector) {
		super();
		this.bindingSelector = bindingSelector;
		_listModels = new Hashtable<BindingPathElement, Hashtable<Type, BindingColumnListModel>>();
		_rootBindingColumnListModel = null;
		_lists = new Vector<FilteredJList>();
	}

	@Override
	public void delete() {
		for (JList list : _lists) {
			list.removeListSelectionListener(this);
			list.setModel(null);
		}
		_lists.clear();
		_listModels.clear();
		_rootBindingColumnListModel = null;
		currentFocused = null;
	}

	public int getIndexOfList(BindingColumnListModel model) {
		for (int i = 0; i < _lists.size(); i++) {
			FilteredJList l = _lists.get(i);
			if (l.getModel() == model) {
				return i;
			}
		}
		return -1;
	}

	public Class getAccessedEntity() {
		Class reply = null;
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			i++;
		}
		if (last != null) {
			return TypeUtils.getBaseClass(last.getElement().getType());
		}
		return reply;
	}

	public BindingVariable getSelectedBindingVariable() {
		if (listAtIndex(0) != null && listAtIndex(0).getSelectedValue() != null) {
			return (BindingVariable) ((BindingColumnElement) listAtIndex(0).getSelectedValue()).getElement();
		} else if (listAtIndex(0) != null && listAtIndex(0).getModel().getSize() == 1) {
			return (BindingVariable) listAtIndex(0).getModel().getElementAt(0).getElement();
		} else {
			return null;
		}
	}

	@Deprecated
	private BindingColumnElement findElementMatching(ListModel listModel, String subPartialPath, Vector<Integer> pathElementIndex) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement
					&& ((BindingColumnElement) listModel.getElementAt(i)).getLabel().startsWith(subPartialPath)) {
				if (pathElementIndex.size() == 0) {
					pathElementIndex.add(i);
				} else {
					pathElementIndex.set(0, i);
				}
				return (BindingColumnElement) listModel.getElementAt(i);
			}
		}
		return null;
	}

	Vector<BindingColumnElement> findElementsMatching(BindingColumnListModel listModel, String subPartialPath) {
		Vector<BindingColumnElement> returned = new Vector<BindingColumnElement>();
		for (int i = 0; i < listModel.getUnfilteredSize(); i++) {
			if (listModel.getUnfilteredElementAt(i).getLabel().startsWith(subPartialPath)) {
				returned.add(listModel.getUnfilteredElementAt(i));
			}
		}
		return returned;
	}

	BindingColumnElement findElementEquals(ListModel listModel, String subPartialPath) {
		for (int i = 0; i < listModel.getSize(); i++) {
			if (listModel.getElementAt(i) instanceof BindingColumnElement) {
				if (((BindingColumnElement) listModel.getElementAt(i)).getLabel().equals(subPartialPath)) {
					return (BindingColumnElement) listModel.getElementAt(i);
				}
			}
		}
		return null;
	}

	public Type getEndingTypeForSubPath(String pathIgnoringLastPart) {
		StringTokenizer token = new StringTokenizer(pathIgnoringLastPart, ".", false);
		Object obj = null;
		int i = 0;
		while (token.hasMoreTokens()) {
			obj = findElementEquals(listAtIndex(i).getModel(), token.nextToken());
			i++;
		}
		if (obj instanceof BindingColumnElement) {
			Typed element = ((BindingColumnElement) obj).getElement();
			return element.getType();
		}
		return null;
	}

	protected class MethodCallBindingsModel extends AbstractModel<FunctionPathElement, Function.FunctionArgument> {
		public MethodCallBindingsModel() {
			super(null);
			addToColumns(new IconColumn<Function.FunctionArgument>("icon", 25) {
				@Override
				public Icon getIcon(Function.FunctionArgument entity) {
					return FIBIconLibrary.METHOD_ICON;
				}
			});
			addToColumns(new StringColumn<Function.FunctionArgument>("name", 100) {
				@Override
				public String getValue(Function.FunctionArgument arg) {
					if (arg != null) {
						return arg.getArgumentName();
					}
					/*
					 * if (paramForValue(bindingValue) != null) return
					 * paramForValue(bindingValue).getName();
					 */
					return "null";
				}
			});
			addToColumns(new StringColumn<Function.FunctionArgument>("type", 100) {
				@Override
				public String getValue(Function.FunctionArgument arg) {
					if (arg != null) {
						return TypeUtils.simpleRepresentation(arg.getArgumentType());
					}
					return "null";
				}
			});
			addToColumns(new BindingValueColumn<Function.FunctionArgument>("value", 250, true) {

				@Override
				public DataBinding getValue(Function.FunctionArgument arg) {
					return getFunctionPathElement().getParameter(arg);
				}

				@Override
				public void setValue(Function.FunctionArgument arg, DataBinding aValue) {
					// logger.info("setValue in BindingValueColumn with " + aValue);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Sets value " + arg + " to be " + aValue);
					}
					if (arg != null) {
						getFunctionPathElement().setParameter(arg, aValue);
						if (bindingSelector.getEditedObject().isBindingValue()) {
							BindingValue bv = (BindingValue) bindingSelector.getEditedObject().getExpression();
							bv.markedAsToBeReanalized();
						}
					}

					bindingSelector.fireEditedObjectChanged();
				}

				@Override
				public Bindable getBindableFor(DataBinding<?> value, Function.FunctionArgument rowObject) {
					if (value != null) {
						return value.getOwner();
					}
					return null;
					// return bindingSelector.getBindable();
				}

				@Override
				public BindingDefinition getBindingDefinitionFor(DataBinding<?> value, Function.FunctionArgument rowObject) {
					if (value != null) {
						return value.getBindingDefinition();
					}
					return null;
					/*if (rowObject != null) {
						return getFunctionPathElement().getParameter(rowObject).getBindingDefinition();
					}
					return null;*/
				}

				@Override
				public boolean allowsCompoundBinding(DataBinding<?> value) {
					return true;
				}

				@Override
				public boolean allowsNewEntryCreation(DataBinding<?> value) {
					return false;
				}
			});
		}

		/*
		 * DMMethodParameter paramForValue(AbstractBinding bindingValue) { if
		 * ((bindingValue.getBindingDefinition() != null) &&
		 * (bindingValue.getBindingDefinition() instanceof
		 * MethodCall.MethodCallParamBindingDefinition)) { return
		 * ((MethodCall.MethodCallParamBindingDefinition
		 * )bindingValue.getBindingDefinition()).getParam(); } return null; }
		 */

		public FunctionPathElement getFunctionPathElement() {
			return getModel();
		}

		@Override
		public Function.FunctionArgument elementAt(int row) {
			if (row >= 0 && row < getRowCount()) {
				return getFunctionPathElement().getFunction().getArguments().get(row);
			} else {
				return null;
			}
		}

		@Override
		public int getRowCount() {
			if (getFunctionPathElement() != null) {
				return getFunctionPathElement().getFunction().getArguments().size();
			}
			return 0;
		}

		@Override
		public void setModel(FunctionPathElement model) {
			// logger.info("On set le modele du MethodCallBindingsModel avec " + model);
			if (model != null) {
				model.instanciateParameters(bindingSelector.getBindable());
			}
			super.setModel(model);
		}

		@Override
		public BindingEvaluationContext getBindingEvaluationContext() {
			return null;
		}
	}

	protected class MethodCallBindingsPanel extends TabularPanel {
		public MethodCallBindingsPanel() {
			super(getMethodCallBindingsModel(), 3);
		}

	}

	private MethodCallBindingsModel _methodCallBindingsModel;

	private MethodCallBindingsPanel _methodCallBindingsPanel;

	public MethodCallBindingsPanel getMethodCallBindingsPanel() {
		if (_methodCallBindingsPanel == null) {
			_methodCallBindingsPanel = new MethodCallBindingsPanel();

		}

		return _methodCallBindingsPanel;
	}

	public MethodCallBindingsModel getMethodCallBindingsModel() {
		if (_methodCallBindingsModel == null) {
			_methodCallBindingsModel = new MethodCallBindingsModel();
		}
		return _methodCallBindingsModel;
	}

	@Override
	public Dimension getDefaultSize() {
		int baseHeight;

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			baseHeight = 300;
		} else {
			baseHeight = 180;
		}

		if (bindingSelector.areStaticValuesAllowed() || bindingSelector.areCompoundBindingAllowed()) {
			baseHeight += 30;
		}

		return new Dimension(500, baseHeight);

	}

	@Override
	protected void willApply() {
		if (editStaticValue && staticBindingPanel != null) {
			staticBindingPanel.willApply();
		}
	}

	private MouseOverButton showHideCompoundBindingsButton;

	private ConstantValuePanel staticBindingPanel;

	@Override
	protected void init() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("init() with " + bindingSelector.editionMode + " for " + bindingSelector.getEditedObject());
		}

		setLayout(new BorderLayout());

		browserPanel = new JPanel();
		browserPanel.setLayout(new BoxLayout(browserPanel, BoxLayout.X_AXIS));
		for (int i = 0; i < defaultVisibleColCount; i++) {
			makeNewJList();
		}

		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, key, component);
			}
		};
		connectButton = _controlPanel.addButton("connect", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.apply();
			}
		});
		cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.cancel();
			}
		});
		resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				bindingSelector.getEditedObject().reset();
				bindingSelector.apply();
			}
		});
		if (bindingSelector.areBindingExpressionsAllowed()) {
			expressionButton = _controlPanel.addButton("expression", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bindingSelector.activateBindingExpressionMode();
				}
			});
		}

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());

		if (bindingSelector.areCompoundBindingAllowed()) {
			showHideCompoundBindingsButton = new MouseOverButton();
			showHideCompoundBindingsButton.setBorder(BorderFactory.createEmptyBorder());
			showHideCompoundBindingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
						bindingSelector.activateNormalBindingMode();
					} else {
						bindingSelector.activateCompoundBindingMode();
					}
				}
			});

			JLabel showHideCompoundBindingsButtonLabel = new JLabel("", SwingConstants.RIGHT);
			showHideCompoundBindingsButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				showHideCompoundBindingsButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_TOP_SELECTED_ICON);
				showHideCompoundBindingsButton.setToolTipText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
						"specify_basic_binding"));
				showHideCompoundBindingsButtonLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
						"specify_basic_binding") + "  ");
			} else {
				showHideCompoundBindingsButton.setNormalIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_ICON);
				showHideCompoundBindingsButton.setMouseOverIcon(FIBIconLibrary.TOGGLE_ARROW_BOTTOM_SELECTED_ICON);
				showHideCompoundBindingsButton.setToolTipText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
						"specify_compound_binding"));
				showHideCompoundBindingsButtonLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION,
						"specify_compound_binding") + "  ");
			}

			JPanel showHideCompoundBindingsButtonPanel = new JPanel();
			showHideCompoundBindingsButtonPanel.setLayout(new BorderLayout());
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButtonLabel, BorderLayout.CENTER);
			showHideCompoundBindingsButtonPanel.add(showHideCompoundBindingsButton, BorderLayout.EAST);

			optionsPanel.add(showHideCompoundBindingsButtonPanel, BorderLayout.EAST);
		}

		if (bindingSelector.areStaticValuesAllowed()) {
			JPanel optionsWestPanel = new JPanel();
			optionsWestPanel.setLayout(new VerticalLayout());
			staticBindingPanel = new ConstantValuePanel(this);
			optionsWestPanel.add(staticBindingPanel);
			optionsPanel.add(optionsWestPanel, BorderLayout.WEST);
		}

		currentTypeLabel = new JLabel(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type"), SwingConstants.LEFT);
		currentTypeLabel.setFont(new Font("SansSerif", Font.ITALIC, 10));
		currentTypeLabel.setForeground(Color.GRAY);

		searchedTypeLabel = new JLabel("[" + FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type") + "]",
				SwingConstants.LEFT);
		searchedTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
		searchedTypeLabel.setForeground(Color.RED);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(currentTypeLabel, BorderLayout.CENTER);
		labelPanel.add(searchedTypeLabel, BorderLayout.EAST);

		JComponent topPane;

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			topPane = new JPanel();
			topPane.setLayout(new BorderLayout());
			bindingValueRepresentation = new JTextArea(3, 80);
			bindingValueRepresentation.setFont(new Font("SansSerif", Font.PLAIN, 10));
			bindingValueRepresentation.setEditable(false);
			bindingValueRepresentation.setLineWrap(true);
			topPane.add(bindingValueRepresentation, BorderLayout.CENTER);
			topPane.add(labelPanel, BorderLayout.SOUTH);
		} else {
			topPane = labelPanel;
		}

		add(topPane, BorderLayout.NORTH);

		JComponent middlePane;

		// logger.info("Rebuild middle pane, with mode="+editionMode);

		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			middlePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(browserPanel,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
					getMethodCallBindingsPanel()); // ICI
			((JSplitPane) middlePane).setDividerLocation(0.5);
			((JSplitPane) middlePane).setResizeWeight(0.5);
		} else { // For NORMAL_BINDING and STATIC_BINDING
			middlePane = new JScrollPane(browserPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // ICI
		}

		JPanel middlePaneWithOptions = new JPanel();
		middlePaneWithOptions.setLayout(new BorderLayout());
		middlePaneWithOptions.add(middlePane, BorderLayout.CENTER);
		if (bindingSelector.areStaticValuesAllowed() || bindingSelector.areCompoundBindingAllowed()) {
			middlePaneWithOptions.add(optionsPanel, BorderLayout.SOUTH);
		}

		add(middlePaneWithOptions, BorderLayout.CENTER);
		add(_controlPanel, BorderLayout.SOUTH);

		resetMethodCallPanel();

		// Init static panel
		editStaticValue = true;
		setEditStaticValue(false);

		update();
		FilteredJList firstList = listAtIndex(0);
		if (firstList != null && firstList.getModel().getSize() == 1) {
			firstList.setSelectedIndex(0);
		}
	}

	protected void updateSearchedTypeLabel() {
		searchedTypeLabel.setText("[" + getTypeStringRepresentation() + "]");
	}

	private String getTypeStringRepresentation() {
		if (bindingSelector.getEditedObject() == null || bindingSelector.getEditedObject().getDeclaredType() == null) {
			return FlexoLocalization.localizedForKey("no_type");
		} else {
			return TypeUtils.simpleRepresentation(bindingSelector.getEditedObject().getDeclaredType());
		}
	}

	protected int getVisibleColsCount() {
		return _lists.size();
	}

	public boolean ensureBindingValueExists() {
		if (bindingSelector.getEditedObject() == null) {
			return false;
		}
		if (bindingSelector.getEditedObject().getExpression() == null) {
			bindingSelector.getEditedObject().setExpression(bindingSelector.makeBinding());
			bindingSelector.fireEditedObjectChanged();
		}
		return true;
	}

	protected class FilteredJList extends JList {
		public FilteredJList() {
			super(new EmptyColumnListModel());
		}

		public String getFilter() {
			return getModel().getFilter();
		}

		public void setFilter(String aFilter) {
			getModel().setFilter(aFilter);
		}

		public boolean isFiltered() {
			return StringUtils.isNotEmpty(getFilter());
		}

		@Override
		public void setModel(ListModel model) {
			if (model != null && !(model instanceof BindingColumnListModel)) {
				new Exception("Oops, this model is " + model).printStackTrace();
			}
			setFilter(null);
			if (model != null) {
				super.setModel(model);
			}
		}

		@Override
		public BindingColumnListModel getModel() {
			if (super.getModel() instanceof BindingColumnListModel) {
				return (BindingColumnListModel) super.getModel();
			} else {
				new Exception("Oops, got a " + super.getModel()).printStackTrace();
				return null;
			}
		}
	}

	protected JList makeNewJList() {
		FilteredJList newList = new FilteredJList();

		TypeResolver typeResolver = new TypeResolver(newList);

		newList.addMouseMotionListener(typeResolver);
		newList.addMouseListener(typeResolver);

		_lists.add(newList);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makeNewJList() size = " + _lists.size());
		}
		newList.setPrototypeCellValue("123456789012345"); // ICI
		newList.setSize(new Dimension(100, 150));
		// newList.setPreferredSize(new Dimension(200,150)); // ICI
		// newList.setMinimumSize(new Dimension(200,150)); // ICI
		newList.setCellRenderer(new BindingSelectorCellRenderer());
		newList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		newList.addListSelectionListener(this);
		newList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isValid()) {
						bindingSelector.apply();
					}
				} else if (e.getClickCount() == 1) {
					// Trying to update MethodCall Panel
					JList list = (JList) e.getSource();
					int index = _lists.indexOf(list);
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Click on index " + index);
					}
					if (index < 0) {
						return;
					}
					_selectedPathElementIndex = index;
					updateMethodCallPanel();
				}
			}
		});

		newList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// processAnyKeyTyped(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					bindingSelector._selectorPanel.processEnterPressed();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					bindingSelector._selectorPanel.processBackspace();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					bindingSelector._selectorPanel.processDelete();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					DataBinding dataBinding = bindingSelector.getEditedObject();
					if (dataBinding.isBindingValue()) {
						int i = _lists.indexOf(e.getSource());
						if (i > -1 && i < _lists.size() && listAtIndex(i + 1) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0).getElement() instanceof BindingPathElement) {
							((BindingValue) dataBinding.getExpression()).setBindingPathElementAtIndex(listAtIndex(i + 1).getModel()
									.getElementAt(0).getElement(), i);
							bindingSelector.setEditedObject(dataBinding);
							bindingSelector.fireEditedObjectChanged();
							listAtIndex(i + 1).requestFocusInWindow();
						}
						e.consume();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					DataBinding dataBinding = bindingSelector.getEditedObject();
					if (dataBinding.isBindingValue()) {
						int i = _lists.indexOf(e.getSource()) - 1;
						if (((BindingValue) dataBinding.getExpression()).getBindingPath().size() > i && i > -1 && i < _lists.size()) {
							((BindingValue) dataBinding.getExpression()).removeBindingPathAt(i);
							// ((BindingValue) dataBinding.getExpression()).disconnect();
							// _bindingSelector.disconnect();
							bindingSelector.setEditedObject(dataBinding);
							bindingSelector.fireEditedObjectChanged();
							listAtIndex(i).requestFocusInWindow();
						}
						e.consume();
					}
				}
			}

		});

		browserPanel.add(new JScrollPane(newList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)); // ICI
		newList.setVisibleRowCount(6);
		revalidate();
		repaint();
		return newList;
	}

	int _selectedPathElementIndex = -1;

	protected void resetMethodCallPanel() {
		if (bindingSelector.getEditedObject() == null || bindingSelector.getEditedObject().isConstant()
				|| bindingSelector.getEditedObject().isBindingValue()
				&& ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPath().size() == 0) {
			_selectedPathElementIndex = -1;
		} else if (bindingSelector.getEditedObject().isBindingValue()) {
			_selectedPathElementIndex = ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPath().size();
		}
		updateMethodCallPanel();
	}

	void updateMethodCallPanel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateMethodCallPanel with " + bindingSelector.editionMode + " binding=" + bindingSelector.getEditedObject()
					+ " _selectedPathElementIndex=" + _selectedPathElementIndex);
		}
		if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING && bindingSelector.getEditedObject().isBindingValue()) {
			// logger.info("On se met le method call panel a jour...");
			if (((BindingValue) bindingSelector.getEditedObject().getExpression()).isCompoundBinding() && _selectedPathElementIndex == -1) {
				_selectedPathElementIndex = ((BindingValue) bindingSelector.getEditedObject().getExpression()).getBindingPathElementCount();
			}
			if (_selectedPathElementIndex >= _lists.size()) {
				_selectedPathElementIndex = -1;
			}
			BindingValue bindingValue = (BindingValue) bindingSelector.getEditedObject().getExpression();
			if (bindingValue == null) {
				_selectedPathElementIndex = -1;
			} else if (_selectedPathElementIndex > bindingValue.getBindingPath().size()) {
				_selectedPathElementIndex = -1;
			}
			// logger.info("Ici avec _selectedPathElementIndex=" + _selectedPathElementIndex + " bindingValue=" + bindingValue);
			if (_selectedPathElementIndex > -1 && bindingValue != null) {
				JList list = _lists.get(_selectedPathElementIndex);
				int newSelectedIndex = list.getSelectedIndex();
				if (newSelectedIndex > 0) {
					// logger.info("newSelectedIndex=" + newSelectedIndex);
					BindingColumnElement selectedValue = (BindingColumnElement) list.getSelectedValue();
					// logger.info("selectedValue.getElement()=" + selectedValue.getElement() + " of " +
					// selectedValue.getElement().getClass());
					if (selectedValue.getElement() instanceof FunctionPathElement) {
						BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(_selectedPathElementIndex - 1);
						if (currentElement instanceof FunctionPathElement
								&& ((FunctionPathElement) currentElement).getFunction() != null
								&& ((FunctionPathElement) currentElement).getFunction().equals(
										((FunctionPathElement) selectedValue.getElement()).getFunction())) {
							// logger.info("On y arrive");
							getMethodCallBindingsModel().setModel((FunctionPathElement) currentElement);
							return;
						} else {
							// logger.info("On y arrive pas");
						}
					}
				}
			}
			getMethodCallBindingsModel().setModel(null);
			return;
		}
	}

	protected void deleteJList(JList list) {
		_lists.remove(list);
		Component[] scrollPanes = browserPanel.getComponents();
		for (int i = 0; i < scrollPanes.length; i++) {
			if (((Container) scrollPanes[i]).isAncestorOf(list)) {
				browserPanel.remove(scrollPanes[i]);
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("deleteJList() size = " + _lists.size());
		}
		revalidate();
		repaint();
	}

	protected FilteredJList listAtIndex(int index) {
		if (index >= 0 && index < _lists.size()) {
			return _lists.elementAt(index);
		}
		return null;
	}

	// TODO ???
	/*
	 * public void setBindingDefinition(BindingDefinition bindingDefinition) {
	 * if (bindingDefinition != getBindingDefinition()) {
	 * super.setBindingDefinition(bindingDefinition);
	 * staticBindingPanel.updateStaticBindingPanel(); } }
	 */

	@Override
	protected void fireBindableChanged() {
		_rootBindingColumnListModel = buildRootColumnListModel();
		update();
	}

	/*@Override
	protected void fireBindingDefinitionChanged() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireBindingDefinitionChanged / Setting new binding definition: " + bindingSelector.getBindingDefinition());
		}

		update();

		if (staticBindingPanel != null) {
			staticBindingPanel.updateConstantValuePanel();
		}

	}*/

	private void clearColumns() {
		listAtIndex(0).setModel(bindingSelector.getRootListModel());
		int lastUpdatedList = 0;
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}
	}

	@Override
	protected void update() {
		DataBinding binding = bindingSelector.getEditedObject();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update with " + binding);
		}

		// logger.info("Update in BindingSelectorPanel with binding " + binding);

		if (binding == null || binding.isConstant()) {
			clearColumns();
			if (binding == null) {
				setEditStaticValue(false);
			}
		} else if (binding.isBindingValue()) {
			BindingValue bindingValue = (BindingValue) binding.getExpression();
			listAtIndex(0).setModel(bindingSelector.getRootListModel());
			int lastUpdatedList = 0;

			// logger.info("bindingValue.getBindingVariable()="+bindingValue.getBindingVariable());

			if (bindingValue.getBindingVariable() != null) {
				if (bindingValue.getBindingVariable().getType() != null) {
					listAtIndex(1)
							.setModel(
									bindingSelector.getListModelFor(bindingValue.getBindingVariable(), bindingValue.getBindingVariable()
											.getType()));
				} else {
					listAtIndex(1).setModel(EMPTY_MODEL);
				}
				listAtIndex(0).removeListSelectionListener(this);
				BindingColumnElement elementToSelect = listAtIndex(0).getModel().getElementFor(bindingValue.getBindingVariable());

				listAtIndex(0).setSelectedValue(elementToSelect, true);
				listAtIndex(0).addListSelectionListener(this);
				lastUpdatedList = 1;
				for (int i = 0; i < bindingValue.getBindingPath().size(); i++) {
					BindingPathElement pathElement = bindingValue.getBindingPath().get(i);
					if (i + 2 == getVisibleColsCount()) {
						final JList l = makeNewJList();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Rectangle r = SwingUtilities.convertRectangle(l, l.getBounds(), browserPanel);
								// System.out.println("scrollRectToVisible with "+r);
								browserPanel.scrollRectToVisible(r); // ICI
							}
						});
					}
					/*
					 * if (i==bindingValue.getBindingPath().size()-1) {
					 * logger.info("Dernier element: "+pathElement); }
					 */

					if (!(bindingValue.isValid() && bindingValue.isLastBindingPathElement(pathElement/*, i*/) && bindingSelector
							.isConnected())) {
						Type resultingType = bindingValue.getBindingPath().get(i).getType();
						listAtIndex(i + 2).setModel(bindingSelector.getListModelFor(bindingValue.getBindingPath().get(i), resultingType));
						lastUpdatedList = i + 2;
					}
					listAtIndex(i + 1).removeListSelectionListener(this);

					BindingColumnElement theElementToSelect = listAtIndex(i + 1).getModel().getElementFor(pathElement);
					listAtIndex(i + 1).setSelectedValue(theElementToSelect, true);

					/*
					 * if (pathElement instanceof KeyValueProperty) {
					 * BindingColumnElement propertyElementToSelect =
					 * listAtIndex(i+1).getModel().getElementFor(pathElement);
					 * listAtIndex(i +
					 * 1).setSelectedValue(propertyElementToSelect, true); }
					 * else if (pathElement instanceof MethodCall) {
					 * BindingColumnElement methodElementToSelect =
					 * listAtIndex(i
					 * +1).getModel().getElementFor(((MethodCall)pathElement
					 * ).getMethodDefinition()); listAtIndex(i +
					 * 1).setSelectedValue(methodElementToSelect, true); if
					 * (methodElementToSelect == null) {
					 * logger.warning("Unexpected NULL BindingColumnElement"); }
					 * /
					 * /logger.info("Set selected value with "+methodElementToSelect
					 * ); }
					 */

					listAtIndex(i + 1).addListSelectionListener(this);
					if (i < bindingValue.getBindingPath().size() - 1) {
						listAtIndex(i).setFilter(null);
					}
				}
				// logger.info("FIN");
			}

			// Remove and clean unused lists
			cleanLists(lastUpdatedList);

			if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING && bindingValueRepresentation != null) {
				bindingValueRepresentation.setText(bindingSelector.renderedString(binding));
				bindingValueRepresentation.setForeground(bindingValue.isValid() ? Color.BLACK : Color.RED);
				updateMethodCallPanel();
			}

			// currentTypeLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type"));
			// currentTypeLabel.setToolTipText(null);

		}

		updateSearchedTypeLabel();

		if (binding != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + binding + " isValid()=" + binding.isValid());
			} else if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding is null");
			}
		}

		// Set connect button state
		connectButton.setEnabled(binding != null && binding.isValid());
		/*if (!binding.isBindingValid()) {
			logger.info("Binding NOT valid: " + binding);
			binding.debugIsBindingValid();
		}*/
		if (binding != null && binding.isValid()) {
			if (ToolBox.isMacOSLaf()) {
				connectButton.setSelected(true);
			}
		}
		if (binding != null) {
			bindingSelector.getTextField().setForeground(binding.isValid() ? Color.BLACK : Color.RED);
			bindingSelector.getTextField().setSelectedTextColor(binding.isValid() ? Color.BLACK : Color.RED);

			if (bindingSelector.areStaticValuesAllowed() && staticBindingPanel != null) {
				staticBindingPanel.updateConstantValuePanel();
			}

			if (binding.isBindingValue()) {
				setEditStaticValue(false);
			} else if (binding.isConstant()) {
				setEditStaticValue(true);
			}
		}
	}

	private void cleanLists(int lastUpdatedList) {
		// Remove unused lists
		int lastVisibleList = defaultVisibleColCount - 1;
		if (lastUpdatedList > lastVisibleList) {
			lastVisibleList = lastUpdatedList;
		}
		int currentSize = getVisibleColsCount();
		for (int i = lastVisibleList + 1; i < currentSize; i++) {
			JList toRemove = listAtIndex(getVisibleColsCount() - 1);
			deleteJList(toRemove);
		}
		// Sets model to null for visible but unused lists
		for (int i = lastUpdatedList + 1; i < getVisibleColsCount(); i++) {
			JList list = listAtIndex(i);
			list.setModel(EMPTY_MODEL);
		}

	}

	private boolean editStaticValue;

	boolean getEditStaticValue() {
		return editStaticValue;
	}

	void setEditStaticValue(boolean aFlag) {
		if (!bindingSelector.areStaticValuesAllowed() || staticBindingPanel == null) {
			return;
		}
		if (editStaticValue != aFlag) {
			editStaticValue = aFlag;
			if (editStaticValue) {
				staticBindingPanel.enableStaticBindingPanel();
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(false);
				}
			} else {
				staticBindingPanel.disableStaticBindingPanel();
				// _bindingSelector.setEditedObject(null,false);
				for (int i = 0; i < getVisibleColsCount(); i++) {
					listAtIndex(i).setEnabled(true);
				}
				// logger.info("bindable="+getBindable()+" bm="+getBindingModel());
				_rootBindingColumnListModel = buildRootColumnListModel();
				// if (listAtIndex(0).getModel() instanceof
				// EmptyColumnListModel) {
				listAtIndex(0).setModel(bindingSelector.getRootListModel());
				// }
			}
			staticBindingPanel.updateConstantValuePanel();
		}
	}

	private boolean editTranstypedBinding;

	boolean getEditTranstypedBinding() {
		return editTranstypedBinding;
	}

	protected BindingColumnListModel getRootColumnListModel() {
		if (_rootBindingColumnListModel == null) {
			_rootBindingColumnListModel = buildRootColumnListModel();
		}
		return _rootBindingColumnListModel;
	}

	protected BindingColumnListModel buildRootColumnListModel() {
		if (bindingSelector.getBindingModel() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("buildRootColumnListModel() from " + bindingSelector.getBindingModel());
			}
			return new RootBindingColumnListModel(bindingSelector.getBindingModel());
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("buildRootColumnListModel(): EMPTY_MODEL");
		}
		return EMPTY_MODEL;
	}

	// public void refreshColumListModel(DMType type){
	// _listModels.remove(type);
	// getColumnListModel(type);
	// }

	protected BindingColumnListModel getColumnListModel(BindingPathElement element, Type resultingType) {
		if (element == null) {
			return EMPTY_MODEL;
		}
		if (TypeUtils.isResolved(element.getType())) {
			Hashtable<Type, BindingColumnListModel> h = _listModels.get(element);
			if (h == null) {
				h = new Hashtable<Type, BindingColumnListModel>();
				_listModels.put(element, h);
			}
			BindingColumnListModel returned = h.get(resultingType);
			if (returned == null) {
				returned = makeColumnListModel(element, resultingType);
				h.put(resultingType, returned);
			}
			return returned;
		} else {
			return EMPTY_MODEL;
		}
	}

	protected BindingColumnListModel makeColumnListModel(BindingPathElement element, Type resultingType) {
		return new NormalBindingColumnListModel(element, resultingType);
	}

	protected class BindingColumnElement {
		private BindingPathElement _element;
		private Type _resultingType;

		protected BindingColumnElement(BindingPathElement element, Type resultingType) {
			_element = element;
			_resultingType = resultingType;
			if (resultingType == null) {
				logger.warning("make BindingColumnElement with null type !");
			}
		}

		public BindingPathElement getElement() {
			return _element;
		}

		public Type getResultingType() {
			return _resultingType;
		}

		public String getLabel() {
			return getElement().getLabel();
			/*
			 * if (getElement() != null && getElement() instanceof
			 * BindingVariable) { return
			 * ((BindingVariable)getElement()).getVariableName(); } else if
			 * (getElement() != null && getElement() instanceof
			 * KeyValueProperty) { return ((KeyValueProperty)
			 * getElement()).getName(); } else if (getElement() != null &&
			 * getElement() instanceof MethodDefinition) { MethodDefinition
			 * method = (MethodDefinition) getElement(); return
			 * method.getSimplifiedSignature(); }
			 */
			// return "???";
		}

		public String getTypeStringRepresentation() {
			if (getResultingType() == null) {
				return FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type");
			} else {
				return TypeUtils.simpleRepresentation(getResultingType());
			}
		}

		public String getTooltipText() {
			return getElement().getTooltipText(getResultingType());

			/*
			 * if (getElement() instanceof BindingVariable) { return
			 * getTooltipText((BindingVariable)getElement()); } else if
			 * (getElement() instanceof KeyValueProperty) { return
			 * getTooltipText
			 * ((KeyValueProperty)getElement(),getResultingType()); } else if
			 * (getElement() instanceof MethodDefinition) { return
			 * getTooltipText
			 * ((MethodDefinition)getElement(),getResultingType()); } else
			 * return "???";
			 */
		}

		/*
		 * private String getTooltipText(BindingVariable bv) { String returned =
		 * "<html>"; String resultingTypeAsString; if (bv.getType()!=null) {
		 * resultingTypeAsString = TypeUtils.simpleRepresentation(bv.getType());
		 * resultingTypeAsString = ToolBox.replaceStringByStringInString("<",
		 * "&LT;", resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+bv.getVariableName()+"</b></p>";
		 * //returned +=
		 * "<p><i>"+(bv.getDescription()!=null?bv.getDescription():
		 * FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		 * returned += "</html>"; return returned; }
		 */

		/*
		 * private String getTooltipText(KeyValueProperty property, Type
		 * resultingType) { String returned = "<html>"; String
		 * resultingTypeAsString; if (resultingType!=null) {
		 * resultingTypeAsString =
		 * TypeUtils.simpleRepresentation(resultingType); resultingTypeAsString
		 * = ToolBox.replaceStringByStringInString("<", "&LT;",
		 * resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+property.getName()+"</b></p>";
		 * //returned +=
		 * "<p><i>"+(property.getDescription()!=null?property.getDescription
		 * ():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		 * returned += "</html>"; return returned; }
		 */

		/*
		 * private String getTooltipText(MethodDefinition method, Type
		 * resultingType) { String returned = "<html>"; String
		 * resultingTypeAsString; if (resultingType!=null) {
		 * resultingTypeAsString =
		 * TypeUtils.simpleRepresentation(resultingType); resultingTypeAsString
		 * = ToolBox.replaceStringByStringInString("<", "&LT;",
		 * resultingTypeAsString); resultingTypeAsString =
		 * ToolBox.replaceStringByStringInString(">", "&GT;",
		 * resultingTypeAsString); } else { resultingTypeAsString = "???"; }
		 * returned +=
		 * "<p><b>"+resultingTypeAsString+" "+method.getSimplifiedSignature
		 * ()+"</b></p>"; //returned +=
		 * "<p><i>"+(method.getDescription()!=null?method
		 * .getDescription():FlexoLocalization
		 * .localizedForKey("no_description"))+"</i></p>"; returned +=
		 * "</html>"; return returned; }
		 */

		@Override
		public String toString() {
			return "BindingColumnElement/" + getLabel() + "[" + _element.toString() + "]";
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BindingColumnElement) {
				BindingColumnElement bce = (BindingColumnElement) obj;
				if (_element == null) {
					return false;
				}
				if (_resultingType == null) {
					return false;
				}
				return _element.equals(bce._element) && _resultingType.equals(bce._resultingType);
			} else {
				return super.equals(obj);
			}
		}

	}

	abstract class BindingColumnListModel extends AbstractListModel {
		public void fireModelChanged() {
			fireContentsChanged(this, 0, getUnfilteredSize() - 1);
		}

		public BindingColumnElement getElementFor(BindingPathElement element) {
			// logger.info("getElementFor() " + element + " of " + element.getClass());
			/*if (element instanceof MethodCall) {
				element = ((MethodCall) element).getMethodDefinition();
			}*/
			for (int i = 0; i < getSize(); i++) {
				// logger.info("getElementAt(i)=" + getElementAt(i).getElement() + " of " + getElementAt(i).getElement().getClass());
				if (getElementAt(i).getElement().equals(element)) {
					return getElementAt(i);
				}
				if (element instanceof FunctionPathElement && getElementAt(i).getElement() instanceof FunctionPathElement) {
					// Special equals, we try to find a FunctionPathElement even if parameters are different
					FunctionPathElement f1 = (FunctionPathElement) element;
					FunctionPathElement f2 = (FunctionPathElement) getElementAt(i).getElement();
					if (f1.getFunction() != null && f1.getFunction().equals(f2.getFunction())) {
						return getElementAt(i);
					}

				}
			}
			logger.info("I cannot find " + element + " of " + (element != null ? element.getClass() : null));
			/*for (int i = 0; i < getSize(); i++) {
				logger.info("Looking with " + getElementAt(i).getElement() + " of "
						+ (getElementAt(i).getElement() != null ? getElementAt(i).getElement().getClass() : null));
			}*/

			return null;
		}

		public void updateValues() {
		}

		private String filter = null;

		public String getFilter() {
			return filter;
		}

		public void setFilter(String aFilter) {
			filter = aFilter;
			fireModelChanged();
		}

		@Override
		public int getSize() {
			if (getFilter() == null && !bindingSelector._hideFilteredObjects) {
				return getUnfilteredSize();
			}
			int returned = 0;
			if (!bindingSelector._hideFilteredObjects) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						returned++;
					}
				}
			} else if (getFilter() == null) {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			} else {
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						returned++;
					}
				}
			}
			return returned;
		}

		@Override
		public BindingColumnElement getElementAt(int index) {
			if (getFilter() == null && !bindingSelector._hideFilteredObjects) {
				return getUnfilteredElementAt(index);
			}
			if (!bindingSelector._hideFilteredObjects) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter)) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			} else if (getFilter() == null) {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (!isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			} else {
				int searchedIndex = -1;
				for (int i = 0; i < getUnfilteredSize(); i++) {
					if (getUnfilteredElementAt(i).getLabel().startsWith(filter) && !isFiltered(getUnfilteredElementAt(i))) {
						searchedIndex++;
					}
					if (searchedIndex == index) {
						return getUnfilteredElementAt(i);
					}
				}
			}
			return null;
		}

		private boolean isFiltered(BindingColumnElement columnElement) {
			// Class resultingTypeBaseClass =
			// TypeUtils.getBaseClass(columnElement.getResultingType());
			Type resultingType = columnElement.getResultingType();

			if (columnElement.getElement() != null && columnElement.getElement() instanceof BindingVariable) {
				BindingVariable bv = (BindingVariable) columnElement.getElement();
				if (bv.getType() == null) {
					return true;
				}
			} else if (columnElement.getElement() != null) {
				DataBinding binding = bindingSelector.getEditedObject();
				if (binding != null && binding.isBindingValue()) {
					BindingValue bindingValue = (BindingValue) binding.getExpression();
					if (bindingValue.isValid()
							&& bindingValue.isLastBindingPathElement(columnElement.getElement()/*, getIndexOfList(this) - 1*/)
							&& bindingSelector.isConnected()) {
						// setIcon(label, CONNECTED_ICON, list);
					} else if (columnElement.getResultingType() != null) {
						if (TypeUtils.isResolved(columnElement.getResultingType()) && bindingSelector.getBindable() != null) {
							// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
							if (bindingSelector.getBindable().getBindingFactory()
									.getAccessibleSimplePathElements(columnElement.getElement()).size() > 0) {
							} else {
								if (!TypeUtils.isTypeAssignableFrom(binding.getDeclaredType(), columnElement.getResultingType(), true)) {
									return true;
								}
								if (binding.isSettable() && !columnElement.getElement().isSettable()) {
									return true;
								}
							}
						}
					}
				}
			}/*
				* else if (columnElement.getElement() != null &&
				* columnElement.getElement() instanceof KeyValueProperty) {
				* KeyValueProperty property = (KeyValueProperty)
				* columnElement.getElement(); AbstractBinding binding =
				* _bindingSelector.getEditedObject(); if
				* ((binding != null) && binding instanceof BindingValue) {
				* BindingValue bindingValue = (BindingValue)binding; if
				* (bindingValue.isConnected() &&
				* (bindingValue.isLastBindingPathElement(property,
				* getIndexOfList(this) - 1))) { //setIcon(label, CONNECTED_ICON,
				* list); } else if (columnElement.getResultingType() != null) { if
				* (TypeUtils.isResolved(columnElement.getResultingType()) &&
				* _bindingSelector.getBindable() != null) { if
				* (_bindingSelector.getBindable
				* ().getBindingFactory().getAccessibleBindingPathElements
				* (columnElement.getElement()).size() > 0) { //if
				* (KeyValueLibrary.getAccessibleProperties(resultingType).size() >
				* 0) { //setIcon(label, ARROW_RIGHT_ICON, list); } else { if
				* ((_bindingSelector.getBindingDefinition() != null) &&
				* (_bindingSelector.getBindingDefinition().getType() != null) &&
				* (!TypeUtils
				* .isTypeAssignableFrom(_bindingSelector
				* .getBindingDefinition
				* ().getType(),columnElement.getResultingType(),true))) { return
				* true; } if ((_bindingSelector.getBindingDefinition() != null) &&
				* (_bindingSelector.getBindingDefinition().getIsSettable()) &&
				* !property.isSettable()) { return true; } } } } } } else if
				* (columnElement.getElement() != null && columnElement.getElement()
				* instanceof MethodDefinition) { MethodDefinition method =
				* (MethodDefinition) columnElement.getElement();
				* 
				* String methodAsString = method.getSimplifiedSignature(); int idx
				* = getIndexOfList(this); if (idx > 0 &&
				* _lists.elementAt(idx-1).getSelectedValue()!=null) { Type context
				* =
				* ((BindingColumnElement)_lists.elementAt(idx-1).getSelectedValue(
				* )).getResultingType(); methodAsString =
				* method.getSimplifiedSignature();
				* //method.getSimplifiedSignatureInContext(context); }
				* 
				* AbstractBinding binding = _bindingSelector.getEditedObject(); if
				* (binding instanceof BindingValue) { BindingValue bindingValue =
				* (BindingValue)binding; BindingPathElement bpe =
				* bindingValue.getBindingPathElementAtIndex(getIndexOfList(this) -
				* 1); if ((bindingValue.isConnected()) &&
				* (bindingValue.isLastBindingPathElement(bpe, getIndexOfList(this)
				* - 1)) && ((bpe instanceof MethodCall) &&
				* (((MethodCall)bpe).getMethod().equals(method.getMethod())))) { }
				* else if (columnElement.getResultingType() != null &&
				* resultingType != null && _bindingSelector.getBindable() != null)
				* { if(_bindingSelector.getBindable().getBindingFactory().
				* getAccessibleBindingPathElements
				* (columnElement.getElement()).size() +
				* _bindingSelector.getBindable
				* ().getBindingFactory().getAccessibleCompoundBindingPathElements
				* (columnElement.getElement()).size() > 0) { //if
				* (KeyValueLibrary.getAccessibleProperties(resultingType).size() //
				* + KeyValueLibrary.getAccessibleMethods(resultingType).size() > 0)
				* { } else { if ((_bindingSelector.getBindingDefinition() != null)
				* && (_bindingSelector.getBindingDefinition().getType() != null &&
				* TypeUtils
				* .getBaseClass(_bindingSelector.getBindingDefinition().getType
				* ())!=null) &&
				* (!TypeUtils.isClassAncestorOf(TypeUtils.getBaseClass
				* (_bindingSelector
				* .getBindingDefinition().getType()),TypeUtils.getBaseClass
				* (resultingType)))) { return true; } } } } }
				*/
			return false;
		}

		public abstract int getUnfilteredSize();

		public abstract BindingColumnElement getUnfilteredElementAt(int index);

	}

	private class NormalBindingColumnListModel extends BindingColumnListModel implements Observer {
		private Type _type;
		private BindingPathElement _element;
		private Vector<BindingPathElement> _accessibleProperties;
		private Vector<BindingPathElement> _accessibleMethods;
		private Vector<BindingColumnElement> _elements;

		NormalBindingColumnListModel(BindingPathElement element, Type resultingType) {
			super();
			_element = element;
			_type = resultingType;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Build NormalBindingColumnListModel for " + element + " base class=" + TypeUtils.getBaseClass(_type));
			}
			_accessibleProperties = new Vector<BindingPathElement>();
			_accessibleMethods = new Vector<BindingPathElement>();
			_elements = new Vector<BindingColumnElement>();
			/*
			 * if(_type.getBaseEntity()!=null)
			 * _type.getBaseEntity().addObserver(this);
			 */
			updateValues();
		}

		@Override
		public void updateValues() {
			_accessibleProperties.clear();
			_accessibleMethods.clear();

			if (bindingSelector.getBindable() == null) {
				return;
			}

			for (BindingColumnElement bce : _elements) {
				if (bce.getElement() instanceof Observable) {
					((Observable) bce.getElement()).deleteObserver(this);
				}
			}
			_elements.clear();
			if (TypeUtils.getBaseClass(_type) == null) {
				return;
			}
			// _accessibleProperties.addAll(KeyValueLibrary.getAccessibleProperties(_type));
			_accessibleProperties.addAll(bindingSelector.getBindable().getBindingFactory().getAccessibleSimplePathElements(_element));
			// _accessibleProperties.addAll(_element.getAccessibleBindingPathElements());

			if (bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				// _accessibleMethods.addAll(KeyValueLibrary.getAccessibleMethods(_type));
				_accessibleProperties.addAll(bindingSelector.getBindable().getBindingFactory().getAccessibleFunctionPathElements(_element));
				// _accessibleProperties.addAll(_element.getAccessibleCompoundBindingPathElements());
			}

			for (BindingPathElement p : _accessibleProperties) {
				_elements.add(new BindingColumnElement(p, TypeUtils.makeInstantiatedType(p.getType(), _type)));
			}
			for (BindingPathElement m : _accessibleMethods) {
				_elements.add(new BindingColumnElement(m, TypeUtils.makeInstantiatedType(m.getType(), _type)));
			}
			for (BindingColumnElement bce : _elements) {
				if (bce.getElement() instanceof Observable) {
					((Observable) bce.getElement()).addObserver(this);
				}
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			if (index < _elements.size() && index >= 0) {
				return _elements.elementAt(index);
			}
			return null;
		}

		@Override
		public void update(Observable observable, Object dataModification) {
		}

	}

	class EmptyColumnListModel extends BindingColumnListModel {
		@Override
		public int getUnfilteredSize() {
			return 0;
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return null;
		}

	}

	private class RootBindingColumnListModel extends BindingColumnListModel {
		private BindingModel _myBindingModel;
		private Vector<BindingColumnElement> _elements;

		RootBindingColumnListModel(BindingModel bindingModel) {
			super();
			_myBindingModel = bindingModel;
			_elements = new Vector<BindingColumnElement>();
			updateValues();
		}

		@Override
		public void updateValues() {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("BindingModel is: " + _myBindingModel + " with " + _myBindingModel.getBindingVariablesCount());
			}

			_elements.clear();
			for (int i = 0; i < _myBindingModel.getBindingVariablesCount(); i++) {
				_elements.add(new BindingColumnElement(_myBindingModel.getBindingVariableAt(i), _myBindingModel.getBindingVariableAt(i)
						.getType()));
			}
		}

		@Override
		public int getUnfilteredSize() {
			return _elements.size();
		}

		@Override
		public BindingColumnElement getUnfilteredElementAt(int index) {
			return _elements.elementAt(index);
		}

		@Override
		public String toString() {
			return "RootBindingColumnListModel with " + getSize() + " elements";
		}

	}

	protected class TypeResolver extends MouseAdapter implements MouseMotionListener {

		private JList list;

		protected TypeResolver(JList aList) {
			currentFocused = null;
			list = aList;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			displayLabel(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			displayLabel(e);
		}

		private void displayLabel(MouseEvent e) {

			// Get item index
			int index = list.locationToIndex(e.getPoint());

			// Get item
			if (index < 0 || index >= list.getModel().getSize()) {
				return;
			}
			BindingColumnElement item = ((BindingColumnListModel) list.getModel()).getElementAt(index);

			if (item != currentFocused) {
				currentFocused = item;
				currentTypeLabel.setText(currentFocused.getTypeStringRepresentation());
			}
		}

	}

	protected class BindingSelectorCellRenderer extends DefaultListCellRenderer {

		private JPanel panel;
		private JLabel iconLabel;

		public BindingSelectorCellRenderer() {
			panel = new JPanel(new BorderLayout());
			iconLabel = new JLabel();
			panel.add(iconLabel, BorderLayout.EAST);
			panel.add(this);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object bce, int index, boolean isSelected, boolean cellHasFocus) {
			JComponent returned = (JComponent) super.getListCellRendererComponent(list, bce, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setToolTipText(null);
				iconLabel.setVisible(false);

				if (bce instanceof BindingColumnElement) {
					BindingColumnElement columnElement = (BindingColumnElement) bce;
					// Class resultingTypeBaseClass =
					// TypeUtils.getBaseClass(columnElement.getResultingType());
					Type resultingType = columnElement.getResultingType();
					label.setText(columnElement.getLabel());
					// if (!(columnElement.getElement() instanceof FinalBindingPathElement)) {
					returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
					// }
					if (columnElement.getElement().getType() != null) {
						returned.setToolTipText(columnElement.getTooltipText());
					} else {
						label.setForeground(Color.GRAY);
					}

					DataBinding binding = bindingSelector.getEditedObject();
					if (binding != null && binding.isBindingValue()) {
						BindingValue bindingValue = (BindingValue) binding.getExpression();
						// System.out.println("bindingValue=" + bindingValue + " valid=" + isValid());
						if (bindingValue.isValid()
								&& bindingValue.isLastBindingPathElement(columnElement.getElement()/*, _lists.indexOf(list) - 1*/)
								&& bindingSelector.isConnected()) {
							// System.out.println("connecte");
							returned = getIconLabelComponent(label, FIBIconLibrary.CONNECTED_ICON);
						} else if (columnElement.getResultingType() != null) {
							if (TypeUtils.isResolved(columnElement.getResultingType()) && bindingSelector.getBindable() != null) {
								// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
								if (bindingSelector.getBindable().getBindingFactory() != null
										&& bindingSelector.getBindable().getBindingFactory()
												.getAccessibleSimplePathElements(columnElement.getElement()) != null
										&& bindingSelector.getBindable().getBindingFactory()
												.getAccessibleSimplePathElements(columnElement.getElement()).size() > 0) {
									returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
								} else {
									if (!TypeUtils.isTypeAssignableFrom(binding.getDeclaredType(), columnElement.getResultingType(), true)) {
										label.setForeground(Color.GRAY);
									}
									if (binding.isSettable() && !columnElement.getElement().isSettable()) {
										label.setForeground(Color.GRAY);
									}
								}
							}
						}
					}

				} else {
					// Happen because of prototype value !
					// logger.warning("Unexpected type: "+bce+" of "+(bce!=null?bce.getClass():"null"));
				}

			}
			return returned;
		}

		private JComponent getIconLabelComponent(JLabel label, Icon icon) {
			iconLabel.setVisible(true);
			iconLabel.setIcon(icon);
			iconLabel.setOpaque(label.isOpaque());
			iconLabel.setBackground(label.getBackground());
			panel.setToolTipText(label.getToolTipText());
			if (label.getParent() != panel) {
				panel.add(label);
			}
			return panel;
		}
	}

	protected JPanel getControlPanel() {
		return _controlPanel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting()) {
			return;
		}

		DataBinding dataBinding = bindingSelector.getEditedObject();

		if (dataBinding == null) {
			logger.warning("dataBinding should not be null");
			return;
		}

		if (dataBinding.getExpression() == null) {
			// if (bindingSelector.getBindingDefinition() != null && bindingSelector.getBindable() != null) {
			BindingValue newBindingValue = new BindingValue();
			newBindingValue.setBindingVariable(getSelectedBindingVariable());
			newBindingValue.setDataBinding(dataBinding);
			// System.out.println("getSelectedBindingVariable()=" + getSelectedBindingVariable());
			dataBinding.setExpression(newBindingValue /*bindingSelector.makeBinding()*/);
			// bindingValue.setBindingVariable(getSelectedBindingVariable());
			// setEditedObject(bindingValue);
			// bindingSelector.fireEditedObjectChanged();
			/*} else {
				return;
			}*/
		}

		JList list = (JList) e.getSource();
		int index = _lists.indexOf(list);
		_selectedPathElementIndex = index;
		if (index < 0) {
			return;
		}
		int newSelectedIndex = list.getSelectedIndex();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("I select something from list at index " + index + " selected=" + newSelectedIndex);
		}
		if (newSelectedIndex < 0) {
			return;
		}

		// This call will perform BV edition
		bindingSelector.valueSelected(index, list, dataBinding);

		list.removeListSelectionListener(this);
		list.setSelectedIndex(newSelectedIndex);
		list.addListSelectionListener(this);

	}

	private boolean hasBindingPathForm(String textValue) {
		if (textValue.length() == 0) {
			return false;
		}

		boolean startingPathItem = true;
		for (int i = 0; i < textValue.length(); i++) {
			char c = textValue.charAt(i);
			if (c == '.') {
				startingPathItem = true;
			} else {
				boolean isNormalChar = c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' && !startingPathItem;
				if (!isNormalChar) {
					return false;
				}
				startingPathItem = false;
			}
		}
		return true;
	}

	@Override
	protected void synchronizePanelWithTextFieldValue(String textValue) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Request synchronizePanelWithTextFieldValue " + textValue);
		}

		try {
			bindingSelector.isUpdatingModel = true;

			if (!bindingSelector.popupIsShown() && textValue != null
					&& !bindingSelector.isAcceptableAsBeginningOfStaticBindingValue(textValue)) {
				boolean requestFocus = bindingSelector.getTextField().hasFocus();
				bindingSelector.openPopup();
				if (requestFocus) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							bindingSelector.getTextField().requestFocusInWindow();
						}
					});
				}
			}

			if (bindingSelector.getTextField().hasFocus()) {
				if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isBindingValue()) {
					// ((BindingValue) _bindingSelector.getEditedObject()).disconnect();
				}
				if (bindingSelector._selectorPanel != null) {
					filterWithCurrentInput(textValue);
				}
			}

			if (textValue == null || !textValue.equals(bindingSelector.renderedString(bindingSelector.getEditedObject()))) {
				bindingSelector.getTextField().setForeground(Color.RED);
			} else {
				bindingSelector.getTextField().setForeground(Color.BLACK);
			}

		} finally {
			bindingSelector.isUpdatingModel = false;
		}

	}

	private void filterWithCurrentInput(String textValue) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Try to filter for current input " + textValue);
		}

		if (!hasBindingPathForm(textValue)) {
			return;
		}

		StringTokenizer tokenizer = new StringTokenizer(textValue, ".", false);
		boolean isCurrentlyValid = true;
		int listIndex = 0;
		String element = null;
		while (isCurrentlyValid && tokenizer.hasMoreTokens()) {
			element = tokenizer.nextToken();
			BindingColumnElement col_element = findElementEquals(_lists.get(listIndex).getModel(), element);
			if (col_element == null) {
				isCurrentlyValid = false;
			} else {
				bindingSelector.setUpdatingModel(true);
				if (!ensureBindingValueExists()) {
					bindingSelector.setUpdatingModel(false);
					return;
				}

				/*
				 * if (listIndex == 0) {
				 * logger.info("Je selectionne "+col_element
				 * +" pour la premiere colonne"); boolean found = false; for
				 * (int i=0; i<_lists.get(listIndex).getModel().getSize(); i++)
				 * { Object o =
				 * _lists.get(listIndex).getModel().getElementAt(i); if
				 * (o.equals(col_element)) { found = true;
				 * logger.info("OK, je l'ai trouve"); } else {
				 * logger.info("Pas pareil: "+o); } } if (!found)
				 * logger.info("Je le trouve pas"); }
				 */

				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setFilter(null);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
				bindingSelector.valueSelected(listIndex, _lists.get(listIndex), bindingSelector.getEditedObject());
				bindingSelector.setUpdatingModel(false);
				listIndex++;
			}
		}

		if (!isCurrentlyValid) {
			_lists.get(listIndex).setFilter(element);
			completionInfo = new CompletionInfo(_lists.get(listIndex), element, textValue);
			if (completionInfo.matchingElements.size() > 0) {
				BindingColumnElement col_element = completionInfo.matchingElements.firstElement();
				_lists.get(listIndex).removeListSelectionListener(this);
				_lists.get(listIndex).setSelectedValue(col_element, true);
				_lists.get(listIndex).addListSelectionListener(this);
			}

		}

		cleanLists(listIndex);
	}

	private CompletionInfo completionInfo;

	protected class CompletionInfo {
		String validPath = null;
		String completionInitPath = null;
		String commonBeginningPath = null;
		Vector<BindingColumnElement> matchingElements = null;

		protected CompletionInfo(FilteredJList list, String subPartialPath, String fullPath) {
			validPath = fullPath.substring(0, fullPath.lastIndexOf(".") + 1);
			completionInitPath = subPartialPath;
			matchingElements = findElementsMatching(list.getModel(), subPartialPath);
			if (matchingElements.size() == 1) {
				commonBeginningPath = matchingElements.firstElement().getLabel();
			} else if (matchingElements.size() > 1) {
				int endCommonPathIndex = 0;
				boolean foundDiff = false;
				while (!foundDiff) {
					if (endCommonPathIndex < matchingElements.firstElement().getLabel().length()) {
						char c = matchingElements.firstElement().getLabel().charAt(endCommonPathIndex);
						for (int i = 1; i < matchingElements.size(); i++) {
							String label = matchingElements.elementAt(i).getLabel();
							if (endCommonPathIndex < label.length() && label.charAt(endCommonPathIndex) != c) {
								foundDiff = true;
							}
						}
						if (!foundDiff) {
							endCommonPathIndex++;
						}
					} else {
						foundDiff = true;
					}
				}
				commonBeginningPath = matchingElements.firstElement().getLabel().substring(0, endCommonPathIndex);
			}
		}

		@Override
		public String toString() {
			return "CompletionInfo, completionInitPath=" + completionInitPath + " validPath=" + validPath + " commonBeginningPath="
					+ commonBeginningPath + " matchingElements=" + matchingElements;
		}

		private boolean alreadyAutocompleted = false;

		protected void autoComplete() {
			if (!alreadyAutocompleted) {
				bindingSelector.getTextField().setText(validPath + commonBeginningPath);
			} else {
				bindingSelector.getTextField().setText(validPath + commonBeginningPath + ".");
			}
			alreadyAutocompleted = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					bindingSelector.getTextField().requestFocusInWindow();
				}
			});
		}

	}

	@Override
	protected void processEnterPressed() {
		logger.fine("Pressed on ENTER");

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList list = listAtIndex(index);
		if (list != null) {
			int currentSelected = list.getSelectedIndex();
			if (currentSelected > -1) {
				valueChanged(new ListSelectionEvent(list, currentSelected, currentSelected, false));
				// list.setSelectedIndex(currentSelected);
				update();
				completionInfo = null;
			}
		}

		if (bindingSelector.getEditedObject() != null && bindingSelector.getEditedObject().isValid()) {
			bindingSelector.apply();
		}
	}

	@Override
	protected void processDelete() {
		logger.fine("Pressed on DELETE");
		suppressSelection();
	}

	@Override
	protected void processBackspace() {
		logger.fine("Pressed on BACKSPACE");
		if (!suppressSelection()) {
			if (bindingSelector.getTextField().getText().length() > 0) {
				bindingSelector.getTextField().setText(
						bindingSelector.getTextField().getText().substring(0, bindingSelector.getTextField().getText().length() - 1));

			}
		}
	}

	private boolean suppressSelection() {
		if (bindingSelector.getTextField().getText().length() > 0) {
			if (bindingSelector.getTextField().getSelectedText() != null && bindingSelector.getTextField().getSelectedText().length() > 0) {
				int begin = bindingSelector.getTextField().getSelectionStart();
				int end = bindingSelector.getTextField().getSelectionEnd();
				bindingSelector.getTextField().setText(
						bindingSelector.getTextField().getText().substring(0, begin)
								+ bindingSelector.getTextField().getText()
										.substring(end, bindingSelector.getTextField().getText().length()));
				return true;
			}
		}
		return false;
	}

	@Override
	protected void processTabPressed() {
		logger.fine("Pressed on TAB, completionInfo=" + completionInfo);
		if (completionInfo != null) {
			completionInfo.autoComplete();
		}
	}

	@Override
	protected void processUpPressed() {
		logger.fine("Pressed on UP");

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		if (currentSelected > 0) {
			list.removeListSelectionListener(this);
			list.setSelectedIndex(currentSelected - 1);
			list.addListSelectionListener(this);
		}
	}

	@Override
	protected void processDownPressed() {
		logger.fine("Pressed on DOWN");
		if (!bindingSelector.popupIsShown()) {
			bindingSelector.openPopup();
		}

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		list.removeListSelectionListener(this);
		list.setSelectedIndex(currentSelected + 1);
		list.addListSelectionListener(this);

	}

	@Override
	protected void processLeftPressed() {
		logger.fine("Pressed on LEFT");
	}

	@Override
	protected void processRightPressed() {
		logger.fine("Pressed on RIGHT");

		if (!bindingSelector.popupIsShown()) {
			bindingSelector.openPopup();
		}

		int index = 0;
		if (bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		if (currentSelected > -1 && list.isFiltered()) {
			valueChanged(new ListSelectionEvent(list, currentSelected, currentSelected, false));
			update();
			completionInfo = null;
		} else if (completionInfo != null) {
			completionInfo.autoComplete();
		} else {
			list.requestFocusInWindow();
		}
	}

	boolean isKeyPathFromTextASubKeyPath(String inputText) {
		int dotCount = StringUtils.countMatches(inputText, ".");
		if (listAtIndex(dotCount) == null) {
			return false;
		}
		BindingColumnListModel listModel = listAtIndex(dotCount).getModel();
		String subPartialPath = inputText.substring(inputText.lastIndexOf(".") + 1);
		Vector<Integer> pathElementIndex = new Vector<Integer>();
		;
		BindingColumnElement pathElement = findElementMatching(listModel, subPartialPath, pathElementIndex);
		return pathElement != null;
	}

	boolean isKeyPathFromPanelValid() {
		if (bindingSelector.getEditedObject() == null) {
			return false;
		}
		int i = 0;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			i++;
		}
		if (listAtIndex(i - 1).getSelectedValue() instanceof BindingColumnElement) {
			if (TypeUtils.isTypeAssignableFrom(bindingSelector.getEditedObject().getDeclaredType(),
					((BindingColumnElement) listAtIndex(i - 1).getSelectedValue()).getResultingType(), true)) {
				return true;
			}
		}
		return false;
	}

	BindingValue makeBindingValueFromPanel() {
		if (bindingSelector.getEditedObject() == null || !bindingSelector.getEditedObject().isBindingValue()) {
			return null;
		}
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			System.out.println("Ici je selectionne " + last.getElement());
			((BindingValue) bindingSelector.getEditedObject().getExpression()).setBindingPathElementAtIndex(last.getElement(), i - 1);
			i++;
		}
		if (last != null) {
			((BindingValue) bindingSelector.getEditedObject().getExpression()).removeBindingPathElementAfter(last.getElement());
		}
		return (BindingValue) bindingSelector.getEditedObject().getExpression();
	}

}
