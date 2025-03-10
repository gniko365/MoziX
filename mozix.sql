-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Mar 10, 2025 at 09:22 PM
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
CREATE DEFINER=`root`@`localhost` PROCEDURE `AddRating` (IN `p_user_id` INT, IN `p_movie_id` INT, IN `p_rating` INT, IN `p_review` VARCHAR(255))   BEGIN
    INSERT INTO ratings (user_id, movie_id, rating, review, rating_date)
    VALUES (p_user_id, p_movie_id, p_rating, p_review, NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CreateAdmin` (IN `p_username` VARCHAR(255), IN `p_email` VARCHAR(255), IN `p_password` VARCHAR(255))   BEGIN
    INSERT INTO users (username, email, password, role, registration_date)
    VALUES (p_username, p_email, SHA2(p_password, 512), 'admin', NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `CreateDirector` (IN `p_name` VARCHAR(100), IN `p_director_image` VARCHAR(255), IN `p_birth_date` DATE)   BEGIN
    INSERT INTO Directors (name, director_image, birth_date)
    VALUES (p_name, p_director_image, p_birth_date);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteDirector` (IN `p_director_id` INT)   BEGIN
    DELETE FROM Directors WHERE director_id = p_director_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `everyActorInAMovie` ()   SELECT movie_id, COUNT(*) AS actor_count
FROM movie_actors
GROUP BY movie_id
ORDER BY actor_count DESC$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `filmDetails` ()   SELECT 
    movie_id, 
    movie_name, 
    release_year, 
    description, 
    (SELECT name FROM directors WHERE director_id = movies.director_id) AS director_name, 
    (SELECT name FROM genres WHERE genre_id = movies.genre_id) AS genre_name
FROM movies$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `genreNames` ()   select name
from genres$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetActorsByMovie` (IN `movie_title` VARCHAR(255))   BEGIN
    SELECT a.actor_id, a.name AS actor_name, a.birth_date, a.actor_image
    FROM actors a
    JOIN movie_actors ma ON a.actor_id = ma.actor_id
    JOIN movies m ON ma.movie_id = m.movie_id
    WHERE m.movie_name = movie_title;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllActors` ()   BEGIN
    SELECT * FROM actors;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllDirectors` ()   BEGIN
    SELECT * FROM Directors;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllMoviesDetails` ()   BEGIN
    SELECT 
        m.movie_id,
        m.movie_name,
        m.release_year,
        m.description,
        GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', ') AS genres,
        GROUP_CONCAT(DISTINCT d.name ORDER BY d.name SEPARATOR ', ') AS directors,
        GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', ') AS actors
    FROM movies m
    LEFT JOIN movie_genres mg ON m.movie_id = mg.movie_id
    LEFT JOIN genres g ON mg.genre_id = g.genre_id
    LEFT JOIN movie_directors md ON m.movie_id = md.movie_id
    LEFT JOIN directors d ON md.director_id = d.director_id
    LEFT JOIN movie_actors ma ON m.movie_id = ma.movie_id
    LEFT JOIN actors a ON ma.actor_id = a.actor_id
    GROUP BY m.movie_id, m.movie_name, m.release_year, m.description;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllRatings` ()   BEGIN
    SELECT 
        r.rating_id,
        u.username AS reviewer,
        m.movie_name,
        r.rating,
        r.review,
        r.rating_date
    FROM ratings r
    JOIN users u ON r.user_id = u.user_id
    JOIN movies m ON r.movie_id = m.movie_id
    ORDER BY r.rating_date DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetAllUsers` ()   BEGIN
    SELECT * FROM users;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetDirectorById` (IN `p_director_id` INT)   BEGIN
    SELECT * FROM Directors WHERE director_id = p_director_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMoviesByActor` (IN `actor_name` VARCHAR(255))   BEGIN
    SELECT m.movie_name, m.release_year
    FROM movies m
    JOIN movie_actors ma ON m.movie_id = ma.movie_id
    JOIN actors a ON ma.actor_id = a.actor_id
    WHERE a.name LIKE CONCAT('%', actor_name, '%');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetMoviesByGenre` (IN `genre_name` VARCHAR(255))   BEGIN
    SELECT m.movie_id, m.movie_name, g.name
    FROM movies m
    JOIN movie_genres mg ON m.movie_id = mg.movie_id
    JOIN genres g ON mg.genre_id = g.genre_id
    WHERE g.name LIKE genre_name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `GetRatingsByMovie` (IN `movie_title` VARCHAR(255))   BEGIN
    SELECT 
        r.rating_id,
        u.username AS reviewer,
        r.rating,
        r.review,
        r.rating_date
    FROM ratings r
    JOIN users u ON r.user_id = u.user_id
    JOIN movies m ON r.movie_id = m.movie_id
    WHERE m.movie_name = movie_title
    ORDER BY r.rating_date DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (IN `emailIN` VARCHAR(255), IN `passwordIN` VARCHAR(255))   BEGIN
    SELECT * FROM users WHERE email = emailIN AND password = passwordIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `movieTitles` ()   SELECT movie_name
FROM movies$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `register_user` (IN `p_username` VARCHAR(255), IN `p_email` VARCHAR(255), IN `p_password` VARCHAR(255))   BEGIN
    INSERT INTO users (username, email, password, registration_date, role)
    VALUES (p_username, p_email, p_password, CURDATE(), 'user');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `SearchUsersByName` (IN `search_term` VARCHAR(255))   BEGIN
    SELECT user_id, username, email, registration_date, role
    FROM users
    WHERE username LIKE CONCAT('%', search_term, '%');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `UpdateDirector` (IN `p_director_id` INT, IN `p_name` VARCHAR(100), IN `p_director_image` VARCHAR(255), IN `p_birth_date` DATE)   BEGIN
    UPDATE Directors
    SET name = p_name,
        director_image = p_director_image,
        birth_date = p_birth_date
    WHERE director_id = p_director_id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `actors`
--

CREATE TABLE `actors` (
  `actor_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `birth_date` date DEFAULT NULL,
  `actor_image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `actors`
--

INSERT INTO `actors` (`actor_id`, `name`, `birth_date`, `actor_image`) VALUES
(1, 'Sinkovits Imre', '1928-09-21', 'sinkovits_imre.jpg'),
(2, 'Bánsági Ildikó', '1947-06-19', 'bansagi_ildiko.jpg'),
(3, 'Kállai Ferenc', '1925-07-04', 'kallai_ferenc.jpg'),
(4, 'Bodrogi Gyula', '1934-04-15', 'bodrogi_gyula.jpg'),
(5, 'Darvas Iván', '1925-06-14', 'darvas_ivan.jpg'),
(6, 'Mikó István', '1947-11-24', 'miko_istvan.jpg'),
(7, 'Cserhalmi György', '1948-02-17', 'cserhalmi_gyorgy.jpg'),
(8, 'Latinovits Zoltán', '1931-09-09', 'latinovits_zoltan.jpg'),
(9, 'Hernádi Judit', '1956-04-11', 'hernadi_judit.jpg'),
(10, 'Kovács Kati', '1944-10-25', 'kovacs_kati.jpg'),
(11, 'Kabos Gyula', '1887-03-19', 'kabos_gyula.jpg'),
(12, 'Béres Ilona', '1942-06-04', 'beres_ilona.jpg'),
(13, 'Balsai Móni', '1977-12-13', 'balsai_moni.jpg'),
(14, 'Csányi Sándor', '1975-12-19', 'csanyi_sandor.jpg'),
(15, 'Trill Zsolt', '1971-03-22', 'trill_zsolt.jpg'),
(16, 'Czapkó Antal', '1978-05-06', 'czapko_antal.jpg'),
(17, 'Pogány Judit', '1944-07-10', 'pogany_judit.jpg'),
(18, 'Garas Dezső', '1934-12-09', 'garas_dezso.jpg'),
(19, 'Törőcsik Mari', '1935-11-23', 'torocsik_mari.jpg'),
(20, 'Nagy Zsolt', '1974-10-28', 'nagy_zsolt.jpg'),
(21, 'Gera Marina', '1984-07-17', 'gera_marina.jpg'),
(22, 'Dobó Kata', '1974-02-25', 'dobo_kata.jpg'),
(23, 'Eperjes Károly', '1954-02-17', 'eperjes_karoly.jpg'),
(24, 'Eszenyi Enikő', '1961-01-11', 'eszenyi_eniko.jpg'),
(25, 'Rudolf Péter', '1959-10-15', 'rudolf_peter.jpg'),
(26, 'Morcsányi Géza', '1952-09-28', 'morcsanyi_geza.jpg'),
(27, 'Nagy Marcell', '2004-03-11', 'nagy_marcell.jpg'),
(28, 'Jászai Mari', '1850-02-24', 'jaszai_mari.jpg'),
(29, 'Koltai Róbert', '1943-12-16', 'koltai_robert.jpg'),
(30, 'Somlay Artúr', '1883-02-28', 'somlay_artur.jpg'),
(31, 'Gábor Miklós', '1919-04-07', 'gabor_miklos.jpg'),
(32, 'Bánky Zsuzsa', '1921-10-19', 'banky_zsuzsa.jpg'),
(33, 'Rónay Ábrahám', '1931-01-01', 'ronay_abraham.jpg'),
(34, 'Bárdy György', '1921-05-26', 'bardy_gyorgy.jpg'),
(35, 'Rozsos István', '1930-06-10', 'rozsos_istvan.jpg'),
(36, 'Klaus Maria Brandauer', '1943-06-22', 'klaus_maria_brandauer.jpg'),
(37, 'Krystyna Janda', '1952-12-18', 'krystyna_janda.jpg'),
(38, 'Rolf Hoppe', '1930-12-06', 'rolf_hoppe.jpg'),
(39, 'Both Béla', '1912-06-01', 'both_bela.jpg'),
(40, 'Őze Lajos', '1935-04-27', 'oze_lajos.jpg'),
(41, 'Major Tamás', '1910-01-26', 'major_tamas.jpg'),
(42, 'Márkus László', '1927-06-10', 'markus_laszlo.jpg'),
(43, 'Darvas Lili', '1902-04-10', 'darvas_lili.jpg'),
(44, 'Schuster Lóránt', '1950-07-20', 'schuster_lorant.jpg'),
(45, 'Földes László (Hobo)', '1945-02-13', 'foldes_laszlo_hobo.jpg'),
(46, 'Deák Bill Gyula', '1948-11-08', 'deak_bill_gyula.jpg'),
(47, 'Póka Egon', '1953-06-19', 'poka_egon.jpg'),
(48, 'Venczel Vera', '1946-03-10', 'venczel_vera.jpg'),
(49, 'Kovács István', '1944-06-27', 'kovacs_istvan.jpg'),
(50, 'Horváth Sándor', '1941-06-14', 'horvath_sandor.jpg'),
(51, 'Bencze Ferenc', '1912-07-04', 'bencze_ferenc.jpg'),
(52, 'Jácint Juhász', '1944-11-28', 'jacint_juhasz.jpg'),
(53, 'Kozák András', '1943-02-23', 'kozak_andras.jpg'),
(54, 'Olvasztó Imre', '1967-01-01', 'olvaszto_imre.jpg'),
(55, 'Haumann Péter', '1941-05-17', 'haumann_peter.jpg'),
(56, 'Horváth Teri', '1925-08-27', 'horvath_teri.jpg'),
(57, 'Pécsi Ildikó', '1940-05-21', 'pecsi_ildiko.jpg');

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
(43, 'Nemes Jeles László', 'nemes_jeles_laszlo.jpg', '1977-02-18'),
(44, 'Tarr Béla', 'tarr_bela.jpg', '1955-07-21'),
(47, 'Gigor Attila', 'gigor_attila.jpg', '1978-04-05'),
(48, 'Gábor Pál', 'gabor_pal.jpg', '1932-11-02'),
(49, 'Bergendy Péter', 'bergendy_peter.jpg', '1969-04-04'),
(50, 'Hajdu Szabolcs', 'hajdu_szabolcs.jpg', '1972-04-26'),
(51, 'Jiří Menzel', 'jiri_menzel.jpg', '1938-02-23'),
(52, 'Bodzsár Márk', 'bodzsar_mark.jpg', '1983-06-16'),
(53, 'Szabó Virág', 'szabo_virag.jpg', '1985-10-04');

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
(9, 'Sci-fi'),
(10, 'Horror'),
(11, 'Thriller'),
(12, 'Kaland'),
(13, 'Fantasztikus'),
(14, 'Dokumentumfilm'),
(15, 'Romantikus dráma'),
(16, 'vígjáték-dráma horror'),
(17, 'vígjáték-dráma'),
(18, 'művészfilm'),
(19, 'Háborús'),
(20, 'Politikai vígjáték'),
(21, 'Krimi'),
(22, 'Western');

-- --------------------------------------------------------

--
-- Table structure for table `movies`
--

CREATE TABLE `movies` (
  `movie_id` int(11) NOT NULL,
  `release_year` int(11) DEFAULT NULL,
  `description` text,
  `movie_name` varchar(99) NOT NULL,
  `Length` int(11) DEFAULT NULL,
  `cover` varchar(255) DEFAULT NULL,
  `trailer_link` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movies`
