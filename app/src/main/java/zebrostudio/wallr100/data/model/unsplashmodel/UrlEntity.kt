package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

data class UrlEntity(
  @SerializedName("raw") val rawImageLink: String,
  @SerializedName("full") val largeImageLink: String,
  @SerializedName("regular") val regularImageLink: String,
  @SerializedName("small") val smallImageLink: String,
  @SerializedName("thumb") val thumbImageLink: String
)