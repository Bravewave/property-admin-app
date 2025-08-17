package uk.co.sheffield.com4510team03

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import uk.co.sheffield.com4510team03.contact.ContactContent
import uk.co.sheffield.com4510team03.contact.ContactNavEnum
import uk.co.sheffield.com4510team03.contact.ContactViewModel
import uk.co.sheffield.com4510team03.setting.SettingContent
import uk.co.sheffield.com4510team03.database.AppDatabase
import uk.co.sheffield.com4510team03.geolocation.GeoLocationService
import uk.co.sheffield.com4510team03.geolocation.GeoLocationViewModel
import uk.co.sheffield.com4510team03.property.PropertyContent
import uk.co.sheffield.com4510team03.property.PropertyNavEnum
import uk.co.sheffield.com4510team03.property.PropertyNavigation
import uk.co.sheffield.com4510team03.property.PropertyViewModel
import uk.co.sheffield.com4510team03.repair.RepairContent
import uk.co.sheffield.com4510team03.repair.RepairNavEnum
import uk.co.sheffield.com4510team03.repair.RepairNavigation
import uk.co.sheffield.com4510team03.repair.RepairViewModel
import uk.co.sheffield.com4510team03.setting.SettingNavEnum
import uk.co.sheffield.com4510team03.setting.SettingViewModel
import uk.co.sheffield.com4510team03.ui.theme.COM4510Team03Theme

enum class SCREEN {
    PROPERTIES, REPAIRS, CONTACT, SETTINGS
}

