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
package org.openflexo.foundation.exec;

import java.util.logging.Logger;

import org.openflexo.foundation.wkf.ws.DeletePort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.NewPort;
import org.openflexo.logging.FlexoLogger;

public abstract class PortActivation<N extends FlexoPort> extends ControlGraphBuilder {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(PortActivation.class.getPackage().getName());

	private N port;

	public static ControlGraphBuilder getActivationPortBuilder(FlexoPort port) throws NotSupportedException {
		if (port instanceof NewPort) {
			return new NewPortActivation((NewPort) port);
		}
		if (port instanceof DeletePort) {
			return new DeletePortActivation((DeletePort) port);
		}
		throw new NotSupportedException("Dont know what to do with a " + port);
	}

	protected PortActivation(N port) {
		super();
		this.port = port;
	}

	public N getPort() {
		return port;
	}
}
