/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.file.UnionFileCollection
import org.gradle.api.internal.file.UnionFileTree
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsDce
import java.io.File

class KotlinJsDcePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(Kotlin2JsPluginWrapper::class.java)

        val javaPluginConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
        javaPluginConvention.sourceSets?.forEach { processSourceSet(project, it) }
    }

    private fun processSourceSet(project: Project, sourceSet: SourceSet) {
        val kotlinTaskName = sourceSet.getCompileTaskName("kotlin2Js")
        val kotlinTask = project.tasks.findByName(kotlinTaskName) as? Kotlin2JsCompile ?: return
        val dceTaskName = sourceSet.getTaskName("dce", "kotlin2Js")
        val dceTask = project.tasks.create(dceTaskName, Kotlin2JsDce::class.java).also {
            it.dependsOn(kotlinTask)
            project.tasks.findByName("assemble")?.dependsOn(it)
        }

        project.afterEvaluate {
            val outputDir = File(kotlinTask.outputFile).parentFile

            val dependenciesDir = File(outputDir, "dependencies")
            copyDependencies(project, sourceSet, dependenciesDir, dceTask)

            val dceInputTrees = listOf(project.fileTree(kotlinTask.outputFile), project.fileTree(dependenciesDir))
            val dceInputFiles = UnionFileTree("dce", dceInputTrees)

            with (dceTask) {
                classpath = sourceSet.compileClasspath
                destinationDir = File(outputDir, "min")
                source(dceInputFiles.filter { it.path.endsWith(".js") })
            }
        }
    }

    private fun copyDependencies(project: Project, sourceSet: SourceSet, outputDir: File, dceTask: Task) {
        val configuration = project.configurations.findByName(sourceSet.compileConfigurationName) ?: return

        val files = UnionFileCollection(configuration.map { project.zipTree(it) })
                .filter { it.path.endsWith(".js") }
                .filter { File(it.path.removeSuffix(".js") + ".meta.js").exists() }

        val name = sourceSet.getTaskName("dependencies", "kotlin2Js")
        with(project.tasks.create(name, Copy::class.java)) {
            from(files)
            into(outputDir)
            includeEmptyDirs = true
            include {
                it.path.let { it.endsWith(".js") && !it.endsWith(".meta.js") }
            }

            dceTask.dependsOn(this)
        }
    }
}