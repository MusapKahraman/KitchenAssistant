package com.example.kitchen.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.kitchen.R;
import com.example.kitchen.activities.MainActivity;
import com.example.kitchen.utility.AppConstants;

public class ShoppingListWidget extends AppWidgetProvider {
    private static String sMessage;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        String message, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shopping_list);


        sMessage = message;
        views.setTextViewText(R.id.appwidget_text, message);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_WIDGET, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateRecipeIngredientsWidget(Context context, AppWidgetManager appWidgetManager,
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
