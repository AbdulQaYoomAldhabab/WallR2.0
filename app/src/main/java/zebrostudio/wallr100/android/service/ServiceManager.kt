package zebrostudio.wallr100.android.service

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startForegroundService
import zebrostudio.wallr100.android.utils.isServiceRunningInForeground

interface ServiceManager {
  fun startAutomaticWallpaperChangerService(): Boolean
  fun stopAutomaticWallpaperChangerService(): Boolean
  fun isAutomaticWallpaperChangerRunning(): Boolean
}

class ServiceManagerImpl(private val context: Context) : ServiceManager {

  override fun startAutomaticWallpaperChangerService(): Boolean {
    if (!isAutomaticWallpaperChangerRunning()) {
      startForegroundService(context, Intent(context, AutomaticWallpaperChangerService::class.java))
    }
    if (isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun stopAutomaticWallpaperChangerService(): Boolean {
    Intent(context, AutomaticWallpaperChangerService::class.java).let {
      context.stopService(it)
    }
    if (!isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun isAutomaticWallpaperChangerRunning(): Boolean {
    return context.isServiceRunningInForeground(AutomaticWallpaperChangerService::class.java)
  }

}