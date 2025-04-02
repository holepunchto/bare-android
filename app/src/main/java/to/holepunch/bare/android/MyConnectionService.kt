package to.holepunch.bare.android

import android.app.ActionBar
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView


class MyConnection(private val ctx: Context) : Connection() {
    companion object {
        private const val TAG = "MyConnection"
        private const val YOUR_CHANNEL_ID = "incoming_call_channel"
    }

    override fun onAnswer() {
        setActive()
        Log.v(TAG, "onAnswer")
    }

    override fun onDisconnect() {
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
        Log.v(TAG, "onDisconnect")
    }

    override fun onReject() {
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
        Log.v(TAG, "onReject")
    }

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Log.v(TAG, "onShowIncomingCallUi")

        val notificationManager: NotificationManager = ctx.getSystemService(
            NotificationManager::class.java
        )
        val channel = NotificationChannel(
            YOUR_CHANNEL_ID, "Incoming Calls",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)


        // Create an intent which triggers your fullscreen incoming call user interface.
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setClass(ctx, YourIncomingCallActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(ctx, 1, intent, PendingIntent.FLAG_MUTABLE)


        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads up notification which slides down over top of the current content.
        val builder: Notification.Builder = Notification.Builder(ctx, YOUR_CHANNEL_ID)
        builder.setOngoing(true)
        builder.setVisibility(Notification.VISIBILITY_PUBLIC)


        // Set notification content intent to take user to fullscreen UI if user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent)


        // Set full screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true)


        // Setup notification content.
        builder.setSmallIcon(android.R.drawable.sym_call_incoming)
        builder.setContentTitle("Your notification title")
        builder.setContentText("Your notification content.")


        // Set notification as insistent to cause your ringtone to loop.
        val notification: Notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        // Use builder.addAction(..) to add buttons to answer or reject the call.

        notificationManager.notify(YOUR_CHANNEL_ID, 1, notification)

        Log.v(TAG, "onShowIncomingCallUi end")
    }
}

class YourIncomingCallActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a RelativeLayout as the root view
        val rootLayout = RelativeLayout(this).apply {
            layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)
        }

        // Create a TextView for displaying the caller's name or ID
        val callerName = TextView(this).apply {
            text = "Incoming Call"
            textSize = 24f
            setTextColor(Color.WHITE)
            id = View.generateViewId()
        }
        val callerNameParams = RelativeLayout.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            topMargin = 200
        }

        // Create a Button for answering the call
        val answerButton = Button(this).apply {
            text = "Answer"
            setBackgroundColor(Color.GREEN)
            id = View.generateViewId()
        }
        val answerButtonParams = RelativeLayout.LayoutParams(
            400,
            150
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            addRule(RelativeLayout.BELOW, callerName.id)
            topMargin = 200
        }

        // Create a Button for rejecting the call
        val rejectButton = Button(this).apply {
            text = "Reject"
            setBackgroundColor(Color.RED)
            id = View.generateViewId()
        }
        val rejectButtonParams = RelativeLayout.LayoutParams(
            400,
            150
        ).apply {
            addRule(RelativeLayout.CENTER_HORIZONTAL)
            addRule(RelativeLayout.BELOW, answerButton.id)
            topMargin = 80
        }

        // Add views to the root layout
        rootLayout.addView(callerName, callerNameParams)
        rootLayout.addView(answerButton, answerButtonParams)
        rootLayout.addView(rejectButton, rejectButtonParams)

        // Set the root layout as the content view
        setContentView(rootLayout)

        // Set button click listeners
        answerButton.setOnClickListener {
            // Handle answering the call
            finish()
        }

        rejectButton.setOnClickListener {
            // Handle rejecting the call
            finish()
        }
    }
}

class MyConnectionService: ConnectionService() {
    companion object {
        private const val TAG = "MyConnectionService"
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.v(TAG, "In on createIncomingConnection")
        val conn = MyConnection(applicationContext)

        conn.setAddress(request?.address, TelecomManager.PRESENTATION_ALLOWED)
        conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        conn.setCallerDisplayName("bare dev", TelecomManager.PRESENTATION_ALLOWED)
        conn.setAudioModeIsVoip(true)

        return conn
    }
}