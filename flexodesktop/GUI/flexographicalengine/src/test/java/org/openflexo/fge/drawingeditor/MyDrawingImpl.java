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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.jdom2.JDOMException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;

public abstract class MyDrawingImpl extends MyDrawingElementImpl<MyDrawing, MyDrawingGraphicalRepresentation> implements MyDrawing {

	private static final Logger logger = FlexoLogger.getLogger(MyDrawingImpl.class.getPackage().getName());

	// private static XMLMapping mapping;

	private int index;
	private File file = null;
	private EditedDrawing editedDrawing = new EditedDrawing(this);

	private DrawingEditorFactory factory;

	/*static {
		try {
			mapping = new XMLMapping(new FileResource("Mappings/DrawingMapping.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	// Used by PAMELA, do not use it
	public MyDrawingImpl() {
		super(null);
	}

	// Called for LOAD
	public MyDrawingImpl(DrawingBuilder builder) {
		this();
		if (builder != null) {
			builder.drawing = this;
		}
		// initializeDeserialization();
	}

	@Override
	public MyDrawingImpl getDrawing() {
		return this;
	}

	@Override
	public DrawingEditorFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(DrawingEditorFactory factory) {
		this.factory = factory;
		setGraphicalRepresentation(factory.makeNewDrawingGR(editedDrawing));
		editedDrawing.init(factory);
	}

	@Override
	public String getTitle() {
		if (file != null) {
			return file.getName();
		} else {
			return FlexoLocalization.localizedForKey(TestDrawingEditor.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	@Override
	public EditedDrawing getEditedDrawing() {
		return editedDrawing;
	}

	@Override
	public boolean save() {
		System.out.println("Saving " + file);

		try {
			getFactory().serialize(this, new FileOutputStream(file));
			System.out.println("Saved " + file.getAbsolutePath());
			System.out.println(getFactory().stringRepresentation(this));
			return true;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;

		/*XMLCoder coder = new XMLCoder(mapping);

		try {
			coder.encodeObject(this, new FileOutputStream(file));
			clearChanged();
			logger.info("Succeeded to save: " + file);
			System.out.println("> " + new XMLCoder(mapping).encodeObject(this));
			System.out.println("Et j'ai ca aussi: " + getFactory().getSerializer().serializeAsString(this));
			return true;
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
				return false;
		 */

	}

	@Override
	public void finalizeDeserialization() {
		// super.finalizeDeserialization();

		/*for (MyDrawingElement e : childs) {
			getDrawing().getEditedDrawing().addDrawable(e, this);
		}*/
		_finalizeDeserializationFor(this);
		_finalizeDeserializationFor2(this);

		editedDrawing.getDrawingGraphicalRepresentation().startConnectorObserving();

	}

	private void _finalizeDeserializationFor(MyDrawingElement<?, ?> element) {
		// element.getGraphicalRepresentation().resetToDefaultIdentifier();

		element.setDrawing(this);
		element.getGraphicalRepresentation().setDrawing(getEditedDrawing());

		for (MyDrawingElement<?, ?> e : element.getChilds()) {
			_finalizeDeserializationFor(e);
		}
	}

	private void _finalizeDeserializationFor2(MyDrawingElement<?, ?> element) {

		/*if (element instanceof MyConnector) {
			Connector c = ((MyConnector) element).getGraphicalRepresentation().getConnector();
			if (c instanceof LineConnector) {
				((LineConnectorImpl) c).updateControlPoints();
			}
		}*/

		// element.getGraphicalRepresentation().resetToDefaultIdentifier();

		/*System.out.println(">>>>>>> " + element);
		System.out.println("Drawing=" + element.getDrawing());
		System.out.println("GR= " + element.getGraphicalRepresentation());
		System.out.println("drawable= " + element.getGraphicalRepresentation().getDrawable());
		System.out.println("gr_drawing=" + element.getGraphicalRepresentation().getDrawing());*/

		for (MyDrawingElement<?, ?> e : element.getChilds()) {
			getDrawing().getEditedDrawing().addDrawable(e, element);
			_finalizeDeserializationFor2(e);
		}
	}

	public static MyDrawing load(File file, DrawingEditorFactory factory) {
		logger.info("Loading " + file);

		try {
			MyDrawing returned = (MyDrawing) factory.deserialize(new FileInputStream(file));
			System.out.println("Loaded " + factory.stringRepresentation(returned));
			returned.finalizeDeserialization();
			returned.setFactory(factory);
			returned.setFile(file);
			return returned;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		}
		return null;

		/*XMLDecoder decoder = new XMLDecoder(mapping, new DrawingBuilder());

		try {
			MyDrawingImpl drawing = (MyDrawingImpl) decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init(factory);
			logger.info("Succeeded to load: " + file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}*/
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

}
