package app.shosetsu.android.ui.browse

import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_QUERY
import app.shosetsu.android.ui.search.SearchController
import com.bluelinelabs.conductor.Controller

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class BrowseSearchQuery(
	private val pushController: (Controller) -> Unit
) :
	SearchView.OnQueryTextListener {
	override fun onQueryTextSubmit(s: String): Boolean = true.also {
		pushController(SearchController(bundleOf(BUNDLE_QUERY to s)))
	}

	override fun onQueryTextChange(s: String): Boolean = false
}