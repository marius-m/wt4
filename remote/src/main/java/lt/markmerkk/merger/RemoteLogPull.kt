package lt.markmerkk.merger

import lt.markmerkk.entities.JiraWork
import java.util.concurrent.Callable

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface RemoteLogPull : Callable<JiraWork> {
}