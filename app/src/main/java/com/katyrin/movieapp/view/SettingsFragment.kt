package com.katyrin.movieapp.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.katyrin.movieapp.databinding.SettingsFragmentBinding
import com.katyrin.movieapp.model.IS_SHOW_ADULT_CONTENT
import com.katyrin.movieapp.model.MIN_YEAR_RESULT
import com.katyrin.movieapp.model.SETTINGS_SHARED_PREFERENCE
import com.katyrin.movieapp.model.VOTE_AVERAGE

class SettingsFragment: Fragment() {

    companion object {
        fun newInstance()= SettingsFragment()
    }

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSettingsSP()
        setVoteSeekBar()
        setYearSeekBar()
        initActions()
    }

    private fun initActions() {
        binding.averageSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setVoteSeekBar()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.yearSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setYearSeekBar()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.saveButton.setOnClickListener {
            saveSharedPreferences(binding.isAdult.isChecked,
                binding.averageSeekBar.progress, binding.yearSeekBar.progress)
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun setVoteSeekBar() {
        binding.countAverageTextView.text = binding.averageSeekBar.progress.toString()
    }

    private fun setYearSeekBar() {
        binding.countYearTextView.text = binding.yearSeekBar.progress.toString()
    }

    private fun saveSharedPreferences(isDataShowAdult: Boolean, voteAverage: Int, minYear: Int) {
        activity?.let {
            with(it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_SHOW_ADULT_CONTENT, isDataShowAdult).apply()
                putInt(VOTE_AVERAGE, voteAverage).apply()
                putInt(MIN_YEAR_RESULT, minYear).apply()
            }
        }
    }

    private fun setSettingsSP() {
        activity?.let {
            binding.isAdult.isChecked =
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                .getBoolean(IS_SHOW_ADULT_CONTENT, false)
            binding.averageSeekBar.progress =
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getInt(VOTE_AVERAGE, 0)
            binding.yearSeekBar.progress =
                it.getSharedPreferences(SETTINGS_SHARED_PREFERENCE, Context.MODE_PRIVATE)
                    .getInt(MIN_YEAR_RESULT, 1895)
        }
    }
}