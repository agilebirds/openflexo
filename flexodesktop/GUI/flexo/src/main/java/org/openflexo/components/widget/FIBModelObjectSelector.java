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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.RGBImageFilter;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.AdvancedPrefs;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBBrowser;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.model.FIBList;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.FIBListWidget;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Widget allowing to select an object while browsing a relevant subset of objects in project
 * 
 * @author sguerin
 * 
 */
public abstract class FIBModelObjectSelector<T> extends TextFieldCustomPopup<T> implements FIBCustomComponent<T, FIBModelObjectSelector>,
		HasPropertyChangeSupport {
	static final Logger logger = Logger.getLogger(FIBModelObjectSelector.class.getPackage().getName());

	private static final String DELETED = "deleted";

	public abstract File getFIBFile();

	private T _revertValue;

	protected SelectorDetailsPanel _selectorPanel;

	private FlexoProject project;
	private Object selectedObject;
	private T selectedValue;
	private final List<T> matchingValues;
	private T candidateValue;

	private FIBCustom component;
	private FIBController controller;

	private PropertyChangeSupport pcSupport;

	private FlexoController flexoController;

	private boolean isFiltered = false;

	private boolean showReset = true;

	public FIBModelObjectSelector(T editedObject) {
		super(editedObject);
		pcSupport = new PropertyChangeSupport(this);
		setRevertValue(editedObject);
		setFocusable(true);
		matchingValues = new ArrayList<T>();
		getTextField().setEditable(true);
		getTextField().getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (!textIsBeeingProgrammaticallyEditing()) {
					updateMatchingValues();
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (!textIsBeeingProgrammaticallyEditing()) {
					updateMatchingValues();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!textIsBeeingProgrammaticallyEditing()) {
					updateMatchingValues();
				}
			}
		});
		getTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							updateMatchingValues();
							if (matchingValues.size() > 0) {
								setSelectedValue(matchingValues.get(0));
								apply();
							}
						}
					});
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					getFIBListWidget().getDynamicJComponent().requestFocusInWindow();
					getFIBListWidget().getDynamicJComponent().setSelectedIndex(
							getFIBListWidget().getDynamicJComponent().getModel().getSize() - 1);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					getFIBListWidget().getJComponent().requestFocusInWindow();
					getFIBListWidget().getDynamicJComponent().setSelectedIndex(0);
				}

			}

			@Override
			public void keyTyped(KeyEvent e) {

				// if command-key is pressed, do not open popup
				if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown()) {
					return;
				}

				boolean requestFocus = getTextField().hasFocus();
				final int selectionStart = getTextField().getSelectionStart() + 1;
				final int selectionEnd = getTextField().getSelectionEnd() + 1;
				if (!popupIsShown()) {
					openPopup();
				}
				// updateMatchingValues();
				if (requestFocus) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							getTextField().requestFocusInWindow();
							getTextField().select(selectionStart, selectionEnd);
						}
					});
				}

				/*SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int selectionStart = getTextField().getSelectionStart();
						int selectionEnd = getTextField().getSelectionEnd();
						System.out.println("Was selected: " + selectionStart + " and " + selectionEnd);
						if (!popupIsShown()) {
							openPopup();
						}
						updateMatchingValues();
						System.out.println("Now select: " + selectionStart + " and " + selectionEnd);
						getTextField().select(selectionStart, selectionEnd);
					}
				});*/
			}
		});
	}

	@Override
	public void delete() {
		super.delete();
		if (pcSupport != null) {
			pcSupport.firePropertyChange(DELETED, false, true);
		}
		matchingValues.clear();
		pcSupport = null;
		selectedObject = null;
		selectedValue = null;
		project = null;
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
		this.component = component;
		this.controller = controller;
	}

	@Override
	public void openPopup() {
		super.openPopup();
		// System.out.println("Request focus now");
		getTextField().requestFocusInWindow();
	}

	public boolean isShowReset() {
		return showReset;
	}

	public void setShowReset(boolean showReset) {
		this.showReset = showReset;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	public boolean isFiltered() {
		return StringUtils.isNotEmpty(getFilteredName()) && isFiltered;
	}

	public String getFilteredName() {
		// return filteredName;
		return getTextField().getText();
	}

	public void setFilteredName(String aString) {
		// logger.info("setFilteredName with "+aString);
		getTextField().setText(aString);
		// filteredName = aString;
		// updateMatchingValues();
	}

	public Object getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(Object selectedObject) {
		// System.out.println("set selected object: "+selectedObject);
		Object old = getSelectedObject();
		this.selectedObject = selectedObject;
		pcSupport.firePropertyChange("selectedObject", old, selectedObject);
		if (isAcceptableValue(selectedObject)) {
			setSelectedValue((T) selectedObject);
		} else {
			setSelectedValue(null);
		}
	}

	public T getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(T selectedValue) {
		// System.out.println("set selected value: "+selectedValue);
		T old = getSelectedValue();
		this.selectedValue = selectedValue;
		pcSupport.firePropertyChange("selectedValue", old, selectedValue);
		if (getSelectedObject() != getSelectedValue()) {
			setSelectedObject(selectedValue);
		}
	}

	private void updateMatchingValues() {
		final List<T> oldMatchingValues = new ArrayList<T>(getMatchingValues());
		// System.out.println("updateMatchingValues() with " + getFilteredName());
		matchingValues.clear();
		if (getAllSelectableValues() != null && getFilteredName() != null) {
			isFiltered = true;
			for (T next : getAllSelectableValues()) {
				if (isAcceptableValue(next) && matches(next, getFilteredName())) {
					matchingValues.add(next);
				}
			}
		}
		logger.fine("Objects matching with " + getFilteredName() + " found " + matchingValues.size() + " values");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pcSupport.firePropertyChange("matchingValues", oldMatchingValues, getMatchingValues());
				if (matchingValues.size() == 1) {
					setSelectedValue(matchingValues.get(0));
				}
			}
		});

		/*pcSupport.firePropertyChange("matchingValues", oldMatchingValues, getMatchingValues());
		
		if (matchingValues.size() == 1) {
			setSelectedValue(matchingValues.get(0));
		}*/
	}

	private void clearMatchingValues() {
		isFiltered = false;
		List<T> oldMatchingValues = new ArrayList<T>(getMatchingValues());
		matchingValues.clear();
		pcSupport.firePropertyChange("matchingValues", oldMatchingValues, null);
	}

	private FIBBrowserWidget getFIBBrowserWidget() {
		return ((SelectorDetailsPanel) getCustomPanel()).retrieveFIBBrowserWidget();
	}

	private FIBListWidget getFIBListWidget() {
		return ((SelectorDetailsPanel) getCustomPanel()).retrieveFIBListWidget();
	}

	/**
	 * This method is used to retrieve all potential values when implementing completion<br>
	 * Completion will be performed on that selectable values<br>
	 * Default implementation is to iterate on all values of browser, please take care to infinite loops.<br>
	 * 
	 * Override when required
	 */
	protected Collection<T> getAllSelectableValues() {
		return ((SelectorDetailsPanel) getCustomPanel()).getAllSelectableValues();
	}

	/**
	 * Override when required
	 */
	protected boolean matches(T o, String filteredName) {
		return o != null && StringUtils.isNotEmpty(renderedString(o))
				&& (renderedString(o)).toUpperCase().indexOf(filteredName.toUpperCase()) > -1;
		/*if (o instanceof FlexoModelObject) {
			return ((FlexoModelObject) o).getName() != null
					&& ((FlexoModelObject) o).getName().toUpperCase().indexOf(filteredName.toUpperCase()) > -1;
		}*/
		// return false;
	}

	public List<T> getMatchingValues() {
		return matchingValues;
	}

	// FIBModelObjectSelector is applicable to something else than objects in a project
	@Deprecated
	public FlexoProject getProject() {
		return project;
	}

	// FIBModelObjectSelector is applicable to something else than objects in a project
	@Deprecated
	@CustomComponentParameter(name = "project", type = CustomComponentParameter.Type.MANDATORY)
	public void setProject(FlexoProject project) {
		if (project == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Passing null project. If you rely on project this is unlikely to work");
			}
		}
		this.project = project;
		pcSupport.firePropertyChange("project", null, project);
	}

	@Override
	public void setRevertValue(T oldValue) {
		if (oldValue != null) {
			_revertValue = oldValue;
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public T getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(T editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		if (flexoController != null) {
			_selectorPanel.getController().setFlexoController(flexoController);
		}
		return _selectorPanel;
	}

	protected SelectorDetailsPanel makeCustomPanel(T editedObject) {
		return new SelectorDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(T editedObject) {
		// logger.info("updateCustomPanel with " + editedObject + " _selectorPanel=" + _selectorPanel);
		setSelectedObject(editedObject);
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	protected SelectorFIBController makeCustomFIBController(FIBComponent fibComponent) {
		return new SelectorFIBController(fibComponent, FIBModelObjectSelector.this);
	}

	public class SelectorDetailsPanel extends ResizablePanel {
		private final FIBComponent fibComponent;
		private final FIBView fibView;
		private final SelectorFIBController controller;

		protected SelectorDetailsPanel(T anObject) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(getFIBFile());
			controller = makeCustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(FIBModelObjectSelector.this, true);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

			selectValue(anObject);
		}

		private void selectValue(T value) {
			FIBBrowserWidget browserWidget = retrieveFIBBrowserWidget();
			if (browserWidget != null) {
				// Force reselect value because tree may have been recomputed
				browserWidget.setSelected(value);
			}
		}

		public void update() {
			controller.setDataObject(FIBModelObjectSelector.this);
			// logger.info("update() selectedValue=" + getSelectedValue() + " selectedObject=" + getSelectedObject());
			selectValue(getSelectedValue());
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
		}

		protected Set<T> getAllSelectableValues() {
			Set<T> returned = new HashSet<T>();
			FIBBrowserWidget browserWidget = retrieveFIBBrowserWidget();
			if (browserWidget == null) {
				return null;
			}
			Iterator<Object> it = browserWidget.getBrowserModel().recursivelyExploreModelToRetrieveContents();
			while (it.hasNext()) {
				Object o = it.next();
				if (getRepresentedType().isAssignableFrom(o.getClass())) {
					returned.add((T) o);
				}
			}
			return returned;
		}

		private FIBBrowserWidget retrieveFIBBrowserWidget() {
			List<FIBComponent> listComponent = fibComponent.retrieveAllSubComponents();
			for (FIBComponent c : listComponent) {
				if (c instanceof FIBBrowser) {
					return (FIBBrowserWidget) controller.viewForComponent(c);
				}
			}
			return null;
		}

		private FIBListWidget retrieveFIBListWidget() {
			List<FIBComponent> listComponent = fibComponent.retrieveAllSubComponents();
			for (FIBComponent c : listComponent) {
				if (c instanceof FIBList) {
					return (FIBListWidget) controller.viewForComponent(c);
				}
			}
			return null;
		}

		public FIBComponent getFIBComponent() {
			return fibComponent;
		}

		public SelectorFIBController getController() {
			return controller;
		}

		public FIBView getFIBView() {
			return fibView;
		}
	}

	public static class SelectorFIBController extends FlexoFIBController {
		private final FIBModelObjectSelector selector;

		public SelectorFIBController(FIBComponent component, FIBModelObjectSelector selector) {
			super(component);
			this.selector = selector;
		}

		public void selectedObjectChanged() {
			selector.setEditedObject(selector.selectedValue);
		}

		public void apply() {
			selector.apply();
		}

		public void cancel() {
			selector.cancel();
		}

		public void reset() {
			selector.setEditedObject(null);
			selector.setSelectedObject(null);
			selector.setSelectedValue(null);
			selector.apply();
		}

		protected final Icon decorateIcon(FlexoObject object, Icon returned) {
			if (AdvancedPrefs.getHightlightUncommentedItem() && object != null && object.isDescriptionImportant()
					&& !object.hasDescription()) {
				if (returned instanceof ImageIcon) {
					returned = IconFactory.getImageIcon((ImageIcon) returned, new IconMarker[] { IconLibrary.WARNING });
				} else {
					logger.severe("CANNOT decorate a non ImageIcon for " + this);
				}
			}
			return returned;
		}

		class ColorSwapFilter extends RGBImageFilter {
			private final int target1;
			private final int replacement1;
			private final int target2;
			private final int replacement2;

			public ColorSwapFilter(Color target1, Color replacement1, Color target2, Color replacement2) {
				this.target1 = target1.getRGB();
				this.replacement1 = replacement1.getRGB();
				this.target2 = target2.getRGB();
				this.replacement2 = replacement2.getRGB();
			}

			@Override
			public int filterRGB(int x, int y, int rgb) {
				if (rgb == target1) {
					return replacement1;
				} else if (rgb == target2) {
					return replacement2;
				}
				return rgb;
			}
		}

	}

	/*
	  @Override public void setEditedObject(BackgroundStyle object) {
	  logger.info("setEditedObject with "+object);
	  super.setEditedObject(object); }
	 */

	@Override
	public void apply() {
		clearMatchingValues();
		setEditedObject(getSelectedValue());
		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	/*
	 * protected void pointerLeavesPopup() { cancel(); }
	 */

	public FIBComponent getFIBComponent() {
		if (getSelectorPanel() != null) {
			return getSelectorPanel().getFIBComponent();
		}
		return null;
	}

	protected SelectorFIBController getController() {
		if (getSelectorPanel() == null) {
			return null;
		}
		return getSelectorPanel().getController();
	}

	protected FIBBrowser getFIBBrowser() {
		if (getFIBComponent() == null) {
			return null;
		}
		List<FIBComponent> listComponent = getFIBComponent().retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowser) c;
			}
		}
		return null;
	}

	protected FIBBrowserWidget retrieveFIBBrowserWidget() {
		if (getFIBComponent() == null) {
			return null;
		}
		if (getController() == null) {
			return null;
		}
		List<FIBComponent> listComponent = getFIBComponent().retrieveAllSubComponents();
		for (FIBComponent c : listComponent) {
			if (c instanceof FIBBrowser) {
				return (FIBBrowserWidget) getController().viewForComponent(c);
			}
		}
		return null;
	}

	public SelectorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	public FIBModelObjectSelector getJComponent() {
		return this;
	}

	@Override
	public String renderedString(T editedObject) {
		if (editedObject == null) {
			return "";
		}
		if (editedObject instanceof FlexoObject) {
			return ((FlexoObject) editedObject).getFullyQualifiedName();
		}
		return editedObject.toString();
	}

	/**
	 * Override when required
	 */
	protected boolean isAcceptableValue(Object o) {
		// System.out.println("acceptable ? " + o);
		if (o == null) {
			return false;
		}

		if (!getRepresentedType().isAssignableFrom(o.getClass())) {
			return false;
		}
		return evaluateSelectableCondition((T) o);
	}

	private String _selectableConditionAsString = null;
	private DataBinding<Boolean> _selectableCondition;

	public DataBinding<Boolean> getSelectableConditionDataBinding() {
		if (_selectableCondition != null) {
			return _selectableCondition;
		}
		if (_selectableConditionAsString == null || StringUtils.isEmpty(_selectableConditionAsString)) {
			return null;
		}
		_selectableCondition = new DataBinding<Boolean>(_selectableConditionAsString);
		_selectableCondition.setOwner(component);
		_selectableCondition.setDeclaredType(Boolean.class);
		_selectableCondition.setBindingDefinitionType(BindingDefinitionType.GET);
		// System.out.println("setSelectableCondition with "+_selectableCondition+" valid ? "+_selectableCondition.isValid());
		return _selectableCondition;
	}

	public String getSelectableCondition() {
		return _selectableConditionAsString;
	}

	@CustomComponentParameter(name = "selectableCondition", type = CustomComponentParameter.Type.OPTIONAL)
	public void setSelectableCondition(String aCondition) {
		_selectableConditionAsString = aCondition;
		_selectableCondition = null;
	}

	public boolean evaluateSelectableCondition(T candidateValue) {
		if (getSelectableConditionDataBinding() == null) {
			return true;
		}
		setCandidateValue(candidateValue);
		boolean returned = true;
		try {
			returned = getSelectableConditionDataBinding().getBindingValue(getSelectorPanel().getFIBView().getBindingEvaluationContext());
		} catch (TypeMismatchException e) {
			e.printStackTrace();
		} catch (NullReferenceException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return returned;
	}

	// Used for computation of "isAcceptableValue()?"
	public T getCandidateValue() {
		return candidateValue;
	}

	// Used for computation of "isAcceptableValue()?"
	public void setCandidateValue(T candidateValue) {
		this.candidateValue = candidateValue;
	}

	public FlexoController getFlexoController() {
		return flexoController;
	}

	public void setFlexoController(FlexoController flexoController) {
		this.flexoController = flexoController;
		if (_selectorPanel != null) {
			_selectorPanel.getController().setFlexoController(flexoController);
		}
	}

	/*public static void testSelector(final FIBModelObjectSelector selector) {
		final FlexoProject prj = loadProject();
		selector.createCustomPanel(null);
		selector.setProject(prj);
		FIBAbstractEditor editor = new FIBAbstractEditor() {

			@Override
			public Object[] getData() {
				return FIBAbstractEditor.makeArray(selector);
			}

			@Override
			public File getFIBFile() {
				return selector.getFIBFile();
			}

			@Override
			public FIBController getController() {
				return selector.getSelectorPanel().controller;
			}

			public FIBController makeNewController() {
				return selector.makeCustomFIBController(selector.getSelectorPanel().fibComponent);
			}

		};

		editor.addAction("set_filtered_name", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selector.setFilteredName("T");
				selector.updateMatchingValues();
			}
		});
		editor.launch();
	}

	protected static final FlexoEditorFactory EDITOR_FACTORY = new FlexoEditorFactory() {
		@Override
		public DefaultFlexoEditor makeFlexoEditor(FlexoProject project) {
			return new DefaultFlexoEditor(project);
		}
	};

	public static FileResource PRJ_FILE = new FileResource("Prj/TestBrowser.prj");

	public static FlexoProject loadProject() {
		File projectFile = PRJ_FILE;
		FlexoProject project = null;
		FlexoEditor editor;
		logger.info("Found project " + projectFile.getAbsolutePath());
		try {
			editor = FlexoResourceManager.initializeExistingProject(projectFile, EDITOR_FACTORY, null);
			project = editor.getProject();
		} catch (ProjectLoadingCancelledException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectInitializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Successfully loaded project " + projectFile.getAbsolutePath());

		return project;
	}*/
}
