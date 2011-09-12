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

import java.util.Collection;

/**
 * Super class for all quid objects that also have a quidu property
 * defined.
 *
 * @version $Id: QuiduObject.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class QuiduObject extends QuidObject implements HasQuidu {
  protected QuiduObject(PetalNode parent, String name, Collection params) {
    super(parent, name, params);
  }

  protected QuiduObject(String name) {
    super(name);
  }

  static String getQuidu(HasQuidu obj) {
    return PetalObject.getPropertyAsString((PetalObject)obj, "quidu");
  }

  @Override
public void setQuidu(String quid) {
    defineProperty("quidu", quid);
  }

  @Override
public String getQuidu() {
    return getQuidu(this);
  }

  @Override
public long getQuiduAsLong() {
    return Long.parseLong(getQuidu(), 16);
  }

  @Override
public void setQuiduAsLong(long quid) {
    defineProperty("quidu", Long.toHexString(quid).toUpperCase());
  }

  @Override
public final QuidObject getReferencedObject() {
    return getRoot().getReferencedObject(this);
  }
}
