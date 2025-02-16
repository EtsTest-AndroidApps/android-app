package app.shosetsu.android.ui.extensionsConfigure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.setting.DropdownSettingContent
import app.shosetsu.android.view.compose.setting.StringSettingContent
import app.shosetsu.android.view.compose.setting.SwitchSettingContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.AExtensionConfigureViewModel
import app.shosetsu.common.domain.model.local.FilterEntity
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.loading
import app.shosetsu.common.enums.TriStateState
import app.shosetsu.lib.ExtensionType
import app.shosetsu.lib.Novel
import app.shosetsu.lib.Version
import coil.compose.rememberImagePainter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import kotlin.random.Random

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
 * 21 / 01 / 2020
 *
 * Opens up detailed view of an extension, allows modifications
 */
class ConfigureExtension(bundle: Bundle) : ShosetsuController(bundle),
	CollapsedToolBarController {
	val viewModel: AExtensionConfigureViewModel by viewModel()

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(args.getInt(BUNDLE_EXTENSION))
	}

	@ExperimentalFoundationApi
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				ConfigureExtensionContent(viewModel, onExit = { activity?.onBackPressed() })
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
	}
}


@ExperimentalFoundationApi
@Composable
fun ConfigureExtensionContent(
	viewModel: AExtensionConfigureViewModel,
	onExit: () -> Unit
) {
	val extensionUIResult by viewModel.liveData.observeAsState(loading)
	val extensionListingResult by viewModel.extensionListing.observeAsState(loading)
	val extensionSettingsResult by viewModel.extensionSettings.observeAsState(loading)


	LazyColumn(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		state = rememberLazyListState(),
		contentPadding = PaddingValues(bottom = 8.dp)
	) {
		stickyHeader(1000000) {
			extensionUIResult.handle {
				ConfigureExtensionHeaderContent(it) {
					viewModel.uninstall(it)
					onExit()
				}
			}
		}

		extensionListingResult.handle { itemData ->
			item {
				DropdownSettingContent(
					title = stringResource(R.string.listings),
					description = stringResource(R.string.controller_configure_extension_listing_desc),
					choices = itemData.choices.toTypedArray(),
					selection = itemData.selection.takeIf { it != -1 } ?: 0,
					onSelection = { index ->
						viewModel.setSelectedListing(index)
					},
					modifier = Modifier.fillMaxWidth()
						.padding(top = 8.dp, start = 16.dp, end = 16.dp)
				)
			}
		}

		extensionSettingsResult.handle {
			SettingsItemAsCompose(this, viewModel, it)
		}
	}
}

fun SettingsItemAsCompose(
	column: LazyListScope,
	viewModel: AExtensionConfigureViewModel,
	list: List<FilterEntity>
) {
	list.forEach { data ->
		when (data) {
			is FilterEntity.Header -> {
				column.item(Random.nextInt() + 1000000) {
					Row(
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					) {
						Text(data.name)
						Divider()
					}
				}
			}
			is FilterEntity.Separator -> {
				column.item(Random.nextInt() + 1000000) {
					Divider()
				}
			}
			is FilterEntity.Text -> {
				column.item(data.id) {
					StringSettingContent(
						data.name,
						"",
						data.state,
						onValueChanged = { value ->
							viewModel.saveSetting(data.id, value)
						},
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					)
				}
			}
			is FilterEntity.Switch -> {
				column.item(data.id) {
					SwitchSettingContent(
						data.name,
						"",
						isChecked = data.state,
						onCheckChange = { newValue ->
							viewModel.saveSetting(data.id, newValue)
						},
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					)
				}
			}
			is FilterEntity.TriState -> {
				column.item(data.id) {
					Row(
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(text = data.name)
						TriStateCheckbox(
							state = when (data.state) {
								TriStateState.CHECKED -> ToggleableState.On
								TriStateState.UNCHECKED -> ToggleableState.Indeterminate
								else -> ToggleableState.Off
							},
							onClick = {
								viewModel.saveSetting(data.id, data.state.cycle(false).name)
							}
						)
					}
				}
			}
			is FilterEntity.Dropdown -> {
				column.item(data.id) {
					DropdownSettingContent(
						title = data.name,
						description = "",
						choices = data.choices.toTypedArray(),
						selection = data.selected,
						onSelection = { index ->
							viewModel.saveSetting(data.id, index)
						},
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					)
				}
			}
			is FilterEntity.FList -> {
				column.item(Random.nextInt() + 1000000) {
					Row(
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					) {
						Text(data.name)
						Divider()
					}
				}
				SettingsItemAsCompose(column, viewModel, data.filters.toList())
			}
			is FilterEntity.Group -> {
				SettingsItemAsCompose(column, viewModel, data.filters.toList())
			}
			is FilterEntity.Checkbox -> {
				column.item(data.id) {
					SwitchSettingContent(
						data.name,
						"",
						isChecked = data.state,
						onCheckChange = { newValue ->
							viewModel.saveSetting(data.id, newValue)
						},
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					)
				}
			}
			is FilterEntity.RadioGroup -> {
				column.item(data.id) {
					DropdownSettingContent(
						title = data.name,
						description = "",
						choices = data.choices.toTypedArray(),
						selection = data.selected,
						onSelection = { index ->
							viewModel.saveSetting(data.id, index)
						},
						modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewConfigureExtensionHeaderContent() {
	ConfigureExtensionHeaderContent(
		ExtensionUI(
			1,
			1,
			"This is an extension",
			"fileName",
			"",
			"en",
			true,
			true,
			installedVersion = Version(1, 0, 0),
			Version(1, 0, 0),
			Novel.ChapterType.HTML,
			"",
			ExtensionType.LuaScript
		)
	) {

	}
}

@Composable
fun ConfigureExtensionHeaderContent(
	extension: ExtensionUI,
	onUninstall: () -> Unit
) {
	Card {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					painter = if (!extension.imageURL.isNullOrEmpty()) {
						rememberImagePainter(extension.imageURL)
					} else {
						painterResource(R.drawable.broken_image)
					},
					stringResource(R.string.extension_image_desc),
					modifier = Modifier.size(100.dp)
				)

				Column {
					Text(extension.name)
					Row(
						horizontalArrangement = Arrangement.SpaceBetween,
					) {
						Text(extension.id.toString())
						Text(extension.fileName, modifier = Modifier.padding(start = 16.dp))
					}
					Text(extension.lang)
				}
			}

			IconButton(
				onClick = onUninstall,
			) {
				Icon(
					painterResource(R.drawable.trash),
					stringResource(R.string.uninstall)
				)
			}
		}
	}
}