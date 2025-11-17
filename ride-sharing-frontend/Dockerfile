FROM node:20-slim AS build
WORKDIR /app
COPY ride-sharing-frontend/package*.json ./
RUN npm ci
COPY ride-sharing-frontend/ .
COPY ride-sharing-frontend/proxy-conf.json ./
EXPOSE 4200
CMD ["npm", "start"]
