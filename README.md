# Recording App

Recording App is a small Android application built with **Kotlin** and **Jetpack Compose**. It demonstrates three common mobile-device capabilities:

- Recording and playing back audio using the microphone
- Capturing photos with the device camera
- Viewing and deleting photos saved by the app

The project uses the Android CameraX libraries, Material 3, Coil for image loading, and Compose Navigation.

## Features

### Audio recording

The **Audio** screen lets the user:

- Request microphone permission
- Start and stop audio recording
- Play back the most recent recording
- See whether recording or playback is currently active

Audio is recorded to a temporary `record.3gp` file in the app’s external cache directory using Android’s `MediaRecorder`. Playback uses `MediaPlayer`.

### Camera capture

The **Image** screen provides:

- A live camera preview using CameraX
- Photo capture with the rear camera
- Automatic saving of captured JPEG images into the device media store under `Pictures/RecordingApp`
- Navigation to the gallery after a photo is captured

The app requires a device with a camera; the manifest declares the camera feature as required.

### Photo gallery

The **Gallery** screen:

- Loads images stored in `Pictures/RecordingApp`
- Displays them in a grid using Coil
- Shows photo names and capture dates
- Allows individual photos to be deleted from the media store

## Tech stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM-style ViewModels with Kotlin `StateFlow` |
| Navigation | Jetpack Compose Navigation |
| Camera | AndroidX CameraX |
| Audio | `MediaRecorder` and `MediaPlayer` |
| Image loading | Coil Compose |
| Build system | Gradle Kotlin DSL |

## Project structure

```text
app/src/main/java/com/example/recordingapp/
├── MainActivity.kt
├── audio/
│   ├── AudioScreen.kt
│   └── AudioViewModel.kt
├── gallery/
│   ├── GalleryScreen.kt
│   └── GalleryViewModel.kt
├── image/
│   ├── ImageCaptureHelper.kt
│   ├── ImageScreen.kt
│   └── ImageViewModel.kt
├── navigation/
│   ├── AppNavigation.kt
│   └── Routes.kt
└── ui/
    └── theme/
```

- `MainActivity.kt` sets up the Compose UI and navigation host.
- `audio/` contains the microphone recording and playback feature.
- `image/` contains the camera preview and photo-capture feature.
- `gallery/` contains the saved-photo gallery and deletion logic.
- `navigation/` defines the app routes and screen transitions.

## Requirements

- Android Studio with support for Android SDK 36
- An Android device or emulator running Android 14 (API 34) or newer
- A device with a camera and microphone for full functionality

The application targets:

- Minimum SDK: 34
- Target SDK: 36
- Java compatibility: Java 11

## Getting started

1. Clone the repository:

   ```bash
   git clone https://github.com/torwanat/recording-app.git
   cd recording-app
   ```

2. Open the project in Android Studio.

3. Let Gradle sync and download the required dependencies.

4. Connect an Android device or start an emulator.

5. Run the app:

   ```bash
   ./gradlew installDebug
   ```

   On Windows:

   ```bash
   gradlew.bat installDebug
   ```

## Permissions

The app requests the following runtime permissions:

| Permission | Purpose |
|---|---|
| `RECORD_AUDIO` | Recording audio through the microphone |
| `CAMERA` | Displaying the camera preview and taking photos |

The manifest also declares that a camera is a required hardware feature.

## App navigation

The application has three screens:

```text
Audio → Image → Gallery
```

- **Audio** is the starting screen.
- **Image** is reached from the Audio screen.
- **Gallery** is reached from the Image screen.
- Back navigation returns to the previous screen.

## Notes and possible improvements

- Audio recordings are currently stored in the external cache directory and use a fixed filename, so each new recording replaces the previous one.
- The audio recording uses the AMR-NB encoder and 3GP container format, which is simple but not ideal for high-quality recordings.
- Photo deletion may trigger an Android system confirmation dialog depending on the device and Android version.
- Future improvements could include persistent recording history, audio file naming by timestamp, video recording, and improved error handling.

## License

No license has been specified yet.
