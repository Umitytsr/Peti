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
                when (checkedId) {
                    R.id.englishRadioButton -> mainViewModel.setLanguageString("en")
                    R.id.turkishRadioButton -> mainViewModel.setLanguageString("tr")
                    R.id.frenchRadioButton -> mainViewModel.setLanguageString("fr")
                    R.id.spanishRadioButton -> mainViewModel.setLanguageString("es")
                    else -> mainViewModel.setLanguageString("tr") // Varsayılan dil
                }
                mainViewModel.setLanguageId(checkedId)
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