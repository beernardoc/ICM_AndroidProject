package br.com.androidproject.repository

import android.util.Log
import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.Loc
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.model.Route
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RouteRepository(
    private val routeDao: RouteDao,
    private val db: FirebaseFirestore
) {
    val routes get() = routeDao.findAll()



    suspend fun save(route: Route) = withContext(IO) {

        routeDao.save(route.toRouteEntity())


        db.collection("routes").add(
            mapOf(
                "title" to route.title,
                "startLat" to route.startLat,
                "startLng" to route.startLng,
                "endLat" to route.endLat,
                "endLng" to route.endLng,
                "distance" to route.distance,
                "duration" to route.duration,
                "userId" to route.userId,
                "points" to route.points
            )
        )






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

    suspend fun findAll() = withContext(IO) {
        routeDao.findAll()
    }



}

fun Route.toRouteEntity() = RouteEntity(
    title = this.title,
    startLat = this.startLat,
    startLng = this.startLng,
    endLat = this.endLat,
    endLng = this.endLng,
    distance = this.distance,
    duration = this.duration,
    userId = this.userId,
    points = this.points
)

fun RouteEntity.toRoute() = Route(
    title = this.title,
    startLat = this.startLat,
    startLng = this.startLng,
    endLat = this.endLat,
    endLng = this.endLng,
    distance = this.distance,
    duration = this.duration,
    userId = this.userId,
    points = this.points
)