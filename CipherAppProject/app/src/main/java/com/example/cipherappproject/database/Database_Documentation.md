# Database

## 1. Overview

The `database` package manages the application's data using Firebase Cloud Firestore.

It contains the data models, repositories, enums, custom exceptions, shared repository provider, and services required to manage the application's database operations.

The main responsibilities are:

- Creating, retrieving, and updating Firestore documents.
- Maintaining local copies of database data using Compose State.
- Providing methods to retrieve and filter objects from local lists.
- Handling database errors using Kotlin's `Result` class and custom exceptions.
- Managing application statuses using enums.
- Coordinating operations that involve multiple repositories through services.

## 2. Package Structure

The current database package is organized as follows:

```text
database/
│
├── models/
│   ├── Profile.kt
│   ├── Campsite.kt
│   ├── CampsiteAvailability.kt
│   ├── Lottery.kt
│   ├── LotteryEntry.kt
│   ├── Reservation.kt
│   ├── Rating.kt
│   ├── Notification.kt
│   └── Message.kt
│
├── repositories/
│   ├── ProfileRepository.kt
│   ├── CampsiteRepository.kt
│   ├── CampsiteAvailabilityRepository.kt
│   ├── LotteryRepository.kt
│   ├── LotteryEntryRepository.kt
│   ├── ReservationRepository.kt
│   ├── RatingRepository.kt
│   ├── NotificationRepository.kt
│   └── MessageRepository.kt
│
├── provider/
│   └── RepositoryDatabase.kt
│
├── services/
│   └── ProfileDeletion.kt
│
├── exceptions/
│   └── DatabaseExceptions.kt
│
├── enums/
│   └── DatabaseEnums.kt
│
└── tests/
    └── DatabaseUnitTest.kt
```

The models represent the objects stored in Firestore.

The repositories contain the methods used to communicate with Firestore and retrieve information from local lists.

The provider supplies shared repository instances.

The services coordinate operations that involve multiple repositories.

The exceptions package contains custom exceptions used throughout the database.

The enums package contains the application's predefined statuses and roles.

## 3. Firestore Structure

The current Firestore structure follows this organization:

```text
profiles/
    {profileId}

campsites/
    {campsiteId}
        availability/
            {campsiteAvailabilityId}
ratings/
    {ratingId}

lotteries/
    {lotteryId}
        entries/
            {lotteryEntryId}

reservations/
    {reservationId}

notifications/
    {notificationId}

conversations/
    {conversationId}
        messages/
            {messageId}
```

Some collections contain subcollections. When accessing a document inside a subcollection, the parent document ID must also be provided when constructing its reference.

For example:

```kotlin
val lotteryEntryRef = db
    .collection("lotteries")
    .document(lotteryId)
    .collection("entries")
    .document(lotteryEntryId)
```

> **Important**: You do no need to worry about this nested children objects. This is an abtraction made already behind the escenes for you. Just make sure when you need to provide the ID of the parent + ID of the children or simply ID of the children or ID of the parent.

## 4. Repository Pattern

Each repository is responsible for managing one type of database object.

For example:

```kotlin
class ProfileRepository {
    // Firestore references

    // State variables and snapshot listeners

    // Firestore operations

    // Local list operations

    // Init listener
}
```

Repositories generally provide two types of operations:

### Firestore operations

These communicate directly with Firestore.

Examples include:

- `addProfile()`
- `getProfileById()`
- `updateProfile()`

These operations are asynchronous and return their results through callbacks using Kotlin's `Result` class.

### Local list operations

These retrieve information from the repository's local State list without making another Firestore request.

Examples include:

- `getProfileFromListById()`
- `getProfilesFromListByStatus()`

Use local list operations when the required information is already available in the repository's local cache.

The local list is synchronized with Firestore through snapshot listeners.

## 5. RepositoryDatabase – Shared Repository Instances

`RepositoryDatabase` is a Kotlin `object` that provides shared repository instances throughout the application.

Example:

```kotlin
object RepositoryDatabase {
    val profileRepository = ProfileRepository()

    val campsiteRepository = CampsiteRepository()
    val reservationRepository = ReservationRepository()

    val lotteryRepository = LotteryRepository()
    val lotteryEntryRepository = LotteryEntryRepository()

    val campsiteAvailabilityRepository = CampsiteAvailabilityRepository(reservationRepository)

    val ratingRepository = RatingRepository()
    val notificationRepository = NotificationRepository()
    val messageRepository = MessageRepository()

    val profileDeletion = ProfileDeletion(
        profileRepository,
        reservationRepository,
        lotteryRepository,
        lotteryEntryRepository,
        campsiteRepository,
        campsiteAvailabilityRepository
    )
}
```
OR

