package com.example.prac3.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.prac3.databinding.FragmentFirstBinding
import androidx.fragment.app.activityViewModels
import com.example.prac3.R
import com.example.prac3.data.repository.TimerRepository

// Фрагмент FirstFragment отображает таймер и предоставляет переход на другой фрагмент
class FirstFragment : Fragment() {

    // Инициализируем ViewModel таймера с использованием activityViewModels и фабрики ViewModel
    private val timerViewModel: TimerViewModel by activityViewModels { TimerViewModelFactory(TimerRepository()) }
    // Переменная для привязки элементов интерфейса
    private var _binding: FragmentFirstBinding? = null
    // Геттер для безопасного доступа к привязке
    private val binding get() = _binding!!

    // Метод для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки макета
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        // Наблюдаем за изменением времени и обновляем UI при изменении данных в ViewModel
        timerViewModel.timeLeft.observe(viewLifecycleOwner, Observer { timeLeft ->
            // Обновляем текстовое поле, отображающее оставшееся время (в секундах)
            binding.timeTextView.text = (timeLeft / 1000).toString()
        })

        // Возвращаем корневое представление
        return binding.root
    }

    // Метод, вызываемый при уничтожении представления фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Освобождаем привязку, чтобы предотвратить утечки памяти
    }

    // Метод, вызываемый, когда представление полностью создано
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем обработчик нажатия на кнопку для перехода на SecondFragment с помощью транзакции вручную
        binding.nextFragmentWithTransition.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // Заменяем текущий фрагмент на SecondFragment
            fragmentTransaction.replace(R.id.fragment_container, SecondFragment())
            // Добавляем транзакцию в стек возврата для возможности возврата
            fragmentTransaction.addToBackStack(null)
            // Подтверждаем транзакцию
            fragmentTransaction.commit()
        }

        // Устанавливаем обработчик нажатия на кнопку для перехода на SecondFragment с использованием Navigation API
        binding.nextFragmentWithNavigationApi.setOnClickListener {
            findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }
}
