package lt.markmerkk.merger

import lt.markmerkk.JiraFilter
import lt.markmerkk.entities.JiraWork
import lt.markmerkk.entities.SimpleLog
import net.rcarz.jiraclient.WorkLog

interface RemoteMergeToolsProvider {
    fun logPullMerger(remoteLog: JiraWork, filter: JiraFilter<WorkLog>): RemoteLogPull
    fun logPushMerger(localLog: SimpleLog, filter: JiraFilter<SimpleLog>): RemoteLogPush
}