# Piskvorky

// TODO

### KIV/PIA DOC
See [this doc](doc/DOC.md).

// build server
cd server
docker build -t piskvorky-server .
docker run -m512M --cpus 2 -it -p 9090:9090 --rm piskvorky-server

// build web
cd root
docker build -t piskvorky-web -f web/Dockerfile .


docker-compose up

docker-compose build

https://youtrack.jetbrains.com/issue/KTOR-646


// connect to db from cli
docker exec -it piskvory_mysql_1 bash -l
mysql -uroot -ppassword
use piskvorky_db;
select * from users;