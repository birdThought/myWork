package com.lifeshs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass=true)
@ImportResource(locations= {"classpath:umeng.xml"})
public class ProductApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
	}

}
