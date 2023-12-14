FROM nginx:latest

COPY ./staticServer/ /usr/share/nginx
COPY ./nginx.conf /etc/nginx/conf.d/default.conf