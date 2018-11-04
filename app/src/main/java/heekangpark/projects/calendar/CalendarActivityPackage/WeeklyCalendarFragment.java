package heekangpark.projects.calendar.CalendarActivityPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import heekangpark.projects.calendar.DataPackage.Date;
import heekangpark.projects.calendar.R;

public class WeeklyCalendarFragment extends BaseFragment {

    private LinearLayout[] linearLayouts;
    private TextView[] textViews;
    private LinearLayout[] containers;
    private View.OnClickListener[] onClickListeners;
    private ArrayList<ArrayList<String>> titles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragmentlayout_weeklycalendar, container, false);

        button_left = rootView.findViewById(R.id.button_weeklycalendar_left);
        button_right = rootView.findViewById(R.id.button_weeklycalendar_right);
        dateIndicator = rootView.findViewById(R.id.textview_weeklycalendar_dateindicator);

        linearLayouts = new LinearLayout[7];
        linearLayouts[0] = rootView.findViewById(R.id.linearlayout_weeklycalendar_sunday);
        linearLayouts[1] = rootView.findViewById(R.id.linearlayout_weeklycalendar_monday);
        linearLayouts[2] = rootView.findViewById(R.id.linearlayout_weeklycalendar_tuesday);
        linearLayouts[3] = rootView.findViewById(R.id.linearlayout_weeklycalendar_wednesday);
        linearLayouts[4] = rootView.findViewById(R.id.linearlayout_weeklycalendar_thursday);
        linearLayouts[5] = rootView.findViewById(R.id.linearlayout_weeklycalendar_friday);
        linearLayouts[6] = rootView.findViewById(R.id.linearlayout_weeklycalendar_saturday);

        textViews = new TextView[7];
        textViews[0] = rootView.findViewById(R.id.textview_weeklycalendar_sunday);
        textViews[1] = rootView.findViewById(R.id.textview_weeklycalendar_monday);
        textViews[2] = rootView.findViewById(R.id.textview_weeklycalendar_tuesday);
        textViews[3] = rootView.findViewById(R.id.textview_weeklycalendar_wednesday);
        textViews[4] = rootView.findViewById(R.id.textview_weeklycalendar_thursday);
        textViews[5] = rootView.findViewById(R.id.textview_weeklycalendar_friday);
        textViews[6] = rootView.findViewById(R.id.textview_weeklycalendar_saturday);

        containers = new LinearLayout[7];
        containers[0] = rootView.findViewById(R.id.linearlayout_weeklycalendar_sunday_container);
        containers[1] = rootView.findViewById(R.id.linearlayout_weeklycalendar_monday_container);
        containers[2] = rootView.findViewById(R.id.linearlayout_weeklycalendar_tuesday_container);
        containers[3] = rootView.findViewById(R.id.linearlayout_weeklycalendar_wednesday_container);
        containers[4] = rootView.findViewById(R.id.linearlayout_weeklycalendar_thursday_container);
        containers[5] = rootView.findViewById(R.id.linearlayout_weeklycalendar_friday_container);
        containers[6] = rootView.findViewById(R.id.linearlayout_weeklycalendar_saturday_container);

        onClickListeners = new View.OnClickListener[7];
        for(int i = 0; i < 7; i++)
        {
            final int i1 = i;
            onClickListeners[i] = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnClick(i1 + Calendar.SUNDAY);
                }
            };
        }

        for(int i = 0; i < 7; i++)
        {
            linearLayouts[i].bringToFront();
            linearLayouts[i].setOnClickListener(onClickListeners[i]);
        }

        titles = new ArrayList<>(7);

        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.DAY_OF_MONTH, -7);
            }
        });

        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.DAY_OF_MONTH, 7);
            }
        });

        OnDateUpdated(getSundayDate());

        return rootView;
    }

    private Date convertToSunday(Date date)
    {
        Calendar temp = Calendar.getInstance();
        temp.set(date.year, date.month - 1, date.date);
        temp.add(Calendar.DAY_OF_MONTH, -(date.getWeekday() - Calendar.SUNDAY));

        return new Date(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.DAY_OF_MONTH));
    }

    private Date getSundayDate()
    {
        return convertToSunday(((CalendarActivity)getActivity()).getDate());
    }

    @Override
    public void OnDateUpdated(Date date) {
        titles.clear();
        ((CalendarActivity)getActivity()).getWeeklySchedule(titles, getSundayDate());
        updateViews(date);
    }

    private void updateDateIndicator(Date date)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(date.year);
        builder.append(".");
        builder.append(date.month);
        builder.append(" ");

        builder.append(date.getWeekOfMonth());
        switch(date.getWeekOfMonth())
        {
            case 1:
                builder.append("st");
                break;
            case 2:
                builder.append("nd");
                break;
            case 3:
                builder.append("rd");
                break;
            default:
                builder.append("th");
                break;
        }

        builder.append(" week");

        dateIndicator.setText(builder.toString());
    }

    private void updateViews(Date date)
    {
        updateDateIndicator(date);

        Date newDate = convertToSunday(date);

        for(int i = 0; i < 7; i++)
        {
            textViews[i].setText("[" + Date.getWeekdayInStr(newDate.getWeekday()) + "] " + newDate.year + "." + newDate.month + "." + newDate.date);

            containers[i].removeAllViews();

            if(titles.get(i).size() == 0)
            {
                TextView textView = new TextView(getActivity());
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ((LinearLayout.LayoutParams) params).leftMargin = 10;
                ((LinearLayout.LayoutParams) params).bottomMargin = 5;
                textView.setLayoutParams(params);
                textView.setText("No Schedule");

                containers[i].addView(textView);
            }
            else
            {
                for(int j = 0; j < titles.get(i).size(); j++)
                {
                    TextView textView = new TextView(getActivity());
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ((LinearLayout.LayoutParams) params).leftMargin = 10;
                    ((LinearLayout.LayoutParams) params).bottomMargin = 5;
                    textView.setLayoutParams(params);
                    textView.setText(titles.get(i).get(j));

                    containers[i].addView(textView);
                }
            }

            newDate.goNext();
        }
    }

    public void OnClick(int weekday)
    {
        //int offset;

        //switch(weekday)
        //{
        //    case Calendar.SUNDAY:
        //        offset = 0;
        //        break;
        //    case Calendar.MONDAY:
        //        offset = 1;
        //        break;
        //    case Calendar.TUESDAY:
        //        offset = 2;
        //        break;
        //    case Calendar.WEDNESDAY:
        //        offset = 3;
        //        break;
        //    case Calendar.THURSDAY:
        //        offset = 4;
        //        break;
        //    case Calendar.FRIDAY:
        //        offset = 5;
        //        break;
        //    case Calendar.SATURDAY:
        //        offset = 6;
        //        break;
        //    default:
        //        offset = 0;
        //}

        ((CalendarActivity)getActivity()).OnWeeklyItemClicked(weekday);

    }
}
