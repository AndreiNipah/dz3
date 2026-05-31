package com.example.dz3.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.example.dz3.model.CountryDetails

@Composable
fun CountryContent(details: CountryDetails) {
    Column {
        AsyncImage(model = details.flagUrl, contentDescription = "Flag")

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