package com.test.simplecurrencyexchanger.screens.exchangecurrency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.test.simplecurrencyexchanger.R
import com.test.simplecurrencyexchanger.appbars.SimpleTopBar
import com.test.simplecurrencyexchanger.dialogs.SimpleAlertDialog
import com.test.simplecurrencyexchanger.ui.compose.CurrencyExchangeBlock
import com.test.simplecurrencyexchanger.ui.compose.cards.CurrencyItemCard
import com.test.simplecurrencyexchanger.ui.compose.progress.BlockingProgressBar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExchangeCurrencyScreen(viewModel: ExchangeCurrencyViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsState()//WithLifecycle()

    val openAlertDialogForgotUserData = remember { mutableStateOf(false) }
    val openInfoDialog = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest {
            handleEvent(it, openAlertDialogForgotUserData)
        }
    }
    if (openAlertDialogForgotUserData.value) {
        SimpleAlertDialog(
            onDismissRequest = { openAlertDialogForgotUserData.value = false },
            onConfirmation = {
                viewModel.forgetUserData()
                openAlertDialogForgotUserData.value = false
            },
            dialogTitle = " Title",
            dialogText = "Description"
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            SimpleTopBar(
                title = { Text(stringResource(R.string.title_currency_exchange)) },
                actions = {
                    IconButton(onClick = {
                        viewModel.onForgetUserDataClicked()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            //  tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Settings"
                        )
                    }
                })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_your_balance)
            )

            HorizontalDivider(
                thickness = 1.dp,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(10.dp),
            )
            {
                items(uiState.balanceItems) {
                    CurrencyItemCard(
                        currency = it.currency,
                        balance = it.balance,
                        action = viewModel::onCurrencyBalanceClicked
                    )
                }
            }
            CurrencyExchangeBlock(
                availableCurrencies = uiState.availableCurrenciesForExchanging,
                onAmountChanged = viewModel::onCurrencyAmountChanged,
                onCurrencyTypeChanged = viewModel::onCurrencyToByTypeChanged
            )
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .align(Alignment.CenterHorizontally),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                enabled = uiState.exchangeEnabled,
                onClick = { },

                ) {
                Text(stringResource(R.string.convert))
            }

        }
    }

    BlockingProgressBar(uiState.isLoading)

}

fun handleEvent(
    event: ExchangeCurrencyContract.Event,
    alertDialogMutableState: MutableState<Boolean>
) {
    when (event) {
        ExchangeCurrencyContract.Event.ForgotUserData -> alertDialogMutableState.value = true
        is ExchangeCurrencyContract.Event.ShowError -> {}

    }

}

