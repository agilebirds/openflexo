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
 * Visitor containing just empty methods.
 *
 * @version $Id: EmptyVisitor.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class EmptyVisitor implements Visitor {
  public EmptyVisitor() {}

  @Override
public void visit(PetalFile obj) { }

  @Override
public void visit(List list) { }

  @Override
public void visit(Value value) { }

  @Override
public void visit(Class obj) { }

  @Override
public void visit(Font obj) { }

  @Override
public void visit(Design obj) { }

  @Override
public void visit(Petal obj) { }

  @Override
public void visit(ClassAttribute obj) { }

  @Override
public void visit(LogicalCategory obj) { }

  @Override
public void visit(ClassCategory obj) { }

  @Override
public void visit(UseCaseCategory obj) { }

  @Override
public void visit(SemanticInfo obj) { }

  @Override
public void visit(Operation obj) { }

  @Override
public void visit(Defaults obj) { }

  @Override
public void visit(Attribute obj) { }

  @Override
public void visit(Processes obj) {  }

  @Override
public void visit(Properties obj) { }

  @Override
public void visit(SubSystem obj) { }

  @Override
public void visit(UseCase obj) { }

  @Override
public void visit(UseCaseDiagram obj) { }

  @Override
public void visit(Compartment obj) { }

  @Override
public void visit(ItemLabel obj) { }

  @Override
public void visit(Label obj) { }

  @Override
public void visit(SimpleViewObject obj) { }

  @Override
public void visit(ClassView obj) { }

  @Override
public void visit(CategoryView obj) { }

  @Override
public void visit(Parameter obj) { }

  @Override
public void visit(Association obj) { }

  @Override
public void visit(Role obj) { }

  @Override
public void visit(ModView obj) { }

  @Override
public void visit(UsesRelationship obj) { }

  @Override
public void visit(ClassDiagram obj) { }

  @Override
public void visit(DependencyRelationship obj) { }

  @Override
public void visit(ImportView obj) { }

  @Override
public void visit(InheritView obj) { }

  @Override
public void visit(InheritanceRelationship obj) { }

  @Override
public void visit(InstantiateView obj) { }

  @Override
public void visit(InstantiationRelationship obj) { }

  @Override
public void visit(ModVisView obj) { }

  @Override
public void visit(ModuleDiagram obj) { }

  @Override
public void visit(ProcessDiagram obj) { }

  @Override
public void visit(SubSysView obj) { }

  @Override
public void visit(VisibilityRelationship obj) { }

  @Override
public void visit(Module obj) { }

  @Override
public void visit(RoleView obj) { }

  @Override
public void visit(SegLabel obj) { }

  @Override
public void visit(AssociationViewNew obj) { }

  @Override
public void visit(NoteView obj) { }

  @Override
public void visit(InheritTreeView obj) { }

  @Override
public void visit(AttachView obj) { }

  @Override
public void visit(AssocAttachView obj) { }

  @Override
public void visit(Mechanism obj) { }

  @Override
public void visit(cb.petal.Object obj) { }

  @Override
public void visit(Message obj) { }

  @Override
public void visit(Event obj) { }

  @Override
public void visit(StateView obj) { }

  @Override
public void visit(TransView obj) { }

  @Override
public void visit(ActionTime obj) { }

  @Override
public void visit(StateDiagram obj) { }

  @Override
public void visit(Link obj) { }

  @Override
public void visit(State obj) { }

  @Override
public void visit(StateTransition obj) { }

  @Override
public void visit(SendEvent obj) { }

  @Override
public void visit(Action obj) { }

  @Override
public void visit(StateMachine obj) { }

  @Override
public void visit(UsesView obj) { }

  @Override
public void visit(InteractionDiagram obj) { }

  @Override
public void visit(InterObjView obj) { }

  @Override
public void visit(MessView obj) { }

  @Override
public void visit(UseCaseView obj) { }

  @Override
public void visit(LinkSelfView obj) { }

  @Override
public void visit(LinkView obj) { }

  @Override
public void visit(DataFlowView obj) { }

  @Override
public void visit(ObjectView obj) { }

  @Override
public void visit(FocusOfControl obj) { }

  @Override
public void visit(SelfMessView obj) { }

  @Override
public void visit(InterMessView obj) { }

  @Override
public void visit(ObjectDiagram obj) { }

  @Override
public void visit(RealizeView obj) { }

  @Override
public void visit(ExternalDoc obj) { }

  @Override
public void visit(ClassInstanceView obj) { }

  @Override
public void visit(Processor obj) { }

  @Override
public void visit(ConnectionRelationship obj) { }

  @Override
public void visit(RealizeRelationship obj) { }

  @Override
public void visit(Process obj) { }

  @Override
public void visit(Device obj) { }

  @Override
public void visit(ProcessorView obj) { }

  @Override
public void visit(DeviceView obj) { }

  @Override
public void visit(ConnectionView obj) { }

  @Override
public void visit(DependencyView obj) { }

  @Override
public void visit(InterfaceView obj) { }

  @Override
public void visit(ModuleVisibilityRelationship obj) { }

  @Override
public void visit(Swimlane obj) { }

  @Override
public void visit(Partition obj) { }

  @Override
public void visit(ActivityStateView obj) { }

  @Override
public void visit(DecisionView obj) { }

  @Override
public void visit(SynchronizationView obj) { }

  @Override
public void visit(ActivityDiagram obj) { }

  @Override
public void visit(ActivityState obj) { }

  @Override
public void visit(Decision obj) { }

  public void visit(SynchronizationState obj) { }

  // Plain stuff

  public void visit(SimpleObject obj) { }

  public void visit(StringLiteral obj) { }

  public void visit(BooleanLiteral obj) { }

  public void visit(FloatLiteral obj) { }

  public void visit(IntegerLiteral obj) { }

  public void visit(Tag tag) { }

  public void visit(Location loc) { }

  public void visit(Tuple tuple) { }
}

