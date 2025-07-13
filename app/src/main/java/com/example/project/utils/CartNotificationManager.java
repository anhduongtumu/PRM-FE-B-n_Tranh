package com.example.project.utils;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import me.leolin.shortcutbadger.ShortcutBadger;
import com.example.project.R;
import com.example.project.activity.CartActivity;

public class CartNotificationManager {
    private static final String CHANNEL_ID = "cart_notifications";
    private static final int NOTIFICATION_ID = 1001;

    public static void showCartNotification(Context context, int cartCount) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart) // use your cart icon
                .setContentTitle("Items in your cart")
                .setContentText("You have " + cartCount + " items waiting.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setNumber(cartCount) // some launchers read this for badge
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Apply badge count using ShortcutBadger
        ShortcutBadger.applyCount(context, cartCount);
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

