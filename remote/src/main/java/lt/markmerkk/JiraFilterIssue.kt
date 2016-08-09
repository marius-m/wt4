package lt.markmerkk

import net.rcarz.jiraclient.Issue

/**
 * @author mariusmerkevicius
 * @since 2016-08-09
 */
class JiraFilterIssue : JiraFilter<Issue> {

    @Throws(JiraFilter.FilterErrorException::class)
    override fun valid(input: Issue?): Boolean {
        if (input == null) throw JiraFilter.FilterErrorException("null input")
        if (input.project == null) throw JiraFilter.FilterErrorException("no binding project")
        if (input.project.key == null) throw JiraFilter.FilterErrorException("no binding project key")
        if (input.key == null) throw JiraFilter.FilterErrorException("no binding key")
        if (input.summary == null) throw JiraFilter.FilterErrorException("no summary")
        if (input.createdDate == null) throw JiraFilter.FilterErrorException("no create date")
        if (input.updatedDate == null) throw JiraFilter.FilterErrorException("no create date")
        return true
    }
}