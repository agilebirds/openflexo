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

package org.openflexo.xmlcode.examples.example2;

/**
 * Class <code>ReggaeAlbum</code> is intented to represent a reggae album (to
 * show an inheritance example).
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class ReggaeAlbum extends Album
{

    @Override
	public String toString()
    {

        String returnedString = "ReggaeAlbum (id=" + articleId + ", price=" + price + ", title=" + title + ", author=" + author + ", description="
                + description + ", date=";
        if (dateReleased != null) {
            returnedString += dateReleased.toString();
        } else {
            returnedString += "null";
        }

        returnedString += ")";
        return returnedString;
    }

}
