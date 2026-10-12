package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OpportunityScore

@Composable
fun OpportunityScorePill(
    score: OpportunityScore,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score.globalScore >= 80 -> Color(0xFF10B981) // Emerald
        score.globalScore >= 65 -> Color(0xFF0284C7) // Sky
        score.globalScore >= 45 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFEF4444) // Red
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(scoreColor.copy(alpha = 0.12f))
            .border(1.dp, scoreColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Global Opportunity Score",
                    tint = scoreColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${score.globalScore}/100",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = scoreColor
                )
            }
            // Split breakdown: Tech vs Visa
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Tech Match",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${score.technicalMatch}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = "Visa Feasibility",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${score.visaFeasibility}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}
