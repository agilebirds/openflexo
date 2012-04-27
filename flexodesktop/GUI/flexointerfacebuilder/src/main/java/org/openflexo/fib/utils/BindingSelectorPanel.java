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
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Type;
import java.util.Hashtable;
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

import org.openflexo.antar.binding.AbstractBinding;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingExpression;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingValue;
import org.openflexo.antar.binding.BindingVariableImpl;
import org.openflexo.antar.binding.FinalBindingPathElement;
import org.openflexo.antar.binding.MethodCall;
import org.openflexo.antar.binding.MethodCall.MethodCallArgument;
import org.openflexo.antar.binding.MethodDefinition;
import org.openflexo.antar.binding.StaticBinding;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.antar.binding.Typed;
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

class BindingSelectorPanel extends BindingSelector.AbstractBindingSelectorPanel implements ListSelectionListener {

	static final Logger logger = Logger.getLogger(BindingSelectorPanel.class.getPackage().getName());

	/**
	 *
	 */
	final BindingSelector _bindingSelector;

	protected JPanel _browserPanel;

	ButtonsControlPanel _controlPanel;

	JButton _connectButton;
	JButton _cancelButton;
	JButton _resetButton;
	JButton _expressionButton;

	JButton _createsButton;

	private Hashtable<BindingPathElement, Hashtable<Type, BindingColumnListModel>> _listModels;

	Vector<FilteredJList> _lists;

	protected int defaultVisibleColCount = 3;

	protected final EmptyColumnListModel EMPTY_MODEL = new EmptyColumnListModel();

	private BindingColumnListModel _rootBindingColumnListModel = null;

	JLabel currentTypeLabel;
	private JLabel searchedTypeLabel;
	private JTextArea bindingValueRepresentation;
	protected BindingColumnElement currentFocused = null;

