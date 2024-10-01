import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

// Класс, представляющий список всех расходов
class ExpenseTracker {
    // Список для хранения всех добавленных расходов
    private val expenses = mutableListOf<Expense>()

    // Метод для добавления нового расхода в список
    fun addExpense(amount: Double, category: String) {
        // Создаем новый объект расхода с заданной суммой и категорией, текущая дата добавляется автоматически
        val newExpense = Expense(amount, category)
        // Добавляем новый расход в список
        expenses.add(newExpense)
        // Сообщаем об успешном добавлении расхода
        println("Добавлен новый расход: $amount руб. в категории '$category' на дату ${newExpense.date}.")
    }

    // Метод для вывода всех расходов
    fun showAllExpenses() {
        // Проверяем, есть ли расходы
        if (expenses.isEmpty()) {
            println("Расходы отсутствуют.")
        } else {
            // Если расходы есть, выводим каждый из них
            println("Все расходы:")
            expenses.forEach { it.printExpense() }
        }
    }

    // Метод для подсчета суммы всех расходов по каждой категории
    fun showExpensesByCategory() {
        // Если расходов нет, сообщаем об этом и прерываем выполнение метода
        if (expenses.isEmpty()) {
            println("Нет расходов для подсчета.")
            return
        }

        // Карта для хранения итоговых сумм по категориям
        val categoryTotals = mutableMapOf<String, Double>()

        // Проходим по каждому расходу и суммируем по категориям
        for (expense in expenses) {
            // Для каждой категории добавляем текущий расход к общей сумме
            categoryTotals[expense.category] = categoryTotals.getOrDefault(expense.category, 0.0) + expense.amount
        }

        // Выводим сумму расходов по каждой категории
        println("Сумма расходов по категориям:")
        categoryTotals.forEach { (category, total) ->
            println("Категория: $category | Сумма: $total руб.")
        }
    }
}
