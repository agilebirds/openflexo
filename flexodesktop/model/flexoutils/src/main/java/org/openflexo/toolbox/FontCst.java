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

import java.awt.Font;

/**
 * @author gpolet
 * 
 */
public class FontCst {
	public static final Font CODE_FONT = ToolBox.getPLATFORM() == ToolBox.WINDOWS ? new Font("Serif", Font.PLAIN, 12) : new Font("Monaco",
			Font.PLAIN, 11);

	public static final Font JAVA_CODE_FONT = ToolBox.getPLATFORM() == ToolBox.WINDOWS ? new Font("Serif", Font.PLAIN, 12) : new Font(
			"Monaco", Font.PLAIN, 11);

	public static final Font TEXT_FONT = ToolBox.getPLATFORM() == ToolBox.WINDOWS ? new Font("SansSerif", Font.PLAIN, 12) : new Font(
			"Monaco", Font.PLAIN, 11);
}
