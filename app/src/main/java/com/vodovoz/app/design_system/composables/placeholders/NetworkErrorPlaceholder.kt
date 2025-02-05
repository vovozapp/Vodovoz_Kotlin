package com.vodovoz.app.design_system.composables.placeholders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.button.VodovozButton

@Composable
fun NetworkErrorPlaceholder(modifier: Modifier = Modifier, onTryAgainClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1.6f))


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wifi_error),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )


            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(R.string.connection_error),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = stringResource(R.string.check_intenet_connection),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodySmall
            )
        }


        Spacer(modifier = Modifier.weight(1.9f))

        VodovozButton(
            text = stringResource(R.string.try_again),
            onClick = onTryAgainClick,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        )
    }
}

@Preview
@Composable
private fun NetworkErrorPlaceholderPreview() {
    VodovozTheme {
        NetworkErrorPlaceholder { }
    }
}
