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

package org.jetbrains.kotlin.script

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.File
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.script.dependencies.KotlinScriptExternalDependencies

class KotlinScriptExternalImportsProviderImpl(
        val project: Project,
        private val scriptDefinitionProvider: KotlinScriptDefinitionProvider
) : KotlinScriptExternalImportsProvider {

    private val cacheLock = ReentrantReadWriteLock()
    private val cache = hashMapOf<String, KotlinScriptExternalDependencies?>()

    override fun <TF: Any> getExternalImports(file: TF): KotlinScriptExternalDependencies? = cacheLock.read {
        calculateExternalDependencies(file)
    }

    fun <TF: Any> getExternalImports(files: Iterable<TF>): List<KotlinScriptExternalDependencies> = cacheLock.read {
        files.mapNotNull { calculateExternalDependencies(it) }
    }

    private fun <TF: Any> calculateExternalDependencies(file: TF): KotlinScriptExternalDependencies? {
        val path = getFilePath(file)
        val cached = cache[path]
        return if (cached != null) cached else {
            val scriptDef = scriptDefinitionProvider.findScriptDefinition(file)
            if (scriptDef != null) {
                val deps = scriptDef.getDependenciesFor(file, project, null)
                if (deps != null) {
                    log.info("[kts] new cached deps for $path: ${deps.classpath.joinToString(File.pathSeparator)}")
                }
                cacheLock.write {
                    cache.put(path, deps)
                }
                deps
            }
            else null
        }
    }

    override fun <TF: Any> cacheExternalImports(files: Iterable<TF>): Iterable<TF> = onlyCalledInIDE()

    // optimized for update, no special duplicates handling
    // returns files with valid script definition (or deleted from cache - which in fact should have script def too)
    // TODO: this is the badly designed contract, since it mixes the entities, but these files are needed on the calling site now. Find out other solution
    override fun <TF: Any> updateExternalImportsCache(files: Iterable<TF>) = onlyCalledInIDE()

    override fun invalidateCaches() = onlyCalledInIDE()

    override fun getKnownCombinedClasspath() = onlyCalledInIDE()

    override fun getKnownSourceRoots() = onlyCalledInIDE()

    override fun <TF: Any> getCombinedClasspathFor(files: Iterable<TF>): List<File> =
        getExternalImports(files)
                .flatMap { it.classpath }
                .distinct()

    companion object {
        @JvmStatic
        fun getInstance(project: Project): KotlinScriptExternalImportsProvider? =
                ServiceManager.getService(project, KotlinScriptExternalImportsProvider::class.java)
        internal val log = Logger.getInstance(KotlinScriptExternalImportsProvider::class.java)
    }
}

private fun Iterable<File>.isSamePathListAs(other: Iterable<File>): Boolean =
        with (Pair(iterator(), other.iterator())) {
            while (first.hasNext() && second.hasNext()) {
                if (first.next().canonicalPath != second.next().canonicalPath) return false
            }
            !(first.hasNext() || second.hasNext())
        }

private inline fun<T> measureThreadTimeMillis(body: () -> T): Pair<T, Long> {
    val mxBeans = ManagementFactory.getThreadMXBean()
    val startTime = mxBeans.currentThreadCpuTime
    val res = body()
    return res to TimeUnit.NANOSECONDS.toMillis(mxBeans.currentThreadCpuTime - startTime)
}

private fun onlyCalledInIDE(): Nothing = error("Only called in IDE")
