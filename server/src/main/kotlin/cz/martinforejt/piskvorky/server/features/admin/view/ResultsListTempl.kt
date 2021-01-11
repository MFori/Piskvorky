package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.domain.model.GameResult
import kotlinx.html.*

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ResultsListTempl(
    private val results: List<GameResult>
) : AdminContentTempl {

    override fun FlowContent.apply() {
        h1 {
            +"Results"
        }

        table("table table-striped mt-4") {
            thead("thead-dark") {
                tr {
                    th { +"ID" }
                    th { +"User1" }
                    th { +"User2" }
                    th { +"Winner" }
                    th { +"Date" }
                }
            }
            tbody {
                results.forEach { result ->
                    resultRow(result)
                }
            }
        }
    }

    private fun TBODY.resultRow(result: GameResult) {
        tr {
            td { +"${result.id}" }
            td { a("/admin/users/${result.user1.id}") {+result.user1.email} }
            td { a("/admin/users/${result.user2.id}") {+result.user2.email} }
            td {
                +if(result.winnerId == result.user1.id) {
                    result.user1.email
                } else {
                    result.user2.email
                }
            }
            td { +"${result.created}" }
        }
    }

}