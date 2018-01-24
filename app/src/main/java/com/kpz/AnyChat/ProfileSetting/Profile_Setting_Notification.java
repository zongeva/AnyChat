package com.kpz.AnyChat.ProfileSetting;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;

import java.util.Date;

/**
 * Created by user on 10/4/2017.
 */

public class Profile_Setting_Notification extends AppCompatActivity {
    private final String ALARMS_COLUMN_TIME = "time";
    int endtime, begintime;
    boolean open;
    Boolean aOpen;
    ToggleButton toggleButton;
    TextView set_begin_tv;
    TextView set_end_tv;
    int realbegin, realend;
    String ampm ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_newmsgnotify);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        long id = RequestHelper.getAccountInfo().getID();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int hour, min;

        set_begin_tv = (TextView) findViewById(R.id.textView70);
        set_end_tv = (TextView) findViewById(R.id.textView73);

        RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
            @Override
            public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                open = aBoolean;
                Log.e("OnBegin ", integer+"");
                Log.e("OnEnd ", integer2+"");
                Log.e("OnOpen ", open+"");

                if(open == true){
                    toggleButton.setChecked(true);
                    toggleButton.isChecked();
                }
                else {
                     if(toggleButton.isChecked()){
                        toggleButton.setChecked(false);

                    }

                }
            }
        });


        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (toggleButton.isChecked()){

                    set_end_tv.performClick();
                    set_begin_tv.performClick();
                    toggleButton.setChecked(true);
                    toggleButton.setActivated(true);
                    RequestHelper.setGlobalNoDisturbMode(begintime, endtime, true, new RequestCallBack() {
                        @Override
                        public void handleSuccess(Object o, Object o2, Object o3) {
                    Toast.makeText(Profile_Setting_Notification.this, "Do Not Disturb Enabled", Toast.LENGTH_SHORT).show();

                        }
                    });

                    RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                            Log.e("Set1 Begin ", integer+"");
                            Log.e("Set1 End ", integer2+"");
                            Log.e("Set1 Open ", aBoolean+"");

                        }
                    });

                }
                else if (!toggleButton.isChecked()){
                    toggleButton.setActivated(false);
                    RequestHelper.setGlobalNoDisturbMode(begintime, endtime, false, new RequestCallBack() {
                        @Override
                        public void handleSuccess(Object o, Object o2, Object o3) {
                            Toast.makeText(Profile_Setting_Notification.this, "Do Not Disturb Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                            Log.e("Set2 Begin ", integer+"");
                            Log.e("Set2 End ", integer2+"");
                            Log.e("Set2 Open ", aBoolean+"");
                        }
                    });

                }
            }
        });
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {

            Toast.makeText(Profile_Setting_Notification.this, date + "", Toast.LENGTH_SHORT).show();

            Log.e("Date Time : ", date.toString());
        }

        @Override
        public void onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    };

    public void set_begin(View view) {
        int hour = 0, min = 0;
        TimePickerDialog Tp = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                begintime = (hourOfDay * 3600) + (minute * 60);

                TextView btime = (TextView)findViewById(R.id.begintime);
                btime.setVisibility(View.VISIBLE);
                if(hourOfDay >12){
                    ampm = "PM";
                    realbegin = hourOfDay - 12;
                } else {
                    ampm = "AM";
                    if(hourOfDay == 0) {
                        realbegin = 12;
                    } else{
                        realbegin = hourOfDay;
                    }
                }
                btime.setText(realbegin + " : " + minute + " "+ ampm);
            }
        }, hour, min, false);
        Tp.setTitle("Begin Time");
        Tp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Tp.show();

        toggleButton.setActivated(false);
        toggleButton.setChecked(false);
        RequestHelper.setGlobalNoDisturbMode(0, 0, false, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {

            }
        });

//        int  i = hour * 3600 + minute * 60;
    }


    public void set_endtime(View view) {
        int hour = 0, min = 0;
        TimePickerDialog Tp = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDays, int minutes) {
                endtime = (hourOfDays * 3600) + (minutes * 60);
                TextView etime = (TextView)findViewById(R.id.endtime);
                etime.setVisibility(View.VISIBLE);
                if(hourOfDays >12){
                    ampm = "PM";
                    realend = hourOfDays - 12;
                } else {
                    if(hourOfDays == 0) {
                        realend = 12;
                    } else{
                        realend = hourOfDays;
                    }
                }
                etime.setText(realend + " : " + minutes + " " +ampm );
            }
        }, hour, min, false);
        Tp.setTitle("End Time");
        Tp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Tp.show();

        toggleButton.setActivated(false);
        toggleButton.setChecked(false);
        RequestHelper.setGlobalNoDisturbMode(0, 0, false, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {

            }
        });

    }





    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}


