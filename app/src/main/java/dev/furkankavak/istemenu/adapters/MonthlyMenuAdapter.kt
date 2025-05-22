package dev.furkankavak.istemenu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.furkankavak.istemenu.databinding.ItemMenuCardBinding
import dev.furkankavak.istemenu.models.MenuDisplayItem

class MonthlyMenuAdapter(
    private var menuItems: List<MenuDisplayItem> = emptyList()
) : RecyclerView.Adapter<MonthlyMenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size

    fun updateData(newMenuItems: List<MenuDisplayItem>) {
        menuItems = newMenuItems
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(private val binding: ItemMenuCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuDisplayItem) {
            binding.apply {
                tvDate.text = menuItem.date
                tvCalories.text = "${menuItem.calories} kcal"
                tvMainDish.text = menuItem.mainDish
                tvSideDish1.text = menuItem.sideDish1
                tvSideDish2.text = menuItem.sideDish2
                tvDessert.text = menuItem.dessert
                tvLikes.text =
                    "${menuItem.likes} kişi beğendi • ${menuItem.dislikes} kişi beğenmedi"

                // Progress bar oranını ayarla
                updateLikeProgress(menuItem.likes, menuItem.dislikes)
            }
        }

        private fun updateLikeProgress(likes: Int, dislikes: Int) {
            val total = likes + dislikes
            if (total > 0) {
                val likePercentage = (likes * 100) / total
                binding.customProgressBar.post {
                    try {
                        val totalWidth = binding.progressBackground.width
                        if (totalWidth > 0) {
                            val progressWidth = (totalWidth * likePercentage) / 100

                            val layoutParams = binding.progressRating.layoutParams
                            layoutParams.width = progressWidth
                            binding.progressRating.layoutParams = layoutParams
                        } else {
                            // Eğer width 0 ise sonraki frame'de tekrar dene
                            binding.customProgressBar.post {
                                updateLikeProgress(likes, dislikes)
                            }
                        }
                    } catch (e: Exception) {
                        // Hata durumunda, progress bar'ı güncelleme
                        e.printStackTrace()
                    }
                }
            } else {
                // If no reactions, set progress to 0
                binding.customProgressBar.post {
                    try {
                        val layoutParams = binding.progressRating.layoutParams
                        layoutParams.width = 0
                        binding.progressRating.layoutParams = layoutParams
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}