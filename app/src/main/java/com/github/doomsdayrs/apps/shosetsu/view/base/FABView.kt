package com.github.doomsdayrs.apps.shosetsu.view.base

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
 * 03 / 06 / 2020
 *
 * For views with an FAB, to provide proper transition support
 */
interface FABView {
	/**
	 * Hide the FAB
	 */
	fun hideFAB()

	/**
	 * Show the FAB
	 */
	fun showFAB()
}