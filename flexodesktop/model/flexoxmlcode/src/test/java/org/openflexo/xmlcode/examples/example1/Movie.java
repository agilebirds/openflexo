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

package org.openflexo.xmlcode.examples.example1;

import java.util.Date;
import java.util.Vector;

import org.openflexo.xmlcode.XMLSerializable;


/**
 * Class <code>Movie</code> is intented to represent a movie object in XML
 * coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Movie implements XMLSerializable
{

    public String title;

    public String description;

    public Date dateReleased;

    protected Vector roles;

    public String category;

    @Override
	public String toString()
    {

        String returnedString = "Movie (title=" + title + ", description=" + description + ", date=";
        if (dateReleased != null) {
            returnedString += dateReleased.toString();
        } else {
            returnedString += "null";
        } // end of else

        returnedString += ", category=" + category + ") roles=(";
        if (roles != null) {
            for (int i = 0; i < roles.size(); i++) {
                returnedString += " " + roles.elementAt(i).toString();
            }
        }

        returnedString += ")";
        return returnedString;
    }

    public Vector getRoles()
    {

        return roles;
    }

    public void setRoles(Vector values)
    {

        roles = values;
    }

    public void addToRoles(Role aRole)
    {

        roles.addElement(aRole);
    }

    public void removeFromRoles(Role aRole)
    {

        roles.removeElement(aRole);
    }

}
