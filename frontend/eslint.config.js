// eslint.config.js
import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { globalIgnores } from 'eslint/config'

export default tseslint.config([
  globalIgnores(['dist']),
  {
    files: ['**/*.{ts,tsx}'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs['recommended-latest'],
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
  },

  // ✅ 여기부터: 타입 정의 폴더에서만 namespace 허용
  {
    files: ['src/types/**/*.ts', 'src/types/**/*.d.ts'],
    rules: {
      '@typescript-eslint/no-namespace': 'off',
      // (선택) 선언 파일에서 중복 선언 경고를 줄이고 싶다면:
      // 'no-redeclare': 'off',
      // '@typescript-eslint/no-redeclare': 'off',
    },
  },
])
