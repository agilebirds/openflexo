package org.openflexo.view;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fib.model.FIBBrowserAction;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * A built-in adapter in openflexo ui layer which allows to automatically map FlexoAction environment to FIBBrowser behaviour
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FIBBrowserActionAdapter.FIBBrowserActionAdapterImpl.class)
public interface FIBBrowserActionAdapter<T extends FlexoObject> extends FIBBrowserAction {

	public Object performAction(T selected);

	public boolean isAvailable(T selected);

	@Override
	public String getName();

	@Override
	public ActionType getActionType();

	public abstract class FIBBrowserActionAdapterImpl<T extends FlexoObject> extends FIBBrowserActionImpl implements
			FIBBrowserActionAdapter<T> {

		public static <T extends FlexoObject> FIBBrowserActionAdapter<T> makeFIBBrowserActionAdapter(FlexoActionType<?, T, ?> actionType,
				FIBBrowserView<?> browserView) {
			FIBBrowserActionAdapterImpl<T> returned = (FIBBrowserActionAdapterImpl<T>) FlexoFIBController.FLEXO_FIB_FACTORY
					.newInstance(FIBBrowserActionAdapter.class);
			returned.initWithActionType(actionType, browserView);
			return returned;
		}

		private FlexoActionType<?, T, ?> actionType;
		private FIBBrowserView<?> browserView;

		private void initWithActionType(FlexoActionType<?, T, ?> actionType, FIBBrowserView<?> browserView) {
			this.actionType = actionType;
			this.browserView = browserView;
			setMethod(new DataBinding<Object>("action.performAction(selected)"));
			setIsAvailable(new DataBinding<Boolean>("action.isAvailable(selected)"));
		}

		@Override
		public Object performAction(T selected) {
			FlexoAction action = actionType.makeNewAction(selected, null, browserView.getFIBController().getEditor());
			action.doAction();
			return action;
		}

		@Override
		public boolean isAvailable(T selected) {
			return browserView.getFIBController().getEditor().isActionVisible(actionType, selected, null)
					&& browserView.getFIBController().getEditor().isActionEnabled(actionType, selected, null);
		}

		@Override
		public String getName() {
			return actionType.getUnlocalizedName();
		}

		@Override
		public ActionType getActionType() {
			if (actionType.getActionCategory() == FlexoActionType.ADD_ACTION_TYPE) {
				return ActionType.Add;
			} else if (actionType.getActionCategory() == FlexoActionType.DELETE_ACTION_TYPE) {
				return ActionType.Delete;
			} else {
				return ActionType.Custom;
			}
		}

	}
}