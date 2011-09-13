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
package org.openflexo.ant;

import java.util.Vector;

import org.openflexo.module.UserType;

public class HelpLanguage {
	private String isoCode;
	private String title;
	private Vector<HelpDistribution> distributions;

	public HelpLanguage() {
		super();
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String iso) {
		this.isoCode = iso;
	}

	public void addConfiguredDistribution(HelpDistribution dist) {
		if (distributions == null) {
			distributions = new Vector<HelpDistribution>();
		}
		if (!distributions.contains(dist)) {
			distributions.add(dist);
		}
	}

	public Vector<UserType> getDistributions() {
		Vector<UserType> v=new Vector<UserType>();
		if (distributions!=null) {
			for (HelpDistribution hd : distributions) {
				v.add(UserType.getUserTypeNamed(hd.getName()));
			}
		}
		return v;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}
}