```kotlin
object RepositoryDatabase {

    val profileRepository by lazy { ProfileRepository() }

    val campsiteRepository by lazy { CampsiteRepository() }

    val reservationRepository by lazy { ReservationRepository() }

    val lotteryRepository by lazy { LotteryRepository() }

    val lotteryEntryRepository by lazy { LotteryEntryRepository() }

    val campsiteAvailabilityRepository by lazy {
        CampsiteAvailabilityRepository(reservationRepository)
    }

    val ratingRepository by lazy { RatingRepository() }

    val notificationRepository by lazy { NotificationRepository() }

    val messageRepository by lazy { MessageRepository() }

    val profileDeletion by lazy {
        ProfileDeletion(
            profileRepository,
            reservationRepository,
            lotteryRepository,
            lotteryEntryRepository,
            campsiteRepository,
            campsiteAvailabilityRepository
        )
    }
}
```

The `by lazy` delegate initializes each repository only when it is first accessed. Once initialized, the same instance is reused. With the other method, however, the repository created when is first initialized.

> **_I will likely change to the last one, but the use is the exact same either way!_**


### Why should repositories be shared?

Repositories may initialize Firestore snapshot listeners in their `init` blocks.

Creating multiple instances of the same repository can create unnecessary listeners and duplicate database reads.

Using a shared instance helps:

- Avoid unnecessary repository instances.
- Avoid creating duplicate snapshot listeners.
- Keep a consistent local cache.
- Allow different screens and services to access the same repository data.
- Centralize repository initialization.

Instead of creating a new repository:

```kotlin
val profileRepository = ProfileRepository()
```

Use the shared instance:

```kotlin
val profileRepository = RepositoryDatabase.profileRepository
```

You can also access it directly:

```kotlin
RepositoryDatabase.profileRepository.getProfileById(profileId,result)
```

Creating a local `val` that references the provider does not create another repository. It simply gives you a convenient variable pointing to the existing shared instance.

### What is `by lazy`?

`by lazy` delays initialization until the property is first accessed.

For example:

```kotlin
val profileRepository by lazy {
    ProfileRepository()
}
```

The repository is created the first time `profileRepository` is accessed. Future accesses reuse that same instance.

This is useful because repositories do not all need to be initialized immediately when the provider is accessed.

## 6. State and Local Lists

Repositories use Jetpack Compose State to maintain local copies of Firestore data.

For example, in `ProfileRepository`:

```kotlin
private val _allProfiles = mutableStateOf<List<Profile>>(emptyList())

val profiles: State<List<Profile>>
    get() = _allProfiles
```

There are two properties:

- `_allProfiles`: private mutable State that the repository can update.
- `profiles`: public read-only State that other parts of the application can observe.

This prevents other classes from directly replacing the repository's State value.

### How does the local list get updated?

Repositories use Firestore snapshot listeners.

Example:

```kotlin
init {
    profilesRef.addSnapshotListener { snapshot, error ->

        if (error != null) {
            return@addSnapshotListener
        }

        if (snapshot == null) {
            return@addSnapshotListener
        }

        val updatedProfiles = snapshot.documents.mapNotNull { document ->
            document.toObject(Profile::class.java)
        }

        _allProfiles.value = updatedProfiles

        _isProfilesLoaded.value = true
    }
}
```

The listener receives updates from Firestore and converts the documents into Kotlin objects.

The new list is assigned to `_allProfiles.value` once the conversion is complete.

This approach replaces the entire list instead of clearing and adding elements one at a time. Therefore, observers do not see an intermediate empty or partially rebuilt list.

### State loading indicator

Repositories also maintain a Boolean State to indicate whether the initial data load has completed.

Example:

```kotlin
private val _isProfilesLoaded = mutableStateOf(false)

val isProfilesLoaded: State<Boolean>
    get() = _isProfilesLoaded
```

The value starts as `false` and becomes `true` after the first successful snapshot is received.

## -> Before using local list methods, please check whether the required data has finished loading!

For example:

