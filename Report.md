# Labo 5 de DAI - Structure HTTP
## Auteurs
-   Fabrice Chapuis
-   Tomas Pavoni
## Partie 1
### Dockerfile
Nous avons créé le fichier Dockerfile permettant de copier les différents fichiers dans l'image nginx. </br>
<code>
FROM nginx:latest </br> </br>
COPY ./staticServer/ /usr/share/nginx </br>
COPY ./nginx.conf /etc/nginx/conf.d/default.conf </br>
</code>
Dans ce Dockerfile, il est spécifié que nous allons utiliser la dernière image de nginx disponible. <br>
Ensutie, nous allons copier le contenu de staticServer dans le dosser /usr/share/nginx de nginx, afin que le framework ait accès aux fichiers statics.<br>
Pour finir, Docker va copier le fichier de configuration de nginx dans l'image afin qu'il soit utilisable. <br>

### nginx.conf
Pour Nginx, nous avons créé [le fichier de configuration](./nginx.conf). <br>
Nous avons créé une cellule "server", dans laquelle nous précisons le port qui doit écouter, le nom du serveur et la localisation des différents fichiers afin qu'ils soient utilisables par nginx (le path est à l'intérieur de l'image, puisque nginx sera lancé dans l'image, donc après la copie du Docker)
<br>

## Partie 2
### Docker compose
Dans cette partie, nous avons créé le fichier docker-compose.yml, qui va nous permettre de build le projet
et le démarrer en une seule commande chacun: <br>
<code>
docker compose build <br>
docker compose up <br>
</code>

voici l'état du fichier à la fin de cette étape 2:
```yaml
version: '3'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile
    image: my-nginx
    ports:
      - "8080:80"
```
le mot-clef "services" est la key du fichier yml qui sert à définir les différents containers. Dans cette key, nous créons donc un container nommé "web" dont le docker file se trouve dans le dosser dans lequel nous sommes, et qui s'appelle "Dockerfile". <br>
lors du build, ce container aura l'image "my-nginx" (qui sera renommée plus tard), et sera disponible depuis l'extérieur via le port 8080.

## Partie 3
### Dockerfile
Avant les explications, voici le Dockerfile dans l'état actuel des choses: <br>
```Dockerfile
FROM openjdk:21

RUN mkdir -p API
ARG JAR_FILE=target/dynamicServer-1.0-SNAPSHOT-jar-with-dependencies.jar
ADD ${JAR_FILE} API/HTTP-API.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","API/HTTP-API.jar"]
```

Pour commencer, nous avons pris une image openjdk afin d'être capable de'exécuter l'api au sein du container. <br>
Ensuite, nous créons dans le container un folder "API" dans lequel nous allons copier le code qui a été compilé à l'aide de <br>
<code> 
ADD fichier-en-local équivalent-dans-l'image
</code>
. le mot-clef "ARG" crée juste une variable stockant le path vers l'exécutalbe.<br>
Pour finir, le mot-clef ENTRYPOINT va représenter la commande qui sera exécutée à la fin de la création du container. Dans notre cas, il s'agit de la commande qui va donc exécuter l'API. La raison pour laquelle la commande est comme tel est juste à des fins d'optimisation, mais un bête <code>java -jar path.jar</code> aurait tout autant fonctionné.

### Docker Compose
Voici l'état actuel du fichier:
```yaml
version: '3'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile
    image: my-nginx
    ports:
      - "8080:80"
  dynamicServer:
    build:
      context: dynamicServer/
      dockerfile: Dockerfile
    image: http-api
    ports:
      - "7001:7001"
```
Dans ce fichier, nous avons ajouté le service "dynamicServer", qui représentera notre API. afin de build, docker se servira du Dockerfile dans dynamicServer/ pour créer une image "http-api" si elle n'existe pas, et écoutera au port 7001.

