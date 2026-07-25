package team.themoment.thup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ThupApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThupApplication.class, args);
    }

}
