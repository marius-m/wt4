package lt.markmerkk.mvp

import org.joda.time.DateTime


interface ClockEditMVP {

    interface View {
        fun onDateChange(
                startDateTime: DateTime,
                endDateTime: DateTime
        )
        fun onHintChange(
                hint: String
        )
    }

    interface Presenter {
        fun onAttach()
        fun onDetach()

        fun updateDateTime(
                start: DateTime,
                end: DateTime
        )
    }

}