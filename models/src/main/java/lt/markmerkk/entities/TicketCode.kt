package lt.markmerkk.entities

import lt.markmerkk.utils.LogUtils

data class TicketCode private constructor(
        val codeProject: String,
        val codeNumber: String
) {

    val code: String = if (isEmpty()) "" else "$codeProject-$codeNumber"

    fun isEmpty(): Boolean = codeProject.isEmpty()
            && codeNumber.isEmpty()

    companion object {
        fun asEmpty(): TicketCode = TicketCode(
                codeProject = "",
                codeNumber = ""
        )
        fun new(
                code: String
        ): TicketCode {
            val validTicketCode = LogUtils.validateTaskTitle(code)
            if (validTicketCode.isEmpty()) {
                return asEmpty()
            }
            return TicketCode(
                    codeProject = LogUtils.splitTaskTitle(validTicketCode),
                    codeNumber = LogUtils.splitTaskNumber(validTicketCode).toString()
            )
        }
    }

}