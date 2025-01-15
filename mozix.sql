-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Jan 15, 2025 at 09:36 AM
-- Server version: 5.7.24
-- PHP Version: 8.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mozix`
--
CREATE DATABASE IF NOT EXISTS `mozix` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `mozix`;

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `actorsRoleWithImage` ()   SELECT name, role, actor_image
FROM movie_actors
WHERE movie_id = 101$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `AllRoleOfActors` (IN `nameIN` VARCHAR(100))   SELECT movie_id, role
FROM movie_actors
WHERE name LIKE nameIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Average_ratingsToAMovie` ()   SELECT 
    movie_id, 
    AVG(rating) AS average_rating
FROM ratings
WHERE movie_id = 102
GROUP BY movie_id$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BestRatedMovies` ()   SELECT 
    movies.movie_id, 
    movies.movie_name, 
    AVG(ratings.rating) AS average_rating
FROM movies
INNER JOIN ratings ON movies.movie_id = ratings.movie_id
GROUP BY movies.movie_id, movies.movie_name
HAVING average_rating > 8.5
ORDER BY average_rating DESC$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `BirthDateInOrder` ()   SELECT name, birth_date, role
FROM movie_actors
ORDER BY birth_date ASC$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `directors movies` (IN `directorIN` VARCHAR(100))   SELECT directors.name AS "director name", movies.movie_name AS "movie name"
FROM movies
INNER JOIN directors ON movies.director_id = directors.director_id
WHERE directors.name LIKE directorIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `everyActorInAMovie` ()   SELECT movie_id, COUNT(*) AS actor_count
FROM movie_actors
GROUP BY movie_id
ORDER BY actor_count DESC$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `filmActorsBirthDateInOrder` ()   SELECT name, birth_date
FROM movie_actors
ORDER BY birth_date ASC$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `filmDetails` ()   SELECT 
    movie_id, 
    movie_name, 
    release_year, 
    description, 
    (SELECT name FROM directors WHERE director_id = movies.director_id) AS director_name, 
    (SELECT name FROM genres WHERE genre_id = movies.genre_id) AS genre_name
FROM movies$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (IN `userIN` VARCHAR(100), IN `passwordIN` VARCHAR(100))   BEGIN
    DECLARE user_count INT;

    SELECT COUNT(*)
    INTO user_count
    FROM users
    WHERE username = userIN AND password_hash = passwordIN;

    IF user_count > 0 THEN
        SELECT 'Login successful' AS Message;
    ELSE
        SELECT 'Invalid username or password' AS Message;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `Movies directors` (IN `movieIN` VARCHAR(100))   SELECT movies.movie_name AS "movie name", directors.name AS "director name"
FROM movies
INNER JOIN directors ON movies.director_id = directors.director_id
WHERE movies.movie_name LIKE movieIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `MoviesByGenre` (IN `genreIN` VARCHAR(100))   SELECT 
    movies.movie_id, 
    movies.movie_name, 
    movies.release_year,
    genres.name
FROM movies
INNER JOIN genres ON movies.genre_id = genres.genre_id
WHERE genres.name LIKE genreIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `movietitles` ()   SELECT movie_name
FROM movies$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `directors`
--

CREATE TABLE `directors` (
  `director_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `director_image` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `directors`
--

