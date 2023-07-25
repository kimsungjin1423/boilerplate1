package com.thejoeun.practice1.boilerplate1.config;

import com.thejoeun.practice1.boilerplate1.config.jwt.JwtAccessDeniedHandler;
import com.thejoeun.practice1.boilerplate1.config.jwt.JwtSecurityConfig;
import com.thejoeun.practice1.boilerplate1.config.jwt.JwtTokenProvider;

import com.thejoeun.practice1.boilerplate1.config.jwt.JwtAuthenticationEntryPoint;
import com.thejoeun.practice1.boilerplate1.config.oauth.OAuth2CustomAuthenticationSuccessHandler;
import com.thejoeun.practice1.boilerplate1.config.oauth.OAuth2CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Component
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2CustomUserService oAuth2CustomUserService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                ;
    }

    /**
     * desc: spring security 설정 부분
     * 참고 공식url: https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
     * @param http
     * @param introspector
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
            , HandlerMappingIntrospector introspector
    ) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
//            .authorizeHttpRequests(request -> request
//                    .requestMatchers(mvcMatcherBuilder.pattern("/auth3/**")).permitAll()
//                    .requestMatchers(mvcMatcherBuilder.pattern("/auth4/**")).permitAll()
//                    .anyRequest().authenticated()
//            )
                .authorizeHttpRequests()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/**")).permitAll()
//            .requestMatchers(AntPathRequestMatcher.antMatcher("/auth2/**")).permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(customAuth2SuccessHandler())
                .userInfoEndpoint() // OAuth 2.0 Provider로부터 사용자 정보를 가져오는 엔드포인트를 지정하는 메서드
                .userService(oAuth2CustomUserService)   // OAuth 2.0 인증이 처리되는데 사용될 사용자 서비스를 지정하는 메서드
        ;
        http.apply(new JwtSecurityConfig(jwtTokenProvider));

        return http.build();
    }

    @Bean
    public OAuth2CustomAuthenticationSuccessHandler customAuth2SuccessHandler() {
        return new OAuth2CustomAuthenticationSuccessHandler(oAuth2CustomUserService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public DataSource dataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//                .build();
//    }
//
//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(user);
//        return users;
//    }
}