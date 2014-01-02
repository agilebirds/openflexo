package org.openflexo.components.validation;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabel.Align;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal.ParameterDefinition;
import org.openflexo.foundation.validation.ParameteredFixProposal.StringParameter;

/**
 * Represents a FIBComponent built at run-time with a {@link ParameteredFixProposal}, and allowing to edit the parameters of the proposal
 * 
 * @author sylvain
 * 
 */
public class AskParametersComponent extends FIBPanel {

	private final ParameteredFixProposal<?, ?> fixProposal;

	public AskParametersComponent(ParameteredFixProposal<?, ?> fixProposal) {
		this.fixProposal = fixProposal;

		setLayout(Layout.twocols);
		setDataClass(ParameteredFixProposal.class);

		FIBLabel title = new FIBLabel(fixProposal.getLocalizedMessage());
		title.setAlign(Align.center);

		addToSubComponents(title, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));

		for (ParameterDefinition<?> p : fixProposal.getParameters()) {
			if (p instanceof StringParameter) {
				FIBLabel label = new FIBLabel(p.getLabel());
				FIBTextField tf = new FIBTextField();
				tf.setData(new DataBinding<String>("data.getStringParameter(\"" + p.getName() + "\").value", tf, String.class,
						BindingDefinitionType.GET_SET));
				// TODO: not finished, and should be tested
				addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
				addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			}
		}

		FIBPanel controlPanel = new FIBPanel();
		controlPanel.setLayout(Layout.buttons);

		FIBButton validateButton = new FIBButton();
		validateButton.setLabel("validate");
		validateButton.setAction(new DataBinding<Object>("controller.validateAndDispose()", validateButton, Void.TYPE,
				BindingDefinitionType.EXECUTE));

		FIBButton cancelButton = new FIBButton();
		cancelButton.setLabel("cancel");
		cancelButton.setAction(new DataBinding<Object>("controller.cancelAndDispose()", validateButton, Void.TYPE,
				BindingDefinitionType.EXECUTE));

		controlPanel.addToSubComponents(validateButton);
		controlPanel.addToSubComponents(cancelButton);

		addToSubComponents(controlPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false));

	}

	public ParameteredFixProposal<?, ?> getFixProposal() {
		return fixProposal;
	}

}
