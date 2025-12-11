# Ride-Sharing Application
## Overview:
This application consists of a frontend and a backend both
secured and authenticated with Keycloak, that's running locally
in a docker container. It has a map integrated to it, and it's possible
to see your passenger's or driver's live location on it.

## Tech Stack:
 - Frontend: Angular 20
 - Backend: Java 17, Java Spring Boot
 - Database: Postgresql
 - Authentication: Keycloak

### Preparations
Run the following commands in one of your folder:

1. Clone the repository
```sh
git clone https://github.com/dsdsgege/ride-sharing-app.git
```

2. Navigate to the projects root folder
```sh
cd ride-sharing-app
```

3. Create a new environment file

On Windows:
```powershell
New-Item -ItemType File -Path "ride-sharing-frontend/src/environments/environments.development.ts"
```

On Linux/macOS:
```sh
cp ride-sharing-frontend/src/environments/environment.development.example.ts ride-sharing-frontend/src/environments/environment.development.ts
```

Now you need to configure the environment.development.ts file. 
This includes giving an api key for openweathermap's API, 
further information in the environment file's documentation.

## Starting the application:
Run the following command:
```sh
docker-compose up
```
If any changes are made, run
```sh
docker-compose up --build
```

## Accessing the application:
 - Frontend: **localhost:4200**
 - Keycloak admin console: **localhost:8090** (username: **admin**, password: **admin**)
 - Keycloak ride-sharing-app realm: **localhost:8090/admin/master/console/#/ride-sharing-app**
 - Backend: running on **localhost:8080** *(No need to access it)*
#### For test purposes there is a realm-import included with a test user.
#### *username:* <code style="color: red">test</code>, *password:* <code style="color: red">test</code>