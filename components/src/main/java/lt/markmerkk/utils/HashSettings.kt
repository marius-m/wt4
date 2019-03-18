package lt.markmerkk.utils

interface HashSettings {
    fun save()
    fun load()
    fun set(key: String, value: String)
    fun get(key: String): String
    fun getLong(key: String, defaultValue: Long): Long
    fun getInt(key: String, defaultValue: Int): Int
}