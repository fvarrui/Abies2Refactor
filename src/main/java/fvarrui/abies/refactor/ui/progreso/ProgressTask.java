package fvarrui.abies.refactor.ui.progreso;

import javafx.concurrent.Task;

public abstract class ProgressTask extends Task<Void> {

	protected abstract Void call() throws Exception;	
	
	public void update(long done, long max) {
		updateProgress(done, max);
	}
	
}
