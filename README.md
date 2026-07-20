# Floating Companion

A sleek, glassmorphic floating desktop companion app built with Kotlin and JetBrains Compose Desktop. The app provides a convenient, globally accessible floating window that hosts various AI providers (ChatGPT, Claude, Gemini, Local AI) directly on your desktop.

## ✨ Features
- **Global Hotkey:** Toggle the floating companion from anywhere, across any app using a customizable global hotkey (default: `Ctrl + Space`).
- **Web Providers:** Drop-in WebView support for your favorite AI tools—no API keys required.
- **Customizable UI:** Draggable window with adjustable opacity and a modern dark theme.
- **Cross-Platform Foundation:** Built on Jetbrains Compose, currently optimized and packaged for Windows.

## 🚀 Getting Started (Development)

This project uses Gradle. If you are on Windows, we have provided helper scripts to make setup instant without needing to manually configure Java environments.

1. **Run the App:**
   Simply double-click `run_app.bat`. This will automatically set up a local JDK environment, download the Chromium embedded engine (KCEF) on first launch, and open the app.
   
2. **Build the Installer:**
   Double-click `build_exe.bat`. This will package the application and bundle the Java runtime into a standalone `.exe` installer inside the `build/compose/binaries/main/app/` (or `\exe\`) folder. You can distribute this `.exe` to users who do not have Java installed.

## 🛠️ Technologies Used
- Kotlin 2.1.21
- JetBrains Compose Multiplatform 1.8.2
- JNativeHook (Global Hotkeys)
- KevinnZou's Compose WebView Multiplatform (Embedded Chromium engine)

## 🤝 Contributing
Contributions are welcome! Feel free to open issues or submit pull requests to help improve the project.
