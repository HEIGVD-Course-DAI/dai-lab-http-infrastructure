DAI Lab - HTTP infrastructure
=============================

Objectives
----------

The main objective of this lab is to learn to build a complete Web infrastructure. This means, we will build a server infrastructure that serves a static Web site and a dynamic HTTP API. The diagram below shows the architecture of the infrastructure that we will build.

```mermaid
graph LR
    subgraph Client
        B((Browser))
    end
    subgraph Server
        RP(Reverse\nProxy)
        SS(Static\nWeb server)
        DS(Dynamic\nAPI server)
    end
    B -.-> RP
    RP --> SS
    RP --> DS
```

In addition to the basic requirement of service static and dynamic content, the infrastructure will have the following features:

- **Scalability**: both the static and the dynamic server will be deployed as a cluster of several instances. The reverse proxy will be configured to distribute the load among the instances.
- **Security**: the connection between the browser and the reverse proxy will be encrypted using HTTPS.
- **Management**: a Web application will be deployed to manage the infrastructure. This application will allow to start/stop instances of the servers and to monitor the state of the infrastructure.

General instructions
--------------------

- This is a **BIG** lab and you will need a lot of time to complete it. 
- You will work in **groups of 2 students** and use a Git workflow to collaborate.
- For certain steps you will need to do research in the documentation by yourself (we are here to help, but we will not give you step-by-step instructions!) or you will need to be creative (do not expect complete guidelines).
- Read carefully all the **acceptance criteria** of each step. They will tell you what you need to do to complete the step.
- After the lab, each group will perform a short **demo** of their infrastructure.
- **You have to write a report with a short descriptioin for each of the steps.** Please do that directly in the repo, in one or more markdown files. Start in the README.md file at the root of your directory.
- The report must contain the procedure that you have followed to prove that your configuration is correct (what you did do make the step work and what you would do if you were doing a demo).


Step 0: GitHub repository
-------------------------

Create a GitHub repository for your project. You will use this repository to collaborate with your team mate. You will also use it to submit your work. 

> [!IMPORTANT]
> Be careful to keep a clear structure of the repository such that the different components are clearly separated.

### Acceptance criteria

- [ ] You have created a GitHub repository for your project.
- [ ] The respository contains a Readme file that you will use to document your project.


Step 1: Static Web site
-----------------------