### pom.xml
Ce fichier a dû être modifié pour deux raison. <br>
La première, est que lors de l'exécution de l'application, nous tombions sur une erreur "no main manifest attribute, in API/HTTP-API.jar", qui veut dire que le fichier jar ne contient pas l'attribut "Main-Class". Afin d'y remédier, nous avons reconstruit un fichier jar avec cette fois-ci l'attribut, en ajoutant ceci: <br>
```html
<configuration>
    <archive>
        <manifest>
            <mainClass>org.example.Main</mainClass>
        </manifest>
    </archive>
</configuration>
```
Ceci résolut ce problème.<br>
Cependant, après avoir réussi à exécuter le code, une autre erreur survint (qui est un peu plus habituelle): <br>
Exception in thread "main" java.lang.NoClassDefFoundError: io/javalin/Javalin at org.example.Main.main(Main.java:7) <br>
Java n'arrivait donc pas à trouver Javalin.<br>
Afin de résoudre ce problème, il a fallu ajouter "maven-assembly", "make-assembly" et "jar-with-dependencies". Ce qui, à la fin, donne ceci qui fu ajouté:
```html
<build>
<plugins>
    <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <configuration>
            <archive>
                <manifest>
                    <mainClass>org.example.Main</mainClass>
                </manifest>
            </archive>
            <descriptorRefs>
                <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
        </configuration>
        <executions>
            <execution>
                <id>make-assembly</id> <!-- this is used for inheritance merges -->
                <phase>package</phase> <!-- bind to the packaging phase -->
                <goals>
                    <goal>single</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
</build>
```

## Partie 4
### Mise en place de Traefik
Configuration du docker compose réservée à Traefik:<br>
```yaml
  reverse-proxy:
    # The official v2 Traefik docker image
    image: traefik:latest
    # Enables the web UI and tells Traefik to listen to docker
    command: --api.insecure=true --providers.docker
    ports:
      # The HTTP port
      - "80:80"
      # The Web UI (enabled by --api.insecure=true)
      - "8080:8080"
    volumes:
      # So that Traefik can listen to the Docker events
      - /var/run/docker.sock:/var/run/docker.sock
```
Nous avons donc créé un service "reverse proxy" ayant comme image "traefik". Le header command va définir une commande devant être lancée au démarrage du container.<br>
La commande donnée ici est une commande faisant démarrer traefik avec l'API non sécurisée, ce qui veut dire que nous n'avons pas besoin de nous authentifier, ce qui permettrait à tout le monde de l'utiliser, et va configurer traefik de manière à utiliser docker comme fournisseur de configuration (c'est donc ce qui nous permet d'utiliser traefik sans fiare de fichier de configuration).<br>
le header ports définit les ports disponibles depuis le reverse proxy. Nous pouvons donc seulement utiliser les ports 80 et 8080. Le reste est caché derrière le reverse proxy, qui peut être rendu disponible. Mais nous verrons cette partie un peu plus bas.<br>
Le header volumes sert à donner accès à une zone afin d'y écrire ou lire à un service. Dans notre cas, nous permettons à traefik de voir les évènemets docker, afin de s'adapter dans le cas où un container apparaîtrait, ou non (Traejik met à jour les ressources disponibles dynamiquement).
### Les services que nous avons adaptés
Dans chaque service, nous avons retiré le mot-clef "ports" pour le remplacer par "expose". Nous avons ensuite ajouté un mot-clef, "labels", permettant de configurer le comportement de traejik vis-à-vis du service concerné.
```yaml
  web:
    build:
      context: .
      dockerfile: Dockerfile
    image: static_server
    expose:
      - "1080"
    labels:
    # Link to access the page: localhost
      - "traefik.http.routers.web.rule=Host(`localhost`)"
      - "traefik.http.services.web.loadbalancer.server.port=1080"
```
Dans notre cas, pour le service "web", nous avons ajouté une première ligne définissant un lien, et deuxièmement, le port auquel nous voulons rediriger la connexion ou le packet dans le cas ou le lien entré précédemment est entré.<br>
Même chose pour l'API, mais cette fois il s'agit d'un path et pas d'un lien entier:
```yaml
dynamicServer:
    build:
      context: dynamicServer/
      dockerfile: Dockerfile
    image: http-api
    expose:
      - "7001"
    labels:
      # Redirige tous les packets allant vers l'entrypoint "/api" vers le port 7001.
      # L'api est donc disponible dans localhost/api, il est donc possible de faire localhost/api/users.
      - "traefik.http.routers.dynamicServer.rule=PathPrefix(`/api`)"
      - "traefik.http.services.dynamicServer.loadbalancer.server.port=7001"
```
### Pourquoi est-ce qu'un reverse proxy est utile ?
Le reverse proxy peut servir à plusieurs choses, notamment:
<ul>
  <li>La sécurité</li>
  Parce que le seul élément à être disponible et atteignable depuis le "monde extérieur" est le reverse proxy. Ceci réduit donc les menaces.
  <li>L'optimisation</li>
  Ce reverse proxy peut être utilisé afin d'optimiser la disparité des connexions entrantes afin d'empêcher la surcharge d'un serveur. Il est après possible de faire des sticky sessions.
