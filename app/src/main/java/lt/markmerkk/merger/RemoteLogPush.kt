package lt.markmerkk.merger

import lt.markmerkk.entities.SimpleLog
import java.util.concurrent.Callable

/**
 * @author mariusmerkevicius
 * @since 2016-08-08
 */
interface RemoteLogPush : Callable<SimpleLog> {
}