## Development Environment
```
docker compose up

docker compose down
```
## Wiremock

### Run using docker
Refer - https://wiremock.org/docs/docker/
```
docker run --rm -d -p 8080:8080 -p 8443:8443 --name wiremock \
  -v /Users/rajan/IdeaProjects/avis/automation/api-automation-starter/src/test/resources/wiremock:/home/wiremock \
  wiremock/wiremock
 
docker container stop wiremock
```
#### Folder structure
* **__files** folder — contains JSON files that can be referenced in the mock endpoints
* **mappings** folder — contains the JSON representation of mock endpoints

#### Sample Working Request
Files
* http://localhost:8080/example.html
* http://localhost:8080/endpoint_1/success.json
* http://localhost:8080/endpoint_1/error_1.json
* http://localhost:8080/__general/notFound.json

Mappings
* http://localhost:8080/api/user/100
* http://localhost:8080/api/user