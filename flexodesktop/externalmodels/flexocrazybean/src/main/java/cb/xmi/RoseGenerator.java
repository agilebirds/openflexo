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
package cb.xmi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import ru.novosoft.uml.MExtension;
import ru.novosoft.uml.MFactory;
import ru.novosoft.uml.behavior.activity_graphs.MActionState;
import ru.novosoft.uml.behavior.activity_graphs.MActivityGraph;
import ru.novosoft.uml.behavior.activity_graphs.MCallState;
import ru.novosoft.uml.behavior.activity_graphs.MClassifierInState;
import ru.novosoft.uml.behavior.activity_graphs.MObjectFlowState;
import ru.novosoft.uml.behavior.activity_graphs.MPartition;
import ru.novosoft.uml.behavior.activity_graphs.MSubactivityState;
import ru.novosoft.uml.behavior.collaborations.MAssociationEndRole;
import ru.novosoft.uml.behavior.collaborations.MAssociationRole;
import ru.novosoft.uml.behavior.collaborations.MClassifierRole;
import ru.novosoft.uml.behavior.collaborations.MCollaboration;
import ru.novosoft.uml.behavior.collaborations.MInteraction;
import ru.novosoft.uml.behavior.collaborations.MMessage;
import ru.novosoft.uml.behavior.common_behavior.MAction;
import ru.novosoft.uml.behavior.common_behavior.MActionSequence;
import ru.novosoft.uml.behavior.common_behavior.MArgument;
import ru.novosoft.uml.behavior.common_behavior.MAttributeLink;
import ru.novosoft.uml.behavior.common_behavior.MCallAction;
import ru.novosoft.uml.behavior.common_behavior.MComponentInstance;
import ru.novosoft.uml.behavior.common_behavior.MCreateAction;
import ru.novosoft.uml.behavior.common_behavior.MDataValue;
import ru.novosoft.uml.behavior.common_behavior.MDestroyAction;
import ru.novosoft.uml.behavior.common_behavior.MException;
import ru.novosoft.uml.behavior.common_behavior.MInstance;
import ru.novosoft.uml.behavior.common_behavior.MLink;
import ru.novosoft.uml.behavior.common_behavior.MLinkEnd;
import ru.novosoft.uml.behavior.common_behavior.MLinkObject;
import ru.novosoft.uml.behavior.common_behavior.MNodeInstance;
import ru.novosoft.uml.behavior.common_behavior.MObject;
import ru.novosoft.uml.behavior.common_behavior.MReception;
import ru.novosoft.uml.behavior.common_behavior.MReturnAction;
import ru.novosoft.uml.behavior.common_behavior.MSendAction;
import ru.novosoft.uml.behavior.common_behavior.MSignal;
import ru.novosoft.uml.behavior.common_behavior.MStimulus;
import ru.novosoft.uml.behavior.common_behavior.MTerminateAction;
import ru.novosoft.uml.behavior.common_behavior.MUninterpretedAction;
import ru.novosoft.uml.behavior.state_machines.MCallEvent;
import ru.novosoft.uml.behavior.state_machines.MChangeEvent;
import ru.novosoft.uml.behavior.state_machines.MCompositeState;
import ru.novosoft.uml.behavior.state_machines.MFinalState;
import ru.novosoft.uml.behavior.state_machines.MGuard;
import ru.novosoft.uml.behavior.state_machines.MPseudostate;
import ru.novosoft.uml.behavior.state_machines.MSignalEvent;
import ru.novosoft.uml.behavior.state_machines.MSimpleState;
import ru.novosoft.uml.behavior.state_machines.MState;
import ru.novosoft.uml.behavior.state_machines.MStateMachine;
import ru.novosoft.uml.behavior.state_machines.MStubState;
import ru.novosoft.uml.behavior.state_machines.MSubmachineState;
import ru.novosoft.uml.behavior.state_machines.MSynchState;
import ru.novosoft.uml.behavior.state_machines.MTimeEvent;
import ru.novosoft.uml.behavior.state_machines.MTransition;
import ru.novosoft.uml.behavior.use_cases.MActor;
import ru.novosoft.uml.behavior.use_cases.MExtend;
import ru.novosoft.uml.behavior.use_cases.MExtensionPoint;
import ru.novosoft.uml.behavior.use_cases.MInclude;
import ru.novosoft.uml.behavior.use_cases.MUseCase;
import ru.novosoft.uml.behavior.use_cases.MUseCaseInstance;
import ru.novosoft.uml.foundation.core.MAbstraction;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAssociationClass;
import ru.novosoft.uml.foundation.core.MAssociationEnd;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MBinding;
import ru.novosoft.uml.foundation.core.MClass;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MComment;
import ru.novosoft.uml.foundation.core.MComponent;
import ru.novosoft.uml.foundation.core.MConstraint;
import ru.novosoft.uml.foundation.core.MDataType;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MElementResidence;
import ru.novosoft.uml.foundation.core.MFlow;
import ru.novosoft.uml.foundation.core.MGeneralization;
import ru.novosoft.uml.foundation.core.MInterface;
import ru.novosoft.uml.foundation.core.MMethod;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MNode;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MParameter;
import ru.novosoft.uml.foundation.core.MPermission;
import ru.novosoft.uml.foundation.core.MPresentationElement;
import ru.novosoft.uml.foundation.core.MRelationship;
import ru.novosoft.uml.foundation.core.MTemplateParameter;
import ru.novosoft.uml.foundation.core.MUsage;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
import ru.novosoft.uml.model_management.MElementImport;
import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.model_management.MSubsystem;
import cb.parser.PrintVisitor;
import cb.petal.ClassCategory;
import cb.petal.PetalFile;
import cb.util.PetalObjectFactory;

