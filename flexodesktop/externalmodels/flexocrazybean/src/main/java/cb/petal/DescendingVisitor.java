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
import java.util.Iterator;

/**
 * By default visits petal tree with DFS.
 *
 * @version $Id: DescendingVisitor.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class DescendingVisitor implements Visitor {
  public DescendingVisitor() {}

  @Override
public void visit(PetalFile obj) {
    obj.getPetal().accept(this);
    obj.getDesign().accept(this);
  }

  @Override
public void visit(List list) {
    for(Iterator i = list.getElements().iterator(); i.hasNext(); )
      ((PetalNode)i.next()).accept(this);
  }

  @Override
public void visit(Value value) {
    value.getValue().accept(this);
  }

  public void visitObject(PetalObject obj) {
    for(Iterator i = obj.getPropertyList().iterator(); i.hasNext(); )
      ((PetalNode)i.next()).accept(this);
  }

  @Override
public void visit(Class obj) { visitObject(obj); }
  @Override
public void visit(SemanticInfo obj) { visitObject(obj); }

  @Override
public void visit(Font obj) { visitObject(obj); }

  @Override
public void visit(Design obj) { visitObject(obj); }

  @Override
public void visit(Petal obj) { visitObject(obj); }

  @Override
public void visit(ClassAttribute obj) { visitObject(obj); }

  @Override
public void visit(LogicalCategory obj) { visitObject(obj); }
  @Override
public void visit(ClassCategory obj) { visitObject(obj); }
  @Override
public void visit(UseCaseCategory obj) { visitObject(obj); }

  @Override
public void visit(Operation obj) { visitObject(obj); }

  @Override
public void visit(Defaults obj) { visitObject(obj); }

  @Override
public void visit(Attribute obj) { visitObject(obj); }

  @Override
public void visit(Processes obj) {  visitObject(obj); }

  @Override
public void visit(Properties obj) { visitObject(obj); }

  @Override
public void visit(SubSystem obj) { visitObject(obj); }

  @Override
public void visit(UseCase obj) { visitObject(obj); }

  @Override
public void visit(UseCaseDiagram obj) { visitObject(obj); }

  @Override
public void visit(Compartment obj) { visitObject(obj); }

  @Override
public void visit(ItemLabel obj) { visitObject(obj); }

  @Override
public void visit(Label obj) { visitObject(obj); }

  @Override
public void visit(SimpleViewObject obj) { visitObject(obj); }

  @Override
public void visit(ClassView obj) { visitObject(obj); }

  @Override
public void visit(CategoryView obj) { visitObject(obj); }

  @Override
public void visit(Parameter obj) { visitObject(obj); }

  @Override
public void visit(Association obj) { visitObject(obj); }

  @Override
public void visit(Role obj) { visitObject(obj); }

  @Override
public void visit(ModView obj) { visitObject(obj); }

  @Override
public void visit(UsesRelationship obj) { visitObject(obj); }

  @Override
public void visit(ClassDiagram obj) { visitObject(obj); }

  @Override
public void visit(DependencyRelationship obj) { visitObject(obj); }

  @Override
public void visit(ImportView obj) { visitObject(obj); }

  @Override
public void visit(InheritView obj) { visitObject(obj); }

  @Override
public void visit(InheritanceRelationship obj) { visitObject(obj); }

  @Override
public void visit(InstantiateView obj) { visitObject(obj); }

  @Override
public void visit(InstantiationRelationship obj) { visitObject(obj); }

  @Override
public void visit(ModVisView obj) { visitObject(obj); }

  @Override
public void visit(ModuleDiagram obj) { visitObject(obj); }

  @Override
public void visit(ProcessDiagram obj) { visitObject(obj); }

  @Override
public void visit(SubSysView obj) { visitObject(obj); }

  @Override
public void visit(VisibilityRelationship obj) { visitObject(obj); }

  @Override
public void visit(Module obj) { visitObject(obj); }

  @Override
public void visit(RoleView obj) { visitObject(obj); }

  @Override
public void visit(SegLabel obj) { visitObject(obj); }

  @Override
public void visit(AssociationViewNew obj) { visitObject(obj); }

  @Override
public void visit(NoteView obj) { visitObject(obj); }

  @Override
public void visit(InheritTreeView obj) { visitObject(obj); }

  @Override
public void visit(AttachView obj) { visitObject(obj); }

  @Override
public void visit(AssocAttachView obj) { visitObject(obj); }

  @Override
public void visit(Mechanism obj) { visitObject(obj); }

  @Override
public void visit(cb.petal.Object obj) { visitObject(obj); }

  @Override
public void visit(Message obj) { visitObject(obj); }

  @Override
public void visit(Event obj) { visitObject(obj); }

  @Override
public void visit(StateView obj) { visitObject(obj); }

  @Override
public void visit(TransView obj) { visitObject(obj); }

  @Override
public void visit(ActionTime obj) { visitObject(obj); }

  @Override
public void visit(StateDiagram obj) { visitObject(obj); }

  @Override
public void visit(Link obj) { visitObject(obj); }

  @Override
public void visit(State obj) { visitObject(obj); }

  @Override
public void visit(StateTransition obj) { visitObject(obj); }

  @Override
public void visit(SendEvent obj) { visitObject(obj); }

  @Override
public void visit(Action obj) { visitObject(obj); }

  @Override
public void visit(StateMachine obj) { visitObject(obj); }

  @Override
public void visit(UsesView obj) { visitObject(obj); }

  @Override
public void visit(InteractionDiagram obj) { visitObject(obj); }

  @Override
public void visit(InterObjView obj) { visitObject(obj); }

  @Override
public void visit(MessView obj) { visitObject(obj); }

  @Override
public void visit(UseCaseView obj) { visitObject(obj); }

  @Override
public void visit(LinkSelfView obj) { visitObject(obj); }

  @Override
public void visit(LinkView obj) { visitObject(obj); }

  @Override
public void visit(DataFlowView obj) { visitObject(obj); }

  @Override
public void visit(ObjectView obj) { visitObject(obj); }

  @Override
public void visit(FocusOfControl obj) { visitObject(obj); }

  @Override
public void visit(SelfMessView obj) { visitObject(obj); }

  @Override
public void visit(InterMessView obj) { visitObject(obj); }

  @Override
public void visit(ObjectDiagram obj) { visitObject(obj); }

  @Override
public void visit(RealizeView obj) { visitObject(obj); }

  @Override
public void visit(ExternalDoc obj) { visitObject(obj); }

  @Override
public void visit(ClassInstanceView obj) { visitObject(obj); }

  @Override
public void visit(Processor obj) { visitObject(obj); }

  @Override
public void visit(ConnectionRelationship obj) { visitObject(obj); }

  @Override
public void visit(RealizeRelationship obj) { visitObject(obj); }

  @Override
public void visit(Process obj) { visitObject(obj); }

  @Override
public void visit(Device obj) { visitObject(obj); }

  @Override
public void visit(ProcessorView obj) { visitObject(obj); }

  @Override
public void visit(DeviceView obj) { visitObject(obj); }

  @Override
public void visit(ConnectionView obj) { visitObject(obj); }

  @Override
public void visit(DependencyView obj) { visitObject(obj); }

  @Override
public void visit(InterfaceView obj) { visitObject(obj); }

  @Override
public void visit(ModuleVisibilityRelationship obj) { visitObject(obj); }

  @Override
public void visit(Swimlane obj) { visitObject(obj); }

  @Override
public void visit(Partition obj) { visitObject(obj); }

  @Override
public void visit(ActivityStateView obj) { visitObject(obj); }

  @Override
public void visit(DecisionView obj) { visitObject(obj); }

  @Override
public void visit(SynchronizationView obj) { visitObject(obj); }

  @Override
public void visit(ActivityDiagram obj) { visitObject(obj); }

  @Override
public void visit(ActivityState obj) { visitObject(obj); }

  @Override
public void visit(Decision obj) { visitObject(obj); }

  public void visit(SynchronizationState obj) { visitObject(obj); }

  // Plain stuff

  public void visit(SimpleObject obj) { visitObject(obj); }

  public void visit(StringLiteral obj) { }

  public void visit(BooleanLiteral obj) { }

  public void visit(FloatLiteral obj) { }

  public void visit(IntegerLiteral obj) { }

  public void visit(Tag tag) { }

  public void visit(Location loc) { }

  public void visit(Tuple tuple) { }
}

