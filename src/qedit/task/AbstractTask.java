package qedit.task;

import java.awt.Cursor;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import qedit.QEditApp;

/**
 *
 * @author Pantelis Sopasakis
 * @author Charalampos Chomenides
 */
public abstract class AbstractTask extends org.jdesktop.application.Task {

    /*
     * While the task is executed, the cursor on this component will appear to
     * be busy. Once the task has finished or is cancelled, the cursor becomes
     * normal again. If is null, no action is taken.
     */
    protected java.awt.Component busyComponent;
    protected String taskName;
    private volatile boolean cancelled = false;
    private volatile boolean failed = false;
    protected String exceptionMessage = null;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public AbstractTask() {
        super(qedit.QEditApp.getApplication());
    }

    public void runInBackground() {
        // Set cursor to "WAITING"
        if (busyComponent != null) {
            busyComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        // Inform the user the the task started running
        QEditApp.getView().getStatusLabel().setText("Task \"" + taskName + "\" is running!");
        ApplicationContext appC = qedit.QEditApp.getInstance().getContext();
        TaskMonitor taskMonitor = appC.getTaskMonitor();
        TaskService taskService = appC.getTaskService();
        taskService.execute(this);
        taskMonitor.setForegroundTask(this);
    }

    @Override
    protected void finished() {
        if (busyComponent != null) {
            busyComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        String outputMessage = null;
        if (cancelled) {
            outputMessage = "Task \"" + taskName + "\" was cancelled";
        } else if (failed) {
            outputMessage = "Task \"" + taskName + "\" FAILED!!!";
            if (exceptionMessage!=null){
                outputMessage += " - "+exceptionMessage;
            }
        } else {
            outputMessage = "Task \"" + taskName + "\" completed.";
        }
        QEditApp.getView().getStatusLabel().setText(outputMessage);
        super.finished();
    }

    @Override
    protected void cancelled() {
        cancelled = true;
        if (busyComponent != null) {
            busyComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        super.cancelled();
        QEditApp.getView().getStatusLabel().setText("Task \"" + taskName + "\" was cancelled!");
        super.cancelled();
    }

    @Override
    protected void failed(Throwable cause) {
        failed = true;
        if (busyComponent != null) {
            busyComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        QEditApp.getView().getStatusLabel().setText("Task \"" + taskName + "\" FAILED!");
        super.failed(cause);
    }
}
