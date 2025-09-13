// src/styles/GlobalStyle.ts
import { createGlobalStyle } from 'styled-components';
import '@fontsource/noto-sans-kr';

/**
 * FitSync 2.0 GlobalStyle
 * - OKLCH 기반 팔레트 (포인트 컬러: Electric Violet 계열)
 * - 다크/라이트 테마 지원 (기본: 다크)
 * - 모바일 반응형 타이포 스케일
 * - 스크롤바/폼/유틸리티 클래스 포함
 *
 * 테마 전환: document.documentElement.setAttribute('data-theme', 'light' | 'dark')
 */
const GlobalStyle = createGlobalStyle`
  /* ------------------------------
   * 1) 디자인 토큰 (기본: 다크 테마)
   * ------------------------------ */
  :root {
    /* ===== 색상 토큰 (fallback → OKLCH 재정의) ===== */

    /* 포인트 컬러(메인) — 과감한 보라(Electric Violet) 계열 */
    --color-primary: #7c3aed;          /* fallback (비OKLCH 브라우저) */
    --color-primary-hover: #6d28d9;    /* hover/darker */
    --color-primary-soft: #a78bfa;     /* 밝은 톤 (배경/배지) */
    --color-primary-ghost: #ede9fe;    /* 아주 연한 톤 (라이트 테마 보조) */

    /* 배경/표면 (Dark) */
    --bg-app: #121212;                 /* 앱 메인 배경 */
    --bg-elev-1: #1b1b1b;              /* 1단 상승 표면 (카드/섹션) */
    --bg-elev-2: #232323;              /* 2단 상승 표면 (모달/드롭다운) */

    /* 텍스트 (Dark) */
    --text-1: #ffffff;                 /* 본문/헤드라인 */
    --text-2: #c9c9c9;                 /* 보조 텍스트 */
    --text-3: #888888;                 /* 비활성/힌트 */

    /* 경계/분리선 */
    --border-1: #2f2f2f;
    --border-2: #3a3a3a;

    /* 상태 색상 (semantic) */
    --success: #22c55e;
    --warning: #f59e0b;
    --danger:  #ef4444;
    --info:    #60a5fa;

    /* 그림자/효과 */
    --shadow-1: 0 1px 2px rgba(0,0,0,0.3);
    --shadow-2: 0 8px 24px rgba(0,0,0,0.35);

    /* 라디우스/간격/콘텐츠 폭 */
    --radius-sm: 8px;
    --radius-md: 12px;
    --radius-lg: 16px;
    --space-1: 4px;
    --space-2: 8px;
    --space-3: 12px;
    --space-4: 16px;
    --space-6: 24px;
    --space-8: 32px;
    --container-max: 1200px;

    /* 타이포 스케일 (rem 기준, clamp로 반응형) */
    --font-size-root: 62.5%; /* 1rem = 10px 기반 */
    --fs-xs: clamp(1.1rem, 0.95rem + 0.2vw, 1.2rem);
    --fs-sm: clamp(1.2rem, 1rem + 0.25vw, 1.4rem);
    --fs-md: clamp(1.4rem, 1.2rem + 0.3vw, 1.6rem);
    --fs-lg: clamp(1.6rem, 1.4rem + 0.5vw, 2.0rem);
    --fs-xl: clamp(2.0rem, 1.6rem + 0.8vw, 2.8rem);
    --fs-2xl: clamp(2.6rem, 2.0rem + 1.2vw, 3.6rem);

    /* 인터랙션 */
    --focus-ring: 0 0 0 3px rgba(124, 58, 237, 0.35); /* primary 기반 포커스 */

    /* 성능/전역 제스처 */
    --scrollbar-size: 10px;
  }

  /* 브라우저가 OKLCH 지원 시, 보다 정확한 색영역으로 재정의 */
  @supports (color: oklch(50% 0.2 0)) {
    :root {
      /* Electric Violet scale */
      --color-primary:       oklch(72% 0.24 305);
      --color-primary-hover: oklch(64% 0.24 305);
      --color-primary-soft:  oklch(82% 0.10 305);
      --color-primary-ghost: oklch(96% 0.03 305);

      /* Dark surfaces */
      --bg-app:    oklch(18% 0.02 255);
      --bg-elev-1: oklch(23% 0.02 255);
      --bg-elev-2: oklch(28% 0.02 255);

      /* Text on dark */
      --text-1: oklch(95% 0 0);
      --text-2: oklch(82% 0.02 255);
      --text-3: oklch(68% 0.02 255);

      /* Borders */
      --border-1: oklch(32% 0.02 255);
      --border-2: oklch(38% 0.02 255);

      /* Semantic states */
      --success: oklch(72% 0.16 150);
      --warning: oklch(75% 0.18 60);
      --danger:  oklch(62% 0.20 27);
      --info:    oklch(72% 0.16 235);
    }
  }

  /* ------------------------------
   * 2) 라이트 테마 토큰
   *    : <html data-theme="light">
   * ------------------------------ */
  [data-theme="light"] {
    --bg-app: #ffffff;
    --bg-elev-1: #f7f7f9;
    --bg-elev-2: #f0f0f4;

    --text-1: #101014;
    --text-2: #3b3b42;
    --text-3: #6b6b78;

    --border-1: #e6e6ef;
    --border-2: #d8d8e2;

    --color-primary: #6d28d9;
    --color-primary-hover: #5b21b6;
    --color-primary-soft: #a78bfa;
    --color-primary-ghost: #ede9fe;

    --shadow-1: 0 1px 2px rgba(0,0,0,0.08);
    --shadow-2: 0 8px 24px rgba(0,0,0,0.12);
  }

  @supports (color: oklch(50% 0.2 0)) {
    [data-theme="light"] {
      --bg-app:    oklch(99% 0.01 255);
      --bg-elev-1: oklch(97% 0.01 255);
      --bg-elev-2: oklch(95% 0.02 255);

      --text-1: oklch(18% 0.02 255);
      --text-2: oklch(34% 0.02 255);
      --text-3: oklch(50% 0.02 255);

      --border-1: oklch(90% 0.01 255);
      --border-2: oklch(85% 0.01 255);

      /* primary 계열은 다크와 동일한 색상군 유지(브랜드 일관성) */
      --color-primary:       oklch(62% 0.24 305);
      --color-primary-hover: oklch(56% 0.24 305);
      --color-primary-soft:  oklch(86% 0.10 305);
      --color-primary-ghost: oklch(96% 0.03 305);
    }
  }

  /* 시스템 선호 테마 자동 반영(명시적 data-theme가 있을 경우 우선) */
  @media (prefers-color-scheme: light) {
    :root:not([data-theme]) {
      color-scheme: light;
    }
  }
  @media (prefers-color-scheme: dark) {
    :root:not([data-theme]) {
      color-scheme: dark;
    }
  }

  /* ------------------------------
   * 3) 리셋 & 기본 설정
   * ------------------------------ */
  *, *::before, *::after {
    box-sizing: border-box;
  }

  html {
    font-size: var(--font-size-root);
    -webkit-text-size-adjust: 100%;
  }

  body {
    margin: 0;
    min-height: 100dvh;
    background: var(--bg-app);
    color: var(--text-1);
    font-family: 'Noto Sans KR', system-ui, -apple-system, Segoe UI, Roboto, 'Helvetica Neue', Arial, 'Apple SD Gothic Neo', 'Noto Sans KR', 'Malgun Gothic', sans-serif;
    line-height: 1.5;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  img, picture, video, canvas, svg {
    display: block;
    max-width: 100%;
  }

  input, button, textarea, select {
    font: inherit;
    color: inherit;
  }

  a {
    color: inherit;
    text-decoration: none;
  }

  /* ------------------------------
   * 4) 타이포 유틸
   * ------------------------------ */
  .fs-xs { font-size: var(--fs-xs); }
  .fs-sm { font-size: var(--fs-sm); }
  .fs-md { font-size: var(--fs-md); }
  .fs-lg { font-size: var(--fs-lg); }
  .fs-xl { font-size: var(--fs-xl); }
  .fs-2xl { font-size: var(--fs-2xl); }

  .ta-c { text-align: center !important; }
  .ta-l { text-align: left !important; }
  .txt-ov {
    overflow: hidden !important;
    text-overflow: ellipsis !important;
    display: -webkit-box !important;
    -webkit-line-clamp: 1 !important;
    -webkit-box-orient: vertical !important;
    max-width: 100% !important;
    margin: 0 !important;
    line-height: 1.4 !important;
  }
  .pos-r { position: relative !important; }

  /* ------------------------------
   * 5) 폼/컨트롤 스타일
   * ------------------------------ */
  input, textarea, select {
    background: var(--bg-elev-1);
    border: 1px solid var(--border-1);
    color: var(--text-1);
    border-radius: var(--radius-md);
    padding: 12px 14px;
    transition: border-color .15s ease, box-shadow .15s ease, background .15s ease;

    &::placeholder { color: var(--text-3); }
    &:hover { border-color: var(--border-2); }
    &:focus-visible {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: var(--focus-ring);
      background: var(--bg-elev-2);
    }
    &:disabled {
      opacity: .6;
      cursor: not-allowed;
    }
  }

  button {
    border: none;
    cursor: pointer;
    border-radius: var(--radius-md);
    padding: 10px 14px;
    background: var(--bg-elev-1);
    color: var(--text-1);
    transition: transform .05s ease, box-shadow .15s ease, background .15s ease;

    &:hover { background: var(--bg-elev-2); }
    &:active { transform: translateY(1px); }
    &:focus-visible {
      outline: none;
      box-shadow: var(--focus-ring);
    }

    /* 프라이머리 버튼 변형 */
    &.btn-primary {
      background: var(--color-primary);
      color: white;
      box-shadow: var(--shadow-1);

      &:hover { background: var(--color-primary-hover); box-shadow: var(--shadow-2); }
      &:disabled { opacity: .7; }
    }

    /* 소프트/고스트 변형 */
    &.btn-soft {
      background: var(--color-primary-soft);
      color: oklch(22% 0 0);
    }
    &.btn-ghost {
      background: transparent;
      border: 1px solid var(--border-1);
      &:hover { border-color: var(--color-primary); }
    }
  }

  /* 체크/토글 색상 */
  input[type="checkbox"], input[type="radio"] {
    accent-color: var(--color-primary);
  }

  /* ------------------------------
   * 6) 카드/컨테이너
   * ------------------------------ */
  .card {
    background: var(--bg-elev-1);
    border: 1px solid var(--border-1);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-1);
  }
  .container {
    max-width: var(--container-max);
    margin: 0 auto;
    padding: 0 var(--space-4);
  }

  /* ------------------------------
   * 7) 스크롤바 (WebKit)
   * ------------------------------ */
  ::-webkit-scrollbar {
    width: var(--scrollbar-size);
    height: var(--scrollbar-size);
  }
  ::-webkit-scrollbar-track {
    background: var(--bg-elev-1);
  }
  ::-webkit-scrollbar-thumb {
    background: var(--border-2);
    border-radius: 999px;
    border: 2px solid transparent;
    background-clip: padding-box;
  }
  ::-webkit-scrollbar-thumb:hover {
    background: var(--color-primary);
  }

  /* ------------------------------
   * 8) 접근성: 포커스 링
   * ------------------------------ */
  :focus-visible {
    outline: none;
    box-shadow: var(--focus-ring);
  }
`;

export default GlobalStyle;
