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
package org.openflexo.components.tabular.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.FlexoCst;
import org.openflexo.components.widget.AbstractBrowserSelector;
import org.openflexo.components.widget.DMEntitySelector;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.rm.FlexoProject;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public abstract class EntitySelectorColumn<D extends FlexoModelObject,T extends DMEntity> extends SelectorColumn<D, T>
{

    protected String STRING_REPRESENTATION_WHEN_NULL = null;

    protected static final Logger logger = Logger.getLogger(EntitySelectorColumn.class.getPackage().getName());

    private Class<T> entityClass;

    public EntitySelectorColumn(String title, int defaultWidth, FlexoProject project, Class<T> entityClass)
    {
        super(title, defaultWidth, project);
        this.entityClass = entityClass;
    }

    @Override
	public Class<T> getValueClass()
    {
        return entityClass;
    }

    private DMEntitySelector<T> _viewSelector;

    private DMEntitySelector<T> _editSelector;

    @Override
	protected AbstractBrowserSelector<T> getViewSelector(D rowObject, T value)
    {
        if (_viewSelector == null) {
            _viewSelector = new DMEntitySelector<T>(_project, null,entityClass);
            _viewSelector.setFont(FlexoCst.MEDIUM_FONT);
            if (STRING_REPRESENTATION_WHEN_NULL != null) {
                _viewSelector.setNullStringRepresentation(STRING_REPRESENTATION_WHEN_NULL);
            }
        }
        _viewSelector.setEditedObject(value);
        return _viewSelector;
    }

    @Override
	protected AbstractBrowserSelector<T> getEditSelector(D rowObject, T value)
    {
        if (_editSelector == null) {
            _editSelector = new DMEntitySelector<T>(_project, null,entityClass) {
                @Override
				public void apply()
                {
                    super.apply();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Apply");
                    if (_editedRowObject != null) {
                        setValue(_editedRowObject, getEditedObject());
                    }
                }

                @Override
				public void cancel()
                {
                    super.cancel();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Cancel");
                    if (_editedRowObject != null) {
                        setValue(_editedRowObject, getRevertValue());
                    }
                }
            };
            _editSelector.setFont(FlexoCst.NORMAL_FONT);
        }
        if (STRING_REPRESENTATION_WHEN_NULL != null) {
            _editSelector.setNullStringRepresentation(STRING_REPRESENTATION_WHEN_NULL);
        }
        _editSelector.setEditedObject(value);
        _editSelector.setRevertValue(value);
        return _editSelector;
    }

    public void setNullStringRepresentation(String aString)
    {
        STRING_REPRESENTATION_WHEN_NULL = aString;
    }

}
