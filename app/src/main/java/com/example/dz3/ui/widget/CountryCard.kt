package com.example.dz3.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dz3.model.Country

@Composable
fun CountryCard(
    country: Country,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("country_card_${country.code}")
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = "Flag",
            modifier = Modifier
                .width(96.dp)
                .height(64.dp),
            contentScale = ContentScale.Fit
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(country.name, fontWeight = FontWeight.Bold)
            Text("Code: ${country.code}")
            Text("Capital: ${country.capital}")
            Text("Region: ${country.region}")
            Text("Population: ${country.population}")
        }

        IconButton(onClick = onToggleFavourite) {
            if (isFavourite) {
                Icon(Icons.Filled.Favorite, contentDescription = "Remove favourite")
            } else {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Add favourite")
            }
        }
    }
}