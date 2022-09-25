package lt.markmerkk.utils

object Logs {

    @JvmStatic
    fun asStringInstance(obj: Any?): String {
        return if (obj != null) {
            String.format(
                "[%s@%s]",
                obj.javaClass.simpleName,
                Integer.toHexString(obj.hashCode())
            )
        } else "[null@instance]"
    }

    fun Any?.toStringInstance(): String {
        return asStringInstance(this)
    }

    @JvmStatic
    fun objMessage(obj: Any?, message: String): String {
        return String.format("%s: %s", obj.toStringInstance(), message)
    }

    fun String.withLogInstance(obj: Any?): String {
        return objMessage(obj, this)
    }
}