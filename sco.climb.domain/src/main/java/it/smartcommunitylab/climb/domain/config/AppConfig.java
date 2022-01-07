/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.climb.domain.config;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mongodb.MongoException;

import it.smartcommunitylab.climb.domain.model.WsnEvent;
import it.smartcommunitylab.climb.domain.model.multimedia.MultimediaContent;
import it.smartcommunitylab.climb.domain.storage.RepositoryManager;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableWebMvc
@EnableSwagger2
@EnableScheduling
public class AppConfig implements WebMvcConfigurer {

	@Autowired
	@Value("${defaultLang}")
	private String defaultLang;
		
	@Autowired
	MongoTemplate mongoTemplate;
	
	public AppConfig() {
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*");
	}

	@Bean
	RepositoryManager getRepositoryManager() throws UnknownHostException, MongoException {
		mongoTemplate.indexOps(WsnEvent.class).ensureIndex(new Index().on("eventType", Direction.ASC));
		mongoTemplate.indexOps(WsnEvent.class).ensureIndex(new Index().on("timestamp", Direction.ASC));
		TextIndexDefinition textIndex = new TextIndexDefinitionBuilder().withDefaultLanguage("it")
				.onField("name").build();
		mongoTemplate.indexOps(MultimediaContent.class).ensureIndex(textIndex);
		mongoTemplate.indexOps(MultimediaContent.class).ensureIndex(new GeospatialIndex("geocode"));
		return new RepositoryManager(mongoTemplate, defaultLang);
	}
	
	@Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/");
		registry
    	.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");
    registry
    	.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("it.smartcommunitylab.climb.domain.controller"))
				.paths(PathSelectors.ant("/api/**"))
				.build()
				.apiInfo(apiInfo());
	}
	
	private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
    		.title("CLIMB Domain")
    		.version("1.4")
    		.license("Apache License Version 2.0")
    		.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
    		.contact(new Contact("SmartCommunityLab", "https://http://www.smartcommunitylab.it/", "info@smartcommunitylab.it"))
    		.build();
	}
	
}
