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
package org.openflexo.antar;

import java.util.logging.Logger;


import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public abstract class ControlGraph implements AlgorithmicUnit {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(ControlGraph.class.getPackage().getName());

	private String headerComment = null;
	private String inlineComment = null;
	
	/**
	 * Normalize this ControlGraph, by builing a new ControlGraph which is normalized
	 * (We don't normalize our ControlGraph, we build an other one)
	 * 
	 * @return
	 */
	public abstract ControlGraph normalize();

	public String getHeaderComment() 
	{
		return headerComment;
	}

	public void setHeaderComment(String headerComment)
	{
		//if (this.comment != null) logger.info("replace "+this.comment+" by "+comment);
		this.headerComment = headerComment;
	}

	public void appendHeaderComment(String comment,boolean first)
	{
		if (this.headerComment == null || this.headerComment.trim().equals(""))
			setHeaderComment(comment);
		else {
			this.headerComment = (first?comment+StringUtils.LINE_SEPARATOR:"")+this.headerComment+(!first?StringUtils.LINE_SEPARATOR+comment:"");
		}
	}

	public String getInlineComment() 
	{
		return inlineComment;
	}

	public void setInlineComment(String inlineComment) 
	{
		this.inlineComment = inlineComment;
	}

	public boolean hasComment()
	{
		return getHeaderComment() != null || getInlineComment() != null;
	}
}
