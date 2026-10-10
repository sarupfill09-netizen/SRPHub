package com.srpteam.srphub

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Do not read, store, or log notification content.
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // No action.
    }
}
