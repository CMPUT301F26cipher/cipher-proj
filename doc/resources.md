citations for final uml wiki


Google, Gemini Hydrogen, "tell me which files this code interacts with, include a brief description of the interaction", 2026-11-25
Google, Gemini Hydrogen, "using template i made, i will give you code for a file. please compile it in the same way @startuml
!include doc/final-uml-wiki/style.puml

class reservationView {
    +ReservationScreen(userID: String, isOwner: Boolean, onRateClick: Reservation)
    +ReservationStatusSelector(selectedStatus: Status, onStatusSelected: Status)
    +DisplayListContent(reservationsList: ReservationList, status: String, isOwner: Boolean, \ncontroller: ReservationController, userID: String, onRateClick: (Reservation) -> Unit)
    +ConfirmDialog(title: String, text: String, confirmTag: String, onConfirm: () -> Unit, onDismiss: () -> Unit)
    +ReservationDetail(reservation: Reservation, helpers: ReservationListHelpers, onReject: () -> Unit, onBack: () -> Unit)
    +ReservationRow(imagePath: String, campsiteName: String, userName: String, bookingPeriod: String, onCancelClick: () -> Unit = {}, buttonLabel: String, showButton: Boolean, onClick: () -> Unit), rateLabel: String, onRateClick: () -> Unit = {})
}
@enduml", 2026-11-24
Google, Gemini Hydrogen, "in kotlin a constructor could have, onRateClick: (Reservation) -> Unit = {}, but how would this be represented in a uml class diagram?", 2026-11-24
[plantuml documentation](https://plantuml.com/), 2026-11-24