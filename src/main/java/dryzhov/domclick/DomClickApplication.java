package dryzhov.domclick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("dryzhov.domclick.domain")
@EnableJpaRepositories("dryzhov.domclick.repository")
public class DomClickApplication {
    public static void main(String[] args) {
        SpringApplication.run(DomClickApplication.class, args);
    }
}
