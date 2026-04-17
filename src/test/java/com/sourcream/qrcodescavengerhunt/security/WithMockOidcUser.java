package com.sourcream.qrcodescavengerhunt.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockOidcUserSecurityContextFactory.class)
public @interface WithMockOidcUser {
    String email() default "john.doe@example.com";
    String name() default "John Doe";
    String[] roles() default {"USER"};
}
