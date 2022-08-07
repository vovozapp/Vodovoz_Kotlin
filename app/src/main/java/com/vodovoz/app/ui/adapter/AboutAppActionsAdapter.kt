package com.vodovoz.app.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.databinding.ViewHolderAboutAppActionBinding

enum class AboutAppAction(
    val actionImgResId: Int,
    val actionNameResId: Int
) {
    SHARE(
        actionImgResId = R.drawable.png_share_app,
        actionNameResId = R.string.share_app_text
    ),
    RATE(
        actionImgResId = R.drawable.png_ic_favorite,
        actionNameResId = R.string.rate_app_text
    ),
    WRITE_DEVELOPERS(
        actionImgResId = R.drawable.png_letter,
        actionNameResId = R.string.write_to_developers
    )
}

class AboutAppActionsAdapter : RecyclerView.Adapter<AboutAppActionVH>() {

    private var aboutAppActionList = listOf<AboutAppAction>()
    private lateinit var onActionClick: (AboutAppAction) -> Unit

    fun setupListeners(onActionClick: (AboutAppAction) -> Unit) {
        this.onActionClick = onActionClick
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(aboutAppActionList: List<AboutAppAction>) {
        this.aboutAppActionList = aboutAppActionList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AboutAppActionVH(
        binding = ViewHolderAboutAppActionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        context = parent.context,
        onActionClick = onActionClick
    )

    override fun onBindViewHolder(
        holder: AboutAppActionVH,
        position: Int
    ) = holder.onBind(aboutAppActionList[position])

    override fun getItemCount() = aboutAppActionList.size

}

class AboutAppActionVH(
    private val binding: ViewHolderAboutAppActionBinding,
    private val context: Context,
    private val onActionClick: (AboutAppAction) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.root.setOnClickListener { onActionClick(aboutAppAction) } }

    private lateinit var aboutAppAction: AboutAppAction

    fun onBind(aboutAppAction: AboutAppAction) {
        this.aboutAppAction = aboutAppAction
        binding.imgActionImage.setImageDrawable(ContextCompat.getDrawable(context, aboutAppAction.actionImgResId))
        binding.tvAction.text = context.getString(aboutAppAction.actionNameResId)
    }
}

