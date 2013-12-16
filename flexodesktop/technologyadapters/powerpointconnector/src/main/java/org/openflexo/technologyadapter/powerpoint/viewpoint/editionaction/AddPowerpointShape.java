package org.openflexo.technologyadapter.powerpoint.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.ShapeTypes;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.powerpoint.BasicPowerpointModelSlot;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;

@FIBPanel("Fib/AddPowerpointShapePanel.fib")
public class AddPowerpointShape extends AssignableAction<BasicPowerpointModelSlot, PowerpointShape> {

	private static final Logger logger = Logger.getLogger(AddPowerpointShape.class.getPackage().getName());

	private DataBinding<List<PowerpointShape>> powerpointShapes;

	private DataBinding<PowerpointSlide> powerpointSlide;

	public AddPowerpointShape(VirtualModelBuilder builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Type getAssignableType() {
		return PowerpointShape.class;
	}

	@Override
	public PowerpointShape performAction(EditionSchemeAction action) {
		PowerpointShape powerpointShape = null;

		FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot> modelSlotInstance = getModelSlotInstance(action);
		if (modelSlotInstance.getResourceData() != null) {

			try {
				PowerpointSlide powerpointSlide = getPowerpointSlide().getBindingValue(action);
				if (powerpointSlide != null) {

					AutoShape shape = new AutoShape(ShapeTypes.Chevron);

					powerpointShape = modelSlotInstance.getResourceData().getConverter()
							.convertPowerpointShapeToShape(shape, powerpointSlide, null);
					powerpointSlide.getSlide().addShape(shape);
					modelSlotInstance.getResourceData().setIsModified();
				} else {
					logger.warning("Create a row requires a sheet");
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

		return powerpointShape;
	}

	public DataBinding<PowerpointSlide> getPowerpointSlide() {
		if (powerpointSlide == null) {
			powerpointSlide = new DataBinding<PowerpointSlide>(this, PowerpointSlide.class, DataBinding.BindingDefinitionType.GET);
			powerpointSlide.setBindingName("powerpointSlide");
		}
		return powerpointSlide;
	}

	public void setPowerpointSlide(DataBinding<PowerpointSlide> powerpointSlide) {
		if (powerpointSlide != null) {
			powerpointSlide.setOwner(this);
			powerpointSlide.setDeclaredType(PowerpointSlide.class);
			powerpointSlide.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			powerpointSlide.setBindingName("powerpointSlide");
		}
		this.powerpointSlide = powerpointSlide;
	}

	@Override
	public FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot> getModelSlotInstance(EditionSchemeAction action) {
		return (FreeModelSlotInstance<PowerpointSlideshow, BasicPowerpointModelSlot>) super.getModelSlotInstance(action);
	}

}