class MainActivity : ComponentActivity() {
    var geoLocationService: GeoLocationService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(1000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            COM4510Team03Theme {
                val (screen, setScreen) = rememberSaveable { mutableStateOf(SCREEN.PROPERTIES) }
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val propertyDAO = AppDatabase.getDB(LocalContext.current).propertyDAO()
                val repairDAO = AppDatabase.getDB(LocalContext.current).repairDAO()
                val settingDAO = AppDatabase.getDB(LocalContext.current).settingDAO()

                val propertyViewModel: PropertyViewModel = viewModel()
                val repairViewModel: RepairViewModel = viewModel()
                val contactInfoViewModel: ContactViewModel = viewModel()
                val settingViewModel: SettingViewModel = viewModel()

                val geolocationViewModel = viewModel<GeoLocationViewModel>()

                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

                geoLocationService = GeoLocationService(applicationContext, geolocationViewModel)

                LaunchedEffect("GET_ALL_PROPERTIES") {
                    propertyDAO.getAll().distinctUntilChanged().collectLatest { propertyEntities ->
                        propertyViewModel.updateAllEntities(propertyEntities)
                    }
                }

                LaunchedEffect("GET_ALL_REPAIRS") {
                    repairDAO.getAll().distinctUntilChanged().collectLatest { repairEntities ->
                        repairViewModel.updateAllEntities(repairEntities)
                    }
                }

                LaunchedEffect("GET_ALL_Contractor") {
                    repairViewModel.updateContractorEntityList()
                }

                LaunchedEffect("GET_ALL_CONTACT") {
                    contactInfoViewModel.updateContactEntityList()
                }

                LaunchedEffect("GET_ALL_SETTINGS") {
                    settingDAO.getAll().distinctUntilChanged().collectLatest { settingEntities ->
                        settingViewModel.updateSettings(settingEntities)
                    }
                }

                @SuppressLint("MissingPermission")
                if (hasPermission()) {
                    geoLocationService?.forceLastKnownLocation()
                } else {
                    requestFineLocationPermission()
                }
                if (!hasPhoneNumberPermission()) {
                    requestPhoneNumberPermission()
                }

                if (settingViewModel.contactNumber == "") {
                    var outputNumber = "+4472321212"
                    @SuppressLint("MissingPermission")
                    if (hasPhoneNumberPermission()) {
                        try {
                            val subscriptionManager: SubscriptionManager =
                                (applicationContext.getSystemService(
                                    TELEPHONY_SUBSCRIPTION_SERVICE
                                ) as SubscriptionManager)

                            if (subscriptionManager != null && subscriptionManager.allSubscriptionInfoList != null) {
                                if (subscriptionManager.allSubscriptionInfoList.size > 0) {
                                    Log.i("phone", "Using localNumber")
                                    val id =
                                        subscriptionManager.allSubscriptionInfoList.get(0).subscriptionId
                                    outputNumber = subscriptionManager.getPhoneNumber(id)
                                }
                            }
                        } catch (ex: SecurityException) {
                            Log.i("phone", ex.message.toString())
                        }
                    } else {
                        Log.i("phone", "No perms")
                        requestPhoneNumberPermission()
                    }
                    if (outputNumber == null) {
                        outputNumber = "+4472321212"
                    }
                    settingViewModel.updatePhoneNumberNonSuspended(outputNumber)
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                            Text("Menu", modifier = Modifier.padding(16.dp))
                            HorizontalDivider(modifier = Modifier.padding(4.dp))
                            NavigationDrawerItem(
                                modifier = Modifier.padding(2.dp),
                                icon = navMenuIcon(SCREEN.PROPERTIES, screen),
                                label = { Text(text = "Properties") },
                                selected = screen == SCREEN.PROPERTIES,
                                onClick = {
                                    setScreen(SCREEN.PROPERTIES)
                                    toggleMenu(scope, drawerState)
                                }
                            )
                            NavigationDrawerItem(
                                modifier = Modifier.padding(2.dp),
                                icon = navMenuIcon(SCREEN.REPAIRS, screen),
                                label = { Text(text = "Repairs") },
                                selected = screen == SCREEN.REPAIRS,
                                onClick = {
                                    setScreen(SCREEN.REPAIRS)
                                    toggleMenu(scope, drawerState)
                                }
                            )
                            NavigationDrawerItem(
                                modifier = Modifier.padding(2.dp),
                                icon = navMenuIcon(SCREEN.CONTACT, screen),
                                label = { Text(text = "Contact Info") },
                                selected = screen == SCREEN.CONTACT,
                                onClick = {
                                    setScreen(SCREEN.CONTACT)
                                    toggleMenu(scope, drawerState)
                                }
                            )
                            NavigationDrawerItem(
                                modifier = Modifier.padding(2.dp),
                                icon = navMenuIcon(SCREEN.SETTINGS, screen),
                                label = { Text(text = "Settings") },
                                selected = screen == SCREEN.SETTINGS,
                                onClick = {
                                    setScreen(SCREEN.SETTINGS)
                                    toggleMenu(scope, drawerState)
                                }
                            )
                        }
                    }
                ) {
                    ShowScaffold(
                        screen,
                        drawerState,
                        scope,
                        propertyViewModel,
                        repairViewModel,
                        contactInfoViewModel,
                        geolocationViewModel,
                        settingViewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        @SuppressLint("MissingPermission")
        if (hasPermission()) {
            geoLocationService?.forceLastKnownLocation()
            geoLocationService?.start()
        } else {
            requestFineLocationPermission()
        }
    }


    override fun onPause() {
        super.onPause()
        geoLocationService?.stop()
        Log.i("AriDebug", "OnPause Removed location listener")

    }

    private val GPS_LOCATION_PERMISSION_REQUEST = 1
    private val PHONE_NUMBER_PERMISSION_REQUEST = 1

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            GPS_LOCATION_PERMISSION_REQUEST
        )
    }

    private fun requestPhoneNumberPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            ),
            PHONE_NUMBER_PERMISSION_REQUEST
        )
    }

    private fun hasPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun hasPhoneNumberPermission(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_PHONE_STATE
        ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_PHONE_NUMBERS
        )
    }
}

