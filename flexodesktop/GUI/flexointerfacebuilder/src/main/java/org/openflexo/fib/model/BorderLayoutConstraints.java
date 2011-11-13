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

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fib.model.FIBPanel.Layout;

public class BorderLayoutConstraints extends ComponentConstraints {

	private static final Logger logger = Logger.getLogger(FIBComponent.class.getPackage().getName());

	private static final String LOCATION = "location";

	public BorderLayoutLocation getLocation() {
		return getEnumValue(LOCATION, BorderLayoutLocation.class, BorderLayoutLocation.center);
	}

	public void setLocation(BorderLayoutLocation location) {
		setEnumValue(LOCATION, location);
	}

	public static enum BorderLayoutLocation {
		north {
			@Override
			public String getConstraint() {
				return BorderLayout.NORTH;
			}
		},
		south {
			@Override
			public String getConstraint() {
				return BorderLayout.SOUTH;
			}
		},
		east {
			@Override
			public String getConstraint() {
				return BorderLayout.EAST;
			}
		},
		west {
			@Override
			public String getConstraint() {
				return BorderLayout.WEST;
			}
		},
		center {
			@Override
			public String getConstraint() {
				return BorderLayout.CENTER;
			}
		},
		beforeFirstLine {
			@Override
			public String getConstraint() {
				return BorderLayout.BEFORE_FIRST_LINE;
			}
		},
		afterLastLine {
			@Override
			public String getConstraint() {
				return BorderLayout.AFTER_LAST_LINE;
			}
		},
		beforeLineBegins {
			@Override
			public String getConstraint() {
				return BorderLayout.BEFORE_LINE_BEGINS;
			}
		},
		afterLineEnds {
			@Override
			public String getConstraint() {
				return BorderLayout.AFTER_LINE_ENDS;
			}
		},
		pageStart {
			@Override
			public String getConstraint() {
				return BorderLayout.PAGE_START;
			}
		},
		pageEnd {
			@Override
			public String getConstraint() {
				return BorderLayout.PAGE_END;
			}
		},
		lineStart {
			@Override
			public String getConstraint() {
				return BorderLayout.LINE_START;
			}
		},
		lineEnd {
			@Override
			public String getConstraint() {
				return BorderLayout.LINE_END;
			}
		};

		public abstract String getConstraint();
	}

	public BorderLayoutConstraints() {
		super();
	}

	public BorderLayoutConstraints(BorderLayoutLocation location) {
		super();
		setLocation(location);
	}

	protected BorderLayoutConstraints(String someConstraints) {
		super(someConstraints);
	}

	BorderLayoutConstraints(ComponentConstraints someConstraints) {
		super(someConstraints);
	}

	@Override
	protected Layout getType() {
		return Layout.border;
	}

	@Override
	public void performConstrainedAddition(JComponent container, JComponent contained) {
		container.add(contained, getLocation().getConstraint());
	}

}
