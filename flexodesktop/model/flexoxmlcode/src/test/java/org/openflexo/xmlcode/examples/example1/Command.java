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

import org.openflexo.xmlcode.XMLSerializable;

/**
 * Class <code>Command</code> is intented to represent a command object in XML
 * coding/decoding example.
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public class Command implements XMLSerializable
{

    private int qty;

    public boolean commandIsAlreadyPaid;

    private Movie movie;

    public Customer customer;

    @Override
	public String toString()
    {
        String returnedString = "Command (qty=" + qty + ", paid=" + commandIsAlreadyPaid;
        if (movie != null) {
            returnedString += "," + movie.toString();
        }
        if (customer != null) {
            returnedString += "," + customer.toString();
        }
        returnedString += ")\n";
        return returnedString;
    }

    public int getQty()
    {
        return qty;
    }

    public void setQty(int aQty)
    {

        qty = aQty;
    }

    public Movie getMovie()
    {

        return movie;
    }

    public void setMovie(Movie aMovie)
    {

        movie = aMovie;
    }

}