	protected BindingSelectorPanel(BindingSelector bindingSelector) {
		bindingSelector.super();
		_bindingSelector = bindingSelector;
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

	public BindingVariableImpl getSelectedBindingVariable() {
		if (listAtIndex(0) != null && listAtIndex(0).getSelectedValue() != null) {
			return (BindingVariableImpl) ((BindingColumnElement) listAtIndex(0).getSelectedValue()).getElement();
		} else if (listAtIndex(0) != null && listAtIndex(0).getModel().getSize() == 1) {
			return (BindingVariableImpl) listAtIndex(0).getModel().getElementAt(0).getElement();
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

	protected class MethodCallBindingsModel extends AbstractModel<MethodCall, MethodCallArgument> {
		public MethodCallBindingsModel() {
			super(null);
			addToColumns(new IconColumn<MethodCallArgument>("icon", 25) {
				@Override
				public Icon getIcon(MethodCallArgument entity) {
					return FIBIconLibrary.METHOD_ICON;
				}
			});
			addToColumns(new StringColumn<MethodCallArgument>("name", 100) {
				@Override
				public String getValue(MethodCallArgument arg) {
					if (arg != null) {
						return arg.getName();
					}
					/*
					 * if (paramForValue(bindingValue) != null) return
					 * paramForValue(bindingValue).getName();
					 */
					return "null";
				}
			});
			addToColumns(new StringColumn<MethodCallArgument>("type", 100) {
				@Override
				public String getValue(MethodCallArgument arg) {
					if (arg != null) {
						return TypeUtils.simpleRepresentation(arg.getType());
					}
					return "null";
				}
			});
			addToColumns(new BindingValueColumn<MethodCallArgument>("value", 250, true) {

				@Override
				public AbstractBinding getValue(MethodCallArgument arg) {
					return arg.getBinding();
				}

				@Override
				public void setValue(MethodCallArgument arg, AbstractBinding aValue) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Sets value " + arg + " to be " + aValue);
					}
					if (arg != null) {
						arg.setBinding(aValue);
					}

					BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
				}

				@Override
				public Bindable getBindableFor(AbstractBinding value, MethodCallArgument rowObject) {
					return BindingSelectorPanel.this._bindingSelector.getBindable();
				}

				@Override
				public BindingDefinition getBindingDefinitionFor(AbstractBinding value, MethodCallArgument rowObject) {
					if (rowObject != null) {
						return rowObject.getBindingDefinition();
					}
					return null;
				}

				@Override
				public boolean allowsCompoundBinding(AbstractBinding value) {
					return true;
				}

				@Override
				public boolean allowsNewEntryCreation(AbstractBinding value) {
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

		public MethodCall getMethodCall() {
			return getModel();
		}

		@Override
		public MethodCallArgument elementAt(int row) {
			if (row >= 0 && row < getRowCount()) {
				return getMethodCall().getArgs().elementAt(row);
			} else {
				return null;
			}
		}

		@Override
		public int getRowCount() {
			if (getMethodCall() != null) {
				return getMethodCall().getArgs().size();
			}
			return 0;
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

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			baseHeight = 300;
		} else {
			baseHeight = 180;
		}

		if (_bindingSelector.areStaticValuesAllowed() || _bindingSelector.areCompoundBindingAllowed()) {
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

	private StaticBindingPanel staticBindingPanel;

	@Override
	protected void init() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("init() with " + _bindingSelector.editionMode + " for " + _bindingSelector.getEditedObject());
		}

		setLayout(new BorderLayout());

		_browserPanel = new JPanel();
		_browserPanel.setLayout(new BoxLayout(_browserPanel, BoxLayout.X_AXIS));
		for (int i = 0; i < defaultVisibleColCount; i++) {
			makeNewJList();
		}

		_controlPanel = new ButtonsControlPanel() {
			@Override
			public String localizedForKeyAndButton(String key, JButton component) {
				return FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, key, component);
			}
		};
		_connectButton = _controlPanel.addButton("connect", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.apply();
			}
		});
		_cancelButton = _controlPanel.addButton("cancel", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.cancel();
			}
		});
		_resetButton = _controlPanel.addButton("reset", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BindingSelectorPanel.this._bindingSelector.setEditedObject(null);
				BindingSelectorPanel.this._bindingSelector.apply();
			}
		});
		if (_bindingSelector.areBindingExpressionsAllowed()) {
			_expressionButton = _controlPanel.addButton("expression", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (BindingSelectorPanel.this._bindingSelector.getEditedObject() != null) {
						BindingSelectorPanel.this._bindingSelector.activateBindingExpressionMode(new BindingExpression(
								BindingSelectorPanel.this._bindingSelector.getBindingDefinition(),
								BindingSelectorPanel.this._bindingSelector.getBindable(), BindingSelectorPanel.this._bindingSelector
										.getEditedObject()));
					} else {
						BindingSelectorPanel.this._bindingSelector.activateBindingExpressionMode(null);
					}
				}
			});
		}

		_controlPanel.applyFocusTraversablePolicyTo(_controlPanel, false);

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BorderLayout());

		if (_bindingSelector.areCompoundBindingAllowed()) {
			showHideCompoundBindingsButton = new MouseOverButton();
			showHideCompoundBindingsButton.setBorder(BorderFactory.createEmptyBorder());
			showHideCompoundBindingsButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (BindingSelectorPanel.this._bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
						BindingSelectorPanel.this._bindingSelector.activateNormalBindingMode();
					} else {
						BindingSelectorPanel.this._bindingSelector.activateCompoundBindingMode();
					}
				}
			});

			JLabel showHideCompoundBindingsButtonLabel = new JLabel("", SwingConstants.RIGHT);
			showHideCompoundBindingsButtonLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
			if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
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

		if (_bindingSelector.areStaticValuesAllowed()) {
			JPanel optionsWestPanel = new JPanel();
			optionsWestPanel.setLayout(new VerticalLayout());
			staticBindingPanel = new StaticBindingPanel(this);
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

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
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

		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
			middlePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(_browserPanel,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
					getMethodCallBindingsPanel()); // ICI
			((JSplitPane) middlePane).setDividerLocation(0.5);
			((JSplitPane) middlePane).setResizeWeight(0.5);
		} else { // For NORMAL_BINDING and STATIC_BINDING
			middlePane = new JScrollPane(_browserPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // ICI
		}

		JPanel middlePaneWithOptions = new JPanel();
		middlePaneWithOptions.setLayout(new BorderLayout());
		middlePaneWithOptions.add(middlePane, BorderLayout.CENTER);
		if (_bindingSelector.areStaticValuesAllowed() || _bindingSelector.areCompoundBindingAllowed()) {
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
		if (_bindingSelector.getBindingDefinition() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateSearchedTypeLabel() with " + _bindingSelector.getBindingDefinition().getTypeStringRepresentation());
			}
			searchedTypeLabel.setText("[" + _bindingSelector.getBindingDefinition().getTypeStringRepresentation() + "]");
		}
	}

	protected int getVisibleColsCount() {
		return _lists.size();
	}

	public boolean ensureBindingValueExists() {
		AbstractBinding bindingValue = _bindingSelector.getEditedObject();
		if (bindingValue == null) {
			if (_bindingSelector.getBindingDefinition() != null && _bindingSelector.getBindable() != null) {
				bindingValue = _bindingSelector.makeBinding();
				_bindingSelector.setEditedObject(bindingValue);
			} else {
				return false;
			}
		}
		return _bindingSelector.getEditedObject() != null;
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
			if (!(model instanceof BindingColumnListModel)) {
				new Exception("oops").printStackTrace();
			}
			setFilter(null);
			super.setModel(model);
		}

		@Override
		public BindingColumnListModel getModel() {
			if (super.getModel() instanceof BindingColumnListModel) {
				return (BindingColumnListModel) super.getModel();
			} else {
				new Exception("oops, j'ai un " + super.getModel()).printStackTrace();
				return null;
			}
		}
	}

	protected JList makeNewJList() {
		FilteredJList newList = new FilteredJList();

		newList.addMouseMotionListener(new TypeResolver(newList));

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
					if (BindingSelectorPanel.this._bindingSelector.getEditedObject() != null
							&& BindingSelectorPanel.this._bindingSelector.getEditedObject().isBindingValid()) {
						BindingSelectorPanel.this._bindingSelector.apply();
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
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processEnterPressed();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processBackspace();
					e.consume();
				} else if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					BindingSelectorPanel.this._bindingSelector._selectorPanel.processDelete();
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					AbstractBinding bindingValue = BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (bindingValue instanceof BindingValue) {
						int i = _lists.indexOf(e.getSource());
						if (i > -1 && i < _lists.size() && listAtIndex(i + 1) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0) != null
								&& listAtIndex(i + 1).getModel().getElementAt(0).getElement() instanceof BindingPathElement) {
							((BindingValue) bindingValue).setBindingPathElementAtIndex(listAtIndex(i + 1).getModel().getElementAt(0)
									.getElement(), i);
							BindingSelectorPanel.this._bindingSelector.setEditedObject(bindingValue);
							BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
							listAtIndex(i + 1).requestFocus();
						}
						e.consume();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (!ensureBindingValueExists()) {
						return;
					}
					AbstractBinding bindingValue = BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (bindingValue instanceof BindingValue) {
						int i = _lists.indexOf(e.getSource()) - 1;
						if (((BindingValue) bindingValue).getBindingPath().size() > i && i > -1 && i < _lists.size()) {
							((BindingValue) bindingValue).getBindingPath().removeElementAt(i);
							((BindingValue) bindingValue).disconnect();
							BindingSelectorPanel.this._bindingSelector.setEditedObject(bindingValue);
							BindingSelectorPanel.this._bindingSelector.fireEditedObjectChanged();
							listAtIndex(i).requestFocus();
						}
						e.consume();
					}
				}
			}

		});

		_browserPanel.add(new JScrollPane(newList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)); // ICI
		newList.setVisibleRowCount(6);
		revalidate();
		repaint();
		return newList;
	}

	int _selectedPathElementIndex = -1;

	protected void resetMethodCallPanel() {
		if (_bindingSelector.getEditedObject() == null || _bindingSelector.getEditedObject().isStaticValue()
				|| _bindingSelector.getEditedObject() instanceof BindingValue
				&& ((BindingValue) _bindingSelector.getEditedObject()).getBindingPath().size() == 0) {
			_selectedPathElementIndex = -1;
		} else if (_bindingSelector.getEditedObject() instanceof BindingValue) {
			_selectedPathElementIndex = ((BindingValue) _bindingSelector.getEditedObject()).getBindingPath().size();
		}
		updateMethodCallPanel();
	}

	void updateMethodCallPanel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("updateMethodCallPanel with " + _bindingSelector.editionMode + " binding=" + _bindingSelector.getEditedObject()
					+ " _selectedPathElementIndex=" + _selectedPathElementIndex);
		}
		if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING && _bindingSelector.getEditedObject() instanceof BindingValue) {
			if (((BindingValue) _bindingSelector.getEditedObject()).isCompoundBinding() && _selectedPathElementIndex == -1) {
				_selectedPathElementIndex = ((BindingValue) _bindingSelector.getEditedObject()).getBindingPathElementCount();
			}
			if (_selectedPathElementIndex >= _lists.size()) {
				_selectedPathElementIndex = -1;
			}
			BindingValue bindingValue = (BindingValue) _bindingSelector.getEditedObject();
			if (bindingValue == null) {
				_selectedPathElementIndex = -1;
			} else if (_selectedPathElementIndex > bindingValue.getBindingPath().size()) {
				_selectedPathElementIndex = -1;
			}
			if (_selectedPathElementIndex > -1 && bindingValue != null) {
				JList list = _lists.get(_selectedPathElementIndex);
				int newSelectedIndex = list.getSelectedIndex();
				if (newSelectedIndex > 0) {
					BindingColumnElement selectedValue = (BindingColumnElement) list.getSelectedValue();
					if (selectedValue.getElement() instanceof MethodDefinition) {
						BindingPathElement currentElement = bindingValue.getBindingPathElementAtIndex(_selectedPathElementIndex - 1);
						if (currentElement instanceof MethodCall
								&& ((MethodCall) currentElement).getMethod().equals(
										((MethodDefinition) selectedValue.getElement()).getMethod())) {
							getMethodCallBindingsModel().setModel((MethodCall) currentElement);
							return;
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
		Component[] scrollPanes = _browserPanel.getComponents();
		for (int i = 0; i < scrollPanes.length; i++) {
			if (((Container) scrollPanes[i]).isAncestorOf(list)) {
				_browserPanel.remove(scrollPanes[i]);
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

	@Override
	protected void fireBindingDefinitionChanged() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("fireBindingDefinitionChanged / Setting new binding definition: " + _bindingSelector.getBindingDefinition());
		}

		update();

		if (staticBindingPanel != null) {
			staticBindingPanel.updateStaticBindingPanel();
		}

	}

	private void clearColumns() {
		listAtIndex(0).setModel(_bindingSelector.getRootListModel());
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
		AbstractBinding binding = _bindingSelector.getEditedObject();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("update with " + binding);
		}

		if (binding == null || binding instanceof StaticBinding) {
			clearColumns();
			if (binding == null) {
				setEditStaticValue(false);
			}
		} else if (binding instanceof BindingValue) {
			BindingValue bindingValue = (BindingValue) binding;
			listAtIndex(0).setModel(_bindingSelector.getRootListModel());
			int lastUpdatedList = 0;

			// logger.info("bindingValue.getBindingVariable()="+bindingValue.getBindingVariable());

			if (bindingValue.getBindingVariable() != null) {
				if (bindingValue.getBindingVariable().getType() != null) {
					listAtIndex(1)
							.setModel(
									_bindingSelector.getListModelFor(bindingValue.getBindingVariable(), bindingValue.getBindingVariable()
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
					BindingPathElement pathElement = bindingValue.getBindingPath().elementAt(i);
					if (i + 2 == getVisibleColsCount()) {
						final JList l = makeNewJList();
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Rectangle r = SwingUtilities.convertRectangle(l, l.getBounds(), _browserPanel);
								// System.out.println("scrollRectToVisible with "+r);
								_browserPanel.scrollRectToVisible(r); // ICI
							}
						});
					}
					/*
					 * if (i==bindingValue.getBindingPath().size()-1) {
					 * logger.info("Dernier element: "+pathElement); }
					 */

					if (!(bindingValue.isConnected() && bindingValue.isLastBindingPathElement(pathElement, i))) {
						Type resultingType = bindingValue.getBindingPath().getResultingTypeAtIndex(i);
						listAtIndex(i + 2).setModel(_bindingSelector.getListModelFor(bindingValue.getBindingPath().get(i), resultingType));
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

			if (_bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				bindingValueRepresentation.setText(_bindingSelector.renderedString(bindingValue));
				bindingValueRepresentation.setForeground(bindingValue.isBindingValid() ? Color.BLACK : Color.RED);
				updateMethodCallPanel();
			}

			currentTypeLabel.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "no_type"));
			currentTypeLabel.setToolTipText(null);

		}

		updateSearchedTypeLabel();

		if (binding != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding " + binding + " isValid()=" + binding.isBindingValid());
			} else if (logger.isLoggable(Level.FINE)) {
				logger.fine("Binding is null");
			}
		}

		// Set connect button state
		_connectButton.setEnabled(binding != null && binding.isBindingValid());
		/*if (!binding.isBindingValid()) {
			logger.info("Binding NOT valid: " + binding);
			binding.debugIsBindingValid();
		}*/
		if (binding != null && binding.isBindingValid()) {
			if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
				_connectButton.setSelected(true);
			}
		}
		if (binding != null) {
			_bindingSelector.getTextField().setForeground(binding.isBindingValid() ? Color.BLACK : Color.RED);
		}

		if (_bindingSelector.areStaticValuesAllowed() && staticBindingPanel != null) {
			staticBindingPanel.updateStaticBindingPanel();
		}

		if (binding instanceof BindingValue) {
			setEditStaticValue(false);
		} else if (binding instanceof StaticBinding) {
			setEditStaticValue(true);
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
		if (!_bindingSelector.areStaticValuesAllowed() || staticBindingPanel == null) {
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
				listAtIndex(0).setModel(_bindingSelector.getRootListModel());
				// }
			}
			staticBindingPanel.updateStaticBindingPanel();
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
		if (_bindingSelector.getBindingModel() != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("buildRootColumnListModel() from " + _bindingSelector.getBindingModel());
			}
			return new RootBindingColumnListModel(_bindingSelector.getBindingModel());
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
			if (element instanceof MethodCall) {
				element = ((MethodCall) element).getMethodDefinition();
			}
			for (int i = 0; i < getSize(); i++) {
				if (getElementAt(i).getElement().equals(element)) {
					return getElementAt(i);
				}
			}
			/*logger.info("I cannot find " + element + " of "
					+ (element != null ? element.getClass() : null));
			for (int i = 0; i < getSize(); i++) {
				logger.info("Looking with "
						+ getElementAt(i).getElement()
						+ " of "
						+ (getElementAt(i).getElement() != null ? getElementAt(
								i).getElement().getClass() : null));
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
			if (getFilter() == null && !_bindingSelector._hideFilteredObjects) {
				return getUnfilteredSize();
			}
			int returned = 0;
			if (!_bindingSelector._hideFilteredObjects) {
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
			if (getFilter() == null && !_bindingSelector._hideFilteredObjects) {
				return getUnfilteredElementAt(index);
			}
			if (!_bindingSelector._hideFilteredObjects) {
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

			if (columnElement.getElement() != null && columnElement.getElement() instanceof BindingVariableImpl) {
				BindingVariableImpl bv = (BindingVariableImpl) columnElement.getElement();
				if (bv.getType() == null) {
					return true;
				}
			} else if (columnElement.getElement() != null) {
				AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
				if (binding != null && binding instanceof BindingValue) {
					BindingValue bindingValue = (BindingValue) binding;
					if (bindingValue.isConnected()
							&& bindingValue.isLastBindingPathElement(columnElement.getElement(), getIndexOfList(this) - 1)) {
						// setIcon(label, CONNECTED_ICON, list);
					} else if (columnElement.getResultingType() != null) {
						if (TypeUtils.isResolved(columnElement.getResultingType()) && _bindingSelector.getBindable() != null) {
							// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
							if (_bindingSelector.getBindable().getBindingFactory()
									.getAccessibleBindingPathElements(columnElement.getElement()).size() > 0) {
							} else {
								if (_bindingSelector.getBindingDefinition() != null
										&& _bindingSelector.getBindingDefinition().getType() != null
										&& !TypeUtils.isTypeAssignableFrom(BindingSelectorPanel.this._bindingSelector
												.getBindingDefinition().getType(), columnElement.getResultingType(), true)) {
									return true;
								}
								if (_bindingSelector.getBindingDefinition() != null
										&& _bindingSelector.getBindingDefinition().getIsSettable()
										&& !columnElement.getElement().isSettable()) {
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
				* BindingSelectorPanel.this._bindingSelector.getEditedObject(); if
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
				* .isTypeAssignableFrom(BindingSelectorPanel.this._bindingSelector
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

			if (_bindingSelector.getBindable() == null) {
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
			_accessibleProperties.addAll(_bindingSelector.getBindable().getBindingFactory().getAccessibleBindingPathElements(_element));
			// _accessibleProperties.addAll(_element.getAccessibleBindingPathElements());

			if (BindingSelectorPanel.this._bindingSelector.editionMode == EditionMode.COMPOUND_BINDING) {
				// _accessibleMethods.addAll(KeyValueLibrary.getAccessibleMethods(_type));
				_accessibleProperties.addAll(_bindingSelector.getBindable().getBindingFactory()
						.getAccessibleCompoundBindingPathElements(_element));
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
			/*
			 * if(((dataModification instanceof PropertyRegistered ||
			 * dataModification instanceof PropertyUnregistered ||
			 * dataModification instanceof PropertiesReordered) ||
			 * (dataModification instanceof DMAttributeDataModification &&
			 * dataModification.propertyName().equals("parentType")) ||
			 * (dataModification instanceof DMAttributeDataModification &&
			 * dataModification.propertyName().equals("isClassProperty"))
			 * 
			 * ) && observable.equals(_type.getBaseEntity())){ updateValues();
			 * fireModelChanged(); } if ((dataModification instanceof
			 * DMAttributeDataModification &&
			 * (dataModification.propertyName().equals
			 * ("returnType")||dataModification.propertyName().equals("type"))))
			 * { updateValues(); fireModelChanged(); if
			 * (BindingSelectorPanel.this._bindingSelector._selectorPanel!=null)
			 * BindingSelectorPanel
			 * .this._bindingSelector._selectorPanel.update(); } if
			 * (dataModification instanceof ObjectDeleted)
			 * observable.deleteObserver(this);
			 */

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

	protected class TypeResolver extends MouseMotionAdapter {

		private JList list;

		protected TypeResolver(JList aList) {
			currentFocused = null;
			list = aList;
		}

		@Override
		public void mouseMoved(MouseEvent e) {

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
					if (!(columnElement.getElement() instanceof FinalBindingPathElement)) {
						returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
					}
					if (columnElement.getElement().getType() != null) {
						returned.setToolTipText(columnElement.getTooltipText());
					} else {
						label.setForeground(Color.GRAY);
					}

					AbstractBinding binding = BindingSelectorPanel.this._bindingSelector.getEditedObject();
					if (binding != null && binding instanceof BindingValue) {
						BindingValue bindingValue = (BindingValue) binding;
						if (bindingValue.isConnected()
								&& bindingValue.isLastBindingPathElement(columnElement.getElement(), _lists.indexOf(list) - 1)) {
							returned = getIconLabelComponent(label, FIBIconLibrary.CONNECTED_ICON);
						} else if (columnElement.getResultingType() != null) {
							if (TypeUtils.isResolved(columnElement.getResultingType()) && _bindingSelector.getBindable() != null) {
								// if (columnElement.getElement().getAccessibleBindingPathElements().size() > 0) {
								if (_bindingSelector.getBindable().getBindingFactory()
										.getAccessibleBindingPathElements(columnElement.getElement()).size() > 0) {
									returned = getIconLabelComponent(label, FIBIconLibrary.ARROW_RIGHT_ICON);
								} else {
									if (_bindingSelector.getBindingDefinition() != null
											&& _bindingSelector.getBindingDefinition().getType() != null
											&& !TypeUtils.isTypeAssignableFrom(_bindingSelector.getBindingDefinition().getType(),
													columnElement.getResultingType(), true)) {
										label.setForeground(Color.GRAY);
									}
									if (_bindingSelector.getBindingDefinition() != null
											&& _bindingSelector.getBindingDefinition().getIsSettable()
											&& !columnElement.getElement().isSettable()) {
										label.setForeground(Color.GRAY);
									}
								}
							}
						}
					}

					// TODO: supprimer la dependance a KeyValueProperty:
					// utiliser seulement l'interface BindingPathElement

					/*
					 * if (columnElement.getElement() != null &&
					 * columnElement.getElement() instanceof BindingVariable) {
					 * BindingVariable bv =
					 * (BindingVariable)columnElement.getElement();
					 * label.setText(columnElement.getLabel()); setIcon(label,
					 * BindingSelector.ARROW_RIGHT_ICON, list); if (bv.getType()
					 * != null) {
					 * label.setToolTipText(columnElement.getTooltipText()); }
					 * else { label.setForeground(Color.GRAY); } } else if
					 * (columnElement.getElement() != null &&
					 * columnElement.getElement() instanceof KeyValueProperty) {
					 * KeyValueProperty property = (KeyValueProperty)
					 * columnElement.getElement();
					 * label.setText(columnElement.getLabel()); AbstractBinding
					 * binding =
					 * BindingSelectorPanel.this._bindingSelector.getEditedObject
					 * (); if ((binding != null) && binding instanceof
					 * BindingValue) { BindingValue bindingValue =
					 * (BindingValue)binding; if (bindingValue.isConnected() &&
					 * (bindingValue.isLastBindingPathElement(property,
					 * _lists.indexOf(list) - 1))) { setIcon(label,
					 * BindingSelector.CONNECTED_ICON, list); } else if
					 * (columnElement.getResultingType() != null) { if
					 * (TypeUtils.isResolved(columnElement.getResultingType())
					 * && _bindingSelector.getBindable() != null) { if
					 * (_bindingSelector.getBindable().getBindingFactory().
					 * getAccessibleBindingPathElements
					 * (columnElement.getElement()).size() > 0) { setIcon(label,
					 * BindingSelector.ARROW_RIGHT_ICON, list); } else { if
					 * ((_bindingSelector.getBindingDefinition() != null) &&
					 * (_bindingSelector.getBindingDefinition().getType() !=
					 * null) &&
					 * (!TypeUtils.isTypeAssignableFrom(_bindingSelector
					 * .getBindingDefinition
					 * ().getType(),columnElement.getResultingType(),true))) {
					 * label.setForeground(Color.GRAY); } if
					 * ((_bindingSelector.getBindingDefinition() != null) &&
					 * (_bindingSelector.getBindingDefinition().getIsSettable())
					 * && !property.isSettable()) {
					 * label.setForeground(Color.GRAY); } } } } } if (property
					 * != null) {
					 * label.setToolTipText(columnElement.getTooltipText()); }
					 * 
					 * } else if (columnElement.getElement() != null &&
					 * columnElement.getElement() instanceof MethodDefinition) {
					 * MethodDefinition method = (MethodDefinition)
					 * columnElement.getElement();
					 * 
					 * String methodAsString = method.getSimplifiedSignature();
					 * int idx = _lists.indexOf(list); if (idx > 0) { Type
					 * context =((BindingColumnElement)_lists.elementAt(idx-1).
					 * getSelectedValue()).getResultingType(); methodAsString =
					 * method.getSimplifiedSignature();
					 * //method.getSimplifiedSignatureInContext(context);
					 * //methodAsString =
					 * method.getSimplifiedSignatureInContext(context); }
					 * 
					 * label.setText(methodAsString); AbstractBinding binding =
					 * BindingSelectorPanel
					 * .this._bindingSelector.getEditedObject(); if ((binding !=
					 * null) && binding instanceof BindingValue) { BindingValue
					 * bindingValue = (BindingValue)binding; BindingPathElement
					 * bpe =
					 * bindingValue.getBindingPathElementAtIndex(_lists.indexOf
					 * (list) - 1); if ((bindingValue.isConnected()) &&
					 * (bindingValue.isLastBindingPathElement(bpe,
					 * _lists.indexOf(list) - 1)) && ((bpe instanceof
					 * MethodCall) &&
					 * (((MethodCall)bpe).getMethod().equals(method
					 * .getMethod())))) { setIcon(label,
					 * BindingSelector.CONNECTED_ICON, list); } else if
					 * (columnElement.getResultingType() != null &&
					 * resultingType != null) { if
					 * (_bindingSelector.getBindable(
					 * ).getBindingFactory().getAccessibleBindingPathElements
					 * (columnElement.getElement()).size() +
					 * _bindingSelector.getBindable().getBindingFactory().
					 * getAccessibleCompoundBindingPathElements
					 * (columnElement.getElement()).size() > 0) { setIcon(label,
					 * BindingSelector.ARROW_RIGHT_ICON, list); } else { if
					 * ((_bindingSelector.getBindingDefinition() != null) &&
					 * (_bindingSelector.getBindingDefinition().getType() !=
					 * null) &&
					 * (!TypeUtils.isTypeAssignableFrom(_bindingSelector
					 * .getBindingDefinition
					 * ().getType(),columnElement.getResultingType(),true))) {
					 * label.setForeground(Color.GRAY); } } } } if
					 * (method.getType() != null) {
					 * label.setToolTipText(columnElement.getTooltipText()); } }
					 */
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

		AbstractBinding bindingValue = _bindingSelector.getEditedObject();
		if (bindingValue == null) {
			if (_bindingSelector.getBindingDefinition() != null && _bindingSelector.getBindable() != null) {
				bindingValue = _bindingSelector.makeBinding();
				// bindingValue.setBindingVariable(getSelectedBindingVariable());
				// setEditedObject(bindingValue);
				// fireEditedObjectChanged();
			} else {
				return;
			}
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
			// if (index == 0 && !(list.getSelectedValue() instanceof
			// BindingVariable)) index = index+1;
			/*
			 * if (index == 0 && (list.getSelectedValue() instanceof
			 * BindingVariable)) { if (list.getSelectedValue() !=
			 * bindingValue.getBindingVariable()) {
			 * bindingValue.setBindingVariable((BindingVariable)
			 * list.getSelectedValue()); setEditedObject(bindingValue);
			 * fireEditedObjectChanged(); } } else { DMObject selectedValue =
			 * (DMObject)list.getSelectedValue(); if (selectedValue instanceof
			 * DMProperty) { if (selectedValue !=
			 * bindingValue.getBindingPathElementAtIndex(index - 1)) {
			 * bindingValue.setBindingPathElementAtIndex((DMProperty)selectedValue,
			 * index - 1); setEditedObject(bindingValue); fireEditedObjectChanged();
			 * } } else if ((selectedValue instanceof DMMethod) &&
			 * (_allowsCompoundBindings)) { BindingPathElement currentElement =
			 * bindingValue.getBindingPathElementAtIndex(index - 1); if
			 * (!(currentElement instanceof MethodCall) ||
			 * (((MethodCall)currentElement).getMethod() != selectedValue)) {
			 * DMMethod method = (DMMethod)selectedValue; MethodCall newMethodCall =
			 * new MethodCall(bindingValue,method);
			 * bindingValue.setBindingPathElementAtIndex(newMethodCall, index - 1);
			 * setEditedObject(bindingValue); fireEditedObjectChanged(); } } }
			 */
		}

		// This call will perform BV edition
		_bindingSelector.valueSelected(index, list, bindingValue);

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
			_bindingSelector.isUpdatingModel = true;

			if (!_bindingSelector.popupIsShown() && textValue != null
					&& !_bindingSelector.isAcceptableAsBeginningOfStaticBindingValue(textValue)) {
				boolean requestFocus = _bindingSelector.getTextField().hasFocus();
				_bindingSelector.openPopup();
				if (requestFocus) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							BindingSelectorPanel.this._bindingSelector.getTextField().requestFocus();
						}
					});
				}
			}

			if (_bindingSelector.getTextField().hasFocus()) {
				if (_bindingSelector.getEditedObject() != null && _bindingSelector.getEditedObject() instanceof BindingValue) {
					((BindingValue) _bindingSelector.getEditedObject()).disconnect();
				}
				if (_bindingSelector._selectorPanel != null) {
					filterWithCurrentInput(textValue);
				}
			}

			if (textValue == null || !textValue.equals(_bindingSelector.renderedString(_bindingSelector.getEditedObject()))) {
				_bindingSelector.getTextField().setForeground(Color.RED);
			} else {
				_bindingSelector.getTextField().setForeground(Color.BLACK);
			}

		} finally {
			_bindingSelector.isUpdatingModel = false;
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
				_bindingSelector.setUpdatingModel(true);
				if (!ensureBindingValueExists()) {
					_bindingSelector.setUpdatingModel(false);
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
				_bindingSelector.valueSelected(listIndex, _lists.get(listIndex), _bindingSelector.getEditedObject());
				_bindingSelector.setUpdatingModel(false);
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
							if (matchingElements.elementAt(i).getLabel().charAt(endCommonPathIndex) != c) {
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
				BindingSelectorPanel.this._bindingSelector.getTextField().setText(validPath + commonBeginningPath);
			} else {
				BindingSelectorPanel.this._bindingSelector.getTextField().setText(validPath + commonBeginningPath + ".");
			}
			alreadyAutocompleted = true;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					BindingSelectorPanel.this._bindingSelector.getTextField().requestFocus();
				}
			});
		}

	}

	@Override
	protected void processEnterPressed() {
		logger.fine("Pressed on ENTER");

		int index = 0;
		if (_bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(_bindingSelector.getTextField().getText(), ".");
		}

		FilteredJList list = listAtIndex(index);

		int currentSelected = list.getSelectedIndex();
		if (currentSelected > -1) {
			valueChanged(new ListSelectionEvent(list, currentSelected, currentSelected, false));
			// list.setSelectedIndex(currentSelected);
			update();
			completionInfo = null;
		}

		if (_bindingSelector.getEditedObject() != null && _bindingSelector.getEditedObject().isBindingValid()) {
			_bindingSelector.apply();
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
			if (_bindingSelector.getTextField().getText().length() > 0) {
				_bindingSelector.getTextField().setText(
						_bindingSelector.getTextField().getText().substring(0, _bindingSelector.getTextField().getText().length() - 1));

			}
		}
	}

	private boolean suppressSelection() {
		if (_bindingSelector.getTextField().getText().length() > 0) {
			if (_bindingSelector.getTextField().getSelectedText() != null && _bindingSelector.getTextField().getSelectedText().length() > 0) {
				int begin = _bindingSelector.getTextField().getSelectionStart();
				int end = _bindingSelector.getTextField().getSelectionEnd();
				_bindingSelector.getTextField().setText(
						_bindingSelector.getTextField().getText().substring(0, begin)
								+ _bindingSelector.getTextField().getText()
										.substring(end, _bindingSelector.getTextField().getText().length()));
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
		if (_bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(_bindingSelector.getTextField().getText(), ".");
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
		if (!_bindingSelector.popupIsShown()) {
			_bindingSelector.openPopup();
		}

		int index = 0;
		if (_bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(_bindingSelector.getTextField().getText(), ".");
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

		if (!_bindingSelector.popupIsShown()) {
			_bindingSelector.openPopup();
		}

		int index = 0;
		if (_bindingSelector.getEditedObject() != null) {
			index = StringUtils.countMatches(_bindingSelector.getTextField().getText(), ".");
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
			list.requestFocus();
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
		if (_bindingSelector.getEditedObject() == null) {
			return false;
		}
		int i = 0;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			i++;
		}
		if (listAtIndex(i - 1).getSelectedValue() instanceof BindingColumnElement) {
			if (_bindingSelector.getBindingDefinition().getType() == null
					|| TypeUtils.isTypeAssignableFrom(_bindingSelector.getBindingDefinition().getType(),
							((BindingColumnElement) listAtIndex(i - 1).getSelectedValue()).getResultingType(), true)) {
				return true;
			}
		}
		return false;
	}

	BindingValue makeBindingValueFromPanel() {
		if (_bindingSelector.getEditedObject() == null || !(_bindingSelector.getEditedObject() instanceof BindingValue)) {
			return null;
		}
		int i = 1;
		BindingColumnElement last = null;
		while (listAtIndex(i) != null && listAtIndex(i).getSelectedValue() != null) {
			last = (BindingColumnElement) listAtIndex(i).getSelectedValue();
			((BindingValue) _bindingSelector.getEditedObject()).setBindingPathElementAtIndex(last.getElement(), i - 1);
			i++;
		}
		if (last != null) {
			((BindingValue) _bindingSelector.getEditedObject()).removeBindingPathElementAfter(last.getElement());
		}
		return (BindingValue) _bindingSelector.getEditedObject();
	}

}
