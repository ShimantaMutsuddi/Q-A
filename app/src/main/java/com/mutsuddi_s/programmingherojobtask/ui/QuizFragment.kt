package com.mutsuddi_s.programmingherojobtask.ui

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.mutsuddi_s.mvvm.model.Question
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
     var totalQestion=0
    private lateinit var countDownTimer: CountDownTimer
    private var remainingTimeMillis: Long = 10000
    private val intervalInMillis: Long = 1000



    private  val TAG = "QuizFragment"
    private val handler = Handler()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        binding.timeProgressBar.max = (remainingTimeMillis / intervalInMillis).toInt()
        viewModel= (activity as MainActivity).viewModel!!


        viewModel.score.observe(viewLifecycleOwner){
            Log.d(TAG, "onViewCreated: $it")
            binding.score.text="Score: $it"
        }

        viewModel.checkNavigation.observe(viewLifecycleOwner){
            if(it)
            {
                findNavController().navigate(R.id.action_quizFragment_to_homeFragment)
                viewModel.setNavigationFalse()

            }
        }

        countDownTimer = object : CountDownTimer(remainingTimeMillis, intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {


                val safeBinding = _binding
                if (safeBinding != null) {

                    val progress = ((remainingTimeMillis - millisUntilFinished) / intervalInMillis).toInt()
                   // safeBinding.quiz.text=progress.toString()

                    safeBinding.tvProgress.text = "${millisUntilFinished / 1000} /10"
                    safeBinding.timeProgressBar.progress = progress
                }
            }


            override fun onFinish() {

                val safeBinding = _binding
                if (safeBinding != null) {
                    safeBinding.progressBar.progress = 0
                }
                viewModel.moveToNextQuestion()
            }
        }





       // setAnswerOptionClickListener()
        viewModel.questions.observe(viewLifecycleOwner){ response ->
            when(response)
            {

                is Response.Success -> {
                    hideProgressBar()
                    stopShimmer()
                    binding.shimmer.visibility=View.GONE

                    binding.mainLayout.visibility=View.VISIBLE

                    val questions = response.data
                    totalQestion= questions!!.size
                    Log.d(TAG, "totalQestion: "+totalQestion)

                    if (questions?.isNotEmpty()!!) {
                        viewModel.currentIndex.observe(viewLifecycleOwner, Observer { currentIndex ->


                                countDownTimer.start()
                                resetAnswerOptionStyles()
                                displayQuestion(questions[currentIndex],currentIndex)



                        })
                    } else {

                    }
                }

                is Response.Error -> {
                    hideProgressBar()
                    Snackbar.make(view, response.errorMessage.toString(), Snackbar.LENGTH_SHORT).show();

                }


                is Response.Loading ->{
                    showProgressBar()
                }

            }

        }






    }







    private fun displayQuestion(question: Question, currentIndex: Int) {

        binding.optionOne.visibility= View.VISIBLE
        binding.optionTwo.visibility= View.VISIBLE
        binding.optionThree.visibility= View.VISIBLE
        binding.optionFour.visibility= View.VISIBLE

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
            val answer = shuffledAnswers.getOrNull(i)
            val answerOption = answerOptions[i]
            answerOption.text = answer
            if (answer != null) {
                answerOption.text = answer
                answerOption.visibility = View.VISIBLE
            } else {
                answerOption.text = ""
                answerOption.visibility = View.INVISIBLE
            }
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


        val placeholderDrawable = R.drawable.logo
        Glide.with(requireContext())
            .load(question.questionImageUrl)
            .placeholder(placeholderDrawable)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.image ?: ImageView(context).apply { setImageResource(placeholderDrawable) })

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

   /* private fun selectRightOption(optionView: TextView) {
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
    }*/

    private fun resetAnswerOptionStyles() {
        val options = listOf( binding.optionOne,  binding.optionTwo,  binding.optionThree,  binding.optionFour)
        for (optionView in options) {
            optionView.setBackgroundResource(R.drawable.bg_card)
            optionView.setTextColor(Color.BLACK)
        }
    }

    private fun checkAnswer(question: Question, selectedAnswer: String, option: TextView) {

        countDownTimer.cancel()
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
            option.setBackgroundResource(R.drawable.correct_option_border_bg)
            viewModel.totalScore(question.score)


        } else {
            // Handle wrong answer

            option.setBackgroundResource(R.drawable.wrong_option_border_bg)
            showCorrectAnswer(answerMap[correctAnswer])
        }
        handler.postDelayed({ viewModel.moveToNextQuestion() }, 2000)

        binding.optionOne.isClickable = false
        binding.optionTwo.isClickable = false
        binding.optionThree.isClickable = false
        binding.optionFour.isClickable = false
    }

    private fun showCorrectAnswer(correctAnswer: String?) {
        when (correctAnswer) {

            binding.optionOne.text.toString() -> binding.optionOne.setBackgroundResource(R.drawable.correct_option_border_bg)
            binding.optionTwo.text.toString() -> binding.optionTwo.setBackgroundResource(R.drawable.correct_option_border_bg)
            binding.optionThree.text.toString() -> binding.optionThree.setBackgroundResource(R.drawable.correct_option_border_bg)
            binding.optionFour.text.toString() -> binding.optionFour.setBackgroundResource(R.drawable.correct_option_border_bg)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)

        startShimmer()



        return binding.root
    }

    private fun startShimmer() {
        val safeBinding = _binding
        safeBinding?.shimmer?.startShimmer() // Start shimmer effect
    }

    override fun onPause() {
        stopShimmer()
        super.onPause()
    }
    private fun stopShimmer() {
        val safeBinding = _binding
        safeBinding?.shimmer?.stopShimmer()


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