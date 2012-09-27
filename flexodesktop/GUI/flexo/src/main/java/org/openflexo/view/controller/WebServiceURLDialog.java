package org.openflexo.view.controller;

import java.io.File;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.toolbox.FileResource;

public class WebServiceURLDialog {

	public static final File FIB_FILE = new FileResource("Fib/WebServiceURLDialog.fib");

	@ModelEntity
	public interface PPMWSClientParameter {
		public static final String WS_INSTANCE = "wsInstance";
		public static final String WS_URL = "wsURL";
		public static final String WS_LOGIN = "wsLogin";
		public static final String WS_PASSWORD = "wsPassword";
		public static final String REMEMBER = "remember";

		@Getter(value = WS_INSTANCE)
		public FlexoServerInstance getWSInstance();

		@Setter(value = WS_INSTANCE)
		public void setWSInstance(FlexoServerInstance instance);

		@Getter(value = WS_URL)
		public String getWSURL();

		@Setter(value = WS_URL)
		public void setWSURL(String wsurl);

		@Getter(value = WS_LOGIN)
		public String getWSLogin();

		@Setter(value = WS_LOGIN)
		public void setWSLogin(String login);

		@Getter(value = WS_PASSWORD)
		public String getWSPassword();

		@Setter(value = WS_PASSWORD)
		public void setWSPassword(String password);

		@Getter(value = REMEMBER)
		public Boolean getRemember();

		@Setter(value = REMEMBER)
		public void setRemember(Boolean remember);

	}

	private PPMWSClientParameter clientParameter;

	public PPMWSClientParameter getClientParameter() {
		return clientParameter;
	}

	public void setClientParameter(PPMWSClientParameter clientParameter) {
		this.clientParameter = clientParameter;
	}

	public FlexoServerAddressBook getAddressBook() {
		return FlexoServerInstanceManager.getInstance().getAddressBook();
	}
}
