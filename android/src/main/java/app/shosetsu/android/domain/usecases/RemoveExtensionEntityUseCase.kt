package app.shosetsu.android.domain.usecases

import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionEntitiesRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.ifSo

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
 * @since 16 / 08 / 2021
 * @author Doomsdayrs
 *
 * Completely remove an extension from Shosetsu
 */
class RemoveExtensionEntityUseCase(
	private val extensionRepository: IExtensionsRepository,
	private val extensionEntitiesRepository: IExtensionEntitiesRepository
) {
	suspend operator fun invoke(entity: ExtensionEntity) {
		extensionEntitiesRepository.uninstall(entity) ifSo {
			extensionRepository.delete(entity)
		}
	}
}