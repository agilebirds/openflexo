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

import ru.novosoft.uml.MBase;
import ru.novosoft.uml.MExtension;
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
import ru.novosoft.uml.foundation.core.MNamespace;
import ru.novosoft.uml.foundation.core.MNode;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MParameter;
import ru.novosoft.uml.foundation.core.MPermission;
import ru.novosoft.uml.foundation.core.MRelationship;
import ru.novosoft.uml.foundation.core.MTemplateParameter;
import ru.novosoft.uml.foundation.core.MUsage;
import ru.novosoft.uml.foundation.extension_mechanisms.MStereotype;
import ru.novosoft.uml.foundation.extension_mechanisms.MTaggedValue;
import ru.novosoft.uml.model_management.MElementImport;
import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.model_management.MSubsystem;

class Dispatcher {
  private RoseGenerator gen;

  Dispatcher(RoseGenerator gen) {
    this.gen = gen;
  }

  void accept(MBase obj) {
    if(obj instanceof MExtension)
      gen.visit((MExtension)obj);
    else if(obj instanceof MActionState)
      gen.visit((MActionState)obj);
    else if(obj instanceof MActivityGraph)
      gen.visit((MActivityGraph)obj);
    else if(obj instanceof MCallState)
      gen.visit((MCallState)obj);
    else if(obj instanceof MClassifierInState)
      gen.visit((MClassifierInState)obj);
    else if(obj instanceof MObjectFlowState)
      gen.visit((MObjectFlowState)obj);
    else if(obj instanceof MPartition)
      gen.visit((MPartition)obj);
    else if(obj instanceof MSubactivityState)
      gen.visit((MSubactivityState)obj);
    else if(obj instanceof MAssociationEndRole)
      gen.visit((MAssociationEndRole)obj);
    else if(obj instanceof MAssociationRole)
      gen.visit((MAssociationRole)obj);
    else if(obj instanceof MClassifierRole)
      gen.visit((MClassifierRole)obj);
    else if(obj instanceof MCollaboration)
      gen.visit((MCollaboration)obj);
    else if(obj instanceof MInteraction)
      gen.visit((MInteraction)obj);
    else if(obj instanceof MMessage)
      gen.visit((MMessage)obj);
    else if(obj instanceof MAction)
      gen.visit((MAction)obj);
    else if(obj instanceof MActionSequence)
      gen.visit((MActionSequence)obj);
    else if(obj instanceof MArgument)
      gen.visit((MArgument)obj);
    else if(obj instanceof MAttributeLink)
      gen.visit((MAttributeLink)obj);
    else if(obj instanceof MCallAction)
      gen.visit((MCallAction)obj);
    else if(obj instanceof MComponentInstance)
      gen.visit((MComponentInstance)obj);
    else if(obj instanceof MCreateAction)
      gen.visit((MCreateAction)obj);
    else if(obj instanceof MDataValue)
      gen.visit((MDataValue)obj);
    else if(obj instanceof MDestroyAction)
      gen.visit((MDestroyAction)obj);
    else if(obj instanceof MException)
      gen.visit((MException)obj);
    else if(obj instanceof MInstance)
      gen.visit((MInstance)obj);
    else if(obj instanceof MLinkEnd)
      gen.visit((MLinkEnd)obj);
    else if(obj instanceof MLink)
      gen.visit((MLink)obj);
    else if(obj instanceof MLinkObject)
      gen.visit((MLinkObject)obj);
    else if(obj instanceof MNodeInstance)
      gen.visit((MNodeInstance)obj);
    else if(obj instanceof MObject)
      gen.visit((MObject)obj);
    else if(obj instanceof MReception)
      gen.visit((MReception)obj);
    else if(obj instanceof MReturnAction)
      gen.visit((MReturnAction)obj);
    else if(obj instanceof MSendAction)
      gen.visit((MSendAction)obj);
    else if(obj instanceof MSignal)
      gen.visit((MSignal)obj);
    else if(obj instanceof MStimulus)
      gen.visit((MStimulus)obj);
    else if(obj instanceof MTerminateAction)
      gen.visit((MTerminateAction)obj);
    else if(obj instanceof MUninterpretedAction)
      gen.visit((MUninterpretedAction)obj);
    else if(obj instanceof MChangeEvent)
      gen.visit((MChangeEvent)obj);
    else if(obj instanceof MCallEvent)
      gen.visit((MCallEvent)obj);
    else if(obj instanceof MCompositeState)
      gen.visit((MCompositeState)obj);
    else if(obj instanceof MFinalState)
      gen.visit((MFinalState)obj);
    else if(obj instanceof MGuard)
      gen.visit((MGuard)obj);
    else if(obj instanceof MPseudostate)
      gen.visit((MPseudostate)obj);
    else if(obj instanceof MSignalEvent)
      gen.visit((MSignalEvent)obj);
    else if(obj instanceof MSimpleState)
      gen.visit((MSimpleState)obj);
    else if(obj instanceof MState)
      gen.visit((MState)obj);
    else if(obj instanceof MStateMachine)
      gen.visit((MStateMachine)obj);
    else if(obj instanceof MStubState)
      gen.visit((MStubState)obj);
    else if(obj instanceof MSubmachineState)
      gen.visit((MSubmachineState)obj);
    else if(obj instanceof MSynchState)
      gen.visit((MSynchState)obj);
    else if(obj instanceof MTimeEvent)
      gen.visit((MTimeEvent)obj);
    else if(obj instanceof MTransition)
      gen.visit((MTransition)obj);
    else if(obj instanceof MActor)
      gen.visit((MActor)obj);
    else if(obj instanceof MExtend)
      gen.visit((MExtend)obj);
    else if(obj instanceof MExtensionPoint)
      gen.visit((MExtensionPoint)obj);
    else if(obj instanceof MInclude)
      gen.visit((MInclude)obj);
    else if(obj instanceof MUseCase)
      gen.visit((MUseCase)obj);
    else if(obj instanceof MUseCaseInstance)
      gen.visit((MUseCaseInstance)obj);
    else if(obj instanceof MAbstraction)
      gen.visit((MAbstraction)obj);
    else if(obj instanceof MAssociationClass)
      gen.visit((MAssociationClass)obj);
    else if(obj instanceof MAssociationEnd)
      gen.visit((MAssociationEnd)obj);
    else if(obj instanceof MAssociation)
      gen.visit((MAssociation)obj);
    else if(obj instanceof MAttribute)
      gen.visit((MAttribute)obj);
    else if(obj instanceof MBinding)
      gen.visit((MBinding)obj);
    else if(obj instanceof MClass)
      gen.visit((MClass)obj);
    else if(obj instanceof MClassifier)
      gen.visit((MClassifier)obj);
    else if(obj instanceof MComment)
      gen.visit((MComment)obj);
    else if(obj instanceof MComponent)
      gen.visit((MComponent)obj);
    else if(obj instanceof MConstraint)
      gen.visit((MConstraint)obj);
    else if(obj instanceof MDataType)
      gen.visit((MDataType)obj);
    else if(obj instanceof MDependency)
      gen.visit((MDependency)obj);
    else if(obj instanceof MElementResidence)
      gen.visit((MElementResidence)obj);
    else if(obj instanceof MFlow)
      gen.visit((MFlow)obj);
    else if(obj instanceof MGeneralization)
      gen.visit((MGeneralization)obj);
    else if(obj instanceof MInterface)
      gen.visit((MInterface)obj);
    else if(obj instanceof MMethod)
      gen.visit((MMethod)obj);
    else if(obj instanceof MNamespace)
      gen.visit((MNamespace)obj);
    else if(obj instanceof MNode)
      gen.visit((MNode)obj);
    else if(obj instanceof MOperation)
      gen.visit((MOperation)obj);
    else if(obj instanceof MParameter)
      gen.visit((MParameter)obj);
    else if(obj instanceof MPermission)
      gen.visit((MPermission)obj);
    else if(obj instanceof MRelationship)
      gen.visit((MRelationship)obj);
    else if(obj instanceof MTemplateParameter)
      gen.visit((MTemplateParameter)obj);
    else if(obj instanceof MUsage)
      gen.visit((MUsage)obj);
    else if(obj instanceof MElementImport)
      gen.visit((MElementImport)obj);
    else if(obj instanceof MModel)
      gen.visit((MModel)obj);
    else if(obj instanceof MPackage)
      gen.visit((MPackage)obj);
    else if(obj instanceof MSubsystem)
      gen.visit((MSubsystem)obj);
    else if(obj instanceof MTaggedValue)
      gen.visit((MTaggedValue)obj);
    else if(obj instanceof MStereotype)
      gen.visit((MStereotype)obj);
  }
}
