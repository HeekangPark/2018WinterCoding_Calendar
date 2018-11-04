package heekangpark.projects.calendar.CalendarActivityPackage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import heekangpark.projects.calendar.AddScheduleActivityPackage.AddScheduleActivity;
import heekangpark.projects.calendar.DataPackage.Date;
import heekangpark.projects.calendar.DataPackage.Schedule;
import heekangpark.projects.calendar.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DailyCalendarFragment extends BaseFragment {

    private Button button_addNewSchedule;

    private ArrayList<Schedule> schedules;
    private ListView listView;
    private ListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragmentlayout_dailycalendar, container, false);

        dateIndicator = rootView.findViewById(R.id.textview_dailycalendar_dateindicator);
        button_left = rootView.findViewById(R.id.button_dailycalendar_left);
        button_right = rootView.findViewById(R.id.button_dailycalendar_right);
        button_addNewSchedule = rootView.findViewById(R.id.button_dailycalendar_addnewschedule);

        schedules = new ArrayList<>();

        listView = rootView.findViewById(R.id.listview_dailycalendar);
        adapter = new ListAdapter(getActivity(), schedules);
        listView.setAdapter(adapter);

        OnDateUpdated(((CalendarActivity)getActivity()).getDate());

        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.DAY_OF_MONTH, -1);
            }
        });

        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.DAY_OF_MONTH, 1);
            }
        });

        button_addNewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddScheduleActivity.class);
                intent.putExtra("year", ((CalendarActivity)getActivity()).getDate().year);
                intent.putExtra("month", ((CalendarActivity)getActivity()).getDate().month);
                intent.putExtra("date", ((CalendarActivity)getActivity()).getDate().date);

                startActivityForResult(intent, 3000);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                dialogBuilder.setTitle("Deletion Confirm");
                dialogBuilder.setMessage("Do you really want to delete the selected schedule?");

                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        Schedule cur_item = (Schedule)(adapterView.getItemAtPosition(i));
                        Date date = ((CalendarActivity)getActivity()).getDate();
                        ((CalendarActivity)getActivity()).deleteItem(date.year, date.month, date.date, cur_item.id);
                        Toast.makeText(getActivity(), "Successfully delete the selected item", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        updateScheduleList();
                    }
                });

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Deletion Canceled", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });

                dialogBuilder.show();

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void OnDateUpdated(Date date) {
        dateIndicator.setText(getStrForDateIndicator(date));
        updateScheduleList();
    }

    private void updateScheduleList()
    {
        schedules.clear();
        ((CalendarActivity)getActivity()).getDailySchedule(schedules);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3000)
        {
            if(resultCode == RESULT_OK)
            {
                String title = data.getStringExtra("title");
                String content = data.getStringExtra("content");
                int result = ((CalendarActivity)getActivity()).addNewSchedule(title, content);

                if(result == RESULT_OK)
                {
                    Toast.makeText(getActivity(), "Successfully added new schedule", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Failed to add new schedule", Toast.LENGTH_SHORT).show();
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }

            updateScheduleList();
        }
    }

    private String getStrForDateIndicator(Date date)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(date.year);
        stringBuilder.append(".");
        stringBuilder.append(date.month);
        stringBuilder.append(".");
        stringBuilder.append(date.date);
        stringBuilder.append(" (");
        stringBuilder.append(Date.getWeekdayInStr(date.getWeekday()));
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private class ListAdapter extends BaseAdapter
    {
        private final ArrayList<Schedule> scheduleList;
        private final LayoutInflater inflater;

        public ListAdapter(Context context, ArrayList<Schedule> scheduleList)
        {
            this.scheduleList = scheduleList;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return scheduleList.size();
        }

        @Override
        public Object getItem(int i) {
            return scheduleList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;

            if(view == null)
            {
                view = inflater.inflate(R.layout.item_listview_dailycalendar_schedule, viewGroup, false);
                holder = new ViewHolder();

                holder.listView_item_title = view.findViewById(R.id.textview_item_listview_dailycalendar_schedule_title);
                holder.listView_item_content = view.findViewById(R.id.textview_item_listview_dailycalendar_schedule_content);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)view.getTag();
            }

            Schedule cur_schedule = (Schedule)getItem(i);

            holder.listView_item_title.setText(cur_schedule.title);
            holder.listView_item_content.setText(cur_schedule.content);

            return view;
        }
    }

    private class ViewHolder
    {
        public TextView listView_item_title;
        public TextView listView_item_content;
    }
}
