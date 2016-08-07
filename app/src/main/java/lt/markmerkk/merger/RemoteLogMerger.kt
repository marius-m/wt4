package lt.markmerkk.merger

import lt.markmerkk.JiraWork
import java.util.concurrent.Callable

/**
 * @author mariusmerkevicius
 * @since 2016-08-07
 */
interface RemoteLogMerger : Callable<JiraWork> {
}