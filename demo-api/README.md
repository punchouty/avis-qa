## Development Environment
```
cd demo-api
docker compose up

docker compose down
```
### Test Api
CRUD for below

http://localhost:9000/locations



### Mocked API
1. http://localhost:8080/html/example.html
2. http://localhost:8080/json/data.json
3. http://localhost:8080/fact

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
docker build -t sample-api:0.1 .
```

### Run Service
```
docker run -e DB_URL=jdbc:postgresql://postgres:5432/postgres -e DB_USER=postgres -e DB_PASSWORD=password -p 8080:8080 --network=avis_network_test -it demo-api:0.1
```

