package com.heig.atmanager.tasks;

import com.heig.atmanager.MainActivity;
import com.heig.atmanager.taskLists.TaskList;

import java.util.ArrayList;
import java.util.Date;

/**
 * Author : Stéphane Bottin
 * Date   : 11.03.2020
 * <p>
 * Task object
 */
public class Task implements Comparable<Task> {

    private static final long UNDEFINED_ID = -1;

    private long id;
    private String title;
    private String description;
    private boolean done;
    private boolean favorite;
    private boolean archived;
    private Date dueDate;
    private Date doneDate;
    private Date reminderDate;
    private TaskList tasklist;
    private ArrayList<String> tags;

    public Task(String title, String description) {
        this(UNDEFINED_ID, title, description, false, false, null, null, null);
    }

    public Task(String title, String description, Date dueDate, Date reminderDate) {
        this(UNDEFINED_ID, title, description, false, false, dueDate, null, reminderDate);
    }

    public Task(String title, String description, boolean favorite) {
        this(UNDEFINED_ID, title, description, false, favorite, null, null, null);
    }

    public Task(long id, String title, String description, boolean done, boolean favorite, Date dueDate, Date doneDate, Date reminderDate, ArrayList<String> tags) {
        this(id, title, description, done, favorite, dueDate, doneDate, reminderDate);
        this.tags = tags;
    }

    public Task(long id, String title, String description, boolean done, boolean favorite,
                Date dueDate, Date doneDate, Date reminderDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.favorite = favorite;
        this.dueDate = dueDate;
        this.doneDate = doneDate;
        this.reminderDate = reminderDate;
        this.tags = new ArrayList<>();
        this.archived = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDone(boolean status) {
        this.done = status;
    }


    public boolean isFavorite() {
        return favorite;
    }

    public boolean isDone() {
        return done;
    }

    public void setFavorite(boolean fav) {
        this.favorite = fav;
    }

    public void setTasklist(TaskList tasklist) {
        this.tasklist = tasklist;
    }

    public TaskList getTasklist() {
        return tasklist;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public void addTag(String tag) {
        // Add the tag to the task and the user
        tags.add(tag);
        MainActivity.getUser().addTag(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }


    public Date getDoneDate() {
        return doneDate;
    }

    public long getId() {
        return id;
    }

    @Override
    public int compareTo(Task task) {
        // If both are null, they are equals
        if (getDueDate() == null && task.getDueDate() == null) {
            return 0;
        } else if (getDueDate() == null) { // if this dueDate is null, it means that arg is greater
            return -1;
        } else if (task.getDueDate() == null) { // if the arg dueDate is null, it means that this is greater
            return 1;
        }
        return getDueDate().compareTo(task.getDueDate());
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setId(long id) {
        this.id = id;
    }
}
