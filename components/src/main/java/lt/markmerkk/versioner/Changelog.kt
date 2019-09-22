package lt.markmerkk.versioner

import java.lang.NumberFormatException

data class Changelog(
        val version: Version,
        val contentAsString: String
) {

    data class Version(
            val asString: String,
            val major: Int = 0,
            val minor: Int = 0,
            val patch: Int = 0
    ): Comparable<Version> {

        override fun compareTo(other: Version): Int {
            return when {
                major > other.major -> 1
                major < other.major -> -1
                else -> {
                    when {
                        minor > other.minor -> 1
                        minor < other.minor -> -1
                        else -> {
                            when {
                                patch > other.patch -> 1
                                patch < other.patch -> -1
                                else -> 0
                            }
                        }
                    }
                }
            }
        }

        override fun toString(): String {
            return "$major.$minor.$patch"
        }

        companion object {
            fun asEmpty(versionAsString: String): Version {
                return Version(versionAsString, 0, 0, 0)
            }
        }
    }

    companion object {
        private val changelogRegex = "Current: (.*)".toRegex()
        private val versionRegex = "([0-9])\\.*([0-9])?\\.*([0-9])?".toRegex()

        fun from(changelogAsString: String): Changelog {
            val changelogMatch = changelogRegex.find(changelogAsString)
            val changelogGroupAsString = if (changelogMatch != null
                    && changelogMatch.groupValues.size >= 2) {
                changelogMatch.groupValues[1]
            } else {
                ""
            }
            return Changelog(
                    version = versionFrom(changelogGroupAsString),
                    contentAsString = changelogAsString
            )
        }

        fun versionFrom(versionAsString: String): Version {
            val versionMatch = versionRegex.find(versionAsString) ?: return Version.asEmpty(versionAsString)
            val versionMajor = versionMatch.groupValues[1].toInt()
            val versionMinor = extractVersionOrZero(versionMatch.groupValues, 2)
            val versionPatch = extractVersionOrZero(versionMatch.groupValues, 3)
            return Version(
                    versionAsString,
                    versionMajor,
                    versionMinor,
                    versionPatch
            )
        }

        private fun extractVersionOrZero(versionNumbers: List<String>, numberIndex: Int): Int {
            return if (versionNumbers.size >= 3
                    && versionNumbers[numberIndex].isNotEmpty()) {
                try {
                    versionNumbers[numberIndex]
                            .replace("\\.", "")
                            .toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            } else {
                0
            }
        }

    }

}