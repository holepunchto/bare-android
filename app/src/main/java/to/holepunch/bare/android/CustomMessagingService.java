package to.holepunch.bare.android;

import android.app.NotificationManager;
import android.app.NotificationChannel;
import androidx.core.app.NotificationCompat;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.json.JSONObject;
import java.io.IOException;

import to.holepunch.bare.kit.Worklet;
import to.holepunch.bare.kit.MessagingService;

public class CustomMessagingService extends MessagingService {
    private static final String CHANNEL_ID = "custom_channel_id";
    private NotificationManager notificationManager;

    public CustomMessagingService() {
        super(new Worklet.Options());
        Log.v("CustomMessagingService", "Worklet created!");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_DEFAULT));

        Context context = getApplicationContext();
        AssetManager assetManager = context.getAssets();

        try {
            this.start("app.js", assetManager.open("push.js"), null);
            Log.v("CustomMessagingService", "Worklet started!");
        } catch (IOException e) {
            Log.e("CustomMessagingService", "Failed to start worklet", e);
        }
    }

    @Override
    public void onWorkletReply(JSONObject reply) {
        Log.v("CustomMessagingService", "json: " + reply);
        try {
            notificationManager.notify(1, new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(reply.optString("title", "Default title"))
                    .setContentText(reply.optString("body", "Default description"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
            );
        } catch (Exception e) {
            Log.e("CustomMessagingService", "Error showing notification", e);
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.v("CustomMessagingService", "token: " + token);
    }
}
