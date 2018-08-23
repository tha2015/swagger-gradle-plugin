# swagger-gradle-plugin

## Purpose

A plugin to help to generate swagger.json file from annotated Java classes. It is  like https://github.com/kongchen/swagger-maven-plugin but for Gradle project.

This project is based on kongchen's swagger-maven-plugin (https://github.com/kongchen/swagger-maven-plugin)

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