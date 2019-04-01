package ch.romix.kotlin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ControllerLinkBuilder.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// the main class and method
@SpringBootApplication
open class JfsApplication

fun main(args: Array<String>) {
    SpringApplication.run(JfsApplication::class.java, *args)
}

// Our domain object
data class Session(val id: Int, val name: String, val speaker: String)

// Resource with self links
class SessionResource(val id: Int, val name: String, val speaker: String): ResourceSupport() {
    constructor(s: Session): this(s.id, s.name, s.speaker)
    init {
        add(linkTo(methodOn(SessionController::class.java).getSession(id)).withSelfRel())
    }
}

// Resource with link to available sessions
class SessionLinkResource(val id: Int, val name: String) : ResourceSupport() {
    constructor(s: Session): this(s.id, s.name)
    init {
        add(linkTo(methodOn(SessionController::class.java).getSession(id)).withRel("session"))
    }
}

// Complete rest controller with list of sessions and detail urls
@RestController
open class SessionController {

    companion object {
        val sessions = listOf(Session(1, "Kotlin", "The Jet Brain"), Session(42, "Spring Boot", "Oliver Gierke"))
    }

    @RequestMapping("/sessions")
    open fun getSessions(): ResponseEntity<List<SessionLinkResource>> {
        return ResponseEntity.ok(sessions.map { s -> SessionLinkResource(s) })
    }

    @RequestMapping("/sessions/{id}")
    open fun getSession(@PathVariable id: Int): ResponseEntity<SessionResource> {
        val session = sessions.find { s -> s.id.equals(id) }
        return if (session == null)
            ResponseEntity<SessionResource>(null, HttpStatus.NOT_FOUND)
        else
            ResponseEntity.ok(SessionResource(session))
    }
}
