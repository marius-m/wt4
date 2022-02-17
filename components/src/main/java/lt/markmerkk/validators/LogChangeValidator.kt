package lt.markmerkk.validators

import lt.markmerkk.LogRepository

/**
 * Defines rules for log changes
 */
class LogChangeValidator(
    private val logRepository: LogRepository
) {

    /**
     * @return true whenever log is valid for editing
     */
    fun canEditSimpleLog(logLocalId: Long): Boolean {
        return logRepository.findByIdOrNull(logLocalId) != null
    }
}