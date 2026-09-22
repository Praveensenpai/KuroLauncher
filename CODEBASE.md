# CODEBASE.md: KuroLauncher Semantic Digest

> **Notice**: This file is an AI-optimized semantic index. Do not write narrative prose. Keep token density high.

## 1. System Topology & Data Flow
```text
Android OS (Home Intent / Window Insets)
  └─> MainActivity (Single Activity Host, Navigation Graph, System UI Controller)
        ├─> NavController (res/navigation/nav_graph.xml)
        │     ├─> HomeFragment (Minimal Text Apps, Gestures, Clock/Date Header)
        │     ├─> AppDrawerFragment (Alphabetical App List, Search Bar, Fast Scroll)
        │     │     └─> AppDrawerAdapter (App Rows, Section Headers, Private Space)
        │     └─> SettingsFragment (Text Tone, Alignment, Hidden Apps, Gestures)
        └─> MainViewModel (Shared State, App Discovery, Search Filter, Usage Stats)
              ├─> LauncherApps / PackageManager (Application Discovery & Launch)
              ├─> AppFilterHelper (Fast Substring & Subsequence Character Search)
              ├─> Prefs (SharedPreferences Encapsulation)
              └─> AppUsageStats / UsageStatsManager (Digital Wellbeing Tracking)
```

## 2. Global Constraints & Architecture Patterns
- **Primary Language & Target**: Kotlin 2.0 / Java 17 target / Android SDK 36 (minSdk 24)
- **Build Tooling**: Gradle 8.11.1 (Wrapper), Android Gradle Plugin 8.8.x
- **Architectural Paradigm**: Single-Activity Jetpack Navigation + MVVM (MainViewModel + LiveData/StateFlow) + SharedPreferences
- **UI Paradigm**: Android View Binding (`ActivityMainBinding`, `FragmentHomeBinding`, etc.) with zero Jetpack Compose overhead
- **Design Philosophy**: Monochrome distraction-free launcher, pure pitch-black AMOLED styling (`#000000`), zero analytics/telemetry, offline-first
- **Distribution**: Standalone signed release APK via GitHub Releases

## 3. Module & Interface Skeleton

### `app/src/main/java/com/paisen/kurolauncher/MainActivity.kt` (Role: api, Lines: ~306)
- **Responsibility**: Top-level single Activity host, navigation back stack manager, window inset handler, and package broadcast receiver.
- **Imports**: `androidx.appcompat.app.AppCompatActivity`, `androidx.navigation.NavController`, `com.paisen.kurolauncher.MainViewModel`, `com.paisen.kurolauncher.data.Prefs`
- **Types & Enums**:
  ```kotlin
  class MainActivity : AppCompatActivity()
  ```
- **Public / Key Functions**:
  ```kotlin
  fun navigateToHome()
  fun navigateToDrawer()
  fun navigateToSettings()
  fun reloadLauncher()
  ```
- **Consumers**: Android OS Launcher Intent (`android.intent.action.MAIN` + `CATEGORY_HOME`).
- **Side Effects / I/O**: Modifies Window flags (`FLAG_LAYOUT_NO_LIMITS`), registers `packageChangeReceiver` broadcast receiver, reads/writes `Prefs`.

### `app/src/main/java/com/paisen/kurolauncher/MainViewModel.kt` (Role: domain, Lines: ~513)
- **Responsibility**: State holder for installed applications, filtered search results, hidden apps, home apps, and digital wellbeing screen time.
- **Imports**: `androidx.lifecycle.ViewModel`, `androidx.lifecycle.LiveData`, `androidx.lifecycle.MutableLiveData`, `com.paisen.kurolauncher.data.AppModel`
- **Types & Enums**:
  ```kotlin
  class MainViewModel(application: Application) : AndroidViewModel(application)
  ```
- **Public Functions & Signatures**:
  ```kotlin
  fun loadApps()
  fun filterApps(query: String)
  fun updateHomeApps(apps: List<AppModel>)
  fun hideApp(appModel: AppModel)
  fun unhideApp(appModel: AppModel)
  fun getAppUsageStats(): LiveData<List<AppUsageStats>>
  ```
- **Consumers**: `HomeFragment`, `AppDrawerFragment`, `SettingsFragment`, `MainActivity`.
- **Side Effects / I/O**: Queries `LauncherApps`, queries `UsageStatsManager`, modifies `Prefs`.

