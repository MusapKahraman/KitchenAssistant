package com.example.kitchen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.kitchen.R;
import com.example.kitchen.activities.LoginActivity;
import com.example.kitchen.data.local.entities.Ware;
import com.example.kitchen.utility.AppConstants;

import java.util.List;

public class ShoppingListWidget extends AppWidgetProvider {
    private static String sMessage;

    public static void fillShoppingListWidget(Context context, List<Ware> wares) {
        if (wares != null) {
            StringBuilder shoppingList = new StringBuilder(context.getString(R.string.shopping_list));
            shoppingList.append("\n\n");
            for (Ware item : wares) {
                shoppingList
                        .append("* ")
                        .append(item.amount).append(" ")
                        .append(item.amountType).append(" ")
                        .append(item.name).append("\n");
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, ShoppingListWidget.class));
            updateAppWidget(context, appWidgetManager, shoppingList.toString(), appWidgetIds);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        String message, int[] appWidgetIds) {
        sMessage = message;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shopping_list);
        views.setTextViewText(R.id.tv_app_widget, message);
        views.setTextColor(R.id.tv_app_widget, context.getResources().getColor(R.color.widget_text));
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AppConstants.EXTRA_APP_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.frame_app_widget, pendingIntent);
        for (int appWidgetId : appWidgetIds) {
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // When the widget is added for the first time, sMessage is null.
        if (sMessage == null) {
            String message = context.getResources().getString(R.string.widget_default);
            updateAppWidget(context, appWidgetManager, message, appWidgetIds);
        } else {
            updateAppWidget(context, appWidgetManager, sMessage, appWidgetIds);
        }
    }
}