```kotlin
if (profileRepository.isProfilesLoaded.value) {
    val profile = profileRepository.getProfileFromListById(profileId)
}
```

The loading indicator represents completion of the initial load. It does not mean that no future updates will occur.

### How to access State: `by` versus `.value`

There are two ways to access State values.

**Option 1: Using `by`**

Inside a Composable:

```kotlin
@Composable
fun ProfileScreen() {

    val profiles by RepositoryDatabase.profileRepository.profiles

    Text("Number of profiles: ${profiles.size}")
}
```

The `by` keyword delegates access to the State value. Compose observes the State when it is read during composition and recomposes the screen when it changes.

**Option 2: Using `.value`**

```kotlin
val profiles = RepositoryDatabase.profileRepository.profiles.value
```

This accesses the current value directly.

It can be used in ordinary Kotlin functions, tests, and Composables.

When `.value` is read during composition, Compose can still observe the State and recompose when it changes.

The main difference is syntax:

- `by` provides convenient delegated access.
- `.value` explicitly accesses the current State value.

Outside a Composable, reading `.value` does not automatically subscribe an ordinary Kotlin function to future updates. Read it again when you need the latest value.

### Do we need `remember`?

`remember` is generally used for local State created inside a Composable.

For example:

```kotlin
@Composable
fun ProfileScreen() {

    var showDialog by remember {
        mutableStateOf(false)
    }

}
```

It preserves the value across recompositions.

However, repository State is already created and stored inside the repository. There is no need to create another `remember` State for the same repository data.

### Important: Updating State lists

Because the public State is `State<List<Profile>>`, you can neither modify the state nor the add or remove items from the list outside of the repository.

To add more clarification how this works, in the repository the list is also replaced rather than adding or removing items from the list one by one:

**Old method**
```kotlin
_allProfiles.value = _allProfiles.value + newProfile
```

**New method**
```kotlin
_allProfiles.value.add(newProfile)
```

The public list is read-only, and replacing the State value also notifies observers of the change, so if there any update, the variable state that you use either using "by" or ".value" will know that new list.

## 7. Kotlin Result and Error Handling

Firestore operations use Kotlin's `Result` class to communicate whether an operation succeeded or failed.

For example:

```kotlin
fun getProfileById(
    profileId: String,
    result: (Result<Profile>) -> Unit
)
```

The callback provides either:

- `Result.success(profile)` when the operation succeeds.
- `Result.failure(exception)` when the operation fails.

The exact success value depends on the operation.

Examples:

| Operation | Success result |
|---|---|
| Add a document | `Result<String>` – document ID |
| Retrieve one object | `Result<Profile>` or another model |
| Update a document | `Result<Unit>` |

### Option 1: Using `isSuccess` and `isFailure`

This is a straightforward way to handle results. **But not recommended at all. This is only useful for test cases.**

```kotlin
val result = awaitCallback<Result<Profile>> { callback ->
    profileRepository.getProfileById(profileId, callback)
}

if (result.isSuccess) {

    val profile = result.getOrThrow()

    Log.d("Profile", "Profile found: ${profile.name}")

} else {

    val exception = result.exceptionOrNull()

    Log.e("Profile", "Failed to retrieve profile", exception)
}
```

Remember:

- `isSuccess` checks whether the operation succeeded.
- `isFailure` checks whether the operation failed.
- `getOrThrow()` retrieves the successful value or throws the stored exception.
- `exceptionOrNull()` retrieves the exception if the operation failed.

Do not use `getOrThrow()` without first establishing that success is expected or handling the possibility of failure.

### Option 2: Using `onSuccess` and `onFailure`

These functions allow success and failure to be handled separately. **I recommend using this one.**

```kotlin
result.onSuccess { profile ->

    Log.d("Profile", "Profile found: ${profile.name}")

}

result.onFailure { exception ->

    Log.e("Profile", "Failed to retrieve profile", exception)

}
```

You can also use `when` inside `onFailure` to handle different exception types.

```kotlin
result.onFailure { exception ->

    when (exception) {

        is ValidationException -> {
            // Invalid input
        }

        is NotFoundException -> {
            // Requested document does not exist
        }

        is DataConversionException -> {
            // Firestore data could not be converted
        }

        is DuplicateException -> {
            // Record already exists
        }

        is ConflictException -> {
            // Operation conflicts with existing data
        }

        is PermissionException -> {
            // User is not authorized
        }

        is OperationException -> {
            // Operation is not allowed in the current state
        }

        else -> {
            // Other errors, including Firebase exceptions
        }
    }
}
```

