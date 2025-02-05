package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.model.SpecialPromotionUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialPromotionBottomSheet(
    specialPromotionUi: SpecialPromotionUi,
    state: SheetState = rememberModalBottomSheetState(true),
    onDismissRequest: () -> Unit,
    onButtonClick: (SpecialPromotionUi) -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = specialPromotionUi.name,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            val painter = rememberAsyncImagePainter(specialPromotionUi.picture)
            val imageState by painter.state.collectAsStateWithLifecycle()

            if (imageState is AsyncImagePainter.State.Success) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.FillBounds
                )
            }

            if (specialPromotionUi.text.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = specialPromotionUi.text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val button = specialPromotionUi.actionWithButton.colorfulButton

            VodovozButton(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 18.dp),
                text = button.name,
                onClick = { onButtonClick(specialPromotionUi) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = button.textColor,
                    containerColor = button.backgroundColor
                )
            )
        }
    }
}