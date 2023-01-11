# Book-Store
Привет, меня зовут Никита. Вашему вниманию представлен аналог читай-города\литреса в моем исполнении.
Стэк технологий мною используемых: Spring-Boot\Security\Data , Jsoup, Liquibase, Json-simple, Hibernate, OAuth2.0, PostgreSql, JUnit, Cookie, JWT token.
Сайт состоит из:
1. Главная страницы на которой есть разделы - Рекомендуемое , Новинки и Полпулярное. У каждого раздела свой алгоритм работы. Так же есть облако тэгов, Header и Footer. 
Интерфейс последних отличается в зависимости АВТОРИЗОВАН пользователь или нет. В  интерфейсе Header расоложены следующие элементы: лого магазиа, поискоая строка, архив авторизованного пользователя,
где хронятся купленные книги с индикатором кол-ва, отложенные пользователем книги с индикатором кол-ва, корзина с индикатором кол-ва, имя и фамилия авторизованного пользователя , а так же его баланс и кнопка входа из аккаунта.
Интерфейс Footer содержит элементы - ссылки на соц. сети, ссылки на следующие страницы: главная, жанры, новинки, популярное, авторы, вход, документы, о сайте, помощь, котнакты и смена языка - (EN, RU).
![localhost_8085_](https://user-images.githubusercontent.com/85135441/211776666-40929c4f-b044-4208-aac9-1112530bb779.png)
2. Жанры, каждый жанр кликабелен и ведет на странице с книгами имеющеми выбранный жанр.У жанров есть поджанры и т.д.
![localhost_8085_genres](https://user-images.githubusercontent.com/85135441/211777337-44024690-c075-44cf-b308-34c32ec55af0.png)
3. Новинки. У данного раздела есть сортировака по дате выхода книги.
![localhost_8085_books_recent](https://user-images.githubusercontent.com/85135441/211777617-da2bdd37-7518-4bea-8fc0-da094b19e547.png)
4. Популярное. Здесь представлены книги , которые заблаговременно были отсортированны спецальным алгоритмом по популярности.
![localhost_8085_books_popular_lang=ru](https://user-images.githubusercontent.com/85135441/211778072-3c2381e3-7dce-4180-b193-e911c38a9927.png)
5. Авторы. На данной странице пользователю представлены имеющиеся авторы в алфовитном порядке. Сверху можно увидеть ряд первых букв полного имени автора , что облегчит поиск заветного автораа в случае , если авторов очень много.
![localhost_8085_authors](https://user-images.githubusercontent.com/85135441/211778695-8b3c80f6-7437-4d4e-9895-fce1f7464581.png)
6. Вход. На данной странице моего сайта представлена функция входа в уже зарегестрированный аккаунт с помощью указанного при регистрации телефона или почты. Так же можно заметить 
возможность зарегестрироваться или войти с помощью соц. сети Вконтакте.
![localhost_8085_signin](https://user-images.githubusercontent.com/85135441/211780749-57c25b5e-4d8d-4243-9bb7-5eb7fbf0e4a8.png)
7. Регистрация. Здесь пользователь может заполнить форму  в которой нужно ввести и подтвердить почту и телефон по смс шлюзу , после чего пользователю представиться возможность войти в новоиспеченный аккаунт.
![localhost_8085_signup](https://user-images.githubusercontent.com/85135441/211780727-30e5d3e7-d684-4714-a78e-10f1d858fe69.png)
8. Личный кабинет.  Здесь пользователь может видеть\изменять информацию о себе , а так же пополнить свой баланс.
![localhost_8085_profile](https://user-images.githubusercontent.com/85135441/211781298-ad5c5254-f22a-4278-b4aa-365eb71ddb6b.png)
9. Книга. После щелчка по изображению и названию книги в описанном мною ранее интерфесе пользователь попадает на данную страницу. Здесь пользователь может: оценть книгу(только если пользователь авторизован),
отложить книгу, купить положить книгу в корзину, скачать в трех вариантах (pdg, epub, fb2) , и добавить к себе в архив. Так же пользователь может оставить совой комментайрий и оценить свой\чужой комментарий.
![localhost_8085_books_book-mds-938](https://user-images.githubusercontent.com/85135441/211783317-67c55d3b-bca5-410a-9c32-03e170b3f84a.png)
10. Страница автора. Здесь пользователь может ознакомиться с краткой биографией автора, увдеть его фото и его самые популярные книги. Если книг данного автора на этой странице пользователю недостаточно, он может нажать на кнопу "Все книги автора"
с указанием кол-ва всех книг автора и ознакомиться с ними.
![localhost_8085_books_authors_author=Anya%20McMeekin offset=0 limit=6](https://user-images.githubusercontent.com/85135441/211784138-50077888-c1a9-4c96-9daf-e2d06cf32559.png)
11. Поиск. На данную странице пользователь прейдет в случае если ему захочится найти книгу (и она есть) по назавнию.
![localhost_8085_search_Tri](https://user-images.githubusercontent.com/85135441/211784563-9763fb1c-69e2-461a-bd2f-c4ba3f55e219.png)
12. Отложенное. Здесь пользователь может выидеть список книг , которые он отложил, удалить\купить все или некоторые из них и купить все(по нажитии данной кнопки пользователь перенесет все книги в Корзину).
![localhost_8085_books_postponed](https://user-images.githubusercontent.com/85135441/211785599-9471c18b-f2dd-433c-9996-884c0459fac1.png)
13. Корзина. Здесь пользователь может посмотреть на выбранне им рание книги, отложить\удалить и оплатить(функция еще в разработке) их.
![localhost_8085_books_cart](https://user-images.githubusercontent.com/85135441/211786049-70aaeac3-2cdc-4bcc-a720-91a197595118.png)


Сайт использует уникальные JWT токены с сроком годности в сутки для аунтетификации пользователя.
