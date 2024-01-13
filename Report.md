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
le mot-clef "services" est la key du fichier yml qui sert à définir les différents containers. Dans cette key, nous créons donc un container dont le docker file se trouve dans le dosser dans lequel nous sommes, et qui s'appelle "Dockerfile". <br>
lors du build, ce container aura l'image "my-nginx" (qui sera renommée plus tard), et sera disponible depuis l'extérieur via le port 8080.

## Partie 3
## Partie 4