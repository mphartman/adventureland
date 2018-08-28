package hartman.games.adventureland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;

@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static String CURIE_NAMESPACE = "adventureland";

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider(CURIE_NAMESPACE, new UriTemplate("/docs/{rel}.html"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
