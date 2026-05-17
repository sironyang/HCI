package com.example.emotiongallery.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class GalleryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val place: String,
    val keywords: List<String>,
    val colorStart: Int,
    val colorEnd: Int
)

@Entity(tableName = "records")
data class PhotoRecordEntity(
    @PrimaryKey val photoId: Int,
    val emotion: String,
    val memo: String
)