INSERT INTO `directors` (`director_id`, `name`, `director_image`, `birth_date`) VALUES
(1, 'Radványi Géza', 'radvanyi_geza.jpg', '1907-09-26'),
(2, 'Szabó István', 'szabo_istvan.jpg', '1938-02-18'),
(3, 'Bacsó Péter', 'bacso_peter.jpg', '1928-01-06'),
(4, 'Keleti Márton', 'keleti_marton.jpg', '1905-04-27'),
(5, 'Makk Károly', 'makk_karoly.jpg', '1925-12-22'),
(6, 'Ternovszky Béla', 'ternovszky_bela.jpg', '1943-05-23'),
(7, 'Szomjas György', 'szomjas_gyorgy.jpg', '1940-11-26'),
(8, 'Várkonyi Zoltán', 'varkonyi_zoltan.jpg', '1912-05-13'),
(9, 'Fábri Zoltán', 'fabri_zoltan.jpg', '1917-10-15'),
(10, 'Jancsó Miklós', 'jancso_miklos.jpg', '1921-09-27'),
(11, 'Mihályfi Imre', 'mihalyfi_imre.jpg', '1912-12-06'),
(12, 'Sándor Pál', 'sandor_pal.jpg', '1939-04-19'),
(13, 'Enyedi Ildikó', 'enyedi_ildiko.jpg', '1955-11-15'),
(14, 'Ujj Mészáros Károly', 'ujj_meszaros_karoly.jpg', '1973-08-21'),
(15, 'Nimród Antal', 'nimrod_antal.jpg', '1973-11-30'),
(16, 'Pálfi György', 'palfi_gyorgy.jpg', '1974-04-19'),
(17, 'Bereményi Géza', 'beremenyi_geza.jpg', '1946-01-25'),
(18, 'Gyöngyössy Imre', 'gyongyossy_imre.jpg', '1930-02-25'),
(19, 'Szőts István', 'szots_istvan.jpg', '1912-06-30'),
(20, 'Török Ferenc', 'torok_ferenc.jpg', '1971-04-23'),
(21, 'Gothár Péter', 'gothar_peter.jpg', '1947-08-28'),
(22, 'Sopsits Árpád', 'sopsits_arpad.jpg', '1952-02-02'),
(23, 'Fekete Ibolya', 'fekete_ibolya.jpg', '1951-01-28'),
(24, 'Mundruczó Kornél', 'mundruczo_kornel.jpg', '1975-04-03'),
(25, 'Hajdu Szabolcs', 'hajdu_szabolcs.jpg', '1972-04-26'),
(26, 'Magyar Dezső', 'magyar_dezso.jpg', '1934-12-04'),
(27, 'Herskó János', 'hersko_janos.jpg', '1926-04-09'),
(28, 'Marton Endre', 'marton_endre.jpg', '1915-09-20'),
(29, 'Fehér György', 'feher_gyorgy.jpg', '1939-12-15'),
(30, 'Kertész Mihály', 'kertesz_mihaly.jpg', '1886-12-24'),
(31, 'Szász Attila', 'szasz_attila.jpg', '1972-10-23'),
(32, 'Goda Krisztina', 'goda_krisztina.jpg', '1970-03-28'),
(33, 'Tímár Péter', 'timar_peter.jpg', '1950-12-19'),
(34, 'Xantus János', 'xantus_janos.jpg', '1953-11-07'),
(35, 'Herendi Gábor', 'herendi_gabor.jpg', '1960-12-02'),
(36, 'Mundruczkó Kornél', 'mundruczko_kornel.jpg', '1975-04-03'),
(37, 'Korda Sándor', 'korda_sandor.jpg', '1893-09-16'),
(38, 'Koltai Róbert', 'koltai_robert.jpg', '1943-12-16'),
(39, 'Mihályfy Sándor', 'mihalyfy_sandor.jpg', '1937-01-21'),
(40, 'Székely István', 'szekely_istvan.jpg', '1899-02-25'),
(41, 'Szász János', 'szasz_janos.jpg', '1958-03-14'),
(42, 'Reisz Gábor', 'reisz_gabor.jpg', '1980-01-19'),
(43, 'Nemes Jeles László', 'nemes_jeles_laszlo.jpg', '1977-02-18');

-- --------------------------------------------------------

--
-- Table structure for table `genres`
--

