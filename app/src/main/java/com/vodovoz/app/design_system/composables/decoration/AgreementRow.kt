package com.vodovoz.app.design_system.composables.decoration

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.vodovozTextLinkStyle

@Composable
fun AgreementRow(
    modifier: Modifier = Modifier,
    checked: Boolean,
    htmlText: String,
    onCheckedChange: (Boolean) -> Unit,
    onUrlClick: (url: String, index: Int) -> Unit,
) {
    Row(modifier = modifier.height(IntrinsicSize.Max)) {
        Checkbox(
            modifier = Modifier.size(24.dp),
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.background,
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
        Spacer(modifier = Modifier.width(16.dp))

        val links = remember {
            mutableStateOf(listOf<String>())
        }

        val agreement = AnnotatedString.fromHtml(
            htmlString = htmlText,
            linkStyles = vodovozTextLinkStyle,
            linkInteractionListener = { link ->
                val linkUrl = link as? LinkAnnotation.Url
                linkUrl.toString()
                linkUrl?.url?.runCatching {
                    val url = linkUrl.url
                    onUrlClick(linkUrl.url, links.value.indexOf(url))
                }
            }
        )



        Text(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.CenterStart),
            text = agreement,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        LaunchedEffect(agreement) {
            links.value = agreement.getLinkAnnotations(0, Int.MAX_VALUE)
                .mapNotNull { (it.item as? LinkAnnotation.Url)?.url }
        }
    }
}

@Preview
@Composable
private fun AgreementRowPreview() {
    VodovozTheme {
        AgreementRow(checked = true, htmlText = "Agreement text", onCheckedChange = {}) { i, i2 ->

        }
    }
}