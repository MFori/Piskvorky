FROM nginx:latest
EXPOSE 80

COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY ./build/distributions /usr/share/nginx/html