CREATE TABLE `genres` (
  `genre_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `genres`
--

INSERT INTO `genres` (`genre_id`, `name`) VALUES
(1, 'Dráma'),
(2, 'Szatíra'),
(3, 'Vígjáték'),
(4, 'Animációs vígjáték'),
(5, 'Zenei'),
(6, 'Történelmi'),
(7, 'Ifjúsági'),
(8, 'Sci-fi'),
(9, 'Romantikus vígjáték'),
(10, 'Horror'),
(11, 'Thriller'),
(12, 'Kaland'),
(13, 'Fantasztikus'),
(14, 'Dokumentumfilm'),
(15, 'Romantikus dráma'),
(16, 'vígjáték-dráma horror'),
(17, 'vígjáték-dráma'),
(18, 'művészfilm');

-- --------------------------------------------------------

--
-- Table structure for table `movies`
--

CREATE TABLE `movies` (
  `movie_id` int(11) NOT NULL,
  `release_year` int(11) DEFAULT NULL,
  `description` text,
  `director_id` int(11) DEFAULT NULL,
  `genre_id` int(11) DEFAULT NULL,
  `movie_name` varchar(99) NOT NULL,
  `Length` int(11) DEFAULT NULL,
  `movie_actor_id` int(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movies`
--

INSERT INTO `movies` (`movie_id`, `release_year`, `description`, `director_id`, `genre_id`, `movie_name`, `Length`, `movie_actor_id`) VALUES
(101, 1948, 'Egy háború utáni történet gyermekekről, akik a túlélésért küzdenek.', 1, 1, 'Valahol Európában', 100, NULL),
(102, 1981, 'Egy színész, aki kompromisszumokat köt a karrierjéért.', 2, 1, 'Mephisto', 144, NULL),
(103, 1969, 'Egy férfi szürreális tapasztalatai a kommunista rezsim alatt.', 3, 2, 'A tanú', 110, NULL),
(104, 1965, 'Egy katona humoros kalandjai a második világháború alatt.', 4, 3, 'Tizedes meg a többiek', 109, NULL),
(105, 1971, 'Egy szerelmi történet a politikai elnyomás árnyékában.', 5, 15, 'Szerelem', 96, NULL),
(106, 1986, 'Egy humoros animációs történet a macskák és egerek harcáról.', 6, 4, 'Macskafogó', 96, NULL),
(107, 1981, 'Egy zenekar tagjainak küzdelmei a rendszer ellen.', 7, 5, 'Kopaszkutya', 98, NULL),
(108, 1968, 'A magyar történelem egyik legnagyobb csatája.', 8, 6, 'Egri csillagok', 120, NULL),
(109, 1976, 'Egy filozófiai dráma az emberi erkölcsről.', 9, 1, 'Az ötödik pecsét', 102, NULL),
(110, 1967, 'Egy csoport katona története az orosz forradalom idején.', 10, 1, 'Csillagosok, katonák', 94, NULL),
(111, 1979, 'Egy humoros történet egy falusi bakter életéről.', 39, 3, 'Indul a bakterház', 85, NULL),
(112, 1931, 'Egy újgazdag család és az elegáns lakáj története.', 40, 3, 'Hyppolit, a lakáj', 90, NULL),
(113, 1989, 'Két nővérek élete a 20. század elején.', 13, 1, 'Az én XX. századom', 95, NULL),
(114, 2015, 'Egy fiatal nő szerelmi története, némi misztikummal.', 14, 4, 'Liza, a rókatündér', 95, NULL),
(115, 2003, 'Egy metróellenőr mindennapjai és kihívásai.', 15, 3, 'Kontroll', 105, NULL),
(116, 2006, 'Egy szürreális családi történet több generáción keresztül.', 16, 16, 'Taxidermia', 91, NULL),
(117, 2002, 'Széchenyi István életének drámai ábrázolása.', 17, 6, 'Hídember', 100, NULL),
(118, 2002, 'Egy falusi közösség csendes története a mindennapokról.', 16, 2, 'Hukkle', 75, NULL),
(119, 1966, 'Egy fiú és az apja kapcsolata a múlt árnyékában.', 2, 1, 'Apa', 90, NULL),
(120, 1969, 'Egy fiúcsapat barátságának története.', 9, 7, 'Pál utcai fiúk', 95, NULL),
(121, 2001, 'A rendszerváltás idején játszódó fiatalos történet.', 20, 3, 'Moszkva tér', 101, NULL),
(122, 2018, 'Egy nő túlélési története egy szovjet munkatáborban.', 31, 6, 'Örök tél', 98, NULL),
(123, 2006, 'Az 1956-os forradalom története és annak hatásai.', 32, 15, 'Szabadság, szerelem', 103, NULL),
(124, 1991, 'Egy humoros történet a rendszerváltás idejéről.', 33, 3, 'Csapd le csacsi!', 95, NULL),
(125, 1984, 'Egy szürreális szerelmi történet.', 34, 1, 'Eszkimó asszony fázik', 92, NULL),
(126, 2004, 'Egy humoros történet a magyar történelem jelentős eseményeiről.', 35, 3, 'Magyar vándor', 116, NULL),
(127, 2017, 'Két lélek különös kapcsolata egy vágóhídon.', 13, 1, 'Testről és lélekről', 116, NULL),
(128, 2014, 'Egy kóbor kutya története a modern társadalomban.', 36, 10, 'Fehér isten', 119, NULL),
(129, 1918, 'Egy klasszikus Jókai Mór regény adaptációja.', 37, 1, 'Aranyember', 98, NULL),
(130, 1993, 'Egy humoros és nosztalgikus történet az életről.', 38, 3, 'Sose halunk meg', 107, NULL),
(131, 2013, 'A film középpontjában álló ikerpárt édesanyjuk egy határszéli faluba küldi nagymamájukhoz, hogy ott vészeljék át a háború végét.', 41, 1, 'A Nagy Füzet', 109, NULL),
(132, 2014, 'Szentesi Áron egy 20-as évei végén járó budapesti fiú, aki munkanélküliként éli mindennapjait, de az egyik nap barátnője, Eszter elhagyja.', 42, 3, 'VAN valami furcsa és megmagyarázhatatlan', 90, NULL),
(133, 2015, '1944. október 7-8-án játszódik Auschwitz-Birkenauban a Sonderkommandók lázadása idején.', 42, 1, 'Saul fia', 107, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `movie_actors`
--

CREATE TABLE `movie_actors` (
  `movie_actor_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `movie_id` int(11) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `actor_image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movie_actors`
--

INSERT INTO `movie_actors` (`movie_actor_id`, `name`, `movie_id`, `birth_date`, `actor_image`) VALUES
(1, 'Sinkovits Imre', 101, '1928-09-21', 'sinkovits_imre.jpg'),
(2, 'Bánsági Ildikó', 102, '1947-06-19', 'bansagi_ildiko.jpg'),
(3, 'Kállai Ferenc', 103, '1925-07-04', 'kallai_ferenc.jpg'),
(4, 'Bodrogi Gyula', 104, '1934-04-15', 'bodrogi_gyula.jpg'),
(5, 'Darvas Iván', 105, '1925-06-14', 'darvas_ivan.jpg'),
(6, 'Mikó István', 106, '1947-11-24', 'miko_istvan.jpg'),
(7, 'Cserhalmi György', 107, '1948-02-17', 'cserhalmi_gyorgy.jpg'),
(8, 'Sinkovits Imre', 108, '1928-09-21', 'sinkovits_imre.jpg'),
(9, 'Latinovits Zoltán', 109, '1931-09-09', 'latinovits_zoltan.jpg'),
(10, 'Hernádi Judit', 110, '1956-04-11', 'hernadi_judit.jpg'),
(11, 'Kovács Kati', 111, '1944-10-25', 'kovacs_kati.jpg'),
(12, 'Kabos Gyula', 112, '1887-03-19', 'kabos_gyula.jpg'),
(13, 'Béres Ilona', 113, '1942-06-04', 'beres_ilona.jpg'),
(14, 'Balsai Móni', 114, '1977-12-13', 'balsai_moni.jpg'),
(15, 'Csányi Sándor', 115, '1975-12-19', 'csanyi_sandor.jpg'),
(16, 'Trill Zsolt', 116, '1971-03-22', 'trill_zsolt.jpg'),
(17, 'Czapkó Antal', 117, '1978-05-06', 'czapko_antal.jpg'),
(18, 'Pogány Judit', 118, '1944-07-10', 'pogany_judit.jpg'),
(19, 'Garas Dezső', 119, '1934-12-09', 'garas_dezso.jpg'),
(20, 'Törőcsik Mari', 120, '1935-11-23', 'torocsik_mari.jpg'),
(21, 'Nagy Zsolt', 121, '1974-10-28', 'nagy_zsolt.jpg'),
(22, 'Gera Marina', 122, '1984-07-17', 'gera_marina.jpg'),
(23, 'Dobó Kata', 123, '1974-02-25', 'dobo_kata.jpg'),
(24, 'Eperjes Károly', 124, '1954-02-17', 'eperjes_karoly.jpg'),
(25, 'Eszenyi Enikő', 125, '1961-01-11', 'eszenyi_eniko.jpg'),
(26, 'Rudolf Péter', 126, '1959-10-15', 'rudolf_peter.jpg'),
(27, 'Morcsányi Géza', 127, '1952-09-28', 'morcsanyi_geza.jpg'),
(28, 'Nagy Marcell', 128, '2004-03-11', 'nagy_marcell.jpg'),
(29, 'Jászai Mari', 129, '1850-02-24', 'jaszai_mari.jpg'),
(30, 'Koltai Róbert', 130, '1943-12-16', 'koltai_robert.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `rating_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `movie_id` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL,
  `review` text,
  `rating_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ratings`
--

INSERT INTO `ratings` (`rating_id`, `user_id`, `movie_id`, `rating`, `review`, `rating_date`) VALUES
(1, 1, 101, 9, 'Nagyszerű történet, kiváló színészek.', '2023-01-15'),
(2, 2, 102, 9, 'Mély és elgondolkodtató.', '2023-01-18'),
(3, 3, 103, 8, 'Humoros, de egy kicsit hosszú.', '2023-01-20'),
(4, 4, 104, 10, 'Fantasztikus klasszikus!', '2023-02-05'),
(5, 5, 105, 8, 'Szívszorító történet.', '2023-02-10'),
(6, 6, 106, 10, 'Gyerekkorom kedvence!', '2023-03-01'),
(7, 7, 107, 7, 'Jó, de nem kiemelkedő.', '2023-03-10'),
(8, 8, 108, 9, 'Kiváló történelmi film.', '2023-03-15'),
(9, 9, 109, 9, 'Elképesztően jó színészi alakítás.', '2023-04-01'),
(10, 10, 110, 8, 'Egyedi hangulatú film.', '2023-04-12'),
(11, 11, 111, 10, 'Igazi klasszikus humor.', '2023-04-20'),
(12, 12, 112, 8, 'Régi, de mindig szórakoztató.', '2023-05-01'),
(13, 13, 113, 9, 'Művészi alkotás.', '2023-05-10'),
(14, 14, 114, 8, 'Furcsa, de szórakoztató.', '2023-06-01'),
(15, 15, 115, 8, 'Izgalmas és sötét.', '2023-06-15'),
(16, 16, 116, 7, 'Túl szürreális nekem.', '2023-07-01'),
(17, 17, 117, 9, 'Széchenyi története nagyon inspiráló.', '2023-07-10'),
(18, 18, 118, 8, 'Csendes, de lenyűgöző.', '2023-07-25'),
(19, 19, 119, 8, 'Megható családi dráma.', '2023-08-05'),
(20, 20, 120, 10, 'Egy igazán időtálló klasszikus.', '2023-08-15'),
(21, 21, 121, 9, 'Nostalgikus és életszagú.', '2023-09-01'),
(22, 22, 122, 9, 'Történelmileg nagyon hiteles.', '2023-09-15'),
(23, 23, 123, 9, 'Romantikus és izgalmas.', '2023-09-20'),
(24, 24, 124, 8, 'Vicces, de néha túl sztereotipikus.', '2023-10-01'),
(25, 25, 125, 8, 'Érdekes karakterek.', '2023-10-12'),
(26, 26, 126, 8, 'Jó történelmi humor.', '2023-10-25'),
(27, 27, 127, 9, 'Lenyűgöző és elgondolkodtató.', '2023-11-01'),
(28, 28, 128, 9, 'Szokatlan nézőpont egy állatról.', '2023-11-10'),
(29, 29, 129, 9, 'Gyönyörű irodalmi adaptáció.', '2023-11-12'),
(30, 30, 130, 10, 'Tökéletes filmélmény.', '2023-11-15');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `registration_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password_hash`, `registration_date`) VALUES
(1, 'user01', 'user01@example.com', 'b01ce21ce5ac38191fcf0fb87c71f53756b2c57e57046f7320a463f1dba3e209', '2022-05-01'),
(2, 'user02', 'user02@example.com', '1321166e0e6eaa14148b1d5a47f4bdfabdabe11800a4579fbf3f77ba55e86517', '2022-06-15'),
(3, 'user03', 'user03@example.com', '433aa7f21428c6b75543cdb46caf31f6828e9a78d262dd5382eeb381c8c20787', '2022-07-20'),
(4, 'user04', 'user04@example.com', '054e2ac1e5b89caf7446f6a6fee681d2259ce871149e162885b4263e6d77c852', '2022-08-05'),
(5, 'user05', 'user05@example.com', '57a319ac89097ea47a14626564ba96f439bdeb213c31869603ec9efd4c612675', '2022-09-12'),
(6, 'user06', 'user06@example.com', 'f1e150a8730a3d23729b4ec12392cb122f81a91fdcce7039f28e0a93cbdef17a', '2022-10-01'),
(7, 'user07', 'user07@example.com', '7d8683503df3a53c54b6d9d18c9591ccb2369bd9c45739f93c34d274aa9dabae', '2022-11-15'),
(8, 'user08', 'user08@example.com', 'e5967501373f7fbf336de9f99cde06d4b8bcd0326d670b13c3ee771d23df0255', '2022-12-25'),
(9, 'user09', 'user09@example.com', 'bba783357187d99e3734fb568f1b901dfcb22050aa06a2cdef06e0531ee13712', '2023-01-10'),
(10, 'user10', 'user10@example.com', '9bf5fe02f00be85700e3e665f83ef4b933f37c032f935619e4d9474359d2ad11', '2023-02-14'),
(11, 'user11', 'user11@example.com', '8d95ce09bbdabd9d82a5397ca8768e11cee8c4d3240554939b75c0c847a65f57', '2023-03-05'),
(12, 'user12', 'user12@example.com', 'd697c6dc67eea5aa93b4e8888a8601216ba3b0958161c4e779d54bff61099df6', '2023-03-18'),
(13, 'user13', 'user13@example.com', '0c234922e5408c4af3af38288cb07f85c2fe8d354ec0d90bf722a3ca8fb320de', '2023-04-01'),
(14, 'user14', 'user14@example.com', 'ce8a146893cbda37ea920d3df3587022998a5f47a110d71af541497acbfa0f8b', '2023-04-15'),
(15, 'user15', 'user15@example.com', '21cdcf752bb5ec64d41290d244a85978b97a46b049cc7d068d380f0d6f619ea3', '2023-05-20'),
(16, 'user16', 'user16@example.com', 'acaf749ea2842470b1f8a226f3a58f235d24b6f0701cf4231f1c945f178ee694', '2023-06-10'),
(17, 'user17', 'user17@example.com', '770ed669934d123ade1232cb17d1bf060a03414145c01fa7c442054e3cbd6050', '2023-07-01'),
(18, 'user18', 'user18@example.com', '5695159addb2c5c3c56a27f1540e213da49e45dca0757c1dd5a4a4cbbc7a7ed5', '2023-08-25'),
(19, 'user19', 'user19@example.com', '6bd744fa602140f8002c41d6de7c3bae4019d89a3e79526afa9b5fd72205558d', '2023-09-05'),
(20, 'user20', 'user20@example.com', 'b9b63b2c402091bcb7cd8220cf7cd03fe9391d670a23106d0513743ab37a1ef6', '2023-10-15'),
(21, 'user21', 'user21@example.com', '5dd2df5864c4c28f0e41a7e6a5e063d4ace57aef6fa055cc66780f515c588ef1', '2023-10-30'),
(22, 'user22', 'user22@example.com', '149079bcec8dd9e73feda93f42c8d909366f28f795afa8169ad0e4416803d0a5', '2023-11-05'),
(23, 'user23', 'user23@example.com', '734b4ef493edb4e53e2df0b709f17c3b891ffe995167350b7d547b4fa57cd83b', '2023-11-10'),
(24, 'user24', 'user24@example.com', '9430f5556c8cc3982c63cb6762566e006fbc7e299b4255cfa569ecc1be7a0b06', '2023-11-12'),
(25, 'user25', 'user25@example.com', 'd18e22a2560dc2bac844c91bd868955cd5a3ea1d7a9c3504516d47f86313e6be', '2023-11-13'),
(26, 'user26', 'user26@example.com', '2a65a1338108372f2422eafefc8809ae516f2039665ab721d0ffc0343d6707ad', '2023-11-14'),
(27, 'user27', 'user27@example.com', 'c25da1436413639abc82c7891467ded58b86956ef793cc5ef72c6867b6d5e144', '2023-11-15'),
(28, 'user28', 'user28@example.com', '1bb36c13be34479f487d2d42d15b19ac3ba52f329461376aae7c5f516f4e346b', '2023-11-16'),
(29, 'user29', 'user29@example.com', '95eec19390b1ae11e0dfe6ae5548c92fb3da67f56fb4f01b731deb039c35ec3f', '2023-11-17'),
(30, 'user30', 'user30@example.com', 'e8a940d4972a7e645e4ea1d712ff1f0c79ec4243378c47e114ca71be2af568e9', '2023-11-18');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `directors`
--
ALTER TABLE `directors`
  ADD PRIMARY KEY (`director_id`);

--
-- Indexes for table `genres`
--
ALTER TABLE `genres`
  ADD PRIMARY KEY (`genre_id`);

--
-- Indexes for table `movies`
--
ALTER TABLE `movies`
  ADD PRIMARY KEY (`movie_id`),
  ADD KEY `fk_movies_genre` (`genre_id`);

--
-- Indexes for table `movie_actors`
--
ALTER TABLE `movie_actors`
  ADD PRIMARY KEY (`movie_actor_id`),
  ADD UNIQUE KEY `unique_movie` (`movie_id`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`rating_id`),
  ADD KEY `fk_ratings_user` (`user_id`),
  ADD KEY `fk_ratings_movie` (`movie_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `directors`
--
ALTER TABLE `directors`
  MODIFY `director_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
  MODIFY `genre_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `movies`
--
ALTER TABLE `movies`
  MODIFY `movie_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=134;

--
-- AUTO_INCREMENT for table `movie_actors`
--
ALTER TABLE `movie_actors`
  MODIFY `movie_actor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `rating_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `movies`
--
ALTER TABLE `movies`
  ADD CONSTRAINT `fk_movies_genre` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`genre_id`);

--
-- Constraints for table `movie_actors`
--
ALTER TABLE `movie_actors`
  ADD CONSTRAINT `fk_movie_actors_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`);

--
-- Constraints for table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `fk_ratings_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  ADD CONSTRAINT `fk_ratings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
