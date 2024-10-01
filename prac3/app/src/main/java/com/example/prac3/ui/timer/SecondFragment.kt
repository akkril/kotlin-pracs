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
import com.example.prac3.databinding.FragmentSecondBinding
import com.example.prac3.data.repository.TimerRepository

// Фрагмент SecondFragment управляет запуском таймера и предоставляет переход к ThirdFragment
class SecondFragment : Fragment() {

    // Инициализируем ViewModel таймера с использованием фабрики и activityViewModels
    private val timerViewModel: TimerViewModel by activityViewModels { TimerViewModelFactory(TimerRepository()) }
    // Переменная для привязки элементов интерфейса
    private var _binding: FragmentSecondBinding? = null
    // Геттер для безопасного доступа к привязке
    private val binding get() = _binding!!

    // Метод для создания представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация привязки макета
        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        // Наблюдаем за состоянием таймера (запущен или нет) и обновляем кнопку старта
        timerViewModel.isRunning.observe(viewLifecycleOwner, Observer { isRunning ->
            // Если таймер запущен, блокируем кнопку старта
            binding.startButton.isEnabled = !isRunning
        })

        // Устанавливаем обработчик для кнопки старта таймера
        binding.startButton.setOnClickListener {
            // Получаем значение времени, введенное пользователем
            val time = binding.timeInputEditText.text.toString().toLongOrNull() ?: 0L
            // Если введенное время больше 0, запускаем таймер
            if (time > 0) {
                timerViewModel.startTimer(time * 1000) // Преобразуем секунды в миллисекунды
            }
        }

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

        // Устанавливаем обработчик нажатия для перехода на ThirdFragment через ручную транзакцию
        binding.nextFragmentWithTransition.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            // Заменяем текущий фрагмент на ThirdFragment
            fragmentTransaction.replace(R.id.fragment_container, ThirdFragment())
            // Добавляем транзакцию в стек возврата для возможности возврата назад
            fragmentTransaction.addToBackStack(null)
            // Подтверждаем транзакцию
            fragmentTransaction.commit()
        }

        // Устанавливаем обработчик нажатия для перехода на ThirdFragment с использованием Navigation API
        binding.nextFragmentWithNavigationApi.setOnClickListener {
            findNavController().navigate(R.id.action_secondFragment_to_thirdFragment)
        }
    }
}
