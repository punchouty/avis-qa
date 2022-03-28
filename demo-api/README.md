## Start Postgres
```
cd sample-api
docker compose up
```
## PGAdmin
* **URL :** http://localhost:5050/
* **User :** admin@admin.com
* **Password :** root

### Create Server
* **Name :** Docker
* **Host name/address :** postgres
* **Port :** 5432
* **Username :** postgres
* **Password :** password

### Build Image
```
docker image rm demo-api:0.1
docker build -t demo-api:0.1 .
```

### Run Service
```
docker run -e DB_URL=jdbc:postgresql://postgres:5432/postgres -e DB_USER=postgres -e DB_PASSWORD=password -p 8080:8080 --network=avis_network_test -it demo-api:0.1
```

