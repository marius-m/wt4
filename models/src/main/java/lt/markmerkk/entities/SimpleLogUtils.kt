package lt.markmerkk.entities

fun List<SimpleLog>.asIds(): List<Long> = this.map { it._id }
