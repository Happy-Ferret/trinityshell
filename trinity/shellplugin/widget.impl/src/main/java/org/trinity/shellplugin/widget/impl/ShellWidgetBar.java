package org.trinity.shellplugin.widget.impl;

import org.trinity.foundation.api.render.PainterFactory;
import org.trinity.foundation.api.render.binding.model.ViewReference;
import org.trinity.foundation.api.render.binding.view.View;
import org.trinity.shell.api.surface.ShellDisplayEventDispatcher;
import org.trinity.shell.api.widget.BaseShellWidget;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class ShellWidgetBar extends BaseShellWidget {

	private final View view;

	@Inject
	ShellWidgetBar(	final EventBus eventBus,
					final ShellDisplayEventDispatcher shellDisplayEventDispatcher,
					final PainterFactory painterFactory,
					final View view) {

		super(	eventBus,
				shellDisplayEventDispatcher,
				painterFactory);
		this.view = view;
	}

	@ViewReference
	public View getView() {
		return view;
	}
}
