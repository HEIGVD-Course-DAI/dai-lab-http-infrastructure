# Labo 5 de DAI - Structure HTTP
## Auteurs
-   Fabrice Chapuis
-   Tomas Pavoni
## Partie 1
### Dockerfile
Nous avons créé le fichier Dockerfile permettant de copier les différents fichiers dans l'image nginx. </br>
```Dockerfile
FROM nginx:latest

COPY ./staticServer/ /usr/share/nginx
COPY ./nginx.conf /etc/nginx/conf.d/default.conf
```
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
### API
Nous avons décidé de créer une classe Animal qui représente
un animal avec son espèce, son nom et son poids ainsi qu'une class 
AnimalController qui fournit les méthodes nécessaires
à la gestion des requêtes HTTP en utilisant les Context HTTP
de Javalin.
Ensuite, dans le main noous avons assigné chaque ressource à 
la méthode correspondante.
```java
public class Main {
public static void main(String[] args) {
Javalin app = Javalin.create().start(7001);

        AnimalController AnimalController = new AnimalController();

        app.get("/api/animals", AnimalController::getAll);
        app.get("/api/animals/{id}", AnimalController::getOne);
        app.post("/api/animals/", AnimalController::create);
        app.put("/api/animals/{id}", AnimalController::update);
        app.delete("/api/animals/{id}", AnimalController::delete);
    }
}
```
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

## Partie 7
Avant tout, il a fallut créer un certificat qui permettra de crypter les packets en https. Pour ce faire, nous avons utilisé la commande
>openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 365

