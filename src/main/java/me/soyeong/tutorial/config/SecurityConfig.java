package me.soyeong.tutorial.config;

import me.soyeong.tutorial.jwt.JwtSecurityConfig;
import me.soyeong.tutorial.jwt.JwtAccessDeniedHandler;
import me.soyeong.tutorial.jwt.JwtAuthenticationEntryPoint;
import me.soyeong.tutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // 기본적인 웹 보안 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize annotation을 메소드 단위로 사용하기 위해 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ){
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web){
        web
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.cio"
                ); // h2-console 하위와 favicon 관련 요청은 무시
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()// token 방식 사용으로 csrf를 disable

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // exception-handler를 우리가 만든 클래스로 추가
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and() // h2-console 설정 추가
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session 사용 안 하므로 STATELESS로 설정

                .and()
                .authorizeRequests()
                .antMatchers("/api/hello").permitAll() // "/api/hello" 에 대한 요청은 인증없이 진행
                .antMatchers("/api/authenticate").permitAll() // login api: token 없는 상태로 요청 들어오므로 열어둠
                .antMatchers("/api/signup").permitAll() // 회원 가입 api: token 없는 상태로 요청 들어오므로 열어둠
                .anyRequest().authenticated()

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
