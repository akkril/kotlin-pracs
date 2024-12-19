package com.example.work6.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.work6.R
import com.example.work6.databinding.FragmentCatBinding
import com.example.work6.presentation.viewmodel.CatViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatFragment : Fragment() {
    private var _binding: FragmentCatBinding? = null
    private val binding get() = _binding!!
    private val catViewModel: CatViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCatBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        catViewModel.catImageUrl.observe(viewLifecycleOwner) { url ->
            if (url != null) {
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.gear_spinner)
                    .error(R.drawable.error)
                    .into(binding.catImageView)
            }
        }

        catViewModel.catImageUrl.observe(viewLifecycleOwner) { url ->
            if (url != null) {
                catViewModel.downloadAndSaveImage(url)
            }
        }

        catViewModel.saveResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Изображение успешно сохранено!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
            }
        }

        binding.catBtn.setOnClickListener {
            catViewModel.fetchCat()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}