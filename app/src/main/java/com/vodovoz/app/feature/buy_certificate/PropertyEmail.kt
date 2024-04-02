package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyEmail(
    property: BuyCertificatePropertyUI,
    onValueChange: (String) -> Unit,
) {

    PropertyItem(property = property) {
        BasicTextField(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .background(color = Color.White),
            value = property.currentValue,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            onValueChange = { newText ->
                onValueChange(newText)
            },
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(
                    Font(R.font.rotonda_normal)
                )
            ),
            cursorBrush = SolidColor(colorResource(R.color.bluePrimary)),
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = property.currentValue,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                singleLine = true,
                enabled = false,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ), // this is how you can remove the padding
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    cursorColor = colorResource(id = R.color.bluePrimary),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        text = "example@mail.com",
                        color = colorResource(id = R.color.blackTextLight),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.rotonda_normal)
                        ),
                    )
                }
            )
        }
    }
}