### `app/src/main/java/com/paisen/kurolauncher/data/AppModel.kt` (Role: domain, Lines: ~48)
- **Responsibility**: Core domain models for installed applications, search filtering states, and app drawer items.
- **Types & Enums**:
  ```kotlin
  data class AppModel(val appLabel: String, val appPackage: String, val activityName: String, val userHandle: UserHandle)
  data class AppFilterModel(val app: AppModel, val matchedIndices: List<Int>)
  data class DrawerSearchModel(val app: AppModel, val isHeader: Boolean, val headerChar: Char?)
  ```
- **Consumers**: `MainViewModel`, `AppDrawerAdapter`, `HomeFragment`, `AppFilterHelper`.

### `app/src/main/java/com/paisen/kurolauncher/data/Prefs.kt` (Role: infra, Lines: ~680)
- **Responsibility**: Typed SharedPreferences abstraction managing user settings (theme, text tone, gestures, hidden apps, home app slots).
- **Types & Enums**:
  ```kotlin
  class Prefs(context: Context) {
      var textTone: Int
      var appAlignment: Int
      var homeAppsNum: Int
      var showStatusBar: Boolean
      var autoShowKeyboard: Boolean
      var hiddenApps: Set<String>
  }
  ```
- **Consumers**: Entire application (`MainViewModel`, `HomeFragment`, `SettingsFragment`, `AppDrawerAdapter`).
- **Side Effects / I/O**: Reads and writes XML preference store in Android private app storage.

### `app/src/main/java/com/paisen/kurolauncher/data/Constants.kt` (Role: domain, Lines: ~129)
- **Responsibility**: Static constant definitions for gesture actions, preference keys, bundle arguments, and intent actions.
- **Types & Enums**:
  ```kotlin
  object Constants {
      const val ACTION_NONE = 0
      const val ACTION_OPEN_APP = 1
      const val ACTION_LOCK_SCREEN = 2
      const val ACTION_OPEN_NOTIFICATIONS = 3
  }
  ```
- **Consumers**: `HomeFragment`, `SettingsFragment`, `OnSwipeTouchListener`.

### `app/src/main/java/com/paisen/kurolauncher/ui/HomeFragment.kt` (Role: api, Lines: ~755)
- **Responsibility**: Displays clean home screen: user-configured text shortcuts, live clock, date, calendar touch targets, and swipe gestures.
- **Imports**: `com.paisen.kurolauncher.ui.BaseFragment`, `com.paisen.kurolauncher.databinding.FragmentHomeBinding`, `com.paisen.kurolauncher.listener.OnSwipeTouchListener`
- **Types & Enums**:
  ```kotlin
  class HomeFragment : BaseFragment<FragmentHomeBinding>()
  ```
- **Public Functions & Signatures**:
  ```kotlin
  fun setupHomeApps()
  fun applyTextTone(tonePercent: Int)
  fun updateDateTime()
  ```
- **Consumers**: `NavController` (home destination).
- **Side Effects / I/O**: Launches application intents, invokes system alarm/calendar intents, manages screen lock via DevicePolicyManager.

### `app/src/main/java/com/paisen/kurolauncher/ui/AppDrawerFragment.kt` (Role: api, Lines: ~400)
- **Responsibility**: Full-screen vertical scrolling app drawer featuring fuzzy search input, automatic keyboard focus, and fast scroll index.
- **Imports**: `com.paisen.kurolauncher.ui.BaseFragment`, `com.paisen.kurolauncher.databinding.FragmentAppDrawerBinding`, `com.paisen.kurolauncher.ui.AppDrawerAdapter`
- **Types & Enums**:
  ```kotlin
  class AppDrawerFragment : BaseFragment<FragmentAppDrawerBinding>()
  ```
- **Public Functions & Signatures**:
  ```kotlin
  fun initSearch()
  fun initAdapter()
  fun handleFastScroll(letter: Char)
  ```
- **Consumers**: `NavController` (drawer destination).
- **Side Effects / I/O**: Controls Android InputMethodManager (keyboard), launches apps on single search match.

### `app/src/main/java/com/paisen/kurolauncher/ui/AppDrawerAdapter.kt` (Role: api, Lines: ~459)
- **Responsibility**: RecyclerView adapter for app drawer items, alphabetical section headers, and Android Private Space collapsible container.
- **Imports**: `androidx.recyclerview.widget.ListAdapter`, `androidx.recyclerview.widget.DiffUtil`, `com.paisen.kurolauncher.data.AppModel`
- **Types & Enums**:
  ```kotlin
  class AppDrawerAdapter(private val clickListener: (AppModel) -> Unit) : ListAdapter<AppModel, RecyclerView.ViewHolder>(DIFF_CALLBACK)
  ```
