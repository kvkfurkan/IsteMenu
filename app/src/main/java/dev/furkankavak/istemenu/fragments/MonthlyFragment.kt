package dev.furkankavak.istemenu.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.furkankavak.istemenu.adapters.MonthlyMenuAdapter
import dev.furkankavak.istemenu.databinding.FragmentMonthlyBinding
import dev.furkankavak.istemenu.model.ApiResponse
import dev.furkankavak.istemenu.model.Menu
import dev.furkankavak.istemenu.models.MenuDisplayItem
import dev.furkankavak.istemenu.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthlyFragment : Fragment() {

    private var _binding: FragmentMonthlyBinding? = null
    private val binding get() = _binding!!
    private lateinit var menuAdapter: MonthlyMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchMenuData()
    }

    private fun setupRecyclerView() {
        menuAdapter = MonthlyMenuAdapter()
        binding.rvMonthlyMenu.adapter = menuAdapter
    }

    private fun fetchMenuData() {
        RetrofitClient.apiService.getMenu().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!

                    if (apiResponse.success) {
                        val menus = apiResponse.data.dailyMenus
                        val displayItems = convertToDisplayItems(menus)
                        menuAdapter.updateData(displayItems)
                        Log.d(
                            "MonthlyFragment",
                            "Menu data fetched successfully: ${menus.size} items"
                        )
                    } else {
                        Log.e("MonthlyFragment", "API error: ${apiResponse.message}")
                        Toast.makeText(context, "Menü bilgileri bulunamadı", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.e("MonthlyFragment", "Error fetching menus: ${response.message()}")
                    Toast.makeText(context, "Menü bilgileri yüklenemedi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("MonthlyFragment", "API call failed", t)
                Toast.makeText(context, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertToDisplayItems(menus: List<Menu>): List<MenuDisplayItem> {
        val displayItems = mutableListOf<MenuDisplayItem>()

        for (menu in menus) {
            val menuItems = menu.menu_items
            if (menuItems.size >= 4) { // Ensure we have enough menu items
                displayItems.add(
                    MenuDisplayItem(
                        id = menu.id,
                        date = menu.date,
                        mainDish = menuItems[0].name,
                        sideDish1 = menuItems[1].name,
                        sideDish2 = if (menuItems.size > 2) menuItems[2].name else "",
                        dessert = if (menuItems.size > 3) menuItems[3].name else "",
                        calories = menu.total_calories,
                        likes = 0, // We could fetch these from a database in the future
                        dislikes = 0
                    )
                )
            }
        }

        return displayItems
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}