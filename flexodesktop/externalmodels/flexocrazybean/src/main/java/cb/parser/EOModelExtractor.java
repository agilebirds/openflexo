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
package cb.parser;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import cb.petal.Association;
import cb.petal.BooleanLiteral;
import cb.petal.Class;
import cb.petal.ClassAttribute;
import cb.petal.DescendingVisitor;
import cb.petal.Design;
import cb.petal.FloatLiteral;
import cb.petal.InheritanceRelationship;
import cb.petal.IntegerLiteral;
import cb.petal.List;
import cb.petal.Location;
import cb.petal.Operation;
import cb.petal.Parameter;
import cb.petal.Petal;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.PetalObject;
import cb.petal.Role;
import cb.petal.StringLiteral;
import cb.petal.Tag;
import cb.petal.Tuple;
import cb.petal.Value;

/**
 * Print petal file exactly like Rose would with some limitations concerning indendattion, i.e., if you don't mind white space, input and
 * output files are identical.
 * 
 * @version $Id: EOModelExtractor.java,v 1.3 2011/09/12 11:47:21 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class EOModelExtractor extends DescendingVisitor {
	private PrintStream out;
	private int level = 0;
	private int column = 0, row = 1;

	public Hashtable<String, cb.petal.Class> classes;
	private Hashtable<Role, String> relations;
	public Vector<Class> orderedClasses;

	public EOModelExtractor(PrintStream out) {
		this.out = out;
		orderedClasses = new Vector<cb.petal.Class>();
		classes = new Hashtable<String, Class>();
		relations = new Hashtable<Role, String>();
	}

	public void arrangeRelationship() {
		Enumeration en = classes.elements();
		while (en.hasMoreElements()) {
			cb.petal.Class currentClass = (cb.petal.Class) en.nextElement();
			Iterator it = relations.keySet().iterator();
			while (it.hasNext()) {
				Role rel = (Role) it.next();
				if (relations.get(rel).equals(getLabel(currentClass))) {
					currentClass.addRelationship(rel);
					System.out.println("adding " + getLabel(rel) + " to class " + getLabel(currentClass));
				}
			}
		}
	}

	@Override
	public void visitObject(PetalObject obj) {
		for (Iterator i = obj.getPropertyList().iterator(); i.hasNext();) {
			((PetalNode) i.next()).accept(this);
		}
	}

	@Override
	public void visit(cb.petal.Class obj) {
		String className = getLabel(obj);
		orderedClasses.add(obj);
		classes.put(className, obj);
		String superClass = getSuperClassLabel(obj);
		System.out.println("Class:" + className + (superClass.length() > 0 ? " extends " + superClass : ""));
		System.out.println("Description:" + obj.getDocumentation());
		List classAttributes = obj.getClassAttributeList();
		if (classAttributes != null && classAttributes.getChildCount() > 0) {
			for (int j = 0; j < classAttributes.getChildCount(); j++) {
				ClassAttribute attrib = (ClassAttribute) classAttributes.get(j);
				String attributeName = getLabel(attrib);
				System.out.println("\tAttribute:" + attributeName);
				System.out.println("\tDescription:" + attrib.getDocumentation());
			}
		}
		visitObject(obj);
	}

	public static String getSuperClassLabel(cb.petal.Class obj) {
		List superClasses = obj.getSuperclassList();
		if (superClasses == null || superClasses.getChildCount() == 0) {
			return "";
		}
		if (superClasses.getChildCount() > 1) {
			return "MoreThanOne";
		}
		InheritanceRelationship superClass = (InheritanceRelationship) superClasses.get(0);
		return getSupplierShortName(superClass);
	}

	public static String getSupplierShortName(Role role) {
		return role.getSupplier().substring(role.getSupplier().lastIndexOf(":") + 1);
	}

	public static String getSupplierShortName(InheritanceRelationship role) {
		return role.getSupplier().substring(role.getSupplier().lastIndexOf(":") + 1);
	}

	public static String getLabel(PetalObject object) {
		Iterator it = object.getParameterList().iterator();
		if (it.hasNext()) {
			return (String) it.next();
		}
		return "no_name";
	}

	@Override
	public void visit(Association obj) {
		List roleList = obj.getRoles();
		if (roleList != null && roleList.getChildCount() > 0) {
			if (roleList.getChildCount() == 2) {
				Role role1 = (Role) roleList.get(0);
				Role role2 = (Role) roleList.get(1);
				role1.commentaires = obj.getDocumentation() != null ? obj.getDocumentation() : "";
				role2.commentaires = obj.getDocumentation() != null ? obj.getDocumentation() : "";
				boolean isRole1ToOne = role1.getCardinality() != null && role1.getCardinality().equals("1");
				boolean isRole2ToOne = role2.getCardinality() != null && role2.getCardinality().equals("1");
				System.out.println("Relationship : " + getLabel(role1) + "(" + getSupplierShortName(role2)
						+ (isRole1ToOne ? "-->" : "-->>") + getSupplierShortName(role1) + ")");
				System.out.println("Relationship : " + getLabel(role2) + "(" + getSupplierShortName(role1)
						+ (isRole2ToOne ? "-->" : "-->>") + getSupplierShortName(role2) + ")");
				relations.put(role1, getSupplierShortName(role2));
				relations.put(role2, getSupplierShortName(role1));
			} else {
				System.out.println("Association with " + roleList.getChildCount() + " roles.");
			}
		} else {
			System.out.println("Association with no roles");
		}
	}

	@Override
	public void visit(Operation op) {
		String opLabel = getLabel(op);
		StringBuffer params = new StringBuffer("");
		List opParameters = op.getParameters();
		if (opParameters != null && opParameters.getChildCount() > 0) {
			for (int j = 0; j < opParameters.getChildCount(); j++) {
				Parameter param = (Parameter) opParameters.get(j);
				String paramLabel = getLabel(param);
				String paramType = param.getType();
				params.append(paramLabel).append(" ").append(paramType);
				if (j + 1 < opParameters.getChildCount()) {
					params.append(" ,");
				}
				System.out.println("\tOperation:" + opLabel + "(" + params.toString() + ")");
				System.out.println("\tDescription:" + (op.getDocumentation() != null ? op.getDocumentation() : ""));
			}
		}
	}

	public static String getOperationsDescription(cb.petal.Class obj) {
		List opList = obj.getOperationList();
		StringBuffer answer = new StringBuffer("");
		if (opList != null && opList.getChildCount() > 0) {
			for (int i = 0; i < opList.getChildCount(); i++) {
				Operation op = (Operation) opList.get(i);

				String opLabel = getLabel(op);
				StringBuffer params = new StringBuffer("");
				List opParameters = op.getParameters();
				if (opParameters != null && opParameters.getChildCount() > 0) {
					for (int j = 0; j < opParameters.getChildCount(); j++) {
						Parameter param = (Parameter) opParameters.get(j);
						String paramLabel = getLabel(param);
						String paramType = param.getType();
						params.append(paramType).append(" ").append(paramLabel);
						if (j + 1 < opParameters.getChildCount()) {
							params.append(" ,");
						}
						if (answer.length() > 0) {
							answer.append("\n\n");
						}
						answer.append("Operation : " + opLabel + "(" + params.toString() + ")\n");
						answer.append("\tDescription:" + (op.getDocumentation() != null ? op.getDocumentation() : ""));
					}
				}
			}
		}
		return answer.toString();
	}

	@Override
	public void visit(Petal obj) {
		visitObject(obj);
	}

	@Override
	public void visit(Design obj) {
		visitObject(obj);
	}

	/*
	 
	public void visit(Operation obj){}
	public void visit(Font obj){}

	public void visit(ClassAttribute obj){}
	public void visit(Defaults obj){}
	public void visit(Attribute obj){}
	public void visit(Processes obj){}
	public void visit(Properties obj){}
	public void visit(SubSystem obj){}
	public void visit(UseCase obj){}
	public void visit(UseCaseDiagram obj){}
	public void visit(Compartment obj){}
	public void visit(ItemLabel obj){}
	public void visit(Label obj){}
	public void visit(ClassCategory obj){}
	public void visit(LogicalCategory obj){}
	public void visit(UseCaseCategory obj){}
	public void visit(ClassView obj){}
	public void visit(CategoryView obj){}
	public void visit(Parameter obj){}
	
	public void visit(Role obj){}
	public void visit(ModView obj){}
	public void visit(SemanticInfo obj){}

	public void visit(UsesRelationship obj){}
	public void visit(RealizeRelationship obj){}
	public void visit(InheritanceRelationship obj){}
	public void visit(InstantiationRelationship obj){}
	public void visit(ConnectionRelationship obj){}
	public void visit(DependencyRelationship obj){}

	public void visit(SimpleViewObject obj){}
	public void visit(ClassDiagram obj){}
	public void visit(ImportView obj){}
	public void visit(InheritView obj){}
	public void visit(InstantiateView obj){}
	public void visit(ModVisView obj){}
	public void visit(ModuleDiagram obj){}
	public void visit(ProcessDiagram obj){}
	public void visit(SubSysView obj){}
	public void visit(VisibilityRelationship obj){}
	public void visit(Module obj){}
	public void visit(RoleView obj){}
	public void visit(SegLabel obj){}
	public void visit(AssociationViewNew obj){}
	public void visit(NoteView obj){}
	public void visit(InheritTreeView obj){}
	public void visit(AttachView obj){}
	public void visit(AssocAttachView obj){}
	public void visit(Mechanism obj){}
	public void visit(cb.petal.Object obj){}
	public void visit(Link obj){}
	public void visit(Message obj){}
	public void visit(Event obj){}
	public void visit(StateView obj){}
	public void visit(TransView obj){}
	public void visit(ActionTime obj){}
	public void visit(StateDiagram obj){}
	public void visit(State obj){}
	public void visit(StateTransition obj){}
	public void visit(SendEvent obj){}
	public void visit(Action obj){}
	public void visit(StateMachine obj){}
	public void visit(UsesView obj){}
	public void visit(InteractionDiagram obj){}
	public void visit(InterObjView obj){}
	public void visit(MessView obj){}
	public void visit(UseCaseView obj){}
	public void visit(LinkSelfView obj){}
	public void visit(LinkView obj){}
	public void visit(DataFlowView obj){}
	public void visit(ObjectView obj){}
	public void visit(FocusOfControl obj){}
	public void visit(SelfMessView obj){}
	public void visit(InterMessView obj){}
	public void visit(ObjectDiagram obj){}
	public void visit(ExternalDoc obj){}
	public void visit(ClassInstanceView obj){}
	public void visit(Processor obj){}
	public void visit(Process obj){}
	public void visit(Device obj){}
	public void visit(ProcessorView obj){}
	public void visit(DeviceView obj){}
	public void visit(ConnectionView obj){}
	public void visit(RealizeView obj){}
	public void visit(DependencyView obj){}
	public void visit(InterfaceView obj){}
	public void visit(ModuleVisibilityRelationship obj){}
	public void visit(Swimlane obj){}
	public void visit(Partition obj){}
	public void visit(ActivityStateView obj){}
	public void visit(DecisionView obj){}
	public void visit(SynchronizationView obj){}
	public void visit(ActivityDiagram obj){}
	public void visit(ActivityState obj){}
	public void visit(Decision obj){}
	public void visit(SynchronizationState obj){}
	public void visit(SimpleObject obj){}
	
	*/
	/*
	  public void visit(List obj){}
	  public void visit(Value obj){}
	  public void visit(Tuple obj){}
	  public void visit(Location obj){}
	  public void visit(Tag obj){}

	  public void visit(StringLiteral obj){}
	  public void visit(BooleanLiteral obj){}
	  public void visit(FloatLiteral obj){}
	  public void visit(IntegerLiteral obj){}
	  */

	@Override
	public void visit(PetalFile obj) {
		obj.getPetal().accept(this);
		obj.getDesign().accept(this);

	}

	@Override
	public void visit(StringLiteral obj) {
	}

	@Override
	public void visit(BooleanLiteral obj) {
	}

	@Override
	public void visit(FloatLiteral obj) {
	}

	@Override
	public void visit(IntegerLiteral obj) {
	}

	@Override
	public void visit(Tag ref) {
	}

	@Override
	public void visit(Location loc) {
	}

	@Override
	public void visit(List list) {
		java.util.List c = list.getElements();

		if (c.size() > 0) {
			for (Iterator i = c.iterator(); i.hasNext();) {
				((PetalNode) i.next()).accept(this);
			}
		}

	}

	@Override
	public void visit(Value value) {
	}

	@Override
	public void visit(Tuple tuple) {
	}

	public static void main(String[] args) {
		PetalFile tree = PetalParser.parse(args);
		EOModelExtractor extractor = new EOModelExtractor(System.out);
		tree.accept(extractor);
		extractor.arrangeRelationship();
	}
}