- **Consumers**: `AppDrawerFragment`.
- **Side Effects / I/O**: Displays context popup menu for app info, uninstall, rename, and hide actions.

### `app/src/main/java/com/paisen/kurolauncher/ui/SettingsFragment.kt` (Role: api, Lines: ~715)
- **Responsibility**: Comprehensive settings screen: text tone presets (Soft White, Pure White, Muted Grey), alignment, gestures, hidden apps, wallpaper.
- **Imports**: `com.paisen.kurolauncher.ui.BaseFragment`, `com.paisen.kurolauncher.databinding.FragmentSettingsBinding`, `com.paisen.kurolauncher.data.Prefs`
- **Types & Enums**:
  ```kotlin
  class SettingsFragment : BaseFragment<FragmentSettingsBinding>()
  ```
- **Consumers**: `NavController` (settings destination).
- **Side Effects / I/O**: Writes `Prefs`, sets pitch-black AMOLED wallpaper via `WallpaperManager`.

### `app/src/main/java/com/paisen/kurolauncher/helper/Utils.kt` (Role: infra, Lines: ~779)
- **Responsibility**: Global utility functions for Android system interactions (launching apps, opening system settings, status bar toggles).
- **Public Functions & Signatures**:
  ```kotlin
  fun openApp(context: Context, appModel: AppModel)
  fun openAppInfo(context: Context, packageName: String)
  fun openDialer(context: Context)
  fun openCamera(context: Context)
  fun lockScreen(context: Context)
  fun isEinkDisplay(): Boolean
  ```
- **Consumers**: `HomeFragment`, `AppDrawerFragment`, `SettingsFragment`, `MainActivity`.
- **Side Effects / I/O**: Launches system intents, accesses `DevicePolicyManager` and `AccessibilityManager`.

### `app/src/main/java/com/paisen/kurolauncher/listener/OnSwipeTouchListener.kt` (Role: infra, Lines: ~112)
- **Responsibility**: Detects directional fling/swipe gestures (up, down, left, right) and double taps on home screen root view.
- **Types & Enums**:
  ```kotlin
  open class OnSwipeTouchListener(context: Context) : View.OnTouchListener
  ```
- **Consumers**: `HomeFragment`.

## 4. Execution Lifecycle Trace
1. **Startup**: OS triggers `MainActivity` via `ACTION_MAIN` / `CATEGORY_HOME`. Activity configures immersive window flags and navigation controller.
2. **State Initialization**: `MainViewModel` asynchronously queries `LauncherApps` for installed packages, applies user filters from `Prefs`, and exposes LiveData streams.
3. **Home Presentation**: `HomeFragment` renders active user app list, applies configured `textTone` (monochrome percentage), and sets gesture listeners.
4. **App Search & Launch**: User swipes up to enter `AppDrawerFragment`. Keyboard opens automatically, keystrokes invoke `filterApps()`. Single-match auto-launch optionally executes `openApp()`.
5. **Settings & Customization**: User long-presses home or navigates to `SettingsFragment` to customize gestures, tone, or hidden apps, persisting instantly via `Prefs`.

## 5. Verification Commands
```bash
# Build Debug APK
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew assembleDebug

# Build Release APK
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew assembleRelease

# Stream Install to Connected Phone via ADB
adb -s 192.168.1.36:39043 install -r app/build/outputs/apk/release/app-release.apk

# Lint Vital Check
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew lintVitalRelease
```

## 6. Recent Iteration Changes
- **2026-09-22**:
  - Migrated complete application identity and namespace from `app.olauncher` to `com.paisen.kurolauncher` (v0.3.0).
  - Relocated all Java/Kotlin source trees from `app/olauncher/` to `com/paisen/kurolauncher/`.
  - Updated all package declarations, imports, navigation manifests, and accessibility configurations.
  - Replaced legacy documentation with aesthetic centered Hero showcase in `README.md` following `aesthetic-readme-craft`.
  - Added dedicated `assets/logo.png` and 5 high-resolution phone screenshots (`assets/screenshots/`): Minimalist Home, Alphabetical Drawer, Fuzzy & Acronym Search, Settings, and Text Tone Presets.
- **2026-09-21**:
  - Added additional 10% margin to KuroLauncher icon assets across all mipmap density buckets (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).
  - Scaled Kanji glyph down proportionally: foreground `174px` → `157px` (`xxxhdpi`), launcher icon `102px` → `93px` (`xxxhdpi`).
  - Recompiled release APK with Java 21 and verified installation on target device (`192.168.1.36:39043`).
  - Created initial AI-first `CODEBASE.md` semantic index following Karakuri `codebase-digest` specification.
