package net.dudss.dcomponents.workers;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public abstract class BetterSwingWorker {
    private final SwingWorker<Void, Void> worker = new SimpleSwingWorker();

    public void execute() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                before();
            }
        });

        worker.execute();
    }

    protected void before() {
        //Nothing by default
    }

    protected abstract void doInBackground() throws Exception;

    protected abstract void done();

    private class SimpleSwingWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            BetterSwingWorker.this.doInBackground();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (final ExecutionException ex) {
                throw new RuntimeException(ex.getCause());
            }

            BetterSwingWorker.this.done();
        }
    }

    public void run() {
        worker.run();
    }

    public int getProgress() {
        return worker.getProgress();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return worker.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return worker.isCancelled();
    }

    public boolean isDone() {
        return worker.isDone();
    }

    public Void get() throws InterruptedException, ExecutionException {
        return worker.get();
    }

    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return worker.get(timeout, unit);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        worker.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        worker.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        worker.firePropertyChange(propertyName, oldValue, newValue);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return worker.getPropertyChangeSupport();
    }

    public SwingWorker.StateValue getState() {
        return worker.getState();
    }
}
