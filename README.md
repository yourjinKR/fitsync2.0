
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
│  ├─ settings.gradle
│  └─ src
│     ├─ main
│     │  ├─ java
│     │  │  └─ com
│     │  │     └─ fitsync
│     │  │        ├─ BackendApplication.java
│     │  │        ├─ config
│     │  │        │  ├─ HealthCheckController.java
│     │  │        │  ├─ jwt
│     │  │        │  │  ├─ JwtAuthenticationFilter.java
│     │  │        │  │  └─ JwtTokenProvider.java
│     │  │        │  ├─ oauth
│     │  │        │  │  ├─ CustomOAuth2UserService.java
│     │  │        │  │  ├─ OAuth2AuthenticationFailureHandler.java
│     │  │        │  │  ├─ OAuth2AuthenticationSuccessHandler.java
│     │  │        │  │  └─ OAuthAttributes.java
│     │  │        │  ├─ SecurityConfig.java
│     │  │        │  └─ WebConfig.java
│     │  │        └─ domain
│     │  │           ├─ AuthController.java
│     │  │           ├─ AuthService.java
│     │  │           ├─ Role.java
│     │  │           ├─ test
│     │  │           │  ├─ Test.java
│     │  │           │  ├─ TestController.java
│     │  │           │  ├─ TestRepository.java
│     │  │           │  └─ TestService.java
│     │  │           └─ user
│     │  │              ├─ SocialProvider.java
│     │  │              ├─ User.java
│     │  │              ├─ UserController.java
│     │  │              ├─ UserGender.java
│     │  │              ├─ UserRepository.java
│     │  │              ├─ UserService.java
│     │  │              ├─ UserStatus.java
│     │  │              └─ UserType.java
│     │  └─ resources
│     │     ├─ application-prod.properties
│     │     ├─ application.properties
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
├─ frontend
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
│  │  │  ├─ AuthApi.ts
│  │  │  └─ UserApi.ts
│  │  ├─ App.css
│  │  ├─ App.test.tsx
│  │  ├─ App.tsx
│  │  ├─ components
│  │  │  ├─ PrivateRoute.tsx
│  │  │  └─ PublicRoute.tsx
│  │  ├─ contexts
│  │  │  ├─ AuthContext.tsx
│  │  │  └─ AuthURL.ts
│  │  ├─ Display.tsx
│  │  ├─ hooks
│  │  ├─ index.css
│  │  ├─ index.tsx
│  │  ├─ logo.svg
│  │  ├─ pages
│  │  │  ├─ AdminMain.tsx
│  │  │  ├─ AuthCallBack.tsx
│  │  │  ├─ ErrorPage.tsx
│  │  │  ├─ Home.tsx
│  │  │  └─ LoginPage.tsx
│  │  ├─ react-app-env.d.ts
│  │  ├─ reportWebVitals.ts
│  │  ├─ setupTests.ts
│  │  ├─ styles
│  │  │  └─ GlobalStyle.js
│  │  ├─ test
│  │  │  └─ DBConnectPage.jsx
│  │  ├─ tests
│  │  ├─ types
│  │  │  ├─ api.ts
│  │  │  ├─ common.ts
│  │  │  └─ domain
│  │  │     └─ users.ts
│  │  └─ utils
│  └─ tsconfig.json
└─ README.md

```