package com.example.project.utils;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.project.R;
import android.util.Log;
import com.example.project.activity.CartActivity;

public class CartNotificationManager {
    private static final String CHANNEL_ID = "cart_notifications";
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "CartNotificationManager";

    public static void showCartNotification(Context context, int cartCount) {
        Log.d(TAG, "showCartNotification called with cartCount = " + cartCount);

        createNotificationChannel(context);

        Intent intent = new Intent(context, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle("Items in your cart")
                .setContentText("You have " + cartCount + " items waiting.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(cartCount) // Sets badge count for supported launchers
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Notification shown with cartCount = " + cartCount);
        } else {
            Log.e(TAG, "NotificationManager is null, cannot show notification.");
        }
    }

    public static void applyBadgeCount(Context context, int cartCount) {
        Log.d(TAG, "applyBadgeCount called with cartCount = " + cartCount);

        if (context == null) {
            Log.e(TAG, "Context is null, cannot apply badge");
            return;
        }

        createNotificationChannel(context);

        if (cartCount <= 0) {
            Log.d(TAG, "cartCount is <= 0, canceling notification and badge");
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
                Log.d(TAG, "Notification and badge canceled");
            }
            return;
        }

        try {
            Intent intent = new Intent(context, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_shopping_cart)
                    .setContentTitle("Items in your cart")
                    .setContentText("You have " + cartCount + " items waiting.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(cartCount) // Sets badge count
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSilent(true); // Silent notification to avoid spamming the user

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                Log.d(TAG, "Badge applied with cartCount = " + cartCount);
            } else {
                Log.e(TAG, "NotificationManager is null, cannot apply badge");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply badge count: " + e.getMessage());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Cart Notifications";
            String description = "Notifications for cart items";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}