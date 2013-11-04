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
package org.openflexo.technologyadapter.powerpoint.view;

import java.util.List;
import java.util.Vector;

import javax.swing.JTabbedPane;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.selection.SelectionListener;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;
import org.openflexo.view.SelectionSynchronizedModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represent the module view for a ExcelWorkbook.<br>
 * 
 * @author vincent, sylvain
 * 
 */
@SuppressWarnings("serial")
public class PowerpointSlideshowView extends JTabbedPane implements SelectionSynchronizedModuleView<PowerpointSlideshow> {

	private PowerpointSlideshow slideshow;
	private FlexoPerspective declaredPerspective;

	public PowerpointSlideshowView(PowerpointSlideshow slideshow, FlexoController controller, FlexoPerspective perspective) {
		super();
		declaredPerspective = perspective;
		for (PowerpointSlide slide : slideshow.getPowerpointSlides()) {
			addTab(slide.getName(), new PowerpointSlideView(slide, controller));
		}
	}

	@Override
	public FlexoPerspective getPerspective() {
		return declaredPerspective;
	}

	@Override
	public void deleteModuleView() {
	}

	@Override
	public List<SelectionListener> getSelectionListeners() {
		Vector<SelectionListener> reply = new Vector<SelectionListener>();
		reply.add(this);
		return reply;
	}

	@Override
	public void willHide() {
	}

	@Override
	public void willShow() {
	}

	@Override
	public PowerpointSlideshow getRepresentedObject() {
		return slideshow;
	}

	@Override
	public boolean isAutoscrolled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fireObjectSelected(FlexoObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireObjectDeselected(FlexoObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireResetSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireBeginMultipleSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireEndMultipleSelection() {
		// TODO Auto-generated method stub

	}

}
