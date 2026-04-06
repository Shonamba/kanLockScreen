package com.kannada.lockclock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    private static final int OVERLAY_PERMISSION_CODE = 100;

    private TextView previewTime, previewDate, previewDay, previewPeriod;
    private TextView statusText;
    private Button btnStart, btnStop, btnPermission;
    private LinearLayout previewCard;
    private Handler handler;
    private Runnable updateRunnable;
    private boolean serviceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        previewTime = findViewById(R.id.preview_time);
        previewDate = findViewById(R.id.preview_date);
        previewDay = findViewById(R.id.preview_day);
        previewPeriod = findViewById(R.id.preview_period);
        statusText = findViewById(R.id.status_text);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnPermission = findViewById(R.id.btn_permission);
        previewCard = findViewById(R.id.preview_card);

        // Button listeners
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOverlayAndStart();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopOverlayService();
            }
        });

        btnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOverlayPermissionSettings();
            }
        });

        // Start preview clock ticking
        handler = new Handler(Looper.getMainLooper());
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePreview();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        if (handler != null && updateRunnable != null) {
            handler.post(updateRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    // ─── Preview Clock ───────────────────────────────────────

    private void updatePreview() {
        Calendar cal = Calendar.getInstance();
        previewTime.setText(KannadaTimeUtil.getTimeLarge(cal));
        previewDate.setText(KannadaTimeUtil.getDate(cal));
        previewDay.setText(KannadaTimeUtil.getDay(cal));
        previewPeriod.setText(KannadaTimeUtil.getPeriodOnly(cal));
    }

    // ─── Overlay Permission ──────────────────────────────────

    private boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    private void openOverlayPermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (hasOverlayPermission()) {
                Toast.makeText(this, "ಅನುಮತಿ ದೊರಕಿದೆ! ✓", Toast.LENGTH_SHORT).show();
                startOverlayService();
            } else {
                Toast.makeText(this,
                    "ಅನುಮತಿ ನಿರಾಕರಿಸಲಾಗಿದೆ. ದಯವಿಟ್ಟು 'Display over other apps' ಆನ್ ಮಾಡಿ.",
                    Toast.LENGTH_LONG).show();
            }
            updateUI();
        }
    }

    // ─── Service Control ─────────────────────────────────────

    private void requestOverlayAndStart() {
        if (!hasOverlayPermission()) {
            Toast.makeText(this,
                "ಮೊದಲು ಅನುಮತಿ ನೀಡಿ — 'Display over other apps' ಆನ್ ಮಾಡಿ",
                Toast.LENGTH_LONG).show();
            openOverlayPermissionSettings();
            return;
        }
        startOverlayService();
    }

    private void startOverlayService() {
        Intent intent = new Intent(this, LockScreenOverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        serviceRunning = true;
        updateUI();
        Toast.makeText(this, "ಕನ್ನಡ ಲಾಕ್ ಗಡಿಯಾರ ಪ್ರಾರಂಭ! 🕐", Toast.LENGTH_SHORT).show();
    }

    private void stopOverlayService() {
        Intent intent = new Intent(this, LockScreenOverlayService.class);
        stopService(intent);
        serviceRunning = false;
        updateUI();
        Toast.makeText(this, "ಗಡಿಯಾರ ನಿಲ್ಲಿಸಲಾಗಿದೆ", Toast.LENGTH_SHORT).show();
    }

    // ─── UI State ────────────────────────────────────────────

    private void updateUI() {
        boolean hasPermission = hasOverlayPermission();

        // Permission button
        if (hasPermission) {
            btnPermission.setText("✓ ಅನುಮತಿ ಇದೆ");
            btnPermission.setEnabled(false);
            btnPermission.setAlpha(0.5f);
        } else {
            btnPermission.setText("⚙ ಅನುಮತಿ ನೀಡಿ (Overlay Permission)");
            btnPermission.setEnabled(true);
            btnPermission.setAlpha(1.0f);
        }

        // Start/Stop buttons
        btnStart.setEnabled(hasPermission && !serviceRunning);
        btnStart.setAlpha((hasPermission && !serviceRunning) ? 1.0f : 0.4f);

        btnStop.setEnabled(serviceRunning);
        btnStop.setAlpha(serviceRunning ? 1.0f : 0.4f);

        // Status
        if (!hasPermission) {
            statusText.setText("⚠ ಮೊದಲು 'Display over other apps' ಅನುಮತಿ ನೀಡಿ");
            statusText.setTextColor(0xFFFF9800);
        } else if (serviceRunning) {
            statusText.setText("✓ ಕನ್ನಡ ಗಡಿಯಾರ ಲಾಕ್ ಸ್ಕ್ರೀನ್‌ನಲ್ಲಿ ಚಾಲನೆಯಲ್ಲಿದೆ");
            statusText.setTextColor(0xFF4CAF50);
        } else {
            statusText.setText("ಗಡಿಯಾರ ನಿಲ್ಲಿಸಲಾಗಿದೆ. ಪ್ರಾರಂಭಿಸಲು ಕೆಳಗೆ ಒತ್ತಿ.");
            statusText.setTextColor(0xFFBBBBBB);
        }
    }
}
