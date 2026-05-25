package itch.ac.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.upload.dir:/uploads}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/images/instructor/**")
				.addResourceLocations("file:" + uploadDir + "/instructor/");

		registry.addResourceHandler("/images/actividad/**")
				.addResourceLocations("file:" + uploadDir + "/actividad/");

		registry.addResourceHandler("/uploads/inscripcion/**")
				.addResourceLocations("file:" + uploadDir + "/inscripcion/");

		registry.addResourceHandler("/uploads/constancia/**")
				.addResourceLocations("file:" + uploadDir + "/constancia/");
	}
}
