// eslint.config.js
import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import tseslint from 'typescript-eslint'
import { globalIgnores } from 'eslint/config'

export default tseslint.config([
  // 무시 대상
  globalIgnores(['dist']),

  // 공통 TS/TSX 규칙
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
    rules: {
      // 기본 규칙 비활성화(충돌 방지)
      'no-unused-vars': 'off',

      // TS용 미사용 변수 규칙: _ 프리픽스와 rest-siblings 허용
      '@typescript-eslint/no-unused-vars': [
        'warn',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          ignoreRestSiblings: true,
        },
      ],
    },
  },

  // ✅ 타입 정의 폴더에서만 namespace 허용
  {
    files: ['src/types/**/*.ts', 'src/types/**/*.d.ts'],
    rules: {
      '@typescript-eslint/no-namespace': 'off',
      // 필요하다면 중복 선언 경고 완화:
      // 'no-redeclare': 'off',
      // '@typescript-eslint/no-redeclare': 'off',
    },
  },
])
