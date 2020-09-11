package com.github.doomsdayrs.apps.shosetsu.ui.settings.sub

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.readAsset
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import java.util.*


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
 */
class TextAssetReader(bundleI: Bundle) : ViewedController(bundleI) {
	companion object {
		const val BUNDLE_KEY: String = "target"
	}

	enum class Target(val bundle: Bundle) {
		LICENSE(bundleOf(Pair(BUNDLE_KEY, "license"))),
		DISCLAIMER(bundleOf(Pair(BUNDLE_KEY, "disclaimer")));
	}

	override val layoutRes: Int = R.layout.large_reader
	private var type: String = ""
	private var message: String = ""

	private fun handleB() {
		Log.d(logID(), "Setting Message")
		type = args.getString(BUNDLE_KEY, "license")
		message = activity?.readAsset("$type.text") ?: ""
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putString("m", message)
		outState.putString("t", type)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		message = savedInstanceState.getString("m", "")
		type = savedInstanceState.getString("t", "")
	}

	@ExperimentalStdlibApi
	override fun onViewCreated(view: View) {
		if (message.isEmpty()) handleB()
		activity?.title = (type.capitalize(Locale.ROOT))
		view.findViewById<TextView>(R.id.title).text = message
	}

}