package swaggergen;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input;

import java.nio.file.Files
import java.nio.file.StandardCopyOption

public class SwaggerGenTask extends DefaultTask {

	@Input
	def version = project.version
	@Input
	def title = "Demo Docs"
	@Input
	def location = "com.demo.service"
	@Input
	def basePath = "/demo-api"
	@Input
	def jsonTargetPath = project.buildDir.absolutePath
	@Input
	def htmlTargetPath = new File(project.buildDir, "swagger.html").absolutePath
	@Input
	def classPath = project.configurations.compile
	@Input
	def swaggerMavenPluginVersion = "3.1.7"

	@TaskAction
	def generate() {

		def swaggerdir = new File(project.buildDir, "swaggergen")
		swaggerdir.mkdirs()


		copyMavenFiles(swaggerdir)


		File fatjar = new File(swaggerdir, "maven/libs/all.jar")
		buildFatJar(fatjar)

		executeMavenBuild(new File(swaggerdir, "maven"))

	}


	void buildFatJar(File fatjar) {

		File swaggerlibsdir = fatjar.parentFile
		swaggerlibsdir.mkdirs()

		classPath.findAll {it.exists()}.each {
			project.ant.copy (file: it, todir: swaggerlibsdir , flatten: true)
		}

		project.ant.manifestclasspath (property: "classpath", jarfile: fatjar, maxParentLevels: 10) {
			classpath {
				project.sourceSets.main.output.classesDirs.each{
					pathelement (location: it)
				}
				fileset (dir: swaggerlibsdir) {
					include (name: "**/*.jar")
				}
			}
		}

		project.ant.jar (destfile: fatjar) {
			manifest {
				attribute (name: "Class-Path", value: project.ant.project.properties.classpath)
			}
		}
	}



	void copyMavenFiles(File dir) {
		[   'maven/mvnw',
			'maven/mvnw.cmd',
			'maven/.mvn/wrapper/maven-wrapper.jar',
			'maven/.mvn/wrapper/maven-wrapper.properties',
			'maven/pom.xml',
			'maven/templates/markdown.hbs',
			'maven/templates/operation.hbs',
			'maven/templates/security.hbs',
			'maven/templates/strapdown.html.hbs'
			].each { f ->

				InputStream is = SwaggerGenTask.class.getResourceAsStream(f)

				new File(dir, f).parentFile.mkdirs()

				is.withStream {
					Files.copy(it, new File(dir, f).toPath(), StandardCopyOption.REPLACE_EXISTING)
				}

			}
	}

	void executeMavenBuild(File dir) {

		new File(dir, "target/classes").mkdirs()

		project.ant.chmod (file: new File(dir, "mvnw"), perm: "uog+rx")

		project.ant.exec (dir: dir, executable: "cmd", osfamily:"windows", failonerror: true) {
			arg (value: "/c")
			arg (value: "mvnw")
			arg (value: "swagger:generate")

			arg (value: "-Dcustom.api.version=${version}")
			arg (value: "-Dcustom.swagger.version=${swaggerMavenPluginVersion}")
			arg (value: "-Dcustom.api.title=${title}")
			arg (value: "-Dcustom.api.location=${location}")
			arg (value: "-Dcustom.api.basePath=${basePath}")
			arg (value: "-Dcustom.api.targetPath=${jsonTargetPath}")
			arg (value: "-Dcustom.api.html.outputPath=${htmlTargetPath}")

			env (key: "JAVA_HOME", value:"${System.getProperty('java.home')}")

		}

		project.ant.exec (dir: dir, executable: "/bin/sh", osfamily:"unix", failonerror: true) {
			arg (value: "-c")
			arg (value:
				"./mvnw swagger:generate"
				+ " \"-Dcustom.api.version=${version}\""
				+ " \"-Dcustom.swagger.version=${swaggerMavenPluginVersion}\""
				+ " \"-Dcustom.api.title=${title}\""
				+ " \"-Dcustom.api.location=${location}\""
				+ " \"-Dcustom.api.basePath=${basePath}\""
				+ " \"-Dcustom.api.targetPath=${jsonTargetPath}\""
				+ " \"-Dcustom.api.html.outputPath=${htmlTargetPath}\""
				)

			env (key: "JAVA_HOME", value:"${System.getProperty('java.home')}")
		}
	}

}
