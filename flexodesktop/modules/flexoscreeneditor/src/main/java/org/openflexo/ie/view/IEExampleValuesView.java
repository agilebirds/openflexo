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
package org.openflexo.ie.view;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.ie.view.controller.ComponentBrowser;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.selection.SelectionListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.FlexoController;


public class IEExampleValuesView extends JPanel implements SelectionSynchronizedModuleView<ComponentInstance>
{

    private static final Logger logger = Logger.getLogger(IEExampleValuesView.class.getPackage().getName());

    private IEController _controller;

    private IETabularBrowserView _treeTable;

    private static final String EMPTY_STRING = "";

    protected ComponentInstance rootObject;

    public IEExampleValuesView(IEController controller, ComponentInstance rootObject)
    {
        super();
        _controller = controller;
        this.rootObject = rootObject;
        // TabularBrowserModel model = new
        // TabularBrowserModel(makeBrowser(process,controller),controller.getProject(),"
        // ",150);
        TabularBrowserModel model = makeTabularBrowserModel(controller.getProject().getFlexoComponentLibrary());
        model.addToColumns(new EditableStringColumn<FlexoModelObject>("default_values", 400) {
            @Override
            public boolean isCellEditableFor(FlexoModelObject object)
            {
                return hasExampleValue(object);
            }

            @Override
            public String getValue(FlexoModelObject object)
            {
                if (hasExampleValue(object)) {
                    return getExampleValue(object);
                } else
                    return EMPTY_STRING;
            }

            @Override
            public void setValue(FlexoModelObject object, String aValue)
            {
                if (hasExampleValue(object)) {
                    setExampleValue(object, aValue);
                }
            }
        });
        _treeTable = new IETabularBrowserView(controller, model, 10);
        setLayout(new BorderLayout());
        add(_treeTable, BorderLayout.CENTER);
        validate();
    }

    @Override
	public ComponentInstance getRepresentedObject()
    {
        return rootObject;
    }

    @Override
	public void deleteModuleView()
    {
    	_controller.removeModuleView(this);
        logger.warning("implements me !");
    }

    @Override
	public FlexoPerspective getPerspective()
    {
        return _controller.EXAMPLE_VALUE_PERSPECTIVE;
    }

    public IETabularBrowserView getTabularBrowserView()
    {
        return _treeTable;
    }

    // Make abstract beyond

    public TabularBrowserModel makeTabularBrowserModel(final FlexoComponentLibrary lib)
    {
        BrowserConfiguration configuration = new BrowserConfiguration() {
            @Override
			public FlexoProject getProject()
            {
                return rootObject.getProject();
            }

            @Override
			public void configure(ProjectBrowser browser)
            {
                // browser.setFilterStatus(BrowserElementType.TABLE,
                // BrowserFilter.ACTIVATE, true);
                browser.setFilterStatus(BrowserElementType.BLOC, BrowserFilterStatus.HIDE, true);
                browser.setFilterStatus(BrowserElementType.HTMLTABLE, BrowserFilterStatus.HIDE, true);
                browser.setFilterStatus(BrowserElementType.TR, BrowserFilterStatus.HIDE, true);
                browser.setFilterStatus(BrowserElementType.TD, BrowserFilterStatus.HIDE, true);
                browser.setFilterStatus(BrowserElementType.CHECKBOX, BrowserFilterStatus.HIDE);
                browser.setFilterStatus(BrowserElementType.BUTTON, BrowserFilterStatus.HIDE);
                browser.setFilterStatus(BrowserElementType.RADIOBUTTON, BrowserFilterStatus.HIDE);
                browser.setFilterStatus(BrowserElementType.FILEUPLOAD, BrowserFilterStatus.HIDE);
            }

            @Override
			public FlexoModelObject getDefaultRootObject()
            {
                return (rootObject).getComponentDefinition();
            }

            @Override
			public BrowserElementFactory getBrowserElementFactory()
            {
                return null; // Use default factory
            }
        };

        return new TabularBrowserModel(configuration, " ", 150);
    }

    public ProjectBrowser makeBrowser(FlexoModelObject rootObject, FlexoController controller)
    {
        ComponentBrowser returned = new ComponentBrowser((IEController) controller);
        returned.setRootObject(rootObject);
        return returned;
    }

    public boolean hasExampleValue(FlexoModelObject object)
    {
        if (object instanceof IETextFieldWidget) {
            return true;
        } else if (object instanceof IEDropDownWidget) {
            return true;
        } else if (object instanceof IELabelWidget) {
            return true;
        } else if (object instanceof IEBlocWidget) {
            return true;
        } else if (object instanceof IEStringWidget)
            return true;
        else if (object instanceof IETextAreaWidget)
            return true;
        else if (object instanceof IEHeaderWidget)
            return true;
        return false;
    }

    public String getExampleValue(FlexoModelObject object)
    {
        if (object instanceof IETextFieldWidget) {
            return ((IETextFieldWidget) object).getValue();
        } else if (object instanceof IEDropDownWidget) {
            return ((IEDropDownWidget) object).getExampleList();
        } else if (object instanceof IELabelWidget) {
            return ((IELabelWidget) object).getValue();
        } else if (object instanceof IEBlocWidget) {
            return ((IEBlocWidget) object).getTitle();
        } else if (object instanceof IEStringWidget)
            return ((IEStringWidget) object).getValue();
        else if (object instanceof IETextAreaWidget)
            return ((IETextAreaWidget) object).getValue();
        else if (object instanceof IEHeaderWidget)
            return ((IEHeaderWidget)object).getValue();
        return null;
    }

    public void setExampleValue(FlexoModelObject object, String value)
    {
        if (object instanceof IETextFieldWidget) {
            ((IETextFieldWidget) object).setValue(value);
        } else if (object instanceof IEDropDownWidget) {
            ((IEDropDownWidget) object).setExampleList(value);
        } else if (object instanceof IELabelWidget) {
            ((IELabelWidget) object).setValue(value);
        } else if (object instanceof IEBlocWidget) {
            ((IEBlocWidget) object).setTitle(value);
        } else if (object instanceof IEStringWidget)
            ((IEStringWidget) object).setValue(value);
        else if (object instanceof IETextAreaWidget)
            ((IETextAreaWidget) object).setValue(value);
        else if (object instanceof IEHeaderWidget)
            ((IEHeaderWidget) object).setValue(value);
    }

    @Override
	public void fireObjectSelected(FlexoModelObject object)
    {
        getTabularBrowserView().fireObjectSelected(object);
    }

    @Override
	public void fireObjectDeselected(FlexoModelObject object)
    {
        getTabularBrowserView().fireObjectDeselected(object);
    }

    @Override
	public void fireResetSelection()
    {
        getTabularBrowserView().fireResetSelection();
    }

    @Override
	public void fireBeginMultipleSelection()
    {
        getTabularBrowserView().fireBeginMultipleSelection();
    }

    @Override
	public void fireEndMultipleSelection()
    {
        getTabularBrowserView().fireEndMultipleSelection();
    }

    /**
     * Overrides willShow
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Overrides willHide
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
        // TODO Auto-generated method stub

    }

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management
	 * When not, Flexo will manage it's own scrollbar for you
	 *
	 * @return
	 */
	@Override
	public boolean isAutoscrolled()
	{
		return false;
	}
	@Override
	public List<SelectionListener> getSelectionListeners(){
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

 }
