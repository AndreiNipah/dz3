Гусев Андрей Юрьевич, Б9123-09.03.02прс

Я выбрал API REST Contries. Это публичный REST-сервис, который предоставляет информацию о странах мира. С его помощью можно получить список всех стран, выполнить поиск страны по названию или коду, а также загрузить подробную информацию о конкретной стране.

API не требует ключа, авторизация и дополнительные настройки не нужны, Базовый URL: https://restcountries.com/v3.1/

Чек-лист:

Из обязательного: Navigation Compose (Search / Detail / Favourites), список + детали через REST Countries API, ViewModel + UiState, stateless UI (state + callbacks), Repository между ViewModel и Retrofit, Coroutines + Retrofit (suspend + viewModelScope), UI-состояния Loading / Error (с Retry) / Empty / Success, избранное локально без БД (хранится в ViewModel), Compose + Material3.

Из бонусов: debounce поиска без Flow (Job + delay), кнопка Refresh, экран Favourites как отдельный route, кэш последнего результата в памяти, логирование запросов через OkHttp/

<img width="337" height="626" alt="изображение" src="https://github.com/user-attachments/assets/2d973d72-f76d-4aa6-bf0a-836f88241934" />
<img width="335" height="615" alt="изображение" src="https://github.com/user-attachments/assets/87eab71f-62e2-4430-8443-8fec989cbd30" />
<img width="340" height="635" alt="изображение" src="https://github.com/user-attachments/assets/77a0d444-b740-4f3a-8edd-6928867c0cec" />
<img width="335" height="628" alt="изображение" src="https://github.com/user-attachments/assets/1b5a6f60-491c-4c51-bf11-115a44aefedd" />

