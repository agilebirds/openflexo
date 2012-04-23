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
package org.openflexo.fge.geomedit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.openflexo.fge.geomedit.gr.GeometricDrawingGraphicalRepresentation;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;

public class GeometricSet implements XMLSerializable, Cloneable, TreeNode {

	private static final Logger logger = FlexoLogger.getLogger(GeometricSet.class.getPackage().getName());

	private static XMLMapping mapping;

	private static int totalOccurences = 0;

	private Vector<GeometricObject> childs;

	private int index;

	static {
		try {
			mapping = new XMLMapping(new FileResource("Mappings/GeomEditMapping.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File file = null;

	private GeometricDrawing editedDrawing;

	// Called for NEW
	public static GeometricSet makeNewDrawing() {
		totalOccurences++;
		GeometricSet returned = new GeometricSet();
		returned.index = totalOccurences;
		returned.editedDrawing.init();
		return returned;
	}

	private GeometricSet() {
		super();
		editedDrawing = new GeometricDrawing(this);
		childs = new Vector<GeometricObject>();
	}

	// Called for LOAD
	public GeometricSet(GeomEditBuilder builder) {
		this();
		builder.drawing = editedDrawing;
		initializeDeserialization();
	}

	public String getTitle() {
		if (file != null) {
			return file.getName();
		} else {
			return FlexoLocalization.localizedForKey(GeomEdit.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	public GeometricDrawingGraphicalRepresentation getGraphicalRepresentation() {
		return editedDrawing.getDrawingGraphicalRepresentation();
	}

	public void setGraphicalRepresentation(GeometricDrawingGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		editedDrawing.setDrawingGraphicalRepresentation(aGR);
	}

	public GeometricDrawing getEditedDrawing() {
		return editedDrawing;
	}

	public void save() {
		System.out.println("Saving " + file);

		for (GeometricObject o : getChilds()) {
			o.resetResultingGeometricObject();
		}

		XMLCoder coder = new XMLCoder(mapping);

		try {
			coder.encodeObject(this, new FileOutputStream(file));
			logger.info("Succeeded to save: " + file);
			System.out.println("> " + (new XMLCoder(mapping)).encodeObject(this));
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	public static GeometricSet load(File file) {
		logger.info("Loading " + file);

		XMLDecoder decoder = new XMLDecoder(mapping, new GeomEditBuilder());

		try {
			GeometricSet drawing = (GeometricSet) decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init();
			logger.info("Succeeded to load: " + file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static class GeomEditBuilder {
		public GeometricDrawing drawing;
	}

	public Vector<GeometricObject> getChilds() {
		return childs;
	}

	public void setChilds(Vector<GeometricObject> someChilds) {
		childs.addAll(someChilds);
	}

	public void addToChilds(GeometricObject aChild) {
		childs.add(aChild);
		// System.out.println("Add "+aChild+" isDeserializing="+isDeserializing());
		if (!isDeserializing()) {
			editedDrawing.addDrawable(aChild, this);
		}
	}

	public void removeFromChilds(GeometricObject aChild) {
		childs.remove(aChild);
	}

	private boolean isDeserializing = false;

	public void initializeDeserialization() {
		isDeserializing = true;
	}

	public void finalizeDeserialization() {
		isDeserializing = false;
		for (GeometricObject aChild : childs) {
			editedDrawing.addDrawable(aChild, this);
		}
		for (GeometricObject aChild : childs) {
			aChild.getGraphicalRepresentation().rebuildControlPoints();
		}
		for (GeometricObject aChild : childs) {
			aChild.getConstruction().refresh();
		}
	}

	public boolean isDeserializing() {
		return isDeserializing;
	}

	@Override
	public GeometricSet clone() {
		try {
			return (GeometricSet) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			// cannot happen since we are clonable
			return null;
		}
	}

	@Override
	public Enumeration children() {
		return childs.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return childs.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return childs.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return childs.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
