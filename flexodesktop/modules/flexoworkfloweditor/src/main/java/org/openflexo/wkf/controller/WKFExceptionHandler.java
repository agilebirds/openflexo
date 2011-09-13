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

import org.openflexo.foundation.wkf.action.BindButtonsToActionNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.toolbox.EmptyVector;


/**
 * 
 * This class is intended to handle the various actions to perform (in the UI)
 * when associating an OperationComponent with an OperationNode.
 * 
 * @author gpolet
 * 
 */
public class WKFExceptionHandler
{

    /**
     * @param assoc -
     *            the exception thrown by the model when associating an
     *            operation with its component
     */
    public static void handleAssociation(OperationAssociatedWithComponentSuccessfully assoc, WKFController controller)
    {
        BindButtonsToActionNode anAction = BindButtonsToActionNode.actionType.makeNewAction(assoc.getNode(),
                EmptyVector.EMPTY_VECTOR, controller.getEditor());
        anAction.setException(assoc);
        anAction.doAction();
    }
}
