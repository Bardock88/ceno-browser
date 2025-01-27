package ie.equalit.ceno.home

import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import ie.equalit.ceno.databinding.HomeMessageCardItemBinding
import ie.equalit.ceno.home.sessioncontrol.HomePageInteractor
import ie.equalit.ceno.utils.view.CenoViewHolder

class CenoMessageViewHolder (
    itemView: View,
    interactor: HomePageInteractor
) : BaseHomeCardViewHolder(itemView, interactor){

    private val binding = HomeMessageCardItemBinding.bind(itemView)

    init {
        cardType = homepageCardType
        binding.closeButton.setOnClickListener {
            interactor.onRemoveCard(cardType)
        }
    }

    fun bind(message: CenoMessageCard) {
        binding.tvCardTitle.text = message.title
        binding.tvCardText.text = message.text
//        binding.ivMessageIcon.setImageDrawable(message.icon)
    }

    companion object {
        val homepageCardType = HomepageCardType.BASIC_MESSAGE_CARD
    }
}