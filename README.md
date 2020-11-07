# APIcoches

APIcoches es una plataforma que nos permite gestionar nuestra Base de Datos de concesionarios y coches.

## Iniciar la API

Para ello abriremos una terminal en una carpeta en la que deseemos tener la API, en ella introduciremos el comando:

```
git clone https://github.com/lorenzovigo/APIcoches/
```

Nos dirigimos con la terminal a la carpeta del código y ejecutamos el siguiente comando para iniciar la API:

```
mvn spring-boot:run
```

Para este último comando **será necesario tener instalado maven**. Esto se puede hacer mediante

```
sudo apt install maven
```

## Utilizar la API

Para utilizar nuestra API es necesario hacerlo desde **Insomnia** o **Postman**. Recomendamos la primera opción. Además, en la carpeta insomnia se puede encontrar un workspace exportado de Insomnia con las llamadas disponibles. Para utilizar estas llamadas habrá que completar formularios Json, cambiar variables entre llaves ({ejemplo}) por su valor y añadir valores tras = para que funcionen correctamente.

Haremos las llamadas siempre a localhost:8080. Podéis consultar la documentación de cada uno de los softwares para consultar acerca de su funcionamiento.

## Llamadas disponibles:

*Observaciones:* Por defecto la API se ejecuta en localhost:8080, en caso contrario hemos de cambiar la dirección en las llamadas siguientes.

**GET localhost:8080/concesionarios**

Devuelve una lista de todos los concesionarios de la cadena guardados en la Base de Datos.

**POST localhost:8080/concesionarios**

Permite la introducción de un nuevo concesionario a la Base de Datos.

Requiere un body Json de este tipo:
```
{
  "direccion": "<valor>"
}
```
, donde <valor> lo cambiariemos por la dirección del concesionario, la cual debe ser única en toda la Base de Datos.

**GET localhost:8080/concesionarios/{id}**

Devuelve la información de un concesario en concreto, el cual tenga un identificador igual a {id}, donde {id} es un número.

*Observaciones:* No está disponible la opción de hacer una llamada DELETE ya que no tiene sentido a la hora de crear el informe de beneficios de la cadena. Aunque el concesionario cierra, sus movimientos siguen siendo relevantes. Además, como decisión de diseño, hemos optado por añadir un identificador numérico a los concesionarios, para así facilitar las referencias a la hora de asignar un concesionario a cada coche.

**GET localhost:8080/coches?sort=**

Devuelve una lista con todos los coches guardados en la base de datos.

**GET localhost:8080/concesionarios/{id}/coches?sort=**

Devuelve una lista con todos los coches asignados a un concesionario con identificador {id}, donde {id} es un número.

*Observaciones:* En las dos últimas llamadas podemos añadir opciones para ordenar la lista. Son las siguientes:

IA: para ordenar los coches por fecha de ingreso, en orden ascendente

ID: para ordenar los coches por fecha de ingreso, en orden descendente

VA: para ordenar los coches por fecha de venta, en orden ascendente

VD: para ordenar los coches por fecha de venta, en orden descendente

*Ejemplo: localhost:8080/coches?sort=IA*

**GET localhost:8080/coches/{id}**

Devuelve la información del coche con identificador igual a {id}, el cual será un número

**DELETE localhost:8080/coches/{id}/force**

Elimina un coche de la base de datos. Se recomienda usarlo solo en caso de necesidad por algún tipo de error.

**PUT localhost:8080/coches/{id}/vender?precio={precio}**

Indicamos en la base de datos que hemos vendido el coche con identificador {id} al precio indicado por la variable {precio}. Introducimos en esta variable solo el número del precio, sin el símbolo de euros.

**PUT localhost:8080/coches/{id}/matricular?matricula={matricula}**

Matriculamos el coche con identificador {id} con el valor incluido en la variable {matricula}.

**DELETE localhost:8080/coches/{id}**

Deja el coche con identificador {id} como 'No disponible'.

**POST localhost:8080/coches**

Incluimos un coche nuevo en la base de datos. Esta acción requiere un Body en formato json siguiendo este esquema:
```
{
  "marca": <marca>,
  "coste": <coste>,
  "fechaVenta": <fechaVenta>,
  "fechaIngreso": <fechaIngreso>,
  "vendido": <vendido>,
  "matricula": <matricula>,
  "precio": <precio>,
  "disponible": <disponible>,
  "localId": <localId>
 }
 ``` 
<marca> y <matricula> serán valores de texto. <coste> y <precio> serán valores numéricos sin el símbolo de euro. <fechaVenta> y <fechaIngreso> serán fechas en el formato "yyyy-MM-dd hh:mm:ss". <vendido> y <disponible> serán booleanos (false o true). <localId> será el identificador de uno de los concesionarios existentes.

Solo las filas de marca, coste (por defecto, 0), fecha ingreso (por defecto añadirá la fecha actual) y localId son obligatorias.

Además, hemos de cumplir con las siguientes restricciones: localId ha de ser un identificador de un concesionario de la base de datos, rellenaremos los valores de precio y fechaVenta si y solo si el coche se ha vendido.

**GET localhost:8080/beneficios**

Muestra un balance de los gastos e ingresos por concensionario y de toda la cadena.
