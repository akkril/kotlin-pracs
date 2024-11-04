package com.example.prac4

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prac4.databinding.DateItemBinding

// Адаптер для RecyclerView, который отображает список строк (dataList)
class DataAdapter(private val dataList: List<String>) : RecyclerView.Adapter<DataAdapter.DataHolder>() {

    // Внутренний класс для хранения и управления элементами списка
    class DataHolder(private val itemBinding: DateItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        // Метод для привязки данных к элементу интерфейса
        fun bind(data: String) {
            // Устанавливаем текстовое значение для TextView из data
            itemBinding.itemTextView.text = data
        }
    }

    // Метод, который вызывается при создании нового элемента ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        // Создаем объект itemBinding для доступа к элементам разметки DateItemBinding
        val itemBinding = DateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Возвращаем новый объект DataHolder
        return DataHolder(itemBinding)
    }

    // Метод, который вызывается для привязки данных к ViewHolder на определенной позиции
    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        // Получаем данные для текущей позиции
        val data = dataList[position]
        // Привязываем данные к ViewHolder
        holder.bind(data)
    }

    // Возвращает общее количество элементов в списке
    override fun getItemCount(): Int {
        return dataList.size
    }
}
