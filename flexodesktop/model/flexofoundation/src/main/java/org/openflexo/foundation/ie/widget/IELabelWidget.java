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
package org.openflexo.foundation.ie.widget;

import java.util.Vector;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.util.TextCSSClass;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * Represents a label widget
 * 
 * @author bmangez
 */
public class IELabelWidget extends IENonEditableTextWidget {

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
     * 
     */
	public static final String LABEL_WIDGET = "label_widget";

	public IELabelWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IELabelWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
		this.setTextCSSClass(TextCSSClass.BLOC_BODY_TITLE);
	}

	@Override
	public String getDefaultInspectorName() {
		return "Label.inspector";
	}

	public boolean endsWidthSemiColon() {
		return getValue() != null && getValue().trim().endsWith(":");
	}

	@Override
	public String getDefaultValue() {
		return "Label: ";
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return EMPTY_IOBJECT_VECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Label";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return LABEL_WIDGET;
	}

	public static class LabelDontHaveDoubleDoubleDot extends ValidationRule {
		public LabelDontHaveDoubleDoubleDot() {
			super(IELabelWidget.class, "usually_labels_dont_have_double_double_dot");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final IELabelWidget label = (IELabelWidget) object;
			if (label.getValue() != null && label.getValue().trim().endsWith(":")
					&& label.getValue().indexOf(":") != label.getValue().lastIndexOf(":")) {
				ValidationWarning error = new ValidationWarning(this, object, "usually_labels_dont_have_double_double_dot");

				error.addToFixProposals(new RemoveEndingDoubleDot(label));
				return error;
			}
			return null;
		}

	}

	public static class RemoveEndingDoubleDot extends FixProposal {
		public IELabelWidget lab;

		public RemoveEndingDoubleDot(IELabelWidget label) {
			super("remove_ending_double_dot");
			lab = label;

		}

		@Override
		protected void fixAction() {
			lab.setValue(lab.getValue().trim().substring(0, lab.getValue().trim().length() - 1));
		}
	}

	public static class LabelHaveAtLeastOneDoubleDot extends ValidationRule {
		public LabelHaveAtLeastOneDoubleDot() {
			super(IELabelWidget.class, "usually_labels_ends_with_double_dot");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final IELabelWidget label = (IELabelWidget) object;
			if (label.getValue() != null && label.getValue().indexOf(":") == -1) {
				ValidationWarning error = new ValidationWarning(this, object, "usually_labels_ends_with_double_dot");

				error.addToFixProposals(new AddingDoubleDot(label));
				return error;
			}
			return null;
		}

	}

	public static class AddingDoubleDot extends FixProposal {
		public IELabelWidget lab;

		public AddingDoubleDot(IELabelWidget label) {
			super("adding_double_dot");
			lab = label;

		}

		@Override
		protected void fixAction() {
			if (lab.getValue().endsWith(" ")) {
				lab.setValue(lab.getValue().trim() + ":");
			} else {
				lab.setValue(lab.getValue() + ":");
			}
		}
	}

	public static class NoWhiteSpaceBeforeDoubleDot extends ValidationRule {
		public NoWhiteSpaceBeforeDoubleDot() {
			super(IELabelWidget.class, "usually_labels_dont_have_white_space_before_double_dot");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final IELabelWidget label = (IELabelWidget) object;
			if (label.getValue() != null && label.getValue().trim().endsWith(" :")) {
				ValidationWarning error = new ValidationWarning(this, object, "usually_labels_dont_have_white_space_before_double_dot");

				error.addToFixProposals(new RemoveSpaceBeforeDoubleDot(label));
				return error;
			}
			return null;
		}

	}

	public static class RemoveSpaceBeforeDoubleDot extends FixProposal {
		public IELabelWidget lab;

		public RemoveSpaceBeforeDoubleDot(IELabelWidget label) {
			super("remove_space_before_double_dot");
			lab = label;

		}

		@Override
		protected void fixAction() {
			String trimed = lab.getValue().trim();
			lab.setValue(trimed.substring(0, trimed.length() - 2) + ":");
		}
	}

}
