FROM node:20-slim AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
COPY proxy.conf.json ./
EXPOSE 4200
CMD ["npm", "start"]
