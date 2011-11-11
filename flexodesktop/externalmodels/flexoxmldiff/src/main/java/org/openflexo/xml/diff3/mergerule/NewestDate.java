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
package org.openflexo.xml.diff3.mergerule;

import java.util.Date;

import org.openflexo.xml.diff3.MergeActionType;
import org.openflexo.xml.diff3.MergeAttributeAction;
import org.openflexo.xml.diff3.UnresolvedAttributesConflict;

public class NewestDate extends MergeAttributeRule {

	public NewestDate(UnresolvedAttributesConflict conflict) {
		super(conflict);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canBeApplyed() {
		return "lastUpdate".equals(_conflict.attributeName())
				&& (_conflict.getRootElementName().equals("DMModel") || _conflict.getRootElementName().equals("FlexoProcess"));
	}

	@Override
	public MergeAttributeAction getAction() {
		if (_action == null) {
			Date date1 = MergeUtils.dateFromString(_conflict.value1());
			Date date2 = MergeUtils.dateFromString(_conflict.value2());
			String reply = null;
			if (date1 == null)
				reply = MergeUtils.dateToString(date1);
			else if (date2 == null)
				reply = MergeUtils.dateToString(date2);
			else if (date1.after(date2))
				reply = MergeUtils.dateToString(date1);
			else
				reply = MergeUtils.dateToString(date2);

			_action = new MergeAttributeAction(_conflict.getConflictIndex(), MergeActionType.UPDATE, _conflict.attributeName(), reply,
					_conflict.getMergedElement());
		}
		return _action;
	}

}
