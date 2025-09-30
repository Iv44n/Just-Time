# Just Time API

Backend para la aplicación Just Time.

## Prerrequisitos

- Java 21 o superior
- Maven

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Iv44n/just_time.git
   ```
2. Navega al directorio del proyecto:
   ```bash
   cd api
   ```
3. Instala las dependencias:
   ```bash
   ./mvnw install
   ```

## Configuración

1. Crea una copia del archivo `.env.example` y renómbrala a `.env`:
   ```bash
   cp .env.example .env
   ```
2. Modifica el archivo `.env` con la configuración de tu base de datos y otras variables de entorno.

## Ejecución

Para iniciar la aplicación, ejecuta el siguiente comando:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.
