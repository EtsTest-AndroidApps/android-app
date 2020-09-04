package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.view.MenuItem
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings

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
 * ====================================================================
 */
/**
 * shosetsu
 * 26 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 *
 * <p>
 *     This file contains random methods/pieces of code that don't seem to be important or make their respective files messy
 * </p>
 */
class SHOWCASE {
	val catalogue = 1
	val downloads = 2
	val library = 3
	val main = 4
	val migration = 5
	val novel = 6
	val novelINFO = 7
	val novelCHAPTERS = 8
	val novelTRACKING = 9
	val reader = 10
	val search = 11
	val updates = 12
	val webView = 13
}

@Deprecated("Find a better use case")
var shoDir: String = "/Shosetsu/"


/**
 * Demarks a list of items, setting only one to be checked.
 *
 * @param menuItems      Items to sort through
 * @param positionSpared Item to set checked
 * @param demarkAction   Any action to proceed with
 */
fun unmarkMenuItems(menuItems: Array<MenuItem>, positionSpared: Int, demarkAction: DeMarkAction?) {
	for (x in menuItems.indices) menuItems[x].isChecked = (x == positionSpared)
	demarkAction?.action(positionSpared)
}


/**
 * Initializes the settings
 *
 * @param mainActivity activity
 */
fun initPreferences(mainActivity: Activity, settings: ShosetsuSettings) {
	var dir = mainActivity.getExternalFilesDir(null)!!.absolutePath
	dir = dir.substring(0, dir.indexOf("/Android"))
	shoDir = settings.settings.getString("dir", "$dir/Shosetsu/")!!
}

/**
 * Abstraction for Actions to take after demarking items. To simplify bulky code
 */
interface DeMarkAction {
	fun action(spared: Int)
}

