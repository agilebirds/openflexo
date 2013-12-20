/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.technologyadapter.excel;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.viewpoint.FMLRepresentationContext;
import org.openflexo.foundation.viewpoint.NamedViewPointObject.NamedViewPointObjectImpl;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.excel.model.ExcelObject;
import org.openflexo.xmlcode.XMLSerializable;

public class BasicExcelModelSlotURIProcessor extends NamedViewPointObjectImpl implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(BasicExcelModelSlotURIProcessor.class.getPackage().getName());

	// Properties actually used to calculate URis
	private BasicExcelModelSlot modelSlot;

	// Cache des URis Pour aller plus vite ??
	// TODO some optimization required
	private final Map<String, ExcelObject> uriCache = new HashMap<String, ExcelObject>();

	public void setModelSlot(BasicExcelModelSlot excelModelSlot) {
		modelSlot = excelModelSlot;
	}

	// Serialized properties

	protected URI typeURI;
	protected String attributeName;

	public String _getTypeURI() {
		return typeURI.toString();
	}

	public void _setTypeURI(String name) {
		typeURI = URI.create(name);
	}

	public String _getAttributeName() {
		return attributeName;
	}

	public void _setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	// Lifecycle management methods
	public void reset() {
		modelSlot = null;
	}

	public BasicExcelModelSlotURIProcessor() {
		super();
	}

	public BasicExcelModelSlotURIProcessor(String typeURI) {
		super();
		this.typeURI = URI.create(typeURI);
	}

	// URI Calculation

	public String getURIForObject(ModelSlotInstance msInstance, ExcelObject excelObject) {
		String builtURI = null;

		try {
			builtURI = URLEncoder.encode(excelObject.getUri(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.warning("Cannot process URI - Unexpected encoding error");
			e.printStackTrace();
		}

		if (builtURI != null) {
			if (uriCache.get(builtURI) == null) {
				// TODO Manage the fact that URI May Change
				uriCache.put(builtURI, excelObject);
			}
		}
		return builtURI.toString();
	}

	// get the Object given the URI
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws Exception {
		ExcelObject o = uriCache.get(objectURI);
		return o;
	}

	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return Collections.emptyList();
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {
		return this.modelSlot.getViewPoint();
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object performSuperGetter(String propertyIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperSetModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSerializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeserializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equalsObject(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperDelete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperUndelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean undelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object cloneObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object cloneObject(Object... context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreatedByCloning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBeingCloned() {
		// TODO Auto-generated method stub
		return false;
	}

}