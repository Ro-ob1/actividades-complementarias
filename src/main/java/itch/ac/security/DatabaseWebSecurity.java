package itch.ac.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

	@Bean
	UserDetailsManager usuarios(DataSource dataSource) {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
		users.setUsersByUsernameQuery("SELECT username, password, activo FROM usuario WHERE username = ?");
		users.setAuthoritiesByUsernameQuery("SELECT username, rol FROM usuario WHERE username = ?");
		return users;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth

			// ── Recursos públicos ──────────────────────────────────────────────
			.requestMatchers("/", "/inicio", "/login",
				"/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**")
				.permitAll()

			// ── Portal ALUMNO (antes del catch-all /alumno/**) ─────────────────
			.requestMatchers("/alumno/inicio", "/alumno/catalogo",
				"/alumno/solicitar", "/alumno/solicitar/**")
				.hasAnyRole("ADMIN", "ALUMNO")

			// ── Gestión de alumnos: solo ADMIN ────────────────────────────────
			.requestMatchers("/alumno/**")
				.hasRole("ADMIN")

			// ── Dashboard INSTRUCTOR (antes del catch-all) ────────────────────
			.requestMatchers("/instructor/inicio")
				.hasAnyRole("ADMIN", "INSTRUCTOR")

			// ── Lista, detalle y alta de instructores: ADMIN y ENCARGADO ────
			.requestMatchers("/instructor/instructores", "/instructor/ver/**",
				"/instructor/nuevo", "/instructor/guardar")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Editar y eliminar instructores: solo ADMIN ────────────────────
			.requestMatchers("/instructor/**")
				.hasRole("ADMIN")

			// ── Dashboard ENCARGADO (antes del catch-all) ─────────────────────
			.requestMatchers("/encargado/inicio")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Gestión de encargados: solo ADMIN ────────────────────────────
			.requestMatchers("/encargado/**")
				.hasRole("ADMIN")

			// ── Catálogos, semestres, personas, usuarios: solo ADMIN ──────────
			.requestMatchers("/carrera/**", "/disciplina/**", "/areaInstructor/**",
				"/espacio/**", "/nivelDesempenio/**", "/criterioBase/**", "/semestre/**",
				"/persona/**", "/usuario/**")
				.hasRole("ADMIN")

			// ── Actividades: ENCARGADO tiene CRUD; INSTRUCTOR y ALUMNO solo ver
			.requestMatchers("/actividad/actividades", "/actividad/ver/**")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR", "ALUMNO")
			.requestMatchers("/actividad/**")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Horarios: INSTRUCTOR solo puede ver su propio horario y PDF ──
			.requestMatchers("/horario/horarios", "/horario/instructor/**")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR")

			// ── Resto de horarios (CRUD): solo ADMIN y ENCARGADO ─────────────
			.requestMatchers("/horario/**")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Inscripciones: ENCARGADO ve y gestiona estatus, no CRUD ───────
			.requestMatchers("/inscripcion/inscripciones", "/inscripcion/ver/**",
				"/inscripcion/aprobar/**", "/inscripcion/rechazar/**")
				.hasAnyRole("ADMIN", "ENCARGADO")
			// CRUD (nuevo/guardar/editar/eliminar): solo ADMIN
			.requestMatchers("/inscripcion/**")
				.hasRole("ADMIN")

			// ── Sesiones: INSTRUCTOR y ENCARGADO registran asistencia ─────────
			.requestMatchers("/sesion/sesiones", "/sesion/reporte/**",
				"/sesion/asistencia/**", "/sesion/guardarAsistencia",
				"/sesion/marcarTodosAsistieron", "/sesion/bulkAccion")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR")
			.requestMatchers("/sesion/**")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Asistencias (vista): ADMIN, ENCARGADO, INSTRUCTOR ─────────────
			.requestMatchers("/asistencia/**")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR")

			// ── Evaluaciones: INSTRUCTOR crea y edita; ENCARGADO solo ve ──────
			.requestMatchers("/evaluacion/nuevo", "/evaluacion/guardar",
				"/evaluacion/editar/**")
				.hasAnyRole("ADMIN", "INSTRUCTOR")
			.requestMatchers("/evaluacion/eliminar/**")
				.hasRole("ADMIN")
			.requestMatchers("/evaluacion/**")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR")

			// ── CriterioEval: INSTRUCTOR y ADMIN ─────────────────────────────
			.requestMatchers("/criterioEval/eliminar/**")
				.hasRole("ADMIN")
			.requestMatchers("/criterioEval/**")
				.hasAnyRole("ADMIN", "INSTRUCTOR")

			// ── Informe semestral: ADMIN y ENCARGADO ─────────────────────────
			.requestMatchers("/informe/**")
				.hasAnyRole("ADMIN", "ENCARGADO")

			// ── Constancias ───────────────────────────────────────────────────
			// ALUMNO e INSTRUCTOR: solo ver lista, detalle y descargar PDF
			.requestMatchers("/constancia/constancias", "/constancia/ver/**",
				"/constancia/pdf/**")
				.hasAnyRole("ADMIN", "ENCARGADO", "INSTRUCTOR", "ALUMNO")
			// Eliminar: solo ADMIN
			.requestMatchers("/constancia/eliminar/**")
				.hasRole("ADMIN")
			// Generar y editar estatus: ADMIN y ENCARGADO
			.requestMatchers("/constancia/**")
				.hasAnyRole("ADMIN", "ENCARGADO")

			.anyRequest().authenticated())

			.formLogin(form -> form
				.loginPage("/login").loginProcessingUrl("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login?error=true").permitAll())
			.logout(logout -> logout
				.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
				.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
