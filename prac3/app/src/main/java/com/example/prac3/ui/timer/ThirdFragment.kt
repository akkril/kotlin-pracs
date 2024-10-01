package com.example.prac3.ui.timer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.prac3.R
import com.example.prac3.databinding.FragmentThirdBinding
import com.example.prac3.data.repository.TimerRepository

// Фрагмент ThirdFragment управляет остановкой таймера и предоставляет переход к FirstFragment
class ThirdFragment : Fragment() {

    // Инициализируем ViewModel таймера с использованием фабрики и activityViewModels
    private val timerViewModel: TimerViewModel by activityViewModels { TimerViewModelFactory(TimerRepository()) }
    // Переменная для привязки элементов интерфейса
    private var _binding: FragmentThirdBinding? = null
    // Геттер для безопасного доступа к привязке
    private val binding get() = _binding!!

    // Метод для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки макета
        _binding = FragmentThirdBinding.inflate(inflater, container, false)

        // Наблюдаем за состоянием таймера (запущен или нет) и обновляем кнопку остановки
        timerViewModel.isRunning.observe(viewLifecycleOwner, Observer { isRunning ->
            // Если таймер запущен, активируем кнопку остановки
            binding.stopButton.isEnabled = isRunning
        })

        // Устанавливаем обработчик для кнопки остановки таймера
        binding.stopButton.setOnClickListener {
            // Останавливаем таймер через ViewModel
            timerViewModel.stopTimer()
        }

        // Возвращаем корневое представление
        return binding.root
    }

    // Метод, вызываемый, когда представление полностью создано
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем обработчик нажатия для перехода на FirstFragment через ручную транзакцию
        binding.nextFragmentWithTransition.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // Заменяем текущий фрагмент на FirstFragment
            fragmentTransaction.replace(R.id.fragment_container, FirstFragment())
            // Добавляем транзакцию в стек возврата для возможности возврата назад
            fragmentTransaction.addToBackStack(null)
            // Подтверждаем транзакцию
            fragmentTransaction.commit()
        }

        // Устанавливаем обработчик нажатия для перехода на FirstFragment с использованием Navigation API
        binding.nextFragmentWithNavigationApi.setOnClickListener {
            findNavController().navigate(R.id.action_thirdFragment_to_firstFragment)
        }
    }

    // Метод, вызываемый при уничтожении представления фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Освобождаем привязку, чтобы предотвратить утечки памяти
    }
}
