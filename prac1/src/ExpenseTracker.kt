import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

// Класс, представляющий список всех расходов
class ExpenseTracker {
    private val expenses = mutableListOf<Expense>()

    // Метод для добавления нового расхода в список
    fun addExpense(amount: Double, category: String) {
        val newExpense = Expense(amount, category)
        expenses.add(newExpense)
        println("Добавлен новый расход: $amount руб. в категории '$category' на дату ${newExpense.date}.")
    }

    // Метод для вывода всех расходов
    fun showAllExpenses() {
        if (expenses.isEmpty()) {
            println("Расходы отсутствуют.")
        } else {
            println("Все расходы:")
            expenses.forEach { it.printExpense() }
        }
    }

    // Метод для подсчета суммы всех расходов по каждой категории
    fun showExpensesByCategory() {
        if (expenses.isEmpty()) {
            println("Нет расходов для подсчета.")
            return
        }

        val categoryTotals = mutableMapOf<String, Double>()

        for (expense in expenses) {
            categoryTotals[expense.category] = categoryTotals.getOrDefault(expense.category, 0.0) + expense.amount
        }

        println("Сумма расходов по категориям:")
        categoryTotals.forEach { (category, total) ->
            println("Категория: $category | Сумма: $total руб.")
        }
    }
}
