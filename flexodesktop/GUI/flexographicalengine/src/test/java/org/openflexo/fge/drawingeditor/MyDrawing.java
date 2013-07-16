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

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(MyDrawingImpl.class)
@XMLElement(xmlTag = "MyDrawing")
public interface MyDrawing extends MyDrawingElement<MyDrawing, DrawingGraphicalRepresentation> {

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

	// ces methodes la ne devraient pas etre necessaire
	/*@Override
	@Getter(value = GRAPHICAL_REPRESENTATION)
	@XMLElement
	public MyDrawingGraphicalRepresentation getGraphicalRepresentation();

	@Override
	@Setter(value = GRAPHICAL_REPRESENTATION)
	public void setGraphicalRepresentation(MyDrawingGraphicalRepresentation graphicalRepresentation);
	*/
}
