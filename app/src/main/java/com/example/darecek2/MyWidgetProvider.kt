package com.example.darecek2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import kotlin.random.Random

class MyWidgetProvider : AppWidgetProvider() {

    private val messages = listOf(
        "Máma je nejlepší!",
        "Máma je skvělá!",
        "Máma je úžasná!",
        "Máma je neporazitelná!",
        "Máma je geniální!"
    )

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Aktualizace widgetu
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        // Náhodná zpráva
        val message = messages[Random.nextInt(messages.size)]
        views.setTextViewText(R.id.widgetText, message)

        // Nastavení akce při kliknutí (pro aktualizaci)
        val intent = Intent(context, MyWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widgetText, pendingIntent)

        // Aktualizace widgetu
        appWidgetManager.updateAppWidget(widgetId, views)
    }
}
