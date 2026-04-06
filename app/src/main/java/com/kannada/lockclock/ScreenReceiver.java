
package com.kannada.lockclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    private ScreenStateListener listener;

    public interface ScreenStateListener {
        void onScreenOn();
        void onScreenOff();
    }

    public void setListener(ScreenStateListener l) {
        this.listener = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()) && listener != null) {
            listener.onScreenOn();
        }
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction()) && listener != null) {
            listener.onScreenOff();
        }
    }
}
