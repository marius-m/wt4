package lt.markmerkk.export.executor

/**
 * Responsible for generating CLI arguments for execution
 */
interface JBundlerScriptProvider {

    /**
     * Prints out arguments used for bundling
     * For debugging purposes
     */
    fun debugPrint()

    /**
     * Script as list of CLI arguments
     */
    fun scriptCommand(): List<String>

    /**
     * Executes bundling script as external process
     */
    fun bundle(): String

}