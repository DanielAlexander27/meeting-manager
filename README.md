# Sistema de Gestión de Reuniones
Integrantes:
- Gabriel Ávalos - 00327381
- David Bucheli - 00329939
- Daniel Andrade - 00330220

## Setup

Sigue estos pasos para configurar el entorno:

1. Copia el archivo `docker-compose.yml` en una carpeta vacía. Luego, dirígete hacia esa carpeta desde la terminal.

2. Ejecuta el comando `docker-compose build`
3. Para iniciar el servidor central, ejecuta el siguiente comando:
   ```
   docker-compose up central-server
   ```
4. Para iniciar el servidor de un empleado, abre una nueva terminal, dirígete hacia la ubicación del archivo `docker-compose.yml` y ejecuta el comando.
   ```
   docker-compose run -it [nombre-contenedor]
   ```
   Las opciones de `[nombre-contenedor]` son `alice-white`, `bob-smith`, `carol-simpson`, `david-black` y `eva-brown`.

   Por ejemplo, para iniciar el servidor de Alice White, sería de la siguiente forma:
   ```
   docker-compose run -it alice-white
   ```

## Uso
Una vez esté corriendo el contenedor del empleado correspondiente, los usuarios interactúan con el sistema de gestión de reuniones a través del menú en la consola:
1. *Panel de Bienvenida*: Muestra un mensaje de bienvenida con el nombre del usuario y su nombre de usuario.
2. *Opciones*:
    - Ver reuniones programadas.
    - Crear una nueva reunión con tema, ubicación e invitados.
    - Modificar una reunión existente, incluyendo cambios en el tema, horario o lista de invitados.
    - Sincronizar reuniones con el almacenamiento centralizado.
    - Recuperar reuniones desde el almacenamiento local (pueden estar desactualizadas).
    - Salir de la aplicación.
   
## Características
- *Gestión de Empleados y Reuniones*: Cada empleado puede ver, crear y gestionar sus reuniones utilizando un menú fácil de usar en la consola.
- *Configuración Multiusuario*: Entorno con contenedores Docker para simular varios empleados junto con un servidor central.
- *Sincronización de Datos*: Los datos de las reuniones pueden sincronizarse entre el almacenamiento local y el almacenamiento centralizado.
- *Programación Dinámica*: Los usuarios pueden invitar a otros empleados, especificar temas de reuniones, elegir ubicaciones y definir horarios de inicio y fin.
- *Manejo de Errores*: Validadores que garantizan que los datos, como las fechas, sigan un formato prescrito (yyyy-MM-dd HH:mm:ss).

## Consideraciones Técnicas
El presente proyecto no hace uso del `employee.properties`. Puesto que, el cliente (empleado) al establecer conexión con el servidor central, se crea una conexión bidireccional a través del socket. La forma de identificar a cada empleado es a través del `EMPLOYEE_USERNAME`.

La imagen de cada empleado define como variable de ambiente el nombre y nombre de usuario del empleado. El programa del empleado lee y envía el `username` al servidor para ser identificado. De esta forma, no se queman los valores de los puertos y en caso de requerir agregar un nuevo empleado, basta con editar el `docker-compose.yml` y agregar una imagen en el siguiente formato:

		[nombre]-[apellido]:  
		  image: danandrade27/java-meeting:employee-client  
		  container_name: [nombre]-[apellido]  
		  ports:  
		    - "8081:8081"  
		  environment:  
		    - EMPLOYEE_USERNAME=[nombre]-[apellido]
		    - EMPLOYEE_NAME=[nombre] [apellido]  
		  depends_on:  
		    - central-server  
		  volumes:  
		    - ./server-output:/app/server-output

**Importante**. Se debe respetar el formato de `[nombre]-[apellido]`.