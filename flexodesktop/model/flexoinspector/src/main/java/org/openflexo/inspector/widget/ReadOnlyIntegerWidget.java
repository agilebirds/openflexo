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
package org.openflexo.inspector.widget;

import java.util.logging.Logger;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * Represents a widget able to edit an int or an Integer object in read only mode
 *
 * @author sguerin
 */
public class ReadOnlyIntegerWidget extends IntegerWidget
{

    static final Logger logger = Logger.getLogger(ReadOnlyIntegerWidget.class.getPackage().getName());

    /**
     * @param model
     */
    public ReadOnlyIntegerWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        valueChooser.setEnabled(false);
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
    public synchronized void updateModelFromWidget()
    {
    }


}
