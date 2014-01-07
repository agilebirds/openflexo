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

import java.lang.reflect.Type;

import javax.swing.SwingConstants;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBLabel.FIBLabelImpl.class)
@XMLElement(xmlTag = "Label")
public interface FIBLabel extends FIBWidget {

	public static enum Align {
		left {
			@Override
			public int getAlign() {
				return SwingConstants.LEFT;
			}
		},
		right {
			@Override
			public int getAlign() {
				return SwingConstants.RIGHT;
			}
		},
		center {
			@Override
			public int getAlign() {
				return SwingConstants.CENTER;
			}
		};
		public abstract int getAlign();
	}

	@PropertyIdentifier(type = String.class)
	public static final String LABEL_KEY = "label";
	@PropertyIdentifier(type = Align.class)
	public static final String ALIGN_KEY = "align";

	@Getter(value = LABEL_KEY)
	@XMLAttribute
	public String getLabel();

	@Setter(LABEL_KEY)
	public void setLabel(String label);

	@Getter(value = ALIGN_KEY)
	@XMLAttribute
	public Align getAlign();

	@Setter(ALIGN_KEY)
	public void setAlign(Align align);

	public static abstract class FIBLabelImpl extends FIBWidgetImpl implements FIBLabel {

		private String label;
		private Align align = Align.left;

		public FIBLabelImpl() {
			super();
		}

		public FIBLabelImpl(String label) {
			this();
			this.label = label;
		}

		@Override
		public String getBaseName() {
			return "Label";
		}

		@Override
		public String getIdentifier() {
			return getLabel();
		}

		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			FIBPropertyNotification<String> notification = requireChange(LABEL_KEY, label);
			if (notification != null) {
				this.label = label;
				hasChanged(notification);
			}
		}

		@Override
		public Align getAlign() {
			return align;
		}

		@Override
		public void setAlign(Align align) {
			FIBPropertyNotification<Align> notification = requireChange(ALIGN_KEY, align);
			if (notification != null) {
				this.align = align;
				hasChanged(notification);
			}
		}

	}
}