--

INSERT INTO `movies` (`movie_id`, `release_year`, `description`, `movie_name`, `Length`, `cover`, `trailer_link`) VALUES
(101, 1948, 'Egy háború utáni történet gyermekekről, akik a túlélésért küzdenek.', 'Valahol Európában', 100, NULL, 'https://www.youtube.com/watch?v=gwhE9A6Pzso'),
(102, 1981, 'Egy színész, aki kompromisszumokat köt a karrierjéért.', 'Mephisto', 144, NULL, 'https://www.youtube.com/watch?v=EbpCuStwXz4'),
(103, 1969, 'Egy férfi szürreális tapasztalatai a kommunista rezsim alatt.', 'A tanú', 110, NULL, 'https://www.youtube.com/watch?v=B696W2Gwvmk'),
(104, 1965, 'Egy katona humoros kalandjai a második világháború alatt.', 'Tizedes meg a többiek', 109, NULL, 'https://www.youtube.com/watch?v=8bhb4eeJB7o'),
(105, 1971, 'Egy szerelmi történet a politikai elnyomás árnyékában.', 'Szerelem', 96, NULL, 'https://www.youtube.com/watch?v=lgkicEevbSA'),
(106, 1986, 'Egy humoros animációs történet a macskák és egerek harcáról.', 'Macskafogó', 96, NULL, 'https://www.youtube.com/watch?v=6WJxaSfAFXY'),
(107, 1981, 'Egy zenekar tagjainak küzdelmei a rendszer ellen.', 'Kopaszkutya', 98, NULL, 'https://www.youtube.com/watch?v=RVRiEwmwNd8'),
(108, 1968, 'A magyar történelem egyik legnagyobb csatája.', 'Egri csillagok', 120, NULL, 'https://www.youtube.com/watch?v=04CZI0A0Vgw'),
(109, 1976, 'Egy filozófiai dráma az emberi erkölcsről.', 'Az ötödik pecsét', 102, NULL, 'https://www.youtube.com/watch?v=d8STQElOASA'),
(110, 1967, 'Egy csoport katona története az orosz forradalom idején.', 'Csillagosok, katonák', 94, NULL, 'https://www.youtube.com/watch?v=xTnJ74KeTfs'),
(111, 1979, 'Egy humoros történet egy falusi bakter életéről.', 'Indul a bakterház', 85, NULL, NULL),
(112, 1931, 'Egy újgazdag család és az elegáns lakáj története.', 'Hyppolit, a lakáj', 90, NULL, NULL),
(113, 1989, 'Két nővérek élete a 20. század elején.', 'Az én XX. századom', 95, NULL, NULL),
(114, 2015, 'Egy fiatal nő szerelmi története, némi misztikummal.', 'Liza, a rókatündér', 95, NULL, NULL),
(115, 2003, 'Egy metróellenőr mindennapjai és kihívásai.', 'Kontroll', 105, NULL, NULL),
(116, 2006, 'Egy szürreális családi történet több generáción keresztül.', 'Taxidermia', 91, NULL, NULL),
(117, 2002, 'Széchenyi István életének drámai ábrázolása.', 'Hídember', 100, NULL, NULL),
(118, 2002, 'Egy falusi közösség csendes története a mindennapokról.', 'Hukkle', 75, NULL, NULL),
(119, 1966, 'Egy fiú és az apja kapcsolata a múlt árnyékában.', 'Apa', 90, NULL, NULL),
(120, 1969, 'Egy fiúcsapat barátságának története.', 'Pál utcai fiúk', 95, NULL, NULL),
(121, 2001, 'A rendszerváltás idején játszódó fiatalos történet.', 'Moszkva tér', 101, NULL, NULL),
(122, 2018, 'Egy nő túlélési története egy szovjet munkatáborban.', 'Örök tél', 98, NULL, NULL),
(123, 2006, 'Az 1956-os forradalom története és annak hatásai.', 'Szabadság, szerelem', 103, NULL, NULL),
(124, 1991, 'Egy humoros történet a rendszerváltás idejéről.', 'Csapd le csacsi!', 95, NULL, NULL),
(125, 1984, 'Egy szürreális szerelmi történet.', 'Eszkimó asszony fázik', 92, NULL, NULL),
(126, 2004, 'Egy humoros történet a magyar történelem jelentős eseményeiről.', 'Magyar vándor', 116, NULL, NULL),
(127, 2017, 'Két lélek különös kapcsolata egy vágóhídon.', 'Testről és lélekről', 116, NULL, NULL),
(128, 2014, 'Egy kóbor kutya története a modern társadalomban.', 'Fehér isten', 119, NULL, NULL),
(129, 1918, 'Egy klasszikus Jókai Mór regény adaptációja.', 'Aranyember', 98, NULL, NULL),
(130, 1993, 'Egy humoros és nosztalgikus történet az életről.', 'Sose halunk meg', 107, NULL, NULL),
(131, 2013, 'A film középpontjában álló ikerpárt édesanyjuk egy határszéli faluba küldi nagymamájukhoz, hogy ott vészeljék át a háború végét.', 'A Nagy Füzet', 109, NULL, NULL),
(132, 2014, 'Szentesi Áron egy 20-as évei végén járó budapesti fiú, aki munkanélküliként éli mindennapjait, de az egyik nap barátnője, Eszter elhagyja.', 'VAN valami furcsa és megmagyarázhatatlan', 90, NULL, NULL),
(133, 2015, '1944. október 7-8-án játszódik Auschwitz-Birkenauban a Sonderkommandók lázadása idején.', 'Saul fia', 107, NULL, NULL),
(134, 2000, 'Egy kisváros lakóit egy vándorcirkusz tartja félelemben.', 'Werckmeister harmóniák', 145, NULL, NULL),
(135, 2011, 'Egy öreg paraszt és lánya monoton, sötét hétköznapjait követjük.', 'A torinói ló', 146, NULL, NULL),
(136, 2016, 'Egy sorozatgyilkos tartja rettegésben az 1950-es évek végének Magyarországát.', 'A martfűi rém', 121, NULL, NULL),
(137, 1978, 'Egy fiatal ápolónő szembesül a rendszer manipulációjával és saját erkölcsi dilemmáival.', 'Angi Vera', 96, NULL, NULL),
(138, 2016, 'Egy benzinkútnál összetalálkozik egy öreg benzinkutas és egy fiatal fiú egy veszélyes helyzettel.', 'Kút', 95, NULL, NULL),
(139, 2012, 'Egy írónő és a házvezetőnője közötti titokzatos kapcsolat története.', 'Az ajtó', 98, NULL, NULL),
(140, 2008, 'Egy patológus egy gyilkossági ügyben válik kulcsszereplővé.', 'A nyomozó', 107, NULL, NULL),
(141, 2007, 'Egy vasúti őr tanúja lesz egy bűnténynek és a pénz csábításának.', 'A londoni férfi', 132, NULL, NULL),
(142, 2014, 'Egy afrikai focista Magyarországra kerül, ahol rabszolgaságba esik.', 'Délibáb', 91, NULL, NULL),
(143, 2011, 'Az 1950-es években egy fiatal titkos ügynök próbára van téve.', 'A vizsga', 89, NULL, NULL),
(144, 1985, 'Egy kis falu lakóinak mindennapjait bemutató történet.', 'Az én kis falum', 98, NULL, NULL),
(145, 1999, 'Egy magyar mentalista segít a rendőrségnek egy rejtélyes ügyben.', 'Simon mágus', 100, NULL, NULL),
(146, 1985, 'Egy baráti társaság életének és álmainak története.', 'A nagy generáció', 97, NULL, NULL),
(147, 2008, 'Egy szélhámos kiforgatja a gazdag nőket a vagyonukból.', 'Kaméleon', 104, NULL, NULL),
(148, 2006, 'Egy tornász élete a fegyelemről és a múlt árnyékairól.', 'Fehér tenyér', 100, NULL, NULL),
(149, 2013, 'Egy mentős különös módon pénzt keres a halottakkal.', 'Isteni műszak', 100, NULL, NULL),
(150, 2019, 'Egy szélhámos menekülés közben egy özvegy életébe csöppen.', 'Apró mesék', 112, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `movie_actors`
--

CREATE TABLE `movie_actors` (
  `movie_id` int(11) NOT NULL,
  `actor_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movie_actors`
--

INSERT INTO `movie_actors` (`movie_id`, `actor_id`) VALUES
(101, 30),
(101, 31),
(101, 32),
(101, 33),
(101, 34),
(101, 35),
(102, 2),
(102, 7),
(102, 36),
(102, 37),
(102, 38),
(103, 3),
(103, 39),
(103, 40),
(104, 1),
(104, 5),
(104, 41),
(104, 42),
(105, 5),
(105, 19),
(105, 43),
(107, 44),
(107, 45),
(107, 46),
(107, 47),
(108, 1),
(108, 48),
(108, 49),
(109, 8),
(109, 40),
(109, 42),
(109, 50),
(109, 51),
(110, 52),
(110, 53),
(111, 29),
(111, 54),
(111, 55),
(111, 56),
(111, 57);

-- --------------------------------------------------------

--
-- Table structure for table `movie_directors`
--

CREATE TABLE `movie_directors` (
  `movie_id` int(11) NOT NULL,
  `director_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movie_directors`
--

INSERT INTO `movie_directors` (`movie_id`, `director_id`) VALUES
(101, 1),
(102, 2),
(119, 2),
(139, 2),
(103, 3),
(104, 4),
(105, 5),
(106, 6),
(107, 7),
(108, 8),
(109, 9),
(120, 9),
(110, 10),
(146, 12),
(113, 13),
(127, 13),
(145, 13),
(114, 14),
(115, 15),
(116, 16),
(118, 16),
(117, 17),
(121, 20),
(136, 22),
(122, 31),
(150, 31),
(123, 32),
(147, 32),
(124, 33),
(125, 34),
(126, 35),
(128, 36),
(129, 37),
(130, 38),
(111, 39),
(112, 40),
(131, 41),
(132, 42),
(133, 43),
(134, 44),
(135, 44),
(141, 44),
(138, 47),
(140, 47),
(137, 48),
(143, 49),
(142, 50),
(148, 50),
(144, 51),
(149, 52);

-- --------------------------------------------------------

--
-- Table structure for table `movie_genres`
--

CREATE TABLE `movie_genres` (
  `movie_id` int(11) NOT NULL,
  `genre_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `movie_genres`
--

INSERT INTO `movie_genres` (`movie_id`, `genre_id`) VALUES
(101, 1),
(102, 1),
(105, 1),
(107, 1),
(108, 1),
(109, 1),
(110, 1),
(113, 1),
(115, 1),
(116, 1),
(117, 1),
(118, 1),
(119, 1),
(120, 1),
(121, 1),
(122, 1),
(123, 1),
(125, 1),
(127, 1),
(128, 1),
(129, 1),
(130, 1),
(131, 1),
(132, 1),
(133, 1),
(134, 1),
(135, 1),
(136, 1),
(137, 1),
(138, 1),
(139, 1),
(140, 1),
(141, 1),
(142, 1),
(143, 1),
(145, 1),
(146, 1),
(147, 1),
(148, 1),
(150, 1),
(103, 2),
(124, 2),
(102, 3),
(104, 3),
(111, 3),
(112, 3),
(114, 3),
(116, 3),
(121, 3),
(124, 3),
(126, 3),
(130, 3),
(132, 3),
(140, 3),
(144, 3),
(149, 3),
(106, 4),
(101, 5),
(107, 5),
(103, 6),
(108, 6),
(110, 6),
(115, 6),
(117, 6),
(122, 6),
(123, 6),
(126, 6),
(147, 6),
(120, 7),
(106, 8),
(113, 9),
(113, 10),
(128, 11),
(136, 11),
(138, 11),
(141, 11),
(143, 11),
(106, 12),
(108, 12),
(120, 12),
(114, 13),
(116, 13),
(107, 14),
(114, 15),
(127, 15),
(129, 15),
(137, 15),
(101, 19),
(104, 19),
(108, 19),
(109, 19),
(110, 19),
(131, 19),
(133, 19),
(103, 20),
(118, 21),
(136, 21),
(140, 21),
(141, 21),
(142, 22);

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
(30, 30, 130, 10, 'Tökéletes filmélmény.', '2023-11-15'),
(31, 20, 101, 3, 'Nem tetszett >:(', '2025-02-27'),
(32, 10, 114, 8, 'tetszi', '2025-02-27'),
(33, 52, 101, 5, 'hatalmas', '2025-03-04'),
(34, 52, 101, 5, 'Great movie!', '2025-03-04'),
(35, 52, 120, 11, 'Great movie!', '2025-03-04'),
(36, 52, 112, 5, 'ennyire tetszett', '2025-03-04');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `registration_date` date DEFAULT NULL,
  `role` enum('user','admin') NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `password`, `registration_date`, `role`) VALUES
(1, 'user01', 'user01@example.com', 'b01ce21ce5ac38191fcf0fb87c71f53756b2c57e57046f7320a463f1dba3e209', '2022-05-01', 'user'),
(2, 'user02', 'user02@example.com', '1321166e0e6eaa14148b1d5a47f4bdfabdabe11800a4579fbf3f77ba55e86517', '2022-06-15', 'user'),
(3, 'user03', 'user03@example.com', '433aa7f21428c6b75543cdb46caf31f6828e9a78d262dd5382eeb381c8c20787', '2022-07-20', 'user'),
(4, 'user04', 'user04@example.com', '054e2ac1e5b89caf7446f6a6fee681d2259ce871149e162885b4263e6d77c852', '2022-08-05', 'user'),
(5, 'user05', 'user05@example.com', '57a319ac89097ea47a14626564ba96f439bdeb213c31869603ec9efd4c612675', '2022-09-12', 'user'),
(6, 'user06', 'user06@example.com', 'f1e150a8730a3d23729b4ec12392cb122f81a91fdcce7039f28e0a93cbdef17a', '2022-10-01', 'user'),
(7, 'user07', 'user07@example.com', '7d8683503df3a53c54b6d9d18c9591ccb2369bd9c45739f93c34d274aa9dabae', '2022-11-15', 'user'),
(8, 'user08', 'user08@example.com', 'e5967501373f7fbf336de9f99cde06d4b8bcd0326d670b13c3ee771d23df0255', '2022-12-25', 'user'),
(9, 'user09', 'user09@example.com', 'bba783357187d99e3734fb568f1b901dfcb22050aa06a2cdef06e0531ee13712', '2023-01-10', 'user'),
(10, 'user10', 'user10@example.com', '9bf5fe02f00be85700e3e665f83ef4b933f37c032f935619e4d9474359d2ad11', '2023-02-14', 'user'),
(11, 'user11', 'user11@example.com', '8d95ce09bbdabd9d82a5397ca8768e11cee8c4d3240554939b75c0c847a65f57', '2023-03-05', 'user'),
(12, 'user12', 'user12@example.com', 'd697c6dc67eea5aa93b4e8888a8601216ba3b0958161c4e779d54bff61099df6', '2023-03-18', 'user'),
(13, 'user13', 'user13@example.com', '0c234922e5408c4af3af38288cb07f85c2fe8d354ec0d90bf722a3ca8fb320de', '2023-04-01', 'user'),
(14, 'user14', 'user14@example.com', 'ce8a146893cbda37ea920d3df3587022998a5f47a110d71af541497acbfa0f8b', '2023-04-15', 'user'),
(15, 'user15', 'user15@example.com', '21cdcf752bb5ec64d41290d244a85978b97a46b049cc7d068d380f0d6f619ea3', '2023-05-20', 'user'),
(16, 'user16', 'user16@example.com', 'acaf749ea2842470b1f8a226f3a58f235d24b6f0701cf4231f1c945f178ee694', '2023-06-10', 'user'),
(17, 'user17', 'user17@example.com', '770ed669934d123ade1232cb17d1bf060a03414145c01fa7c442054e3cbd6050', '2023-07-01', 'user'),
(18, 'user18', 'user18@example.com', '5695159addb2c5c3c56a27f1540e213da49e45dca0757c1dd5a4a4cbbc7a7ed5', '2023-08-25', 'user'),
(19, 'user19', 'user19@example.com', '6bd744fa602140f8002c41d6de7c3bae4019d89a3e79526afa9b5fd72205558d', '2023-09-05', 'user'),
(20, 'user20', 'user20@example.com', 'b9b63b2c402091bcb7cd8220cf7cd03fe9391d670a23106d0513743ab37a1ef6', '2023-10-15', 'user'),
(21, 'user21', 'user21@example.com', '5dd2df5864c4c28f0e41a7e6a5e063d4ace57aef6fa055cc66780f515c588ef1', '2023-10-30', 'user'),
(22, 'user22', 'user22@example.com', '149079bcec8dd9e73feda93f42c8d909366f28f795afa8169ad0e4416803d0a5', '2023-11-05', 'user'),
(23, 'user23', 'user23@example.com', '734b4ef493edb4e53e2df0b709f17c3b891ffe995167350b7d547b4fa57cd83b', '2023-11-10', 'user'),
(24, 'user24', 'user24@example.com', '9430f5556c8cc3982c63cb6762566e006fbc7e299b4255cfa569ecc1be7a0b06', '2023-11-12', 'user'),
(25, 'user25', 'user25@example.com', 'd18e22a2560dc2bac844c91bd868955cd5a3ea1d7a9c3504516d47f86313e6be', '2023-11-13', 'user'),
(26, 'user26', 'user26@example.com', '2a65a1338108372f2422eafefc8809ae516f2039665ab721d0ffc0343d6707ad', '2023-11-14', 'user'),
(27, 'user27', 'user27@example.com', 'c25da1436413639abc82c7891467ded58b86956ef793cc5ef72c6867b6d5e144', '2023-11-15', 'user'),
(28, 'user28', 'user28@example.com', '1bb36c13be34479f487d2d42d15b19ac3ba52f329461376aae7c5f516f4e346b', '2023-11-16', 'user'),
(29, 'user29', 'user29@example.com', '95eec19390b1ae11e0dfe6ae5548c92fb3da67f56fb4f01b731deb039c35ec3f', '2023-11-17', 'user'),
(30, 'user30', 'user30@example.com', 'e8a940d4972a7e645e4ea1d712ff1f0c79ec4243378c47e114ca71be2af568e9', '2023-11-18', 'user'),
(32, 'ákoska', 'elkepeszto1995@gmail.com', '*0821C7D9F57AAB8C9ABF26B56F573571229FA626', '2025-01-17', 'user'),
(36, 'elsoezenaneven', 'nagyb6605@gmail.com', 'igeniskapitány0123!', '2025-01-17', 'user'),
(44, 'petike', 'horvathtibor@gmail.com', '082635b2eb16500b82ea6b7a05d175b233e907c8f33c9eb60acda370fd386b094ff2371aa1c0e750b0687e34aa210766fb57460b3cf31290d847a7570a464b91', '2025-01-17', 'admin'),
(45, 'fasz', 'szopo', 'jaha', '2025-01-31', 'user'),
(46, 'ujfelhasználó', 'uj@example.com', 'ujjelszó', '2025-02-25', 'user'),
(48, 'ujfelhasználó2', 'uj@exampple.com', 'Ujjelszó1!', '2025-02-26', 'user'),
(49, 'faszosom', 'uj@valami.com', 'Ujjelszó1!', '2025-02-26', 'user'),
(50, 'hatalma', 'faszosom@gmail.hu', 'Hatalom6!', '2025-02-27', 'user'),
(51, 'hatalmaa', 'faszossom@gmail.hu', 'Hatalom6!', '2025-03-04', 'user'),
(52, 'wáááopoo', 'vicces@gmail.hu', 'Hatalom6!', '2025-03-04', 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `actors`
--
ALTER TABLE `actors`
  ADD PRIMARY KEY (`actor_id`),
  ADD UNIQUE KEY `name` (`name`);

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
  ADD PRIMARY KEY (`movie_id`);

--
-- Indexes for table `movie_actors`
--
ALTER TABLE `movie_actors`
  ADD PRIMARY KEY (`movie_id`,`actor_id`);

--
-- Indexes for table `movie_directors`
--
ALTER TABLE `movie_directors`
  ADD PRIMARY KEY (`movie_id`,`director_id`),
  ADD KEY `director_id` (`director_id`);

--
-- Indexes for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD PRIMARY KEY (`movie_id`,`genre_id`),
  ADD KEY `genre_id` (`genre_id`);

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
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `unique_username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `actors`
--
ALTER TABLE `actors`
  MODIFY `actor_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT for table `directors`
--
ALTER TABLE `directors`
  MODIFY `director_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT for table `genres`
--
ALTER TABLE `genres`
  MODIFY `genre_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `movies`
--
ALTER TABLE `movies`
  MODIFY `movie_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=151;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `rating_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `movie_directors`
--
ALTER TABLE `movie_directors`
  ADD CONSTRAINT `fk_director` FOREIGN KEY (`director_id`) REFERENCES `directors` (`director_id`),
  ADD CONSTRAINT `fk_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`),
  ADD CONSTRAINT `movie_directors_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `movie_directors_ibfk_2` FOREIGN KEY (`director_id`) REFERENCES `directors` (`director_id`) ON DELETE CASCADE;

--
-- Constraints for table `movie_genres`
--
ALTER TABLE `movie_genres`
  ADD CONSTRAINT `fk_movie_genre` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `movie_genres_ibfk_1` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `movie_genres_ibfk_2` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`genre_id`) ON DELETE CASCADE;

--
-- Constraints for table `ratings`
--
ALTER TABLE `ratings`
  ADD CONSTRAINT `fk_ratings_movie` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`movie_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_ratings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
