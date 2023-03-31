package dev.lotnest.sombrero;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sombrero {

    @SneakyThrows
    public static void main(String... args) {
        SpringApplication.run(Sombrero.class, args);
    }
}
