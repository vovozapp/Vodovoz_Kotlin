package com.vodovoz.app.feature.profile.viewholders

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.databinding.ItemProfileBestForYouBinding
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.inner.AvailableProductsAdapter
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.profile.viewholders.models.ProfileBestForYou
import com.vodovoz.app.ui.decoration.GridMarginDecoration


class ProfileBestForYouViewHolder(
    view: View,
    cartManager: CartManager,
    likeManager: LikeManager,
    ratingProductManager: RatingProductManager,
    productsClickListener: ProductsClickListener,
    private val onRecyclerReady: () -> Unit,
) : ItemViewHolder<ProfileBestForYou>(view) {

    private val binding: ItemProfileBestForYouBinding = ItemProfileBestForYouBinding.bind(view)

    private val space: Int by lazy {
        itemView.context.resources.getDimension(R.dimen.space_16).toInt()
    }

    private val gridMarginDecoration: GridMarginDecoration by lazy {
        GridMarginDecoration(space)
    }

    private val availableProductsAdapter =
        AvailableProductsAdapter(
            cartManager = cartManager,
            likeManager = likeManager,
            productsClickListener = productsClickListener,
            ratingProductManager = ratingProductManager
        )

    init {
        with(binding.bestForYouProductsRecycler) {
            adapter = availableProductsAdapter
            layoutManager = GridLayoutManager(context, 2)
//            layoutManager = object : GridLayoutManager(context, 2){
//                override fun onLayoutCompleted(state: RecyclerView.State?) {
//                    super.onLayoutCompleted(state)
//                    if(height > 0) {
//                        onRecyclerReady()
//                    }
//                }
//
//            }
            addItemDecoration(gridMarginDecoration)

        }
    }

    override fun bind(item: ProfileBestForYou) {
        super.bind(item)

        binding.tvTitleBestForYou.text = item.data.name
        availableProductsAdapter.submitList(item.data.productUIList)

        binding.bestForYouProductsRecycler.preDraw {
            onRecyclerReady()
        }
    }

    inline fun <T : View> T.preDraw(crossinline callBack: () -> Unit) {

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {

            override fun onPreDraw(): Boolean {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    callBack()
                }
                return true
            }
        })

//        viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                if (measuredWidth > 0 && measuredHeight > 0) {
//                    viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    callBack()
//                }
//            }
//        })
    }

}