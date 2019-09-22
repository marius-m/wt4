package lt.markmerkk.events

import lt.markmerkk.versioner.Changelog

class EventNewVersion(
        val changelog: Changelog
) : EventsBusEvent