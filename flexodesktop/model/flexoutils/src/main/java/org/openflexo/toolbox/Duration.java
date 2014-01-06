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
package org.openflexo.toolbox;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Duration implements Cloneable, Comparable<Duration> {

	private long value;
	private DurationUnit unit;

	public enum DurationUnit implements Comparable<DurationUnit> {
		MILLIS, SECONDS, MINUTES, HOURS, DAYS, WEEKS, MONTHS, YEARS;

		public String getSymbol() {
			if (this == MILLIS) {
				return "ms";
			} else if (this == SECONDS) {
				return "s";
			} else if (this == MINUTES) {
				return "min";
			} else if (this == HOURS) {
				return "h";
			} else if (this == DAYS) {
				return "d";
			} else if (this == WEEKS) {
				return "w";
			} else if (this == MONTHS) {
				return "m";
			} else if (this == YEARS) {
				return "y";
			}
			return "?";
		}

		public String getLocalizedKey() {
			if (this == MILLIS) {
				return "millisecond";
			} else if (this == SECONDS) {
				return "second";
			} else if (this == MINUTES) {
				return "minute";
			} else if (this == HOURS) {
				return "hour";
			} else if (this == DAYS) {
				return "day";
			} else if (this == WEEKS) {
				return "week";
			} else if (this == MONTHS) {
				return "month";
			} else if (this == YEARS) {
				return "year";
			}
			return "?";
		}

		public int getCalendarField() {
			if (this == MILLIS) {
				return Calendar.MILLISECOND;
			} else if (this == SECONDS) {
				return Calendar.SECOND;
			} else if (this == MINUTES) {
				return Calendar.MINUTE;
			} else if (this == HOURS) {
				return Calendar.HOUR;
			} else if (this == DAYS) {
				return Calendar.DAY_OF_YEAR;
			} else if (this == WEEKS) {
				return Calendar.WEEK_OF_YEAR;
			} else if (this == MONTHS) {
				return Calendar.MONTH;
			} else if (this == YEARS) {
				return Calendar.YEAR;
			}
			return -1;
		}

		// assert this.ordinality > oppositUnit.ordinality
		public long getCardinalityOf(DurationUnit oppositeUnit) {
			if (this == MILLIS) {
				if (oppositeUnit == MILLIS) {
					return 1;
				}
			} else if (this == SECONDS) {
				if (oppositeUnit == MILLIS) {
					return 1000;
				} else if (oppositeUnit == SECONDS) {
					return 1;
				}
			} else if (this == MINUTES) {
				if (oppositeUnit == MILLIS) {
					return 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 60;
				} else if (oppositeUnit == MINUTES) {
					return 1;
				}
			} else if (this == HOURS) {
				if (oppositeUnit == MILLIS) {
					return 60 * 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 60 * 60;
				} else if (oppositeUnit == MINUTES) {
					return 60;
				} else if (oppositeUnit == HOURS) {
					return 1;
				}
			} else if (this == DAYS) {
				if (oppositeUnit == MILLIS) {
					return 24 * 60 * 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 24 * 60 * 60;
				} else if (oppositeUnit == MINUTES) {
					return 24 * 60;
				} else if (oppositeUnit == HOURS) {
					return 24;
				} else if (oppositeUnit == DAYS) {
					return 1;
				}
			} else if (this == WEEKS) {
				if (oppositeUnit == MILLIS) {
					return 7 * 24 * 60 * 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 7 * 24 * 60 * 60;
				} else if (oppositeUnit == MINUTES) {
					return 7 * 24 * 60;
				} else if (oppositeUnit == HOURS) {
					return 7 * 24;
				} else if (oppositeUnit == DAYS) {
					return 7;
				} else if (oppositeUnit == WEEKS) {
					return 1;
				}
			} else if (this == MONTHS) {
				if (oppositeUnit == MILLIS) {
					return 30 * 24 * 60 * 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 30 * 24 * 60 * 60;
				} else if (oppositeUnit == MINUTES) {
					return 30 * 24 * 60;
				} else if (oppositeUnit == HOURS) {
					return 30 * 24;
				} else if (oppositeUnit == DAYS) {
					return 30;
				} else if (oppositeUnit == WEEKS) {
					return 4;
				} else if (oppositeUnit == MONTHS) {
					return 1;
				}
			} else if (this == YEARS) {
				if (oppositeUnit == MILLIS) {
					return 365 * 24 * 60 * 60 * 1000;
				} else if (oppositeUnit == SECONDS) {
					return 365 * 24 * 60 * 60;
				} else if (oppositeUnit == MINUTES) {
					return 365 * 24 * 60;
				} else if (oppositeUnit == HOURS) {
					return 365 * 24;
				} else if (oppositeUnit == DAYS) {
					return 365;
				} else if (oppositeUnit == WEEKS) {
					return 52;
				} else if (oppositeUnit == MONTHS) {
					return 12;
				} else if (oppositeUnit == YEARS) {
					return 1;
				}
			}
			return -1;
		}
	}

	public Duration(long aValue, DurationUnit aUnit) {
		super();
		this.value = aValue;
		this.unit = aUnit;
	}

	public Duration() {
		this(0, DurationUnit.SECONDS);
	}

	public boolean isValid() {
		return unit != null;
	}

	public String getStringRepresentation() {
		return getSerializationRepresentation();
	}

	public String getSerializationRepresentation() {
		if (unit == null) {
			return "";
		}
		return value + unit.getSymbol();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public DurationUnit getUnit() {
		return unit;
	}

	public void setUnit(DurationUnit unit) {
		this.unit = unit;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Duration && obj != null) {
			return getSerializationRepresentation().equals(((Duration) obj).getSerializationRepresentation());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getSerializationRepresentation().hashCode();
	}

	@Override
	public Duration clone() {
		return new Duration(value, unit);
	}

	public static Date datePlusDuration(Date aDate, Duration aDuration) {
		GregorianCalendar calendar = new GregorianCalendar(Locale.getDefault());
		calendar.setTime(aDate);
		calendar.add(aDuration.getUnit().getCalendarField(), (int) aDuration.getValue());
		return calendar.getTime();
	}

	public static Date dateMinusDuration(Date aDate, Duration aDuration) {
		GregorianCalendar calendar = new GregorianCalendar(Locale.getDefault());
		calendar.setTime(aDate);
		calendar.add(aDuration.getUnit().getCalendarField(), -(int) aDuration.getValue());
		return calendar.getTime();
	}

	public static Duration dateMinusDate(Date aDate, Date anOtherDate) {
		// TODO: not implemented yet
		System.err.println("org.openflexo.toolbox.Duration.dateMinusDate(Date,Date) NOT IMPLEMENED YET");
		return new Duration(1, DurationUnit.SECONDS);
	}

	public static Duration durationPlusDuration(Duration aDuration1, Duration aDuration2) {
		Duration minUnitDuration;
		Duration maxUnitDuration;
		if (aDuration1.getUnit().ordinal() <= aDuration2.getUnit().ordinal()) {
			minUnitDuration = aDuration1;
			maxUnitDuration = aDuration2;
		} else {
			minUnitDuration = aDuration2;
			maxUnitDuration = aDuration1;
		}
		return new Duration(minUnitDuration.getValue() + maxUnitDuration.getValue()
				* maxUnitDuration.getUnit().getCardinalityOf(minUnitDuration.getUnit()), minUnitDuration.getUnit());
	}

	public static Duration durationMinusDuration(Duration aDuration1, Duration aDuration2) {
		Duration minUnitDuration;
		Duration maxUnitDuration;
		if (aDuration1.getUnit().ordinal() <= aDuration2.getUnit().ordinal()) {
			minUnitDuration = aDuration1;
			maxUnitDuration = aDuration2;
		} else {
			minUnitDuration = aDuration2;
			maxUnitDuration = aDuration1;
		}
		return new Duration(minUnitDuration.getValue() - maxUnitDuration.getValue()
				* maxUnitDuration.getUnit().getCardinalityOf(minUnitDuration.getUnit()), minUnitDuration.getUnit());
	}

	public boolean lessThan(Duration aDuration) {
		return compareTo(aDuration) < 0;
	}

	public boolean lessOrEqualsThan(Duration aDuration) {
		return compareTo(aDuration) <= 0;
	}

	public boolean greaterThan(Duration aDuration) {
		return compareTo(aDuration) > 0;
	}

	public boolean greaterOrEqualsThan(Duration aDuration) {
		return compareTo(aDuration) >= 0;
	}

	@Override
	public int compareTo(Duration o) {
		int returned = getUnit().compareTo(o.getUnit());
		if (returned != 0) {
			return returned;
		}
		return new Long(getValue()).compareTo(new Long(o.getValue()));
	}
}
