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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Widget allowing to select a component while browing component library
 *
 * @author sguerin
 *
 */
public abstract class AbstractComponentSelector<T extends ComponentDefinition> extends AbstractBrowserSelector<T>
{

    protected static final String EMPTY_STRING = "";

    public AbstractComponentSelector(FlexoProject project, T component, Class selectableClass)
    {
        super(project, component, selectableClass);
    }

    public AbstractComponentSelector(FlexoProject project, T component, Class selectableClass, int cols)
    {
        super(project, component, selectableClass, cols);
    }

    protected FlexoComponentLibrary getComponentLibrary()
    {
        if (getProject() != null) {
            return getProject().getFlexoComponentLibrary();
        }
        return null;
    }

    @Override
	protected AbstractSelectorPanel<T> makeCustomPanel(T editedObject)
    {
        return new ComponentSelectorPanel();
    }

    @Override
	public String renderedString(T editedObject)
    {
        if (editedObject != null) {
            return editedObject.getName();
        }
        return EMPTY_STRING;
    }

    protected class ComponentSelectorPanel extends AbstractSelectorPanel<T>
    {
        private JButton _newButton;

        protected ComponentSelectorPanel()
        {
            super(AbstractComponentSelector.this);
        }

        @Override
		protected void init()
        {
            super.init();
            getControlPanel().add(_newButton = new JButton(FlexoLocalization.localizedForKey("new")));
            _newButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e)
                {
                    newComponent();
                }
            });

        }

        @Override
		public Dimension getDefaultSize()
        {
            Dimension returned = _browserView.getDefaultSize();
            returned.width = returned.width + 100;
            returned.height = returned.height /* + 40 */;
            return returned;
        }

        @Override
		protected ProjectBrowser createBrowser(FlexoProject project)
        {
            return new ComponentLibraryBrowser();
        }

    }

    protected class ComponentLibraryBrowser extends ProjectBrowser
    {

        protected ComponentLibraryBrowser()
        {
            super(getComponentLibrary().getProject(), false);
            init();
        }

        @Override
		public void configure()
        {
            setFilterStatus(BrowserElementType.COMPONENT_FOLDER, BrowserFilterStatus.SHOW);

            setFilterStatus(BrowserElementType.OPERATION_COMPONENT, BrowserFilterStatus.SHOW);
            setFilterStatus(BrowserElementType.REUSABLE_COMPONENT, BrowserFilterStatus.SHOW);
            setFilterStatus(BrowserElementType.TAB_COMPONENT, BrowserFilterStatus.SHOW);
            setFilterStatus(BrowserElementType.POPUP_COMPONENT, BrowserFilterStatus.SHOW);

            setFilterStatus(BrowserElementType.BLOC,BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.SEQUENCE,BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.CONDITIONAL,BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.REPETITION,BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.HTMLTABLE,BrowserFilterStatus.HIDE);
            setFilterStatus(BrowserElementType.TAB_CONTAINER,BrowserFilterStatus.HIDE);
        }

        @Override
		public FlexoModelObject getDefaultRootObject()
        {
            return getComponentLibrary().getRootFolder();
        }
    }

    public abstract void newComponent();
}
