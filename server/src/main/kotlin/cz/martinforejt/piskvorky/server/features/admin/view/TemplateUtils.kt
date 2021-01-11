package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.server.routing.utils.currentUser
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
import kotlinx.html.HTML
import kotlin.reflect.full.primaryConstructor

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

// only for templates with empty constructors!
suspend inline fun <reified CT : AdminContentTempl> PipelineContext<*, ApplicationCall>.adminTemplate(
    status: HttpStatusCode = HttpStatusCode.OK,
    noinline body: AdminLayoutTempl<CT>.() -> Unit
) {
    call.respondHtmlTemplate(AdminLayoutTempl(create<CT>() as CT, currentUser), status, body)
}

inline fun <reified CT : Template<*>> create() = CT::class.primaryConstructor?.call()

// for templates with constructors
suspend fun <CT : AdminContentTempl> PipelineContext<*, ApplicationCall>.adminTemplate(
    template: CT,
    status: HttpStatusCode = HttpStatusCode.OK,
    body: AdminLayoutTempl<CT>.() -> Unit
) {
    call.respondHtmlTemplate(AdminLayoutTempl(template, currentUser), status, body)
}

suspend fun <TTemplate : Template<HTML>> ApplicationCall.respondHtmlTemplate(
    template: TTemplate,
    status: HttpStatusCode = HttpStatusCode.OK,
    body: TTemplate.() -> Unit
) {
    template.body()
    respondHtml(status) { with(template) { apply() } }
}