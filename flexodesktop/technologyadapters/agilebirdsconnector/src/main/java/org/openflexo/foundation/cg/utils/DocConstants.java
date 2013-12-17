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
package org.openflexo.foundation.cg.utils;

import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public class DocConstants {
	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(DocConstants.class.getPackage().getName());

	public static final String PURPOSE_BUSINESS_CONTENT = "The purpose of this <I>Business Requirement Specification</I> is to model and document the functional behaviors of the underlying process."
			+ StringUtils.LINE_SEPARATOR
			+ StringUtils.LINE_SEPARATOR
			+ "These <B>Business Requirements</B> were collected during short user meetings and iterative <B>Prototype</B> reviews. "
			+ StringUtils.LINE_SEPARATOR
			+ "A Prototype is provided in parallel of this document to illustrate the user interface and the application logic (screen sequence). It is provided as an Internet website without databases. For a better understanding and review of this document, it is recommended to use the associated Prototype."
			+ StringUtils.LINE_SEPARATOR
			+ StringUtils.LINE_SEPARATOR
			+ "This document is usually associated with another more technical document that defines the <B>Application Blueprint</B> or <B>Technical Specifications</B> that should be validated and signed before starting any development."
			+ StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR;
	public static final String PURPOSE_TECHNICAL_CONTENT = "The purpose of this <I>Technical Requirement Specification</I> is to model and document the functional behaviors of the underlying process."
			+ StringUtils.LINE_SEPARATOR
			+ StringUtils.LINE_SEPARATOR
			+ "These <B>Technical Requirements</B> were collected during short user meetings and iterative <B>Prototype</B> reviews."
			+ StringUtils.LINE_SEPARATOR
			+ "A Prototype is provided in parallel of this document to illustrate the user interface and the application logic (screen sequence). It is provided as an Internet website without databases. For a better understanding and review of this document, it is recommended to use the associated Prototype."
			+ StringUtils.LINE_SEPARATOR
			+ StringUtils.LINE_SEPARATOR
			+ "This document is usually associated with another more Business oriented document, the <B>Business Requirement Specifications</B>."
			+ StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR;

	public static final String PURPOSE_DEFAULT_CONTENT = "The purpose of this Functional Specification is to define and document the functional behavior of .... This Functional Specification corresponds to specification collected during user meetings and Prototype reviews. "
			+ StringUtils.LINE_SEPARATOR
			+ "Processes defined for this application are described in details in respect of the following structure: Process &gt; Activity &gt; Operation &gt; Action."
			+ StringUtils.LINE_SEPARATOR
			+ "A Prototype is provided in parallel of this document to illustrate the user interface and the application logic. It is provided as an internet website without databases. For a better understanding and review of this document, it is recommended to use the associated Prototype."
			+ StringUtils.LINE_SEPARATOR
			+ "This document and the prototype define the Application Blueprint and should be validated and signed before any development start."
			+ StringUtils.LINE_SEPARATOR + StringUtils.LINE_SEPARATOR;

}
