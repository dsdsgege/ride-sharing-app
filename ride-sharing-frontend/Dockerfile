FROM node:lts-slim AS build
WORKDIR /src
RUN npm install -g @angular/cli
COPY . ./
RUN npm ci
RUN ng build --configuration=production

FROM nginx:stable AS final
EXPOSE 80
COPY --from=build src/dist/ride-sharing-frontend/browser /usr/share/nginx/html/