### Option 3: Using `fold`

`fold` handles both possible outcomes in one expression.

```kotlin
result.fold(

    onSuccess = { profile ->

        Log.d("Profile", "Profile found: ${profile.name}")

    },

    onFailure = { exception ->

        Log.e("Profile", "Failed to retrieve profile", exception)

    }

)
```

Use whichever approach makes the code easiest to understand. All three approaches are valid.

When using `Result`, you can handle successful operations and failures through methods such as `onSuccess`, `onFailure`, or `fold`, without needing a `try-catch` for every repository call.

However, `Result` does not automatically prevent the application from crashing. Make sure failures are handled appropriately, and be careful when using methods such as `getOrThrow()`, which can throw an exception.

## 8. Custom Database Exceptions

The `exceptions` package contains `DatabaseExceptions.kt`.

These custom exceptions make it easier to identify why a database operation failed.

### ValidationException

```kotlin
class ValidationException(message: String) : Exception(message)
```

An ID, date, price, or other input is invalid.

Examples:

- An empty document ID.
- An invalid date range.
- A negative price.

### NotFoundException

```kotlin
class NotFoundException(message: String) : Exception(message)
```

A requested document does not exist.

Examples:

- Retrieving a profile that does not exist.
- Updating a campsite that does not exist.
- A required parent document is missing.

### DataConversionException

```kotlin
class DataConversionException(message: String) : Exception(message)
```

Firestore data cannot be converted into the expected Kotlin model.

### DuplicateException

```kotlin
class DuplicateException(message: String) : Exception(message)
```

A record already exists when duplicates are not allowed.

Example:

- A user attempts to register for the same lottery more than once.

### ConflictException

```kotlin
class ConflictException(message: String) : Exception(message)
```

An operation conflicts with existing data.

Example:

- A reservation overlaps with another reservation for the same campsite.

### PermissionException

```kotlin
class PermissionException(message: String) : Exception(message)
```

The user is not authorized to perform an operation.

Example:

- An entrant attempts to update a campsite owned by another user.

### OperationException

```kotlin
class OperationException(message: String) : Exception(message)
```

An operation cannot be completed because of the current state of the object.

Example:

- Someone attempts to join a lottery that has already been completed.

The input itself might be valid and the lottery might exist, but the operation is not allowed in its current state.

### Important

Firebase exceptions are also passed through `Result.failure()` when a Firebase operation fails.

The custom exceptions are used for application-specific errors. They do not replace every exception that Firebase can produce.

## 9. Database Enums

The `enums` package contains `DatabaseEnums.kt`.

Enums provide predefined values for roles and statuses. They help prevent inconsistent strings from being used throughout the application.

The current enums are:

| Enum | Purpose |
|---|---|
| `CampsiteAvailabilityStatus` | Status of a campsite's offered availability period |
| `CampsiteStatus` | Status of a campsite |
| `LotteryEntryStatus` | Status of an entrant's lottery entry |
| `LotteryStatus` | Status of a lottery |
| `MessageStatus` | Status of a message |
| `ProfileRole` | User roles and permissions |
| `ProfileStatus` | Status of a profile |
| `RatingStatus` | Status of a rating or review |
| `ReservationStatus` | Status of a reservation |

### Using enums

Firestore stores the enum values as strings.

Use `.name` to obtain the string representation of an enum.

For example:

```kotlin
val status = ProfileStatus.ACTIVE.name

val role = listOf(
    ProfileRole.OWNER.name,
    ProfileRole.USER.name
)
```

This stores:

```text
status = "ACTIVE"

role = ["OWNER", "USER"]
```

When creating or updating objects, use enum values instead of manually typing status strings.

This helps keep the stored data consistent throughout the application.

## 10. Timestamps – createdAt

The models use Firebase Firestore `Timestamp` for their `createdAt` properties.

For example:

```kotlin
data class Profile(
    val profileId: String = "",
    var name: String = "",
    var email: String = "",
    val role: List<String> = emptyList(),
    var imageProfilePath: String = "",
    val deviceId: String = "",
    var status: String = "",
    var phoneNumber: String = "",
    var notificationsEnabled: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
)
```

