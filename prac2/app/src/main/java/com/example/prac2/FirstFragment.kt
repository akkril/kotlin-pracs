package com.example.prac2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.prac2.databinding.FragmentFirstBinding

// Класс FirstFragment представляет первый фрагмент в приложении
class FirstFragment : Fragment() {
    // Переменная _binding используется для хранения ссылки на объект привязки (binding) фрагмента
    private var _binding: FragmentFirstBinding? = null
    // Свойство binding предоставляет безопасный доступ к объекту привязки
    private val binding get() = _binding!!

    // Метод вызывается при создании представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация объекта привязки с использованием inflater для заполнения макета
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        // Возвращаем корневое представление привязки
        val view = binding.root
        return view
    }

    // Метод вызывается при уничтожении представления фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Освобождаем объект привязки, чтобы предотвратить утечки памяти
        _binding = null
    }

    // Метод вызывается, когда представление уже создано
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем обработчик нажатия на кнопку, чтобы перейти на второй фрагмент через транзакцию вручную
        binding.nextFragmentWithTransition.setOnClickListener {
            // Получаем FragmentManager для управления фрагментами
            val fragmentManager = requireActivity().supportFragmentManager
            // Начинаем транзакцию для замены текущего фрагмента
            val fragmentTransaction = fragmentManager.beginTransaction()
            // Заменяем первый фрагмент вторым
            fragmentTransaction.replace(R.id.fragment_container, SecondFragment())
            // Добавляем транзакцию в стек возврата, чтобы можно было вернуться к предыдущему фрагменту
            fragmentTransaction.addToBackStack(null)
            // Подтверждаем транзакцию
            fragmentTransaction.commit()
        }

        // Устанавливаем обработчик нажатия на кнопку для навигации на второй фрагмент с использованием Navigation API
        binding.nextFragmentWithNavigationApi.setOnClickListener {
            // Используем NavController для навигации с первого фрагмента на второй
            findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }
}
