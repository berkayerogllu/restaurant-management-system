FROM node:22-alpine AS build
WORKDIR /workspace

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
# Baked into the static bundle at build time: the browser calls the backend
# directly at this URL, so it must be reachable from the user's machine, not
# just from inside the Docker network.
ARG VITE_API_BASE_URL=http://localhost:8082/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

FROM nginx:1.27-alpine AS run
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist /usr/share/nginx/html

EXPOSE 80