`Timestamp.now()` assigns the current timestamp when the Kotlin object is created.

Firestore stores the timestamp as a Timestamp value rather than an ordinary formatted date string.

### Important when updating objects

The `createdAt` property represents when the object was originally created.

It should generally remain unchanged when updating an existing object.

For example, when retrieving an existing profile and constructing an updated profile, preserve its original timestamp:

```kotlin
val updatedProfile = Profile(
    profileId = existingProfile.profileId,
    name = "Updated Name",
    email = existingProfile.email,
    role = existingProfile.role,
    imageProfilePath = existingProfile.imageProfilePath,
    deviceId = existingProfile.deviceId,
    status = existingProfile.status,
    phoneNumber = existingProfile.phoneNumber,
    notificationsEnabled = existingProfile.notificationsEnabled,
    createdAt = existingProfile.createdAt
)
```

This is particularly important because `transaction.set()` replaces the entire document. If a new model is created without specifying `createdAt`, its default `Timestamp.now()` will replace the original creation timestamp.

If an operation needs to update only selected fields, consider using Firestore's `update()` instead of replacing the entire document.


**Important: Preserving `createdAt` When Updating Documents**

When creating a new model, you do not need to manually provide `createdAt`, because it has a default value of `Timestamp.now()`.

However, when updating an existing document, remember that `transaction.set()` replaces the entire Firestore document. If you create a new model without preserving its original `createdAt`, the timestamp will be replaced with the current date and time.

To avoid this, use `.copy()` when updating an existing model:

```kotlin
val updatedProfile = existingProfile.copy(
    name = "Updated Name"
)
```

The `.copy()` function preserves all existing properties that are not explicitly changed, including `createdAt`.

If you create a new model manually for an update, make sure to provide the original `createdAt` value.


## 11. Services – ProfileDeletion

The `services` package contains operations that coordinate multiple repositories.

`ProfileDeletion` is a service responsible for coordinating the profile deletion workflow.

Profile deletion may affect several related collections, such as:

- Reservations
- Lottery entries
- Lotteries
- Campsites
- Campsite availability
- Ratings
- Messages
- Notifications

This is why profile deletion should not simply be implemented as a single document deletion inside `ProfileRepository`.

The service receives the repositories it needs through constructor injection.

For example:

```kotlin
val profileDeletion by lazy {
    ProfileDeletion(
        profileRepository,
        reservationRepository,
        lotteryRepository,
        lotteryEntryRepository,
        campsiteRepository,
        campsiteAvailabilityRepository
    )
}
```

The service coordinates the necessary operations across these repositories.

### Why use a service?

Repositories are responsible for operations involving their own data.

A service is useful when one business operation requires multiple repositories to work together.

For example, deleting a profile may require handling its reservations, lottery entries, and campsites according to the application's rules.

Keeping this workflow in a service avoids putting cross-repository responsibilities inside a single repository.

**Important: Deleting a profile does not automatically delete every related Firestore document. Instead the database use a soft deletion using status.**

## 12. General Guidelines for the Team

Please follow these guidelines when working with the database:

1. Use `RepositoryDatabase` to access shared repository instances instead of creating unnecessary new repository instances.

2. Keep Firestore operations inside repositories. Screens and Composables should call repository methods rather than directly accessing Firestore.

3. Use local list methods when the required data is already available in the repository's State list.

4. Check the corresponding `is...Loaded` State before relying on data from a local list.

5. Use enums for roles and statuses instead of manually typing strings. Use `.name` when storing enum values in Firestore.

6. Use the custom exceptions in `DatabaseExceptions.kt` when handling application-specific errors.

7. Handle repository callbacks through `Result`. Use `isSuccess` / `isFailure`, `onSuccess` / `onFailure`, or `fold`, depending on what makes the code easiest to understand.

8. Preserve `createdAt` when updating existing objects.

9. Use services for operations that coordinate multiple repositories.

10. Avoid modifying repository internals directly. Use the repository's public methods to update database data and its local cache.

11. Coordinate changes to shared repository methods with the team. Avoid changing method signatures or repository behavior without informing teammates, since other parts of the application may depend on them.

12. **Adding or Modifying Queries:**

If you need a new query or want to modify an existing one, please let me know what you want to accomplish first so I can review it and implement it with you.

