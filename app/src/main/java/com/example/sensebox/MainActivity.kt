package com.example.sensebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.sensebox.ui.compose.boxlist.BoxListScreen
import com.example.sensebox.ui.compose.boxlist.BoxListViewModel
import com.example.sensebox.ui.compose.boxlist.BoxUiState
import com.example.sensebox.ui.compose.home.HomeScreen
import com.example.sensebox.ui.navigation.BoxAppNavHost
import com.example.sensebox.ui.navigation.MyModalNavigationDrawer
import com.example.sensebox.ui.theme.SenseBoxTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Modern Android projects use a single Activity.
 * In order for an Activity to work with compose functions, it must be inherited from the ComponentActivity class.
 * The @AndroidEntryPoint annotation is used to tell Hilt to use the Dependency Injection.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /**
             * Compose function that defines the theme of the application (colors, fonts, shape sizes, etc.).
             * If the android version supports Dynamic Theme, then the system colors will be used,
             * if not - the default colors
             */
            SenseBoxTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /**
                     * The application content is wrapped in a navigation panel (FlyoutPage in MAUI),
                     * which in turn contains a navigation host function. Although this is not entirely correct,
                     * since the BoxDetailScreen should not have access to the navigation panel.
                     */
                    val navController = rememberNavController()
                    MyModalNavigationDrawer (
                        navHostController = navController
                    ) { drawerState ->
                        BoxAppNavHost(
                            navController = navController,
                            drawerState = drawerState,
                        )
                    }
                }
            }
        }
    }
}
