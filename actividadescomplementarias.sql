-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: actividadescomplementarias
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actividad`
--

DROP TABLE IF EXISTS `actividad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `activo` int DEFAULT NULL,
  `id_disciplina` int NOT NULL,
  `id_semestre` int NOT NULL,
  `id_instructor` int NOT NULL,
  `cupoMaximo` int DEFAULT NULL,
  `imagenFlyer` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_actividad_disciplina` (`id_disciplina`),
  KEY `fk_actividad_semestre` (`id_semestre`),
  KEY `fk_actividad_instructor` (`id_instructor`),
  CONSTRAINT `fk_actividad_disciplina` FOREIGN KEY (`id_disciplina`) REFERENCES `disciplina` (`id`),
  CONSTRAINT `fk_actividad_instructor` FOREIGN KEY (`id_instructor`) REFERENCES `instructor` (`id`),
  CONSTRAINT `fk_actividad_semestre` FOREIGN KEY (`id_semestre`) REFERENCES `semestre` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actividad`
--

LOCK TABLES `actividad` WRITE;
/*!40000 ALTER TABLE `actividad` DISABLE KEYS */;
INSERT INTO `actividad` VALUES (1,'Futbol','Futbol',1,1,1,1,25,'futbol.jpg'),(2,'Basquetbol','',0,2,1,4,40,'basquet.jpg'),(3,'Danza','',1,4,1,2,25,'danza.jpg');
/*!40000 ALTER TABLE `actividad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alumno`
--

DROP TABLE IF EXISTS `alumno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alumno` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` int DEFAULT NULL,
  `id_persona` int NOT NULL,
  `id_carrera` int NOT NULL,
  `numControl` varchar(255) DEFAULT NULL,
  `semestreActual` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_alumno_persona` (`id_persona`),
  KEY `fk_alumno_carrera` (`id_carrera`),
  CONSTRAINT `fk_alumno_carrera` FOREIGN KEY (`id_carrera`) REFERENCES `carrera` (`id`),
  CONSTRAINT `fk_alumno_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alumno`
--

LOCK TABLES `alumno` WRITE;
/*!40000 ALTER TABLE `alumno` DISABLE KEYS */;
INSERT INTO `alumno` VALUES (1,1,2,1,'22520001',8),(2,1,3,1,'22520002',6),(3,1,4,2,'22520003',4),(4,1,5,3,'22520004',8),(5,1,6,1,'22520005',6),(6,1,7,4,'22520006',4),(7,1,8,2,'22520007',8),(8,1,9,5,'22520008',6),(9,1,10,1,'22520009',4),(10,1,11,3,'22520010',8),(11,0,16,3,'12345678',3),(12,1,17,1,'1111111111',9);
/*!40000 ALTER TABLE `alumno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `asistio` tinyint(1) NOT NULL DEFAULT '0',
  `observaciones` varchar(255) DEFAULT NULL,
  `id_alumno` int NOT NULL,
  `id_sesion` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_asistencia_alumno` (`id_alumno`),
  KEY `fk_asistencia_sesion` (`id_sesion`),
  CONSTRAINT `fk_asistencia_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id`),
  CONSTRAINT `fk_asistencia_sesion` FOREIGN KEY (`id_sesion`) REFERENCES `sesion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
INSERT INTO `asistencia` VALUES (1,1,NULL,1,1),(2,1,NULL,2,1),(4,1,NULL,2,22),(5,1,NULL,1,22),(6,1,NULL,1,43),(7,1,NULL,2,43),(8,1,NULL,1,2),(9,1,NULL,2,2),(10,1,NULL,1,23),(11,1,NULL,2,23),(12,1,NULL,1,44),(13,1,NULL,2,44),(14,1,NULL,1,3),(15,1,NULL,2,3),(16,1,NULL,1,24),(17,1,NULL,2,24),(18,1,NULL,1,45),(19,1,NULL,2,45),(20,1,NULL,1,4),(21,1,NULL,2,4),(22,1,NULL,1,25),(23,1,NULL,2,25),(24,1,NULL,1,46),(25,1,NULL,2,46),(26,1,NULL,1,5),(27,1,NULL,2,5),(28,1,NULL,1,26),(29,1,NULL,2,26),(30,1,NULL,1,47),(31,1,NULL,2,47),(32,1,NULL,1,6),(33,1,NULL,2,6),(34,1,NULL,1,27),(35,1,NULL,2,27),(36,1,NULL,1,48),(37,1,NULL,2,48),(38,1,NULL,1,7),(39,1,NULL,2,7),(40,1,NULL,1,28),(41,1,NULL,2,28),(42,1,NULL,1,49),(43,1,NULL,2,49),(44,1,NULL,1,8),(45,1,NULL,2,8),(46,1,NULL,1,29),(47,1,NULL,2,29),(48,1,NULL,1,50),(49,1,NULL,2,50),(50,1,NULL,1,9),(51,1,NULL,2,9),(52,1,NULL,1,30),(53,1,NULL,2,30),(54,1,NULL,1,51),(55,1,NULL,2,51),(56,1,NULL,1,10),(57,1,NULL,2,10),(58,1,NULL,1,31),(59,1,NULL,2,31),(60,1,NULL,1,52),(61,1,NULL,2,52),(62,1,NULL,1,11),(63,1,NULL,2,11),(64,1,NULL,1,32),(65,1,NULL,2,32),(66,1,NULL,1,53),(67,1,NULL,2,53),(68,1,NULL,1,12),(69,1,NULL,2,12),(70,1,NULL,1,33),(71,1,NULL,2,33),(72,1,NULL,1,54),(73,1,NULL,2,54),(74,1,NULL,1,13),(75,1,NULL,2,13),(76,1,NULL,1,34),(77,1,NULL,2,34),(78,1,NULL,1,55),(79,1,NULL,2,55),(80,1,NULL,1,14),(81,1,NULL,2,14),(82,1,NULL,1,35),(83,1,NULL,2,35),(84,1,NULL,1,56),(85,1,NULL,2,56),(86,1,NULL,1,15),(87,1,NULL,2,15),(88,1,NULL,1,36),(89,1,NULL,2,36),(90,1,NULL,1,57),(91,1,NULL,2,57),(92,1,NULL,1,16),(93,1,NULL,2,16),(94,1,NULL,1,37),(95,1,NULL,2,37),(96,1,NULL,1,58),(97,1,NULL,2,58),(98,1,NULL,1,17),(99,1,NULL,2,17),(100,1,NULL,1,38),(101,1,NULL,2,38),(102,1,NULL,1,59),(103,1,NULL,2,59),(104,1,NULL,1,18),(105,1,NULL,2,18),(106,1,NULL,1,39),(107,1,NULL,2,39),(108,1,NULL,1,60),(109,1,NULL,2,60),(110,1,NULL,1,19),(111,1,NULL,2,19),(112,1,NULL,1,40),(113,1,NULL,2,40);
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrera`
--

DROP TABLE IF EXISTS `carrera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrera` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrera`
--

LOCK TABLES `carrera` WRITE;
/*!40000 ALTER TABLE `carrera` DISABLE KEYS */;
INSERT INTO `carrera` VALUES (1,'ISC','Ingeniería En Sistemas Computacionales'),(2,'IGE','Ingeniería En Gestión Empresarial'),(3,'IC','Ingeniería Civil'),(4,'II','Ingeniería En Informática'),(5,'CP','Contador Público');
/*!40000 ALTER TABLE `carrera` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `constancia`
--

DROP TABLE IF EXISTS `constancia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `constancia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `estatus` varchar(255) DEFAULT NULL,
  `id_inscripcion` int NOT NULL,
  `id_semestre` int NOT NULL,
  `id_nivel` int NOT NULL,
  `archivoPdf` varchar(255) DEFAULT NULL,
  `fechaGeneracion` date DEFAULT NULL,
  `valorCurricular` int DEFAULT NULL,
  `valorNumerico` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_constancia_inscripcion` (`id_inscripcion`),
  KEY `fk_constancia_semestre` (`id_semestre`),
  KEY `fk_constancia_nivel` (`id_nivel`),
  CONSTRAINT `fk_constancia_inscripcion` FOREIGN KEY (`id_inscripcion`) REFERENCES `inscripcion` (`id`),
  CONSTRAINT `fk_constancia_nivel` FOREIGN KEY (`id_nivel`) REFERENCES `nivel_desempenio` (`id`),
  CONSTRAINT `fk_constancia_semestre` FOREIGN KEY (`id_semestre`) REFERENCES `semestre` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `constancia`
--

LOCK TABLES `constancia` WRITE;
/*!40000 ALTER TABLE `constancia` DISABLE KEYS */;
INSERT INTO `constancia` VALUES (2,'GENERADA',1,1,1,NULL,'2026-05-25',1,99.9);
/*!40000 ALTER TABLE `constancia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `criterio_base`
--

DROP TABLE IF EXISTS `criterio_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `criterio_base` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `numeroCriterio` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `criterio_base`
--

LOCK TABLES `criterio_base` WRITE;
/*!40000 ALTER TABLE `criterio_base` DISABLE KEYS */;
INSERT INTO `criterio_base` VALUES (1,'Puntualidad','Llega a tiempo a las sesiones',1),(2,'Participación','Participa activamente en las actividades',2),(3,'Actitud','Muestra disposición positiva',3),(4,'Desempeño Técnico','Demuestra habilidades técnicas de la disciplina',4),(5,'Trabajo En Equipo','Colabora con sus compañeros',5),(6,'Responsabilidad','Cumple con sus compromisos',6),(7,'Presentación Personal','Cuida su imagen y presentación',7);
/*!40000 ALTER TABLE `criterio_base` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `criterio_eval`
--

DROP TABLE IF EXISTS `criterio_eval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `criterio_eval` (
  `id` int NOT NULL AUTO_INCREMENT,
  `valor` double DEFAULT NULL,
  `id_evaluacion` int NOT NULL,
  `id_criterio` int NOT NULL,
  `id_nivel` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_criterioeval_evaluacion` (`id_evaluacion`),
  KEY `fk_criterioeval_criterio` (`id_criterio`),
  KEY `fk_criterioeval_nivel` (`id_nivel`),
  CONSTRAINT `fk_criterioeval_criterio` FOREIGN KEY (`id_criterio`) REFERENCES `criterio_base` (`id`),
  CONSTRAINT `fk_criterioeval_evaluacion` FOREIGN KEY (`id_evaluacion`) REFERENCES `evaluacion` (`id`),
  CONSTRAINT `fk_criterioeval_nivel` FOREIGN KEY (`id_nivel`) REFERENCES `nivel_desempenio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `criterio_eval`
--

LOCK TABLES `criterio_eval` WRITE;
/*!40000 ALTER TABLE `criterio_eval` DISABLE KEYS */;
INSERT INTO `criterio_eval` VALUES (1,99.9,1,1,1);
/*!40000 ALTER TABLE `criterio_eval` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disciplina`
--

DROP TABLE IF EXISTS `disciplina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disciplina` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `creditos` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disciplina`
--

LOCK TABLES `disciplina` WRITE;
/*!40000 ALTER TABLE `disciplina` DISABLE KEYS */;
INSERT INTO `disciplina` VALUES (1,'Fútbol',1),(2,'Basquetbol',1),(3,'Voleibol',1),(4,'Danza Folclórica',1),(5,'Teatro',1),(6,'Coro',1),(7,'Karate',1),(8,'Ajedrez',1);
/*!40000 ALTER TABLE `disciplina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `encargado`
--

DROP TABLE IF EXISTS `encargado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `encargado` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activo` int DEFAULT NULL,
  `id_persona` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_encargado_persona` (`id_persona`),
  CONSTRAINT `fk_encargado_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `encargado`
--

LOCK TABLES `encargado` WRITE;
/*!40000 ALTER TABLE `encargado` DISABLE KEYS */;
INSERT INTO `encargado` VALUES (1,1,15);
/*!40000 ALTER TABLE `encargado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `espacio`
--

DROP TABLE IF EXISTS `espacio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `espacio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `espacio`
--

LOCK TABLES `espacio` WRITE;
/*!40000 ALTER TABLE `espacio` DISABLE KEYS */;
INSERT INTO `espacio` VALUES (1,'Cancha De Fútbol','Cancha principal exterior'),(2,'Edificio S - Explanada','Explanada del edificio S'),(3,'Edificio S - S1','Aula 1 del edificio S'),(4,'Cancha De Basquetbol','Cancha techada interior');
/*!40000 ALTER TABLE `espacio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluacion`
--

DROP TABLE IF EXISTS `evaluacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `id_inscripcion` int NOT NULL,
  `id_instructor` int NOT NULL,
  `id_nivel` int NOT NULL,
  `valorNumerico` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_evaluacion_inscripcion` (`id_inscripcion`),
  KEY `fk_evaluacion_instructor` (`id_instructor`),
  KEY `fk_evaluacion_nivel` (`id_nivel`),
  CONSTRAINT `fk_evaluacion_inscripcion` FOREIGN KEY (`id_inscripcion`) REFERENCES `inscripcion` (`id`),
  CONSTRAINT `fk_evaluacion_instructor` FOREIGN KEY (`id_instructor`) REFERENCES `instructor` (`id`),
  CONSTRAINT `fk_evaluacion_nivel` FOREIGN KEY (`id_nivel`) REFERENCES `nivel_desempenio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluacion`
--

LOCK TABLES `evaluacion` WRITE;
/*!40000 ALTER TABLE `evaluacion` DISABLE KEYS */;
INSERT INTO `evaluacion` VALUES (1,'2026-05-25','',1,1,1,99.9);
/*!40000 ALTER TABLE `evaluacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horario`
--

DROP TABLE IF EXISTS `horario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_actividad` int NOT NULL,
  `id_espacio` int NOT NULL,
  `diaSemana` varchar(255) DEFAULT NULL,
  `horaFin` time DEFAULT NULL,
  `horaInicio` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_horario_actividad` (`id_actividad`),
  KEY `fk_horario_espacio` (`id_espacio`),
  CONSTRAINT `fk_horario_actividad` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id`),
  CONSTRAINT `fk_horario_espacio` FOREIGN KEY (`id_espacio`) REFERENCES `espacio` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horario`
--

LOCK TABLES `horario` WRITE;
/*!40000 ALTER TABLE `horario` DISABLE KEYS */;
INSERT INTO `horario` VALUES (1,1,1,'MARTES','09:00:00','08:00:00'),(2,1,1,'MIERCOLES','09:00:00','08:00:00'),(3,1,1,'JUEVES','09:00:00','08:00:00'),(4,2,4,'LUNES','10:00:00','09:00:00'),(5,2,4,'MIERCOLES','10:00:00','09:00:00'),(6,2,4,'VIERNES','10:00:00','09:00:00');
/*!40000 ALTER TABLE `horario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripcion`
--

DROP TABLE IF EXISTS `inscripcion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripcion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_alumno` int NOT NULL,
  `id_actividad` int NOT NULL,
  `id_encargado` int DEFAULT NULL,
  `archivoHorario` varchar(255) DEFAULT NULL,
  `estatusSolicitud` varchar(255) DEFAULT NULL,
  `fechaSolicitud` date DEFAULT NULL,
  `motivoRechazo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_inscripcion_alumno` (`id_alumno`),
  KEY `fk_inscripcion_actividad` (`id_actividad`),
  KEY `fk_inscripcion_encargado` (`id_encargado`),
  CONSTRAINT `fk_inscripcion_actividad` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id`),
  CONSTRAINT `fk_inscripcion_alumno` FOREIGN KEY (`id_alumno`) REFERENCES `alumno` (`id`),
  CONSTRAINT `fk_inscripcion_encargado` FOREIGN KEY (`id_encargado`) REFERENCES `encargado` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripcion`
--

LOCK TABLES `inscripcion` WRITE;
/*!40000 ALTER TABLE `inscripcion` DISABLE KEYS */;
INSERT INTO `inscripcion` VALUES (1,1,1,1,'carga_academica.pdf','APROBADA','2026-05-25',NULL),(2,2,1,1,'carga_academica.pdf','APROBADA','2026-05-25',NULL),(3,12,3,1,'carga_academica.pdf','PENDIENTE','2026-05-25',NULL),(4,3,3,1,'carga_academica.pdf','PENDIENTE','2026-05-25',NULL);
/*!40000 ALTER TABLE `inscripcion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instructor`
--

DROP TABLE IF EXISTS `instructor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `foto` varchar(255) DEFAULT NULL,
  `activo` int DEFAULT NULL,
  `id_persona` int NOT NULL,
  `id_disciplina` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_instructor_persona` (`id_persona`),
  KEY `id_disciplina` (`id_disciplina`),
  CONSTRAINT `fk_instructor_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id`),
  CONSTRAINT `instructor_ibfk_1` FOREIGN KEY (`id_disciplina`) REFERENCES `disciplina` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructor`
--

LOCK TABLES `instructor` WRITE;
/*!40000 ALTER TABLE `instructor` DISABLE KEYS */;
INSERT INTO `instructor` VALUES (1,'no-image.jpg',1,12,1),(2,'no-image.jpg',1,13,4),(3,'no-image.jpg',1,14,6),(4,'WhatsApp Image 2026-05-22 at 8.05.23 PM.jpeg',1,18,2);
/*!40000 ALTER TABLE `instructor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nivel_desempenio`
--

DROP TABLE IF EXISTS `nivel_desempenio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nivel_desempenio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `rangoMax` double DEFAULT NULL,
  `rangoMin` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nivel_desempenio`
--

LOCK TABLES `nivel_desempenio` WRITE;
/*!40000 ALTER TABLE `nivel_desempenio` DISABLE KEYS */;
INSERT INTO `nivel_desempenio` VALUES (1,'Excelente',100,90),(2,'Notable',89.99,80),(3,'Bueno',79.99,70),(4,'Suficiente',69.99,60),(5,'Insuficiente',59.99,0);
/*!40000 ALTER TABLE `nivel_desempenio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `persona`
--

DROP TABLE IF EXISTS `persona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persona` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persona`
--

LOCK TABLES `persona` WRITE;
/*!40000 ALTER TABLE `persona` DISABLE KEYS */;
INSERT INTO `persona` VALUES (1,'Roberta','Garcia Oropeza','admin@correo.com','123456789'),(2,'Lucero','Oropeza Oropeza','lucero@itch.edu.mx','7471110012'),(3,'Roberto','Mendez Mendez','roberto@itch.edu.mx','7471110013'),(4,'Yesenia','Ortiz Ortiz','yesenia@itch.edu.mx','7471110003'),(5,'Martin','Lopez Lopez','martin@itch.edu.mx','7471110004'),(6,'Francisca','Juarez Juarez','francisca@itch.edu.mx','7471110005'),(7,'Carlos','Prado Prado','carlos@itch.edu.mx','7471110006'),(8,'Diana','Ramirez Ramirez','diana@itch.edu.mx','7471110007'),(9,'Eduardo','Soto Soto','eduardo@itch.edu.mx','7471110008'),(10,'Fernanda','Cruz Cruz','fernanda@itch.edu.mx','7471110009'),(11,'Gabriel','Mendez Mendez','gabriel@itch.edu.mx','7471110010'),(12,'Gerardo','Castillo Mendez','gerardo@itch.edu.mx','7471110011'),(13,'Carmen','Serdan Gutierrez','carmen@itch.edu.mx','7471110012'),(14,'Erick','Niño Vazquez','erick@itch.edu.mx','7471110013'),(15,'Martha','Villanueva Villanueva','martha@itch.edu.mx','7471110014'),(16,'Sss','Dd','antonios@itch.edu.mx','7471110001'),(17,'Prueba','Prueba','prueba@itch.edu.mx','1234241324'),(18,'Mauricio','Leyva Solis','mauricio@itch.edu.mx','');
/*!40000 ALTER TABLE `persona` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semestre`
--

DROP TABLE IF EXISTS `semestre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semestre` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  `activo` int DEFAULT NULL,
  `fechaFin` date DEFAULT NULL,
  `fechaInicio` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semestre`
--

LOCK TABLES `semestre` WRITE;
/*!40000 ALTER TABLE `semestre` DISABLE KEYS */;
INSERT INTO `semestre` VALUES (1,'Ene-Jun 2026',1,'2026-06-22','2026-01-26'),(2,'Ago-Dic 2025',0,'2025-12-20','2025-08-11'),(3,'Ago-Dic 2021',0,'2021-12-31','2021-08-01');
/*!40000 ALTER TABLE `semestre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesion`
--

DROP TABLE IF EXISTS `sesion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `id_horario` int NOT NULL,
  `numeroSesion` int DEFAULT NULL,
  `estatus` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_sesion_horario` (`id_horario`),
  CONSTRAINT `fk_sesion_horario` FOREIGN KEY (`id_horario`) REFERENCES `horario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesion`
--

LOCK TABLES `sesion` WRITE;
/*!40000 ALTER TABLE `sesion` DISABLE KEYS */;
INSERT INTO `sesion` VALUES (1,'2026-01-27','erw',1,1,'CANCELADA'),(2,'2026-02-03',NULL,1,4,'REALIZADA'),(3,'2026-02-10',NULL,1,7,'REALIZADA'),(4,'2026-02-17',NULL,1,10,'REALIZADA'),(5,'2026-02-24',NULL,1,13,'REALIZADA'),(6,'2026-03-03',NULL,1,16,'REALIZADA'),(7,'2026-03-10',NULL,1,19,'REALIZADA'),(8,'2026-03-17',NULL,1,22,'REALIZADA'),(9,'2026-03-24',NULL,1,25,'REALIZADA'),(10,'2026-03-31',NULL,1,28,'REALIZADA'),(11,'2026-04-07',NULL,1,31,'REALIZADA'),(12,'2026-04-14',NULL,1,34,'REALIZADA'),(13,'2026-04-21',NULL,1,37,'REALIZADA'),(14,'2026-04-28',NULL,1,40,'REALIZADA'),(15,'2026-05-05',NULL,1,43,'REALIZADA'),(16,'2026-05-12',NULL,1,46,'REALIZADA'),(17,'2026-05-19',NULL,1,49,'REALIZADA'),(18,'2026-05-26',NULL,1,52,'REALIZADA'),(19,'2026-06-02',NULL,1,55,'REALIZADA'),(20,'2026-06-09',NULL,1,58,'PROGRAMADA'),(21,'2026-06-16',NULL,1,61,'PROGRAMADA'),(22,'2026-01-28',NULL,2,2,'REALIZADA'),(23,'2026-02-04',NULL,2,5,'REALIZADA'),(24,'2026-02-11',NULL,2,8,'REALIZADA'),(25,'2026-02-18',NULL,2,11,'REALIZADA'),(26,'2026-02-25',NULL,2,14,'REALIZADA'),(27,'2026-03-04',NULL,2,17,'REALIZADA'),(28,'2026-03-11',NULL,2,20,'REALIZADA'),(29,'2026-03-18',NULL,2,23,'REALIZADA'),(30,'2026-03-25',NULL,2,26,'REALIZADA'),(31,'2026-04-01',NULL,2,29,'REALIZADA'),(32,'2026-04-08',NULL,2,32,'REALIZADA'),(33,'2026-04-15',NULL,2,35,'REALIZADA'),(34,'2026-04-22',NULL,2,38,'REALIZADA'),(35,'2026-04-29',NULL,2,41,'REALIZADA'),(36,'2026-05-06',NULL,2,44,'REALIZADA'),(37,'2026-05-13',NULL,2,47,'REALIZADA'),(38,'2026-05-20',NULL,2,50,'REALIZADA'),(39,'2026-05-27',NULL,2,53,'REALIZADA'),(40,'2026-06-03',NULL,2,56,'REALIZADA'),(41,'2026-06-10',NULL,2,59,'PROGRAMADA'),(42,'2026-06-17',NULL,2,62,'PROGRAMADA'),(43,'2026-01-29',NULL,3,3,'REALIZADA'),(44,'2026-02-05',NULL,3,6,'REALIZADA'),(45,'2026-02-12',NULL,3,9,'REALIZADA'),(46,'2026-02-19',NULL,3,12,'REALIZADA'),(47,'2026-02-26',NULL,3,15,'REALIZADA'),(48,'2026-03-05',NULL,3,18,'REALIZADA'),(49,'2026-03-12',NULL,3,21,'REALIZADA'),(50,'2026-03-19',NULL,3,24,'REALIZADA'),(51,'2026-03-26',NULL,3,27,'REALIZADA'),(52,'2026-04-02',NULL,3,30,'REALIZADA'),(53,'2026-04-09',NULL,3,33,'REALIZADA'),(54,'2026-04-16',NULL,3,36,'REALIZADA'),(55,'2026-04-23',NULL,3,39,'REALIZADA'),(56,'2026-04-30',NULL,3,42,'REALIZADA'),(57,'2026-05-07',NULL,3,45,'REALIZADA'),(58,'2026-05-14',NULL,3,48,'REALIZADA'),(59,'2026-05-21',NULL,3,51,'REALIZADA'),(60,'2026-05-28',NULL,3,54,'REALIZADA'),(61,'2026-06-04',NULL,3,57,'PROGRAMADA'),(62,'2026-06-11',NULL,3,60,'PROGRAMADA'),(63,'2026-06-18',NULL,3,63,'PROGRAMADA'),(64,'2026-01-26',NULL,4,1,'PROGRAMADA'),(65,'2026-02-02',NULL,4,4,'PROGRAMADA'),(66,'2026-02-09',NULL,4,7,'PROGRAMADA'),(67,'2026-02-16',NULL,4,10,'PROGRAMADA'),(68,'2026-02-23',NULL,4,13,'PROGRAMADA'),(69,'2026-03-02',NULL,4,16,'PROGRAMADA'),(70,'2026-03-09',NULL,4,19,'PROGRAMADA'),(71,'2026-03-16',NULL,4,22,'PROGRAMADA'),(72,'2026-03-23',NULL,4,25,'PROGRAMADA'),(73,'2026-03-30',NULL,4,28,'PROGRAMADA'),(74,'2026-04-06',NULL,4,31,'PROGRAMADA'),(75,'2026-04-13',NULL,4,34,'PROGRAMADA'),(76,'2026-04-20',NULL,4,37,'PROGRAMADA'),(77,'2026-04-27',NULL,4,40,'PROGRAMADA'),(78,'2026-05-04',NULL,4,43,'PROGRAMADA'),(79,'2026-05-11',NULL,4,46,'PROGRAMADA'),(80,'2026-05-18',NULL,4,49,'PROGRAMADA'),(81,'2026-05-25',NULL,4,52,'PROGRAMADA'),(82,'2026-06-01',NULL,4,55,'PROGRAMADA'),(83,'2026-06-08',NULL,4,58,'PROGRAMADA'),(84,'2026-06-15',NULL,4,61,'PROGRAMADA'),(85,'2026-06-22',NULL,4,64,'PROGRAMADA'),(86,'2026-01-28',NULL,5,2,'PROGRAMADA'),(87,'2026-02-04',NULL,5,5,'PROGRAMADA'),(88,'2026-02-11',NULL,5,8,'PROGRAMADA'),(89,'2026-02-18',NULL,5,11,'PROGRAMADA'),(90,'2026-02-25',NULL,5,14,'PROGRAMADA'),(91,'2026-03-04',NULL,5,17,'PROGRAMADA'),(92,'2026-03-11',NULL,5,20,'PROGRAMADA'),(93,'2026-03-18',NULL,5,23,'PROGRAMADA'),(94,'2026-03-25',NULL,5,26,'PROGRAMADA'),(95,'2026-04-01',NULL,5,29,'PROGRAMADA'),(96,'2026-04-08',NULL,5,32,'PROGRAMADA'),(97,'2026-04-15',NULL,5,35,'PROGRAMADA'),(98,'2026-04-22',NULL,5,38,'PROGRAMADA'),(99,'2026-04-29',NULL,5,41,'PROGRAMADA'),(100,'2026-05-06',NULL,5,44,'PROGRAMADA'),(101,'2026-05-13',NULL,5,47,'PROGRAMADA'),(102,'2026-05-20',NULL,5,50,'PROGRAMADA'),(103,'2026-05-27',NULL,5,53,'PROGRAMADA'),(104,'2026-06-03',NULL,5,56,'PROGRAMADA'),(105,'2026-06-10',NULL,5,59,'PROGRAMADA'),(106,'2026-06-17',NULL,5,62,'PROGRAMADA'),(107,'2026-01-30',NULL,6,3,'PROGRAMADA'),(108,'2026-02-06',NULL,6,6,'PROGRAMADA'),(109,'2026-02-13',NULL,6,9,'PROGRAMADA'),(110,'2026-02-20',NULL,6,12,'PROGRAMADA'),(111,'2026-02-27',NULL,6,15,'PROGRAMADA'),(112,'2026-03-06',NULL,6,18,'PROGRAMADA'),(113,'2026-03-13',NULL,6,21,'PROGRAMADA'),(114,'2026-03-20',NULL,6,24,'PROGRAMADA'),(115,'2026-03-27',NULL,6,27,'PROGRAMADA'),(116,'2026-04-03',NULL,6,30,'PROGRAMADA'),(117,'2026-04-10',NULL,6,33,'PROGRAMADA'),(118,'2026-04-17',NULL,6,36,'PROGRAMADA'),(119,'2026-04-24',NULL,6,39,'PROGRAMADA'),(120,'2026-05-01',NULL,6,42,'PROGRAMADA'),(121,'2026-05-08',NULL,6,45,'PROGRAMADA'),(122,'2026-05-15',NULL,6,48,'PROGRAMADA'),(123,'2026-05-22',NULL,6,51,'PROGRAMADA'),(124,'2026-05-29',NULL,6,54,'PROGRAMADA'),(125,'2026-06-05',NULL,6,57,'PROGRAMADA'),(126,'2026-06-12',NULL,6,60,'PROGRAMADA'),(127,'2026-06-19',NULL,6,63,'PROGRAMADA');
/*!40000 ALTER TABLE `sesion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `rol` varchar(255) DEFAULT NULL,
  `activo` int DEFAULT NULL,
  `id_persona` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `fk_usuario_persona` (`id_persona`),
  CONSTRAINT `fk_usuario_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin','$2a$10$VH99oNcKuVUQJXZHASCrcuoXFNNqS0.GGenfLCYcrNqRfDtWAxnd.','ROLE_ADMIN',1,1),(2,'alumno1','$2a$10$whYLEcI9GoqWbc2.di8lde1RJARCIT7j1ulszCrKw4C4X/U6DOv3G','ROLE_ALUMNO',1,2),(3,'instructor1','$2a$10$Rm2aTRFdlrXD2ncyKakTs.LjjrMZf2LoeM9j4sgXUBasXlMeQMdPm','ROLE_INSTRUCTOR',1,14),(4,'encargado1','$2a$10$HUldcuLlHuj40HeZp.mhOOef5jTLU47KuYJ4/oRf7NG/MiWJQCOFC','ROLE_ENCARGADO',1,15),(5,'prueba','$2a$10$eCbOeWzM6MbN10y3L.PI/.c9/AacmplDQfeeyGbMI3mZsk1blsae2','ROLE_ALUMNO',1,17),(6,'instructor2','$2a$10$.yTI8AajR2QuuYxFD3VMfOA8IGLQyyrIrDtl3G4OHe3NOmH0wJ3xm','ROLE_INSTRUCTOR',1,12),(7,'alumno2','$2a$10$VRDH0WjaP2DCCBSq1Mh02esN36G/mqTN.pognIaI5f.o1d9fO5DXW','ROLE_ALUMNO',1,4),(8,'alumnop','$2a$10$NMHpWaTkD7dM.DpetC2Q4OIz61NX2j4/Tim/FWb20GkZaAZ1eo2WK','ROLE_ALUMNO',1,6);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-25  7:14:42
