package com.example.playlistmaker.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.AudioPlayerScreen
import com.example.playlistmaker.playlist.ui.MediaLibraryScreen
import com.example.playlistmaker.playlist.ui.create.CreatePlaylistScreen
import com.example.playlistmaker.playlist.ui.details.PlaylistDetailsScreen
import com.example.playlistmaker.playlist.ui.edit.EditPlaylistScreen
import com.example.playlistmaker.search.ui.SearchScreen
import com.example.playlistmaker.settings.ui.SettingsScreen
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.ui.theme.AppTheme
import com.google.gson.Gson
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph() {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val isDarkTheme by settingsViewModel.isDarkTheme.observeAsState()
    val actualDarkTheme = isDarkTheme ?: isSystemInDarkTheme()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { dest ->
        !dest.hasRoute<AudioPlayerRoute>() &&
        !dest.hasRoute<CreatePlaylistRoute>() &&
        !dest.hasRoute<PlaylistDetailsRoute>() &&
        !dest.hasRoute<EditPlaylistRoute>()
    } ?: true

    AppTheme(darkTheme = actualDarkTheme) {
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier.height(56.dp),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        val navItemColors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = Color.Transparent
                        )
                        NavigationBarItem(
                            selected = currentDestination?.hasRoute<SearchRoute>() == true,
                            onClick = {
                                navController.navigate(SearchRoute) {
                                    popUpTo<SearchRoute> { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search_19),
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(R.string.text_search)) },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = currentDestination?.hasRoute<MediaLibraryRoute>() == true,
                            onClick = {
                                navController.navigate(MediaLibraryRoute) {
                                    popUpTo<SearchRoute> { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_library_20),
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(R.string.text_library)) },
                            colors = navItemColors
                        )
                        NavigationBarItem(
                            selected = currentDestination?.hasRoute<SettingsRoute>() == true,
                            onClick = {
                                navController.navigate(SettingsRoute) {
                                    popUpTo<SearchRoute> { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_settings_20),
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(R.string.text_settings)) },
                            colors = navItemColors
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = SearchRoute,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable<SearchRoute> {
                    SearchScreen(
                        onTrackClick = { track ->
                            navController.navigate(AudioPlayerRoute(Gson().toJson(track)))
                        }
                    )
                }

                composable<MediaLibraryRoute> {
                    MediaLibraryScreen(
                        onTrackClick = { track ->
                            navController.navigate(AudioPlayerRoute(Gson().toJson(track)))
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(PlaylistDetailsRoute(playlist.id))
                        },
                        onCreatePlaylistClick = {
                            navController.navigate(CreatePlaylistRoute)
                        }
                    )
                }

                composable<SettingsRoute> {
                    SettingsScreen(viewModel = settingsViewModel)
                }

                composable<AudioPlayerRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<AudioPlayerRoute>()
                    AudioPlayerScreen(
                        trackJson = route.trackJson,
                        navController = navController,
                        onBack = { navController.popBackStack() },
                        onNavigateToCreatePlaylist = { navController.navigate(CreatePlaylistRoute) }
                    )
                }

                composable<CreatePlaylistRoute> {
                    CreatePlaylistScreen(
                        onBack = { navController.popBackStack() },
                        onCreated = { playlistId ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("created_playlist_id", playlistId)
                            navController.popBackStack()
                        }
                    )
                }

                composable<PlaylistDetailsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PlaylistDetailsRoute>()
                    PlaylistDetailsScreen(
                        playlistId = route.playlistId,
                        onTrackClick = { track ->
                            navController.navigate(AudioPlayerRoute(Gson().toJson(track)))
                        },
                        onBack = { navController.popBackStack() },
                        onEditClick = { id ->
                            navController.navigate(EditPlaylistRoute(id))
                        }
                    )
                }

                composable<EditPlaylistRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<EditPlaylistRoute>()
                    EditPlaylistScreen(
                        playlistId = route.playlistId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
