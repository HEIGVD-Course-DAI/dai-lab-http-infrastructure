FROM nginx:latest

COPY /staticServer/ /app/staticServer
COPY nginx.conf /app/nginx.conf

EXPOSE 80
