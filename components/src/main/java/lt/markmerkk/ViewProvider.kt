package lt.markmerkk

/**
 * Thin layer to get the available view
 * Ex.1 Useful when presenter is initialized, when view is not available yet
 *      The view is passed through lambda, thus functions only trigger when view is available
 */
abstract class ViewProvider<T> {
    abstract fun get(): T?

    /**
     * It is easy to get the view from Kotlin and do a null check.
     * This is not the case on Java.
     * This convenience function will trigger function *only when view is available*
     */
    fun invoke(block: T.() -> Unit) {
        val view: T? = get()
        if (view != null) {
            block.invoke(view)
        }
    }

    /**
     * More java-like lambda consumable function
     * Successor of [invoke]
     */
    fun invokeJ(block: OnViewAvailableListener<T>) {
        val view: T? = get()
        if (view != null) {
            block.invokeOnView(view)
        }
    }
}

class ViewProviderEmpty<T> : ViewProvider<T>() {
    override fun get(): T? = null
}

@FunctionalInterface
interface OnViewAvailableListener<T> {
    fun invokeOnView(view: T)
}