@Composable
fun ShowScaffold(
    screen: SCREEN,
    drawerState: DrawerState,
    scope: CoroutineScope,
    propertyViewModel: PropertyViewModel,
    repairViewModel: RepairViewModel,
    contactInfoViewModel: ContactViewModel,
    geolocationViewModel: GeoLocationViewModel,
    settingsViewModel: SettingViewModel
) {

    var topBarVar: @Composable () -> Unit = { }
    var bottomBarVar: @Composable () -> Unit = { }
    var content: @Composable () -> Unit = { }

    when (screen) {
        SCREEN.PROPERTIES -> {
            val (propScreen, setPropScreen) = rememberSaveable { mutableStateOf(PropertyNavEnum.HOMES) }
            topBarVar = @Composable {
                CreateTopBar(
                    scope, drawerState
                ) { Greeting(name = "Property Management") }
            }
            bottomBarVar = @Composable { PropertyNavigation(propScreen, setPropScreen) }
            content =
                @Composable { PropertyContent(propScreen, propertyViewModel, geolocationViewModel) }
        }

        SCREEN.REPAIRS -> {
            val (repScreen, setRepScreen) = rememberSaveable { mutableStateOf(RepairNavEnum.SHOW) }
            topBarVar = @Composable {
                CreateTopBar(scope, drawerState) {
                    Greeting(name = "Repair Management")
                }
            }
            bottomBarVar = @Composable { RepairNavigation(repScreen, setRepScreen) }
            content = @Composable {
                RepairContent(
                    repScreen,
                    repairViewModel,
                    propertyViewModel,
                    geolocationViewModel,
                    settingsViewModel
                )
            }
        }

        SCREEN.CONTACT -> {
            val (contactScreen) = rememberSaveable { mutableStateOf(ContactNavEnum.SHOW) }
            topBarVar = @Composable {
                CreateTopBar(scope, drawerState) {
                    Greeting(name = "Regent Court Locksmiths")
                }
            }
            content = @Composable { ContactContent(contactScreen, contactInfoViewModel) }
        }

        SCREEN.SETTINGS -> {
            val (settingScreen) = rememberSaveable { mutableStateOf(SettingNavEnum.SHOW) }
            topBarVar = @Composable {
                CreateTopBar(scope, drawerState) {
                    Greeting(name = "Settings")
                }
            }
            content = @Composable { SettingContent(settingScreen, settingsViewModel) }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBarVar,
        bottomBar = bottomBarVar
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopBar(scope: CoroutineScope, drawerState: DrawerState, content: @Composable () -> Unit) {
    TopAppBar(title = { content() },
        navigationIcon = {
            IconButton(onClick = {
                toggleMenu(scope, drawerState)
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )

}

/**
 * Toggles the [ModalNavigationDrawer] with a delay
 * @param scope the active [CoroutineScope]
 * @param drawerState a [remember]ed [DrawerState]
 * @return [Unit]
 */
private fun toggleMenu(scope: CoroutineScope, drawerState: DrawerState) {
    scope.launch {
        drawerState.apply {
            if (isClosed) open() else {
                delay(100); close()
            }
        }
    }
}

@Composable
private fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

/**
 * Helper function for assigning dynamic icons to a [NavigationDrawerItem]
 * @param targetScreen the screen linked to by the [NavigationDrawerItem]
 * @param currentScreen the active [SCREEN]
 * @return An [Icon] composable
 */
@Composable
private fun navMenuIcon(targetScreen: SCREEN, currentScreen: SCREEN): @Composable (() -> Unit) = {
    val icon = when (targetScreen) {
        SCREEN.PROPERTIES -> if (targetScreen == currentScreen) Icons.Filled.Home else Icons.Outlined.Home
        SCREEN.REPAIRS -> if (targetScreen == currentScreen) Icons.Filled.Build else Icons.Outlined.Build
        SCREEN.CONTACT -> if (targetScreen == currentScreen) Icons.Filled.Info else Icons.Outlined.Info
        SCREEN.SETTINGS -> if (targetScreen == currentScreen) Icons.Filled.Settings else Icons.Outlined.Settings
    }

    Icon(icon, contentDescription = "${targetScreen.name} icon")
}
