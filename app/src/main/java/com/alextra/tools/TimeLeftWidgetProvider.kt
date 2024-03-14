package xml

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.alextra.tools.R
import java.util.*

/*
<receiver android:name=".YourBroadcastReceiver">
    <intent-filter>
        <action android:name="android.intent.action.USER_PRESENT" />
    </intent-filter>
</receiver>

 */

public class TimeLeftWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update each of the widgets with the remote adapter
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Get current time and calculate time left for a given hour
            val timeLeft = calculateTimeLeftForHour(17) // Example for 5 PM

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_time_left, timeLeft)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun calculateTimeLeftForHour(targetHour: Int): String {
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, targetHour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            if (currentTime.after(targetTime)) {
                targetTime.add(Calendar.DAY_OF_MONTH, 1)
            }

            val millisLeft = targetTime.timeInMillis - currentTime.timeInMillis
            val hoursLeft = millisLeft % (1000*60*60)
            val minutesLeft = millisLeft % 1000*60
            return String.format("%02d:%02d hours left", hoursLeft, minutesLeft)
        }
    }
}
