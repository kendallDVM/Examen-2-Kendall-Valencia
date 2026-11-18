# Clínica Veterinaria

Este proyecto es un programa de escritorio hecho en Java que sirve para registrar clientes y sus mascotas. Los datos se guardan en una base de datos MySQL, así que antes de abrir el programa hay que preparar la base de datos y decirle al programa cómo conectarse a ella.

## Cómo preparar la base de datos

Primero abra MySQL Workbench con su usuario administrador y abra el archivo `database/01_veterinaria.sql`. Dentro del archivo va a encontrar dos contraseñas de ejemplo: `CAMBIAR_PASSWORD_DEFINER` y `CAMBIAR_PASSWORD_APP`. Cámbielas por dos contraseñas suyas, que sean diferentes entre sí, y luego ejecute todo el archivo. Esto crea la base de datos `veterinaria`, sus tablas, los usuarios y algunos datos de ejemplo.

## Cómo conectar el programa

El programa se conecta a MySQL con el usuario `vet_app`, nunca con el usuario administrador. Para que sepa a dónde conectarse, hay que darle tres variables de entorno. En IntelliJ entre a Run, luego a Edit Configurations, elija la configuración de `Main` y en la parte de Environment variables escriba lo siguiente, cambiando la contraseña por la que usted le puso a `vet_app`:

`VET_DB_URL=jdbc:mysql://localhost:3306/veterinaria?serverTimezone=UTC;VET_DB_USER=vet_app;VET_DB_PASSWORD=su_contraseña`

## Cómo abrir el programa

Abra en IntelliJ la carpeta donde está el archivo `pom.xml`. Si es la primera vez, vaya a la pestaña de Maven y presione el botón de recargar para que se descargue el conector de MySQL. Después ejecute la clase `Main` y se abrirá la ventana de la clínica.

## Cómo usarlo

En la pestaña de Clientes puede registrar un cliente escribiendo su cédula, nombre, teléfono y edad, y también puede ver la lista de todos los clientes. En la pestaña de Mascotas puede registrar, actualizar o eliminar una mascota, ver todas las mascotas junto con el nombre de su dueño y buscar las mascotas de un cliente por su cédula.

Antes de registrar una mascota, su dueño ya debe estar registrado como cliente. La edad del cliente debe estar entre 18 y 60 años, la edad de la mascota entre 0 y 40 años, y ningún campo puede quedar vacío. Si algo no está bien, el programa le muestra un mensaje explicando el problema y sigue funcionando normalmente.

## Relación y claves de la base de datos

La base de datos tiene dos tablas: `cliente` y `mascota`. En `cliente` la clave primaria es la `cedula`, así que no pueden existir dos clientes con la misma cédula. Además, la tabla `cliente` guarda la edad del cliente, y la base de datos solo acepta edades entre 18 y 60 años. En `mascota` la clave primaria es el `codigo`, así que tampoco se repiten los códigos de mascota.

La relación entre las dos tablas es de uno a muchos: un cliente puede tener varias mascotas, pero cada mascota pertenece a un solo cliente. Esto se logra con la columna `cedula_cliente` de la tabla `mascota`, que es una clave foránea hacia `cliente.cedula`. Gracias a eso no se puede registrar una mascota cuyo dueño no exista, y tampoco se puede borrar un cliente que todavía tenga mascotas, porque la clave foránea usa `ON DELETE RESTRICT`. Los datos del cliente, como su nombre y teléfono, se guardan solo en la tabla `cliente`, y la tabla `mascota` únicamente guarda la cédula del dueño, para no repetir información.

## Cómo comprobar los permisos

El usuario `vet_definer` es el dueño de los procedimientos y solo tiene permiso para leer e insertar clientes, y para leer, insertar, modificar y borrar mascotas. El usuario `vet_app`, que es el que usa el programa, solo puede ejecutar los siete procedimientos y no puede tocar las tablas directamente. Para comprobarlo, abra en Workbench una conexión con el usuario `vet_app` y ejecute `CALL sp_listar_mascotas();`, que debe mostrar las mascotas. Después ejecute `SELECT * FROM mascota;`, que debe dar el error 1142 de permiso denegado.


 
