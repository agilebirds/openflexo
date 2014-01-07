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
package org.openflexo.fib.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTab.FIBTabImpl.class)
@XMLElement(xmlTag = "Tab")
public interface FIBTab extends FIBPanel {

	@PropertyIdentifier(type = String.class)
	public static final String TITLE_KEY = "title";
	public static final String PARAMETERS_KEY = "parameters";

	@Getter(value = TITLE_KEY)
	@XMLAttribute
	public String getTitle();

	@Setter(TITLE_KEY)
	public void setTitle(String title);

	public static abstract class FIBTabImpl extends FIBPanelImpl implements FIBTab {

		private String title;

		@Override
		public String getIdentifier() {
			return getTitle();
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public void setTitle(String title) {
			FIBPropertyNotification<String> notification = requireChange(TITLE_KEY, title);
			if (notification != null) {
				this.title = title;
				hasChanged(notification);
			}
		}

	}
}
