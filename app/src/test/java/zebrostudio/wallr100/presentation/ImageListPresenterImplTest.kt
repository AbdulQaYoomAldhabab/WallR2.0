package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.domain.datafactory.ImageModelFactory.getImageModel
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract
import zebrostudio.wallr100.presentation.wallpaper.ImageListPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class ImageListPresenterImplTest {

  @Mock
  lateinit var postExecutionThread: PostExecutionThread
  @Mock
  lateinit var wallpaperImagesUseCase: WallpaperImagesUseCase
  @Mock
  lateinit var imageListView: ImageListContract.ImageListView
  private lateinit var imagePresenterEntityMapper: ImagePresenterEntityMapper
  private lateinit var imageListPresenter: ImageListPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider

  @Before
  fun setup() {
    imagePresenterEntityMapper = ImagePresenterEntityMapper()
    imageListPresenter =
        ImageListPresenterImpl(wallpaperImagesUseCase, imagePresenterEntityMapper,
          postExecutionThread)
    imageListPresenter.attachView(imageListView)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(
      TestLifecycleScopeProvider.TestLifecycle.STARTED)

    `when`(imageListView.getScope()).thenReturn(testScopeProvider)
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test
  fun `should return imagePresenterEntity list of explore images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.exploreImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(EXPLORE_TAG, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(0 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).exploreImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of explore images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.exploreImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(EXPLORE_TAG, 0)

    imageListPresenter.fetchImages(true)

    assertTrue(0 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).exploreImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of recent images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.recentImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(1 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).recentImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of recent images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.recentImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 0)

    imageListPresenter.fetchImages(true)

    assertTrue(1 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).recentImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of popular images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.popularImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 1)

    imageListPresenter.fetchImages(false)

    assertTrue(2 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).popularImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of popular images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.popularImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 1)

    imageListPresenter.fetchImages(true)

    assertTrue(2 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).popularImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of standout images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.standoutImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 2)

    imageListPresenter.fetchImages(false)

    assertTrue(3 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).standoutImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of standout images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.standoutImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(TOP_PICKS_TAG, 2)

    imageListPresenter.fetchImages(true)

    assertTrue(3 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).standoutImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of building images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.buildingsImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 0)

    imageListPresenter.fetchImages(false)

    assertTrue(4 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).buildingsImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of building images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.buildingsImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 0)

    imageListPresenter.fetchImages(true)

    assertTrue(4 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).buildingsImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of food images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.foodImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 1)

    imageListPresenter.fetchImages(false)

    assertTrue(5 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).foodImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of food images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.foodImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 1)

    imageListPresenter.fetchImages(true)

    assertTrue(5 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).foodImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of nature images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.natureImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 2)

    imageListPresenter.fetchImages(false)

    assertTrue(6 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).natureImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of nature images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.natureImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 2)

    imageListPresenter.fetchImages(true)

    assertTrue(6 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).natureImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of object images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.objectsImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 3)

    imageListPresenter.fetchImages(false)

    assertTrue(7 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).objectsImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of object images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.objectsImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 3)

    imageListPresenter.fetchImages(true)

    assertTrue(7 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).objectsImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of people images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.peopleImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 4)

    imageListPresenter.fetchImages(false)

    assertTrue(8 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).peopleImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of people images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.peopleImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 4)

    imageListPresenter.fetchImages(true)

    assertTrue(8 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).peopleImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh false is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 5)

    imageListPresenter.fetchImages(false)

    assertTrue(9 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should return imagePresenterEntity list of technology images when fetchImages call with refresh true is success`() {
    val imageModelList = listOf(getImageModel())
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(Single.just(imageModelList))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 5)

    imageListPresenter.fetchImages(true)

    assertTrue(9 == imageListPresenter.imageListType)
    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verifyLoaderIsHiddenAndDisplayImageList(imageModelList)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet message view on fetchImages call without refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(
      Single.error(TimeoutException()))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 5)

    imageListPresenter.fetchImages(false)

    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).showLoader()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet message view on fetchImages call with refresh failure due to timeout`() {
    `when`(wallpaperImagesUseCase.technologyImagesSingle()).thenReturn(
      Single.error(TimeoutException()))
    imageListPresenter.setImageListType(CATEGORIES_TAG, 5)

    imageListPresenter.fetchImages(true)

    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verify(wallpaperImagesUseCase).technologyImagesSingle()
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showNoInternetMessageView()
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  private fun verifyLoaderIsShownAndThenHiddenAndImageListIsDisplayed(
    imageModelList: List<ImageModel>
  ) {
    val mappedPresenterEntityList = imagePresenterEntityMapper.mapToPresenterEntity(imageModelList)
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).showLoader()
    verify(imageListView).hideLoader()
    verify(imageListView).showImageList(mappedPresenterEntityList)
    verify(imageListView).getScope()
  }

  private fun verifyLoaderIsHiddenAndDisplayImageList(imageModelList: List<ImageModel>) {
    val mappedPresenterEntityList = imagePresenterEntityMapper.mapToPresenterEntity(imageModelList)
    verify(imageListView).hideAllLoadersAndMessageViews()
    verify(imageListView).hideLoader()
    verify(imageListView).showImageList(mappedPresenterEntityList)
    verify(imageListView).hideRefreshing()
    verify(imageListView).getScope()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallpaperImagesUseCase, imageListView, postExecutionThread)
    imageListPresenter.detachView()
  }

  private fun verifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
  }
}