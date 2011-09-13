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

package org.openflexo.xmlcode;

/**
 * <p>
 * Classes implementing interface <code>XMLSerializable</code> could be
 * instancied and coded from/to XML strings and streams.
 * </p>
 * No special behaviour has to be implemented in your
 * <code>XMLSerializable</code> objects, since it is done via an externaly
 * defined mapping (see {@link  org.openflexo.xmlcode.XMLMapping}). This interface
 * just help the programmer to remember that this class xml serialization MUST
 * be described somewhere.<br>
 * <b>Note that it means that you can encode/decode into/from XML all types of
 * classes</b>, since you just have to declare those classes as implementing
 * this interface..
 * 
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 */
public interface XMLSerializable
{

} /* end interface XMLSerializable */
