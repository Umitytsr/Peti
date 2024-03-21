package com.umitytsr.peti.view.home.language

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.umitytsr.peti.R
import com.umitytsr.peti.databinding.FragmentLanguageBinding
import com.umitytsr.peti.view.authentication.mainActivity.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private val mainViewModel : MainActivityViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater,container,false)
        getData()
        return binding.root
    }

    private fun getData(){
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.isLanguageId.collectLatest {
                binding.languageRadioGroup.check(getRadioButtonIdForLanguage(it))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            arrowBackButton.setOnClickListener {
                findNavController().popBackStack()
            }

            languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val language = when (checkedId) {
                    R.id.englishRadioButton -> "en"
                    R.id.turkishRadioButton -> "tr"
                    R.id.frenchRadioButton -> "fr"
                    R.id.spanishRadioButton -> "es"
                    else -> "tr"
                }
                mainViewModel.setLanguageString(language)
            }
        }
    }

    private fun getRadioButtonIdForLanguage(languageId: Int): Int {
        return when (languageId) {
            1 -> R.id.englishRadioButton
            2 -> R.id.turkishRadioButton
            3 -> R.id.frenchRadioButton
            4 -> R.id.spanishRadioButton
            else -> R.id.turkishRadioButton
        }
    }
}