I have spent a lot of time designing the database structure and maintaining data integrity, so any direct Firestore queries should be reviewed before implementation. Even a small query change can affect relationships between collections, data consistency, or existing repository behavior.

If you want to add or modify a local list query, you can work on it yourself, but please let me review it first because it may affect the repository's State and how the application receives updated data.

When adding a query, please also:

- Add you name and date.
- Follow the existing naming conventions.
- Add a clear comment explaining what the query does and its purpose.
- Explain the logic, including the conditions or filters being applied.
- Document what the query returns and how its result is handled.

The goal is to keep the database consistent and make sure everyone understands how each query works.

## 13. Testing

Database tests are located in:

```text
androidTest/DatabaseUnitTest.kt 
```

Tests cover:

- Adding documents.
- Retrieving existing documents.
- Retrieving documents that do not exist.
- Updating existing documents.
- Updating documents that do not exist.
- Invalid inputs and custom exceptions.
- Retrieving objects from local State lists.
- Retrieving objects that are not present in local lists.

For asynchronous Firestore operations, use the existing `awaitCallback` helper to wait for the callback result.

For local list tests, wait for the relevant `is...Loaded.value` State to become `true` before testing the local list methods.

Remember that Firestore operations modify real database documents during these tests. Use appropriate test data and document IDs.

**You can also refer to DatabaseUnitTest.kt to better understand how the repository methods work, the expected results, and the logic behind different operations.**

The test cases provide examples of how to call repository methods, handle successful and failed Result responses, and verify that the database behaves as expected.

If you are unsure how a method works or what it should return, please check the corresponding test case first.
---

**Main idea:** Repositories manage database operations and local State. `RepositoryDatabase` provides shared instances. Enums keep statuses consistent. Custom exceptions identify application-specific errors. Services coordinate workflows involving multiple repositories.

## 14. Additional Recommendations and Important Notes

There are a few important details that everyone on the team should keep in mind when working with the database.

### 1. Shared Repository Instances

Repositories should be accessed through `RepositoryDatabase` instead of creating unnecessary new instances throughout the application.

Since repositories use Firestore snapshot listeners, creating a new repository every time a screen opens can create unnecessary listeners and duplicate database reads.

The shared repository provider helps avoid this by allowing different screens and services to reuse the same repository instances.

### 2. Preserve `createdAt` When Updating Documents

The `createdAt` property represents when a document was originally created. It should generally remain unchanged when updating an existing document.

Our repository update methods may use:

```kotlin
transaction.set(profileRef, profile)
```

This replaces the entire Firestore document rather than updating only selected fields.

Therefore, creating a new model with the default `Timestamp.now()` can overwrite the original creation timestamp.

When constructing an updated object, preserve the existing timestamp:

```kotlin
createdAt = existingProfile.createdAt
```

This also applies to other models that contain a `createdAt` property.

### 3. Local State Is Not a Replacement for Firestore

The repository's State list is a local cache of the data stored in Firestore.

Firestore remains the persistent database, while snapshot listeners synchronize changes between Firestore and the local State list.

The local list is useful for retrieving and filtering already-loaded data without making additional Firestore requests.

However, changes to local data alone do not automatically save changes to Firestore. Database updates should be performed through the appropriate repository methods.

### 4. Eager Initialization vs. Lazy Initialization

`RepositoryDatabase` can initialize repository properties eagerly or lazily.

Both approaches are valid.

**Eager initialization:**

```kotlin
val profileRepository = ProfileRepository()
```

The repository is created when the `RepositoryDatabase` object is first initialized.

**Lazy initialization:**

```kotlin
val profileRepository by lazy {
    ProfileRepository()
}
```

The repository is created only when `profileRepository` is accessed for the first time. The same instance is reused afterward.

Lazy initialization can be useful when not every repository is needed immediately.

Using eager initialization is also perfectly fine. The important part is that the application accesses shared repository instances instead of repeatedly creating new ones.


## Citation
- _Used OpenAI, ChatGPT for documentation formatting, grammar, and visual representation on 05-16-2026. Assistance was based on my existing sketches and documentation in Google Drive, including my class notes, project ideas, and the main methods from two repository classes. AI assistance was primarily used to speed up the documentation process and improve its presentation. The underlying ideas, design decisions, and code are my own unless explicitly acknowledged otherwise. This is also after I reviewed and adapted the resulting documentation provided by the Chatbot. Thank you._