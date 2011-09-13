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
 * Represents operation object, i.e. method.
 *
 * @version $Id: Operation.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Operation extends AccessObject {
  static final long serialVersionUID=8462139492522368436L;

  public Operation(PetalNode parent, java.util.Collection params) {
    super(parent, "Operation", params);
  }

  public Operation() {
    super("Operation");
  }

  public List getParameters() {
    return (List)getProperty("parameters");
  }

  public void setParameters(List o) {
    defineProperty("parameters", o);
  }

  public String getConcurrency() {
    return getPropertyAsString("concurrency");
  }

  public void setConcurrency(String c) {
    defineProperty("concurrency", c);
  }

  public String getResult() {
    return getPropertyAsString("result");
  }

  public void setResult(String c) {
    defineProperty("result", c);
  }

  public void setPostCondition(SemanticInfo c) {
    defineProperty("post_condition", c);
  }

  public SemanticInfo getPostCondition() {
    return (SemanticInfo)getProperty("post_condition");
  }

  public void setSemantics(SemanticInfo c) {
    defineProperty("semantics", c);
  }

  public SemanticInfo getSemantics() {
    return (SemanticInfo)getProperty("semantics");
  }

  public int getUid() {
    return getPropertyAsInteger("uid");
  }

  public void setUid(int uid) {
    defineProperty("uid", uid);
  }

  // Overridden, has different name for some reason
  @Override
public String getExportControl() {
    return getPropertyAsString("opExportControl");
  }

  @Override
public void setExportControl(String o) {
    defineProperty("opExportControl", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
