package heekangpark.projects.calendar.IntroActivityPackage;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import heekangpark.projects.calendar.CalendarActivityPackage.CalendarActivity;
import heekangpark.projects.calendar.R;

public class IntroActivity extends AppCompatActivity {
    private String db_name = "Schedule.db";
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.layout_intro);

        db = openOrCreateDatabase(db_name, MODE_PRIVATE, null);
        db.execSQL("create table if not exists 'Monthly_T' (" +
                "'year' INTEGER, " +
                "'month' INTEGER, " +
                "'date' INTEGER, " +
                "'eventNum' INTEGER, " +
                "PRIMARY KEY('year','month','date'));");
        db.execSQL("create table if not exists 'Schedule_T' (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'year' INTEGER NOT NULL, " +
                "'month' INTEGER NOT NULL, " +
                "'date' INTEGER NOT NULL, " +
                "'title' TEXT NOT NULL, " +
                "'content' TEXT);");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startLoading();
    }

    private void startLoading()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();

                Intent intent = new Intent(IntroActivity.this, CalendarActivity.class);
                intent.putExtra("db_name", db_name);
                startActivity(intent);
            }
        },1300);
    }
}
