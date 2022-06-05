package com.example.birthdiary;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {
    private int color;
    private Context context;
    private HashSet<CalendarDay> dates;
    private int code;
    public EventDecorator(int color, Collection<CalendarDay> dates, Context context, int code){
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.context = context;
        this.code = code;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
//        Drawable icon = context.getDrawable(R.drawable.ic_baseline_check_circle_24);
//        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);
        switch (code){
            case 0:
                view.addSpan(new DotSpan(10, color));
                break;
            case 1:
                view.addSpan(new DotSpan(10, Color.WHITE));
                break;
        }

//        view.addSpan(new DotSpan(10, Color.WHITE));

    }
}
