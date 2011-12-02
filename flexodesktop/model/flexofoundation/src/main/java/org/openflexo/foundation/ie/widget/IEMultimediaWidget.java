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
package org.openflexo.foundation.ie.widget;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.bindings.StringStaticBinding;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.StringUtils;

public class IEMultimediaWidget extends AbstractInnerTableWidget {

	private static final Logger logger = FlexoLogger.getLogger(IEMultimediaWidget.class.getPackage().getName());

	public static enum MediaFormat {
		FLASH, QUICKTIME/*,MEDIA_PLAYER*/;
	}

	protected AbstractBinding bindingUrl;
	private int widthPixel = 200;
	private int heightPixel = 200;
	private MediaFormat format;

	private String embeddedVideoCode;

	public IEMultimediaWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEMultimediaWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	public int getWidthPixel() {
		return widthPixel;
	}

	public void setWidthPixel(int widthPixel) {
		this.widthPixel = widthPixel;
		if (!isCreatedByCloning() && !isDeserializing()) {
			setChanged();
			notifyObservers(new IEDataModification("widthPixel", null, widthPixel));
		}
	}

	public int getHeightPixel() {
		return heightPixel;
	}

	public void setHeightPixel(int heightPixel) {
		this.heightPixel = heightPixel;
		if (!isCreatedByCloning() && !isDeserializing()) {
			setChanged();
			notifyObservers(new IEDataModification("heightPixel", null, heightPixel));
		}
	}

	@Override
	public boolean areComponentInstancesValid() {
		return true;
	}

	@Override
	public String getDefaultInspectorName() {
		return "Multimedia.inspector";
	}

	@Override
	public void removeInvalidComponentInstances() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Verifying component instances for " + getClass().getName());
		}
	}

	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return new Vector<IObject>();
	}

	@Override
	public String getClassNameKey() {
		return "multimedia_widget";
	}

	@Override
	public String getFullyQualifiedName() {
		return "Multimedia";
	}

	public String getUrl() {
		if (getBindingUrl() != null && getBindingUrl() instanceof StringStaticBinding) {
			return ((StringStaticBinding) getBindingUrl()).getValue();
		}
		return null;
	}

	public void setUrl(String url) {
		if (url != null) {
			URL u = null;
			try {
				u = new URL(url);
			} catch (MalformedURLException e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("URL " + url + " is not correct. Returning.");
				}
				setChanged();
				notifyModification("url", null, getUrl(), true);
				return;
			}
			boolean urlHasBeenAdapted = false;
			if (url.indexOf("youtube.com") > -1 && url.indexOf("/watch/") > -1) {
				Hashtable<String, String> query = StringUtils.getQueryFromURL(u);
				if (query.get("v") != null) {
					url = url.substring(0, url.indexOf("/watch/")) + "/v/" + query.get("v");
					urlHasBeenAdapted = true;
					setFormat(MediaFormat.FLASH);
				}
			} else if (url.indexOf("dailymotion.com") > -1 && url.indexOf("/video/") > -1 && url.indexOf("_", url.indexOf("/video/")) > -1) {
				url = url.substring(0, url.indexOf("/video/")) + "/swf/"
						+ url.substring(url.indexOf("/video/") + 7, url.indexOf("_", url.indexOf("/video/")));
				urlHasBeenAdapted = true;
				setFormat(MediaFormat.FLASH);
			}
			if (!(getBindingUrl() instanceof StringStaticBinding)) {
				setBindingUrl(new StringStaticBinding(getBindingSourceUrlDefinition(), this, url));
			}
			((StringStaticBinding) getBindingUrl()).setValue(url);
			setChanged();
			notifyModification("url", null, url, urlHasBeenAdapted);
		} else {
			setBindingUrl(null);
		}

	}

	public AbstractBinding getBindingUrl() {
		if (isBeingCloned()) {
			return null;
		}
		return bindingUrl;
	}

	public void setBindingUrl(AbstractBinding value) {
		bindingUrl = value;
		if (bindingUrl != null) {
			bindingUrl.setOwner(this);
			bindingUrl.setBindingDefinition(getBindingSourceUrlDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingUrl", null, bindingUrl));
	}

	public WidgetBindingDefinition getBindingSourceUrlDefinition() {
		return WidgetBindingDefinition.get(this, "bindingUrl", String.class, BindingDefinitionType.GET, false);
	}

	public MediaFormat getFormat() {
		return format;
	}

	public void setFormat(MediaFormat format) {
		MediaFormat old = this.format;
		this.format = format;
		setChanged();
		notifyModification("format", old, format);
	}

	public String getEmbeddedVideoCode() {
		return embeddedVideoCode;
	}

	public void setEmbeddedVideoCode(String embeddedVideoCode) {
		String url = HTMLUtils.extractSourceFromEmbeddedTag(embeddedVideoCode);
		setUrl(url);
		this.embeddedVideoCode = embeddedVideoCode;
	}

}
