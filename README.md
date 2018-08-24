# swagger-gradle-plugin

## Purpose

A plugin to help to generate swagger.json file from annotated Java classes. It is  like https://github.com/kongchen/swagger-maven-plugin but for Gradle project.

This project is a wapper for kongchen's swagger-maven-plugin (https://github.com/kongchen/swagger-maven-plugin) to be used from Gradle. Internally it will use a Maven project to generate swagger.json file. 

The reason for creating this plugin is because the existing gradle plugin by gigaSproule has one issue (https://github.com/gigaSproule/swagger-gradle-plugin/issues/76) regarding classpath conflicts. Specifically I had one project using log4j (while Gradle uses log4j-over-slf4j) and I got NoSuchMethodError when trying gigaSproule's plugin because log4j-over-slf4j was used when scanning classes (instead of log4j). 

## Usage

Define a custom task as below, when running "gradle swagger", the swagger.json file will be created. See https://github.com/kongchen/swagger-maven-plugin for more info about title, location, basePath. 

```groovy
plugins {
  id "com.tha2015.swagger-gradle-plugin" version "1.0.0"
}

task swagger(type: swaggergen.SwaggerGenTask) {
	title = "Demo Docs"
	location = "com.example.demo"
	basePath = "/demo-api"
	jsonTargetPath = "${project.buildDir}/swagger"
	htmlTargetPath = "${project.buildDir}/swagger/swagger.html"
	doFirst {new File("${project.buildDir}/swagger").mkdirs()}
}
```

## License

Apache 2 license