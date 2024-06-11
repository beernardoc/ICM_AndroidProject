package br.com.androidproject.repository

import br.com.androidproject.database.dao.PhotoDao
import br.com.androidproject.database.dao.RouteDao
import br.com.androidproject.database.entity.PhotoEntity
import br.com.androidproject.database.entity.RouteEntity
import br.com.androidproject.model.Photo
import br.com.androidproject.model.Route
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class PhotoRepository(
    private val photoDao: PhotoDao
) {
    val photos get() = photoDao.findAll()

    suspend fun save(photo: Photo) = withContext(IO) {
        photoDao.save(photo.toPhotoEntity())
    }

    suspend fun delete(photo: Photo) = withContext(IO) {
        photoDao.delete(
            photo.toPhotoEntity()
        )

    }

    suspend fun findById(id: String) = withContext(IO) {
        photoDao.findById(id)
    }

    suspend fun findByRouteId(id: String) = withContext(IO) {
        photoDao.findByRouteId(id)
    }



}

fun Photo.toPhotoEntity() = PhotoEntity(
    id = this.id,
    path = this.path,
    longitude = this.longitude,
    latitude = this.latitude,
    routeId = this.routeId
)

fun PhotoEntity.toPhoto() = Photo(
    id = this.id,
    path = this.path,
    longitude = this.longitude,
    latitude = this.latitude,
    routeId = this.routeId
)