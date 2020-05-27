package com.example.startservicenokill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    Button btnStartSer,btnStopSer;
    String TAG ="hank";
    boolean isServiceWork;
    MyReceive myReceive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartSer = findViewById(R.id.btnStart);
        btnStopSer = findViewById(R.id.btnStop);
        btnStartSer.setOnClickListener(this);
        btnStopSer.setOnClickListener(this);

        myReceive = new MyReceive();
        IntentFilter intentFilter = new IntentFilter("MyService");
        registerReceiver(myReceive,intentFilter);
        Log.v(TAG,"MainActivity =>on Create()");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                mStartService();
                break;
            case R.id.btnStop:
                mStopService();
                break;
        }
    }
    //停止Servie
    private void mStopService() {
        //停止Servie
        Intent intent = new Intent(this,MyService.class); //要Intent到的Service
        stopService(intent);//停止Service
        Log.v(TAG,"MainActivity =>mStopService()");
    }

    //啟動Servie
    private void mStartService() {
        //啟動Service
        Intent intent = new Intent(this,MyService.class);
        //7.這邊Startservice 產生亂數,當按下start服務時,產生int亂數資料,讓onStartCommand接收
        intent.putExtra("i",(int)(Math.random()*49+1));
        startService(intent); //啟動Service
        Log.v(TAG,"MainActivity =>mStartService()");
    }

    //按鈕檢查Service是否活著
    public void isServiceLive(View view) {
        isServiceWork = MyService.isWorked(this);
        Log.v(TAG,"Service是否活著:" + isServiceWork +"");
    }


    public class MyReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getIntExtra("action",-1) == MyService.ACTION_REBOOT){
                Intent intentReceive = new Intent();
                intent.putExtra("action",MyService.ACTION_OK);
                startService(intentReceive);
                Log.v(TAG,"MyReceive:" + intent.getIntExtra("action",-1) +"");
            }
            Log.v(TAG,"MyReceive:" );
        }
    }
}
