fun main() {
    // Создаем экземпляр класса ExpenseTracker для отслеживания расходов
    val tracker = ExpenseTracker()

    // Пример добавления расходов
    tracker.addExpense(150.0, "Еда")
    tracker.addExpense(200.0, "Транспорт")
    tracker.addExpense(50.0, "Еда")

    // Вывод всех расходов
    tracker.showAllExpenses()

    // Подсчет суммы расходов по категориям
    tracker.showExpensesByCategory()
}
