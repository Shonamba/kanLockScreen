# 🕐 Kannada Lock Clock

A lightweight Android lock screen overlay that displays the current time
in **Kannada (ಕನ್ನಡ)** numerals and script.

## Features
- 🔤 Time displayed in Kannada numerals
- 🔒 Shows on lock screen as an overlay
- 🔄 Auto-starts on device boot
- 🎨 Beautiful gradient background

## Screenshots
<!-- Add screenshots here -->

## Permissions Required
- `SYSTEM_ALERT_WINDOW` — Draw overlay on lock screen
- `RECEIVE_BOOT_COMPLETED` — Restart service after reboot
- `FOREGROUND_SERVICE` — Keep clock service running

## Building
```bash
./gradlew assembleDebug
