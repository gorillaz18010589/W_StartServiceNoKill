package com.example.startservicenokill;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    String TAG = "hank";
    int i = 1;
    Timer timer;
    Context context;
    final static int ACTION_REBOOT = 0;
    final static int ACTION_OK = 1;

    public MyService(Context context) {
        this.context = context;
    }

    public MyService() {
    }

    //1.創建Service 自動有了onBind(Intent intent),但出現因為尚未實作的例外
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    //1.StartService初次被啟用使(第一個被呼叫只做一次,做初始化的事情)
    @Override
    public void onCreate() {
        super.onCreate();

        //5.Service 創建時初始化計時器
        timer = new Timer();
        timer.schedule(new MyTask(), 0, 1000);


        Log.v(TAG, "Service:onCreate:");
    }

    //2.每當有Acitvity用StartSevice時,會被呼叫(第二個被呼叫的,每次呼叫StartService都會被呼叫)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //8.onStartCommand當startService會被呼叫因此透過MainActivity傳來的Intent取得資料
        int rand = intent.getIntExtra("i", -1);
        i = rand;
        Log.v(TAG, "onStartCommand:" + "/Intent:" + intent + "/flags:" + flags + "/startId:" + startId);


        if (intent.getIntExtra("action", -1) == ACTION_OK) {
            Log.v(TAG, "onStartCommand:" + "ACTION_OK" + "重啟Service成功");
        }

        //在service的onstart方法裡返回 STATR_STICK
        //return START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }


    //3.當StropService時被呼叫,(測試結果關掉App也馬上呼叫SerVice被摧毀)
    @Override
    public void onDestroy() {
        super.onDestroy();
        //6.onDestroy時關掉季時
        if (timer != null) {
            timer.cancel();
            timer.purge(); //刪除所有已取消的任務
            timer = null; //重新給空值

            Intent intent = new Intent("MyService");
            intent.putExtra("action", ACTION_REBOOT);
            Log.v(TAG, "onDestroy:" + "Service是否活著:" + isWorked(context));
            sendBroadcast(intent);
        }

        /*
        覆寫Service的onDestroy方法
        思路：在onDestroy中再次啟動該服務
        保活程度：很弱，只在兩種情況下工作：正在運行里殺服務，DDMS裡停止進程
        */
//        Intent intent = new Intent(this, MyService.class);
//        this.startService(intent);

    }


    //4.計時Service秒數任務
    private class MyTask extends TimerTask {
        @Override
        public void run() {
            Log.v(TAG, "Service存活秒數:" + i++ + "秒");
        }
    }

    //檢查我這隻APP是否SerVice還存活著
    public static boolean isWorked(Context context) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals("MyService")) ;
            {
                return true;
            }
        }
        return false;
    }


}
