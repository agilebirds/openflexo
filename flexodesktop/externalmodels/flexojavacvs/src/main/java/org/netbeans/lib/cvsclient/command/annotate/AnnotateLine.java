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
package org.netbeans.lib.cvsclient.command.annotate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Thomas Singer
 */
public class AnnotateLine {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy", // NOI18N
			Locale.US);

	private String author;
	private String revision;
	private Date date;
	private String dateString;
	private String content;
	private int lineNum;

	public AnnotateLine() {
	}

	/**
	 * Returns the author of this line.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author of this line.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Returns the revision of this line.
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * Sets the revision of this line.
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * Returns the date of this line.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the date in original String-representation of this line.
	 */
	public String getDateString() {
		return dateString;
	}

	/**
	 * Sets the date of this line.
	 */
	public void setDateString(String dateString) {
		this.dateString = dateString;
		try {
			this.date = DATE_FORMAT.parse(dateString);
		} catch (ParseException ex) {
			// print stacktrace, because it's a bug
			ex.printStackTrace();
		}
	}

	/**
	 * Return the line's content.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the line's content.
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Returns the line's number. It's 1 based.
	 */
	public int getLineNum() {
		return lineNum;
	}

	/**
	 * Returns the line's number.
	 */
	public Integer getLineNumInteger() {
		return new Integer(lineNum);
	}

	/**
	 * Sets the line's number.
	 */
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
}