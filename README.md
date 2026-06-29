Гусев Андрей Юрьевич, Б9123-09.03.02прс


Я использую REST Countries API. Это REST-сервис, который предоставляет информацию о странах мира. Через него приложение получает список стран, выполняет поиск по названию и загружает подробную информацию по коду страны.

В текущей версии API используется авторизация через Bearer token. Поэтому для работы приложения нужен API-ключ. Он добавляется в сетевой слой через OkHttp interceptor в заголовок:

Authorization: Bearer <API_KEY>

Но так как ключ бесплатный и неограниченный в использовании, он уже вставлен в код проекта.

Базовый URL:

https://api.restcountries.com/countries/


## Что было исправлено и добавлено

1) Добавлен Hilt

В проект добавлен dependency injection через Hilt.

Через Hilt создаются:
- Retrofit;
- OkHttpClient;
- RestCountriesApi;
- Room database;
- DAO;
- CountriesRepository;
- ViewModel.

Также добавлены:
- Application-класс с @HiltAndroidApp;
- @AndroidEntryPoint для MainActivity;
- @HiltViewModel для ViewModel.

2) Добавлена Room база данных

Room используется для хранения локальных пользовательских данных:
- избранных стран;
- истории просмотров.

Данные сохраняются после перезапуска приложения.

3) Добавлены отдельные экраны

Добавлены экраны:
- Favourites;
- History.

Favourites показывает список избранных стран.
History показывает список стран, которые пользователь открывал на экране деталей.

4) Исправлена архитектура ViewModel

Логика разделена по разным ViewModel:
- SearchViewModel — список стран и поиск;
- CountryDetailsViewModel — детали страны;
- FavouritesViewModel — избранное;
- HistoryViewModel — история просмотров.

Это сделано, чтобы один ViewModel не отвечал сразу за все экраны.

5) Исправлена обработка состояний

Для экранов используются состояния:
- Loading;
- Success;
- Empty;
- Error.

Если страна не найдена, приложение показывает пустое состояние, а не падает.
Если произошла ошибка сети, пользователь может повторить запрос через Retry/Refresh.

6) Исправлена работа с новым форматом API

Был обновлён сетевой слой под актуальный формат API.

Исправлены:
- BASE_URL;
- endpoint-ы;
- Bearer authorization;
- DTO-модели;
- мапперы ответа API.

Ответ API обрабатывается через wrapper-объект, а не как простой список.

7) Исправлен UI карточек

Флаги стран в списке и на экране деталей получили фиксированный размер и contentScale, чтобы карточки не ломались из-за разных размеров изображений.


<img width="349" height="753" alt="изображение" src="https://github.com/user-attachments/assets/11b771d1-2cf4-4eef-a9b4-7c67a77ae308" />
<img width="342" height="752" alt="изображение" src="https://github.com/user-attachments/assets/6339193b-8601-4f59-b6b7-c4fcd5250bc1" />

<img width="337" height="626" alt="изображение" src="https://github.com/user-attachments/assets/2d973d72-f76d-4aa6-bf0a-836f88241934" />
<img width="335" height="615" alt="изображение" src="https://github.com/user-attachments/assets/87eab71f-62e2-4430-8443-8fec989cbd30" />
<img width="340" height="635" alt="изображение" src="https://github.com/user-attachments/assets/77a0d444-b740-4f3a-8edd-6928867c0cec" />
<img width="338" height="753" alt="изображение" src="https://github.com/user-attachments/assets/e25ed431-6e45-4076-bf03-b6cab028f737" />
<img width="344" height="753" alt="изображение" src="https://github.com/user-attachments/assets/5222eccb-1e26-4cf3-8cc0-9d44b836281c" />

