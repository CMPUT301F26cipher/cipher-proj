# Data Classes, Repositories, and Enums

## 1. Data Classes

There are six data classes in our database structure:

- `Campsite`
- `CampsiteAvailability`
- `Lottery`
- `LotteryEntry`
- `Profile`
- `Reservation`

Their attributes and specifications are described in the project documentation.

You can use getters and setters to access and modify their attributes, following a Java-style approach. However, there is something important to understand: these objects are local copies of the data stored in Firebase Firestore.

When you modify an attribute using a setter, you are only changing the local object. It does not automatically update the database.

If you want to save those changes to Firestore, you must use the corresponding `update` method in that object's repository. For example, modifying a `Campsite` object locally does not change the Firestore document until you call `updateCampsite()` from `CampsiteRepository`.

These data classes are still Kotlin classes, but their main purpose in our project is to represent and hold data. They are not intended to contain all the functionality or business logic of the application.

You can use their attributes and getters/setters, but additional methods and functionality needed by the application should generally belong in their own appropriate classes.

## 2. Repositories

Repositories act as the bridge between our Kotlin data classes and Firebase Firestore.

Each repository is responsible for communicating with the corresponding Firestore collection or subcollection. It provides methods to create, retrieve, update, and, when appropriate, delete objects.

For example:

- `ProfileRepository` communicates with the `profiles` collection.
- `CampsiteRepository` communicates with the `campsites` collection.
- `CampsiteAvailabilityRepository` communicates with the `availability` subcollections.
- `LotteryEntryRepository` communicates with the `entries` subcollections.
- `ReservationRepository` communicates with the `reservations` collection.

The repositories also provide methods to query the database. This means the application does not need to communicate directly with Firebase every time it needs information.

### Example: ProfileRepository

The `ProfileRepository` communicates with one main Firestore collection:

```text
profiles
    ├── profileId_1 -> Profile
    ├── profileId_2 -> Profile
    └── profileId_3 -> Profile
```

Each document represents one `Profile` object.

The repository has a private list:

```kotlin
private val _allProfiles = mutableStateListOf<Profile>()
```

And a public property:

```kotlin
val profiles: List<Profile>
    get() = _allProfiles
```

The `init` block creates a Firestore snapshot listener. Whenever the collection changes, the listener retrieves the documents, converts them into Kotlin `Profile` objects, and updates `_allProfiles`.

This means the repository maintains a local list of the profiles it receives from Firestore. Inspired and taken from lab 05.

**Important:** `_allProfiles` is private. Other classes should access the list through the public `profiles` property.

For example:

```kotlin
val profileRepository = ProfileRepository()

val allProfiles = profileRepository.profiles
```

The snapshot listener works asynchronously, so the list might initially be empty while the data is being retrieved. Do not assume that the profiles are immediately available when you create the repository.

### Available ProfileRepository operations

Based on the current implementation:

| Method | Purpose |
|---|---|
| `addProfile()` | Creates a new Profile object and saves it in Firestore with a generated document ID. |
| `updateProfile()` | Saves the provided Profile object to its existing Firestore document. |
| `getProfileById()` | Retrieves one Profile using its document ID. |
| `profiles` | Provides access to the locally maintained list of profiles. |
| `init` snapshot listener | Keeps the local list synchronized with changes in the profiles collection. |

The Firestore path for a profile is:

```text
profiles/{profileId}
```

For example, `updateProfile()` uses the profile ID to locate the correct document.

**You do no need to worry about this, I am trying to encapsulate this so you only worry about the important stuff.**

Remember that modifying a Profile with a setter only changes the local object. Calling `updateProfile()` is what sends the updated object to Firestore.

Also, the current implementation uses `.set(profile)`. This replaces the document's stored data with the provided object; it is not a partial field update.

## 3. How the other repositories work

The other repositories follow a similar general idea, but their Firestore structures are not all identical.

For example:

**CampsiteRepository**

```text
campsites/{campsiteId}
```

Works with the main `campsites` collection.

**CampsiteAvailabilityRepository**

```text
campsites/{campsiteId}/availability/{availabilityId}
```

Works with availability documents nested inside a campsite. It also uses a collection group query to observe availability documents across different campsites.

**LotteryEntryRepository**

```text
lotteries/{lotteryId}/entries/{entryId}
```

Works with entries nested inside individual lotteries. It can retrieve entries for one lottery or use a collection group query to observe entries across lotteries.

**ReservationRepository**

```text
reservations/{reservationId}
```

Works with the main reservations collection. It can also query reservations using fields such as `userId`.

The specific methods available in each repository are documented alongside their implementations.

## 4. Enums: Proposed but they are not definitive yet

We can also use enums to represent a fixed set of possible values, such as statuses.

For example:

```kotlin
enum class ProfileStatus {
    ACTIVE,
    DELETED
}
```

Instead of passing an arbitrary String everywhere, we can use the enum:

```kotlin
val status = ProfileStatus.ACTIVE
```

However, Firestore stores the enum as a String in our current approach. We can use `.name` to convert it:

```kotlin
val statusForFirestore = ProfileStatus.ACTIVE.name
```

This stores:

```text
"ACTIVE"
```

For example, when creating a profile:

```kotlin
addProfile(
    name = "Alex",
    email = "alex@example.com",
    role = listOf("USER"),
    imageProfilePath = "",
    deviceId = "device123",
    status = ProfileStatus.ACTIVE.name
)
```

If we retrieve the String from Firestore, we can convert it back into an enum:

```kotlin
val status = ProfileStatus.valueOf(statusString)
```

Be aware that `valueOf()` requires an exact match. If the stored String does not match an enum entry, it throws an exception.

Another example for lottery entries:

```kotlin
enum class LotteryEntryStatus {
    REGISTERED,
    WITHDRAWN,
    SELECTED,
    NOT_SELECTED
}
```

Then we can use:

```kotlin
val status = LotteryEntryStatus.REGISTERED.name
```

**Important:** The enum examples are suggestions, not an instruction to change the existing models immediately. We should agree as a team on the enum values or any other similar method and how we want to use them before changing shared interfaces or existing code.

## 5. General architecture

The main idea is to keep responsibilities separate:

| Component | Responsibility |
|---|---|
| Data classes | Represent and hold the application's data. |
| Getters and setters | Access and modify attributes in local objects. |
| Repositories | Communicate with Firestore and provide database queries. |
| Other application classes | Handle additional functionality and business logic. |
| Enums | Represent a predefined set of possible values. |

In short, use the data classes to work with objects, use the repositories when you need to communicate with Firestore, and place additional application functionality in the appropriate classes.

> ### Remember: changing a local object does not automatically change Firestore. You must call the corresponding repository method to save those changes.

## Citation
- _Used OpenAI, ChatGPT for documentation formatting, grammar, and visual representation on 05-11-2026. Assistance was based on my existing sketches and documentation in Google Drive, including my class notes, project ideas, and the main methods from two repository classes. AI assistance was primarily used to speed up the documentation process and improve its presentation. The underlying ideas, design decisions, and code are my own unless explicitly acknowledged otherwise. This is also after I reviewed and adapted the resulting documentation provided by the Chatbot. Thank you_.