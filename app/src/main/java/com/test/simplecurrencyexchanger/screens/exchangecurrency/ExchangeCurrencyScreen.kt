package com.test.simplecurrencyexchanger.screens.exchangecurrency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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

    val uiState by viewModel.uiState.collectAsState()

    val openAlertDialogForgotUserData = remember { mutableStateOf(false) }
    val openInfoDialog = remember { mutableStateOf(false) }
    val openErrorDialog = remember { mutableStateOf(false) }

    val dialogMessage = remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest {
            handleEvent(
                event = it,
                alertDialogMutableState = openAlertDialogForgotUserData,
                infoDialogMutableState = openInfoDialog,
                errorDialogMutableState = openErrorDialog,
                messageMutableState = dialogMessage
            )
        }
    }
    if (openAlertDialogForgotUserData.value) {
        SimpleAlertDialog(
            onDismissRequest = { openAlertDialogForgotUserData.value = false },
            onConfirmation = {
                viewModel.forgetUserData()
                openAlertDialogForgotUserData.value = false
            },
            dialogTitle = stringResource(R.string.warning),
            dialogText = stringResource(R.string.dsc_are_you_sure_to_clear_user_data),
            icon = Icons.Default.Warning
        )
    }

    if (openInfoDialog.value) {
        SimpleAlertDialog(
            onDismissRequest = { openInfoDialog.value = false },
            onConfirmation = {},
            confirmationButtonEnable = false,
            dialogTitle = stringResource(R.string.info),
            dialogText = if (dialogMessage.value.isEmpty()) stringResource(R.string.error_missed_information)
            else dialogMessage.value,
            icon = Icons.Default.Info
        )
    }

    if (openErrorDialog.value) {
        SimpleAlertDialog(
            onDismissRequest = { openErrorDialog.value = false },
            onConfirmation = {},
            confirmationButtonEnable = false,
            dialogTitle = stringResource(R.string.error),
            dialogText = if (dialogMessage.value.isEmpty()) stringResource(R.string.error_missed_information)
            else dialogMessage.value,
            icon = Icons.Default.Warning
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        SimpleTopBar(title = { Text(stringResource(R.string.title_currency_exchange)) }, actions = {
            IconButton(onClick = {
                viewModel.onForgetUserDataClicked()
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_forget_user_data)
                )
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()
                ),
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
            ) {
                items(uiState.balanceItems) {
                    CurrencyItemCard(
                        currency = it.currency,
                        balance = it.balance,
                        action = viewModel::onCurrencyBalanceClicked
                    )
                }
            }
            if (!uiState.selectedCurrencyForSold.isNullOrEmpty()) {
                CurrencyExchangeBlock(
                    isOperationSuccess = uiState.exchangeEnabled,
                    fromCurrency = uiState.selectedCurrencyForSold!!,
                    availableCurrencies = uiState.availableCurrenciesForExchanging,
                    tipSold = uiState.possibleSoldTip,
                    tipBought = uiState.possibleBoughtTip,
                    onAmountChanged = viewModel::onCurrencyAmountChanged,
                    onCurrencyTypeChanged = viewModel::onCurrencyToByTypeChanged
                )
            }
            Spacer(Modifier.height(20.dp))
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .align(Alignment.CenterHorizontally)
                    .defaultMinSize(minWidth = 50.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                enabled = uiState.exchangeEnabled,
                onClick = {
                    viewModel.saveUserData()
                },

                ) {
                Text(
                    text = stringResource(R.string.convert),
                    style = MaterialTheme.typography.titleLarge
                )
            }

        }
    }
    BlockingProgressBar(uiState.isLoading)
}

fun handleEvent(
    event: ExchangeCurrencyContract.Event,
    alertDialogMutableState: MutableState<Boolean>,
    infoDialogMutableState: MutableState<Boolean>,
    errorDialogMutableState: MutableState<Boolean>,
    messageMutableState: MutableState<String>
) {
    when (event) {
        is ExchangeCurrencyContract.Event.ShowInfo -> {
            messageMutableState.value = event.message
            infoDialogMutableState.value = true
        }

        ExchangeCurrencyContract.Event.ForgotUserData -> alertDialogMutableState.value = true
        is ExchangeCurrencyContract.Event.ShowError -> {
            messageMutableState.value = event.message
            errorDialogMutableState.value = true
        }
    }
}