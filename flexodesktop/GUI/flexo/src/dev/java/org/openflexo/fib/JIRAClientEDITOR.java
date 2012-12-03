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
package org.openflexo.fib;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.br.view.JIRAIssueReportDialog;
import org.openflexo.fib.editor.FIBAbstractEditor;
import org.openflexo.module.UserType;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class JIRAClientEDITOR extends FIBAbstractEditor {

	@Override
	public Object[] getData() {
		JIRAIssueReportDialog o;
		try {
			o = new JIRAIssueReportDialog();
			return FIBAbstractEditor.makeArray(o);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Object[0];
	}

	@Override
	public File getFIBFile() {
		return JIRAIssueReportDialog.FIB_FILE;
	}

	public static void main(String[] args) {
		UserType.setCurrentUserType(UserType.MAINTAINER);
		main(JIRAClientEDITOR.class);
	}
}
