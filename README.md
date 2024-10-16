# EpicFitApp
## GitHub
### Creación del repositorio en local
Clonar un repositorio, donde la url es la ruta que se copia al entrar al repositorio:
```bash
git clone git@github.com:propietario/nombreProyecto.git
```
Establecer el nombre y correo:
```bash
git config user.name "Nombre"
git config user.email "tu-correo"
```
### Establecer la conexión mediante SSH
Crear una clave SSH para conectarse desde el equipo al repositorio remoto (GitHub):
1. Crear una clave ssh:
```bash
ssh-keygen
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
Cambiarse a una rama:
```bash
git switch nombre
```
Eliminar rama local:
```bash
git branch -d nombre-de-tu-rama
```
Eliminar rama remota:
```bash
git push origin --delete nombre-de-tu-rama
```

### Gestionar merge
Actualizar tu rama:
```bash
git fetch origin
git merge origin/main
```
Tras pasar el contenido de la rama main a la tuya, pueden surgir conflictos que se deben solucionar.
Una vez solucionados los posibles conflictos, pasar a la rama main:
```bash
git switch main
```
Pasar el contenido de tu rama al main:
```bash
git merge nombre-de-tu-rama
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
