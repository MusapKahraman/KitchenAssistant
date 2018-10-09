# Kitchen Assistant
_Capstone Project for the Android Developer Nanodegree Program_

This app is being developed according to a [technical specification](Capstone_Stage1.pdf) which is written in the first stage of the capstone project. The document includes UI mocks and a technical breakdown of components.

## Description

Kitchen Assistant helps you track your food, prepare a meal plan and create a shopping list. In addition to that, you can either select recipes from the public database or add your own and see how much people like it.

![thumbnail](https://raw.githubusercontent.com/MusapKahraman/KitchenAssistant/master/docs/Screenshot_login.png)

## Getting Started

This app depends on [Firebase Authentication](https://firebase.google.com/docs/auth/), [Firebase Realtime Database](https://firebase.google.com/docs/database/) and [Cloud Storage](https://firebase.google.com/docs/storage/). Please do the following before running the app:
1. [Install the Firebase SDK](https://firebase.google.com/docs/android/setup).
2. Add the app to your Firebase project in the [Firebase console](https://console.firebase.google.com/).

You will need to have:
1. "google-services.json" file under "app" folder.
2. "keystore.properties" file in root directory.
3. "kitchen-assistant.jks" file in root directory.

"keystore.properties" will have the following.
```
storePassword=yourPasswordHere
keyPassword=yourPasswordHere
keyAlias=key0
storeFile=../kitchen-assistant.jks
```

## Versioning

Using [SemVer](http://semver.org/) for versioning.
