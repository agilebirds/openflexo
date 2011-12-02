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
package org.netbeans.lib.cvsclient.command.add;

import org.netbeans.lib.cvsclient.command.DefaultFileInfoContainer;

/**
 * Describes add information for a file. This is the result of doing a cvs add command. The fields in instances of this object are populated
 * by response handlers.
 * 
 * @author Thomas Singer
 */
public class AddInformation extends DefaultFileInfoContainer {
	public static final String FILE_ADDED = "A"; // NOI18N
	public static final String FILE_RESURRECTED = "U"; // NOI18N

	public AddInformation() {
	}
}
