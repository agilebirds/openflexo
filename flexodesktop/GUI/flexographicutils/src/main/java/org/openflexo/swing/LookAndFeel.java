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
package org.openflexo.swing;

import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.openflexo.kvc.ChoiceList;

/**
 * @author gpolet
 * 
 */
public class LookAndFeel implements ChoiceList {

	private static final Vector<LookAndFeel> availableValues = new Vector<LookAndFeel>();

	public static LookAndFeel getDefaultLookAndFeel() {
		for (LookAndFeel feel : availableValues()) {
			if (feel.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
				return feel;
			}
		}
		return availableValues().firstElement();
	}

	public static Vector<LookAndFeel> availableValues() {
		if (availableValues.size() == 0) {
			LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
			for (int i = 0; i < lafs.length; i++) {
				LookAndFeelInfo feel = lafs[i];
				availableValues.add(new LookAndFeel(feel));
			}
		}
		return availableValues;
	}

	private final LookAndFeelInfo info;

	/**
     *
     */
	public LookAndFeel(LookAndFeelInfo info) {
		this.info = info;
	}

	public String getClassName() {
		return info.getClassName();
	}

	public String getName() {
		return info.getName();
	}

	/**
	 * Overrides getAvailableValues
	 * 
	 * @see org.openflexo.kvc.ChoiceList#getAvailableValues()
	 */
	@Override
	public Vector getAvailableValues() {
		return availableValues;
	}

}
