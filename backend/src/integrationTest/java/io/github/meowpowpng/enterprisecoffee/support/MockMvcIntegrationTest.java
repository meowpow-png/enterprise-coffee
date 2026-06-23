package io.github.meowpowpng.enterprisecoffee.support;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntegrationTest
@AutoConfigureMockMvc
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MockMvcSupport.class)
public @interface MockMvcIntegrationTest {}
