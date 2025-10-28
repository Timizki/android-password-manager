# 📱 APK-paketin tuottaminen

## ⭐ Vaihtoehto 1: Android Studio (SUOSITELTU)

### Asennus ja käyttöönotto
1. **Lataa Android Studio**: https://developer.android.com/studio
2. **Kloonaa projekti**:
   ```bash
   git clone https://github.com/Timizki/android-password-manager.git
   cd android-password-manager
   ```
3. **Avaa projekti**: File → Open → Valitse projektin kansio
4. **Synkronoi**: Android Studio synkronoi automaattisesti Gradle-tiedostot
5. **Odota**: Ensimmäinen synkronointi voi kestää useita minuutteja

### Debug APK (Testikäyttöön)
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. APK löytyy: `app/build/outputs/apk/debug/app-debug.apk`
3. Siirrä APK Android-laitteelle ja asenna

### Release APK (Tuotantokäyttöön)
1. **Luo signing key**:
   ```bash
   keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Luo `app/keystore.properties`**:
   ```properties
   storePassword=your_store_password
   keyPassword=your_key_password
   keyAlias=alias_name
   storeFile=../my-release-key.keystore
   ```

3. **Build → Generate Signed Bundle / APK**
4. Valitse APK, valitse keystore, build release

## Vaihtoehto 2: Komentorivi (Gradle)

### Debug APK
```bash
cd /workspace/project
./gradlew assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK
```bash
cd /workspace/project
./gradlew assembleRelease
```
APK: `app/build/outputs/apk/release/app-release.apk`

## 🚀 Vaihtoehto 3: GitHub Actions (AUTOMAATTINEN)

**Hyvä uutinen!** Projekti sisältää jo valmiin GitHub Actions -workflown!

### Automaattinen APK-buildi:
1. **Jokainen push master-branchiin** → Debug APK
2. **Git tag (esim. v1.0.0)** → Debug + Release APK + GitHub Release

### Lataa valmis APK:
1. Mene: https://github.com/Timizki/android-password-manager/actions
2. Valitse viimeisin "Build APK" workflow
3. Lataa "app-debug-apk" artifacts-osiosta

### Luo uusi release:
```bash
git tag v1.0.0
git push origin v1.0.0
```
→ Automaattisesti luo GitHub Releasen APK-tiedostoilla!

## Asennus Android-laitteelle

### Vaihtoehdot:
1. **ADB (Android Debug Bridge)**:
   ```bash
   adb install app-debug.apk
   ```

2. **Suora asennus**:
   - Siirrä APK laitteelle (USB/email/cloud)
   - Salli "Tuntemattomista lähteistä" asennus
   - Napauta APK-tiedostoa

3. **Google Play Console** (Release APK):
   - Luo kehittäjätili
   - Lataa signed APK
   - Julkaise sovellus

## Autofill-palvelun testaus

### Käyttöönotto laitteella:
1. Asenna APK
2. Mene: **Asetukset → Järjestelmä → Kielet ja syöttö → Automaattinen täyttö**
3. Valitse "Salasanahallinta" palveluksi
4. Testaa kirjautumalla toiseen sovellukseen

### Testisovellukset:
- Chrome-selain
- Gmail
- Facebook
- Instagram
- Mikä tahansa kirjautumislomake

## Vianmääritys

### Gradle-ongelmat:
```bash
# Puhdista build
./gradlew clean

# Päivitä Gradle Wrapper
./gradlew wrapper --gradle-version 8.2
```

### Android SDK puuttuu:
1. Asenna Android Studio
2. SDK Manager → Lataa tarvittavat SDK:t
3. Aseta ANDROID_HOME ympäristömuuttuja

### Signing-ongelmat:
- Varmista keystore.properties polut
- Tarkista salasanat
- Luo uusi keystore tarvittaessa

## Suorituskyvyn optimointi

### ProGuard/R8 (Release):
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### APK-koon pienentäminen:
- Käytä vector drawables
- Optimoi kuvat
- Poista käyttämättömät resurssit
- Käytä App Bundle (.aab) APK:n sijaan