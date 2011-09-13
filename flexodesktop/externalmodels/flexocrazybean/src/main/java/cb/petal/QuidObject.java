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
 * Super class for all petal objects that have a "quid" property
 * defined, i.e. a globally unique identifier.
 *
 * @version $Id: QuidObject.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class QuidObject extends PetalObject {
  static final long serialVersionUID = -1442259881047075234L;

  protected QuidObject(PetalNode parent, String name, Collection params) {
    super(parent, name, params);
  }

  protected QuidObject(String name) {
    super(name);
  }

  static String getQuid(QuidObject obj) {
    return PetalObject.getPropertyAsString(obj, "quid");
  }

  /** Registers this object.
   */
  @Override
public void init() {
    getRoot().registerQuidObject(this);
  }

  public void setQuid(String quid) {
    defineProperty("quid", quid);
  }

  public String getQuid() {
    return getQuid(this);
  }

  public long getQuidAsLong() {
    return Long.parseLong(getQuid(), 16);
  }

  public void setQuidAsLong(long quid) {
    defineProperty("quid", Long.toHexString(quid).toUpperCase());
  }
  
  public boolean hasQuidu() {
	  return getProperty("quidu")!=null;
  }
  

}
