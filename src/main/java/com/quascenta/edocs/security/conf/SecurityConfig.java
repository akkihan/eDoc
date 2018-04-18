package com.quascenta.edocs.security.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.apache.commons.io.filefilter.FileFilterUtils.and;


@Configuration
@EnableWebSecurity
@ComponentScan
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .authorizeRequests()
                .anyRequest().permitAll()
                .antMatchers(HttpMethod.GET, "/documentObjects/**").hasAnyRole("USER_READ","ADMIN")
                .antMatchers(HttpMethod.GET,"/folderObjects/**").hasAnyRole("USER_READ","ADMIN")
                .antMatchers(HttpMethod.GET,"/FileFolders/**").hasAnyRole("USER_READ","ADMIN")
                .antMatchers(HttpMethod.POST,"/documentObjects/**").hasAnyRole("USER_ADD","ADMIN")
                .antMatchers(HttpMethod.POST,"/folderObjects/**").hasAnyRole("USER_ADD","ADMIN")
                .antMatchers(HttpMethod.POST,"/folderObjects/**").hasAnyRole("USER_ADD","ADMIN")
                 .antMatchers(HttpMethod.PUT,"/documentObjects/*/review/accept").hasRole("USER_APPROVE")
                .antMatchers(HttpMethod.PUT,"/documentObjects/*/review/reject").hasRole("USER_APPROVE")
                .antMatchers(HttpMethod.DELETE,"documentObjects/**").hasAnyRole("USER_EDIT","ADMIN")
                .antMatchers(HttpMethod.DELETE,"folderObjects/**").hasAnyRole("USER_EDIT","ADMIN")
                .antMatchers(HttpMethod.PATCH,"documentObjects/**").hasAnyRole("USER_EDIT","ADMIN")
                .antMatchers(HttpMethod.PATCH,"folderObjects/**").hasAnyRole("USER_EDIT","ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        .withUser("userRead").password("user").roles("USER_READ")
                .and()
        .withUser("userADD").password("user").roles("USER_ADD","USER_READ")
                .and()
        .withUser("userEdit").password("user").roles("USER_READ","USER_EDIT")
                .and()
        .withUser("userApprove").password("user").roles("USER_READ","USER_APPROVE")
                .and()
        .withUser("admin").password("admin").roles("ADMIN")
                .and()
        .withUser("user1").password("user").roles("ADMIN")
                .and()
        .withUser("user2").password("user").roles("ADMIN");

    }
}
