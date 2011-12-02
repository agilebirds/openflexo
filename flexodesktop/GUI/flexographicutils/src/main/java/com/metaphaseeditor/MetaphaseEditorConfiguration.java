/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor;

import java.util.Vector;

public class MetaphaseEditorConfiguration {
	public Vector<MetaphaseEditorOption> options;

	public MetaphaseEditorConfiguration() {
		options = new Vector<MetaphaseEditorOption>();
	}

	public void addToOptions(MetaphaseEditorOption o) {
		options.add(o);
	}

	public MetaphaseEditorOption getOption(String optionName) {
		for (MetaphaseEditorOption o : options) {
			if (o.optionName.equals(optionName)) {
				return o;
			}
		}
		return null;
	}

	public boolean hasOption(String optionName) {
		return getOption(optionName) != null;
	}

	public boolean hasOption(String optionName, int line) {
		MetaphaseEditorOption o = getOption(optionName);
		return o != null && o.line == line;
	}

	@Override
	public String toString() {
		return "MetaphaseEditorConfiguration[" + options + "]";
	}

	public static class MetaphaseEditorOption {
		public String optionName;
		public int index;
		public int line;

		public MetaphaseEditorOption(String optionName, int index, int line) {
			super();
			this.optionName = optionName;
			this.index = index;
			this.line = line;
		}

		@Override
		public String toString() {
			return "Option[" + optionName + ",index=" + index + ",line=" + line + "]";
		}

	}
}