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
 * Refer to external documentation, i.e. a file or URL
 *
 * @version $Id: ExternalDoc.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ExternalDoc extends PetalObject {
  public ExternalDoc(PetalNode parent, Collection params) {
    super(parent, "external_doc", params);
  }

  public ExternalDoc() {
    super("external_doc");
  }

  public String getExternalDocUrl() {
    return getPropertyAsString("external_doc_url");
  }

  public void setExternalDocUrl(String o) {
    defineProperty("external_doc_url", o);
  }

  public String getExternalDocPath() {
    return getPropertyAsString("external_doc_path");
  }

  public void setExternalDocPath(String o) {
    defineProperty("external_doc_path", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
