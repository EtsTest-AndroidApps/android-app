package app.shosetsu.android.domain.repository.impl

import android.net.Uri
import app.shosetsu.android.domain.repository.base.IBackupUriRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult

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
 * Shosetsu
 *
 * @since 13 / 09 / 2021
 * @author Doomsdayrs
 */
class BackupUriRepository : IBackupUriRepository {
	private var uri: Uri? = null

	override fun give(path: Uri): HResult<*> {
		uri = path
		return successResult()
	}

	override fun take(): HResult<Uri> =
		if (uri != null)
			successResult(uri!!).also { uri = null }
		else emptyResult()
}