package heekangpark.projects.calendar.DataPackage;

import android.support.annotation.Nullable;

public class Schedule {
    public int id;
    public String title;
    public String content;

    public Schedule(int id, String title, @Nullable String content)
    {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
