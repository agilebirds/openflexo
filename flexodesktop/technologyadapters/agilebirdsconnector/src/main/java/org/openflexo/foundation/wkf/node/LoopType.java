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
package org.openflexo.foundation.wkf.node;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;
import org.openflexo.xmlcode.XMLSerializable;

public enum LoopType implements XMLSerializable, StringConvertable {

	WHILE("WHILE", "while"), UNTIL("UNTIL", "until"), FOR_INCREMENT("FOR_INCREMENT", "for_increment"), FOR_COLLECTION("FOR_COLLECTION",
			"for_collection");

	private final String name;
	private final String localizedKey;

	public String getName() {
		return name;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(localizedKey);
	}

	LoopType(String _name, String _locKey) {
		this.name = _name;
		this.localizedKey = _locKey;
	}

	@Override
	public Converter<LoopType> getConverter() {
		return loopTypeConverter;
	}

	public static final StringEncoder.EnumerationConverter<LoopType> loopTypeConverter = new StringEncoder.EnumerationConverter<LoopType>(
			LoopType.class, "getName");

}
