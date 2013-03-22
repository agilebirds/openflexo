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


public class FIBEditor extends FIBTextWidget {

	public static enum Parameters implements FIBModelAttribute {
		rows, tokenMarkerStyle
	}

	private Integer rows = null;
	private FIBTokenMarkerStyle tokenMarkerStyle = FIBTokenMarkerStyle.None;

	public FIBEditor() {
	}

	@Override
	protected String getBaseName() {
		return "Editor";
	}

	/**
	 * @return the rows
	 */
	public Integer getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(Integer rows) {
		FIBAttributeNotification<Integer> notification = requireChange(Parameters.rows, rows);
		if (notification != null) {
			this.rows = rows;
			hasChanged(notification);
		}
		this.rows = rows;
	}

	public FIBTokenMarkerStyle getTokenMarkerStyle() {
		return tokenMarkerStyle;
	}

	public void setTokenMarkerStyle(FIBTokenMarkerStyle tokenMarkerStyle) {
		System.out.println("setTokenMarkerStyle with " + tokenMarkerStyle);
		FIBAttributeNotification<FIBTokenMarkerStyle> notification = requireChange(Parameters.tokenMarkerStyle, tokenMarkerStyle);
		if (notification != null) {
			this.tokenMarkerStyle = tokenMarkerStyle;
			hasChanged(notification);
		}
	}

	public static enum FIBTokenMarkerStyle {
		None,
		BatchFile,
		C,
		CC,
		IDL,
		JavaScript,
		Java,
		Eiffel,
		HTML,
		Patch,
		Perl,
		PHP,
		Props,
		Python,
		ShellScript,
		SQL,
		TSQL,
		TeX,
		WOD,
		XML,
		FML /* Flexo Modelling Language */
	}
}