</ul>

### Comment accéder au dashboard
Jusqu'à maintenant, nous n'avons pas vraiment modifié le comportement de traefik, donc la connexion à son dashboard reste la même.<br>
Afin de s'y connecter, il suffit d'entrer le lien "localhost:8080/dashboard/#/ .<br>
Il serait possible de modifier ceci en faisant quelque chose de similaire à ce qui a été fait plus haut, avec le routage de l'API et du serveur statique.<br>

## Partie 5
### Mise en place du load balancing
Pour cette partie, la mise en place était assez rapide.<br>
En effet, la chose la plus importante a été faite dans la partie 3:<br>
```yaml
volumes:
  # So that Traefik can listen to the Docker events
  - /var/run/docker.sock:/var/run/docker.sock```
```
Comme dit précédemment, cette ligne donne accès à traefik à la liste de containers. Ceci permet à traefik de dynamiquement gérer les containers actifs, et donc les ajouter dans la liste de services disponibles. Les autres choses ajoutées ne sont que mineures:
```yaml
deploy:
  replicas: 2
```
Ceci servant juste à créer plusieurs instance au démarrage du (ou des) containers afin de vérifier que traefik est capable de gérer plusieurs instances du même service, puis avons utilisé la commande <code>docker compose up --scale (service)</code> afin d'ajouter des instances pour voir comment traefik réagit. D'après ce que nous avons vu dans le dashboard, traefik détermine bel et bien si une instance a été créée ou éteinte dynamiquement, et est capable de s'adapter et de rediriger des queries dans chaque serveur.

## Partie 6
### Docker-Compose
Pour cette partie, il a suffit d'ajouter seulement 2 lignes:<br>
```yaml
dynamicServer:
  build:
    context: dynamicServer/
    dockerfile: Dockerfile
  image: http-api
  expose:
    - "7001"
  labels:
    # Redirige tous les packets allant vers l'entrypoint "/api" vers le port 7001.
    # L'api est donc disponible dans localhost/api, il est donc possible de faire localhost/api/users.
    - "traefik.http.routers.dynamicServer.rule=PathPrefix(`/api`)"
    - "traefik.http.services.dynamicServer.loadbalancer.server.port=7001"
    - "traefik.http.services.dynamicserver.loadbalancer.sticky.cookie=true"
    - "traefik.http.services.dynamicserver.loadbalancer.sticky.cookie.name=dynamicServerCookie"
  deploy:
    replicas: 2
```
La première ligne que nous avons ajoutée est la ligne 
>"traefik.http.services.dynamicserver.loadbalancer.sticky.cookie=true"<br>

Cette ligne va prévenir traefik que le loadbalancing du dynamicServer doit être fait avec des sticky sessions en définissant les cookies sous "true".

La deuxième ligne, ci-dessous, va définir le nom du cookie, afin qu'il puisse être utilisé: <br>
>"traefik.http.services.dynamicserver.loadbalancer.sticky.cookie.name=dynamicServerCookie"