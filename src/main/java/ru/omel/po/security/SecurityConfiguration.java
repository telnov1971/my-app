package ru.omel.po.security;

import ru.omel.po.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_URL="/login";
    private static final String LOGIN_PROCESSING_URL ="/login";
    private static final String LOGOUT_SUCCESS_URL ="/login";
    private static final String LOGIN_FAILURE_URL ="/login?error";

    /**
     * Требование входа для доступа к внутренним страницам и настройки формы входа.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        // Не используйте Spring CSRF здесь, чтобы иметь возможность
        // использовать обычный HTML для страницы входа в систему
        http.csrf().disable() // Vaadin уже имеет встроенную поддержкумежсайтовых запросов.
        // Зарегистрируйте кэш пользовательских запросов,
        // который сохранит неавторизованные запросы,
        // чтобы перенаправить после входа в систему.
        .requestCache().requestCache(new CustomRequestCache())
        // Ограничим доступ к нашему приложению
        .and().authorizeRequests()
                .antMatchers("/login","/logout",
                        "/**/profile/",
                        "/**/forgot",
                        "/static/**",
                        "/icons/**",
                        "/activate/**",
                        "/reset/**").permitAll()
        // Разрешим все внутренние запросы потока связанных с Vaadin.
        .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
        // Разрешим все запросы пользователей, вошедших в систему.
        .anyRequest().authenticated() // (4)
        // Настройм страницу входа в систему.
        .and().formLogin().loginPage(LOGIN_URL).permitAll() // Настройм URL-адрес страницы входа и разрешим доступ всем.
        .loginProcessingUrl(LOGIN_PROCESSING_URL) // Настройте URL-адрес для входа в Spring Security для ожидания POST-запросов.
        .failureUrl(LOGIN_FAILURE_URL);
        // Настроим выход
        //.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }

    /**
     * Разрешает доступ к статическим резурсам минуя Spring security.
     */
    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers(
        // Vaadin Flow статические ресурсы
                "/VAADIN/**",
        // стандартная favicon URI
                "/favicon.ico",
        // the robots exclusion standard
                "/robots.txt",
        // web application manifest при разработке прогрессивного веб-приложения
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                "/sw-runtime-resources-precache.js",
                "/icons/**",
        // (development mode) Разрешает доступ к веб-ресурсам в режиме разработки
                "/frontend/**",
        // (development mode) webjars Разрешает доступ к веб-ресурсам в режиме разработки
                "/webjars/**",
        // (production mode) Разрешает доступ к веб-ресурсам в рабочем режиме
                "/frontend-es5/**", "/frontend-es6/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CustomRequestCache requestCache() {
        return new CustomRequestCache();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

}
