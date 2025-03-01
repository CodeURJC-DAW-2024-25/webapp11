package es.daw.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	CourseStatisticsAuthorizationManager courseStatisticsAuth; // Inyectamos el Custom Authorization Manager

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/signUp/**").permitAll() // Allow access to static resources
						.requestMatchers("/course/**").permitAll()
						.requestMatchers("/courses/**").permitAll()
						.requestMatchers("/coursesPage/**").permitAll()
						.requestMatchers("/index/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/**").permitAll() // Esto hay que quitarlo, pero sin esto no
						// funciona
						// PRIVATE PAGES
						.requestMatchers("/new_course").hasAnyRole("USER")
						.requestMatchers("/edit_course/*").hasAnyRole("USER")
						.requestMatchers("/profile").hasAnyRole("USER")
						.requestMatchers("/profile/taughtCourses/**").hasAnyRole("USER")
						// .requestMatchers("/course/*/statistics").access(courseStatisticsAuth) // usa
						// la lógica personalizada
						.requestMatchers("/course/*/statistics").hasAnyRole("USER") // Ns como hacer
						// que solo el profesor/admin pueda ver las estadisticas
						.requestMatchers("/admin/*").hasAnyRole("ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureHandler((request, response, exception) -> {
							response.sendRedirect("/error");
						})
						.defaultSuccessUrl("/")
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}
}