qui crée un certificat auto-signé, d'une validité de 365 jours, du nom de cert.pem et sa clef, key.pem. Ces fichiers ont été placés dans le dossier ` traejik/certificates/.`<br>
Ensuite, afin de pouvoir les utiliser, nous avons créé des volumes dans docker compose, et en définissant le path des clefs:<br>
```yaml
volumes:
  certificates:
  traefik:
```
qui se trouve en bas du docker compose, qui est à la même tabulation que services, et qui sert à créer les volumes. <br>
Il a fallu ensuite définir le chemin du container, qui a été défini dans le service traefik (puisque c'est traefik qui utilisera le certificat):<br>
```yaml
volumes:
  - ./traefik/certificates:/etc/traefik/certificates
```
Cette ligne représente le path du volume en local (partie gauche de ":"), et son path dans le container du côté droit. <br>
Après avoir fait ceci, pour pouvoir utiliser ces certificats, il a fallu créer le fichier de configuration de base de traefik. Nous avons donc créé traefik.yaml dans `traefik/`, et avons ajouté le path vers ce fichier dans le service "traefik" de docker-compose: <br>
```yaml
volumes:
- ./traefik/traefik.yaml:/etc/traefik/traefik.yaml
```
Et après coup, nous avons travaillé dans le fichier de configuration de traefik. <br>
Nous avons commencé par définir le provider, qui est docker dans notre cas:<br>
```yaml
providers:
  docker: {}
```
Pour ensuite créer 2 entrypoints: http et https, et les avons chacun link à son port (respectivement 80 et 443):
```yaml
entrypoints:
  http: 
    address: ":80"
  https:
    address: ":443"
```
Maintenant qui nous avons défini tout ceci, il faut préciser à traefik où se trouve le certificat. Pour ceci, nous avons rempli la catégorie "tls" comme suit:<br>
```yaml
tls:
  certificates:
    - certFile: /certificates/cert.pem
      keyFile: /certificates/key.pem
```
Et, pour encore avoir accès au dashboard de traefik:
```yaml
api:
  dashboard: true
  insecure: true
```
Et donc avons retiré l'équivalent du docker-compose:
```yaml
command: --api.insecure=true --providers.docker
```
qui ne sert plus à rien.<br>
Maintenant plus qu'à activer HTTPS sur le serveur dynamique et le statique !<br>
Alors, pour chaque serveur, nous avons ajouté ces lignes:
```yaml
labels:
  - "traefik.http.routers.web.entrypoints=http,https"
  - "traefik.http.routers.web.tls=true"
```
dont la première va activer les entrypoints "http" et "https" créés précédemment, et le deuxième qui active la Transport Layer Security. Ceci était pour le serveur statique, mais il y a bien entendu les lignes équivalentes dans le serveur statique.<br>
Après tout ça, le serveur fonctionne maintenant sous https. Si nous voulions le mettre sur internet, nous pourrions ouvrir les connexions entrantes sur un port donné, et nous enregistrer dans un DNS. Et au niveau du certificat, Nous aurions pu le générer avec Let's Encrypte.

## Optional1 
Dans le but d'avoir une application web pour monitorer motre infrastructure web dynamique,
nous avons décidé d'implémenter portnair dans notre projet.
Portainer est une plateforme de gestion de conteneurs open-source qui offre une interface utilisateur graphique pour faciliter la gestion des environnements Docker et Kubernetes. Elle permet de simplifier le déploiement, la surveillance, et la maintenance des conteneurs et des services associés.

Afin de l'intégrer à notre projet il a fallu compléter le docker-compose:
```yaml
services:
# ...
portainer:
image: portainer/portainer-ce
expose:
- "9000"  # Port pour accéder à l'interface utilisateur de Portainer
volumes:
- /var/run/docker.sock:/var/run/docker.sock
- portainer_data:/data

volumes:
portainer_data:
```
Explications:

Image: Utilise l'image officielle de Portainer (portainer/portainer-ce).

Ports: Expose le port 9000 pour accéder à l'interface utilisateur de Portainer depuis un navigateur web.

Volumes:<br>
    Le montage de /var/run/docker.sock permet à Portainer d'interagir avec le daemon Docker de l'hôte, lui donnant la capacité de gérer les conteneurs et autres ressources.
    Le volume portainer_data est utilisé pour stocker les données persistantes de Portainer (comme les configurations et les informations de l'utilisateur).<br><br>
Après avoir fait ça, nous avons mis en place portainer de manière à ce qu'il soit accessible depuis `portainer.localhost`.<br>
Pour ce faire, nous avons redirigé tout requête dirigée vers le sous-domain `portainer.localhost` au port 9000:
```yaml
labels:
  # To connect to portainer: enter portainer.localhost
  - "traefik.http.routers.portainer.rule=Host(`portainer.localhost`)"
  - "traefik.http.services.portainer.loadbalancer.server.port=9000"
```
Puis avons ajouté la TLS et les entrypoints pour pouvoir s'y connecter en HTTPS:
```yaml
- "traefik.http.routers.portainer.entrypoints=http,https"
- "traefik.http.routers.portainer.tls=true"
```

## Optional 2
Pour cette partie, nous avons ajouté le code javascript dans le fichier HTML.
```html
<script>
    // Function to fetch data from your API
    function fetchData() {
      fetch('https://localhost/api') // Replace with your API endpoint
              .then(response => {
                if (!response.ok) {
                  throw new Error('Network response was not ok');
                }
                return response.json();
              })
              .then(data => {
                document.getElementById('apiResult').innerHTML = JSON.stringify(data, null, 2);
              })
              .catch(error => {
                console.error('There has been a problem with your fetch operation:', error);
              });
    }

    // Call fetchData() function every 5 seconds
    setInterval(fetchData, 5000);
  </script>
 ``` 
fetch() est utilisé pour effectuer une requête GET vers notre API.
La réponse de l'API est convertie en JSON.
Les données sont ensuite affichées dans la div ayant l'identifiant "apiResult".
setInterval est utilisé pour appeler fetchData toutes les 5 secondes (5000 millisecondes). <br>
Actuellement, notre code javascript ne change absolument pas la page web, pour la simple et bonne raison que la page n'est pas faite pour ça. Mais le côté infrastructure fonctionne, lui.