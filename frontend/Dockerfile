# build stage
FROM node:lts-alpine as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . /app
RUN npm run build

# production stage
FROM nginx:1.15.2-alpine as production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80

RUN echo http://mirrors.aliyun.com/alpine/v3.7/main/ >> /etc/apk/repositories
RUN apk add --no-cache bash

CMD ["/bin/bash", "-c", "sed -i \"s@<html@<html data-api-base-url=\"$API_BASE_URL\"@\" /usr/share/nginx/html/index.html; nginx -g \"daemon off;\""]
