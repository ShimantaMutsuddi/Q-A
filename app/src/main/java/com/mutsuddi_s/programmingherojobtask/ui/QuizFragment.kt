package com.mutsuddi_s.programmingherojobtask.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.load.engine.Resource
import com.mutsuddi_s.mvvm.viewmodel.MainViewModel
import com.mutsuddi_s.programmingherojobtask.R
import com.mutsuddi_s.programmingherojobtask.databinding.FragmentQuizBinding
import com.mutsuddi_s.programmingherojobtask.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {



    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel:MainViewModel
    var isLoading=false
    private  val TAG = "QuizFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quiz.setOnClickListener {
            it.findNavController().navigate(R.id.action_quizFragment_to_homeFragment)
        }

        viewModel= (activity as MainActivity).viewModel!!

        viewModel.questions.observe(viewLifecycleOwner){ response ->
            when(response)
            {

                is Response.Success -> {
                    hideProgressBar()
                    //response.data?.let { newsResponse ->
                   // Toast.makeText(requireActivity(),response.data.toString(),Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, response.data.toString())
                   // Toast.makeText(this@r, "value- else", Toast.LENGTH_SHORT).show()
                    val questions = response.data
                    if (questions?.isNotEmpty()!!) {
                        viewModel.currentIndex.observe(viewLifecycleOwner, Observer { currentIndex ->
                           // displayQuestion(questions[currentIndex])
                            Toast.makeText(requireActivity(),questions[currentIndex].toString(),Toast.LENGTH_SHORT).show()
                            Log.d(TAG, questions[currentIndex].toString())
                        })
                    } else {
                        //finish() // No questions, end the quiz
                    }
                }

                is Response.Error -> {
                    hideProgressBar()
                }


                is Response.Loading ->{
                    showProgressBar()
                }

            }

        }



    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar() {
        _binding!!.progressBar?.visibility=View.INVISIBLE
        isLoading=false
    }
    private fun showProgressBar() {
        _binding!!.progressBar?.visibility=View.VISIBLE
        isLoading=true
    }


}