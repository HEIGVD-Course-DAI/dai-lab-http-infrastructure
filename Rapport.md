Lab 5 - HTTP infrastructure
=============================

## Step 0
- Github repository creation
- 'Rapport.md' file creation for project documentation

## Step 1: Static Web site
- Creation of 'web-static' folder
- Creation of 'web-static/static-html'
- Creation of a Dockerfile in 'web-static' folder.
  This docker file is based on the nginx image ('FROM nginx:latest'). It copies the static site content into the image ('COPY static-html /usr/share/nginx/html')
  Verification:
  - go to folder where Dockerfile is and run:
  ```bash docker build -t image-name . ```
  (chez moi le nom de l'image est 'static-web-server' mais on peut choisir n'importe lequel)
  - check if it's built with following command and image should be listed.
  ```bash docker images ```
    - Creation of 'nginx.conf' file and configuration
      This file is the main configuration for Nginx web server.
      (For information: it contains directives that define how Nginx should function, what content it should serve and how it should handle various aspects of web requests)
      It is composed of configuration blocks. In our case, we need a 'http' block which will contain server configuration and a one 'server' block inside it that will specify how Nginx handles requests for static content of our website.
      'access_log' and 'error_log' are not mandatory but it is better to set it up for troubleshooting.
      'sendfile' and 'keepalive_timeout' not mandatory but enhances performance as it facilitates data transfering and sets how long server should wait before closing a connection.
        ```bash
        http { // http block
        // Specifies where Nginx should log access and error information. 
        access_log /var/log/nginx/access.log ;
        error_log /var/log/nginx/error.log;
      
            sendfile on;
            keepalive_timeout 65;
      
            server {
                listen 80; // specifies in which port the server will be listening for connections to serve the content
                server_name localhost;
      
                location / { // specifies
                    root /usr/share/nginx/html;
                    index index.html;
                }
            }
        }
      ```

      Verification:
      Run image
      ```bash docker run -p 8080:80 static-web-server ```
      Access the static content from a browser by opening a browser and going to
      ```bash http://localhost:8080/ ```
      You should be able to see the website. 
