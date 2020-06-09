package lt.markmerkk.export.icons

interface IconScriptProvider {

    /**
     * Prints out debug data
     */
    fun debugPrint()

    /**
     * Generated command to execute
     */
    fun scriptCommand(): List<String>

}