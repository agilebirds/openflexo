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
 * Visitor interface. The problem with this is that we may find
 * further petal objects in future versions which must be added then.
 * So it's safer to inherit from EmptyVisitor or DescendingVisitor,
 * because all methods are garantueed to be implemented.
 *
 * @version $Id: Visitor.java,v 1.2 2011/09/12 11:46:48 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public interface Visitor {
  public void visit(PetalFile obj);

  public void visit(cb.petal.Class obj);

  public void visit(Operation obj);
  public void visit(Font obj);
  public void visit(Petal obj);
  public void visit(Design obj);
  public void visit(ClassAttribute obj);
  public void visit(Defaults obj);
  public void visit(Attribute obj);
  public void visit(Processes obj);
  public void visit(Properties obj);
  public void visit(SubSystem obj);
  public void visit(UseCase obj);
  public void visit(UseCaseDiagram obj);
  public void visit(Compartment obj);
  public void visit(ItemLabel obj);
  public void visit(Label obj);
  public void visit(ClassCategory obj);
  public void visit(LogicalCategory obj);
  public void visit(UseCaseCategory obj);
  public void visit(ClassView obj);
  public void visit(CategoryView obj);
  public void visit(Parameter obj);
  public void visit(Association obj);
  public void visit(Role obj);
  public void visit(ModView obj);
  public void visit(SemanticInfo obj);

  public void visit(UsesRelationship obj);
  public void visit(RealizeRelationship obj);
  public void visit(InheritanceRelationship obj);
  public void visit(InstantiationRelationship obj);
  public void visit(ConnectionRelationship obj);
  public void visit(DependencyRelationship obj);

  public void visit(SimpleViewObject obj);
  public void visit(ClassDiagram obj);
  public void visit(ImportView obj);
  public void visit(InheritView obj);
  public void visit(InstantiateView obj);
  public void visit(ModVisView obj);
  public void visit(ModuleDiagram obj);
  public void visit(ProcessDiagram obj);
  public void visit(SubSysView obj);
  public void visit(VisibilityRelationship obj);
  public void visit(Module obj);
  public void visit(RoleView obj);
  public void visit(SegLabel obj);
  public void visit(AssociationViewNew obj);
  public void visit(NoteView obj);
  public void visit(InheritTreeView obj);
  public void visit(AttachView obj);
  public void visit(AssocAttachView obj);
  public void visit(Mechanism obj);
  public void visit(cb.petal.Object obj);
  public void visit(Link obj);
  public void visit(Message obj);
  public void visit(Event obj);
  public void visit(StateView obj);
  public void visit(TransView obj);
  public void visit(ActionTime obj);
  public void visit(StateDiagram obj);
  public void visit(State obj);
  public void visit(StateTransition obj);
  public void visit(SendEvent obj);
  public void visit(Action obj);
  public void visit(StateMachine obj);
  public void visit(UsesView obj);
  public void visit(InteractionDiagram obj);
  public void visit(InterObjView obj);
  public void visit(MessView obj);
  public void visit(UseCaseView obj);
  public void visit(LinkSelfView obj);
  public void visit(LinkView obj);
  public void visit(DataFlowView obj);
  public void visit(ObjectView obj);
  public void visit(FocusOfControl obj);
  public void visit(SelfMessView obj);
  public void visit(InterMessView obj);
  public void visit(ObjectDiagram obj);
  public void visit(ExternalDoc obj);
  public void visit(ClassInstanceView obj);
  public void visit(Processor obj);
  public void visit(Process obj);
  public void visit(Device obj);
  public void visit(ProcessorView obj);
  public void visit(DeviceView obj);
  public void visit(ConnectionView obj);
  public void visit(RealizeView obj);
  public void visit(DependencyView obj);
  public void visit(InterfaceView obj);
  public void visit(ModuleVisibilityRelationship obj);
  public void visit(Swimlane obj);
  public void visit(Partition obj);
  public void visit(ActivityStateView obj);
  public void visit(DecisionView obj);
  public void visit(SynchronizationView obj);
  public void visit(ActivityDiagram obj);
  public void visit(ActivityState obj);
  public void visit(Decision obj);
  public void visit(SynchronizationState obj);
  public void visit(SimpleObject obj);

  public void visit(List obj);
  public void visit(Value obj);
  public void visit(Tuple obj);
  public void visit(Location obj);
  public void visit(Tag obj);

  public void visit(StringLiteral obj);
  public void visit(BooleanLiteral obj);
  public void visit(FloatLiteral obj);
  public void visit(IntegerLiteral obj);
}
