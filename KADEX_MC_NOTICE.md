# Kade'X MC — unofficial modified version

Kade'X MC is an **Unofficial Modified Version** of Zalith Launcher 2
(https://github.com/ZalithLauncher/ZalithLauncher2), Copyright (C) 2025 MovTery and contributors,
licensed under GPL-3.0 (see `LICENSE`) with the Section 7 additional terms in `README_EN_US.md`.

It is not affiliated with or endorsed by the original developers, Mojang Studios, or Microsoft.
All original copyright notices, the LICENSE file, and third-party license notices are retained.
Complete corresponding source for any distributed APK must be provided under GPL-3.0.

## Changes made (stage 1: branding)
- ZalithLauncher/gradle.properties: launcher_name=KadeXMC, launcher_app_name=Kade'X MC, launcher_short_name=KXMC
- ZalithLauncher/build.gradle.kts: applicationId = "com.kadex.mc" (removed ".v2" suffix; namespace unchanged
  so Kotlin/JNI symbols keep working; debug builds install as com.kadex.mc.debug)
- Launcher icons: res/drawable-nodpi/ic_launcher_foreground.png, ic_launcher_monochrome.png,
  res/mipmap-*/ic_launcher(.png|_round.png), ic_launcher-playstore.png, res/values/ic_launcher_background.xml
  (old vector foreground/monochrome and legacy .webp icons removed — replaced by the above)
- res/drawable/img_launcher.png and res/drawable-nodpi/kadex_logo.png: in-app logo (About screen)
- strings.xml: unofficial_modified_label / unofficial_modified_notice
- ui/screens/main/MainScreen.kt: "Unofficial Modified Version" label under the toolbar title
- ui/screens/content/settings/AboutInfoScreen.kt: unofficial-modification disclaimer
- setting/AllSettings.kt: default color theme -> VERDANT_DAWN
- ui/theme/Color.kt: VERDANT_DAWN palette tuned to graphite surfaces with emerald/lime accents

## NOT done / unverified
- Nothing has been compiled: the sandbox had no Android SDK/NDK and no network. No APK exists.
- Dashboard/Play-button, per-screen redesign, splash redesign: not implemented.
- Logo derived from a JPEG, so transparency is approximate; supply the original PNG/SVG for production.

## Build (on a machine with Android Studio / SDK 34+, NDK 25.2.9519653, JDK 21, network)
    ./gradlew :ZalithLauncher:assembleDebug
