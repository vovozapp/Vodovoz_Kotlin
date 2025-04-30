package com.vodovoz.app.feature.questionnaires

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.design_system.composables.button.VodovozButtonsColumn
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.design_system.model.ColorfulButtonUi

@Composable
fun WelcomeQuestionnairesScreen(
    uiState: QuestionnairesFlowViewModel.QuestionnairesUiState.Welcome,
    onWelcomeButtonClick: (ColorfulButtonUi) -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        VodovozTopBar(onBack = onBackClick, title = uiState.title)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = uiState.image),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = AnnotatedString.fromHtml(uiState.header),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = AnnotatedString.fromHtml(uiState.description),
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }


            VodovozButtonsColumn(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                buttons = uiState.buttons,
                onButtonClick = onWelcomeButtonClick
            )

            Spacer(modifier = Modifier.weight(2f))

        }
    }
}