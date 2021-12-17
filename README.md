# Minimalist Weather

Minimalist Weather is a simple Android application which will show the latest weather forecast summary using the [Dark Sky API](https://darksky.net/dev).

This document explains some high level architectural decisions. Some specific design choices are explained in the code.

## Code Architecture

The source code is organized under packages named after features. Within the feature packages, there's an interface e.g. `WeatherContract.kt` which provides an outline of the feature and its components. 

Intended architecture is an implementation of MVP pattern where `Activity` class is considered as _View_, WeatherPresenter and WeatherInteractor together represents the _Presentation_ and WeatherService is as data/_Model_

When required, components of the contract are provided as constructor parameters in order to make testing easier by providing mock implementations while testing.

### Model

[OkHttp](https://square.github.io/okhttp/) is used for performing network requests. 

`Application` class holds a single instance of `OkHttpClient` and provides it by its Singleton instance. This is a shortcut and ideally replaced by a dependency injection framework like [Dagger](https://github.com/google/dagger) or [Koin](https://github.com/InsertKoinIO/koin).

`WeatherService` orchestrates the network request by creating a `Request` with given latitude and longitude and executes when invoked. It will return the `Response` or will throw an exception. A proper exception handling is not in place for now.

### View

`MainActivity` is the only view in the app which renders the states provided by the presenter and performs tasks for granting location permission.

### Presentation

`WeatherInteractor` class is responsible for getting the network response from `WeatherService` and converting the response to a domain object, i.e. `Forecast`. It handles some exceptional cases (e.g. response or response body being null) but proper error handling and propagation is not in place.

`WeatherPresenter` translates `UiAction`s and `Forecast` data from the interactor into `UiState`s. Some tasks (e.g. data mapping) in the presenter can and should be delegated to other interfaces while it grows.

#### State Management

Presentation is achieved by a primitive implementation of a finite state machine where ui state and actions are enumerated by sealed classes. It simplifies the communication between view-presenter and prevents app going into unexpected states. 

**UI State**

State of the UI are enumerated as following

- **Loading**: User will see a loading indicator
- **RequestLocationPermission**: We need to request user permission to access location
- **RefreshLocation**: Presenter wants to get a new location. Not really a state but an action to be performed by the view. It is a shortcut to avoid delegating `LocationProvider ` into presenter.
- **Content**: There's some weather data to render, provided in a ui model 
- **Error**: An error occurred and we want to communicate it to the user.

**Ui Actions**

Actions that can be performed by the User/UI are enumerated as following

- **LocationUpdated**: Location callback provided a new location 
- **RetryClicked**: Oversimplified default action for retry button in error case. Shortcut to limit error handling to once case.
- **LocationPermissionDenied**: When we have no permission to get the user location.
- **LocationPermissionGranted**: User granted the location permission. We can access location now.

#### Concurrency

Kotlin [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) are utilized for orchestrating asynchronous tasks. _Main_ dispatcher is used for updating the UI, while _IO_ dispatcher is used for performing network operations.

Dispatchers are injected to the presenter in order to be able to inject _Unconfined_ dispatchers for testing.

### Testing

Some functionality of the presenter and the interactor are tested. Test classes showcase mocking (using [Mockito](https://github.com/mockito/mockito)) and handling asynchronous tasks.

UI tests are missing and needs to be created to test rendering of the states.