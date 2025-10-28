# Android Password Manager - PassTool Compatible

PassTool-yhteensopiva Android-salasanahallinta, joka ei tallenna salasanoja vaan generoi ne tarvittaessa.

## 🔄 TÄRKEÄ MUUTOS (Issue #1)

**Sovellus on refaktoroitu täysin uudenlaiseksi!**

### Ennen ❌
- Tallensi salatut salasanat tietokantaan
- Käytti AES-salausta
- Vaati master-salasanan

### Nyt ✅
- Tallentaa vain **salasanaprofiileja** (generointisäännöt)
- Generoi salasanat tarvittaessa PassTool-logiikalla
- Yhteensopiva PassTool bash-skriptin kanssa

---

## 🛠️ PassTool-yhteensopivuus

Sovellus käyttää samaa logiikkaa kuin PassTool bash-skripti:

```bash
# PassTool-logiikka
echo -n "$passphrase" | sha256sum | cut -d' ' -f1 | sed 's/../\\x&/g' | xargs -0 printf | base64
```

### Kotlin-toteutus
```kotlin
fun generatePassword(passphrase: String, length: Int): String {
    val hash = MessageDigest.getInstance("SHA-256").digest(passphrase.toByteArray())
    val base64 = Base64.getEncoder().encodeToString(hash)
    return base64.take(length)
}
```

## 📱 Uusi käyttöliittymä

### Pääruutu (ProfileListScreen)
- Näyttää kaikki salasanaprofiilit
- Ohjeteksti PassTool-yhteensopivuudesta
- FAB-painike uuden profiilin lisäämiseen

### Profiilin yksityiskohdat (ProfileDetailScreen)
- Näyttää profiilin tiedot
- Passphrase-syöttökenttä
- Generoi salasanan painikkeesta
- Kopioi-toiminto leikepöydälle

### Lisää/muokkaa profiilia (AddEditProfileScreen)
- Perustiedot (otsikko, sivusto, käyttäjä)
- Salasana-asetukset (pituus, erikoismerkit)
- Muistiinpanot

## 🗄️ Tietokantamuutokset

### Vanha rakenne (PasswordEntity) ❌
```kotlin
data class PasswordEntity(
    val id: Long,
    val title: String,
    val encryptedPassword: String, // Poistettu!
    // ...
)
```

### Uusi rakenne (PasswordProfileEntity) ✅
```kotlin
data class PasswordProfileEntity(
    val id: Long,
    val title: String,
    val passwordLength: Int,        // Uusi!
    val useSpecialChars: Boolean,   // Uusi!
    val specialChars: String,       // Uusi!
    // ...
)
```

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

## 🔧 Arkkitehtuurimuutokset

### Poistettu ❌
- `CryptoManager` (ei tarvita salausta)
- `PasswordEntity` ja siihen liittyvät
- Salasanojen tallennus ja haku
- AES-salaus ja biometrinen tunnistautuminen

### Lisätty ✅
- `PassToolGenerator` (deterministinen generointi)
- `PasswordProfile` domain-malli
- Uudet use caset profiilien hallintaan
- Uusi UI profiileille

## 🧪 Testit

```kotlin
@Test
fun `generatePassword should be compatible with bash script logic`() {
    val password = PassToolGenerator.generatePassword("test", 43)
    val expected = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg"
    assertEquals(expected, password)
}
```

## 🚀 Käyttö

### Vaatimukset
- Android 7.0 (API level 24) tai uudempi
- **Ei vaadi** biometristä tunnistautumista tai master-salasanaa

### Käyttöönotto
1. Asenna sovellus Android-laitteeseen
2. **Ei tarvitse** pääsalasanaa - sovellus käynnistyy suoraan
3. Aloita profiilien lisääminen

### Käyttö
1. **Profiilin lisääminen**: Paina + -painiketta ja määritä asetukset
2. **Salasanan generointi**: Anna passphrase ja paina "Generoi salasana"
3. **Kopiointi**: Käytä kopioi-painiketta leikepöydälle
4. **Muokkaus**: Napauta profiilia listasta

## 🔒 Tietoturva

### Uusi turvallisuusmalli
- **Ei tallenneta salasanoja** - vain generointisäännöt
- **Deterministinen**: Sama passphrase tuottaa aina saman salasanan
- **PassTool-yhteensopiva**: Voit käyttää samoja profiileja bash-skriptissä
- **Offline**: Ei vaadi internetyhteyttä
- **SHA-256**: Kryptografisesti vahva hajautusfunktio

### Tietosuoja
- Kaikki tiedot tallennetaan paikallisesti laitteeseen
- Ei verkkoyhteyksiä tai pilvipalveluja
- Ei analytiikkaa tai seurantaa
- Avoimen lähdekoodin toteutus

### ⚠️ Tärkeää
**Tämä muutos tekee sovelluksesta yhteensopimattoman vanhan version kanssa. Vanhat salatut salasanat eivät ole enää käytettävissä.**

## Kehitys

### Uusi projektin rakenne
```
app/src/main/java/com/passwordmanager/
├── data/
│   ├── database/          # Room-tietokanta (PasswordProfileEntity)
│   └── repository/        # PasswordProfileRepositoryImpl
├── domain/
│   ├── model/            # PasswordProfile
│   ├── repository/       # PasswordProfileRepository
│   └── usecase/profile/  # Profiilin CRUD + generointi
├── presentation/profile/ # Uudet UI-komponentit
│   ├── ProfileListScreen.kt
│   ├── ProfileDetailScreen.kt
│   └── AddEditProfileScreen.kt
├── utils/                # PassToolGenerator
└── di/                   # Päivitetyt moduulit
```

### Testaus
- PassToolGenerator-yksikkötestit (yhteensopivuus bash-skriptin kanssa)
- Profiilin CRUD-toimintojen testit
- UI-testit uusille Compose-komponenteille

## Lisenssi

Tämä projekti on lisensoitu MIT-lisenssillä. Katso [LICENSE](LICENSE) tiedosto lisätietoja varten.

## Tekijä

Kehitetty OpenHands AI:n avulla Android-salasanojen hallintasovellukseksi.