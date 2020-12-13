package app.shosetsu.android.domain.repository.impl

import android.util.Log
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.datasource.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.DebugAppUpdate
import app.shosetsu.android.domain.repository.base.IAppUpdatesRepository
import app.shosetsu.common.consts.ErrorKeys.ERROR_DUPLICATE
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import kotlinx.coroutines.flow.Flow

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 07 / 09 / 2020
 */
class AppUpdatesRepository(
		private val iRemoteAppUpdateDataSource: IRemoteAppUpdateDataSource,
		private val iFileAppUpdateDataSource: IFileCachedAppUpdateDataSource,
) : IAppUpdatesRepository {
	private var running = false

	override fun watchAppUpdates(): Flow<HResult<DebugAppUpdate>> =
			iFileAppUpdateDataSource.updateAvaLive


	private fun compareVersion(newVersion: DebugAppUpdate): HResult<DebugAppUpdate> {
		val currentV: Int
		val remoteV: Int

		if (newVersion.versionCode == -1) {
			currentV = BuildConfig.VERSION_NAME.substringAfter("r").toInt()
			remoteV = newVersion.version.toInt()
		} else {
			currentV = BuildConfig.VERSION_CODE
			remoteV = newVersion.versionCode
		}

		return when {
			remoteV < currentV -> {
				val message = "This a future release compared to $newVersion"
				Log.i(logID(), message)
				emptyResult()
			}
			remoteV > currentV -> {
				val message = "Update found compared to $newVersion"
				Log.i(logID(), message)
				successResult(newVersion)
			}
			remoteV == currentV -> {
				val message = "This the current release compared to $newVersion"
				Log.i(logID(), message)
				emptyResult()
			}
			else -> emptyResult()
		}
	}

	@Synchronized
	override suspend fun checkForAppUpdate(): HResult<DebugAppUpdate> {
		if (running) return errorResult(ERROR_DUPLICATE, "Cannot run duplicate")
		else running = true
		Log.d(logID(), "Checking for update")

		val rR = iRemoteAppUpdateDataSource.loadGitAppUpdate().let {
			if (it is HResult.Error || it is HResult.Empty) return it.also {
				running = false
			}
			if (it is HResult.Success)
				it.data
			else null
		}!!

		return compareVersion(rR).also {
			iFileAppUpdateDataSource.putAppUpdateInCache(rR, it is HResult.Success)
			running = false
		}
	}
}