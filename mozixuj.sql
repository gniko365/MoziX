-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Dec 13, 2024 at 05:37 PM
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
FROM movies
WHERE movie_id = 103$$

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
(30, 'Kertész Mihály', 'kertesz_mihaly.jpg', '1886-12-24');

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
(15, 'Romantikus dráma');

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
  `Length` time(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movies`
--

INSERT INTO `movies` (`movie_id`, `release_year`, `description`, `director_id`, `genre_id`, `movie_name`, `Length`) VALUES
(101, 1948, 'Egy háború utáni történet gyermekekről, akik a túlélésért küzdenek.', 1, 1, 'Valahol Európában', NULL),
(102, 1981, 'Egy színész, aki kompromisszumokat köt a karrierjéért.', 2, 1, 'Mephisto', NULL),
(103, 1969, 'Egy férfi szürreális tapasztalatai a kommunista rezsim alatt.', 3, 2, 'A tanú', NULL),
(104, 1965, 'Egy katona humoros kalandjai a második világháború alatt.', 4, 3, 'Tizedes meg a többiek', NULL),
(105, 1971, 'Egy szerelmi történet a politikai elnyomás árnyékában.', 5, 1, 'Szerelem', NULL),
(106, 1986, 'Egy humoros animációs történet a macskák és egerek harcáról.', 6, 4, 'Macskafogó', NULL),
(107, 1981, 'Egy zenekar tagjainak küzdelmei a rendszer ellen.', 7, 5, 'Kopaszkutya', NULL),
(108, 1968, 'A magyar történelem egyik legnagyobb csatája.', 8, 6, 'Egri csillagok', NULL),
(109, 1976, 'Egy filozófiai dráma az emberi erkölcsről.', 9, 1, 'Az ötödik pecsét', NULL),
(110, 1967, 'Egy csoport katona története az orosz forradalom idején.', 10, 6, 'Csillagosok, katonák', NULL),
(111, 1979, 'Egy humoros történet egy falusi bakter életéről.', 11, 3, 'Indul a bakterház', NULL),
(112, 1931, 'Egy újgazdag család és az elegáns lakáj története.', 12, 3, 'Hyppolit, a lakáj', NULL),
(113, 1989, 'Két nővérek élete a 20. század elején.', 13, 1, 'Az én XX. századom', NULL),
(114, 2015, 'Egy fiatal nő szerelmi története, némi misztikummal.', 14, 4, 'Liza, a rókatündér', NULL),
(115, 2003, 'Egy metróellenőr mindennapjai és kihívásai.', 15, 1, 'Kontroll', NULL),
(116, 2006, 'Egy szürreális családi történet több generáción keresztül.', 16, 1, 'Taxidermia', NULL),
(117, 2002, 'Széchenyi István életének drámai ábrázolása.', 17, 6, 'Hídember', NULL),
(118, 2002, 'Egy falusi közösség csendes története a mindennapokról.', 18, 1, 'Hukkle', NULL),
(119, 1966, 'Egy fiú és az apja kapcsolata a múlt árnyékában.', 19, 1, 'Apa', NULL),
(120, 1969, 'Egy fiúcsapat barátságának története.', 20, 7, 'Pál utcai fiúk', NULL),
(121, 2001, 'A rendszerváltás idején játszódó fiatalos történet.', 21, 1, 'Moszkva tér', NULL),
(122, 2018, 'Egy nő túlélési története egy szovjet munkatáborban.', 22, 6, 'Örök tél', NULL),
(123, 2006, 'Az 1956-os forradalom története és annak hatásai.', 23, 6, 'Szabadság, szerelem', NULL),
(124, 1991, 'Egy humoros történet a rendszerváltás idejéről.', 24, 3, 'Csapd le csacsi!', NULL),
(125, 1984, 'Egy szürreális szerelmi történet.', 25, 1, 'Eszkimó asszony fázik', NULL),
(126, 2004, 'Egy humoros történet a magyar történelem jelentős eseményeiről.', 26, 3, 'Magyar vándor', NULL),
(127, 2017, 'Két lélek különös kapcsolata egy vágóhídon.', 27, 1, 'Testről és lélekről', NULL),
(128, 2014, 'Egy kóbor kutya története a modern társadalomban.', 28, 1, 'Fehér isten', NULL),
(129, 1918, 'Egy klasszikus Jókai Mór regény adaptációja.', 29, 1, 'Aranyember', NULL),
(130, 1993, 'Egy humoros és nosztalgikus történet az életről.', 30, 1, 'Sose halunk meg', NULL),
(150, 1948, 'Egy háború utáni történet gyermekekről, akik a túl...', 1, 1, 'Valahol Európában', '02:11:00.000000'),
(151, NULL, NULL, NULL, NULL, 'Valahol európában', '02:11:00.000000'),
(152, NULL, NULL, NULL, NULL, 'A Tanú', '02:11:00.000000'),
(153, NULL, NULL, NULL, NULL, 'A tanú', '02:11:00.000000'),
(154, NULL, NULL, NULL, NULL, 'A tanú', '02:00:00.000000'),
(155, NULL, NULL, NULL, NULL, 'A tanú', '02:00:00.000000'),
(156, 1969, 'Egy férfi szürreális tapasztalatai a kommunista re...', 3, 2, 'A tanú', '02:11:00.000000'),
(157, 1969, 'Egy férfi szürreális tapasztalatai a kommunista re...', NULL, NULL, 'A tanú', '02:11:00.000000'),
(158, 1969, 'Egy férfi szürreális tapasztalatai a kommunista re...', NULL, NULL, 'A tanú', '02:11:02.000000'),
(159, 1969, 'Egy férfi szürreális tapasztalatai a kommunista re...', NULL, NULL, 'A tanú', '02:11:44.000000');

-- --------------------------------------------------------

--
-- Table structure for table `movie_actors`
--

CREATE TABLE `movie_actors` (
  `movie_actor_id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `movie_id` int(11) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `actor_image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movie_actors`
--

INSERT INTO `movie_actors` (`movie_actor_id`, `name`, `movie_id`, `role`, `birth_date`, `actor_image`) VALUES
(1, 'Sinkovits Imre', 101, 'Simon Péter', '1928-09-21', 'sinkovits_imre.jpg'),
(2, 'Bánsági Ildikó', 102, 'Barbara', '1947-06-19', 'bansagi_ildiko.jpg'),
(3, 'Kállai Ferenc', 103, 'Pelikán József', '1925-07-04', 'kallai_ferenc.jpg'),
(4, 'Bodrogi Gyula', 104, 'Tizedes', '1934-04-15', 'bodrogi_gyula.jpg'),
(5, 'Darvas Iván', 105, 'János', '1925-06-14', 'darvas_ivan.jpg'),
(6, 'Mikó István', 106, 'Grabowski', '1947-11-24', 'miko_istvan.jpg'),
(7, 'Cserhalmi György', 107, 'Kokó', '1948-02-17', 'cserhalmi_gyorgy.jpg'),
(8, 'Sinkovits Imre', 108, 'Dobó István', '1928-09-21', 'sinkovits_imre.jpg'),
(9, 'Latinovits Zoltán', 109, 'Kardos', '1931-09-09', 'latinovits_zoltan.jpg'),
(10, 'Hernádi Judit', 110, 'Ági', '1956-04-11', 'hernadi_judit.jpg'),
(11, 'Kovács Kati', 111, 'Mariska', '1944-10-25', 'kovacs_kati.jpg'),
(12, 'Kabos Gyula', 112, 'Hyppolit', '1887-03-19', 'kabos_gyula.jpg'),
(13, 'Béres Ilona', 113, 'Dóra', '1942-06-04', 'beres_ilona.jpg'),
(14, 'Balsai Móni', 114, 'Liza', '1977-12-13', 'balsai_moni.jpg'),
(15, 'Csányi Sándor', 115, 'Bulcsú', '1975-12-19', 'csanyi_sandor.jpg'),
(16, 'Trill Zsolt', 116, 'Kálmán', '1971-03-22', 'trill_zsolt.jpg'),
(17, 'Czapkó Antal', 117, 'Széchenyi', '1978-05-06', 'czapko_antal.jpg'),
(18, 'Pogány Judit', 118, 'Néni', '1944-07-10', 'pogany_judit.jpg'),
(19, 'Garas Dezső', 119, 'Apa', '1934-12-09', 'garas_dezso.jpg'),
(20, 'Törőcsik Mari', 120, 'Éva', '1935-11-23', 'torocsik_mari.jpg'),
(21, 'Nagy Zsolt', 121, 'Peti', '1974-10-28', 'nagy_zsolt.jpg'),
(22, 'Gera Marina', 122, 'Anna', '1984-07-17', 'gera_marina.jpg'),
(23, 'Dobó Kata', 123, 'Juli', '1974-02-25', 'dobo_kata.jpg'),
(24, 'Eperjes Károly', 124, 'Pista', '1954-02-17', 'eperjes_karoly.jpg'),
(25, 'Eszenyi Enikő', 125, 'Margit', '1961-01-11', 'eszenyi_eniko.jpg'),
(26, 'Rudolf Péter', 126, 'Főszereplő', '1959-10-15', 'rudolf_peter.jpg'),
(27, 'Morcsányi Géza', 127, 'Endre', '1952-09-28', 'morcsanyi_geza.jpg'),
(28, 'Nagy Marcell', 128, 'Hagen', '2004-03-11', 'nagy_marcell.jpg'),
(29, 'Jászai Mari', 129, 'Noémi', '1850-02-24', 'jaszai_mari.jpg'),
(30, 'Koltai Róbert', 130, 'Gyuszi', '1943-12-16', 'koltai_robert.jpg');

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
(1, 'user01', 'user01@example.com', 'hashed_password_01', '2022-05-01'),
(2, 'user02', 'user02@example.com', 'hashed_password_02', '2022-06-15'),
(3, 'user03', 'user03@example.com', 'hashed_password_03', '2022-07-20'),
(4, 'user04', 'user04@example.com', 'hashed_password_04', '2022-08-05'),
(5, 'user05', 'user05@example.com', 'hashed_password_05', '2022-09-12'),
(6, 'user06', 'user06@example.com', 'hashed_password_06', '2022-10-01'),
(7, 'user07', 'user07@example.com', 'hashed_password_07', '2022-11-15'),
(8, 'user08', 'user08@example.com', 'hashed_password_08', '2022-12-25'),
(9, 'user09', 'user09@example.com', 'hashed_password_09', '2023-01-10'),
(10, 'user10', 'user10@example.com', 'hashed_password_10', '2023-02-14'),
(11, 'user11', 'user11@example.com', 'hashed_password_11', '2023-03-05'),
(12, 'user12', 'user12@example.com', 'hashed_password_12', '2023-03-18'),
(13, 'user13', 'user13@example.com', 'hashed_password_13', '2023-04-01'),
(14, 'user14', 'user14@example.com', 'hashed_password_14', '2023-04-15'),
(15, 'user15', 'user15@example.com', 'hashed_password_15', '2023-05-20'),
(16, 'user16', 'user16@example.com', 'hashed_password_16', '2023-06-10'),
(17, 'user17', 'user17@example.com', 'hashed_password_17', '2023-07-01'),
(18, 'user18', 'user18@example.com', 'hashed_password_18', '2023-08-25'),
(19, 'user19', 'user19@example.com', 'hashed_password_19', '2023-09-05'),
(20, 'user20', 'user20@example.com', 'hashed_password_20', '2023-10-15'),
(21, 'user21', 'user21@example.com', 'hashed_password_21', '2023-10-30'),
(22, 'user22', 'user22@example.com', 'hashed_password_22', '2023-11-05'),
(23, 'user23', 'user23@example.com', 'hashed_password_23', '2023-11-10'),
(24, 'user24', 'user24@example.com', 'hashed_password_24', '2023-11-12'),
(25, 'user25', 'user25@example.com', 'hashed_password_25', '2023-11-13'),
(26, 'user26', 'user26@example.com', 'hashed_password_26', '2023-11-14'),
(27, 'user27', 'user27@example.com', 'hashed_password_27', '2023-11-15'),
(28, 'user28', 'user28@example.com', 'hashed_password_28', '2023-11-16'),
(29, 'user29', 'user29@example.com', 'hashed_password_29', '2023-11-17'),
(30, 'user30', 'user30@example.com', 'hashed_password_30', '2023-11-18');

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
  MODIFY `director_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
  MODIFY `genre_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `movies`
--
ALTER TABLE `movies`
  MODIFY `movie_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=160;

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
