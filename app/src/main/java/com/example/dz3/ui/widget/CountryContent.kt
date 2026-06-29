package com.example.dz3.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.example.dz3.model.CountryDetails
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun CountryContent(details: CountryDetails) {
    Column {
        AsyncImage(
            model = details.flagUrl,
            contentDescription = "Flag",
            modifier = Modifier
                .width(160.dp)
                .height(100.dp),
            contentScale = ContentScale.Fit
        )

        InfoItem("Name", details.name)
        InfoItem("Official name", details.officialName)
        InfoItem("Code", details.code)
        InfoItem("Capital", details.capital)
        InfoItem("Region", details.region)
        InfoItem("Subregion", details.subregion)
        InfoItem("Population", details.population.toString())
        InfoItem("Languages", details.languages)
        InfoItem("Currencies", details.currencies)
        InfoItem("Timezones", details.timezones)
        InfoItem("Borders", details.borders)
        InfoItem("Google Maps", details.googleMapsUrl.ifBlank { "—" })
    }
}