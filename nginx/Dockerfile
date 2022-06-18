FROM node:lts-alpine as FRONT

WORKDIR /front

COPY front .

RUN npm i
RUN npm run build


FROM nginx:1.19.0-alpine as NGINX

RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/application.conf /etc/nginx/conf.d

COPY nginx/cert.crt /etc/nginx
COPY nginx/cert.key /etc/nginx

COPY --from=FRONT /front/build/index.html /var/www/html/
COPY --from=FRONT /front/build/static /var/www/html/