/**
 * Entry point to spring boot application. It responses for creating all Spring Beans
 * like @Controller, @Service and @Configuration
 * All of this Spring beans types are singletons by default.
 */
package org.opendap.harvester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

//added Spring Boot ServletInitializer so that './gradew war' command 
//	can build working war file  

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories
public class HarvesterApplication extends SpringBootServletInitializer {
	private static final Logger log = LoggerFactory.getLogger(HarvesterApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HarvesterApplication.class);
	}
    /*
     * Enable this if you want to have both http and https connectors in embedded
     * tomcat by Spring Boot
     */
	
	/*
    @Bean
    public Integer port() {
        return SocketUtils.findAvailableTcpPort();
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port());
        return connector;
    }
    */
	
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(HarvesterApplication.class, args);
        log.info("Application has been started");
    }
}
