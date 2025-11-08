FROM node:20-slim AS build
WORKDIR /app
RUN npm install -g @angular/cli
COPY ride-sharing-frontend/ .
RUN npm install
RUN ng build --configuration=development

FROM nginx:stable AS final
EXPOSE 80
COPY --from=build /app/dist/ride-sharing-frontend/browser /usr/share/nginx/html/
