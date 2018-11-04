package heekangpark.projects.calendar.AddScheduleActivityPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import heekangpark.projects.calendar.R;

public class AddScheduleActivity extends AppCompatActivity {

    private int year;
    private int month;
    private int date;
    private TextView textView_date;
    private EditText editText_title;
    private EditText editText_content;
    private Button button_add;
    private Button button_cancel;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.layout_addschedule);

        Intent intent = getIntent();
        year = intent.getIntExtra("year", 0);
        month = intent.getIntExtra("month", 0);
        date = intent.getIntExtra("date", 0);

        textView_date = findViewById(R.id.textview_addschedule_date);
        editText_title = findViewById(R.id.edittext_addschedule_title);
        editText_content = findViewById(R.id.edittext_addschedule_content);
        button_add = findViewById(R.id.button_addschedule_add);
        button_cancel = findViewById(R.id.button_addschedule_cancel);

        textView_date.setText(year + "." + month + "." + date);

        //db = openOrCreateDatabase(db_name, MODE_PRIVATE, null);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_title.getText().toString().length() == 0)
                {
                    Toast.makeText(AddScheduleActivity.this, "Title should be entered.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("title", editText_title.getText().toString());
                intent.putExtra("content", editText_content.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
