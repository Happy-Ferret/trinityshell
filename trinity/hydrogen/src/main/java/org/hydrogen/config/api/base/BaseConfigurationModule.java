package org.hydrogen.config.api.base;

import org.hydrogen.config.api.DisplayConfiguration;

import com.google.inject.AbstractModule;

public class BaseConfigurationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DisplayConfiguration.class).to(BaseDisplayConfiguration.class);
	}

}