/**
 * Convert an <a href="http://xml.coverpages.org/xmi.html">XMI</a> file into a Rose Petal file.
 * 
 * @version $Id: RoseGenerator.java,v 1.2 2011/09/12 11:47:01 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class RoseGenerator /*extends DescendingVisitor*/{
	/**
	 * Where to dump the XMI file
	 */
	protected String dump;

	/**
	 * Which factory to use
	 */
	protected static PetalObjectFactory factory = PetalObjectFactory.getInstance();

	/**
	 * The Rose Petal file to convert
	 */
	protected PetalFile tree;

	/**
	 * The XMI model being set up
	 */
	protected MModel model;

	/**
	 * Stack<ClassCategory>
	 */
	private Stack packages = new Stack();

	/**
	 * The current package level (may be nested)
	 */
	private ClassCategory pack;

	/**
	 * Use that until NSUML has visitors.
	 */
	private Dispatcher d = new Dispatcher(this);

	/**
	 * Register created objects by the quid of the petal object.
	 */
	protected HashMap quid_map = new HashMap(); // Map<quid, MClassifier>

	protected HashMap package_map = new HashMap(); // Map<ClassCategory, MPackage>

	/**
	 * @param model
	 *            the XMI model to convert
	 * @param dump
	 *            where to dump the generated petal file
	 */
	public RoseGenerator(MModel model, String dump) {
		this.dump = dump;
		this.model = model;

		tree = factory.createModel();
	}

	/**
	 * Start generation of Petal file.
	 */
	public void start() {
		d.accept(model); // Should read: model.accept(this) ...
	}

	public void dump() throws IOException {
		PrintStream os = new PrintStream(new FileOutputStream(dump));
		tree.accept(new PrintVisitor(os));
		os.close();
	}

	/**
	 * @return generated model
	 */
	public PetalFile getModel() {
		return tree;
	}

	public void visit(MExtension obj) {
	}

	public void visit(MFactory obj) {
	}

	public void visit(MActionState obj) {
	}

	public void visit(MActivityGraph obj) {
	}

	public void visit(MCallState obj) {
	}

	public void visit(MClassifierInState obj) {
	}

	public void visit(MObjectFlowState obj) {
	}

	public void visit(MPartition obj) {
	}

	public void visit(MSubactivityState obj) {
	}

	public void visit(MAssociationEndRole obj) {
	}

	public void visit(MAssociationRole obj) {
	}

	public void visit(MClassifierRole obj) {
	}

	public void visit(MCollaboration obj) {
	}

	public void visit(MInteraction obj) {
	}

	public void visit(MMessage obj) {
	}

	public void visit(MAction obj) {
	}

	public void visit(MActionSequence obj) {
	}

	public void visit(MArgument obj) {
	}

	public void visit(MAttributeLink obj) {
	}

	public void visit(MCallAction obj) {
	}

	public void visit(MComponentInstance obj) {
	}

	public void visit(MCreateAction obj) {
	}

	public void visit(MDataValue obj) {
	}

	public void visit(MDestroyAction obj) {
	}

	public void visit(MException obj) {
	}

	public void visit(MInstance obj) {
	}

	public void visit(MLinkEnd obj) {
	}

	public void visit(MLink obj) {
	}

	public void visit(MLinkObject obj) {
	}

	public void visit(MNodeInstance obj) {
	}

	public void visit(MObject obj) {
	}

	public void visit(MReception obj) {
	}

	public void visit(MReturnAction obj) {
	}

	public void visit(MSendAction obj) {
	}

	public void visit(MSignal obj) {
	}

	public void visit(MStimulus obj) {
	}

	public void visit(MTerminateAction obj) {
	}

	public void visit(MUninterpretedAction obj) {
	}

	public void visit(MCallEvent obj) {
	}

	public void visit(MChangeEvent obj) {
	}

	public void visit(MCompositeState obj) {
	}

	public void visit(MFinalState obj) {
	}

	public void visit(MGuard obj) {
	}

	public void visit(MPseudostate obj) {
	}

	public void visit(MSignalEvent obj) {
	}

	public void visit(MSimpleState obj) {
	}

	public void visit(MState obj) {
	}

	public void visit(MStateMachine obj) {
	}

	public void visit(MStubState obj) {
	}

	public void visit(MSubmachineState obj) {
	}

	public void visit(MSynchState obj) {
	}

	public void visit(MTimeEvent obj) {
	}

	public void visit(MTransition obj) {
	}

	public void visit(MActor obj) {
	}

	public void visit(MExtend obj) {
	}

	public void visit(MExtensionPoint obj) {
	}

	public void visit(MInclude obj) {
	}

	public void visit(MUseCase obj) {
	}

	public void visit(MUseCaseInstance obj) {
	}

	public void visit(MAbstraction obj) {
	}

	public void visit(MAssociationClass obj) {
	}

	public void visit(MAssociationEnd obj) {
	}

	public void visit(MAssociation obj) {
	}

	public void visit(MAttribute obj) {
	}

	public void visit(MBinding obj) {
	}

	public void visit(MClass obj) {
	}

	public void visit(MClassifier obj) {
	}

	public void visit(MComment obj) {
	}

	public void visit(MComponent obj) {
	}

	public void visit(MConstraint obj) {
	}

	public void visit(MDataType obj) {
	}

	public void visit(MDependency obj) {
	}

	public void visit(MElementResidence obj) {
	}

	public void visit(MFlow obj) {
	}

	public void visit(MGeneralization obj) {
	}

	public void visit(MInterface obj) {
	}

	public void visit(MMethod obj) {
	}

	public void visit(MNamespace obj) {
	}

	public void visit(MNode obj) {
	}

	public void visit(MOperation obj) {
	}

	public void visit(MParameter obj) {
	}

	public void visit(MPermission obj) {
	}

	public void visit(MPresentationElement obj) {
	}

	public void visit(MRelationship obj) {
	}

	public void visit(MTemplateParameter obj) {
	}

	public void visit(MUsage obj) {
	}

	public void visit(MElementImport obj) {
	}

	public void visit(MModel obj) {
		tree.setModelName(obj.getName());

		for (Iterator i = obj.getOwnedElements().iterator(); i.hasNext();)
			d.accept((MModelElement) i.next());
	}

	public void visit(MPackage obj) {
	}

	public void visit(MSubsystem obj) {
	}

	public void visit(MStereotype obj) {
	}

	public void visit(MTaggedValue obj) {
	}

}
