/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.buildUtils.idea

import idea.DistCopyDetailsMock
import org.gradle.api.Project

class DistModelFlattener(val rootProject: Project) {
    val visited = mutableSetOf<DistVFile>()

    fun DistVFile.flatten(): DistVFile {
        val new = DistVFile(parent, name, file)
        copyFlattenedContentsTo(new)
        return new
    }

    private fun DistVFile.copyFlattenedContentsTo(new: DistVFile, inJar: Boolean = false) {
        if (!visited.add(this)) {
            println("SKIPPED RECUESIVE ARTIFACT:\n - ${visited.joinToString("\n - ") { it.file.path }}\n - ${this.file.path}")
            return
        }

        contents.forEach {
            when (it) {
                is DistCopy -> {
                    val srcName = it.customTargetName ?: it.src.name
                    if (it.src.file.exists()) {
                        DistCopy(new, it.src, srcName)
                    }

                    if (!inJar && srcName.endsWith(".jar")) {
                        val newChild = new.getOrCreateChild(srcName)
                        it.src.copyFlattenedContentsTo(newChild, inJar = true)
                    } else {
                        it.src.copyFlattenedContentsTo(new, inJar)
                    }
                }
                is DistModuleOutput -> DistModuleOutput(new, it.projectId)
            }
        }

        child.values.forEach { oldChild ->
            if (inJar) {
                oldChild.copyFlattenedContentsTo(new, inJar = true)
            } else {
                val newChild = new.getOrCreateChild(oldChild.name)
                oldChild.copyFlattenedContentsTo(newChild)
            }
        }

        visited.remove(this)
    }
}

