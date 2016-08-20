package lt.markmerkk.merger

import net.rcarz.jiraclient.Issue
import java.util.concurrent.Callable

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
interface RemoteIssuePull : Callable<Issue> {
}