package luis.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.servlet.Filter;

/**
 * Classe principal SpringBoot.
 * @author Luis
 */
@SpringBootApplication
public class BootInitializer extends SpringBootServletInitializer   {

    private static final Logger logger = LoggerFactory.getLogger(BootInitializer.class);


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootInitializer.class);
    }

    /**
     * Sobe o SpringBoot.
     * @param args
     */
    //classe principal do main
    public static void main(String[] args) {
        SpringApplication.run(BootInitializer.class, args);
    }


}
