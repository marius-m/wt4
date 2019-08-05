package lt.markmerkk.di

import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Inject
import com.jfoenix.svg.SVGGlyph
import lt.markmerkk.*
import lt.markmerkk.entities.database.interfaces.IExecutor
import lt.markmerkk.interactors.ActiveLogPersistence
import lt.markmerkk.interactors.KeepAliveInteractor
import lt.markmerkk.mvp.HostServicesInteractor
import lt.markmerkk.tickets.TicketsNetworkRepo
import lt.markmerkk.ui_2.StageProperties
import lt.markmerkk.utils.ConfigSetSettings
import lt.markmerkk.utils.hourglass.HourGlass
import lt.markmerkk.utils.tracker.ITracker
import lt.markmerkk.validators.LogChangeValidator

/**
 * Using double DI system, so GUICE is compatible with TornadoFX
 * In the future, all DI things will be moved to GUICE
 */
class GuiceModuleApp(
        private val schedulerProvider: SchedulerProvider,
        private val stageProperties: StageProperties,
        private val eventBus: EventBus,
        private val configPathProvider: ConfigPathProvider,
        private val configSetSettings: ConfigSetSettings,
        private val config: Config,
        private val hostServiceInteractor: HostServicesInteractor,
        private val tracker: ITracker,
        private val userPrefs: UserSettings,
        private val dbExecutor: IExecutor,
        private val ticketsDatabaseRepo: TicketsDatabaseRepo,
        private val ticketsNetworkRepo: TicketsNetworkRepo,
        private val timeProvider: TimeProvider,
        private val basicLogStorage: LogStorage,
        private val hourGlass: HourGlass,
        private val keepAliveInteractor: KeepAliveInteractor,
        private val dayProvider: DayProvider,
        private val strings: Strings,
        private val graphics: Graphics<SVGGlyph>,
        private val activeLogPersistence: ActiveLogPersistence,
        private val resultDispatcher: ResultDispatcher,
        private val logChangeValidator: LogChangeValidator
): AbstractModule() {

    override fun configure() {
        super.configure()
        bind(SchedulerProvider::class.java)
                .toInstance(schedulerProvider)
        bind(StageProperties::class.java)
                .toInstance(stageProperties)
        bind(EventBus::class.java)
                .toInstance(eventBus)
        bind(ConfigPathProvider::class.java)
                .toInstance(configPathProvider)
        bind(ConfigSetSettings::class.java)
                .toInstance(configSetSettings)
        bind(Config::class.java)
                .toInstance(config)
        bind(HostServicesInteractor::class.java)
                .toInstance(hostServiceInteractor)
        bind(ITracker::class.java)
                .toInstance(tracker)
        bind(UserSettings::class.java)
                .toInstance(userPrefs)
        bind(IExecutor::class.java)
                .toInstance(dbExecutor)
        bind(TicketsDatabaseRepo::class.java)
                .toInstance(ticketsDatabaseRepo)
        bind(TicketsNetworkRepo::class.java)
                .toInstance(ticketsNetworkRepo)
        bind(TimeProvider::class.java)
                .toInstance(timeProvider)
        bind(LogStorage::class.java)
                .toInstance(basicLogStorage)
        bind(HourGlass::class.java)
                .toInstance(hourGlass)
        bind(KeepAliveInteractor::class.java)
                .toInstance(keepAliveInteractor)
        bind(DayProvider::class.java)
                .toInstance(dayProvider)
        bind(Graphics::class.java)
                .toInstance(graphics)
        bind(Strings::class.java)
                .toInstance(strings)
        bind(ActiveLogPersistence::class.java)
                .toInstance(activeLogPersistence)
        bind(ResultDispatcher::class.java)
                .toInstance(resultDispatcher)
        bind(LogChangeValidator::class.java)
                .toInstance(logChangeValidator)
    }

}