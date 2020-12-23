package cz.martinforejt.piskvorky.domain.repository

import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.model.personBios
import cz.martinforejt.piskvorky.domain.model.personImages
import cz.martinforejt.piskvorky.domain.remote.Assignment
import cz.martinforejt.piskvorky.domain.remote.IssPosition
import cz.martinforejt.piskvorky.domain.remote.PeopleInSpaceApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.*
import org.koin.core.KoinComponent
import org.koin.core.inject


class PeopleInSpaceRepository() : KoinComponent {
    private val peopleInSpaceApi: PeopleInSpaceApi by inject()
    private val logger: Kermit by inject()

    private val coroutineScope: CoroutineScope = MainScope()
    private val peopleInSpaceDatabase = createDb()
  //  private val peopleInSpaceQueries = peopleInSpaceDatabase?.peopleInSpaceQueries

    var peopleJob: Job? = null
    var issPositionJob: Job? = null

    init {
        coroutineScope.launch {
            fetchAndStorePeople()
        }
    }

    /*
    fun fetchPeopleAsFlow(): Flow<List<Assignment>> {
        // the main reason we need to do this check is that sqldelight isn't currently
        // setup for javascript client

       return peopleInSpaceQueries?.selectAll(mapper = { name, craft ->
            Assignment(name = name, craft = craft)
        })?.asFlow()?.mapToList() ?: flowOf(emptyList<Assignment>())
    }*/

    private suspend fun fetchAndStorePeople()  {
        logger.d { "fetchAndStorePeople" }
        val result = peopleInSpaceApi.fetchPeople()

        // this is very basic implementation for now that removes all existing rows
        // in db and then inserts results from api request
       /* peopleInSpaceQueries?.deleteAll()
        result.people.forEach {
            peopleInSpaceQueries?.insertItem(it.name, it.craft)
        }*/
    }

    // Used by web client atm
    suspend fun fetchPeople() = peopleInSpaceApi.fetchPeople().people

    fun getPersonBio(personName: String): String {
        return personBios[personName] ?: ""
    }

    fun getPersonImage(personName: String): String {
        return personImages[personName] ?: ""
    }

    // called from Kotlin/Native clients
    fun startObservingPeopleUpdates(success: (List<Assignment>) -> Unit) {
        logger.d { "startObservingPeopleUpdates" }
        peopleJob = coroutineScope.launch {
            /*fetchPeopleAsFlow().collect {
                success(it)
            }*/
        }
    }

    fun stopObservingPeopleUpdates() {
        logger.d { "stopObservingPeopleUpdates, peopleJob = $peopleJob" }
        peopleJob?.cancel()
    }


    fun startObservingISSPosition(success: (IssPosition) -> Unit) {
        logger.d { "startObservingISSPosition" }
        issPositionJob = coroutineScope.launch {
            pollISSPosition().collect {
                success(it)
            }
        }
    }

    fun stopObservingISSPosition() {
        logger.d { "stopObservingISSPosition, peopleJob = $issPositionJob" }
        issPositionJob?.cancel()
    }


    fun pollISSPosition(): Flow<IssPosition> = flow {
        while (true) {
            val position = peopleInSpaceApi.fetchISSPosition().iss_position
            emit(position)
            logger.d("PeopleInSpaceRepository") { position.toString() }
            delay(POLL_INTERVAL)
        }
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}

