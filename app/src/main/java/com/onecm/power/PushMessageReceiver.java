package com.onecm.power;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.onecm.bean.Discover;
import com.onecm.bean.PushDetails;
import com.onecm.util.DataUtils;

import cn.bmob.push.PushConstants;

/**
 * Created by kaming on 2015/4/5 0005.
 */
public class PushMessageReceiver extends BroadcastReceiver {
    private PushDetails details;
    private Notification notification;
    private NotificationManager mNotificationManager;
    private static final int IDNORMAL = 1;
    private static final int IDUPDATE = 2;
    private static final int IDEMPTY = 3;
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("TAG", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            String pushJson = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            Gson gson = new Gson();
            details = gson.fromJson(pushJson,PushDetails.class);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (details!=null){
                notification = new Notification();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notification.icon = R.drawable.ic_launcher;
                notification.when = System.currentTimeMillis();
                notification.tickerText = details.getContent();
                switch (details.getType()){
                    case IDNORMAL:
                        pushNormal();
                        break;
                    case IDUPDATE:
                        pushUpdate(context);
                        mNotificationManager.notify(IDUPDATE,notification);
                        break;
                    case IDEMPTY:
                        pushEmpty(context);
                        mNotificationManager.notify(IDEMPTY,notification);
                        break;
                }
            }
        }
    }

    private void pushEmpty(Context context) {
        Intent startContent = new Intent(context, MainActivity.class);
        startContent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pen = PendingIntent.getActivity(context, 0, startContent, 0);
        notification.setLatestEventInfo(context, details.getTitle(), details.getContent(), pen);
    }

    private void pushUpdate(Context context) {
        //
        Intent startContent = new Intent(context, MainActivity.class);
        startContent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pen = PendingIntent.getActivity(context, 0, startContent, 0);
        notification.setLatestEventInfo(context, details.getTitle(), details.getContent(), pen);
    }

    private void pushNormal() {
        new GetNews().execute();
    }

    class GetNews extends AsyncTask<Void,Void,Discover>{

        @Override
        protected Discover doInBackground(Void... params) {
            Discover dis = new DataUtils(context).getNew(details.getObjectId());
            return dis;
        }

        @Override
        protected void onPostExecute(Discover discover) {
            super.onPostExecute(discover);
            Intent startContent;
            if(discover!=null){
                startContent = new Intent(context, ContentActivity.class);
                Log.d("TAG","getNEWS:"+discover);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mDis", discover);
                startContent.putExtra("bundle", bundle);
            }else{
                startContent = new Intent(context, MainActivity.class);
            }
            startContent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pen = PendingIntent.getActivity(context, 0, startContent, 0);
            notification.setLatestEventInfo(context, details.getTitle(), details.getContent(), pen);
            mNotificationManager.notify(IDNORMAL,notification);
        }
    }


}
