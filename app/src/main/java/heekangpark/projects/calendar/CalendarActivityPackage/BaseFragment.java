package heekangpark.projects.calendar.CalendarActivityPackage;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.TextView;


import heekangpark.projects.calendar.DataPackage.Date;

public abstract class BaseFragment extends Fragment {
    protected TextView dateIndicator;
    protected Button button_left;
    protected Button button_right;

    public abstract void OnDateUpdated(Date date);
}
