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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.viewpoint.NamedViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.excel.model.ExcelObject;

public interface BasicExcelModelSlotURIProcessor extends NamedViewPointObject {

	// URI Calculation
	public String getURIForObject(ModelSlotInstance msInstance, ExcelObject excelObject);

	// get the Object given the URI
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) throws Exception;

	public static abstract class BasicExcelModelSlotURIProcessorImpl extends NamedViewPointObjectImpl {

		private static final Logger logger = Logger.getLogger(BasicExcelModelSlotURIProcessor.class.getPackage().getName());

		// Properties actually used to calculate URis
		private BasicExcelModelSlot modelSlot;

		// Cache des URis Pour aller plus vite ??
		// TODO some optimization required
		private final Map<String, ExcelObject> uriCache = new HashMap<String, ExcelObject>();

		// Serialized properties

		protected URI typeURI;
		protected String attributeName;

		public BasicExcelModelSlotURIProcessorImpl() {
			super();
		}

		public BasicExcelModelSlotURIProcessorImpl(String typeURI) {
			super();
			this.typeURI = URI.create(typeURI);
		}

		public void setModelSlot(BasicExcelModelSlot excelModelSlot) {
			modelSlot = excelModelSlot;
		}

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
		public String getURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ViewPoint getViewPoint() {
			return this.modelSlot.getViewPoint();
		}

	}
}