package com.kpz.AnyChat.Others;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by Lenovo on 2017-11-03.
 */

public class GlobalVariable extends Application{

    int mSkillPoint;
    int msg_count;
    int engergy;

    public int Get_Skill_Point(){
//        Toast.makeText(Point_GlobalVariable.this, "Point Counter : " + mSkillPoint, Toast.LENGTH_SHORT).show();
        return mSkillPoint;
    }

    public void  Set_Skill_Point(int point){
        Toast.makeText(GlobalVariable.this, "Point Counter : " + point, Toast.LENGTH_SHORT).show();

//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                mSkillPoint = point;
//            }
//
//            public void onFinish() {
//                mSkillPoint = 0;
//
//            }
//
//        }.start();
        mSkillPoint = point;

    }



    public  int Get_Energy(){
        return  engergy;
    }

    public  void Set_Energy(int counter){
        msg_count = engergy;
    }

}
