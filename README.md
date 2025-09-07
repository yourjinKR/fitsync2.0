
```
fitsync2.0
├─ backend
│  ├─ build.gradle
│  ├─ Dockerfile
│  ├─ gradle
│  │  └─ wrapper
│  │     ├─ gradle-wrapper.jar
│  │     └─ gradle-wrapper.properties
│  ├─ gradlew
│  ├─ gradlew.bat
│  ├─ scripts
│  ├─ settings.gradle
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ fitsync
│     │  │        ├─ BackendApplication.java
│     │  │        ├─ config
│     │  │        │  └─ WebConfig.java
│     │  │        └─ domain
│     │  │           ├─ test
│     │  │           │  ├─ Test.java
│     │  │           │  ├─ TestController.java
│     │  │           │  ├─ TestRepository.java
│     │  │           │  └─ TestService.java
│     │  │           └─ users
│     │  │              ├─ SocialProvider.java
│     │  │              ├─ UserGender.java
│     │  │              ├─ Users.java
│     │  │              ├─ UsersController.java
│     │  │              ├─ UsersRepository.java
│     │  │              ├─ UsersService.java
│     │  │              ├─ UserStatus.java
│     │  │              └─ UserType.java
│     │  └─ resources
│     │     ├─ application-prod.properties
│     │     ├─ application.properties
│     │     ├─ db
│     │     │  └─ migration
│     │     ├─ static
│     │     │  └─ index.html
│     │     └─ templates
│     └─ test
│        ├─ java
│        │  └─ com
│        │     └─ fitsync
│        │        └─ BackendApplicationTests.java
│        └─ resources
│           └─ application.properties
├─ docs
│  ├─ data
│  │  ├─ attachments.md
│  │  ├─ users.md
│  │  └─ user_time_slots.md
│  └─ memo.md
├─ frontend
│  ├─ .env.production
│  ├─ Dockerfile
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ public
│  │  ├─ favicon.ico
│  │  ├─ index.html
│  │  ├─ logo192.png
│  │  ├─ logo512.png
│  │  ├─ manifest.json
│  │  └─ robots.txt
│  ├─ README.md
│  ├─ src
│  │  ├─ api
│  │  │  ├─ apiClient.ts
│  │  │  └─ users.ts
│  │  ├─ App.css
│  │  ├─ App.test.tsx
│  │  ├─ App.tsx
│  │  ├─ components
│  │  │  └─ Home.jsx
│  │  ├─ Display.tsx
│  │  ├─ hooks
│  │  ├─ index.css
│  │  ├─ index.tsx
│  │  ├─ logo.svg
│  │  ├─ pages
│  │  ├─ react-app-env.d.ts
│  │  ├─ reportWebVitals.ts
│  │  ├─ setupTests.ts
│  │  ├─ styles
│  │  ├─ test
│  │  │  └─ DBConnectPage.jsx
│  │  ├─ tests
│  │  ├─ types
│  │  └─ utils
│  └─ tsconfig.json
└─ README.md

```