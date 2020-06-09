package lt.markmerkk.export.executor

/**
 * Responsible for generating CLI arguments for execution
 */
interface JBundlerScriptProvider {

    /**
     * Script as list of CLI arguments
     */
    fun scriptCommand(): List<String>

    fun bundle(): String

}