package fvarrui.abies.refactor.services.items;

public abstract class ProgressListener {
	
	public boolean cancelled = false;
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void cancel() {
		cancelled = true;
	}
	
	public abstract void update(long workDone, long max);
	
}
