import java.text.SimpleDateFormat
import java.util.Date

// Класс, представляющий расход
class Expense(val amount: Double, val category: String, val date: String, val item: String,var firstname: String, var lastname: String) {

    // Метод, выводящий информацию о конкретном расходе
    fun printExpense() {
        // Выводим на экран сумму, категорию и дату расхода
        println("Сумма: $amount | Категория: $category | Дата: $date | Item: $item | New Item: ${Item}")
    }

    val fullname: String
        get() = "$firstname $lastname"


    // Дополнительный конструктор, который автоматически задает текущую дату
    constructor(amount: Double, category: String) : this(
        amount,
        category,
        SimpleDateFormat("dd/MM/yyyy").format(Date()),
    )
}
