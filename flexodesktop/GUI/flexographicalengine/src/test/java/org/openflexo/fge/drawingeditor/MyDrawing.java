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

import java.io.File;
import java.util.Vector;

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

@ModelEntity
@ImplementationClass(MyDrawingImpl.class)
public interface MyDrawing extends MyDrawingElement<MyDrawing, MyDrawingGraphicalRepresentation> {

	public File getFile();

	public void setFile(File file);

	public int getIndex();

	public void setIndex(int index);

	@Override
	public MyDrawing getDrawing();

	public String getTitle();

	public EditedDrawing getEditedDrawing();

	public boolean save();

	@Override
	public void finalizeDeserialization();

	public static class DrawingBuilder {
		public MyDrawing drawing;
	}

	public DrawingEditorFactory getFactory();

	public void setFactory(DrawingEditorFactory factory);

	@Override
	@Getter(value = CHILDS, cardinality = Cardinality.LIST)
	@XMLElement(primary = true)
	@CloningStrategy(StrategyType.CLONE)
	@Embedded
	public Vector<MyDrawingElement/*<?, ?>*/> getChilds();

	@Override
	@Setter(CHILDS)
	public void setChilds(Vector<MyDrawingElement/*<?, ?>*/> someChilds);

	@Override
	@Adder(CHILDS)
	@PastingPoint
	public void addToChilds(MyDrawingElement/*<?, ?>*/aChild);

	@Override
	@Remover(CHILDS)
	public void removeFromChilds(MyDrawingElement/*<?, ?>*/aChild);

}
