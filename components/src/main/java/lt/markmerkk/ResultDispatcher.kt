package lt.markmerkk

/**
 * Responsible for dispatching events and lets holds
 * the data to be consumed across different views
 */
class ResultDispatcher {

    private val resultEntities = mutableMapOf<String, Any>()

    fun resultEntities(): Map<String, Any> = resultEntities.toMap()

    /**
     * Publish entity with result
     */
    fun publish(key: String, resultEntity: Any) {
        resultEntities[key] = resultEntity
    }

    /**
     * Consumes result entity by the [key]
     * Will return null if no entity is published
     */
    fun <T> consume(key: String, clazz: Class<T>): T? {
        if (resultEntities.containsKey(key)) {
            val entity = resultEntities[key]
            resultEntities.remove(key)
            if (entity != null && entity::class.java == clazz) {
                return entity as T
            }
        }
        return null
    }

    /**
     * Consumes result entity by the [key]
     * Will return [defaultValue] if not found
     */
    fun consumeBoolean(key: String, defaultValue: Boolean = false): Boolean {
        if (resultEntities.containsKey(key)) {
            val valueAsBoolean = resultEntities[key]
            resultEntities.remove(key)
            if (valueAsBoolean != null && valueAsBoolean is Boolean) {
                return valueAsBoolean
            }
        }
        return defaultValue
    }

}