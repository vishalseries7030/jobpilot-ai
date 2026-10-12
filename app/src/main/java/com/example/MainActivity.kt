package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.JobPilotViewModel
import com.example.ui.dialogs.ApplicationWorkflowDialog
import com.example.ui.dialogs.CountryInfoDialog
import com.example.ui.dialogs.SponsorshipEvidenceDialog
import com.example.ui.screens.ApplicationsTrackerScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VisaIntelligenceScreen
import com.example.ui.theme.JobPilotTheme

class MainActivity : ComponentActivity() {

    private val viewModel: JobPilotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobPilotTheme {
                JobPilotApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobPilotApp(viewModel: JobPilotViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val evidenceJob by viewModel.selectedJobForEvidence.collectAsStateWithLifecycle()
    val countryInfo by viewModel.selectedCountryForInfo.collectAsStateWithLifecycle()
    val applicationJob by viewModel.selectedJobForApplication.collectAsStateWithLifecycle()
    val candidateProfile by viewModel.candidateProfile.collectAsStateWithLifecycle()
    val applications by viewModel.allApplications.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "JobPilot AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Global",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Tab 0: Discover
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Explore else Icons.Outlined.Explore,
                            contentDescription = "Discover Jobs"
                        )
                    },
                    label = { Text("Discover", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_discover")
                )

                // Tab 1: Visa Guides
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Default.Security else Icons.Outlined.Security,
                            contentDescription = "Visa Guides"
                        )
                    },
                    label = { Text("Visa Intel", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_visa")
                )

                // Tab 2: Applications Tracker
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        if (applications.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("${applications.size}", fontSize = 9.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 2) Icons.Default.Assignment else Icons.Outlined.Assignment,
                                    contentDescription = "Applications"
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.Assignment else Icons.Outlined.Assignment,
                                contentDescription = "Applications"
                            )
                        }
                    },
                    label = { Text("Applications", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_applications")
                )

                // Tab 3: Profile
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DiscoverScreen(viewModel = viewModel)
                1 -> VisaIntelligenceScreen(viewModel = viewModel)
                2 -> ApplicationsTrackerScreen(viewModel = viewModel)
                3 -> ProfileScreen(viewModel = viewModel)
            }

            // Dialogs
            evidenceJob?.let { job ->
                SponsorshipEvidenceDialog(
                    job = job,
                    onDismiss = { viewModel.closeEvidenceDialog() }
                )
            }

            countryInfo?.let { info ->
                CountryInfoDialog(
                    countryInfo = info,
                    onDismiss = { viewModel.closeCountryDialog() }
                )
            }

            applicationJob?.let { job ->
                val existingApp = applications.firstOrNull { it.jobId == job.id }
                ApplicationWorkflowDialog(
                    job = job,
                    profile = candidateProfile,
                    existingApplication = existingApp,
                    onDismiss = { viewModel.closeApplicationWorkflow() },
                    onSubmitApplication = { record ->
                        viewModel.submitApplication(record)
                    }
                )
            }
        }
    }
}
