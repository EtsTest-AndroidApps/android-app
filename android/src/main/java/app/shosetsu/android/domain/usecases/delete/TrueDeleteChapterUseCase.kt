package app.shosetsu.android.domain.usecases.delete

import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.dto.HResult

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
 * @since 18 / 11 / 2021
 * @author Doomsdayrs
 */
class TrueDeleteChapterUseCase(
	private val repo: IChaptersRepository,
	private val deleteChapter: DeleteChapterPassageUseCase
) {
	suspend operator fun invoke(chapterUI: ChapterUI) {
		this(chapterUI.convertTo())
	}

	suspend operator fun invoke(chapterUI: ChapterEntity): HResult<*> {
		deleteChapter(chapterUI)
		return repo.delete(chapterUI)
	}
}