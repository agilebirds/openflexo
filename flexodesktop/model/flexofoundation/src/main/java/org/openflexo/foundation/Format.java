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
package org.openflexo.foundation;

import java.util.Vector;

import org.openflexo.foundation.rm.FlexoProject;



public enum Format {
	DOCX {
		@Override
		public String getPostBuildFileExtension() {
			return ".docx";
		}

		@Override
		public String getPostBuildKey() {
			return "DOCX";
		}

	},
	HTML {
		@Override
		public String getPostBuildFileExtension() {
			return ".zip";
		}

		@Override
		public String getPostBuildKey() {
			return "ZIP";
		}

	},
	LATEX {
		@Override
		public String getPostBuildFileExtension() {
			return ".pdf";
		}

		@Override
		public String getPostBuildKey() {
			return "PDF";
		}

	},
	WEBOBJECTS {

		@Override
		public String getPostBuildFileExtension() {
			return ".war";
		}

		@Override
		public String getPostBuildKey() {
			return "WAR";
		}
	},
	BPEL {

		@Override
		public String getPostBuildFileExtension() {
			return ".bpel";
		}

		@Override
		public String getPostBuildKey() {
			return "BPEL";
		}
	};

	private Format() {
	}
	public Vector<TargetType> getAvailableTargets(FlexoProject project) {
		Vector<TargetType> v = new Vector<TargetType>();
		for (DocType type: project.getDocTypes()){
			if (type.getAvailableFormats().indexOf(this)>-1) {
				v.add(type);
			}
		}
		for (CodeType type: CodeType.availableValues()){
			if (type.getAvailableFormats().indexOf(this)>-1) {
				v.add(type);
			}
		}
		return v;
	}
	public abstract String getPostBuildFileExtension();
	public abstract String getPostBuildKey();
}