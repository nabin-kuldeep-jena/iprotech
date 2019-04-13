package com.asjngroup.deft.entrypoint.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@ComponentScan(basePackages="com.asjngroup.deft")
public class DeftApplicationBootController
{

	@RequestMapping( "/test" )
	public Map<String, String> test( @RequestParam( value = "name", defaultValue = "Deft Admin-->Nabin Kuldeep Jena" ) String name )
	{
		Map<String, String> result = new HashMap<>();
		result.put( "message", String.format( "Wellcome to Deft-->, %s", name ) );
		return result;
	}

	public static void main( String[] args )
	{
		SpringApplication.run( DeftApplicationBootController.class, args );
	}
}