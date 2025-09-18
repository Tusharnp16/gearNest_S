package com.example.gearnest.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    // ✅ Restrict access to admin URLs

 //.formLogin(Customizer.withDefaults())
//
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //             .csrf(csrf -> csrf.disable())
    //             .authorizeHttpRequests(auth -> auth
    //                     .requestMatchers("/admin/**").hasRole("ADMIN")
    //                     .anyRequest().permitAll())
    //             .formLogin(form -> form
    //                     .loginPage("/admin/dashboard")
    //                     .defaultSuccessUrl("/admin/dashboard") // 👈 custom login page
    //                     .permitAll())
    //             .logout(logout -> logout.permitAll());

    //     return http.build();
    // }


    //  @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //             .csrf(csrf -> csrf.disable())
    //             .authorizeHttpRequests(auth -> auth
    //                     .requestMatchers("/admin/**").permitAll() 
    //                     .anyRequest().permitAll())
    //             .formLogin(form -> form.disable()) 
    //             .logout(logout -> logout.disable());

    //     return http.build();
    // }


    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").permitAll() // abhi ke liye sab allowed
            .anyRequest().authenticated())
        .formLogin(Customizer.withDefaults()); // 👈 yeh line default login page enable karegi

    return http.build();
}



//     @Bean
// public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//     http
//         .csrf(csrf -> csrf.disable())
//         .authorizeHttpRequests(auth -> auth
//             .requestMatchers("/admin/**").permitAll() // 👈 direct access for now
//             .anyRequest().permitAll())
//         .formLogin(form -> form
//             .loginPage("/login")   // 👈 point to your custom login page
//             .defaultSuccessUrl("/admin/dashboard") // after login redirect here
//             .permitAll())
//         .logout(logout -> logout.permitAll());

//     return http.build();
// }


}
