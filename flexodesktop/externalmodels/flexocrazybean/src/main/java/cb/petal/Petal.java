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
package cb.petal;

/**
 * Represents top level petal object.
 * 
 * @version $Id: Petal.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Petal extends PetalObject {
	public Petal(java.util.Collection params) {
		super(null, "Petal", params);
	}

	public Petal() {
		super("Petal");
	}

	public int getCharSet() {
		return getPropertyAsInteger("charset");
	}

	public void setCharSet(int o) {
		defineProperty("charset", o);
	}

	public String getWritten() {
		return getPropertyAsString("_written");
	}

	public void setWritten(String o) {
		defineProperty("_written", o);
	}

	public int getVersion() {
		return getPropertyAsInteger("version");
	}

	public void setVersion(int o) {
		defineProperty("version", o);
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
