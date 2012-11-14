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

import org.openflexo.antar.ControlGraph;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.toolbox.ToolBox;

public abstract class SendToken<E extends FlexoPostCondition> extends ControlGraphBuilder {

	private E edge;

	/**
	 * Returns control graph associated to a token sending in supplied TokenEdge
	 * 
	 * @param edge
	 *            the TokenEdge where we send a token
	 * @return the computed control graph
	 * @throws NotSupportedException
	 *             when an element contained in the model is not currently supported by execution model
	 * @throws InvalidModelException
	 *             when the model is not conform (validation should have failed) and thus workflow cannot be computed
	 */
	public static ControlGraph sendToken(FlexoPostCondition<?, ?> edge, boolean interprocedural) throws InvalidModelException,
			NotSupportedException {
		return new SendTokenOnTokenEdge(edge).makeControlGraph(interprocedural);
	}

	protected SendToken(E edge) {
		super();
		this.edge = edge;
	}

	public E getEdge() {
		return edge;
	}

	@Override
	protected String getProcedureName() {
		return "sendTokenTo"
				+ ToolBox.getJavaName(getEdge() != null ? getEdge().getFullyQualifiedName() + "_" + getEdge().getFlexoID() : "Null");
	}

}
