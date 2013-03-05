package org.trinity.shellplugin.wm.impl.view;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class Module extends AbstractModule {

	@Override
	protected void configure() {
		bind(Object.class).annotatedWith(Names.named("RootView")).toProvider(ShellRootViewProvider.class);
	}
}