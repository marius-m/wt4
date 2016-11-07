package lt.markmerkk

interface ConfigPathProvider {
    /**
     * Path for user home directory
     */
    fun userHome(): String

    /**
     * Default config middle path. For ex.: '.middle_path/'
     */
    fun configDefault(): String

    /**
     * Home path + path extension. For ex.: '.wt4/expansion'
     */
    fun configExtension(): String

    /**
     * Runs a run the path on the file system
     * and creates missing folders
     */
    @Throws(IllegalStateException::class)
    fun absolutePathWithMissingFolderCreate(path: String): String

}