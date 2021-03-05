package com.katyrin.movieapp.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.katyrin.movieapp.*
import com.katyrin.movieapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: MainActivityBinding

    private var navPosition: BottomNavigationPosition = BottomNavigationPosition.MOVIES
    private val myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Received event, value: $activeNetwork",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(myReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        initBottomNavigation(savedInstanceState)

        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.textInputEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                supportFragmentManager.apply {
                    popBackStack()
                    beginTransaction()
                            .add(
                                R.id.container, SearchMoviesFragment
                                    .newInstance(binding.textInputEditText.text.toString())
                            )
                            .addToBackStack(null)
                            .commit()
                }

                inputManager.hideSoftInputFromWindow(
                    currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )

                binding.textInputEditText.clearFocus()
            }
            return@setOnEditorActionListener true
        }

        binding.textInputLayout.setEndIconOnClickListener {
            binding.textInputEditText.setText("")
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navPosition = findNavigationPositionById(item.itemId)
        return if (navPosition == BottomNavigationPosition.MAP) {
            checkPermission(navPosition)
        } else {
            switchFragment(navPosition)
        }
    }

    private fun initBottomNavigation(savedInstanceState: Bundle?) {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.movies
        }
    }

    private fun switchFragment(navPosition: BottomNavigationPosition): Boolean {
        val fragment = supportFragmentManager.findFragment(navPosition)
        if (fragment.isAdded) return false
        removeAllStackFragments()
        detachFragment()
        attachFragment(fragment, navPosition.getTag())
        supportFragmentManager.executePendingTransactions()
        return true
    }

    private fun removeAllStackFragments() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is BottomNavigationView) {
                continue
            } else if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }

    private fun FragmentManager.findFragment(position: BottomNavigationPosition): Fragment {
        return findFragmentByTag(position.getTag()) ?: position.createFragment()
    }

    private fun detachFragment() {
        supportFragmentManager.findFragmentById(R.id.container)?.also {
            supportFragmentManager.beginTransaction().detach(it).commit()
        }
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        if (fragment.isDetached) {
            supportFragmentManager.beginTransaction().attach(fragment).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
        }
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.settings -> {
                supportFragmentManager.apply {
                    popBackStack()
                    beginTransaction()
                        .add(R.id.container, SettingsFragment.newInstance())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                true
            }
            R.id.contacts -> {
                supportFragmentManager.apply {
                    popBackStack()
                    beginTransaction()
                        .add(R.id.container, ContentProviderFragment.newInstance())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    private fun checkPermission(navPosition: BottomNavigationPosition): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ->
                switchFragment(navPosition)
            else -> {
                requestPermissions()
                false
            }
        }
    }
}