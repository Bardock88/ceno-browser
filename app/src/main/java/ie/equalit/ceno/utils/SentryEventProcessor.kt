package ie.equalit.ceno.utils

import android.content.Context
import android.util.Log
import io.sentry.EventProcessor
import io.sentry.Hint
import io.sentry.SentryEvent
import org.json.JSONObject
import com.google.gson.Gson
import ie.equalit.ceno.settings.Settings

class SentryEventProcessor(val context: Context) : EventProcessor {
    override fun process(event: SentryEvent, hint: Hint): SentryEvent? {

        val isPermissionGranted = Settings.isCrashReportingPermissionGranted(context)
        val isCrash = event.exceptions?.isNotEmpty() == true

        return when {
            isPermissionGranted -> {
                event
            }
            isCrash -> {
                Settings.setLastCrash(context, JSONObject(Gson().toJson(event)).toString())
                Log.d("PPPPPP", "crash")
                null
            }
            else -> {
                // There's no need to nudge the user when there's a non-crash (e.g ANR)
                null
            }
        }
    }
}