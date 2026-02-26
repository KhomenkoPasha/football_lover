Football Lover - Project Description

Overview

This is an Android application for viewing football data via TheSportsDB API:
- leagues list
- matches by date
- live matches
- favorite leagues
- today's matches from favorite leagues
- league teams
- standings
- match details

Core Features
- Main screen with tabs: "All leagues", "Live", "Favorites", "Matches"
- Match loading for a selected date (calendar on the home screen)
- Add/remove leagues to favorites (persisted in local database)
- Separate tab with today's matches only from favorite leagues
- Navigation flow: league -> teams -> matches/standings -> match details
- League and team logos (badge/logo) display
- Local data caching (Room)
- Localization support
- UI and instrumentation tests (Compose UI + Espresso)

Architecture
- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Layered structure: data / domain / ui
- ViewModel + UseCase + Repository
- Async: Kotlin Coroutines
- Local storage: Room
- Navigation: Navigation Compose
- Networking: Retrofit + OkHttp + Moshi

Jetpack / AndroidX
Jetpack Compose
Navigation
- androidx.navigation:navigation-compose
Images
- Coil Compose

Local Database
- Room Runtime
- Room KTX 
- Room Compiler 2.6.1 (via KSP)

Testing
- JUnit 4.13.2 (unit tests)
- AndroidX Test JUnit 1.3.0 (instrumented tests)
- Espresso Core 3.7.0
- Compose UI Test (junit4, test-manifest)

- Local cache and favorites are stored in Room
- Includes a basic set of unit, instrumentation, and UI tests

![app_screens_compressed](https://github.com/user-attachments/assets/3eef2d75-0617-4b0b-b086-b503405721e6)

