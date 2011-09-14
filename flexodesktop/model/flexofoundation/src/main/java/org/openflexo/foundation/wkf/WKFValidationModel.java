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
package org.openflexo.foundation.wkf;

import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.RequiredBindingValidationRule;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.wkf.edge.BackwardWSEdge;
import org.openflexo.foundation.wkf.edge.ExternalMessageEdge;
import org.openflexo.foundation.wkf.edge.ExternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.ExternalMessageOutEdge;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.ForwardWSEdge;
import org.openflexo.foundation.wkf.edge.InternalMessageInEdge;
import org.openflexo.foundation.wkf.edge.InternalMessageOutEdge;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.edge.TokenEdge;
import org.openflexo.foundation.wkf.edge.TransferWSEdge;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.ExclusiveEventBasedOperator;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.IFOperator;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.ws.FlexoPortMap;
import org.openflexo.foundation.wkf.ws.InOutPort;
import org.openflexo.foundation.wkf.ws.InPort;
import org.openflexo.foundation.wkf.ws.MessageBindings;
import org.openflexo.foundation.wkf.ws.OutPort;
import org.openflexo.foundation.wkf.ws.PortMapRegistery;

/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class WKFValidationModel extends ValidationModel
{

    public WKFValidationModel(FlexoProject project)
    {
        this(project,project.getTargetType());
    }

    public WKFValidationModel(FlexoProject project, TargetType targetType)
    {
        super(project,targetType);

        registerRule(new FlexoWorkflow.WorkflowMustHaveARootProcess());
        registerRule(new FlexoWorkflow.BusinessDataClassMustHaveAStatusColumn());
        registerRule(new FlexoWorkflow.BusinessDataMustNotBeReadOnly());

        registerRule(new WKFObject.WKFObjectMustReferToAProcess());

        registerRule(new FlexoProcess.FlexoProcessMustHaveADefaultStatus());
        registerRule(new FlexoProcess.NonRootProcessShouldBeUsed());
        registerRule(new FlexoProcess.ProcessHierarchyIsConsistent());
        registerRule(new FlexoProcess.ProcessMustDefineBusinessDataClass());
        registerRule(new FlexoProcess.BusinessDataClassMustHaveStatusProperty());
        registerRule(new FlexoProcess.ImportedProcessShouldExistOnServer());

        registerRule(new Role.ImportedRoleShouldExistOnServer());

        registerRule(new StatusList.ProcessShouldHaveAtLeastAStatus());
        registerRule(new StatusList.ProcessShouldHaveADefaultStatus());

        //registerRule(new FlexoPetriGraph.PetriGraphMustHaveAtLeastOneBeginNode());
        registerRule(new FlexoPetriGraph.ExecutionPetriGraphMustHaveExactelyOneBeginNode());
        registerRule(new FlexoPetriGraph.ExecutionPetriGraphMustHaveExactelyOneEndNode());

        // Scrum 1.3.2: request to remove this validation rule
        //registerRule(new AbstractNode.NodeShouldHaveNonAmbigousName());

        registerRule(new AbstractNode.NodeCannotHaveMoreThanOneDefaultOutgoingTokenEdge());
        registerRule(new AbstractNode.NodeWithConditionalEdgeOrDefaultEdgeMustHaveMoreThanOneEdge());
        registerRule(new AbstractNode.NodeWithDefaultFlowMustHaveConditionOneOtherEdge());

        registerRule(new PetriGraphNode.PetriGraphNodeShouldBeAccessible());
        registerRule(new PetriGraphNode.PetriGraphNodeNameCannotBeEmpty());
        registerRule(new FlexoNode.InteractiveNodeCannotBePutInsideExecutionPetriGraph());
        registerRule(new FlexoNode.EndNodeCannotHaveMultipleEdges());


        registerRule(new AbstractActivityNode.ActivityMustHaveARole());
        registerRule(new AbstractActivityNode.ActivityCouldNotDefineOperationPetriGraphWhenNotAllowed());

        registerRule(new SubProcessNode.SubProcessNodeMustReferToAProcess());
        registerRule(new SubProcessNode.SubProcessReferenceMustBeValid());

        registerRule(new OperationNode.OperationMustHaveAWOComponent());
        registerRule(new OperationNode.OperationComponentActionsMustBeBoundToAnActionNode());
        registerRule(new OperationNode.OperationShouldBeSynchronized());
        registerRule(new OperationNode.MandatoryBindingsMustHaveAValue());
        registerRule(new OperationNode.MandatoryBooleanBindingsMustHaveAValue());
        registerRule(new OperationNode.OperationMustDefineABindingOfBusinessDataType());
        registerRule(new OperationNode.OperationMustHaveATab());
        registerRule(new ActionNode.OnlyOneActionNodeMustBeBoundToOperationComponentAction());
        registerRule(new ActionNode.DisplayActionMustHaveADisplayProcess());
        //registerRule(new ActionNode.FlexaActionMustBeBondToButton());
        registerRule(new ActionNode.FlexoActionMustSendToken());
        registerRule(new ActionNode.DisplayActionShouldHaveADisplayOperation());
        registerRule(new ActionNode.DisplayActionShouldHaveASelectedTab());
        registerRule(new ActionNode.ActionTypeMustMatchButtonType());
        registerRule(new ActionNode.ActionNodeCanBeBoundToFlexoActionOrDisplayActionButton());
        //registerRule(new ActionNode.WorkFlowBypassingActionsMustSpecifyTargetNode());

        registerRule(new OperatorNode.OperatorNodeShouldSendTokens());
        registerRule(new IFOperator.CannotHaveMoreThanTwoOutgoingEdge());
        registerRule(new IFOperator.MustHaveOnePositiveAndOneNegativeOutgoingFlow());

        registerRule(new EventNode.EndEventCannotHaveAStartingTokenFlow());
        registerRule(new EventNode.StartEventCannotHaveIncomingTokenFlow());
        registerRule(new EventNode.IntermediateEventCannotHaveMoreThanOneIncomingEdge());
        registerRule(new EventNode.IntermediateEventCannotHaveMoreThanOneOutgoingEdge());
        registerRule(new EventNode.NodeAfterEventBasedGatewayRules());
        registerRule(new ExclusiveEventBasedOperator.NodeAfterEventBasedGatewayRules());

        registerRule(new FlexoPreCondition.PreConditionMustBeAttachedToANode());
        registerRule(new FlexoPreCondition.PreConditionMustHaveIncomingEdges());
        registerRule(new FlexoPreCondition.PreConditionMustBeLinkedToABeginNode());

        registerRule(new FlexoPostCondition.PostConditionMustHaveAStartingObject());
        registerRule(new FlexoPostCondition.PostConditionMustHaveAnEndingObject());
        registerRule(new FlexoPostCondition.PostConditionStartingPointShouldBeExplicitelyDefined());
        registerRule(new FlexoPostCondition.SinglePostConditionCannotBeConditionnal());
        registerRule(new FlexoPostCondition.EdgesStartingFromEventBasedGatewayCannotBeConditionnal());
        registerRule(new FlexoPostCondition.EdgesStartingFromEventBasedGatewayCannotBeDefaultFlow());

        //registerRule(new NextPageEdge.NextPageEdgeMustBeValid());
        registerRule(new TokenEdge.TokenEdgeMustBeValid());
        registerRule(new TokenEdge.TokenEdgeShouldHaveNonNullTokenIncrement());
/*        registerRule(new OperatorInEdge.OperatorInEdgeMustBeValid());
        registerRule(new OperatorOutEdge.OperatorOutEdgeMustBeValid());
        registerRule(new OperatorInterEdge.OperatorInterEdgeMustBeValid());*/
        registerRule(new ExternalMessageEdge.ExternalMessageEdgeMustReferToAValidPortMapRegistery());
        registerRule(new ExternalMessageInEdge.ExternalMessageInEdgeMustBeValid());
        registerRule(new ExternalMessageOutEdge.ExternalMessageOutEdgeMustBeValid());
        registerRule(new InternalMessageInEdge.InternalMessageInEdgeMustBeValid());
        registerRule(new InternalMessageOutEdge.InternalMessageOutEdgeMustBeValid());
        registerRule(new ForwardWSEdge.ForwardWSEdgeMustBeValid());
        registerRule(new BackwardWSEdge.BackwardWSEdgeEdgeMustBeValid());
        registerRule(new TransferWSEdge.TransferWSEdgeMustBeValid());
        registerRule(new MessageEdge.MessageEdgeCannotBeLinkedWithGateway());

        registerRule(new PortMapRegistery.PortMapRegisteryMustReferToServiceInterface());
        registerRule(new FlexoPortMap.PortMapMustReferToAServiceOperation());
        registerRule(new RequiredBindingValidationRule(FlexoPortMap.class, "accessedProcessInstance",
                "accessedProcessInstanceBindingDefinition"));
        registerRule(new RequiredBindingValidationRule(FlexoPortMap.class, "parentProcessInstance",
                "parentProcessInstanceBindingDefinition"));
        registerRule(new RequiredBindingValidationRule(FlexoPortMap.class, "returnedProcessInstance",
                "returnedProcessInstanceBindingDefinition"));
        registerRule(new MessageBindings.DefinedBindingsMustBeValid());
        registerRule(new MessageBindings.MandatoryBindingsMustHaveAValue());

        //registerRule(new NewPort.NewPortMustBeLinkedToABeginNode());
        //registerRule(new DeletePort.DeletePortMustBeLinkedToAEndNode());
        registerRule(new InPort.InPortMustBeLinkedToAtLeastAnActivityNode());
        registerRule(new InOutPort.InOutPortMustBeLinkedToAtLeastAnActivityNode());
        registerRule(new OutPort.OutPortMustBeLinkedToAtLeastAnActivityNode());

        registerRule(new ComponentInstance.DefinedBindingsMustBeValid());
        registerRule(new ComponentInstance.MandatoryBindingsMustHaveAValue());

        // Notify that the validation model is complete and that inheritance
        // computation could be performed
        update();
    }

    /**
     * Return a boolean indicating if validation of supplied object must be
     * notified
     *
     * @param next
     * @return a boolean
     */
    @Override
	protected boolean shouldNotifyValidation(Validable next)
    {
        return ((next instanceof FlexoWorkflow) || (next instanceof FlexoProcess));
    }

    /**
     * Overrides fixAutomaticallyIfOneFixProposal
     * @see org.openflexo.foundation.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
     */
    @Override
    public boolean fixAutomaticallyIfOneFixProposal()
    {
        return false;
}
}
