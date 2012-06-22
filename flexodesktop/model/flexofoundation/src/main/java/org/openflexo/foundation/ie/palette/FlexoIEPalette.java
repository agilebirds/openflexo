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
package org.openflexo.foundation.ie.palette;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.JDOMException;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.PaletteHasChanged;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoCSS;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.InvalidXMLDataException;
import org.openflexo.xmlcode.XMLDecoder;

public abstract class FlexoIEPalette<W extends FlexoIEPalette<W>.FlexoIEPaletteWidget> extends FlexoObservable {
	protected static final Logger logger = FlexoLogger.getLogger(FlexoIEPalette.class.getPackage().getName());

	public enum PaletteAttribute {
		SCREENSHOT {
			@Override
			public String getAttributeTag() {
				return "screenshot";
			}
		},
		IS_TOP_COMPONENT {
			@Override
			public String getAttributeTag() {
				return "isTopComponent";
			}
		},
		XML {
			@Override
			public String getAttributeTag() {
				return "xml";
			}
		},
		TARGET_CLASS_MODEL {
			@Override
			public String getAttributeTag() {
				return "target.class.model";
			}
		};
		public abstract String getAttributeTag();

		@Override
		public String toString() {
			return getAttributeTag();
		}

	}

	private final FlexoProject project;

	private Vector<W> widgets;

	public FlexoIEPalette(FlexoProject project) {
		super();
		this.project = project;
	}

	protected abstract void loadWidgets();

	public abstract boolean resizeScreenshots();

	public FlexoProject getProject() {
		return project;
	}

	public Vector<W> getWidgets() {
		if (widgets == null) {
			widgets = new Vector<W>();
			loadWidgets();
		}
		return widgets;
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		if (widgets == null) {
			return;
		}
		widgets.clear();
		loadWidgets();
		notifyPaletteHasChanged();
	}

	public void notifyPaletteHasChanged() {
		setChanged();
		notifyObservers(new PaletteHasChanged(this));
	}

	public abstract class FlexoIEPaletteWidget extends FlexoObservable {
		protected Properties properties;

		private final String name;
		protected String xml;
		protected Class<?> targetClass;
		protected boolean isTopComponent;
		protected File screenshotFile;

		public FlexoIEPaletteWidget(String name, Properties props) {
			this.name = name;
			properties = props;
			fetchXML();
			fetchTargetClass();
			fetchIsTopComponent();
		}

		public IEWidget getWidget(FlexoComponentBuilder builder) throws InvalidXMLDataException, InvalidObjectSpecificationException,
				AccessorInvocationException, InvalidModelException, IOException, JDOMException {
			IEWidget retval = (IEWidget) XMLDecoder.decodeObjectWithMapping(xml, getProject().getXmlMappings().getIEMapping(), builder,
					getProject().getStringEncoder());
			retval.removeInvalidComponentInstances();
			return retval;
		}

		/**
		 *
		 */
		protected void fetchIsTopComponent() {
			isTopComponent = Boolean.valueOf(properties.getProperty(PaletteAttribute.IS_TOP_COMPONENT.getAttributeTag()));
		}

		/**
		 *
		 */
		protected void fetchTargetClass() {
			try {
				targetClass = Class.forName(properties.getProperty(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag()));
			} catch (ClassNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find class " + properties.getProperty(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag()));
				}
			} catch (RuntimeException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.log(Level.WARNING, "Error while loading class", e);
				}
			}
		}

		/**
		 *
		 */
		protected void fetchXML() {
			xml = properties.getProperty(PaletteAttribute.XML.getAttributeTag());
			if (!xml.startsWith("<?xml")) {
				xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
			}
			properties.put(PaletteAttribute.XML.getAttributeTag(), xml);
		}

		public String getXML() {
			return xml;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}

		public boolean isTopComponent() {
			return isTopComponent;
		}

		public boolean hasScreenshot() {
			return getScreenshotFile(FlexoCSS.CONTENTO) != null && getScreenshotFile(FlexoCSS.CONTENTO).exists();
		}

		public File getScreenshotFile(FlexoCSS css) {
			return screenshotFile;
		}

		public boolean canDeleteWidget() {
			return false;
		}

		public void deleteWidget() {
			if (canDeleteWidget()) {
				getWidgets().remove(this);
				setChanged();
				notifyObservers(new DataModification(DELETED_PROPERTY, this, null));
			}
		}

		@Override
		public String getDeletedProperty() {
			return DELETED_PROPERTY;
		}

		public String getName() {
			return name;
		}

	}

}
