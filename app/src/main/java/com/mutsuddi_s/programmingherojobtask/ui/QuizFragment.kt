package com.mutsuddi_s.programmingherojobtask.ui

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.mutsuddi_s.mvvm.model.Question
import com.mutsuddi_s.mvvm.viewmodel.MainViewModel
import com.mutsuddi_s.programmingherojobtask.databinding.FragmentQuizBinding
import com.mutsuddi_s.programmingherojobtask.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {



    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel:MainViewModel
    var isLoading=false
     var totalQestion=0
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long =  10000
    private val countdownInterval: Long = 1000
    private  val TAG = "QuizFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quiz.setOnClickListener {
            //it.findNavController().navigate(R.id.action_quizFragment_to_homeFragment)
            viewModel.moveToNextQuestion()
        }

        viewModel= (activity as MainActivity).viewModel!!

        viewModel.score.observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: $it")
            binding.score.text="Score: $it"
        }

       // setAnswerOptionClickListener()
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
                    totalQestion= questions!!.size

                    if (questions?.isNotEmpty()!!) {
                        viewModel.currentIndex.observe(viewLifecycleOwner, Observer { currentIndex ->
                            startCountdown()
                            resetAnswerOptionStyles()
                            displayQuestion(questions[currentIndex],currentIndex)
                           // Toast.makeText(requireActivity(),questions[currentIndex].toString(),Toast.LENGTH_SHORT).show()
                            //Log.d(TAG, questions[currentIndex].toString())

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

    private fun startCountdown() {
        timeLeftInMillis =  10000
        countDownTimer = object : CountDownTimer(timeLeftInMillis, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownUI()
            }

            override fun onFinish() {
                // Countdown finished
                timeLeftInMillis = 0
                viewModel.moveToNextQuestion()
                //updateCountdownUI()
            }
        }.start()
    }

    private fun updateCountdownUI() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60



       // countdownTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun displayQuestion(question: Question, currentIndex: Int) {

        val shuffledAnswers = listOf(
            question.answers.A,
            question.answers.B,
            question.answers.C,
            question.answers.D
        ).shuffled()
        val answerOptions = listOf(
            binding.optionOne,
            binding.optionTwo,
            binding.optionThree,
            binding.optionFour
        )

        for (i in 0 until shuffledAnswers.size) {
            val answer = shuffledAnswers[i]
            val answerOption = answerOptions[i]
            answerOption.text = answer

           /* Log.d(TAG, "displayQuestion: $answer")
            if (answer.isNullOrEmpty()) {
                answerOption.visibility = View.GONE
            } else {
                answerOption.visibility = View.VISIBLE
                answerOption.text = answer
            }*/
        }

        binding.question.text = question.question
        binding.txtPoint.text = question.score.toString()


        /*binding.optionOne.text = question.answers.A
        binding.optionTwo.text = question.answers.B
        binding.optionThree.text = question.answers.C
        binding.optionFour.text = question.answers.D*/
        //binding.optionOne.text = shuffledAnswers.getOrNull(0)
       /* binding.optionOne.apply {
            if (shuffledAnswers.getOrNull(0).isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = shuffledAnswers[0]
            }
        }

        binding.optionTwo.apply {
            if (shuffledAnswers.getOrNull(1).isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = shuffledAnswers[1]
            }
        }
        binding.optionThree.apply {
            if (shuffledAnswers.getOrNull(2).isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = shuffledAnswers[2]
            }
        }
        binding.optionFour.apply {
            if (shuffledAnswers.getOrNull(3).isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = shuffledAnswers[3]
            }
        }*/
        //binding.optionTwo.text = shuffledAnswers[1]
       // binding.optionThree.text = shuffledAnswers[2]
       // binding.optionFour.text = shuffledAnswers[3]
       // binding.txtPoint.text = question.score.toString()
        binding.questionNumber.text="Question: ${currentIndex+1}/$totalQestion"
        Glide.with(binding.image.context).load( question.questionImageUrl).into(binding.image)

        binding.optionOne.setOnClickListener { checkAnswer(question, question.answers.A,binding.optionOne) }
        binding.optionTwo.setOnClickListener { checkAnswer(
            question,
            question.answers.B,
            binding.optionTwo
        ) }
        binding.optionThree.setOnClickListener { checkAnswer(
            question,
            question.answers.C,
            binding.optionThree
        ) }
        binding.optionFour.setOnClickListener { checkAnswer(
            question,
            question.answers.D,
            binding.optionFour
        ) }

    }

    /*private fun setAnswerOptionClickListener() {
        // Set click listeners to answer options
        binding.optionOne.setOnClickListener { selectOption(binding.optionOne) }
        binding.optionTwo.setOnClickListener { selectOption(binding.optionTwo) }
        binding.optionThree.setOnClickListener { selectOption(binding.optionThree) }
        binding.optionFour.setOnClickListener { selectOption(binding.optionFour) }
    }*/

    private fun selectRightOption(optionView: TextView) {
        // Reset background colors and text colors for all options
        resetAnswerOptionStyles()

        // Highlight the selected option
        optionView.setBackgroundColor(Color.parseColor("#FF03DAC5")) // Change color as needed
        optionView.setTextColor(Color.WHITE)
    }
    private fun selectWrongOption(optionView: TextView) {
        // Reset background colors and text colors for all options
        resetAnswerOptionStyles()

        // Highlight the selected option
        optionView.setBackgroundColor(Color.parseColor("#FFC107")) // Change color as needed
        optionView.setTextColor(Color.WHITE)
    }

    private fun resetAnswerOptionStyles() {
        val options = listOf( binding.optionOne,  binding.optionTwo,  binding.optionThree,  binding.optionFour)
        for (optionView in options) {
            optionView.setBackgroundColor(Color.WHITE)
            optionView.setTextColor(Color.BLACK)
        }
    }

    private fun checkAnswer(question: Question, selectedAnswer: String, option: TextView) {
        val correctAnswer = question.correctAnswer
        val answerMap = mapOf(
            "A" to question.answers.A,
            "B" to question.answers.B,
            "C" to question.answers.C,
            "D" to question.answers.D
        )


        Log.d(TAG, "checkAnswer: $correctAnswer")
        Log.d(TAG, "selectedAnswer: $selectedAnswer")
        //Log.d(TAG, "question.correctAnswer: ${question.answers.selectedAnswer}")
        if (answerMap[correctAnswer] == selectedAnswer) {
            // Handle correct answer
           // Toast.makeText(requireActivity(),"Right answer",Toast.LENGTH_SHORT).show()
            //Log.d(TAG, "checkAnswer: ${question.score}")
            selectRightOption(option)
            viewModel.totalScore(question.score)

        } else {
            // Handle wrong answer
           // Toast.makeText(requireActivity(),"Wrong answer",Toast.LENGTH_SHORT).show()
          //  selectWrongOption(option)
          //  selectRightOption(option)
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