The goal of this step is to build a Docker image that contains a static HTTP server Nginx. The server will serve a static Web site. The static Web site will be a single page with a nice looking template. You can use a free template for example from [Free-CSS](https://www.free-css.com/free-css-templates) or [Start Bootstrap](https://startbootstrap.com/themes).

### Acceptance criteria

- [ ] You have created a separate folder in your respository for your static Web server.
- [ ] You have a Dockerfile based on the Nginx image. The Dockerfile copies the static site content into the image.
- [ ] You have configured the `nginx.conf` configuration file to serve the static content on a port (normally 80).
- [ ] You are able to explain the content of the `nginx.conf` file.
- [ ] You can run the image and access the static content from a browser.
- [ ] You have **documented** your configuration in your report.


Step 2: Docker compose
----------------------

The goal of this step is to use Docker compose to deploy a first version of the infrastructure with a single service: the static Web server.

In addition to the basic docker compose configuration, we want to be able to rebuild the docker image of the Web server. See the [Docker compose Build documentation](https://docs.docker.com/compose/compose-file/build/) for this part.

### Acceptance criteria

- [ ] You have added a docker compose configuration file to your GitHub repo.
- [ ] You can start and stop an infrastructure with a single static Web server using docker compose.
- [ ] You can access the Web server on your local machine on the respective port.
- [ ] You can rebuild the docker image with `docker compose build`
- [ ] You have **documented** your configuration in your report.


Step 3: HTTP API server
-----------------------

This step requires a more work. The goal is to build a HTTP API with Javalin. You can implement any API of your choice, such as:

- an API to manage a list of quotes of the day
- an API to manage a list of TODO items
- an API to manage a list of people

Use your imagination and be creative!

The only requirement is that the API supports at all CRUD operations, i.e.: Create, Read, Update, Delete. 

Use a API testing tool such as Insomnia, Hoppscotch or Bruno to test all these operations.

The server does not need to use a database. You can store the data in memory. But if you want to add a DB, feel free to do so.

Once you're finished with the implementation, create a Dockerfile for the API server. Then add it as a service to your docker compose configuration.

### Acceptance criteria

- [ ] Your API supports all CRUD operations.
- [ ] You are able to explain your implementation and walk us through the code.
- [ ] You can start and stop the API server using docker compose.
- [ ] You can access both the API and the static server from your browser.
- [ ] You can rebuild the docker image with docker compose.
- [ ] You can do demo where use an API testing tool to show that all CRUD operations work.
- [ ] You have **documented** your implementation in your report.


Step 4: Reverse proxy with Traefik
----------------------------------

The goal of this step is to place a reverse proxy in front of the dynamic and static Web servers such that the reverse proxy receives all connections and relays them to the respective Web server. 

You will use [Traefik](https://traefik.io/traefik/) as a reverse proxy. Traefik interfaces directly with Docker to obtain the list of active backend servers. This means that it can dynamically adjust to the number of running server. Traefik has the particularity that it can be configured using labels in the docker compose file. This means that you do not need to write a configuration file for Traefik, but Traefik will read container configurations from the docker engine through the file `/var/run/docker.sock`.

The steps to follow for this section are thus:

- Add a new service "reverse_proxy" to your docker compose file using the Traefik docker image
- Read the [Traefik Quick Start](https://doc.traefik.io/traefik/getting-started/quick-start/) documentation to establish the basic configuration.
- Read the [Traefik & Docker](https://doc.traefik.io/traefik/routing/providers/docker/) documentation to learn how to configure Traefik to work with Docker.
- Then implement the reverse proxy:
  - relay the requests coming to "localhost" to the static HTTP server
  - relay the requests coming to "localhost/api" to the API server. See the [Traefik router documentation](https://doc.traefik.io/traefik/routing/routers/) for managing routes based on path prefixes. 
  - you will have to remove the `ports` configuration from the static and dynamic server in the docker compose file and replace them with `expose` configuration. Traefik will then be able to access the servers through the internal Docker network.
- You can use the [Traefik dashboard](https://doc.traefik.io/traefik/operations/dashboard/) to monitor the state of the reverse proxy.

### Acceptance criteria

- [ ] You can do a demo where you start from an "empty" Docker environment (no container running) and using docker compose you can start your infrastructure with 3 containers: static server, dynamic server and reverse proxy
- [ ] In the demo you can access each server from the browser in the demo. You can prove that the routing is done correctly through the reverse proxy.
- [ ] You are able to explain in the documentation how you have implemented the solution and walk us through the configuration and the code.
- [ ] You are able to explain in the documentation why a reverse proxy is useful to improve the security of the infrastructure.
- [ ] You are able to explain in the documentation how to access the dashboard of Traefik and how it works.
- [ ] You have **documented** your configuration in your report.


Step 5: Scalability and load balancing
--------------------------------------

The goal of this section is to allow Traefik to dynamically detect several instances of the (dynamic/static) Web servers. You may have already done this in the previous step 3.

Modify your docker compose file such that several instances of each server are started. Check that the reverse proxy distributes the connections between the different instances. Then, find a way to *dynamically* update the number of instances of each service with docker compose, without having to stop and restart the topology.

### Acceptance criteria

- [ ] You can use docker compose to start the infrastructure with several instances of each server (static and dynamic).
- [ ] You can dynamically add and remove instances of each server.
- [ ] You can do a demo to show that Traefik performs load balancing among the instances.
- [ ] If you add or remove instances, you can show that the load balancer is dynamically updated to use the available instances.
- [ ] You have **documented** your configuration in your report.


Step 6: Load balancing with round-robin and sticky sessions
-----------------------------------------------------------

By default, Traefik uses round-robin to distribute the load among all available instances. However, if a service is stateful, it would be better to send requests of the same session always to the same instance. This is called sticky sessions.

The goal of this step is to change the configuration such that:

- Traefik uses sticky session for the dynamic server instances (API service).
- Traefik continues to use round robin for the static servers (no change required).

### Acceptance criteria

- [ ] You do a setup to demonstrate the notion of sticky session.
- [ ] You prove that your load balancer can distribute HTTP requests in a round-robin fashion to the static server nodes (because there is no state).
- [ ] You prove that your load balancer can handle sticky sessions when forwarding HTTP requests to the dynamic server nodes.
- [ ] You have **documented** your configuration and your validation procedure in your report.


Step 7: Securing Traefik with HTTPS
-----------------------------------

Any real-world web infrastructure must be secured with HTTPS instead of clear-text HTTP. The goal of this step is to configure Traefik to use HTTPS with the clients. The schema below shows the architecture.

```mermaid

graph LR
    subgraph Client
        B((Browser))
    end
    subgraph Server
        RP(Reverse\nProxy)
        SS(Static\nWeb server)
        DS(Dynamic\nAPI server)
    end
    B -. HTTPS .-> RP
    RP -- HTTP --> SS
    RP -- HTTP --> DS
```

This means that HTTPS is used for connection with clients, over the Internet. Inside the infrastructure, the connections between the reverse proxy and the servers are still done in clear-text HTTP.

### Certificate

To do this, you will first need to generate an encryption certificate. Since the system is not exposed to the Internet, you cannot use a public certificate such as Let's encrypt, but have to generate a self-signed certificate. You can [do this using openssl](https://stackoverflow.com/questions/10175812/how-to-create-a-self-signed-certificate-with-openssl#10176685).

Once you got the two files (certificate and key), you can place them into a folder, which has to be [mounted as a volume in the Traefik container](https://docs.docker.com/compose/compose-file/compose-file-v3/#short-syntax-3). You can mount the volume at any path in the container, for example `/etc/traefik/certificates`.

### Traefik configuration file

Up to now, you've configured Traefik through labels directely in the docker compose file. However, it is not possible to specify the location of the certificates to Traefik with labels. You have to create a configuration file `traefik.yaml`. 

Again, you have to mount this file into the Traefik container as a volume, at the location `/etc/traefik/traefik.yaml`.

The configuration file has to contain several sections:

- The [providers](https://doc.traefik.io/traefik/providers/docker/#configuration-examples) section to configure Traefik to read the configuration from Docker.
- The [entrypoints](https://doc.traefik.io/traefik/routing/entrypoints/#configuration-examples) section to configure two endpoints:  `http` and `https`.
- The [tls](https://doc.traefik.io/traefik/https/tls/#user-defined) section to configure the TLS certificates. Specify the location of the certificates as the location where you mounted the directory into the container (such as `/etc/traefik/certificates`).
- In order to make the dashboard accessible, you have to configure the [api](https://doc.traefik.io/traefik/operations/dashboard/#insecure-mode) section. You can remove the respective labels from the docker compose file.

### Activating the HTTPS entrypoint for the servers

Finally, you have to activate HTTPS for the static and dynamic servers. This is done in the docker compose file. You have to add two labels to each server:

- to activate the HTTPS entrypoint,
- to set TLS to true.

See the [Traefik documentation for Docker](https://doc.traefik.io/traefik/routing/providers/docker/#routers) for these two labels.

### Testing

After these configurations it should be possible to access the static and the dynamic servers through HTTPS. The browser will complain that the sites are not secure, since the certificate is self-signed. But you can ignore this warning.

If it does not work, go to the Traefik dashboard and check the configuration of the routers and the entrypoints.

### Acceptance criteria

- [ ] You can do a demo where you show that the static and dynamic servers are accessible through HTTPS.
- [ ] You have **documented** your configuration in your report.



Optional steps
==============

If you sucessfully complete all the steps above, you can reach a grade of 5.0. If you want to reach a higher grade, you can do one or more of the following optional steps. 

Optional step 1: Management UI
------------------------------

The goal of this step is to deploy or develop a Web app that can be used to monitor and update your Web infrastructure dynamically. You should be able to list running containers, start/stop them and add/remove instances.

- you use an existing solution (search on Google)
- for extra points, develop your own Web app. In this case, you can use the Dockerode npm module (or another Docker client library, in any of the supported languages) to access the docker API.

### Acceptance criteria

- [ ] You can do a demo to show the Management UI and manage the containers of your infrastructure.
- [ ] You have **documented** how to use your solution.
- [ ] You have **documented** your configuration in your report.


Optional step 2: Integration API - static Web site
--------------------------------------------------

This is a step into unknow territory. But you will figure it out.

The goal of this step is to change your static Web page to periodically make calls to your API server and show the results in the Web page. You will need JavaScript for this and this functionality is called AJAX.

Keep it simple! You can start by just making a GET request to the API server and display the result on the page. If you want, you can then you can add more features, but this is not obligatory.


The modern way to make such requests is to use the [JavaScript Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch). But you can also use JQuery if you prefer.


### Acceptance criteria

- [ ] You have added JavaScript code to your static Web page to make at least a GET request to the API server.
- [ ] You can do a demo where you show that the API is called and the result is displayed on the page.
- [ ] You have **documented** your implementation in your report.

