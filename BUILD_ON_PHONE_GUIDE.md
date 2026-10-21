# AFM — How to Build Your APK Completely on Your Android Phone (Zero Cost, Zero PC)

Welcome Amanuel! You do **not** need an expensive computer or paid tools to turn this project into an installable Android APK on your phone.

Here are the two simplest methods:

---

## METHOD 1: Free Automated Cloud Build (Easiest — 2 Minutes on Phone)

You can use GitHub Actions for 100% free cloud compilation straight from your Android phone's web browser:

1. **Download the Project ZIP**:
   - In AFM, tap the **Download Complete Android Project (.zip)** button.
   - It downloads `AFM_Android_Project.zip` to your phone's Downloads folder.

2. **Create a Free GitHub Account**:
   - Open Chrome on your phone, go to [github.com](https://github.com) and create an account or sign in.
   - Tap **New Repository**, name it `amanuel-finance-manager`, and make it Private or Public.

3. **Upload the Files**:
   - Extract the downloaded ZIP file using your phone's Files app.
   - On GitHub, tap **Add file -> Upload files** and select the project files.
   - Commit the files.

4. **Automatic APK Build**:
   - The included `.github/workflows/build-apk.yml` automatically activates!
   - Tap the **Actions** tab on your repository.
   - You will see the build running (takes ~2 minutes).
   - When finished with a green checkmark, tap into the run and download **AFM-Amanuel-Finance-Manager-Debug-APK**.
   - Tap the downloaded `app-debug.apk` on your phone and tap **Install**!

---

## METHOD 2: Direct Local Build on Phone via Free Termux App

If you want to compile Kotlin code offline directly inside Android:

1. Install **Termux** from F-Droid (free, open source terminal for Android).
2. Open Termux and run:
   ```bash
   pkg update && pkg install openjdk-17 git wget -y
   ```
3. Unzip your AFM project into Termux and run:
   ```bash
   chmod +x gradlew
   ./gradlew assembleDebug
   ```
4. Your finished APK will be in:
   `app/build/outputs/apk/debug/app-debug.apk`
   Open and install it directly!

---

## METHOD 3: Progressive Web App (PWA) Instant Install

You can also install AFM right now without compiling anything:
- Tap **Install App** in the top bar.
- AFM will be added directly to your phone's home screen with the AFM app icon and work 100% offline with full data persistence!
