# 1. 빌드 환경 설정
FROM node:18-alpine as builder

WORKDIR /app

# 의존성 설치
COPY package.json .
COPY package-lock.json .
RUN npm install

# 소스 코드 복사 및 빌드
COPY . .
RUN npm run build

# 2. 프로덕션 환경 설정
FROM nginx:stable-alpine

# 빌드된 정적 파일을 Nginx 웹 서버의 기본 경로로 복사합니다.
COPY --from=builder /app/build /usr/share/nginx/html

# Nginx 포트를 외부에 노출합니다.
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]