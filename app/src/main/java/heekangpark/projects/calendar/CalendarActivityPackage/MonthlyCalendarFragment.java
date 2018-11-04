package heekangpark.projects.calendar.CalendarActivityPackage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import heekangpark.projects.calendar.DataPackage.Date;
import heekangpark.projects.calendar.R;

public class MonthlyCalendarFragment extends BaseFragment {
    private ArrayList<Integer> eventNums;
    private ArrayList<GridCell> gridCellList;
    private GridAdapter adapter_dates;
    private GridView gridView_dates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragmentlayout_monthlycalendar, container, false);

        dateIndicator = rootView.findViewById(R.id.textview_monthlycalendar);
        button_left = rootView.findViewById(R.id.button_monthlycalendar_left);
        button_right = rootView.findViewById(R.id.button_monthlycalendar_right);
        gridView_dates = rootView.findViewById(R.id.gridview_monthlycalendar_dates);

        gridCellList = new ArrayList<>();
        eventNums = new ArrayList<>();

        adapter_dates = new GridAdapter(getActivity(), gridCellList);

        gridView_dates.setAdapter(adapter_dates);

        OnDateUpdated(((CalendarActivity)getActivity()).getDate());

        button_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.MONTH, -1);
            }
        });

        button_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CalendarActivity)getActivity()).changeDate(Calendar.MONTH, 1);
            }
        });


        gridView_dates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ((CalendarActivity)getActivity()).OnMonthlyGridCellClicked(((GridCell)adapterView.getItemAtPosition(i)).year, ((GridCell)adapterView.getItemAtPosition(i)).month, ((GridCell)adapterView.getItemAtPosition(i)).date);
            }
        });

        return rootView;
    }

    @Override
    public void OnDateUpdated(Date date)
    {
        dateIndicator.setText(date.year + "." + date.month);
        updateCalendar(date);
    }

    private void updateCalendar(Date date)
    {
        gridCellList.clear();
        eventNums.clear();

        int blankNum = date.getBlankDays();

        for(int i = 0; i < blankNum; i++)
        {
            gridCellList.add(null);
        }

        //마지막 날짜 찾기
        int lastDate = date.getDaysInMonth();

        //eventNum 받기
        ((CalendarActivity)getActivity()).getMonthlyEventNum(eventNums, date.year, date.month);

        //값 추가하기
        for(int i = 1; i <= lastDate; i++)
        {
            int eventNum = eventNums.get(i);
            gridCellList.add(new GridCell(date.year, date.month, i, (i + blankNum - 1) % 7 + 1, eventNum));
        }

        adapter_dates.notifyDataSetChanged();
    }

    private class GridAdapter extends BaseAdapter
    {
        private final ArrayList<GridCell> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, ArrayList<GridCell> list)
        {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
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
                view = inflater.inflate(R.layout.item_gridview_montlycalendar_dates, viewGroup, false);
                holder = new ViewHolder();

                holder.gridView_item_date = view.findViewById(R.id.textview_item_gridview_montlycalendar_dates_date);
                holder.gridView_item_eventNum = view.findViewById(R.id.textview_item_gridview_montlycalendar_dates_eventNum);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)view.getTag();
            }

            GridCell cur_cell = (GridCell)getItem(i);

            if(cur_cell == null)
            {
                holder.gridView_item_date.setText("");
                holder.gridView_item_eventNum.setText("");
                return view;
            }

            int eventNum = cur_cell.eventNum;
            holder.gridView_item_date.setText(cur_cell.getDateStr());

            StringBuilder stringBuilder = new StringBuilder(eventNum);
            for(int x = 0; x < eventNum; x++)
            {
                stringBuilder.append('●');
            }
            holder.gridView_item_eventNum.setText(stringBuilder.toString());

            holder.gridView_item_date.setBackgroundColor(Color.TRANSPARENT);

            //토요일 색 변경
            if(cur_cell.weekday == Calendar.SATURDAY)
            {
                holder.gridView_item_date.setTextColor(Color.BLUE);
            }
            //일요일 색 변경
            if(cur_cell.weekday == Calendar.SUNDAY)
            {
                holder.gridView_item_date.setTextColor(Color.RED);
            }

            //오늘 날짜 배경색 변경
            Calendar today = Calendar.getInstance();
            if(cur_cell.date == today.get(Calendar.DAY_OF_MONTH) && cur_cell.month == today.get(Calendar.MONTH) + 1 && cur_cell.year == today.get(Calendar.YEAR)) {
                holder.gridView_item_date.setBackgroundColor(Color.YELLOW);
            }

            return view;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            if(getItem(position) == null)
            {
                return false;
            }

            return true;
        }
    }

    private class ViewHolder
    {
        public TextView gridView_item_date;
        public TextView gridView_item_eventNum;
    }

    private class GridCell
    {
        public int year;
        public int month;
        public int date;
        public int weekday;
        public int eventNum;

        public GridCell(Calendar date, int eventNum)
        {
            this.year = date.get(Calendar.YEAR);
            this.month = date.get(Calendar.MONTH) + 1;
            this.date = date.get(Calendar.DAY_OF_MONTH);
            this.weekday = date.get(Calendar.DAY_OF_WEEK);
            this.eventNum = eventNum;
        }

        public GridCell(int year, int month, int date, int weekday, int eventNum)
        {
            this.year = year;
            this.month = month;
            this.date = date;
            this.weekday = weekday;
            this.eventNum = eventNum;
        }

        public String getYearStr()
        {
            return Integer.toString(year);
        }

        public String getMonthStr()
        {
            return Integer.toString(month);
        }

        public String getDateStr()
        {
            return Integer.toString(date);
        }
    }
}
