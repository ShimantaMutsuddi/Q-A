package com.mutsuddi_s.programmingherojobtask.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.mutsuddi_s.mvvm.viewmodel.MainViewModel
import com.mutsuddi_s.programmingherojobtask.R
import com.mutsuddi_s.programmingherojobtask.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel!!

        viewModel.highestScore.observe(viewLifecycleOwner){

            binding.score.text="${it.toString()} point"

        }



        binding.btnStart.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_quizFragment)
        }



    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}