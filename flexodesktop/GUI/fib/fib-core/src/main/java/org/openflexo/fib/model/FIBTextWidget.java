package org.openflexo.fib.model;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(FIBTextWidget.FIBTextWidgetImpl.class)
public abstract interface FIBTextWidget extends FIBWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = String.class)
	public static final String TEXT_KEY = "text";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String EDITABLE_KEY = "editable";

	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isValidateOnReturn();

	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	@Getter(value = TEXT_KEY)
	@XMLAttribute
	public String getText();

	@Setter(TEXT_KEY)
	public void setText(String text);

	@Getter(value = EDITABLE_KEY)
	@XMLAttribute
	public DataBinding<Boolean> getEditable();

	@Setter(EDITABLE_KEY)
	public void setEditable(DataBinding<Boolean> editable);

	public static abstract class FIBTextWidgetImpl extends FIBWidgetImpl implements FIBTextWidget {

		@Deprecated
		public static BindingDefinition EDITABLE = new BindingDefinition("editable", Boolean.class, DataBinding.BindingDefinitionType.GET,
				false);

		private boolean validateOnReturn = false;
		private String text = null;
		private Integer columns = null;
		private DataBinding<Boolean> editable;

		@Override
		public DataBinding<Boolean> getEditable() {
			if (editable == null) {
				editable = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			}
			return editable;
		}

		@Override
		public void setEditable(DataBinding<Boolean> editable) {
			if (editable != null) {
				editable.setOwner(this);
				editable.setDeclaredType(Boolean.class);
				editable.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.editable = editable;
		}

		@Override
		public Type getDefaultDataClass() {
			return String.class;
		}

		/**
		 * @return the columns
		 */
		@Override
		public Integer getColumns() {
			return columns;
		}

		/**
		 * @param columns
		 *            the columns to set
		 */
		@Override
		public void setColumns(Integer columns) {
			this.columns = columns;
		}

		/**
		 * @return the text
		 */
		@Override
		public String getText() {
			return text;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		@Override
		public void setText(String text) {
			this.text = text;
		}

		/**
		 * @return the validateOnReturn
		 */
		@Override
		public boolean isValidateOnReturn() {
			return validateOnReturn;
		}

		/**
		 * @param validateOnReturn
		 *            the validateOnReturn to set
		 */
		@Override
		public void setValidateOnReturn(boolean validateOnReturn) {
			this.validateOnReturn = validateOnReturn;
		}

		/**
		 * Return a list of all bindings declared in the context of this component
		 * 
		 * @return
		 */
		@Override
		public List<DataBinding<?>> getDeclaredBindings() {
			List<DataBinding<?>> returned = super.getDeclaredBindings();
			returned.add(getEditable());
			return returned;
		}

	}
}
