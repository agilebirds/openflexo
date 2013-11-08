package org.openflexo.technologyadapter.powerpoint.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hslf.usermodel.SlideShow;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;
import org.openflexo.technologyadapter.powerpoint.model.io.BasicPowerpointModelConverter;
import org.openflexo.technologyadapter.powerpoint.rm.PowerpointSlideshowResource;

public class PowerpointSlideshow extends PowerpointObject implements ResourceData<PowerpointSlideshow> {

	private SlideShow slideshow;
	private PowerpointSlideshowResource resource;
	private List<PowerpointSlide> powerpointSlides;
	private BasicPowerpointModelConverter converter;
	
	public SlideShow getSlideShow() {
		return slideshow;
	}

	public PowerpointSlideshow(SlideShow slideshow, PowerpointTechnologyAdapter adapter) {
		super(adapter);
		this.slideshow = slideshow;
		powerpointSlides = new ArrayList<PowerpointSlide>();
	}

	public PowerpointSlideshow(PowerpointTechnologyAdapter adapter) {
		super(adapter);
		powerpointSlides = new ArrayList<PowerpointSlide>();
	}
	
	public PowerpointSlideshow(SlideShow slideshow, BasicPowerpointModelConverter converter, PowerpointTechnologyAdapter adapter) {
		super(adapter);
		this.slideshow = slideshow;
		this.converter = converter;
		powerpointSlides = new ArrayList<PowerpointSlide>();
	}

	public BasicPowerpointModelConverter getConverter() {
		return converter;
	}

	@Override
	public FlexoResource<PowerpointSlideshow> getResource() {
		return resource;
	}

	@Override
	public void setResource(FlexoResource<PowerpointSlideshow> resource) {
		this.resource = (PowerpointSlideshowResource) resource;
	}

	@Override
	public String getName() {
		return getResource().getName();
	}

	public List<PowerpointSlide> getPowerpointSlides() {
		return powerpointSlides;
	}

	public void setPowerpointSlides(List<PowerpointSlide> powerpointSlides) {
		this.powerpointSlides = powerpointSlides;
	}

	public void addToPowerpointSlides(PowerpointSlide newPowerpointSlide) {
		this.powerpointSlides.add(newPowerpointSlide);
	}

	public void removeFromPowerpointSlides(PowerpointSlide deletedPowerpointSlide) {
		this.powerpointSlides.remove(deletedPowerpointSlide);
	}


	@Override
	public String getUri() {
		String uri = "PowerpointSlideshow="+getResource().getName();
		return uri;
	}
	
}
