package heekangpark.projects.calendar.CalendarActivityPackage;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;

import heekangpark.projects.calendar.DataPackage.Date;
import heekangpark.projects.calendar.DataPackage.Schedule;
import heekangpark.projects.calendar.R;

public class CalendarActivity extends AppCompatActivity {
    public String db_name;

    private Toolbar toolbar;
    private BaseFragment monthlyFragment;
    private BaseFragment weeklyFragment;
    private BaseFragment dailyFragment;
    private TabLayout tabs;

    private Calendar date;

    private int currentFragment;

    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.layout_calendar);

        date = Calendar.getInstance();

        db_name = getIntent().getStringExtra("db_name");
        db = openOrCreateDatabase(db_name, MODE_PRIVATE, null);

        toolbar = findViewById(R.id.toolbar_calendar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        monthlyFragment = new MonthlyCalendarFragment();
        weeklyFragment = new WeeklyCalendarFragment();
        dailyFragment = new DailyCalendarFragment();

        currentFragment = 0;
        changeCurrentFragment(currentFragment);

        tabs = findViewById(R.id.tabs_calendar);
        tabs.addTab(tabs.newTab().setText("Monthly"));
        tabs.addTab(tabs.newTab().setText("Weekly"));
        tabs.addTab(tabs.newTab().setText("Daily"));

        tabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragment = tab.getPosition();
                changeCurrentFragment(currentFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeCurrentFragment(int currentFragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.linearlayout_calendar_fragmentcontainer, getCorrespondingFragment(currentFragment)).commit();
    }

    public void OnMonthlyGridCellClicked(int _year, int _month, int _date)
    {
        date.set(_year, _month - 1, _date);

        gotoDailyTab();
    }

    public void OnWeeklyItemClicked(int weekday)
    {
        date.add(Calendar.DAY_OF_MONTH, (weekday - getDate().getWeekday()));

        gotoDailyTab();
    }

    private void gotoDailyTab()
    {
        tabs.getTabAt(2).select();
        currentFragment = 2;
    }

    private BaseFragment getCorrespondingFragment(int fragment)
    {
        switch(currentFragment)
        {
            case 0:
                return monthlyFragment;
            case 1:
                return weeklyFragment;
            case 2:
                return dailyFragment;
            default:
                throw new RuntimeException("changeCurrentFragment() Error");
        }
    }

    public void changeDate(int field, int value)
    {
        date.add(field, value);

        getCorrespondingFragment(currentFragment).OnDateUpdated(getDate());
    }

    public Date getDate()
    {
        return new Date(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    public ArrayList<Integer> getMonthlyEventNum(ArrayList<Integer> eventNums, int year, int month)
    {
        int[] result = new int[32];

        Cursor cursor = db.rawQuery("select * from Monthly_T where year = " + year + " and month = " + month + " order by date asc", null);

        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            result[cursor.getInt(cursor.getColumnIndex("date"))] = cursor.getInt(cursor.getColumnIndex("eventNum"));
            cursor.moveToNext();
        }

        for(int i = 0; i < 32; i++)
        {
            eventNums.add(result[i]);
        }

        return eventNums;
    }

    public ArrayList<Schedule> getDailySchedule(ArrayList<Schedule> schedules, int year, int month, int date)
    {
        Cursor cursor = db.rawQuery("select * from Schedule_T where year = " + year + " and month = " + month + " and date = " + date, null);

        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            schedules.add(new Schedule(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("title")), cursor.getString(cursor.getColumnIndex("content"))));
            cursor.moveToNext();
        }

        return schedules;
    }

    public ArrayList<ArrayList<String>> getWeeklySchedule(ArrayList<ArrayList<String>> titles, Date date)
    {
        for(int i = 0; i < 7; i++)
        {
            ArrayList<String> newArray = new ArrayList<>();

            Cursor cursor = db.rawQuery("select * from Schedule_T where year = " + date.year + " and month = " + date.month + " and date = " + date.date, null);

            cursor.moveToFirst();
            for(int j = 0; j < cursor.getCount(); j++)
            {
                newArray.add(cursor.getString(cursor.getColumnIndex("title")));
                cursor.moveToNext();
            }

            titles.add(newArray);
            date.goNext();
        }

        return titles;
    }

    public void deleteItem(int year, int month, int date, int id)
    {
        Cursor cursor = db.rawQuery("select * from Monthly_T where year = " + year + " and month = " + month + " and date = " + date, null);
        cursor.moveToFirst();
        int eventNum = cursor.getInt(cursor.getColumnIndex("eventNum")) - 1;

        db.execSQL("update Monthly_T set eventNum = " + eventNum + " where year = " + year + " and month = " + month + " and date = " + date);

        db.execSQL("delete from Schedule_T where id = " + id);
    }

    public ArrayList<Schedule> getDailySchedule(ArrayList<Schedule> schedules)
    {
        return getDailySchedule(schedules, getDate().year, getDate().month, getDate().date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public int addNewSchedule(String title, String content)
    {
        int mYear = getDate().year;
        int mMonth = getDate().month;
        int mDate = getDate().date;

        int result = RESULT_OK;

        Cursor cursor = db.rawQuery("select * from Monthly_T where year = " + mYear + " and month = " + mMonth + " and date = " + mDate, null);
        if(cursor.getCount() == 0)
        {
            db.execSQL("insert into Monthly_T(year, month, date, eventNum) values (" + mYear + ", " + mMonth + ", " + mDate + ", 1)");
        }
        else
        {
            cursor.moveToFirst();
            int eventNum = cursor.getInt(cursor.getColumnIndex("eventNum")) + 1;
            db.execSQL("update Monthly_T set eventNum = " + eventNum + " where year = " + mYear + " and month = " + mMonth + " and date = " + mDate);
        }

        db.execSQL("insert into Schedule_T(year, month, date, title, content) values (" + mYear + ", " + mMonth + ", " + mDate + ", '" + title + "', '" + content + "')");

        return result;
    }
}
