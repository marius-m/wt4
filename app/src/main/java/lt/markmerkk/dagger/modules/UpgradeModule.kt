package lt.markmerkk.dagger.modules

import dagger.Module
import dagger.Provides
import lt.markmerkk.BuildConfig
import lt.markmerkk.Tags
import lt.markmerkk.upgrade.RemoteFileRepository
import lt.markmerkk.upgrade.RemoteFileRepositoryImpl
import lt.markmerkk.upgrade.SFTPClient
import lt.markmerkk.upgrade.SFTPCreds
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Module
class UpgradeModule {

    @Provides
    @Singleton
    fun provideSftpClient(): SFTPClient {
        return SFTPClient(
                SFTPCreds(
                        username = BuildConfig.sftpUser,
                        port = 22,
                        hostname = BuildConfig.sftpHost,
                        pass = BuildConfig.sftpPass
                )
        )
    }

    @Provides
    @Singleton
    fun provideRemoteFileRepository(
            sftpClient: SFTPClient
    ): RemoteFileRepository {
        return RemoteFileRepositoryImpl(sftpClient)
    }

    companion object {
        val loggerNetwork = LoggerFactory.getLogger(Tags.NETWORK)!!
    }

}