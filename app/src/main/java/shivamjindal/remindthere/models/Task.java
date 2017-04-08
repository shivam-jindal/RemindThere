package shivamjindal.remindthere.models;

/**
 * Created by shivam on 9/4/17.
 */

public class Task {

    private String taskName;
    private boolean isChecked;
    private boolean isCheckVisible;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isCheckVisible() {
        return isCheckVisible;
    }

    public void setCheckVisible(boolean checkVisible) {
        isCheckVisible = checkVisible;
    }

}
