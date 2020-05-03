package com.heig.atmanager.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.heig.atmanager.Interval;
import com.heig.atmanager.MainActivity;
import com.heig.atmanager.R;
import com.heig.atmanager.User;
import com.heig.atmanager.goals.Goal;
import com.heig.atmanager.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Author : Mattei Simon
 * Date   : 09.04.2020
 */
public class StatsFragment  extends Fragment {

    private Spinner menu;
    private static final String[] items = new String[]{"Today","This Week","This Month","This Year"};

    private AnyChartView pieChartTasksView;
    private AnyChartView lineChartTasksView;
    private AnyChartView pieChartGoalsView;

    private Cartesian lineChartTasks;
    private Pie pieChartTasks;
    private Pie pieChartGoals;

    private static String bgColor;

    private User user;
    private ArrayList<Task> tasks;
    private ArrayList<Goal> goals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        //DropDown menu
        menu = v.findViewById(R.id.menuXML);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapter);
        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0 : makeCharts(Interval.DAY); break;
                    case 1 : makeCharts(Interval.WEEK); break;
                    case 2 : makeCharts(Interval.MONTH); break;
                    case 3 : makeCharts(Interval.YEAR); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //init user and stuff
        user = MainActivity.getUser();

        //TODO : get this by values.colors, not working for some reason
        bgColor = "#F1F1F1";

        initCharts(v);

        return v;
    }

    private void makeCharts(Interval interval){

        String lineChartLegend = "";

        //keep interval tasks
        switch(interval){
            case DAY:
                tasks = user.getTasksForDay(new Date());
                lineChartLegend = "Hours";
                break;
            case WEEK:
                tasks = user.getTasksForLastWeek();
                lineChartLegend = "Days";
                break;
            case MONTH:
                tasks = user.getTasksForLastMonth();
                lineChartLegend = "Days";
                break;
            case YEAR:
                tasks = user.getTasksForLastYear();
                lineChartLegend = "Months";
                break;
        }


        //keep interval goals
        goals = new ArrayList<>();
        for(Goal g : user.getGoals()){
            if(g.getInterval() == interval)
                goals.add(g);
        }

        //make charts
        makePieChartTasks(interval);
        makeLineChartTasks(interval,lineChartLegend);
        makePieChartGoals(interval);
    }

    private void makePieChartTasks(Interval interval){

        APIlib.getInstance().setActiveAnyChartView(pieChartTasksView);

        List<DataEntry> data = new ArrayList<>();
        int tasksDone = 0, tasksToDo = 0;

        for(Task t : tasks){
            if(t.isDone())
                ++tasksDone;
            else
                ++tasksToDo;
        }

        //since all tasks are not done, im adding 1 just to see colors TODO: remove 1
        data.add(new ValueDataEntry("Done", tasksDone));
        data.add(new ValueDataEntry("Todo", tasksToDo));

        pieChartTasks.data(data); //data
    }

    private void makeLineChartTasks(Interval interval,String lineChartLegend){

        APIlib.getInstance().setActiveAnyChartView(lineChartTasksView);

        lineChartTasks.animation(true);

        lineChartTasks.padding(10d, 20d, 5d, 20d);

        lineChartTasks.crosshair().enabled(true);
        lineChartTasks.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        lineChartTasks.tooltip().positionMode(TooltipPositionMode.POINT);

        lineChartTasks.title("Tasks done the past " + interval.name().toLowerCase());

        //TODO : set title and stuff accordingly to interval
        lineChartTasks.xAxis(0).title(lineChartLegend);
        lineChartTasks.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new ValueDataEntry(1,1));
        seriesData.add(new ValueDataEntry(2,2));
        seriesData.add(new ValueDataEntry(3,3));

        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = lineChartTasks.line(series1Mapping);
        series1.name("Brandy");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        lineChartTasks.legend().enabled(false);
        lineChartTasks.legend().fontSize(13d);
        lineChartTasks.legend().padding(0d, 0d, 10d, 0d);
        lineChartTasks.background().fill(bgColor);
    }

    private void makePieChartGoals(Interval interval){

        APIlib.getInstance().setActiveAnyChartView(pieChartGoalsView);

        List<DataEntry> data = new ArrayList<>();
        int goalsDone = 0, goalsToDo = 0;

        for(Goal g : goals){
            double p = g.getOverallPercentage();
            goalsDone += p * 100;
            goalsToDo += (1-p) * 100;
        }

        data.add(new ValueDataEntry("Done", goalsDone));
        data.add(new ValueDataEntry("Todo", goalsToDo));

        pieChartGoals.data(data); //data
    }

    private void initCharts(View v){

        //PieChartTasks
        pieChartTasksView = v.findViewById(R.id.pie_chart_tasks);
        APIlib.getInstance().setActiveAnyChartView(pieChartTasksView);
        pieChartTasks = AnyChart.pie();
        pieChartTasksView.setBackgroundColor(bgColor);
        pieChartTasksView.setChart(pieChartTasks);
        pieChartTasks.palette(new String[]{"#3F58FF","#FF2E2E"}); //colors
        pieChartTasks.title(Task.class.getSimpleName()+ "s"); //title
        pieChartTasks.background().fill(bgColor); //bgcolor

        //LineChartTasks
        lineChartTasksView = v.findViewById(R.id.line_chart_tasks);
        APIlib.getInstance().setActiveAnyChartView(lineChartTasksView);
        lineChartTasks = AnyChart.line();
        lineChartTasksView.setProgressBar(v.findViewById(R.id.progress_bar));
        lineChartTasksView.setBackgroundColor(bgColor);
        lineChartTasksView.setChart(lineChartTasks);

        //PieChartGoals
        pieChartGoalsView = v.findViewById(R.id.pie_chart_goals);
        APIlib.getInstance().setActiveAnyChartView(pieChartGoalsView);
        pieChartGoals = AnyChart.pie();
        pieChartGoalsView.setBackgroundColor(bgColor);
        pieChartGoalsView.setChart(pieChartGoals);
        pieChartGoals.palette(new String[]{"#80EB5A","#FF9745"}); //Colors
        pieChartGoals.title(Goal.class.getSimpleName()+ "s"); //title
        pieChartGoals.background().fill(bgColor); //bgColor
    }


}
