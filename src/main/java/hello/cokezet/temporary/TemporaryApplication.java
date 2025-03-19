package hello.cokezet.temporary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TemporaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemporaryApplication.class, args);
    }

}
