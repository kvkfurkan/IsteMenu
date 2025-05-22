package dev.furkankavak.istemenu.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentHomeBinding
import dev.furkankavak.istemenu.databinding.LayoutSkeletonHomeBinding
import dev.furkankavak.istemenu.model.ApiResponse
import dev.furkankavak.istemenu.model.Menu
import dev.furkankavak.istemenu.model.ReactionResponse
import dev.furkankavak.istemenu.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _skeletonBinding: LayoutSkeletonHomeBinding? = null
    private val skeletonBinding get() = _skeletonBinding!!

    // Current menu ID
    private var currentMenuId: Int = -1

    // Reaction states
    private var effectiveLikes: Int = 0
    private var effectiveDislikes: Int = 0
    private var currentUserReaction: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _skeletonBinding = LayoutSkeletonHomeBinding.inflate(inflater, container, false)


        binding.cardMenu.visibility = View.GONE
        binding.cardRating.visibility = View.GONE


        val rootLayout = binding.root as ViewGroup
        rootLayout.addView(skeletonBinding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.orange_very_light)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
        val formattedDate = dateFormat.format(currentDate)

        binding.tvDate.text = formattedDate
        binding.tvSubtitle.text = "Bugünkü Menü"

        setupRatingButtons()
        fetchTodayMenu()
    }

    private fun fetchTodayMenu() {

        showSkeleton()
            RetrofitClient.apiService.getMenu().enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!

                        if (apiResponse.success) {
                            hideSkeleton()

                            val menus = apiResponse.data.dailyMenus
                            val todayMenu = findTodaysMenu(menus)

                            if (todayMenu != null && todayMenu.menu_items.isNotEmpty()) {
                                currentMenuId = todayMenu.id
                                effectiveLikes = todayMenu.likesCount
                                effectiveDislikes = todayMenu.dislikesCount
                                currentUserReaction = todayMenu.userReaction

                                updateUIWithMenuData(todayMenu)
                                updateReactionButtons()
                            } else {
                                showWeekendMessage()
                            }
                        } else {
                            Log.e("HomeFragment", "API error: ${apiResponse.message}")
                            showWeekendMessage()
                        }
                    } else {
                        Log.e("HomeFragment", "Error fetching menus: ${response.message()}")
                        Toast.makeText(context, "Menü bilgileri yüklenemedi", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    skeletonBinding.shimmerLayout.stopShimmer()
                    skeletonBinding.shimmerLayout.visibility = View.GONE
                    binding.cardMenu.visibility = View.VISIBLE
                    binding.cardRating.visibility = View.VISIBLE

                    Log.e("HomeFragment", "API call failed", t)
                    Toast.makeText(context, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun findTodaysMenu(menus: List<Menu>): Menu? {
        val today = Calendar.getInstance().time
        val apiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayFormatted = apiDateFormat.format(today)
        
        return menus.find { menu ->
            menu.date.startsWith(todayFormatted)
        }
    }

    private fun updateUIWithMenuData(menu: Menu) {
        val menuItems = menu.menu_items

        if (menuItems.isNotEmpty()) {
            binding.tvMealDate.text = "Öğle Yemeği"

            if (menuItems.size >= 1) binding.tvSoup.text = menuItems[0].name
            if (menuItems.size >= 2) binding.tvMainDish.text = menuItems[1].name
            if (menuItems.size >= 3) binding.tvSideDish.text = menuItems[2].name
            if (menuItems.size >= 4) binding.tvSalad.text = menuItems[3].name
            if (menuItems.size >= 5) binding.tvDessert.text = menuItems[4].name

            binding.tvCaloriesHeader.text = "${menu.total_calories} kcal"

            updateLikeCounters(effectiveLikes, effectiveDislikes)
        }
    }

    private fun showWeekendMessage() {
        binding.tvSoup.text = "Hafta sonu yemekhane çalışmamaktadır"
        binding.tvMainDish.text = ""
        binding.tvSideDish.text = ""
        binding.tvSalad.text = ""
        binding.tvDessert.text = ""

        binding.tvMealDate.text = "Kapalı"
        binding.tvCaloriesHeader.text = "0 kcal"

        binding.btnLike.isEnabled = false
        binding.btnDislike.isEnabled = false
    }

    private fun updateReactionButtons() {
        // Reset button styles
        resetButtonStyles()

        // Update button states based on user's reaction
        when (currentUserReaction) {
            "like" -> {
                setLikeButtonActive()
                binding.btnDislike.isEnabled = true
                binding.btnLike.isEnabled = true
            }

            "dislike" -> {
                setDislikeButtonActive()
                binding.btnDislike.isEnabled = true
                binding.btnLike.isEnabled = true
            }

            else -> {
                // No reaction
                binding.btnLike.isEnabled = true
                binding.btnDislike.isEnabled = true
            }
        }
    }

    private fun resetButtonStyles() {
        // Reset like button
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.gray_medium)
        binding.btnLike.backgroundTintList = ColorStateList.valueOf(defaultColor)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.metalic_gray)
        binding.btnLike.iconTint = ColorStateList.valueOf(defaultTextColor)
        binding.btnLike.setTextColor(defaultTextColor)

        // Reset dislike button
        binding.btnDislike.backgroundTintList = ColorStateList.valueOf(defaultColor)
        binding.btnDislike.iconTint = ColorStateList.valueOf(defaultTextColor)
        binding.btnDislike.setTextColor(defaultTextColor)
    }

    private fun setLikeButtonActive() {
        val activeButtonColor = ContextCompat.getColor(requireContext(), R.color.orange_base)
        binding.btnLike.backgroundTintList = ColorStateList.valueOf(activeButtonColor)
        val activeIconColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.btnLike.iconTint = ColorStateList.valueOf(activeIconColor)
        binding.btnLike.setTextColor(activeIconColor)
    }

    private fun setDislikeButtonActive() {
        val activeButtonColor = ContextCompat.getColor(requireContext(), R.color.orange_base)
        binding.btnDislike.backgroundTintList = ColorStateList.valueOf(activeButtonColor)
        val activeIconColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.btnDislike.iconTint = ColorStateList.valueOf(activeIconColor)
        binding.btnDislike.setTextColor(activeIconColor)
    }

    private fun setupRatingButtons() {
        binding.btnLike.setOnClickListener {
            if (currentMenuId == -1) return@setOnClickListener

            // Create request body
            val requestBody = mapOf("type" to "like")

            // Log the request for debugging
            Log.d(
                "HomeFragment",
                "Sending toggle reaction: menuId=$currentMenuId, body=$requestBody"
            )

            RetrofitClient.apiService.toggleReaction(currentMenuId, requestBody)
                .enqueue(object : Callback<ReactionResponse> {
                    override fun onResponse(
                        call: Call<ReactionResponse>,
                        response: Response<ReactionResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val reactionResponse = response.body()!!

                            if (reactionResponse.success) {
                                // Update local state
                                effectiveLikes = reactionResponse.data.effectiveLikes
                                effectiveDislikes = reactionResponse.data.effectiveDislikes
                                currentUserReaction = reactionResponse.data.effectiveUserReaction

                                // Update UI
                                updateLikeCounters(effectiveLikes, effectiveDislikes)
                                updateReactionButtons()

                                // Show message based on action
                                val message = if (reactionResponse.data.action == "created") {
                                    "Menüyü beğendiniz!"
                                } else {
                                    "Beğeni kaldırıldı"
                                }

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "İşlem gerçekleştirilemedi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Log error details for debugging
                            try {
                                val errorBody = response.errorBody()?.string()
                                Log.e("HomeFragment", "Server Error: $errorBody")
                                Toast.makeText(
                                    context,
                                    "Sunucu hatası: ${errorBody?.take(50)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Sunucu hatası: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReactionResponse>, t: Throwable) {
                        Log.e("HomeFragment", "Reaction API call failed", t)
                        Toast.makeText(context, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        binding.btnDislike.setOnClickListener {
            if (currentMenuId == -1) return@setOnClickListener

            // Create request body
            val requestBody = mapOf("type" to "dislike")

            // Log the request for debugging
            Log.d(
                "HomeFragment",
                "Sending toggle reaction: menuId=$currentMenuId, body=$requestBody"
            )

            RetrofitClient.apiService.toggleReaction(currentMenuId, requestBody)
                .enqueue(object : Callback<ReactionResponse> {
                    override fun onResponse(
                        call: Call<ReactionResponse>,
                        response: Response<ReactionResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val reactionResponse = response.body()!!

                            if (reactionResponse.success) {
                                // Update local state
                                effectiveLikes = reactionResponse.data.effectiveLikes
                                effectiveDislikes = reactionResponse.data.effectiveDislikes
                                currentUserReaction = reactionResponse.data.effectiveUserReaction

                                // Update UI
                                updateLikeCounters(effectiveLikes, effectiveDislikes)
                                updateReactionButtons()

                                // Show message based on action
                                val message = if (reactionResponse.data.action == "created") {
                                    "Menüyü beğenmediniz!"
                                } else {
                                    "Beğenmeme kaldırıldı"
                                }

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "İşlem gerçekleştirilemedi",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            // Log error details for debugging
                            try {
                                val errorBody = response.errorBody()?.string()
                                Log.e("HomeFragment", "Server Error: $errorBody")
                                Toast.makeText(
                                    context,
                                    "Sunucu hatası: ${errorBody?.take(50)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Sunucu hatası: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReactionResponse>, t: Throwable) {
                        Log.e("HomeFragment", "Reaction API call failed", t)
                        Toast.makeText(context, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        // Long click on progress bar to reset UI (just local reset, not sending to API)
        binding.customProgressBar.setOnLongClickListener {
            // Reset reaction state
            currentUserReaction = null
            resetButtonStyles()

            // Enable both buttons
            binding.btnLike.isEnabled = true
            binding.btnDislike.isEnabled = true

            Toast.makeText(context, "Butonlar sıfırlandı", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun updateLikeCounters(likes: Int, dislikes: Int) {
        binding.tvLikeCount.text = "$likes kişi beğendi • $dislikes kişi beğenmedi"

        val total = likes + dislikes
        if (total > 0) {
            val likePercentage = (likes * 100) / total
            val totalWidth = binding.progressBackground.width
            val progressWidth = (totalWidth * likePercentage) / 100

            val layoutParams = binding.progressRating.layoutParams
            layoutParams.width = progressWidth
            binding.progressRating.layoutParams = layoutParams
        } else {
            // If no reactions, set progress to 0
            val layoutParams = binding.progressRating.layoutParams
            layoutParams.width = 0
            binding.progressRating.layoutParams = layoutParams
        }
    }

    private fun showSkeleton() {
        skeletonBinding.shimmerLayout.startShimmer()
        skeletonBinding.shimmerLayout.visibility = View.VISIBLE
        binding.cardMenu.visibility = View.GONE
        binding.cardRating.visibility = View.GONE
    }

    private fun hideSkeleton(){
        skeletonBinding.shimmerLayout.stopShimmer()
        skeletonBinding.shimmerLayout.visibility = View.GONE
        binding.cardMenu.visibility = View.VISIBLE
        binding.cardRating.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        binding.customProgressBar.post {
            if (currentMenuId != -1) {
                updateLikeCounters(effectiveLikes, effectiveDislikes)
                updateReactionButtons()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _skeletonBinding = null
        _binding = null
    }
}