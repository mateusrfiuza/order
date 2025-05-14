package challenge.order.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("challenge.order.dataprovider.database")
@EntityScan("challenge.order.dataprovider.database")
public class EnablePersistenceConfig {

}
