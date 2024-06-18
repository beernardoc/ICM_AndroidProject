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




}

fun Photo.toPhotoEntity() = PhotoEntity(
    path = this.path,
    longitude = this.longitude,
    latitude = this.latitude,
)

fun PhotoEntity.toPhoto() = Photo(
    path = this.path,
    longitude = this.longitude,
    latitude = this.latitude,
)