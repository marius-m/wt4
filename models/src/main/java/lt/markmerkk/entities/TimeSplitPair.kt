package lt.markmerkk.entities

data class TimeSplitPair(
        val first: TimeGap,
        val second: TimeGap,
        val splitPercent: Int
)