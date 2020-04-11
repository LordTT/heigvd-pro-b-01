package com.heig.atmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heig.atmanager.goals.GoalTodo;
import com.heig.atmanager.tasks.Task;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Author : Stéphane Bottin
 * Date   : 11.03.2020
 *
 * Fragment for the Home view (User's Tasks and Goals of the day)
 */
public class HomeFragment extends Fragment {
    // Greeting message
    private TextView greetingText;

    // Goal feed
    private ArrayList<GoalTodo> goals; // user data
    private RecyclerView goalsRecyclerView;

    // Task feed
    private ArrayList<Task> tasks; // user data
    private RecyclerView tasksRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        tasks = new ArrayList<>();
        goals = new ArrayList<>();

        // Greeting
        greetingText = (TextView) v.findViewById(R.id.greeting_text);
        greetingText.setText(getGreetings());

        // Task feed
        tasksRecyclerView = (RecyclerView) v.findViewById(R.id.tasks_rv);
        Utils.setupTodosFeed(v, tasksRecyclerView, tasks);

        // Goal feed
        goalsRecyclerView = (RecyclerView) v.findViewById(R.id.goals_rv);
        Utils.setupGoalTodosFeedBubbled(v, goalsRecyclerView, goals);

        return v;
    }

    /**
     * Get the welcoming sentence (dynamic with user's data)
     * @return the proper greetings
     */
    private String getGreetings() {
        Calendar calendar = Calendar.getInstance();
        String greeting = "";
        String user_info = "";
        String user = "User123";

        // Hour (0 - 23)
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        // Select proper greeting
        if(currentHour < 11) {
            greeting = "Good morning ";
        } else if (currentHour < 13) {
            greeting = "Hello ";
        } else if (currentHour < 18) {
            greeting = "Good afternoon ";
        } else {
            greeting = "Good evening ";
        }

        // Select user info sentence (total tasks/goals for the day)
        if(tasks.size() == 0 && goals.size() == 0) {
            user_info = "relax! You have nothing to do today.";
        } else if (tasks.size() != 0 && goals.size() == 0) {
            user_info = getSingleUserInfoGreeting(tasks.size()) + " for today.";
        } else if (tasks.size() == 0) { // goals != 0 always true
            user_info = getSingleUserInfoGreeting(goals.size()) + " for today.";
        } else {
            user_info = getSingleUserInfoGreeting(tasks.size()) + " and "
                    + goals.size() + " goal" + (goals.size() > 1 ? "s" : "") + " for today.";
        }

        greeting += user + ",\n" + user_info;

        return greeting;
    }

    /**
     * Get the total of a value in a sentence
     * @param value : the value to display
     * @return the formatted sentence
     */
    private String getSingleUserInfoGreeting(int value) {
        return "You " + (value < 5 ? "just " : "") + "have " +
                value + " task" + (value > 1 ? "s" : "");
    }
}
