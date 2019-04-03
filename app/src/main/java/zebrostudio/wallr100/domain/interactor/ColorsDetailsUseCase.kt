package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType

interface ColorsDetailsUseCase {
  fun getColorBitmapSingle(colorHex: String): Single<Bitmap>
  fun getMultiColorMaterialSingle(
    colorHexList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap>

  fun getBitmapSingle(): Single<Bitmap>
  fun getBitmapFromUriSingle(uri: Uri): Single<Bitmap>
  fun saveToCollectionsCompletable(): Completable
  fun getCroppingSourceUri(): Uri
  fun getCroppingDestinationUri(): Uri
  fun clearCachesCompletable(): Completable
}

class ColorsDetailsInteractor(private val wallrRepository: WallrRepository) : ColorsDetailsUseCase {

  override fun getColorBitmapSingle(colorHex: String): Single<Bitmap> {
    return wallrRepository.getSingleColorBitmap(colorHex)
  }

  override fun getMultiColorMaterialSingle(
    colorHexList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap> {
    return wallrRepository.getMultiColorBitmap(colorHexList, multiColorImageType)
  }

  override fun getBitmapSingle(): Single<Bitmap> {
    return wallrRepository.getImageBitmap()
  }

  override fun getBitmapFromUriSingle(uri: Uri): Single<Bitmap> {
    return wallrRepository.getBitmapFromUri(uri)
  }

  override fun saveToCollectionsCompletable(): Completable {
    return wallrRepository.saveImageToCollections()
  }

  override fun getCroppingSourceUri() = wallrRepository.getCacheSourceUri()

  override fun getCroppingDestinationUri() = wallrRepository.getCacheResultUri()

  override fun clearCachesCompletable(): Completable {
    return wallrRepository.clearImageCaches()
  }

}