package lt.markmerkk.entities

import lt.markmerkk.utils.LogUtils

data class TicketCode(
        val code: String,
        val codeProject: String,
        val codeNumber: String
) {

    fun isEmpty(): Boolean = code.isEmpty()
            && codeProject.isEmpty()
            && codeNumber.isEmpty()

    companion object {
        fun asEmpty(): TicketCode = TicketCode(
                code = "",
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
                    code = validTicketCode,
                    codeProject = LogUtils.splitTaskTitle(validTicketCode),
                    codeNumber = LogUtils.splitTaskNumber(validTicketCode).toString()
            )
        }
    }

}