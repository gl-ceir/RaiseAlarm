package com.gl.alarm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component

@PropertySources({
    @PropertySource(value = {"file:application.properties"}, ignoreResourceNotFound = true),
    @PropertySource(value = {"file:configuration.properties"}, ignoreResourceNotFound = true)
})

public class PropertiesReader {

	@Value("${spring.jpa.properties.hibernate.dialect}")
    public String dialect;

    @Value("${appdbName}")
    public String appdbName;

    @Value("${repdbName}")
    public String repdbName;

    @Value("${auddbName}")
    public String auddbName;

    @Value("${oamdbName}")
    public String oamdbName;
}
