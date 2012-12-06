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
package org.openflexo.components.browser.wkf;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.DuplicateRoleException;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.dm.RoleColorChange;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing a Role
 * 
 * @author sguerin
 * 
 */
public class RoleElement extends BrowserElement {

	public RoleElement(Role role, ProjectBrowser browser, BrowserElement parent) {
		super(role, BrowserElementType.ROLE, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {

	}

	@Override
	public String getName() {
		if (getRole() != null) {
			return getRole().getName();
		}
		return super.getName();
	}

	protected Role getRole() {
		return (Role) getObject();
	}

	@Override
	public boolean isNameEditable() {
		return !getRole().isImported();
	}

	@Override
	public void setName(String aName) throws DuplicateRoleException {
		if (getRole().isImported()) {
			return;
		}
		getRole().setName(aName);
	}

	@Override
	public String getToolTip() {
		if (getRole().isImported() && getRole().isDeletedOnServer()) {
			return FlexoLocalization.localizedForKey("object_is_no_more_available_in_portfolio");
		} else {
			return null;
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getRole() && dataModification instanceof RoleColorChange) {
			customIcon = buildCustomIcon(getRole().getColor());
		} else if (observable == getRole() && "isDeletedOnServer".equals(dataModification.propertyName())) {
			refreshWhenPossible();
		}
		super.update(observable, dataModification);
	}

	private ImageIcon customIcon = null;

	@Override
	public Icon getIcon() {
		if (getRole().getIsSystemRole()) {
			return decorateIcon(WKFIconLibrary.SYSTEM_ROLE_ICON);
		}
		if (customIcon == null) {
			customIcon = buildCustomIcon(getRole().getColor());
		}
		IconMarker[] markers = getIconMarkers();
		if (markers != null) {
			return IconFactory.getImageIcon(customIcon, markers);
		}
		return decorateIcon(customIcon);
	}

	private IconMarker[] getIconMarkers() {
		int count = 0;
		if (getRole().isImported()) {
			count++;
			if (getRole().isDeletedOnServer()) {
				count++;
			}
		}
		IconMarker[] markers = null;
		if (count > 0) {
			markers = new IconMarker[count];
		}
		if (getRole().isImported()) {
			markers[0] = IconLibrary.IMPORT;
			if (getRole().isDeletedOnServer()) {
				markers[1] = IconLibrary.WARNING;
			}
		}
		return markers;
	}

	public ImageIcon buildCustomIcon(Color aColor) {
		ImageIcon imageIcon = getElementType().getIcon();
		Image image = imageIcon.getImage();
		// See also org.openflexo.wkf.swleditor.gr.RoleContainerGR.updateColors() and
		// org.openflexo.wkf.roleeditor.RoleGR.getRoleColor() and
		// org.openflexo.wkf.processeditor.gr.AbstractActivityNodeGR.getMainBgColor()
		if (aColor == null) {
			aColor = Color.RED;
		}
		Color mainColor = aColor;
		Color borderColor = new Color((aColor.getRed() + 255) / 2, (aColor.getGreen() + 255) / 2, (aColor.getBlue() + 255) / 2);
		ImageFilter imgfilter = new ColorSwapFilter(new Color(255, 51, 0), mainColor, new Color(255, 153, 102), borderColor);
		Image img = new JPanel().createImage(new FilteredImageSource(image.getSource(), imgfilter));
		return new ImageIcon(img);

	}

	class ColorSwapFilter extends RGBImageFilter {
		private int target1;
		private int replacement1;
		private int target2;
		private int replacement2;

		public ColorSwapFilter(Color target1, Color replacement1, Color target2, Color replacement2) {
			this.target1 = target1.getRGB();
			this.replacement1 = replacement1.getRGB();
			this.target2 = target2.getRGB();
			this.replacement2 = replacement2.getRGB();
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			if (rgb == target1) {
				return replacement1;
			} else if (rgb == target2) {
				return replacement2;
			}
			return rgb;
		}
	}
}
