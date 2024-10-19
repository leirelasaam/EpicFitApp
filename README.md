# EpicFitApp
## GitHub
### Creación del repositorio en local
Clonar un repositorio, donde la url es la ruta que se copia al entrar al repositorio:
```bash
git clone git@github.com:propietario/nombreProyecto.git
```
Establecer el nombre:
```bash
git config user.name "Nombre"
```
Establecer correo:
```bash
git config user.email "tu-correo"
```
### Establecer la conexión mediante SSH
Crear una clave SSH para conectarse desde el equipo al repositorio remoto (GitHub):
1. Crear una clave ssh:
```bash
ssh-keygen
```
1.1.Forma alternativa de crear una clave ed25519 con tu email
```bash
  $ ssh-keygen -t ed25519 -C "email@email.com"
```
2. Seguir los pasos donde primero se puede indicar la ruta y nombre del archivo id_rsa/id_ed que se va a generar.
3. Enter varias veces seguidas.
4. Copiar la clave pública (dirección puesta como ejemplo, donde se suele generar el archivo).
```bash
cat ~/.ssh/id_rsa.pub
```
5. En GitHub acceder a Settings > SSH and GPG Keys
6. Crear una nueva clave haciendo click en _New SSH key_ y pegar el contenido de la misma.

### Configurar SSH en local
1. Editar el archivo de configuración dentro del directorio del repositorio. Cuidado con tocar el archivo de config que es sensible.
```bash
nano .git/config
``` 
2. Dentro del apartado [core] introducir el comando a ejecutar donde la ruta que se indica debe coincidir con la ruta en la que se encuentra tu clave SSH generada.
```bash
sshCommand = ssh -i ~/.ssh/id_rsa
``` 
3. Si aún así persisten los problemas, quizás sea por el agente SSH. Para ello, introducir los siguientes comandos (la ruta debe ser la de tu clave):
```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
``` 

### Gestionar ramas
Crear una rama:
```bash
git branch nombre
```
Listar todas las ramas (tecla **q** para cerrar):
```bash
git branch -a
```
Crear y subir la rama a GitHub:
```bash
git push --set-upstream origin Lucian
```
Cambiarse a una rama:
```bash
git switch nombre
```
Eliminar rama local:
```bash
git branch -d tu-rama
```
Forzar borrado de la rama:
```bash
git branch -D tu-rama
```
Eliminar rama remota:
```bash
git push origin --delete nombre-de-tu-rama
```
Actualizar referencias de las ramas remotas:
```bash
git remote prune origin
```

### Gestionar subida de contenido a main desde otra rama
1. Actualizar la rama main:
```bash
git switch main
```
```bash
git pull
```
2. Pasarse a la rama:
```bash
git switch tu-rama
```
3. Añadir el contenido de main a tu rama
```bash
git merge origin/main
```
4. Si hay conflictos, se indican los archivos conflictivos que deben ser modificados. Vas a entrar en un estado de MERGING que se verá reflejado en el nombre de la rama actual.
5. Para solucionar los conflictos, hay que editar los archivos y eliminar los flags que aparecen.
6. Tras solucionarlo, hacer push de esta rama:
```bash
git add .
```
```bash
git commit -m "Comentario"
```
```bash
git push
```
7. Pasar el contenido actualizado de la rama a main.
```bash
git switch main
```
```bash
git merge tu-rama
```
```bash
git push
```

### Hacer un push
Añadir los archivos (el punto es para añadir todo):
```bash
git add .
```
Hacer el commit:
```bash
git commit -m "Comentario"
```
Hacer push:
```bash
git push
```
Ver el estado de git local:
```bash
git status
```
Ver el listado de commits (para salir pulsar la tecla **q**):
```bash
git log
```
