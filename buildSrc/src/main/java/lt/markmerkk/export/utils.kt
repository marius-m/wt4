package lt.markmerkk.export

import org.gradle.api.Project

fun Project.getPropertyBool(
        key: String
): Boolean {
    return if (this.properties.containsKey(key)) {
        this.properties[key]!!.toString()
                .toBoolean()
    } else {
        false
    }
}
