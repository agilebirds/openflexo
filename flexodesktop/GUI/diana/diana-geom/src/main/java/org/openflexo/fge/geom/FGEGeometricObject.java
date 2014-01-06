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
package org.openflexo.fge.geom;

import java.util.List;
import java.util.Vector;

import org.openflexo.fge.geom.area.FGEArea;

public interface FGEGeometricObject<O extends FGEGeometricObject<O>> extends Cloneable, FGEArea {

	public static final double EPSILON = 1e-10;

	public static enum Filling {
		NOT_FILLED, FILLED
	}

	@Override
	public FGEGeometricObject<? extends O> clone();

	public List<FGEPoint> getControlPoints();

	public static enum SimplifiedCardinalDirection {
		NORTH, EAST, SOUTH, WEST;

		public boolean isHorizontal() {
			return this == EAST || this == WEST;
		}

		public boolean isVertical() {
			return this == NORTH || this == SOUTH;
		}

		public double getRadians() {
			switch (this) {
			case NORTH:
				return Math.PI / 2;
			case SOUTH:
				return -Math.PI / 2;
			case EAST:
				return 0;
			case WEST:
				return Math.PI;
			default:
				return 0;
			}
		}

		public FGEPoint getNormalizedRepresentativePoint() {
			switch (this) {
			case NORTH:
				return new FGEPoint(0.5, 0.0);
			case SOUTH:
				return new FGEPoint(0.5, 1.0);
			case EAST:
				return new FGEPoint(1.0, 0.5);
			case WEST:
				return new FGEPoint(0.0, 0.5);
			default:
				return new FGEPoint();
			}
		}

		public SimplifiedCardinalDirection getOpposite() {
			if (this == EAST) {
				return WEST;
			}
			if (this == WEST) {
				return EAST;
			}
			if (this == SOUTH) {
				return NORTH;
			}
			if (this == NORTH) {
				return SOUTH;
			}
			return null;
		}

		public CardinalDirection getCardinalDirectionEquivalent() {
			switch (this) {
			case EAST:
				return CardinalDirection.EAST;
			case NORTH:
				return CardinalDirection.NORTH;
			case SOUTH:
				return CardinalDirection.SOUTH;
			case WEST:
				return CardinalDirection.WEST;
			}
			return null;
		}

		public static Vector<SimplifiedCardinalDirection> allDirections() {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : values()) {
				returned.add(o);
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> uniqueDirection(SimplifiedCardinalDirection aDirection) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			returned.add(aDirection);
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> someDirections(SimplifiedCardinalDirection... someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : someDirections) {
				returned.add(o);
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(SimplifiedCardinalDirection aDirection) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : values()) {
				if (o != aDirection) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(SimplifiedCardinalDirection... someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : values()) {
				boolean isToBeExcepted = false;
				for (int i = 0; i < someDirections.length; i++) {
					if (o == someDirections[i]) {
						isToBeExcepted = true;
					}
				}
				if (!isToBeExcepted) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> allDirectionsExcept(Vector<SimplifiedCardinalDirection> someDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : values()) {
				boolean isToBeExcepted = false;
				for (SimplifiedCardinalDirection o2 : someDirections) {
					if (o == o2) {
						isToBeExcepted = true;
					}
				}
				if (!isToBeExcepted) {
					returned.add(o);
				}
			}
			return returned;
		}

		public static Vector<SimplifiedCardinalDirection> intersection(Vector<SimplifiedCardinalDirection> someDirections,
				Vector<SimplifiedCardinalDirection> someOtherDirections) {
			Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : someDirections) {
				if (someOtherDirections.contains(o)) {
					returned.add(o);
				}
			}
			return returned;
		}

	}

	public static enum CardinalQuadrant {
		NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST;

		public SimplifiedCardinalDirection getVerticalComponent() {
			if (this == NORTH_EAST || this == NORTH_WEST) {
				return SimplifiedCardinalDirection.NORTH;
			}
			if (this == SOUTH_EAST || this == SOUTH_WEST) {
				return SimplifiedCardinalDirection.SOUTH;
			}
			return null;
		}

		public SimplifiedCardinalDirection getHorizonalComponent() {
			if (this == NORTH_WEST || this == SOUTH_WEST) {
				return SimplifiedCardinalDirection.WEST;
			}
			if (this == NORTH_EAST || this == SOUTH_EAST) {
				return SimplifiedCardinalDirection.EAST;
			}
			return null;
		}
	}

	public static enum CardinalDirection {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;
	}

	public abstract String getStringRepresentation();

}
