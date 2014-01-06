package org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.powerpoint.BasicPowerpointModelSlot;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;

@FIBPanel("Fib/AddPowerpointSlidePanel.fib")
public class AddPowerpointSlide extends AssignableAction<BasicPowerpointModelSlot, PowerpointSlide> {

	private static final Logger logger = Logger.getLogger(AddPowerpointSlide.class.getPackage().getName());

	private DataBinding<Integer> slideIndex;

	public AddPowerpointSlide() {
		super();
	}

	@Override
	public Type getAssignableType() {
		return PowerpointSlide.class;
	}

	@Override
	public PowerpointSlide performAction(EditionSchemeAction action) {

		PowerpointSlide result = null;

		FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot> modelSlotInstance = getModelSlotInstance(action);
		if (modelSlotInstance.getResourceData() != null) {
			try {
				SlideShow ss = modelSlotInstance.getAccessedResourceData().getSlideShow();
				Slide slide = null;
				if (ss != null) {
					slide = ss.createSlide();
					Integer slideIndex = getSlideIndex().getBindingValue(action);
					if (slideIndex != null) {
						slide.setSlideNumber(slideIndex);
					}

					// Instanciate Wrapper.
					result = modelSlotInstance.getAccessedResourceData().getConverter()
							.convertPowerpointSlideToSlide(slide, modelSlotInstance.getAccessedResourceData(), null);
					modelSlotInstance.getAccessedResourceData().addToPowerpointSlides(result);
					modelSlotInstance.getAccessedResourceData().setIsModified();
				} else {
					logger.warning("Create a sheet requires a workbook");
				}
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.warning("Model slot not correctly initialised : model is null");
			return null;
		}

		return result;
	}

	public DataBinding<Integer> getSlideIndex() {
		if (slideIndex == null) {
			slideIndex = new DataBinding<Integer>(this, Integer.class, DataBinding.BindingDefinitionType.GET);
			slideIndex.setBindingName("slideIndex");
		}
		return slideIndex;
	}

	public void setSlideIndex(DataBinding<Integer> slideIndex) {
		if (slideIndex != null) {
			slideIndex.setOwner(this);
			slideIndex.setDeclaredType(Integer.class);
			slideIndex.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			slideIndex.setBindingName("slideIndex");
		}
		this.slideIndex = slideIndex;
	}

	@Override
	public FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot> getModelSlotInstance(EditionSchemeAction action) {
		return (FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot>) super.getModelSlotInstance(action);
	}

}
