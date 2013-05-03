package org.trinity.shellplugin.wm.x11.impl.view;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.annotations.GuiceModule;

@GuiceModule
public class Module extends AbstractModule {

	@Override
	protected void configure() {
		bind(Object.class).annotatedWith(Names.named("RootView")).toProvider(RootViewProvider.class);
	}
}