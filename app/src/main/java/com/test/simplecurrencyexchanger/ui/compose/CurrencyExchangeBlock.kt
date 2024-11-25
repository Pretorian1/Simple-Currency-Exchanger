package com.test.simplecurrencyexchanger.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.test.simplecurrencyexchanger.R
import com.test.simplecurrencyexchanger.ui.compose.dropdownmenus.DropDownMenuBoxOutlined
import com.test.simplecurrencyexchanger.ui.compose.textfields.SimpleDebouncingOutlinedTextField

@Composable
fun CurrencyExchangeBlock(
    fromCurrency: String,
    availableCurrencies: List<String>,
    tipSold: String? = null,
    tipBought: String? = null,
    onAmountChanged: (Double) -> Unit,
    onCurrencyTypeChanged: (String) -> Unit
) {
    Column {
        Text(stringResource(R.string.title_currency_exchange))
        Spacer(Modifier.height(5.dp))
        HorizontalDivider(thickness = 1.dp)
        Text(stringResource(R.string.sell))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!tipSold.isNullOrEmpty()) Text(tipSold, modifier = Modifier.fillMaxWidth(.3f))
            SimpleDebouncingOutlinedTextField(
                modifier = Modifier.fillMaxWidth(.4f),
                onInputChanged = onAmountChanged
            )
            Text(fromCurrency, modifier = Modifier.fillMaxWidth(.3f))
        }
        Spacer(Modifier.height(5.dp))
        HorizontalDivider(thickness = 1.dp)
        Text(stringResource(R.string.receive))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!tipBought.isNullOrEmpty()) Text(tipBought, modifier = Modifier.fillMaxWidth(.3f))
            if (availableCurrencies.isNotEmpty()) DropDownMenuBoxOutlined(
                modifier = Modifier.fillMaxWidth(.5f),
                availableCurrencies,
                onItemSelected = onCurrencyTypeChanged,
            )
        }
        Spacer(Modifier.height(5.dp))
        HorizontalDivider(thickness = 1.dp)
    }
}