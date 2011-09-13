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
 * Super class for all petal objects that have access qualifiers, i.e.,
 * "Public", "Protected", "Private", or "Implementation" (whatever that means, probably
 * it reads "language dependent")
 *
 * @version $Id: AccessObject.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class AccessObject extends QuidObject
  implements AccessQualified, Named, StereoTyped, Documented
{
  static final long serialVersionUID = -1442259881847075234L;

  protected AccessObject(PetalNode parent, String name, Collection params) {
    super(parent, name, params);
  }

  protected AccessObject(String name) {
    super(name);
  }

  @Override
public void setNameParameter(String o) {
    params.set(0, o);
  }

  @Override
public String getNameParameter() {
    return (String)params.get(0);
  }

  @Override
public String getDocumentation() {
    return getPropertyAsString("documentation");
  }

  @Override
public void setDocumentation(String o) {
    defineProperty("documentation", o);
  }

  @Override
public String getStereotype() {
    return getPropertyAsString("stereotype");
  }

  @Override
public void setStereotype(String c) {
    defineProperty("stereotype", c);
  }

  @Override
public String getExportControl() {
    return getPropertyAsString("exportControl");
  }

  @Override
public void setExportControl(String o) {
    defineProperty("exportControl", o);
  }

  public boolean isPublic() {
    return "Public".equals(getExportControl());
  }

  public boolean isProtected() {
      return "Protected".equals(getExportControl());
  }

  public boolean isPrivate() {
      return "Private".equals(getExportControl());
  }
}
