package org.openflexo.foundation.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.XMLDeserializer;
import org.openflexo.model.xml.InvalidXMLDataException;
import org.openflexo.toolbox.IProgress;

public class UserResourceCenter implements FlexoResourceCenter {

	private ModelFactory modelFactory;
	private File userResourceCenterStorageFile;
	private Storage storage;

	public UserResourceCenter(File userResourceCenterStorageFile) {
		this.userResourceCenterStorageFile = userResourceCenterStorageFile;
		this.modelFactory = new ModelFactory();
		try {
			modelFactory.importClass(Storage.class);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
	}

	public File getUserResourceCenterStorageFile() {
		return userResourceCenterStorageFile;
	}

	public void setUserResourceCenterStorageFile(File userResourceCenterStorageFile) {
		this.userResourceCenterStorageFile = userResourceCenterStorageFile;
	}

	@ModelEntity
	@XMLElement(xmlTag = "UserResourceCenter")
	@Imports({ @Import(FlexoFileResource.class) })
	public static interface Storage {
		public static final String RESOURCES = "resources";

		@Getter(value = RESOURCES, cardinality = Cardinality.LIST)
		@XMLElement
		public List<FlexoResource<?>> getResources();

		@Setter(RESOURCES)
		public void setResources(List<FlexoResource<?>> resources);

		@Adder(RESOURCES)
		public void addToResources(FlexoResource<?> resource);

		@Remover(RESOURCES)
		public void removeFromResources(FlexoResource<?> resource);
	}

	@ModelEntity
	@XMLElement
	public static interface FlexoFileResource<RD extends ResourceData<RD>> extends FlexoResource<RD> {
		public static final String FILE = "file";

		@Getter(FILE)
		@XmlAttribute
		public File getFile();

		@Setter(FILE)
		public void setFile(File file);
	}

	@Override
	public List<FlexoResource<?>> getAllResources(IProgress progress) {
		return Collections.unmodifiableList(storage.getResources());
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, String version, Class<T> type, IProgress progress) {
		List<FlexoResource<?>> resources = storage.getResources();
		if (progress != null) {
			progress.resetSecondaryProgress(resources.size());
		}
		for (FlexoResource<?> resource : resources) {
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("retrieving_resource"));
			}
			if (resource.getURI().equals(uri)) {
				if (resource.getVersion() == null && version == null || resource.getVersion() != null
						&& resource.getVersion().equals(version) && type.isAssignableFrom(resource.getResourceDataClass())) {
					return (FlexoResource<T>) resource;
				}
			}
		}
		return null;
	}

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		List<FlexoResource<T>> returned = new ArrayList<FlexoResource<T>>();
		List<FlexoResource<?>> resources = storage.getResources();
		if (progress != null) {
			progress.resetSecondaryProgress(resources.size());
		}
		for (FlexoResource<?> resource : resources) {
			if (progress != null) {
				progress.setSecondaryProgress(FlexoLocalization.localizedForKey("retrieving_resources"));
			}
			if (resource.getURI().equals(uri)) {
				if (type.isAssignableFrom(resource.getResourceDataClass())) {
					returned.add((FlexoResource<T>) resource);
				}
			}
		}
		return returned;

	}

	@Override
	public void publishResource(FlexoResource<?> resource, String newVersion, IProgress progress) throws Exception {
		retrieveResource(resource.getURI(), newVersion, resource.getResourceDataClass(), progress);
	}

	@Override
	public void update() throws IOException {
		FileInputStream fis = new FileInputStream(userResourceCenterStorageFile);
		try {
			try {
				storage = (Storage) new XMLDeserializer(modelFactory).deserializeDocument(fis);
			} catch (JDOMException e) {
				e.printStackTrace();
				throw new IOException("Parsing XML data failed: " + e.getMessage(), e);
			} catch (InvalidXMLDataException e) {
				e.printStackTrace();
				throw new IOException("Invalid XML data: " + e.getMessage(), e);
			} catch (ModelDefinitionException e) {
				// This should not happen
				e.printStackTrace();
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	@Override
	public OntologyLibrary retrieveBaseOntologyLibrary() {
		return null;
	}

	@Override
	public ViewPointLibrary retrieveViewPointLibrary() {
		return null;
	}

	@Override
	public ViewPoint getOntologyCalc(String ontologyCalcUri) {
		return null;
	}

	@Override
	public File getNewCalcSandboxDirectory() {
		return new File(userResourceCenterStorageFile.getParentFile(), "ViewPoints");
	}

}
