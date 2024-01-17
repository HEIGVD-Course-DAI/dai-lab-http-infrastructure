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

