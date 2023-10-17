package com.laioffer.twitch;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.sql.DataSource;


@Configuration
public class AppConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //filterChain: 一层层的filter，都通过就允许access
        http
                .csrf().disable()//csrf是一种攻击的方式，如果不在同一个服务器，就不让通过，开发时通常disable;
                //->是lambda,后面的function的参数是auth; 其实就是一个callback, auth是authorizeHTTP里面丢出来的;
                //里面有一个function叫customize会被call;
                //requestMatchers是一个priority的从上到下的关系;如果上面都没有match，最后有一个终极的剩下的所有的都要authentication;
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()//允许前端被访问; atCommonLocations包括了主要的前端接口;
                                //有些文件第一行没有specify,在下一行specify;这是java语言; 用了所谓的fluent API;
                                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/*.json", "/*.png", "/static/**").permitAll()
                                //hello下面也没写啥API;
                                .requestMatchers("/hello/**").permitAll()
                                //下面这些是真正的API：/login和/logout不需要authenticate;这些是后续操作;.POST可以不写，写上是仅限于POST
                                .requestMatchers(HttpMethod.POST, "/login", "/register", "/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/recommendation", "/game","/search").permitAll()
                                .anyRequest().authenticated() //这个是兜底的authentication,如果写在最上面，就是所有的都要authenticate;
                )
                .exceptionHandling()//出错了怎么办？自动带到login的界面;下面unauthorized阻止了redirect;
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .formLogin()//twitch用的是os2,我们用form login(session-based authentication);
                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value()))//登陆成功，就return一个NO_CONTENT;前端自己去改页面;如果不specify，也会自作主张redirect;
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())//成功redirect到登录界面;failure直接return出错的error而不是redirection;
                .and()
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));//logout成功不要redirect;
        return http.build();
    }





    //我们自己来进行authentication:
    @Bean //为什么这里还有@Bean? 都是dependency injection; 提供dependency;
    //还需要加annotation是因为这些都没有源代码，不是我们自己创建的;
    //咖啡豆：非常重要的dependency;
    UserDetailsManager users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource); //interface,spring从db check用户是否存在；
    }


    @Bean
    PasswordEncoder passwordEncoder() { //为什么要encode password? 因为如果hacker黑了网站的密码，就可以去其他地方登录了；
        //这些网站都想让我们用复杂的密码;
        //所以现在所有的网站的密码都是加密的; one-way encripted
        //用户每次登陆都要用密钥验证一遍；
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();//也是interface;
    }





}
