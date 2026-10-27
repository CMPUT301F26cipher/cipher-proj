# Project Part 3 Task Distribution
Legend

S - Schedule related\
D - Dependence on\
P - Productivity related (low priority)

---

## Madison
- [ ] 
- [ ] 
- [ ] 

## Neth
- [ ] 
- [ ] 
- [ ] 

## Shira
- [x] grouped project classes by features using CRC cards and user stories (oct 25) (D)
    -   8 feature groups
- [x] set up UML class diagrams using PlantUML (v.1 from CRC -> UML) (oct 25) (D) 
    - [x] lottery
    - [x] filter
    - [x] campsite-display
    - [x] admin
    - [x] notifications-and-messages
    - [x] profile
    - [x] reservation-and-waitlist
- [ ] write script to update uml's images (P)
- [ ] 

## Sofia
- [ ] 
- [ ] 
- [ ] 

## Bailey
- [ ] 
- [ ] 
- [ ] 

## Justin
- [ ] 
- [ ] 
- [ ] 

## Hye-Sung
- [ ] 
- [ ] 
- [ ] 

# Additionals
- [ ] Presentation (P)
- [ ] Meeting Minutes (S)
- [ ] I want to see the images in our doc/wiki-images on a wiki page for quick referencing (S)
- [ ] 

---
# CRC & US breakdown
---
## Grouping tasks
*Midpoint US are bolded and italicized*



### Data
- Database
- Device
- CampsiteModel
- QRScan (01.12.01, 02.02.01)
- CalendarModel
- WaitlistModel

### Filter
- SearchBarController
- SerachBarView
- FilterBy
- EntrantsView

Related US\
US ***01.01.01, 01.01.02, 01.01.03,***



### Campsite Display
- CampsiteSummaryView
- CampsiteInfoView
- CampsiteOwnerView

Related US\
US ***01.01.04, 01.01.05,*** 01.13.01

### Notifications & messages
- NotificationView
- NotificationModel
- MessageModel
- MessageView
- confirmationView
- PolicyView

Related US\
US 01.03.02, 02.12.01, 03.12.01, 01.05.02, 02.10.01, 02.10.02, 03.08.01, 01.05.01, 01.07.02, 01.10.01, 01.11.01, 



### Profile
- User
- AccountController
- LoginView
- SignupView
- ProfileInfoView
- CampsiteOwner
- Entrant

Related US\
US 01.07.03, 01.08.01, ***01.09.01***, 

### Admin (low prior right now as heavy dependency)
- AdministratorView
- Administrator

Related US\
US ***03.11.01***

### Lottery 
- LotteryLoseView
- LotteryWinView
- LotteryController
- CreateLotteryView
- EditLotteryView
- LotteryModel

Related US\
US 01.01.06, ***01.04.01, 01.04.02, 01.04.03, 02.04.01, 02.04.02, 02.04.05, 02.04.06, 02.04.07***, 02.05.01, 

Note: both lottery and reservation interact with reservationModel heavily

### Reservation & waitlists
- ReservationModel
- ActivationReservationView
- PastReserviationView
- RegisterCampsiteView
- CampsiteOwnerReservationView
- WaitlistView
- CampsiteOwnerWaitlistView

Related US\
US ***01.06.01, 01.06.02***, 01.07.01, ***02.01.01, 02.01.02, 02.01.03, 02.01.04, 02.03.01, 02.04.03, 02.04.04***, 02.06.01, ***02.07.01, 02.07.02***, 02.07.03, 02.08.01, ***02.09.01***, 


waitlists\
US ***01.02.01***, 01.02.02, 