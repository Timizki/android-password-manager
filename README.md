# Android Salasanahallinta

Turvallinen Android-sovellus salasanojen hallintaan, joka tarjoaa modernin käyttöliittymän ja vahvan tietoturvan.

## Ominaisuudet

### 🔐 Tietoturva
- **AES-256 salaus**: Kaikki salasanat salataan AES-256-algoritmilla
- **Biometrinen tunnistautuminen**: Sormenjälki- ja kasvojentunnistus
- **Pääsalasana**: Vahva pääsalasana suojaa kaikkia tietoja
- **Turvallinen tallennustila**: Android Keystore ja EncryptedSharedPreferences

### 📱 Käyttöliittymä
- **Material Design 3**: Moderni ja intuitiivinen käyttöliittymä
- **Jetpack Compose**: Nopea ja sujuva käyttökokemus
- **Tumma teema**: Automaattinen tumman teeman tuki
- **Hakutoiminto**: Nopea salasanojen haku ja suodatus

### 🛠️ Toiminnot
- **CRUD-toiminnot**: Lisää, muokkaa, poista ja hae salasanoja
- **Kategoriat**: Järjestä salasanat kategorioittain
- **Salasanageneraattori**: Luo turvallisia salasanoja automaattisesti
- **Kopioi leikepöydälle**: Nopea käyttäjänimen ja salasanan kopiointi
- **Muistiinpanot**: Lisätietoja salasanoille

## Tekninen toteutus

### Arkkitehtuuri
- **MVVM (Model-View-ViewModel)**: Selkeä arkkitehtuuri
- **Clean Architecture**: Kerrostettu rakenne
- **Dependency Injection**: Hilt-kirjasto
- **Reactive Programming**: Kotlin Coroutines ja Flow

### Teknologiat
- **Kotlin**: Ohjelmointikieli
- **Jetpack Compose**: Käyttöliittymä
- **Room Database**: Paikallinen tietokanta
- **Hilt**: Dependency injection
- **Navigation Compose**: Navigointi
- **Biometric API**: Biometrinen tunnistautuminen
- **Security Crypto**: Salaus

### Tietokannan rakenne
```kotlin
@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val website: String,
    val username: String,
    val encryptedPassword: String,
    val notes: String,
    val category: String,
    val createdAt: Long,
    val updatedAt: Long
)
```

## Asennus ja käyttö

### Vaatimukset
- Android 7.0 (API level 24) tai uudempi
- Biometrinen tunnistautuminen vaatii tuetun laitteen

### Käyttöönotto
1. Asenna sovellus Android-laitteeseen
2. Luo pääsalasana ensimmäisellä käynnistyskerralla
3. Valitse haluatko käyttää biometristä tunnistautumista
4. Aloita salasanojen lisääminen

### Käyttö
1. **Kirjautuminen**: Syötä pääsalasana tai käytä biometristä tunnistautumista
2. **Salasanan lisääminen**: Paina + -painiketta ja täytä tiedot
3. **Salasanan muokkaaminen**: Napauta salasanaa listasta
4. **Haku**: Käytä hakukenttää löytääksesi salasanoja nopeasti
5. **Kategoriat**: Suodata salasanoja kategorioittain

## Tietoturva

### Salausmenetelmät
- **AES-256-GCM**: Symmetrinen salaus salasanoille
- **PBKDF2**: Pääsalasanan hajautus
- **Android Keystore**: Salausavainten turvallinen säilytys
- **EncryptedSharedPreferences**: Asetusten salaus

### Tietosuoja
- Kaikki tiedot tallennetaan paikallisesti laitteeseen
- Ei verkkoyhteyksiä tai pilvipalveluja
- Ei analytiikkaa tai seurantaa
- Avoimen lähdekoodin toteutus

## Kehitys

### Projektin rakenne
```
app/src/main/java/com/passwordmanager/
├── data/
│   ├── database/          # Room-tietokanta
│   └── repository/        # Tietojen hallinta
├── domain/
│   ├── model/            # Tietomallit
│   ├── repository/       # Repository-rajapinnat
│   └── usecase/          # Liiketoimintalogiikka
├── presentation/
│   ├── auth/             # Tunnistautuminen
│   ├── main/             # Pääsivun näkymä
│   ├── add_edit/         # Salasanan lisäys/muokkaus
│   ├── components/       # Uudelleenkäytettävät komponentit
│   └── navigation/       # Navigointi
├── utils/                # Apuluokat
└── di/                   # Dependency injection
```

### Testaus
- Yksikkötestit ViewModeleille
- Integraatiotestit tietokannalle
- UI-testit Compose-komponenteille

## Lisenssi

Tämä projekti on lisensoitu MIT-lisenssillä. Katso [LICENSE](LICENSE) tiedosto lisätietoja varten.

## Tekijä

Kehitetty OpenHands AI:n avulla Android-salasanojen hallintasovellukseksi.