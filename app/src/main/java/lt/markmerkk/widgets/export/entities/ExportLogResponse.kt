package lt.markmerkk.widgets.export.entities

import lt.markmerkk.entities.Log

data class ExportLogResponse(
        val start: Long,
        val end: Long,
        val duration: Long,
        val code: String,
        val comment: String
) {
    companion object {
        fun fromLog(log: Log): ExportLogResponse {
            return ExportLogResponse(
                    start = log.time.startAsRaw,
                    end = log.time.endAsRaw,
                    duration = log.time.durationAsRaw,
                    code = log.code.code,
                    comment = log.comment
            )
        }
    }
}