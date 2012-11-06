package org.openflexo.fge;

import javax.inject.Inject;

import org.openflexo.model.factory.PamelaInjectionModule;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Names;

public class FGEPamelaInjectionModule extends PamelaInjectionModule {

	public FGEPamelaInjectionModule(FGEModelFactory modelFactory) {
		super(modelFactory);
	}

	@Override
	protected void configure() {
		super.configure();
		bind(ShadowStyle.class).annotatedWith(Names.named(ShadowStyle.NONE_CONFIGURATION)).toProvider(new Provider<ShadowStyle>() {
			@Inject
			private Injector injector;

			@Override
			public ShadowStyle get() {
				ShadowStyle shadow = injector.getInstance(ShadowStyle.class);
				shadow.setDrawShadow(false);
				shadow.setShadowDepth(0);
				return shadow;
			}
		});
		bind(ShadowStyle.class).annotatedWith(Names.named(ShadowStyle.DEFAULT_CONFIGURATION)).toProvider(new Provider<ShadowStyle>() {
			@Inject
			private Injector injector;

			@Override
			public ShadowStyle get() {
				ShadowStyle shadow = injector.getInstance(ShadowStyle.class);
				shadow.setDrawShadow(true);
				shadow.setShadowDarkness(FGEConstants.DEFAULT_SHADOW_DARKNESS);
				shadow.setShadowDepth(FGEConstants.DEFAULT_SHADOW_DEEP);
				shadow.setShadowBlur(FGEConstants.DEFAULT_SHADOW_BLUR);
				return shadow;
			}
		});
	}

}
