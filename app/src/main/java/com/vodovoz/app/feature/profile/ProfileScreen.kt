package com.vodovoz.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vodovoz.app.feature.home.composables.AuthScrollImagePager
import com.vodovoz.app.feature.profile.composables.ProfileCardsRow
import com.vodovoz.app.feature.profile.composables.ProfileMenuColumn
import com.vodovoz.app.feature.profile.composables.ProfileUserInfoRow
import com.vodovoz.app.feature.profile.composables.ProfileWalletItemsRow

@Suppress("NonSkippableComposable")
@Composable
fun ProfileScreen(
    viewModel: ProfileFlowViewModel,
    viewState: ProfileFlowViewModel.ProfileState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .clip(
                    MaterialTheme.shapes.large.copy(
                        topStart = CornerSize(0.dp),
                        topEnd = CornerSize(0.dp)
                    )
                )
                .background(MaterialTheme.colorScheme.background)
        ) {
            ProfileUserInfoRow(
                userInfoBlock = viewState.userInfoBlock,
                onClick = { viewModel.navigateToUserData() }
            )
            ProfileCardsRow(
                modifier = Modifier.padding(top = 16.dp),
                cards = viewState.cards,
                onCardClick = { }
            )
            ProfileWalletItemsRow(
                modifier = Modifier.padding(top = 16.dp),
                walletItems = viewState.walletItems,
                onCardClick = { }
            )

            val bannerImages = viewState.banners.map { bannerUi -> bannerUi.detailPicture }
            val pagerState = rememberPagerState { bannerImages.size }

            AuthScrollImagePager(
                modifier = Modifier
                    .padding(top = 17.dp, bottom = 16.dp)
                    .height(68.dp),
                images = bannerImages,
                onImageClick = {

                },
                pageWidth = Dp.Unspecified,
                pagerState = pagerState
            )
        }



        ProfileMenuColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            menuItems = viewState.smallMenu,
            onItemClick = {

            }
        )

        ProfileMenuColumn(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(
                    MaterialTheme.shapes.large.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                ),
            menuItems = viewState.normalMenu,
            onItemClick = {

            }
        )
    }
}