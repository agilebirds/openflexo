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
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.excel.model.ExcelObject;
import org.openflexo.xmlcode.XMLSerializable;

/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XMLType wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

public class BasicExcelModelSlotURIProcessor extends NamedViewPointObject implements XMLSerializable {

	private static final Logger logger = Logger.getLogger(BasicExcelModelSlotURIProcessor.class.getPackage().getName());

	// Properties actually used to calculate URis
	private BasicExcelModelSlot modelSlot;

	// Cache des URis Pour aller plus vite ??
	// TODO some optimization required
	private Map<String, ExcelObject> uriCache = new HashMap<String, ExcelObject>();

	public void setModelSlot(BasicExcelModelSlot excelModelSlot) {
		modelSlot = excelModelSlot;
	}

	// Serialized properties

	protected URI typeURI;
	protected String attributeName;

	public String _getTypeURI() {
		return typeURI.toString();
	}

	// TODO WARNING!!! Pb avec les typeURI....
	public void _setTypeURI(String name) {
		typeURI = URI.create(name);
		bindtypeURIToMappedClass();
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

	public void bindtypeURIToMappedClass() {
		// TODO?
	}

	public BasicExcelModelSlotURIProcessor() {
		super((VirtualModelBuilder) null);
	}

	public BasicExcelModelSlotURIProcessor(String typeURI) {
		super((VirtualModelBuilder) null);
		this.typeURI = URI.create(typeURI);
	}

	// URI Calculation

	public String getURIForObject(ModelSlotInstance msInstance, ExcelObject excelObject) {
		String builtURI = null;
		StringBuffer completeURIStr = new StringBuffer();

		Object value = excelObject;
		try {
			builtURI = URLEncoder.encode(value.toString(), "UTF-8");
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
		completeURIStr.append(typeURI.getScheme()).append("://").append(typeURI.getHost()).append(typeURI.getPath()).append("?")
				.append(builtURI).append("#").append(typeURI.getFragment());
		return completeURIStr.toString();
	}

	// get the Object given the URI
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws Exception {

		ExcelObject o = uriCache.get(objectURI);

		// retrieve object
		if (o == null) {

		}

		return o;
	}

	// get the right URIProcessor for URI

	static public String retrieveTypeURI(ModelSlotInstance msInstance, String objectURI) {

		URI fullURI;
		StringBuffer typeURIStr = new StringBuffer();

		fullURI = URI.create(objectURI);
		typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost()).append(fullURI.getPath()).append("#")
				.append(fullURI.getFragment());

		return typeURIStr.toString();
	}

	// TODO ... To support Notification

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

}