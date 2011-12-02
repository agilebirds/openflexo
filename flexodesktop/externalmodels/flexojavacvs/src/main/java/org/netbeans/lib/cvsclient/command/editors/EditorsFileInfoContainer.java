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

package org.netbeans.lib.cvsclient.command.editors;

import java.io.File;
import java.util.Date;

import org.netbeans.lib.cvsclient.command.FileInfoContainer;

/**
 * Data object created by parsing the output of the Editors command.
 * 
 * @author Thomas Singer
 */
public class EditorsFileInfoContainer extends FileInfoContainer {

	private final String client;
	private final Date date;
	private final File file;
	private final String user;

	EditorsFileInfoContainer(File file, String user, Date date, String client) {
		this.file = file;
		this.user = user;
		this.date = date;
		this.client = client;
	}

	@Override
	public File getFile() {
		return file;
	}

	public String getClient() {
		return client;
	}

	public Date getDate() {
		return date;
	}

	public String getUser() {
		return user;
	}
}
