# Timer - Stopwatch App

A modern, precise stopwatch application for Android built with Jetpack Compose and Material Design 3.

## Features

- **Precise Stopwatch**: Counts up from 00:00:00 with centisecond precision using `SystemClock.elapsedRealtime()`
- **Start / Pause / Reset**: Full control with smooth animations
- **Lap Times**: Record intermediate times with split and total columns
- **Best/Worst Indicators**: Green highlight for best lap, red for worst
- **Dark/Light Theme**: Toggle between themes with a single tap
- **Landscape Support**: Responsive layout adapts to screen orientation
- **Haptic Feedback**: Vibration on reset

## Architecture

- **MVVM** with `ViewModel` + `StateFlow`
- **Jetpack Compose** for declarative UI
- **Kotlin Coroutines** for timer precision (no `Thread.sleep()`)
- **Material Design 3** color scheme and components

## Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Requirements

- Android 8.0+ (API 26)
- Android Studio Hedgehog or later (for development)
