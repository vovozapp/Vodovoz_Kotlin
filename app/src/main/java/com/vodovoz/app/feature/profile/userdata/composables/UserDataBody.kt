package com.vodovoz.app.feature.profile.userdata.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun UserDataBody(
    modifier: Modifier = Modifier,
    photo: String,
    photoTitle: String,
    photoDescription: String,
    fields: List<FieldUi>,
    buttonEnabled: Boolean,
    onFieldValueChange: (FieldUi, String) -> Unit,
    onSaveDataClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserDataPhotoColumn(
            modifier = Modifier.padding(top = 8.dp),
            photo = photo,
            photoTitle = photoTitle,
            photoDescription = photoDescription,
            onPhotoClick = onAvatarClick
        )

        UserDataFieldsColumn(
            modifier = Modifier.padding(top = 16.dp),
            fields = fields,
            onFieldValueChange = onFieldValueChange
        )

        VodovozButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
            text = stringResource(id = R.string.save),
            onClick = onSaveDataClick,
            enabled = buttonEnabled
        )

        Text(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.Start)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = onDeleteAccountClick)
                .padding(16.dp),
            text = stringResource(id = R.string.delete_account),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}