package app.shosetsu.android.common.enums

import androidx.annotation.StringRes
import com.github.doomsdayrs.apps.shosetsu.R

enum class TextAsset(val assetName: String, @StringRes val titleRes: Int) {
	LICENSE(
		"license-gplv3",
		R.string.license
	),
	DISCLAIMER(
		"disclaimer",
		R.string.disclaimer
	);
}