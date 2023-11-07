# Documentación del Proyecto Libreria API

## Descripción

Proyecto de administración, control de ventas y stock de liberia con traducción a Español, portugues y ingles. 


## Características
Este proyecto es el desarrollo full stack de un sistema de control de ventas y stock.  **con tecnología Java utilizando el framework Spring Boot**. E
A continuación, se detallan las principales características de este proyecto:

1 **Almacenamiento en Base de Datos**: Se implementa un esquema de base de datos en MySQL, para almacenar toda la información necesaria. 

2 **API de Suministro de Información**: Se crea una API que ofrece diversas funcionalidades para obtener información de Marvel, incluyendo:
    - Búsqueda por producto.
    - Obtención del listado de clientes, productos y vendedores
    - Acceso a la imagen y descripción del producto. 
    - Listado completo de compras realizadas.
    - Filtrado por nombre de producto
    - Registro de productos, empleados y ventas.

5. **Autenticación con Spring Security**: La API implementa un medio y esquema de autenticación utilizando Spring Security. 

6. **Scripts de Base de Datos**: El archivo README de la aplicación incluye scripts de base de datos, que contienen datos iniciales necesarios para ejecutar las APIs en su primer uso.




## Paquetes del Proyecto
El proyecto está organizado en los siguientes paquetes:

- `com.example.app.auth.handler`: Contiene metodo hanbler login de usuario.
- `com.example.app.controllers`: Controladores web para manejar las solicitudes de la API.
- `com.example.app.models.dao`: Contiene las interfaces de implementacion de CrudRepository y PagingAndSortingRepository.
- `com.example.app.models.entity`: Contiene las entidades mapeadas con JPA.
- `com.example.app.models.service`: Contiene los servicios necesarios para la implementación de Crud.


## Spring Security: Usuarios y Roles
Los scripts incluyen la creación de dos roles y dos usuarios.
Estos roles son **"ADMIN" y "USER"** y los dos usuarios son **"rafa" y "admin" con las contraseñas "12345" y "12345"** respectivamente.
