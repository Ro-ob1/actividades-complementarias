package itch.ac.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${upload.path}")
	private String uploadPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String base = "file:" + uploadPath + "/";

		registry.addResourceHandler("/images/instructor/**")
				.addResourceLocations(base + "instructor/");

		registry.addResourceHandler("/images/actividad/**")
				.addResourceLocations(base + "actividad/");

		registry.addResourceHandler("/uploads/inscripcion/**")
				.addResourceLocations(base + "inscripcion/");

		registry.addResourceHandler("/uploads/constancia/**")
				.addResourceLocations(base + "constancia/");
	}
}
