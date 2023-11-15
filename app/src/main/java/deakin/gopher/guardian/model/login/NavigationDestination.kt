package deakin.gopher.guardian.model.login

sealed class NavigationDestination(destination: String) {
    data object CaretakerHomepage : NavigationDestination("CaretakerHomepage")
    data object AdminHomepage : NavigationDestination("AdminHomepage")
    data object NurseHomepage : NavigationDestination("NurseHomepage")
}
