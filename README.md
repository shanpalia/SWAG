# SWAG — Professional Video Editor

Android-native Kotlin + Jetpack Compose starter project.

## Home screen
- SWAG branding with PRO, notifications and profile actions
- Neon purple/magenta “Edit Like a Pro” hero banner
- New Project call-to-action
- 10 quick editing tool tiles
- AI Studio and Templates feature cards
- My Projects preview tiles
- Custom bottom navigation with central create button

## Build
Requires JDK 17, Android SDK 36 and Gradle 8.13.

Run:
```bash
gradle :app:assembleDebug
```

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

## Current scope
This is the UI-first foundation. Project preview tiles are placeholders. Real video import, timeline playback, editing operations, project persistence, AI processing and MP4 export still need implementation.
