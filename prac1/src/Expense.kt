import java.text.SimpleDateFormat
import java.util.Date

// Класс, представляющий расход
class Expense(val amount: Double, val category: String, val date: String) {

    // Метод, выводящий информацию о конкретном расходе
    fun printExpense() {
        // Выводим на экран сумму, категорию и дату расхода
        println("Сумма: $amount | Категория: $category | Дата: $date")
    }

    // Дополнительный конструктор, который автоматически задает текущую дату
    constructor(amount: Double, category: String) : this(
        amount,
        category,
        // Используем SimpleDateFormat для форматирования текущей даты в формате "день/месяц/год"
        SimpleDateFormat("dd/MM/yyyy").format(Date())
    )
}
