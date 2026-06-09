A native Android application for a collaborative group calendar and memo system. Built with Java and Gradle as part of a course project alongside a Node.js REST API backend.

## Features

- View and manage group calendar events
- Create and share memos across group members
- Communicates with the [GroupCalendarMemo Backend](https://github.com/fiend993/GroupCalendarMemo-Backend) via REST API

## Tech Stack

- **Language:** Java
- **Platform:** Android
- **Build Tool:** Gradle (Kotlin DSL)
- **Architecture:** Client-server — Android app consumes a Node.js/Express REST API

## Project Structure

```
app/          # Android application source code
gradle/       # Gradle wrapper files
build.gradle.kts    # Build configuration
settings.gradle.kts # Project settings
```

## Related Repository

- **Backend:** [GroupCalendarMemo-Backend](https://github.com/fiend993/GroupCalendarMemo-Backend) — Node.js/Express REST API with JWT authentication

## Getting Started

1. Clone the repository
   ```bash
   git clone https://github.com/fiend993/GroupCalendarMemo-Frontend.git
   ```
2. Open in Android Studio
3. Make sure the backend server is running
4. Build and run on an Android emulator or physical device

## Author

**Charles Lin** — [github.com/fiend993](https://github.com/fiend993)
