package br.com.duannistontriocodechallenge.game.board.application.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.duannistontriocodechallenge.R
import br.com.duannistontriocodechallenge.databinding.GameBoardAdapterItemBinding
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData

class GameBoardAdapter :
    ListAdapter<GameBoardAdapterItemData, GameBoardAdapter.GameBoardViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameBoardViewHolder {
        val view =
            GameBoardAdapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameBoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameBoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GameBoardViewHolder(private val binding: GameBoardAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(item: GameBoardAdapterItemData) {
            when (item.gameBoardAdapterType) {
                GameBoardAdapterItemData.GameBoardAdapterType.EMPTY -> {
                    val color = ContextCompat.getColor(context, R.color.gray)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))
                }

                GameBoardAdapterItemData.GameBoardAdapterType.PRIZE -> {
                    val color = ContextCompat.getColor(context, R.color.yellow)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))

                }

                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT -> {
                    val color = ContextCompat.getColor(context, R.color.purple_1)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))

                }

                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT -> {
                    val color = ContextCompat.getColor(context, R.color.teal_1)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))

                }

                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE -> {
                    val color = ContextCompat.getColor(context, R.color.purple_2)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))

                }

                GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE -> {
                    val color = ContextCompat.getColor(context, R.color.teal_2)
                    ImageViewCompat.setImageTintList(binding.ivItem, ColorStateList.valueOf(color))

                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GameBoardAdapterItemData>() {
            override fun areItemsTheSame(
                oldItem: GameBoardAdapterItemData,
                newItem: GameBoardAdapterItemData
            ): Boolean {
                return oldItem ==newItem
            }

            override fun areContentsTheSame(
                oldItem: GameBoardAdapterItemData,
                newItem: GameBoardAdapterItemData
            ): Boolean {
                return oldItem ==newItem
            }
        }
    }
}
