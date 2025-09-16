
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
│     │  │        │  ├─ JpaAuditingConfig.java
│     │  │        │  ├─ SecurityConfig.java
│     │  │        │  └─ WebConfig.java
│     │  │        ├─ domain
│     │  │        │  ├─ auth
│     │  │        │  │  ├─ AuthController.java
│     │  │        │  │  ├─ AuthService.java
│     │  │        │  │  ├─ GoogleAuthResponse.java
│     │  │        │  │  ├─ KakaoAuthResponse.java
│     │  │        │  │  └─ NaverAuthResponse.java
│     │  │        │  ├─ exercise
│     │  │        │  │  ├─ controller
│     │  │        │  │  │  └─ ExerciseController.java
│     │  │        │  │  ├─ dto
│     │  │        │  │  │  ├─ ExerciseDto.java
│     │  │        │  │  │  └─ ExerciseResponseDto.java
│     │  │        │  │  ├─ entity
│     │  │        │  │  │  ├─ Exercise.java
│     │  │        │  │  │  └─ ExerciseInstruction.java
│     │  │        │  │  ├─ repository
│     │  │        │  │  │  ├─ ExerciseInstructionRepository.java
│     │  │        │  │  │  └─ ExerciseRepository.java
│     │  │        │  │  └─ service
│     │  │        │  │     └─ ExerciseService.java
│     │  │        │  ├─ jwt
│     │  │        │  │  ├─ JwtAuthenticationFilter.java
│     │  │        │  │  └─ JwtTokenProvider.java
│     │  │        │  ├─ oauth
│     │  │        │  │  ├─ CustomOAuth2UserService.java
│     │  │        │  │  ├─ OAuth2AuthenticationFailureHandler.java
│     │  │        │  │  ├─ OAuth2AuthenticationSuccessHandler.java
│     │  │        │  │  ├─ OAuth2Response.java
│     │  │        │  │  └─ OAuthAttributes.java
│     │  │        │  └─ user
│     │  │        │     ├─ controller
│     │  │        │     │  └─ UserController.java
│     │  │        │     ├─ entity
│     │  │        │     │  ├─ Role.java
│     │  │        │     │  ├─ SocialProvider.java
│     │  │        │     │  ├─ User.java
│     │  │        │     │  ├─ UserGender.java
│     │  │        │     │  ├─ UserStatus.java
│     │  │        │     │  └─ UserType.java
│     │  │        │     ├─ repository
│     │  │        │     │  └─ UserRepository.java
│     │  │        │     └─ service
│     │  │        │        └─ UserService.java
│     │  │        ├─ global
│     │  │        └─ HealthCheckController.java
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
│  ├─ dist
│  │  ├─ assets
│  │  │  ├─ index-ajP0DiCn.js
│  │  │  ├─ index-DUm9wyTj.css
│  │  │  ├─ noto-sans-kr-cyrillic-400-normal-D9rwZ47q.woff2
│  │  │  ├─ noto-sans-kr-cyrillic-400-normal-Dw-N_lhu.woff
│  │  │  ├─ noto-sans-kr-latin-400-normal-9HDSzKCN.woff
│  │  │  ├─ noto-sans-kr-latin-400-normal-CdyDEFPE.woff2
│  │  │  ├─ noto-sans-kr-latin-ext-400-normal-BYEQ_wmt.woff
│  │  │  ├─ noto-sans-kr-latin-ext-400-normal-D7aSIu2N.woff2
│  │  │  ├─ noto-sans-kr-vietnamese-400-normal-BwH49Cp7.woff
│  │  │  ├─ noto-sans-kr-vietnamese-400-normal-D41pEeVj.woff2
│  │  │  └─ react-CHdo91hT.svg
│  │  ├─ index.html
│  │  └─ vite.svg
│  ├─ Dockerfile
│  ├─ eslint.config.js
│  ├─ index.html
│  ├─ package-lock.json
│  ├─ package.json
│  ├─ public
│  │  └─ vite.svg
│  ├─ README.md
│  ├─ src
│  │  ├─ api
│  │  │  ├─ apiClient.ts
│  │  │  ├─ AuthApi.ts
│  │  │  └─ UserApi.ts
│  │  ├─ App.css
│  │  ├─ App.tsx
│  │  ├─ assets
│  │  │  └─ react.svg
│  │  ├─ components
│  │  │  ├─ PrivateRoute.tsx
│  │  │  └─ PublicRoute.tsx
│  │  ├─ contexts
│  │  │  └─ AuthContext.tsx
│  │  ├─ Display.tsx
│  │  ├─ hooks
│  │  │  └─ useAuth.ts
│  │  ├─ index.css
│  │  ├─ index.tsx
│  │  ├─ main.tsx
│  │  ├─ pages
│  │  │  ├─ AdminMain.tsx
│  │  │  ├─ AuthCallback.tsx
│  │  │  ├─ ErrorPage.tsx
│  │  │  ├─ Home.tsx
│  │  │  └─ LoginPage.tsx
│  │  ├─ styles
│  │  │  └─ GlobalStyle.ts
│  │  ├─ test
│  │  │  └─ DBConnectPage.jsx
│  │  ├─ types
│  │  │  ├─ api.ts
│  │  │  ├─ common.ts
│  │  │  ├─ domain
│  │  │  │  └─ users.ts
│  │  │  ├─ error.ts
│  │  │  └─ font.d.ts
│  │  └─ vite-env.d.ts
│  ├─ tsconfig.app.json
│  ├─ tsconfig.json
│  ├─ tsconfig.node.json
│  ├─ tsconfig.tsbuildinfo
│  └─ vite.config.ts
└─ README.md

```