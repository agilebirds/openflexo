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
package org.openflexo.javaparser;

import java.util.StringTokenizer;
import java.util.Vector;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.foundation.dm.javaparser.ParsedJavadoc;
import org.openflexo.foundation.dm.javaparser.ParsedJavadocItem;
import org.openflexo.toolbox.StringUtils;

import com.thoughtworks.qdox.model.DocletTag;

public class JavadocItem extends KVCFlexoObject implements ParsedJavadoc {

	private String comment;
	private Vector<FJPDocletTag> _docletTags;

	protected JavadocItem(String comment, DocletTag[] tags) {
		this.comment = comment;
		_docletTags = new Vector<FJPDocletTag>();
		for (DocletTag dt : tags) {
			_docletTags.add(new FJPDocletTag(dt));
		}
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public Vector<? extends ParsedJavadocItem> getDocletTags() {
		return _docletTags;
	}

	@Override
	public Vector<? extends ParsedJavadocItem> getTagsByName(String tagName) {
		Vector<ParsedJavadocItem> returned = new Vector<ParsedJavadocItem>();
		for (int i = 0; i < _docletTags.size(); i++) {
			ParsedJavadocItem docletTag = _docletTags.elementAt(i);
			if (docletTag.getTag().equals(tagName)) {
				returned.add(docletTag);
			}
		}
		return returned;
	}

	@Override
	public ParsedJavadocItem getTagByName(String tagName) {
		for (int i = 0; i < _docletTags.size(); i++) {
			ParsedJavadocItem docletTag = _docletTags.elementAt(i);
			if (docletTag.getTag().equals(tagName)) {
				return docletTag;
			}
		}
		return null;
	}

	/**
	 * Convenience method for <code>getTagByName(String).getNamedParameter(String)</code> that also checks for null tag.
	 */
	@Override
	public ParsedJavadocItem getTagByName(String tagName, String parameterName) {
		if (parameterName.equals("User Manual")) {
			parameterName = "UserManual";
		}
		Vector<? extends ParsedJavadocItem> tags = getTagsByName(tagName);
		for (ParsedJavadocItem tag : tags) {
			if (tag.getParameterName() != null && tag.getParameterName().equals(parameterName)) {
				return tag;
			}
		}
		return null;
	}

	// When insert set to true, insert at first position (related to tag name), otherwise put at the end
	@Override
	public ParsedJavadocItem addTagForNameAndValue(String tagName, String parameterName, String parameterValue, boolean insert) {
		if (parameterName == null) {
			parameterName = "";
		}
		if (parameterName.equals("User Manual")) {
			parameterName = "UserManual";
		}
		int index = _docletTags.size();
		for (int i = 0; i < _docletTags.size(); i++) {
			ParsedJavadocItem docletTag = _docletTags.elementAt(i);
			if (docletTag.getTag() != null && docletTag.getTag().equals(tagName) && i < index) {
				index = i;
			}
			if (docletTag.getParameterName() != null && docletTag.getParameterName().equals(parameterName)) {
				docletTag.setParameterValue(parameterValue);
				return docletTag;
			}
		}
		FJPDocletTag returned;
		if (index < _docletTags.size() && insert) {
			_docletTags.insertElementAt(returned = new FJPDocletTag(tagName, parameterName, parameterValue), index);
		} else {
			_docletTags.add(returned = new FJPDocletTag(tagName, parameterName, parameterValue));
		}
		return returned;
	}

	@Override
	public String getStringRepresentation() {
		StringBuffer returned = new StringBuffer();
		returned.append("/**" + StringUtils.LINE_SEPARATOR);

		StringTokenizer st = new StringTokenizer(getComment() == null ? "" : getComment(), StringUtils.LINE_SEPARATOR);
		while (st.hasMoreTokens()) {
			returned.append("  * " + st.nextToken() + StringUtils.LINE_SEPARATOR);
		}
		returned.append("  *" + StringUtils.LINE_SEPARATOR);

		for (FJPDocletTag tag : _docletTags) {
			int indentLength = (" @" + tag.getTag() + " " + tag.getParameterName()/*+" "*/).length();
			String indent = null;
			String tagValue = !tag.getParameterName().equals(tag.getParameterValue()) ? tag.getParameterValue() : "";
			StringTokenizer st2 = new StringTokenizer(tagValue, StringUtils.LINE_SEPARATOR);
			boolean isFirst = true;
			while (st2.hasMoreTokens()) {
				String next = st2.nextToken();
				if (isFirst) {
					returned.append("  * @" + tag.getTag() + " " + tag.getParameterName() + " " + next + StringUtils.LINE_SEPARATOR);
				} else {
					if (indent == null) {
						indent = StringUtils.buildWhiteSpaceIndentation(indentLength);
					}
					returned.append("  *" + indent + " " + next + StringUtils.LINE_SEPARATOR);
				}
				isFirst = false;
			}
		}
		returned.append("  */" + StringUtils.LINE_SEPARATOR);

		return returned.toString();
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}

	public class FJPDocletTag extends FJPJavaElement implements ParsedJavadocItem {

		private String _tagName;
		private String _parameterName;
		private String _parameterValue;

		FJPDocletTag(DocletTag docletTag) {
			super(null);
			_tagName = docletTag.getName();
			if (docletTag.getValue().indexOf(" ") > -1) {
				_parameterName = docletTag.getValue().substring(0, docletTag.getValue().indexOf(" ")).trim();
				_parameterValue = docletTag.getValue().substring(docletTag.getValue().indexOf(" ")).trim();
			} else {
				_parameterName = docletTag.getValue();
				_parameterValue = docletTag.getValue();
			}
		}

		FJPDocletTag(String tagName, String parameterName, String parameterValue) {
			super(null);
			_tagName = tagName;
			_parameterName = parameterName;
			_parameterValue = parameterValue;
		}

		@Override
		public String getInspectorName() {
			// not inspectable alone
			return null;
		}

		@Override
		public String getTag() {
			return _tagName;
		}

		@Override
		public String getParameterName() {
			return _parameterName;
		}

		@Override
		public String getParameterValue() {
			return _parameterValue;
		}

		@Override
		public void setParameterValue(String value) {
			_parameterValue = value;
		}

	}

}
