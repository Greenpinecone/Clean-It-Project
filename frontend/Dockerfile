# syntax=docker/dockerfile:1

FROM node:24-alpine AS build

WORKDIR /workspace

COPY package.json package-lock.json ./

RUN npm ci

COPY angular.json ./
COPY tsconfig*.json ./
COPY src/ src/

RUN npm run build

FROM nginx:stable-alpine AS final

COPY nginx/default.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80