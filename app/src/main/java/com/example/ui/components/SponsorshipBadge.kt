package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SponsorshipStatus
import com.example.ui.theme.StatusSponsorConditional
import com.example.ui.theme.StatusSponsorConfirmed
import com.example.ui.theme.StatusSponsorPotential
import com.example.ui.theme.StatusSponsorUnavailable
import com.example.ui.theme.StatusSponsorUnclear

@Composable
fun SponsorshipBadge(
    status: SponsorshipStatus,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, icon) = when (status) {
        SponsorshipStatus.CONFIRMED -> Triple(
            StatusSponsorConfirmed.copy(alpha = 0.15f),
            StatusSponsorConfirmed,
            Icons.Default.VerifiedUser
        )
        SponsorshipStatus.CONDITIONAL -> Triple(
            StatusSponsorConditional.copy(alpha = 0.15f),
            StatusSponsorConditional,
            Icons.Default.CheckCircle
        )
        SponsorshipStatus.POTENTIALLY_FRIENDLY -> Triple(
            StatusSponsorPotential.copy(alpha = 0.18f),
            Color(0xFFD97706),
            Icons.Default.Info
        )
        SponsorshipStatus.UNCLEAR -> Triple(
            StatusSponsorUnclear.copy(alpha = 0.18f),
            StatusSponsorUnclear,
            Icons.Default.HelpOutline
        )
        SponsorshipStatus.UNAVAILABLE -> Triple(
            StatusSponsorUnavailable.copy(alpha = 0.15f),
            StatusSponsorUnavailable,
            Icons.Default.Block
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.title,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (compact) "[Cat ${status.code}] ${status.title}" else "Cat ${status.code}: ${status.title}",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
