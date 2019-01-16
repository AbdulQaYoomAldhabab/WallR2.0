package zebrostudio.wallr100.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel

interface ImageOptionsUseCase {
  fun quickSetImageObservable(link: String): Observable<ImageDownloadModel>
  fun getImageShareableLinkSingle(link: String): Single<String>
}

class ImageOptionsInteractor(
  private var wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : ImageOptionsUseCase {

  override fun quickSetImageObservable(link: String): Observable<ImageDownloadModel> {
    return wallrRepository.quickSetWallpaper(link)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

  override fun getImageShareableLinkSingle(link: String): Single<String> {
    return wallrRepository.getShortImageLink(link)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

}