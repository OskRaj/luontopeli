Luontopeli

Tämä on Android-sovellus, joka on toteutettu kouluharjoitustyönä. Sovelluksen avulla käyttäjä voi seurata kävelylenkkejään, mitata askelia ja matkaa sekä tunnistaa luontokohteita (kuten kasveja ja puita) kameran avulla.

## Projektin tila
Kyseessä on kurssin vaatimusten mukainen **kouluprojekti**. Projekti sisältää kaikki viikkoharjoituksissa määritellyt perusominaisuudet, mutta siihen ei ole toteutettu erillisiä lisäominaisuuksia.

## Peli-idea
Luontopeli kannustaa liikkumaan luonnossa. Käyttäjä voi:
* **Aloittaa kävelyn:** Sovellus piirtää reitin kartalle ja laskee matkan pituuden sekä askeleet.
* **Tunnistaa kohteita:** Käyttäjä voi ottaa kuvan löytämästään kasvista, jolloin sovellus yrittää tunnistaa sen koneoppimisen avulla.
* **Tallentaa löydöt:** Löydetyt kohteet tallentuvat paikalliseen tietokantaan ja synkronoituvat pilvipalveluun.
* **Tarkastella tilastoja:** Käyttäjä näkee yhteenvedon kaikista kävelyistään ja keräämistään löydöistä.

## Käytetyt teknologiat
Sovellus on toteutettu modernilla Android-arkkitehtuurilla käyttäen seuraavia tekniikoita:

* **Kotlin & Jetpack Compose:** Natiivi käyttöliittymä ja sovelluslogiikka.
* **Hilt (Dependency Injection):** Riippuvuuksien hallinta.
* **Room Database:** Paikallinen SQLite-tietokanta tietojen tallennukseen offline-tilassa.
* **Firebase (Auth & Firestore):** Anonyymi kirjautuminen ja löytöjen metadatan pilvisynkronointi.
* **CameraX:** Kameratoiminnallisuus kuvien ottamiseen.
* **ML Kit (Image Labeling):** Laitteella tapahtuva kuvatunnistus luontokohteiden nimeämiseen.
* **osmdroid (OpenStreetMap):** Karttanäkymä ja reitin piirtäminen.
* **Coil:** Kuvien asynkroninen lataaminen ja näyttäminen.
* **Android 12 Splash Screen API:** Moderni käynnistysruutu.

## Asennus
Löydät valmiin **Debug APK** -tiedoston tämän repositorion **Releases**-osiosta testaamista varten.
