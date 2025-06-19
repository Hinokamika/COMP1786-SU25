package com.example.comp1786_su25.User_interface.Site_pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.comp1786_su25.MVC.teacherDatabase
import com.example.comp1786_su25.Models.teacherModel
import com.example.comp1786_su25.R
import com.example.comp1786_su25.User_interface.Adapters.TeacherAdapter
import com.example.comp1786_su25.User_interface.Components.AddTeacherDialog
import java.util.Locale

class teacher_page : Fragment(), AddTeacherDialog.DataRefreshListener {

    private lateinit var searchEditText: EditText
    private lateinit var yogaTeachersRecyclerView: RecyclerView
    private lateinit var pilatesTeachersRecyclerView: RecyclerView
    private lateinit var otherTeachersRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var emptyStateView: LinearLayout

    private var allTeachers: List<teacherModel> = listOf()
    private var flowyogaTeachers: List<teacherModel> = listOf()
    private var aerialYogaTeachers: List<teacherModel> = listOf()
    private var familyYogaTeachers: List<teacherModel> = listOf()

    private lateinit var dbHelper: teacherDatabase

    private lateinit var FlowyogaTeachersAdapter: TeacherAdapter
    private lateinit var AerialYogaTeachersAdapter: TeacherAdapter
    private lateinit var FamilyYogaTeachersAdapter: TeacherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_teacher_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = teacherDatabase(requireContext())

        initViews(view)

        setupRecyclerViews()

        setupSearchAndFilters()

        loadTeachersFromDatabase()

    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.searchEditText)
        yogaTeachersRecyclerView = view.findViewById(R.id.yogaTeachersRecyclerView)
        pilatesTeachersRecyclerView = view.findViewById(R.id.pilatesTeachersRecyclerView)
        otherTeachersRecyclerView = view.findViewById(R.id.otherTeachersRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        emptyStateView = view.findViewById(R.id.emptyStateView)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        emptyStateView = view.findViewById(R.id.emptyStateView)
    }

    private fun setupRecyclerViews() {
        yogaTeachersRecyclerView.layoutManager = LinearLayoutManager(context)
        pilatesTeachersRecyclerView.layoutManager = LinearLayoutManager(context)
        otherTeachersRecyclerView.layoutManager = LinearLayoutManager(context)

        FlowyogaTeachersAdapter = TeacherAdapter(flowyogaTeachers)
        AerialYogaTeachersAdapter = TeacherAdapter(aerialYogaTeachers)
        FamilyYogaTeachersAdapter = TeacherAdapter(familyYogaTeachers)

        yogaTeachersRecyclerView.adapter = FlowyogaTeachersAdapter
        pilatesTeachersRecyclerView.adapter = AerialYogaTeachersAdapter
        otherTeachersRecyclerView.adapter = FamilyYogaTeachersAdapter
    }

    private fun setupSearchAndFilters() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase(Locale.getDefault()) ?: ""
                filterTeachers(query.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        swipeRefreshLayout.setOnRefreshListener {
            loadTeachersFromDatabase()
        }
    }

    private fun filterTeachers(query: String) {
        val filteredFlowyogaTeachers = flowyogaTeachers.filter { teacher ->
            teacher.name.lowercase(Locale.getDefault()).contains(query) ||
                (teacher.specializations.joinToString(", ").lowercase(Locale.getDefault()).contains(query)
                    ?: false)
        }

        val filteredAerialYogaTeachers = aerialYogaTeachers.filter { teacher ->
            teacher.name.lowercase(Locale.getDefault()).contains(query) ||
                (teacher.specializations.joinToString(", ").lowercase(Locale.getDefault()).contains(query)
                    ?: false)
        }

        val filteredFamilyYogaTeachers = familyYogaTeachers.filter { teacher ->
            teacher.name.lowercase(Locale.getDefault()).contains(query) ||
                (teacher.specializations.joinToString(", ").lowercase(Locale.getDefault()).contains(query)
                    ?: false)
        }

        updateRecyclerViews(
            filteredFlowyogaTeachers,
            filteredAerialYogaTeachers,
            filteredFamilyYogaTeachers
        )
    }

    private fun updateRecyclerViews(
        flowyogaTeachers: List<teacherModel>,
        aerialYogaTeachers: List<teacherModel>,
        familyYogaTeachers: List<teacherModel>
    ) {
        FlowyogaTeachersAdapter.updateData(flowyogaTeachers)
        AerialYogaTeachersAdapter.updateData(aerialYogaTeachers)
        FamilyYogaTeachersAdapter.updateData(familyYogaTeachers)

        yogaTeachersRecyclerView.visibility = if (flowyogaTeachers.isNotEmpty()) View.VISIBLE else View.GONE
        pilatesTeachersRecyclerView.visibility = if (aerialYogaTeachers.isNotEmpty()) View.VISIBLE else View.GONE
        otherTeachersRecyclerView.visibility = if (familyYogaTeachers.isNotEmpty()) View.VISIBLE else View.GONE

        emptyStateView.visibility = if (flowyogaTeachers.isEmpty() && aerialYogaTeachers.isEmpty() && familyYogaTeachers.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun loadTeachersFromDatabase() {
        loadingProgressBar.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE

        allTeachers = dbHelper.getAllTeachers()

        flowyogaTeachers = allTeachers.filter { it.classType == "Flow Yoga" }
        aerialYogaTeachers = allTeachers.filter { it.classType == "Aerial Yoga" }
        familyYogaTeachers = allTeachers.filter { it.classType == "Family Yoga" }

        updateRecyclerViews(flowyogaTeachers, aerialYogaTeachers, familyYogaTeachers)

        loadingProgressBar.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
    }


    override fun onDataChanged() {
        loadTeachersFromDatabase()
    }
}
