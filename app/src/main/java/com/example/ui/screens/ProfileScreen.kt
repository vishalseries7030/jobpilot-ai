package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CandidateProfileEntity
import com.example.ui.JobPilotViewModel

@Composable
fun ProfileScreen(
    viewModel: JobPilotViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.candidateProfile.collectAsStateWithLifecycle()

    var fullName by remember(currentProfile) { mutableStateOf(currentProfile.fullName) }
    var email by remember(currentProfile) { mutableStateOf(currentProfile.email) }
    var residenceCountry by remember(currentProfile) { mutableStateOf(currentProfile.residenceCountry) }
    var citizenshipCountry by remember(currentProfile) { mutableStateOf(currentProfile.citizenshipCountry) }
    var currentAuthorizations by remember(currentProfile) { mutableStateOf(currentProfile.currentAuthorizations) }
    var requiresSponsorship by remember(currentProfile) { mutableStateOf(currentProfile.requiresSponsorship) }
    var willingToRelocate by remember(currentProfile) { mutableStateOf(currentProfile.willingToRelocate) }
    var preferredDestinations by remember(currentProfile) { mutableStateOf(currentProfile.preferredDestinations) }
    var englishProficiency by remember(currentProfile) { mutableStateOf(currentProfile.englishProficiency) }
    var otherLanguages by remember(currentProfile) { mutableStateOf(currentProfile.otherLanguages) }
    var degree by remember(currentProfile) { mutableStateOf(currentProfile.degree) }
    var graduationDate by remember(currentProfile) { mutableStateOf(currentProfile.graduationDate) }
    var yearsExperience by remember(currentProfile) { mutableDoubleStateOf(currentProfile.yearsExperience) }
    var earliestJoiningDate by remember(currentProfile) { mutableStateOf(currentProfile.earliestJoiningDate) }
    var keySkills by remember(currentProfile) { mutableStateOf(currentProfile.keySkills) }
    var resumeFileName by remember(currentProfile) { mutableStateOf(currentProfile.resumeFileName) }
    var resumeContent by remember(currentProfile) { mutableStateOf(currentProfile.resumeContent) }
    var dailyLimit by remember(currentProfile) { mutableIntStateOf(currentProfile.dailyApplicationLimit) }

    var saveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Badge,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Candidate Visa & Eligibility Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Truthful profile inputs used to compute visa feasibility and draft applications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 1: Identity & Residence
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Personal & Residence Data", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Legal Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = residenceCountry,
                        onValueChange = { residenceCountry = it },
                        label = { Text("Country of Residence") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = citizenshipCountry,
                        onValueChange = { citizenshipCountry = it },
                        label = { Text("Citizenship / Passport") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = currentAuthorizations,
                    onValueChange = { currentAuthorizations = it },
                    label = { Text("Current Local Work Authorizations") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Section 2: Visa Sponsorship & Relocation Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Visa & Relocation Preferences", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Requires Employer Visa Sponsorship", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(
                            "Sets visa screening answers truthfully and filters for sponsor-ready employers.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = requiresSponsorship,
                        onCheckedChange = { requiresSponsorship = it },
                        modifier = Modifier.testTag("requires_sponsorship_switch")
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Willingness to Relocate", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(
                            "Authorizes relocation to company headquarters or hubs abroad.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = willingToRelocate,
                        onCheckedChange = { willingToRelocate = it }
                    )
                }

                OutlinedTextField(
                    value = preferredDestinations,
                    onValueChange = { preferredDestinations = it },
                    label = { Text("Target Countries (Comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = earliestJoiningDate,
                        onValueChange = { earliestJoiningDate = it },
                        label = { Text("Earliest Joining Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = graduationDate,
                        onValueChange = { graduationDate = it },
                        label = { Text("Graduation Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        // Section 3: Education & Technical Skills
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Qualifications & Technical Stack", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                OutlinedTextField(
                    value = degree,
                    onValueChange = { degree = it },
                    label = { Text("Degree & Major (e.g. B.Tech / BSc Computer Science)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = keySkills,
                    onValueChange = { keySkills = it },
                    label = { Text("Key Technical Skills (Comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = englishProficiency,
                        onValueChange = { englishProficiency = it },
                        label = { Text("English Level") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = otherLanguages,
                        onValueChange = { otherLanguages = it },
                        label = { Text("Other Languages") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        // Section 4: Resume & Documents Upload / Text
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Verified Resume & CV Data", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                OutlinedTextField(
                    value = resumeFileName,
                    onValueChange = { resumeFileName = it },
                    label = { Text("Resume Attachment Name (.pdf / .docx)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = resumeContent,
                    onValueChange = { resumeContent = it },
                    label = { Text("Resume Text / Experience Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )
            }
        }

        // Section 5: Daily Application Limit & Anti-Spam Safety
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text("Daily Application Safety Limit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(
                    text = "Prevents bulk spamming and adheres to platform rate limits. Current limit: $dailyLimit applications/day.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = dailyLimit.toFloat(),
                    onValueChange = { dailyLimit = it.toInt() },
                    valueRange = 1f..15f,
                    steps = 13,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Save Button & Confirmation
        if (saveSuccess) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECFDF5))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
                Text(
                    text = "Candidate profile updated! Matching scores refreshed.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF047857)
                )
            }
        }

        Button(
            onClick = {
                val updated = currentProfile.copy(
                    fullName = fullName,
                    email = email,
                    residenceCountry = residenceCountry,
                    citizenshipCountry = citizenshipCountry,
                    currentAuthorizations = currentAuthorizations,
                    requiresSponsorship = requiresSponsorship,
                    willingToRelocate = willingToRelocate,
                    preferredDestinations = preferredDestinations,
                    englishProficiency = englishProficiency,
                    otherLanguages = otherLanguages,
                    degree = degree,
                    graduationDate = graduationDate,
                    yearsExperience = yearsExperience,
                    earliestJoiningDate = earliestJoiningDate,
                    keySkills = keySkills,
                    resumeFileName = resumeFileName,
                    resumeContent = resumeContent,
                    dailyApplicationLimit = dailyLimit
                )
                viewModel.updateProfile(updated)
                saveSuccess = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button")
        ) {
            Text("Save Candidate Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
