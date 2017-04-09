package shivamjindal.remindthere.models;

import java.util.List;

/**
 * Created by shivam on 9/4/17.
 */

public class TasksContainer {

    private String title;
    private List<Task> tasks;
    private int categoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
