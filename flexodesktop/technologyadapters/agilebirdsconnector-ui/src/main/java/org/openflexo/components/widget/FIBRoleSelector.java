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
package org.openflexo.components.widget;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a FlexoRole
 * 
 * @author sguerin
 * 
 */
public class FIBRoleSelector extends FIBFlexoObjectSelector<Role> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBRoleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/RoleSelector.fib");

	public FIBRoleSelector(Role editedObject) {
		super(editedObject);
	}

	@Override
	protected CustomFIBController makeCustomFIBController(FIBComponent fibComponent) {
		return new CustomFIBController(fibComponent, this);
	}

	/**
	 * Override when required
	 */
	@Override
	protected Collection<Role> getAllSelectableValues() {
		if (getProject() != null) {
			return getProject().getWorkflow().getRoleList().getRoles();
		}
		return null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	public Class<Role> getRepresentedType() {
		return Role.class;
	}

	@Override
	public String renderedString(Role editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public static class CustomFIBController extends FIBFlexoObjectSelector.SelectorFIBController {
		public CustomFIBController(FIBComponent component, FIBRoleSelector selector) {
			super(component, selector);
		}

		private final Map<Role, ImageIcon> icons = new HashMap<Role, ImageIcon>();

		public Icon getIconForRole(Role aRole) {
			if (aRole.getIsSystemRole()) {
				return decorateIcon(aRole, WKFIconLibrary.SYSTEM_ROLE_ICON);
			}
			ImageIcon customIcon = icons.get(aRole);
			if (customIcon == null) {
				customIcon = buildCustomIcon(aRole);
			}
			return decorateIcon(aRole, customIcon);
		}

		private ImageIcon buildCustomIcon(Role aRole) {
			Color aColor;
			if (aRole != null) {
				aColor = aRole.getColor();
			} else {
				aColor = Color.RED;
			}
			ImageIcon imageIcon = WKFIconLibrary.ROLE_ICON;
			Image image = imageIcon.getImage();
			Color mainColor = aColor;
			Color borderColor = new Color((aColor.getRed() + 255) / 2, (aColor.getGreen() + 255) / 2, (aColor.getBlue() + 255) / 2);
			ImageFilter imgfilter = new ColorSwapFilter(new Color(255, 51, 0), mainColor, new Color(255, 153, 102), borderColor);
			Image img = new JPanel().createImage(new FilteredImageSource(image.getSource(), imgfilter));
			ImageIcon returned = new ImageIcon(img);
			icons.put(aRole, returned);
			return returned;

		}

	}

	/*public static void main(String[] args)
	{
		testSelector(new FIBRoleSelector(null));
		
	}*/
}
