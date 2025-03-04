package es.daw.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	// @Autowired
	// AuthorizationManager<RequestAuthorizationContext> courseStatisticsAuth; //
	// Custom Authorization Manager inyected

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
						.requestMatchers("/signUp/**").permitAll()
						.requestMatchers("/login").permitAll()
						.requestMatchers("/course/**").permitAll()
						.requestMatchers("/courses/**").permitAll()
						.requestMatchers("/coursesPage/**").permitAll()
						.requestMatchers("/index/**").permitAll()
						.requestMatchers("/error").permitAll()
						.requestMatchers("/error-login").permitAll()
						.requestMatchers("/newUser").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers("/getCourses").permitAll()
						.requestMatchers("/getCoursesByTopic").permitAll()
						.requestMatchers("/showCourse/*").permitAll()
						.requestMatchers("/showCourses/*").permitAll()
						.requestMatchers("/getCoursesByTitle").permitAll()
						.requestMatchers("/findCourses/*").permitAll()
						.requestMatchers("/image/*").permitAll()
						.requestMatchers("/charts").permitAll()
						.requestMatchers("/mostInscribedCathegories").permitAll()
						.requestMatchers("/mostCoursesCategories").permitAll()
						.requestMatchers("/mostInscribedCategories").permitAll()

						// PRIVATE PAGES
						.requestMatchers("/course/enroll").hasAnyRole("USER")
						.requestMatchers("/enrollment/rate").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/course/newReview").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/reviews/{id}/mark-pending").hasAnyRole("USER")
						.requestMatchers("/reviews/{id}/desmark-pending").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/editReview").hasAnyRole("ADMIN")
						.requestMatchers("/deleteReview").hasAnyRole("ADMIN")
						.requestMatchers("/profileImage/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/newCourse").hasAnyRole("USER")
						.requestMatchers("/notes/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/createCourse").hasAnyRole("USER")
						.requestMatchers("/editCourse/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/updateCourse/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/getTaughtCourses").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/deleteCourse/*").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/profile").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/updateUser/*").hasAnyRole("USER")
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")

						.requestMatchers("/statistics/*").hasAnyRole("USER")
						.requestMatchers("/puntuationChart/*").hasAnyRole("USER"))
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