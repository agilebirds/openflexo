package org.openflexo.fge.control.tools;

import java.util.Observable;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.notifications.ScaleChanged;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represents a widget allowing to adjust scale of viewer
 * 
 * @author sylvain
 * 
 * @param <C>
 * @param <F>
 * @param <ME>
 */
public abstract class DianaScaleSelector<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	public DianaScaleSelector(AbstractDianaEditor<?, F, ?> editor) {
		super();
		attachToEditor(editor);
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor) {
		super.attachToEditor(editor);
		handleScaleChanged();
	}

	/**
	 * Return the technology-specific component representing the selector
	 * 
	 * @return
	 */
	public abstract C getComponent();

	@Override
	public void update(Observable o, Object notification) {
		if (notification instanceof ScaleChanged) {
			handleScaleChanged();
		}
	}

	public abstract void handleScaleChanged();

}