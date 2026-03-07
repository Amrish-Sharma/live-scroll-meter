# Live Scroll Meter 📱✨

**Live Scroll Meter** is a productivity and usage-tracking Android application designed to help users monitor their social media consumption in real-time. It features a lightweight, floating overlay that tracks **session time** and **swipe counts** specifically for "scroll-heavy" apps like **YouTube Shorts**, **Instagram Reels**, and **X (Twitter)**.

---

## 🚀 Features

*   **Floating Overlay Bubbles**: Two small, draggable, non-intrusive bubbles that stay on top of supported apps.
    *   **Bubble 1**: Real-time session timer (e.g., `12m` or `05:30`).
    *   **Bubble 2**: Swipe counter (e.g., `↑ 42`) to track how many videos or posts you've scrolled through.
*   **Auto-Detection**: The overlay automatically appears when you open YouTube, Instagram, or X and hides when you leave them.
*   **Lifetime Statistics**: Uses a local **Room Database** to persist your total usage time and total swipe count across all sessions.
*   **Modern UI**: Built entirely with **Jetpack Compose** for a smooth, reactive user experience.
*   **Lightweight**: Designed to run efficiently in the background without impacting device performance.

---

## 🛠 Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (including Overlay UI via `WindowManager`)
*   **Architecture**: Clean Architecture (UI, Domain, Data)
*   **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
*   **Database**: [Room](https://developer.android.com/training/data-storage/room) (Persistence)
*   **Background Services**:
    *   `AccessibilityService`: To detect foreground app changes and swipe gestures.
    *   `OverlayService`: To manage the lifecycle of the floating windows.
*   **Processing**: [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html) for faster builds.

---

## 🔒 Permissions Required

To function correctly, the app requires two sensitive permissions:

1.  **Display Over Other Apps (`SYSTEM_ALERT_WINDOW`)**: Required to show the floating bubbles while you are using other applications.
2.  **Accessibility Service (`BIND_ACCESSIBILITY_SERVICE`)**: Required to:
    *   Detect which app is currently in the foreground.
    *   Increment the swipe count by detecting specific scroll gestures in supported apps.

> **Note**: This app does *not* collect or transmit any personal data. All tracking is performed locally on your device.

---

## 📦 Installation & Setup

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/amris/LiveScrollMeter.git
    ```
2.  **Open in Android Studio**: (Ladybug or newer recommended).
3.  **Sync Gradle**: Ensure all dependencies are downloaded via the Version Catalog (`libs.versions.toml`).
4.  **Build & Run**: Deploy to a device running **Android 8.0 (API 26)** or higher.

---

## 📖 Usage

1.  Launch the app and grant the **Overlay Permission** and **Accessibility Permission** when prompted.
2.  The main dashboard will show your "Total Lifetime Swipes" and "Total Time Spent."
3.  Open **YouTube Shorts**, **Instagram Reels**, or **X**.
4.  The floating bubbles will appear. You can **drag them** to any position on the screen.
5.  Watch the numbers update in real-time as you scroll!
6.  Close the target apps or return to the Dashboard to see your updated lifetime stats.

---

## 🏗 Project Structure

```
com.cb.apps.livescrollmeter
├── core            # Common utilities and base classes
├── data            # Room Entities, DAOs, and Database configuration
├── di              # Hilt Modules for dependency injection
├── domain          # Business logic, SessionManager, and UseCases
├── service
│   ├── accessibility # ScrollDetection logic
│   └── overlay       # WindowManager and Overlay lifecycle
└── ui
    ├── main          # App Dashboard and Permission screens
    ├── overlay       # Compose UI for the floating bubbles
    └── theme         # Design system (Color, Type, Shape)
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an issue for any bugs or feature requests.

---

## 📄 License

This project is licensed under the **MIT License**. See the `LICENSE` file for details.
