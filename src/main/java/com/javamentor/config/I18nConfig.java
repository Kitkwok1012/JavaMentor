package com.javamentor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Internationalization Configuration
 * 
 * Features:
 * - Cookie-based locale storage
 * - Support for: en (default), zh, zh_TW
 * - Locale change via ?lang=zh query parameter
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    /**
     * Cookie-based locale resolver
     * Stores locale in "javamentor_locale" cookie
     * Default: English
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName("javamentor_locale");
        resolver.setCookieMaxAge(31536000); // 1 year
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    /**
     * Interceptor to change locale via query param: ?lang=zh
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
