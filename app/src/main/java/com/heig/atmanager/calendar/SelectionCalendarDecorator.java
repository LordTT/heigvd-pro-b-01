package com.heig.atmanager.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.heig.atmanager.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import androidx.core.content.ContextCompat;

/**
 * Author : Stéphane Bottin
 * Date   : 02.04.2020
 */
public class SelectionCalendarDecorator implements DayViewDecorator {

    private static final String TAG = "SelectionCalendarDecorator";

    private Context context;

    public SelectionCalendarDecorator(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Selection outline when pressing on a day
        Drawable selectionOutline = ContextCompat.getDrawable(context, R.drawable.calendar_selection);
        if(selectionOutline != null)
            view.setSelectionDrawable(selectionOutline);
    }
}
