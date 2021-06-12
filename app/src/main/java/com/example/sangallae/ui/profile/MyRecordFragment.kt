package com.example.sangallae.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sangallae.R
import com.example.sangallae.databinding.ActivityCourseDetailBinding
import com.example.sangallae.databinding.CourseDetailFragmentBinding
import com.example.sangallae.databinding.FragmentMyrecordBinding

class MyRecordFragment : Fragment() {
    private lateinit var recordViewModel: MyRecordViewModel
    private var _binding: FragmentMyrecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyrecordBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.myRecordToolbar.inflateMenu(R.menu.detail_course_menu)
        setHasOptionsMenu(true)
        return view
    }
}