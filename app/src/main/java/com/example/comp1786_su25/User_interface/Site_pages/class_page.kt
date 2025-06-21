package com.example.comp1786_su25.User_interface.Site_pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.comp1786_su25.R
import com.example.comp1786_su25.Models.classModel
import com.example.comp1786_su25.MVC.insertClass
import com.example.comp1786_su25.User_interface.Adapters.ClassAdapter
import com.example.comp1786_su25.User_interface.Components.AddClassDialog
import com.google.android.material.chip.Chip
import java.sql.Date
import java.util.Calendar

class class_page : Fragment(), AddClassDialog.DataRefreshListener {

    // UI components
    private lateinit var searchEditText: EditText
    private lateinit var todaysClassesRecyclerView: RecyclerView
    private lateinit var upcomingClassesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var emptyStateView: LinearLayout
    private lateinit var noTodaysClassesTextView: TextView
    private lateinit var noUpcomingClassesTextView: TextView

    // Filter chips
    private lateinit var filterTeacherChip: Chip
    private lateinit var filterAvailabilityChip: Chip
    private lateinit var filterDateChip: Chip
    private lateinit var filterPriceChip: Chip
    private lateinit var filterDifficultyChip: Chip

    // Data
    private var allClasses = listOf<classModel>()
    private var todaysClasses = listOf<classModel>()
    private var tomorrowClasses = listOf<classModel>()
    private var upcomingClasses = listOf<classModel>()

    // Database helper
    private lateinit var dbHelper: insertClass

    // Adapters
    private lateinit var todaysClassesAdapter: ClassAdapter
    private lateinit var upcomingClassesAdapter: ClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_class_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database helper
        dbHelper = insertClass(requireContext())

        // Initialize UI components
        initViews(view)

        // Set up RecyclerViews
        setupRecyclerViews()

        // Set up search and filters
        setupSearchAndFilters()

        // Load data from database
        loadClassesFromDatabase()

        // Pull to refresh setup
        swipeRefreshLayout.setOnRefreshListener {
            loadClassesFromDatabase()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    // Implementation of DataRefreshListener interface
    override fun onDataChanged() {
        // Reload data when a new class is added
        loadClassesFromDatabase()
    }

    private fun initViews(view: View) {
        // Search and filters
        searchEditText = view.findViewById(R.id.searchEditText)
        filterTeacherChip = view.findViewById(R.id.filterTeacherChip)
        filterAvailabilityChip = view.findViewById(R.id.filterAvailabilityChip)
        filterDateChip = view.findViewById(R.id.filterDateChip)
        filterPriceChip = view.findViewById(R.id.filterPriceChip)
        filterDifficultyChip = view.findViewById(R.id.filterDifficultyChip)

        // RecyclerViews
        todaysClassesRecyclerView = view.findViewById(R.id.todaysClassesRecyclerView)
        upcomingClassesRecyclerView = view.findViewById(R.id.upcomingClassesRecyclerView)

        // Other UI components
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        emptyStateView = view.findViewById(R.id.emptyStateView)
        noTodaysClassesTextView = view.findViewById(R.id.noTodaysClassesTextView)
        noUpcomingClassesTextView = view.findViewById(R.id.noUpcomingClassesTextView)
    }

    private fun setupRecyclerViews() {
        // Setup for Today's Classes
        todaysClassesRecyclerView.layoutManager = LinearLayoutManager(context)
        todaysClassesAdapter = ClassAdapter(emptyList())
        todaysClassesRecyclerView.adapter = todaysClassesAdapter

        // Setup for Upcoming Classes
        upcomingClassesRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingClassesAdapter = ClassAdapter(emptyList())
        upcomingClassesRecyclerView.adapter = upcomingClassesAdapter
    }

    private fun setupSearchAndFilters() {
        // Search functionality
        searchEditText.addTextChangedListener { editable ->
            val searchText = editable.toString().lowercase()
            filterClasses(searchText)
        }

        // Filter chip click listeners
        filterTeacherChip.setOnClickListener {
            // Implement teacher filter dialog or dropdown
            // For now, just toggle the chip
            filterTeacherChip.isChecked = !filterTeacherChip.isChecked
            filterClasses(searchEditText.text.toString())
        }

        filterAvailabilityChip.setOnClickListener {
            // Implement availability filter
            filterAvailabilityChip.isChecked = !filterAvailabilityChip.isChecked
            filterClasses(searchEditText.text.toString())
        }

        filterDateChip.setOnClickListener {
            // Implement date filter
            filterDateChip.isChecked = !filterDateChip.isChecked
            filterClasses(searchEditText.text.toString())
        }

        filterPriceChip.setOnClickListener {
            // Implement price filter
            filterPriceChip.isChecked = !filterPriceChip.isChecked
            filterClasses(searchEditText.text.toString())
        }

        filterDifficultyChip.setOnClickListener {
            // Implement difficulty filter
            filterDifficultyChip.isChecked = !filterDifficultyChip.isChecked
            filterClasses(searchEditText.text.toString())
        }
    }

    private fun filterClasses(searchText: String) {
        // Apply filters based on search text and selected chips
        val filteredTodaysClasses = todaysClasses.filter { classItem ->
            classItem.class_type.lowercase().contains(searchText) ||
            classItem.description.lowercase().contains(searchText)
        }

        val filteredTomorrowClasses = tomorrowClasses.filter { classItem ->
            classItem.class_type.lowercase().contains(searchText) ||
            classItem.description.lowercase().contains(searchText)
        }

        val filteredUpcomingClasses = upcomingClasses.filter { classItem ->
            classItem.class_type.lowercase().contains(searchText) ||
            classItem.description.lowercase().contains(searchText)
        }

        // Update RecyclerViews with filtered data
        updateRecyclerViews(
            filteredTodaysClasses,
            filteredTomorrowClasses,
            filteredUpcomingClasses
        )
    }

    private fun updateRecyclerViews(
        todaysClasses: List<classModel>,
        tomorrowClasses: List<classModel>,
        upcomingClasses: List<classModel>
    ) {
        // Update adapters with new data
        todaysClassesAdapter = ClassAdapter(todaysClasses)
        todaysClassesRecyclerView.adapter = todaysClassesAdapter

        upcomingClassesAdapter = ClassAdapter(upcomingClasses)
        upcomingClassesRecyclerView.adapter = upcomingClassesAdapter

        // Show/hide empty state messages
        noTodaysClassesTextView.visibility = if (todaysClasses.isEmpty()) View.VISIBLE else View.GONE
        noUpcomingClassesTextView.visibility = if (upcomingClasses.isEmpty()) View.VISIBLE else View.GONE

        // Show empty state view if all lists are empty
        emptyStateView.visibility = if (
            todaysClasses.isEmpty() &&
            tomorrowClasses.isEmpty() &&
            upcomingClasses.isEmpty()
        ) View.VISIBLE else View.GONE
    }

    private fun loadClassesFromDatabase() {
        // Show loading indicator
        loadingProgressBar.visibility = View.VISIBLE

        // Get classes from database
        allClasses = dbHelper.getAllClasses()

        if (allClasses.isEmpty()) {
            // If database is empty, add some mock data for demonstration
            insertMockDataIfEmpty()
            allClasses = dbHelper.getAllClasses()
        }

        // Categorize classes by date
        categorizeClasses()

        // Update RecyclerViews
        updateRecyclerViews(todaysClasses, tomorrowClasses, upcomingClasses)

        // Hide loading indicator
        loadingProgressBar.visibility = View.GONE
    }

    private fun categorizeClasses() {
        val today = Calendar.getInstance()
        // Reset time component for today
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)
        // Reset time component for tomorrow
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)

