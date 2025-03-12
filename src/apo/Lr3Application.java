package apo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;

@SpringBootApplication(exclude = CacheAutoConfiguration.class)
public class Lr3Application
{

	public static void main(String[] args)
	{
//		System.setProperty("debug", "true");
		SpringApplication app = new SpringApplication(Lr3Application.class);
		app.run(args);
	}

}
