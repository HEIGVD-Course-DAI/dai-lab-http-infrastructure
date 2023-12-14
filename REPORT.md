# Labo 5 de DAI - Structure HTTP
Auteurs:
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
