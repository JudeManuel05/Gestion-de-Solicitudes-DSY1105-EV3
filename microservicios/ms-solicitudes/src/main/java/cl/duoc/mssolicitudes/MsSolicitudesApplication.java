package cl.duoc.mssolicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada del microservicio de solicitudes (puerto 8082). */
@SpringBootApplication
public class MsSolicitudesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsSolicitudesApplication.class, args);
    }
}
