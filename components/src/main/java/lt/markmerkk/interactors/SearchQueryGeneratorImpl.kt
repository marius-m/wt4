package lt.markmerkk.interactors

import lt.markmerkk.LikeQueryGenerator
import lt.markmerkk.utils.IssueSplit
import lt.markmerkk.utils.IssueSplitImpl
import lt.markmerkk.utils.LogUtils

/**
 * @author mariusmerkevicius
 * @since 2016-11-22
 */
class SearchQueryGeneratorImpl(
        private val issueSplit: IssueSplit,
        private val descriptionQueryGenerator: LikeQueryGenerator,
        private val keyQueryGenerator: LikeQueryGenerator
) : SearchQueryGenerator {

    override fun searchQuery(input: String): String {
        val phraseMap = issueSplit.split(input)
        val descriptionClauses = descriptionQueryGenerator.genClauses(phraseMap[IssueSplitImpl.DESCRIPTION_KEY]!!)
        val descriptionQuery = descriptionQueryGenerator.genQuery(descriptionClauses)
        val possibleIssueTitle = LogUtils.validateTaskTitle(phraseMap[IssueSplitImpl.DESCRIPTION_KEY] ?: "")
        val keyClauses = keyQueryGenerator.genClauses(possibleIssueTitle)
        val keyQuery = keyQueryGenerator.genQuery(keyClauses)
        return descriptionQueryGenerator.genQuery(listOf(descriptionQuery, keyQuery))
    }
}