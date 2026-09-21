# 黒 Kuro Launcher | Minimal AF Launcher

> **AF stands for Ad-Free & Distraction-Free.**  
> A hyper-minimalist, pure-pitch-black AMOLED Android launcher with zero telemetry, zero bloat, and lightning-fast navigation.

---

> [!NOTE]
> ### 🌸 Upstream Attribution & Credits
> **Kuro Launcher** is an enhanced, dark-purist fork of the renowned [**Olauncher**](https://github.com/tanujnotes/Olauncher) originally crafted by **[Tanuj (@tanujnotes)](https://github.com/tanujnotes)**.  
> All core minimalist design principles originated from Tanuj's exceptional vision. Deep gratitude and respect to Tanuj for building one of the cleanest open-source Android projects in existence.  
> - **Original Repository**: [github.com/tanujnotes/Olauncher](https://github.com/tanujnotes/Olauncher)  
> - **Original Author**: [Tanuj (@tanujnotes)](https://x.com/tanujnotes) • [Website](https://tanujnotes.substack.com)

---

## ⚡ Why Kuro Launcher?

While preserving the core elegance of Olauncher, Kuro Launcher introduces dark-mode enhancements, security features, and tactile polish for modern OLED displays:

```
                      ┌─────────────────────────────┐
                      │      黒 Kuro Launcher       │
                      └──────────────┬──────────────┘
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         ▼                           ▼                           ▼
┌───────────────────┐       ┌───────────────────┐       ┌───────────────────┐
│   Pure AMOLED     │       │    Smart Search   │       │ Biometric Privacy │
│ 4K 1-bit Black    │       │ Fuzzy + Acronyms  │       │ PIN / Fingerprint │
│ Custom Wallpaper  │       │ Quick-Clear (✕)   │       │ Hidden App Lock   │
└───────────────────┘       └───────────────────┘       └───────────────────┘
```

---

## 🌟 Enhanced Features

| Feature | Description |
| :--- | :--- |
| 🌌 **4K Pitch-Black Default** | Bundled with an authentic 4K 1-bit `#000000` wallpaper out-of-the-box for maximum battery savings on OLED/AMOLED screens. |
| 🖼️ **Custom Wallpaper Support** | Choose your own custom wallpaper directly from your device gallery without external manager apps. |
| ⚡ **Fuzzy & Acronym Search** | Type initials or quick abbreviations (e.g. `cgpt` → `ChatGPT`, `yt` → `YouTube`, `ps` → `Play Store`) or loose fuzzy queries. |
| ✕ **Quick-Clear Search** | Dedicated clear icon `✕` and smart Back-press gesture that clears typed text before exiting the drawer. |
| 🔒 **Biometric / PIN Protection** | Safeguard hidden apps behind native Android fingerprint, face unlock, or device PIN authentication. |
| 🔤 **Text Case Customization** | Switch home screen and drawer text styles between **Default**, **lowercase**, or **UPPERCASE**. |
| 📳 **Tactile Haptic Feedback** | Subtle haptic vibrations for home app launches and swipe gestures (toggleable in Settings). |
| 🖤 **Always Dark Mode** | Permanently optimized for night and dark themes; zero light-mode flashes or unneeded theme restarts. |
| 🧹 **Debloated & Privacy-First** | Stripped out background SQLite polling, periodic restart timers, review nags, promo banners, and external links. |

---

## 🚀 Installation & Downloads

Grab the latest compiled APK from the official [GitHub Releases](https://github.com/Praveensenpai/KuroLauncher/releases):

```bash
# Direct download via GitHub CLI
gh release download v0.2.0 --repo Praveensenpai/KuroLauncher --pattern "*.apk"
```

Or download directly from your browser:
👉 **[Download Kuro Launcher v0.2.0 APK](https://github.com/Praveensenpai/KuroLauncher/releases/latest)**

---

## 🛠️ Building from Source

Ensure you have **JDK 17** installed:

```bash
# Clone the repository
git clone https://github.com/Praveensenpai/KuroLauncher.git
cd KuroLauncher

# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease
```

The release APK will be generated at `app/build/outputs/apk/release/app-release.apk`.

---

## 📜 License

Kuro Launcher is licensed under the [GNU General Public License v3.0 (GPL-3.0)](LICENSE) in accordance with the upstream Olauncher project.
