## Ride-Sharing Application
#### Overview:
This application consists of a frontend and a backend both
secured and authenticated with Keycloak, that's running locally
in a docker container. It has a map integrated to it, and it's possible
to see your passenger's or driver's live location on it.

#### Tech Stack:
 - Frontend: Angular 20
 - Backend: Java 17, Java Spring Boot
 - Database: Postgresql
 - Authentication: Keycloak

#### Starting the application:
    1. git clone https://github.com/dsdsgege/ride-sharing-app.git
    2. cd ride-sharing-app
    3. docker-compose up --build

#### Accessing the application:
 - Frontend: **localhost:80**
 - Keycloak admin console: **localhost:8090** (username: **admin**, password: **admin**)
 - Keycloak ride-sharing-app realm: **localhost:8090/admin/master/console/#/ride-sharing-app**
 - Backend: running on **localhost:8080** *(No need to access it)*
##### For test purposes there is a realm-import included with a test user.
##### *username:* <code style="color: red">test</code>, *password:* <code style="color: red">test</code>