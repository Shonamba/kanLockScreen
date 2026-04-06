package com.kannada.lockclock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

public class LockScreenOverlayService extends Service implements ScreenReceiver.ScreenStateListener {

    private static final String CHANNEL_ID = "kannada_clock_channel";
    private static final int NOTIFICATION_ID = 1;

    private WindowManager windowManager;
    private View overlayView;
    private Handler handler;
    private Runnable ticker;
    private ScreenReceiver screenReceiver;
    private boolean overlayVisible = false;
    private GestureDetector gestureDetector;

    private TextView overlayTime, overlayPeriod, overlayDate, overlayDay, overlaySeconds;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("ಕನ್ನಡ ಗಡಿಯಾರ")
                .setContentText("ಲಾಕ್ ಸ್ಕ್ರೀನ್ ಗಡಿಯಾರ ಚಾಲನೆಯಲ್ಲಿದೆ")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setOngoing(true)
                .build();
        } else {
            notification = new Notification.Builder(this)
                .setContentTitle("ಕನ್ನಡ ಗಡಿಯಾರ")
                .setContentText("ಲಾಕ್ ಸ್ಕ್ರೀನ್ ಗಡಿಯಾರ ಚಾಲನೆಯಲ್ಲಿದೆ")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setOngoing(true)
                .build();
        }
        startForeground(NOTIFICATION_ID, notification);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        handler = new Handler(Looper.getMainLooper());

        screenReceiver = new ScreenReceiver();
        screenReceiver.setListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 150;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffY = e1.getY() - e2.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        hideOverlay();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                hideOverlay();
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showOverlay();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideOverlay();
        if (handler != null && ticker != null) {
            handler.removeCallbacks(ticker);
        }
        if (screenReceiver != null) {
            try {
                unregisterReceiver(screenReceiver);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onScreenOn() {
        showOverlay();
    }

    @Override
    public void onScreenOff() {
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "ಕನ್ನಡ ಗಡಿಯಾರ ಸೇವೆ",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("ಲಾಕ್ ಸ್ಕ್ರೀನ್ ಗಡಿಯಾರ ಸೇವೆ");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    private void showOverlay() {
        if (overlayVisible) {
            startTicking();
            return;
        }

        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            overlayView = inflater.inflate(R.layout.overlay_clock, null);

            overlayTime = overlayView.findViewById(R.id.overlay_time);
            overlayPeriod = overlayView.findViewById(R.id.overlay_period);
            overlayDate = overlayView.findViewById(R.id.overlay_date);
            overlayDay = overlayView.findViewById(R.id.overlay_day);
            overlaySeconds = overlayView.findViewById(R.id.overlay_seconds);

            overlayView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });

            windowManager.addView(overlayView, params);
            overlayVisible = true;

            startTicking();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideOverlay() {
        if (!overlayVisible || overlayView == null) return;
        try {
            windowManager.removeView(overlayView);
        } catch (Exception ignored) {
        }
        overlayView = null;
        overlayVisible = false;

        if (handler != null && ticker != null) {
            handler.removeCallbacks(ticker);
        }
    }

    private void startTicking() {
        if (ticker != null) {
            handler.removeCallbacks(ticker);
        }

        ticker = new Runnable() {
            @Override
            public void run() {
                if (!overlayVisible) return;
                updateClock();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(ticker);
    }

    private void updateClock() {
        if (overlayTime == null) return;
        try {
            Calendar cal = Calendar.getInstance();
            overlayTime.setText(KannadaTimeUtil.getTimeLarge(cal));
            overlayPeriod.setText(KannadaTimeUtil.getPeriodOnly(cal));
            overlayDate.setText(KannadaTimeUtil.getDate(cal));
            overlayDay.setText(KannadaTimeUtil.getDay(cal));
            overlaySeconds.setText(KannadaTimeUtil.getSeconds(cal));
        } catch (Exception ignored) {
        }
    }
}
