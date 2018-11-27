package zebrostudio.wallr100.data

import android.annotation.SuppressLint
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import java.util.concurrent.TimeUnit.*

class WallrDataRepository(
  private var retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
  private var unsplashClientFactory: UnsplashClientFactory,
  private var sharedPrefsHelper: SharedPrefsHelper,
  private var unsplashPictureEntityMapper: UnsplashPictureEntityMapper,
  private var firebaseDatabaseHelper: FirebaseDatabaseHelper,
  private var firebasePictureEntityMapper: FirebasePictureEntityMapper
) : WallrRepository {

  private val purchasePreferenceName = "PURCHASE_PREF"
  private val premiumUserTag = "premium_user"
  private val unableToResolveHostExceptionMessage = "Unable to resolve host " +
      "\"api.unsplash.com\": No address associated with hostname"
  private val firebaseDatabasePath = "wallr"
  val childPathExplore = "explore"
  val childPathCategories = "categories"
  val childPathCollections = "collections"
  val childPathRecent = "recent"
  val childPathPopular = "popular"
  val childPathStandout = "standout"
  val childPathBuilding = "building"
  val childPathFood = "food"
  val childPathNature = "nature"
  val childPathObject = "object"
  val childPathPeople = "people"
  val childPathTechnology = "technology"
  val firebaseTimeoutDuration = 15

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable {
    return Completable.fromSingle(retrofitFirebaseAuthFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
        .flatMap {
          if (it.status == "success") {
            Single.just(true)
          } else if (it.status == "error" && (it.errorCode == 404 || it.errorCode == 403)) {
            Single.error(InvalidPurchaseException())
          } else {
            Single.error(UnableToVerifyPurchaseException())
          }
        })
  }

  override fun updateUserPurchaseStatus() = sharedPrefsHelper.setBoolean(purchasePreferenceName,
      premiumUserTag, true)

  override fun isUserPremium() = sharedPrefsHelper.getBoolean(purchasePreferenceName,
      premiumUserTag, false)

  override fun getSearchPictures(query: String): Single<List<SearchPicturesModel>> {
    return unsplashClientFactory.getPicturesService(query)
        .flatMap {
          if (it.isEmpty()) {
            Single.error(NoResultFoundException())
          } else {
            val map = unsplashPictureEntityMapper.mapFromEntity(it)
            Single.just(map)
          }
        }
        .onErrorResumeNext {
          if (it.message != null && it.message == unableToResolveHostExceptionMessage) {
            Single.error(UnableToResolveHostException())
          } else {
            Single.error(it)
          }
        }
  }

  override fun getExplorePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getExploreNodeReference())
  }

  override fun getRecentPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCollectionsNodeReference()
        .child(childPathRecent))
  }

  override fun getPopularPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCollectionsNodeReference()
        .child(childPathPopular))
  }

  override fun getStandoutPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCollectionsNodeReference()
        .child(childPathStandout))
  }

  override fun getBuildingsPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathBuilding))
  }

  override fun getFoodPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathFood))
  }

  override fun getNaturePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathNature))
  }

  override fun getObjectsPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathObject))
  }

  override fun getPeoplePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathPeople))
  }

  override fun getTechnologyPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(childPathTechnology))
  }

  private fun getExploreNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathExplore)

  private fun getCollectionsNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathCollections)

  private fun getCategoriesNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathCategories)

  private fun getPicturesFromFirebase(firebaseDatabaseReference: DatabaseReference): Single<List<ImageModel>> {
    val imageList = mutableListOf<FirebaseImageEntity>()
    return firebaseDatabaseHelper
        .fetch(firebaseDatabaseReference)
        .flatMap {
          it.values.forEach { jsonString ->
            imageList.add(Gson().fromJson(jsonString, FirebaseImageEntity::class.java))
          }
          imageList.reverse()
          val image = firebasePictureEntityMapper.mapFromEntity(imageList)
          Single.just(image)
        }
        .timeout(firebaseTimeoutDuration.toLong(), SECONDS)
  }
}