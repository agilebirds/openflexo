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
package org.openflexo.fge.drawingeditor;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PastingPoint;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.xmlcode.XMLSerializable;

@ModelEntity
@ImplementationClass(MyDrawingElementImpl.class)
public interface MyDrawingElement<M extends MyDrawingElement<M, G>, G extends GraphicalRepresentation<M>> extends XMLSerializable,
		Cloneable, Observer, AccessibleProxyObject, CloneableProxyObject {

	public static final String GRAPHICAL_REPRESENTATION = "graphicalRepresentation";
	public static final String DRAWING = "drawing";
	public static final String CHILDS = "childs";

	@Getter(value = CHILDS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public Vector<MyDrawingElement/*<?, ?>*/> getChilds();

	@Setter(CHILDS)
	public void setChilds(Vector<MyDrawingElement/*<?, ?>*/> someChilds);

	@Adder(CHILDS)
	@PastingPoint
	public void addToChilds(MyDrawingElement/*<?, ?>*/aChild);

	@Remover(CHILDS)
	public void removeFromChilds(MyDrawingElement/*<?, ?>*/aChild);

	@Getter(value = DRAWING)
	public MyDrawing getDrawing();

	@Setter(value = DRAWING)
	public void setDrawing(MyDrawing drawing);

	@Getter(value = GRAPHICAL_REPRESENTATION)
	@XMLElement
	public G getGraphicalRepresentation();

	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(G graphicalRepresentation);

	// public void initializeDeserialization();

	public void finalizeDeserialization();

	public MyDrawingElement<M, G> clone();

	@Override
	public void update(Observable o, Object arg);

	public void setChanged();

	public boolean hasChanged();
}
