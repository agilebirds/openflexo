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
package org.openflexo.technologyadapter.owl.model;

/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Ian Dickinson, HP Labs Bristol
 * Author email       ian.dickinson@hp.com
 * Package            Jena 2
 * Web                http://sourceforge.net/projects/jena/
 * Created            25-Jul-2003
 * Filename           $RCSfile: DescribeDatatypeProperty.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 *
 * Last modified on   $Date: 2011/09/12 11:47:27 $
 *               by   $Author: gpolet $
 *
 * (c) Copyright 2002, 2003, 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * (see footer for full conditions)
 *****************************************************************************/

//	 Imports
//////////////	/
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntResource;

/**
 * <p>
 * Simple example of describing the basic attributes of a OWL, DAML or RDFS class using the ontology API. This is not meant as a definitive
 * solution to the problem, but as an illustration of one approach to solving the problem. This example should be adapted as necessary to
 * provide a given application with the means to render a class description in a readable form.
 * </p>
 * 
 * @author Ian Dickinson, HP Labs (<a href="mailto:Ian.Dickinson@hp.com" >email</a>)
 * @version CVS $Id: DescribeDatatypeProperty.java,v 1.2 2011/09/12 11:47:27 gpolet Exp $
 */
public class DescribeDatatypeProperty {
	// Constants
	// ////////////////////////////////

	// Static variables
	// ////////////////////////////////

	// Instance variables
	// ////////////////////////////////

	private Map m_anonIDs = new HashMap();
	private int m_anonCount = 0;

	// Constructors
	// ////////////////////////////////

	// External signature methods
	// ////////////////////////////////

	/**
	 * <p>
	 * Describe the given ontology property in texttual form.
	 * 
	 * @param out
	 *            The print stream to write the description to
	 * @param property
	 *            The ontology property to describe
	 */
	public void describeProperty(PrintStream out, DatatypeProperty property) {
		renderPropertyDescription(out, property);
		out.println();

		// sub-classes
		for (Iterator i = property.listSuperProperties(true); i.hasNext();) {
			out.print("  is a sub-class of ");
			renderPropertyDescription(out, (DatatypeProperty) i.next());
			out.println();
		}

		// super-classes
		for (Iterator i = property.listSubProperties(true); i.hasNext();) {
			out.print("  is a super-class of ");
			renderPropertyDescription(out, (DatatypeProperty) i.next());
			out.println();
		}
	}

	/**
	 * <p>
	 * Render a description of the given property to the given output stream.
	 * </p>
	 * 
	 * @param out
	 *            A print stream to write to
	 * @param property
	 *            The property to render
	 */
	public void renderPropertyDescription(PrintStream out, DatatypeProperty property) {
		out.println("Datatype property " + property.getLocalName());
		out.println("Domain: " + property.getDomain() + " of " + property.getDomain().getClass().getSimpleName());
		out.println("Range: " + property.getRange() + " of " + property.getRange().getClass().getSimpleName());
		OntResource domain = property.getDomain();
		out.println("domain.isClass()=" + domain.isClass());
	}

}
