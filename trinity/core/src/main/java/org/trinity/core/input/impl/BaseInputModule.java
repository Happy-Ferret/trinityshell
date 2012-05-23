package org.trinity.core.input.impl;

import org.trinity.core.input.api.Button;
import org.trinity.core.input.api.ButtonFactory;
import org.trinity.core.input.api.InputModifiers;
import org.trinity.core.input.api.InputModifiersFactory;
import org.trinity.core.input.api.Key;
import org.trinity.core.input.api.KeyFactory;
import org.trinity.core.input.api.KeyboardInput;
import org.trinity.core.input.api.KeyboardInputFactory;
import org.trinity.core.input.api.Momentum;
import org.trinity.core.input.api.MouseInput;
import org.trinity.core.input.api.MouseInputFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class BaseInputModule extends AbstractModule {

	@Override
	protected void configure() {
		// start instance bindings
		bind(Momentum.class).annotatedWith(Names.named("Started"))
				.toInstance(Momentum.STARTED);
		bind(Momentum.class).annotatedWith(Names.named("Stopped"))
				.toInstance(Momentum.STOPPED);
		// end instance bindings

		// start factory bindings
		install(new FactoryModuleBuilder().implement(	Button.class,
														BaseButton.class)
				.build(ButtonFactory.class));
		install(new FactoryModuleBuilder().implement(Key.class, BaseKey.class)
				.build(KeyFactory.class));
		install(new FactoryModuleBuilder()
				.implement(InputModifiers.class, BaseInputModifiers.class)
				.build(InputModifiersFactory.class));
		install(new FactoryModuleBuilder()
				.implement(	KeyboardInput.class,
							Names.named("Started"),
							BaseKeyboardInputStarted.class)
				.implement(	KeyboardInput.class,
							Names.named("Stopped"),
							BaseKeyboardInputStopped.class)
				.build(KeyboardInputFactory.class));
		install(new FactoryModuleBuilder()
				.implement(	MouseInput.class,
							Names.named("Started"),
							BaseMouseInputStarted.class)
				.implement(	MouseInput.class,
							Names.named("Stopped"),
							BaseMouseInputStopped.class)
				.build(MouseInputFactory.class));
		// end factory bindings
	}

}