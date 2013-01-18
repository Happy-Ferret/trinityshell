package org.trinity.shellplugin.styledwidget.view.qt.impl;

import javax.inject.Named;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.To;
import de.devsurf.injection.guice.annotations.To.Type;

@Bind
@To(value = Type.CUSTOM, customs = Object.class)
@Named("ShellRootWidgetView")
public class ShellRootWidgetView extends StyledView {

}