        // Filter for today's classes
        todaysClasses = allClasses.filter { classItem ->
            val classDate = Calendar.getInstance()
            classDate.time = classItem.day_of_week
            // Reset time component for comparison
            classDate.set(Calendar.HOUR_OF_DAY, 0)
            classDate.set(Calendar.MINUTE, 0)
            classDate.set(Calendar.SECOND, 0)
            classDate.set(Calendar.MILLISECOND, 0)

            classDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            classDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }

        // Filter for tomorrow's classes
        tomorrowClasses = allClasses.filter { classItem ->
            val classDate = Calendar.getInstance()
            classDate.time = classItem.day_of_week
            // Reset time component for comparison
            classDate.set(Calendar.HOUR_OF_DAY, 0)
            classDate.set(Calendar.MINUTE, 0)
            classDate.set(Calendar.SECOND, 0)
            classDate.set(Calendar.MILLISECOND, 0)

            classDate.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
            classDate.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)
        }

        // Filter for upcoming classes (after tomorrow)
        upcomingClasses = allClasses.filter { classItem ->
            val classDate = Calendar.getInstance()
            classDate.time = classItem.day_of_week
            // Reset time component for comparison
            classDate.set(Calendar.HOUR_OF_DAY, 0)
            classDate.set(Calendar.MINUTE, 0)
            classDate.set(Calendar.SECOND, 0)
            classDate.set(Calendar.MILLISECOND, 0)

            // A class is "upcoming" if it's after tomorrow
            classDate.after(tomorrow)
        }
    }

    private fun insertMockDataIfEmpty() {
        // Create and insert mock data
        val today = Date(System.currentTimeMillis())

        // Create calendar for tomorrow
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = Date(calendar.timeInMillis)

        // Create calendar for next week
        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val nextWeek = Date(calendar.timeInMillis)

        // Add classes for demonstration
        val mockClasses = listOf(
            // Today's classes
            classModel(
                day_of_week = today,
                time_of_course = 1000, // 10:00 AM
                capacity = 15,
                duration = 90,
                price = 25.0,
                class_type = "Flow Yoga",
                description = "A gentle introduction to yoga poses and breathing techniques."
            ),
            classModel(
                day_of_week = today,
                time_of_course = 1400, // 2:00 PM
                capacity = 20,
                duration = 60,
                price = 30.0,
                class_type = "Aerial Yoga",
                description = "High-intensity aerial yoga class for experienced practitioners."
            ),

            // Tomorrow's classes
            classModel(
                day_of_week = tomorrow,
                time_of_course = 900, // 9:00 AM
                capacity = 12,
                duration = 75,
                price = 28.0,
                class_type = "Family Yoga",
                description = "Yoga class designed for families to practice together."
            ),
            classModel(
                day_of_week = tomorrow,
                time_of_course = 1800, // 6:00 PM
                capacity = 25,
                duration = 45,
                price = 20.0,
                class_type = "Flow Yoga",
                description = "Learn techniques to reduce stress and improve mental clarity."
            ),

            // Next week's class
            classModel(
                day_of_week = nextWeek,
                time_of_course = 1700, // 5:00 PM
                capacity = 18,
                duration = 45,
                price = 35.0,
                class_type = "Aerial Yoga",
                description = "Advanced aerial yoga techniques to challenge your strength and flexibility."
            )
        )

        // Insert each mock class into the database
        for (mockClass in mockClasses) {
            dbHelper.insertClass(mockClass)
        }
    }
}