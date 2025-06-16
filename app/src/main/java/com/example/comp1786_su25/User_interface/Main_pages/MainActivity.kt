package com.example.comp1786_su25.User_interface.Main_pages

import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.comp1786_su25.R
import com.example.comp1786_su25.User_interface.Site_pages.class_page
import com.example.comp1786_su25.User_interface.Site_pages.home_page
import android.view.ContextThemeWrapper
import com.example.comp1786_su25.User_interface.Components.AddClassDialog
import com.example.comp1786_su25.User_interface.Components.AddTeacherDialog

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Initialize the FloatingActionButton
        fab = findViewById(R.id.fab)

        // Set FAB click listener
        fab.setOnClickListener { view ->
            showFabMenu(view)
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    replaceFragment(home_page())
                    true
                }
                R.id.cart -> {
                    replaceFragment(class_page())
                    true
                }
                R.id.search -> {

                    true
                }
                else -> false
            }
        }

        // Set default selection and load default fragment
        bottomNavigationView.selectedItemId = R.id.home
    }

    private fun showFabMenu(view: android.view.View) {
        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.fab_menu, popupMenu.menu)

        // Add animation for the popup
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        // Style the menu items to have centered text
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceShowIcon = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.java
                    )
                    setForceShowIcon.invoke(menuPopupHelper, true)

                    // Apply custom text appearance to menu items
                    val menuView = menuPopupHelper.javaClass.getDeclaredMethod("getMenuView").invoke(menuPopupHelper) as android.widget.ListView
                    menuView.divider = null
                    menuView.dividerHeight = 0
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_class -> {
                    try {
                        // Show the Add Class dialog instead of a Toast
                        val addClassDialog = AddClassDialog.newInstance()
                        addClassDialog.show(supportFragmentManager, AddClassDialog.TAG)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error showing dialog: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.add_teacher -> {
                    // Handle add teacher action
                    val addTeacherDialog = AddTeacherDialog.newInstance(null)
                    addTeacherDialog.show(supportFragmentManager, AddTeacherDialog.TAG)
                    true
                }
                R.id.add_student -> {
                    // Handle add student action
                    Toast.makeText(this, "Add Student Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.add_note -> {
                    // Handle add note action
                    Toast.makeText(this, "Add Note Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Show the popup menu
        popupMenu.show()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }
}