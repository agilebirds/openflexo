package org.openflexo.foundation.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.DeserializationPolicy;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.SerializationPolicy;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

public class UserResourceCenter /*extends FileSystemBasedResourceCenter*/implements FlexoResourceCenter {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(UserResourceCenter.class
			.getPackage().getName());

	private ModelFactory modelFactory;
	private Storage storage;
	private File userResourceCenterStorageFile;

	public UserResourceCenter(File userResourceCenterStorageFile) {
		// super(userResourceCenterStorageFile.getParentFile());
		super();
		this.userResourceCenterStorageFile = userResourceCenterStorageFile;
		try {
			this.modelFactory = new ModelFactory(Storage.class);
		} catch (ModelDefinitionException e1) {
			// Hum this sucks...
			e1.printStackTrace();
		}
		if (userResourceCenterStorageFile.exists() && userResourceCenterStorageFile.isFile() && userResourceCenterStorageFile.canWrite()) {
			try {
				update();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (!userResourceCenterStorageFile.canRead() || !userResourceCenterStorageFile.canWrite()) {
			System.err.println("Permission denied for file " + userResourceCenterStorageFile.getAbsolutePath());
		}
		if (storage == null) {
			storage = modelFactory.newInstance(Storage.class);
		}
		modelFactory.getStringEncoder().addConverter(FLEXO_VERSION_CONVERTER);
	}

	public File getUserResourceCenterStorageFile() {
		return userResourceCenterStorageFile;
	}

	@Override
	public String getName() {
		return userResourceCenterStorageFile.getParentFile().getAbsolutePath();
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

	@Override
	public List<FlexoResource<?>> getAllResources(IProgress progress) {
		return Collections.unmodifiableList(storage.getResources());
	}

	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, FlexoVersion version, Class<T> type, IProgress progress) {
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
						&& resource.getVersion().equals(version)
						&& (type == null || type.isAssignableFrom(resource.getResourceDataClass()))) {
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
	public void publishResource(FlexoResource<?> resource, FlexoVersion newVersion, IProgress progress) throws Exception {
		FlexoResource<?> oldResource = retrieveResource(resource.getURI(), newVersion, resource.getResourceDataClass(), progress);
		if (oldResource != null) {
			storage.removeFromResources(oldResource);
		}
		if (resource instanceof FlexoFileResource) {
			File file = ((FlexoFileResource<?>) resource).getFile();
			if (file != null) {
				for (FlexoResource<?> r : new ArrayList<FlexoResource<?>>(storage.getResources())) {
					if (r instanceof FlexoFileResource) {
						if (((FlexoFileResource<?>) r).getFile().equals(file)) {
							storage.removeFromResources(r);
						}
					}
				}
			}
		}
		storage.addToResources(resource);
		saveStorage();
	}

	private void saveStorage() throws IOException {
		if (!getUserResourceCenterStorageFile().exists()) {
			getUserResourceCenterStorageFile().getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(getUserResourceCenterStorageFile());
		try {
			modelFactory.serialize(storage, fos, SerializationPolicy.EXTENSIVE);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	@Override
	public void update() throws IOException {
		if (userResourceCenterStorageFile.exists()) {
			FileInputStream fis = new FileInputStream(userResourceCenterStorageFile);
			try {
				try {
					storage = (Storage) modelFactory.deserialize(fis, DeserializationPolicy.EXTENSIVE);
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Loaded " + storage.getResources().size() + " resources from user resource cente file");
					}
				} catch (JDOMException e) {
					e.printStackTrace();
					throw new IOException("Parsing XML data failed: " + e.getMessage(), e);
				} catch (InvalidDataException e) {
					e.printStackTrace();
					throw new IOException("Invalid XML data: " + e.getMessage(), e);
				} catch (ModelDefinitionException e) {
					// This should not happen
					e.printStackTrace();
				}
			} finally {
				IOUtils.closeQuietly(fis);
			}
			checkKnownResources();
		}
	}

	private void checkKnownResources() throws IOException {
		if (storage != null) {
			boolean changed = false;
			for (FlexoResource<?> resource : storage.getResources()) {
				// TODO check resources
			}
			if (changed) {
				saveStorage();
			}
		}
	}

	@StringConverter
	public static final Converter<FlexoVersion> FLEXO_VERSION_CONVERTER = new FlexoVersionConverter();

	public static class FlexoVersionConverter extends Converter<FlexoVersion> {

		public FlexoVersionConverter() {
			super(FlexoVersion.class);
		}

		@Override
		public FlexoVersion convertFromString(String value, ModelFactory factory) {
			return new FlexoVersion(value);
		}

		@Override
		public String convertToString(FlexoVersion value) {
			return value.toString();
		}

	}

	@Override
	public void initialize(ViewPointLibrary viewPointLibrary) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(TechnologyAdapterService technologyAdapterService) {
		// TODO Auto-generated method stub

	}

	@Override
	public ViewPointRepository getViewPointRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends M>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> ModelRepository<R, M, MM, TA> getModelRepository(
			TA technologyAdapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends MM>, M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>, TA extends TechnologyAdapter<M, MM>> MetaModelRepository<R, M, MM, TA> getMetaModelRepository(
			TA technologyAdapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
