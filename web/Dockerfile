#FROM node:alpine as builder

#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#RUN npm run build

#FROM nginx:latest
#COPY ./build/distributions/index.html /usr/share/nginx/html/index.html

FROM nginx:latest
EXPOSE 80

COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY ./build/distributions /usr/share/nginx/html

#FROM nginx:latest
#ADD build/distributions/ /user/share/nginx/html
#ADD build/distributions /user/share/nginx/html
#COPY ./build/distributions/web.js /user/share/nginx/html
#COPY --from=builder /app/build /user/share/nginx/html