# Just Time API

Backend para la aplicación [Just Time](https://github.com/Iv44n/just_time). La API se encarga de gestionar la autenticación y los datos de los usuarios.

## ✅ Prerrequisitos

-   Java 21 o superior.
-   Maven.
-   PostgreSQL.
-   Un cliente de API como [Postman](https://www.postman.com/) o [Insomnia](https://insomnia.rest/).

## 🚀 Instalación

1.  Clona el repositorio:

    ```bash
    git clone https://github.com/Iv44n/Just-Time
    ```

2.  Navega al directorio del proyecto:

    ```bash
    cd api
    ```

3.  Instala las dependencias:

    ```bash
    ./mvnw install
    ```

## ⚙️ Configuración

1.  Crea una copia del archivo `.env.example` y renómbrala a `.env`:

    ```bash
    cp .env.example .env
    ```

2.  Modifica el archivo `.env` con la configuración de tu base de datos y otras variables de entorno:

    ```
    DATABASE_URL=jdbc:postgresql://localhost:5432/justtime
    DATABASE_USERNAME=user
    DATABASE_PASSWORD=password
    JWT_SECRET=your-secret-key
    ACCESS_TOKEN_EXPIRATION=3600000
    REFRESH_TOKEN_EXPIRATION=604800000
    ```

## 🗃️ Base de datos

1.  Asegúrate de tener PostgreSQL instalado y en ejecución.
2.  Crea una nueva base de datos con el nombre que especificaste en la variable `DATABASE_URL`.
3.  Ejecuta el script `seed.sql` para crear las tablas necesarias:

    ```bash
    psql -U tu-usuario -d justtime -a -f seed.sql
    ```

    > Reemplaza `tu-usuario` con tu nombre de usuario de PostgreSQL.


## ▶️ Ejecución

Para iniciar la aplicación, ejecuta el siguiente comando:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.
