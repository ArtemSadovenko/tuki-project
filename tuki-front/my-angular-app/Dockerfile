# Stage 1: Build the Angular application
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --quiet
COPY . .
# Build for production
RUN npm run build -- --configuration production
# Log the build output for debugging
RUN ls -la /app/dist/my-angular-app/ /app/dist/my-angular-app/browser/

# Stage 2: Serve with Nginx
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/*
# Copy the browser subdirectory contents
COPY --from=build /app/dist/my-angular-app/browser/ /usr/share/nginx/html/
# Fix permissions
RUN chown -R nginx:nginx /usr/share/nginx/html/ && chmod -R 755 /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]