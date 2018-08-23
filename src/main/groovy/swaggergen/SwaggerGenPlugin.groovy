package swaggergen;

import org.gradle.api.Action
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

public class SwaggerGenPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		ensureTaskDependsOnJavaClassesTask(project)
	}

	private static void ensureTaskDependsOnJavaClassesTask(Project project) {
		// if Java plugin is applied, make this task depend on "classes" task
		// see https://guides.gradle.org/implementing-gradle-plugins/#reacting_to_plugins

		project.getPlugins().withType(JavaPlugin.class, new Action<JavaPlugin>() {
			@Override
			public void execute(JavaPlugin javaPlugin) {

				project.getTasks().withType(SwaggerGenTask.class).all(new Action<SwaggerGenTask>() {

					@Override
					public void execute(SwaggerGenTask task) {

						task.dependsOn(project.getTasks().getByName(JavaPlugin.CLASSES_TASK_NAME))

					}
				})
			}
		})
	}
}
