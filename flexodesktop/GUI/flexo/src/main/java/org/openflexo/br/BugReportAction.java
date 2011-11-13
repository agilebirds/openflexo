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
package org.openflexo.br;

import java.util.Date;

import org.openflexo.kvc.KVCObject;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * Represents a bug report action
 * 
 * @author sguerin
 */
public class BugReportAction extends KVCObject implements XMLSerializable {

	public Date date;

	public String username;

	public String description;

	public int status;

	private String _dateAsString;

	public BugReportAction() {
		super();
		date = new Date();
		username = System.getProperty("user.name");
	}

	public String statusAsString() {
		return BugReport.getAvailableStatus().get(status);
	}

	public String dateAsString() {
		if (_dateAsString == null) {
			StringEncoder.setDateFormat("HH:mm:ss dd/MM");
			_dateAsString = StringEncoder.getDateRepresentation(date);
		}
		return _dateAsString;
	}

}
