package org.trinity.render.qt.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.trinity.foundation.display.api.DisplaySurfaceHandle;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.gui.QWidget;

public class QJDisplaySurfaceHandle implements DisplaySurfaceHandle {

	private final QWidget visual;

	QJDisplaySurfaceHandle(final QWidget visual) {
		this.visual = visual;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DisplaySurfaceHandle) {
			final DisplaySurfaceHandle otherObj = (DisplaySurfaceHandle) obj;
			return otherObj.getNativeHandle().equals(getNativeHandle());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getNativeHandle().hashCode();
	}

	@Override
	public Integer getNativeHandle() {
		final FutureTask<Integer> getHandleTask = new FutureTask<Integer>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return Integer.valueOf((int) QJDisplaySurfaceHandle.this.visual.effectiveWinId());
			}
		});
		QCoreApplication.invokeLater(getHandleTask);
		Integer handle = null;
		try {
			handle = getHandleTask.get();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handle;
	}
}