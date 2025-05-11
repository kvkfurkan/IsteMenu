package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.furkankavak.istemenu.databinding.FragmentHomeBinding
import dev.furkankavak.istemenu.databinding.LayoutSkeletonHomeBinding
import dev.furkankavak.istemenu.model.ApiResponse
import dev.furkankavak.istemenu.model.Menu
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

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
        val formattedDate = dateFormat.format(currentDate)

        binding.tvDate.text = formattedDate
        binding.tvSubtitle.text = "Bugünkü Menü"

        setupRatingButtons()
        fetchTodayMenu()
    }

    private fun fetchTodayMenu() {
        // Show skeleton loading
        showSkeleton()

        // Add a 3-second delay to be able to see the shimmer effect during development

            RetrofitClient.apiService.getMenu().enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {


                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()!!

                        if (apiResponse.success) {
                            hideSkeleton()

                            val menus = apiResponse.data.dailyMenus
                            val todayMenu = findTodaysMenu(menus)

                            if (todayMenu != null && todayMenu.menu_items.isNotEmpty()) {
                                updateUIWithMenuData(todayMenu)
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
                    // Hide skeleton loading
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
        
        val bugunMenu =  menus.find { menu ->
            menu.date.startsWith(todayFormatted)
        }
        return bugunMenu
    }

    private fun updateUIWithMenuData(menu: Menu) {
        val menuItems = menu.menu_items

        if (menuItems.isNotEmpty()) {
            // Update meal type
            binding.tvMealDate.text =
                "Öğle Yemeği" // This could be dynamic if API provided meal type

            // Update menu items based on the number of items available
            if (menuItems.size >= 1) binding.tvSoup.text = menuItems[0].name
            if (menuItems.size >= 2) binding.tvMainDish.text = menuItems[1].name
            if (menuItems.size >= 3) binding.tvSideDish.text = menuItems[2].name
            if (menuItems.size >= 4) binding.tvSalad.text = menuItems[3].name
            if (menuItems.size >= 5) binding.tvDessert.text = menuItems[4].name

            // Update calories
            binding.tvCaloriesHeader.text = "${menu.total_calories} kcal"

            // Update ratings
            updateLikeCounters(125, 43) // Hardcoded for now, could be from DB later
        }
    }

    private fun showWeekendMessage() {
        // Hide menu items
        binding.tvSoup.text = "Hafta sonu yemekhane çalışmamaktadır"
        binding.tvMainDish.text = ""
        binding.tvSideDish.text = ""
        binding.tvSalad.text = ""
        binding.tvDessert.text = ""

        // Update header
        binding.tvMealDate.text = "Kapalı"
        binding.tvCaloriesHeader.text = "0 kcal"

        // Disable rating buttons
        binding.btnLike.isEnabled = false
        binding.btnDislike.isEnabled = false
    }

    private fun setupRatingButtons() {
        var likeCount = 125
        var dislikeCount = 43

        binding.btnLike.setOnClickListener {
            likeCount++
            updateLikeCounters(likeCount, dislikeCount)

            binding.btnLike.isEnabled = false
            binding.btnDislike.isEnabled = true

            Toast.makeText(
                context,
                "Menüyü beğendiniz!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnDislike.setOnClickListener {
            dislikeCount++
            updateLikeCounters(likeCount, dislikeCount)

            binding.btnDislike.isEnabled = false
            binding.btnLike.isEnabled = true

            Toast.makeText(
                context,
                "Menüyü beğenmediniz!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.customProgressBar.setOnLongClickListener {
            likeCount = 125
            dislikeCount = 43
            updateLikeCounters(likeCount, dislikeCount)

            binding.btnLike.isEnabled = true
            binding.btnDislike.isEnabled = true

            Toast.makeText(context, "Beğeni değerleri sıfırlandı!", Toast.LENGTH_SHORT).show()
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
            updateLikeCounters(125, 43)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _skeletonBinding = null
        _binding = null
    }
}