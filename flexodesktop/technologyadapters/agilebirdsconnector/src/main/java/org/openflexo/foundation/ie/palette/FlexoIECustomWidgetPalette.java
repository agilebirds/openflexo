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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.widget.IESequenceTopComponent;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.xml.XMLUtils2;
import org.openflexo.swing.ImageUtils;
import org.openflexo.swing.ImageUtils.ImageType;
import org.openflexo.toolbox.FileUtils;

public class FlexoIECustomWidgetPalette extends FlexoIEPalette<FlexoIECustomWidgetPalette.FlexoIECustomWidget> {

	public FlexoIECustomWidgetPalette(FlexoProject project) {
		super(project);
	}

	public File getPaletteDirectory() {
		return getProject().getIECustomPaletteDirectory();
	}

	@Override
	public boolean resizeScreenshots() {
		return true;
	}

	@Override
	protected void loadWidgets() {
		File dir = getPaletteDirectory();
		if (!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".woxml");
			}
		});
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			Properties props = new Properties();
			try {
				props.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Custom widget file not found " + file.getAbsolutePath());
				}
				continue;
			} catch (IOException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not open file " + file.getAbsolutePath());
				}
				continue;
			}
			try {
				getWidgets().add(new FlexoIECustomWidget(file.getName().substring(0, file.getName().length() - 6), props));
			} catch (RuntimeException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Caught exception while loading widget: " + file.getName());
				}
				getProject().addToFilesToDelete(file);
			}
		}
	}

	public boolean widgetWithNameExists(String name) {
		File f = new File(getProject().getIECustomPaletteDirectory(), name + ".woxml");
		return f.exists();
	}

	public void addNewWidgetToIEPaletteDirectory(IEWidget object, String widgetName, BufferedImage widgetScreenshot) {
		IEWOComponent component = object.getWOComponent();
		if (component == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not export widget to palette because WOComponent is null.");
			}
			return;
		}
		try {
			component.initializeSerialization();
			String content = object.getXMLRepresentation();
			content = content.substring(40);
			content = content.replaceAll("flexoID=\"[0-9]*\"\\s*", "");
			content = content.replaceAll("userID=\"[^\"]*\"\\s*", "");
			Properties p = new Properties();
			String targetClass = object.getClass().getName();
			p.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), targetClass);
			p.put(PaletteAttribute.XML.getAttributeTag(), content);
			p.put(PaletteAttribute.IS_TOP_COMPONENT.getAttributeTag(), String.valueOf(object.isTopComponent()));
			File dir = getPaletteDirectory();
			if (widgetScreenshot != null) {
				int i = 0;
				File newFile = new File(dir, widgetName + ".screenshot");
				while (newFile.exists()) {
					i++;
					newFile = new File(dir, widgetName + "-" + i + ".screenshot");
				}
				try {
					FileUtils.createNewFile(newFile);
					ImageUtils.saveImageToFile(widgetScreenshot, newFile, ImageType.PNG);
					p.put(PaletteAttribute.SCREENSHOT.getAttributeTag(), newFile.getName());
				} catch (RuntimeException e) {
					e.printStackTrace();
				}

			}
			if (dir != null && widgetName != null) {
				File newFile = new File(dir, widgetName + ".woxml");
				FileUtils.createNewFile(newFile);
				FileOutputStream out = new FileOutputStream(newFile);
				try {
					p.store(out, "Exported widget " + widgetName);
				} finally {
					out.close();
				}
				getWidgets().add(new FlexoIECustomWidget(widgetName, p));
				notifyPaletteHasChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			component.finalizeSerialization();
		}
	}

	public class FlexoIECustomWidget extends FlexoIEPalette<FlexoIECustomWidget>.FlexoIEPaletteWidget {

		public FlexoIECustomWidget(String name, Properties props) {
			super(name, props);
			String screenshot = properties.getProperty(PaletteAttribute.SCREENSHOT.getAttributeTag());
			if (screenshot != null) {
				screenshotFile = new File(getPaletteDirectory(), screenshot);
			}
		}

		@Override
		public boolean canDeleteWidget() {
			return true;
		}

		@Override
		public void deleteWidget() {
			super.deleteWidget();
			getWidgetFile().delete();
			if (screenshotFile != null) {
				screenshotFile.delete();
			}
		}

		private void saveToFile() throws FileNotFoundException, IOException {
			properties.store(new FileOutputStream(getWidgetFile()), "Exported widget " + getName());
		}

		/**
		 * @return
		 */
		private File getWidgetFile() {
			return new File(getPaletteDirectory(), getName() + ".woxml");
		}

		protected boolean convertTopSequenceToWidgetSequence() {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Document document = XMLUtils2.getJDOMDocument(new ByteArrayInputStream(getXML().getBytes("UTF-8")));
				Iterator sequence = document.getDescendants(new ElementFilter("IESequenceTopComponent"));
				boolean hasSequenceTopComponent = false;
				while (sequence.hasNext()) {
					hasSequenceTopComponent = true;
					Element element = (Element) sequence.next();
					element.setName("IESequenceWidget");
				}
				if (hasSequenceTopComponent) {
					XMLUtils2.saveXMLFile(document, baos);
				}
				if (getTargetClass() == IESequenceTopComponent.class) {
					properties.put(PaletteAttribute.TARGET_CLASS_MODEL.getAttributeTag(), IESequenceWidget.class.getName());
					properties.put(PaletteAttribute.IS_TOP_COMPONENT.getAttributeTag(), String.valueOf(isTopComponent));
					properties.put(PaletteAttribute.XML.getAttributeTag(), new String(baos.toByteArray(), "UTF-8"));
					fetchXML();
					fetchTargetClass();
					fetchIsTopComponent();
					saveToFile();
				}
				return true;
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
				return false;
			}
		}
	}

	public boolean convertTopSequenceToWidgetSequence() {
		boolean result = true;
		Enumeration<FlexoIECustomWidgetPalette.FlexoIECustomWidget> en = getWidgets().elements();
		while (en.hasMoreElements()) {
			FlexoIECustomWidgetPalette.FlexoIECustomWidget widget = en.nextElement();
			result &= widget.convertTopSequenceToWidgetSequence();
		}
		return result;
	}

}
