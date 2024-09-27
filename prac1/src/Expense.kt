import java.text.SimpleDateFormat
import java.util.Date

// Класс, представляющий расход
class Expense(val amount: Double, val category: String, val date: String) {
    // Метод, выводящий информацию о конкретном расходе
    fun printExpense() {
        println("Сумма: $amount | Категория: $category | Дата: $date")
    }

    // Дополнительный конструктор для автоматической установки даты
    constructor(amount: Double, category: String) : this(amount, category, SimpleDateFormat("dd/MM/yyyy").format(Date()))
}