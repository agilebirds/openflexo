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
package org.openflexo.module.external;

import javax.swing.JComponent;

import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.wkf.node.OperationNode;

/**
 * External view of the IE Module
 * 
 * @author sguerin
 */
public interface ExternalIEModule extends ExternalModule {

	public ExternalIEController getIEExternalController();

	public void showScreenInterface(ComponentInstance component);

	public JComponent getWOComponentView(ExternalIEController controller, IEWOComponent component);

	public JComponent createViewForOperation(OperationNode operation);

	public void finalizeScreenshot();
}
