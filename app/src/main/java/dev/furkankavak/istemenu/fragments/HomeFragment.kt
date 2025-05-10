package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.furkankavak.istemenu.R
import dev.furkankavak.istemenu.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
        val formattedDate = dateFormat.format(currentDate)

        binding.tvDate.text = formattedDate

        setupMenuData()

        setupRatingButtons()

        setupBottomNavigation()
    }

    private fun setupMenuData() {
        binding.tvMealDate.text = "Öğle Yemeği"
        binding.tvMainDish.text = "Etli Nohut"
        binding.tvSideDish.text = "Pirinç Pilavı"
        binding.tvSoup.text = "Mercimek Çorbası"
        binding.tvSalad.text = "Mevsim Salatası"
        binding.tvDessert.text = "Sütlaç"

        updateLikeCounters(125, 43)
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

    private fun setupBottomNavigation() {
        binding.bottomNavigation.menu.findItem(R.id.navigation_daily).isChecked = true

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_daily -> true
                R.id.navigation_weekly -> {
                    findNavController().navigate(R.id.weeklyFragment)
                    true
                }

                R.id.navigation_profile -> {
                    findNavController().navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.customProgressBar.post {
            updateLikeCounters(125, 43)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}