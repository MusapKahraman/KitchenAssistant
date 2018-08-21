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
            ShoppingListWidget.updateRecipeIngredientsWidget(context, appWidgetManager,
                    shoppingList.toString(), appWidgetIds);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        String message, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shopping_list);

        sMessage = message;
        views.setTextViewText(R.id.tv_app_widget, message);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AppConstants.EXTRA_APP_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.frame_app_widget, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void updateRecipeIngredientsWidget(Context context, AppWidgetManager appWidgetManager,
                                                      String message, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, message, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // When the widget is added for the first time, sMessage is null.
        if (sMessage == null) {
            String message = context.getResources().getString(R.string.widget_default);
            updateRecipeIngredientsWidget(context, appWidgetManager, message, appWidgetIds);
        }
    }
}
