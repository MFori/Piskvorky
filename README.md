# Piskvorky

// build server
cd server
docker build -t piskvorky-server .
docker run -m512M --cpus 2 -it -p 9090:9090 --rm piskvorky-server

// build web
cd root
docker build -t piskvorky-web -f web/Dockerfile .


docker-compose up