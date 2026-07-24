# Sebha

Compteur de tasbih Android natif — Kotlin, Jetpack Compose, Material Design 3, MVVM, DataStore.

## Prérequis

- JDK 17+
- Android SDK 35
- Android Studio Ladybug+ (recommandé)

## Ouvrir le projet

1. Ouvrir le dossier `Sebha` dans Android Studio
2. Laisser Gradle synchroniser
3. Lancer sur un émulateur ou appareil (API 26+)

## Build CLI

```bash
./gradlew assembleDebug
```

APK : `app/build/outputs/apk/debug/app-debug.apk`

## Fonctionnalités

- Écran unique, compteur + objectif + barre de progression
- Dates grégorienne et hégirienne (HijrahDate) avec correction ±2 jours
- Paramètres en bottom sheet (objectif, vibration, son, correction, reset)
- Vibration différenciée (33 / 99 / 100 / objectif)
- Persistance DataStore
- FR / EN / AR + dark mode
