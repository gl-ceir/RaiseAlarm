package com.gl.alarm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("classpath:application.properties")
@PropertySources({
//@PropertySource(value = {"file:${spring.config.location}/application.properties"}, ignoreResourceNotFound = true),
@PropertySource(value = {"classpath:application.properties"}, ignoreResourceNotFound = true)
})
public class PropertiesReader {

	@Value("${spring.jpa.properties.hibernate.dialect}")
	public String dialect;
}
