package heekangpark.projects.calendar.DataPackage;

import java.util.Calendar;

public class Date {

    public int year;
    public int month;
    public int date;

    public Date(int year, int month, int date)
    {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    public int getWeekday()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, date);
        return temp.get(Calendar.DAY_OF_WEEK);
    }

    public int getBlankDays()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, 1);
        return temp.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public int getDaysInMonth()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, date);
        return temp.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getWeekOfMonth()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, date);
        return temp.get(Calendar.WEEK_OF_MONTH);
    }

    public static String getWeekdayInStr(int weekday)
    {
        switch(weekday)
        {
            case Calendar.SUNDAY:
                return "SUN";
            case Calendar.MONDAY:
                return "MON";
            case Calendar.TUESDAY:
                return "TUE";
            case Calendar.WEDNESDAY:
                return "WED";
            case Calendar.THURSDAY:
                return "THU";
            case Calendar.FRIDAY:
                return "FRI";
            case Calendar.SATURDAY:
                return "SAT";
            default:
                throw new RuntimeException("getWeekdayInStr() Error");
        }
    }

    public void goNext()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(year, month - 1, date);
        temp.add(Calendar.DAY_OF_MONTH, 1);

        year = temp.get(Calendar.YEAR);
        month = temp.get(Calendar.MONTH) + 1;
        date = temp.get(Calendar.DAY_OF_MONTH);
    }
}
