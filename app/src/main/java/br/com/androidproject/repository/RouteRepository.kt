package br.com.androidproject.repository

import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.model.Route
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class RouteRepository(
    private val routeDao: RouteDao
) {
    val routes get() = routeDao.findAll()

    suspend fun save(route: Route) = withContext(IO) {
        routeDao.save(route.toRouteEntity())
    }

    suspend fun delete(route: Route) = withContext(IO) {
        routeDao.delete(
            route.toRouteEntity()
        )

    }

    suspend fun findById(id: String) = withContext(IO) {
        routeDao.findById(id)
    }

    suspend fun findByUserId(id: String) = withContext(IO) {
        routeDao.findByUserId(id)
    }



}

fun Route.toRouteEntity() = RouteEntity(
    id = this.id,
    title = this.title,
    startLat = this.startLat,
    startLng = this.startLng,
    endLat = this.endLat,
    endLng = this.endLng,
    distance = this.distance,
    duration = this.duration,
    userId = this.userId
)

fun RouteEntity.toRoute() = Route(
    id = this.id,
    title = this.title,
    startLat = this.startLat,
    startLng = this.startLng,
    endLat = this.endLat,
    endLng = this.endLng,
    distance = this.distance,
    duration = this.duration,
    userId = this.userId
)