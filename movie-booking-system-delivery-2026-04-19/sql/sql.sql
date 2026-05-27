-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: movie_booking_system_new
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名称',
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
                         `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
                         `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
                         `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
                         `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                         `status` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` VALUES (1,'admin','123456','管理员','http://localhost:1000/file/e5764188ea16f355ffc2dfc84858b48f.png','123456','123456@qq.com','启用','2025-03-01 17:29:35');

--
-- Table structure for table `advertising`
--

DROP TABLE IF EXISTS `advertising`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `advertising` (
                               `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '位置',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                               `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接',
                               `main_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图',
                               `sort` int NOT NULL COMMENT '排序',
                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='广告位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `advertising`
--

INSERT INTO `advertising` VALUES (1,'轮播图右下侧','海上钢琴师','movieDetails/3','http://localhost:1000/file/32031ad39475bb5ca41dd19b690409a3.jpeg',0,'2025-03-10 14:14:45');
INSERT INTO `advertising` VALUES (2,'轮播图左下侧','唐探1900','movieDetails/2','http://localhost:1000/file/c262a1b185217816cdec09b812549ffa.jpg',0,'2025-03-10 14:15:12');

--
-- Table structure for table `cinema`
--

DROP TABLE IF EXISTS `cinema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cinema` (
                          `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                          `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                          `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                          `nickname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
                          `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像',
                          `tel` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
                          `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                          `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
                          `address` text COLLATE utf8mb4_unicode_ci COMMENT '地址',
                          `label` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签',
                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影院';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cinema`
--

INSERT INTO `cinema` VALUES (1,'c1','123456','惠友影院','http://localhost:1000/file/3cbb94f9bdf6529b84e2123ed8a74d62.jpg','13022506541',NULL,'启用','河北省曲阳县惠友影院','杜比音效,停车方便,亲子观影','2025-03-07 17:19:10');
INSERT INTO `cinema` VALUES (2,'c2','123456','凯嘉影院','http://localhost:1000/file/46f2af5677f981c41c5ff987bae77e20.jpg','18775612310',NULL,'启用','河北省曲阳县凯嘉影院','IMAX厅,休息区,情侣座','2025-03-07 18:25:13');
INSERT INTO `cinema` VALUES (3,'c3','123456','永宁影院','http://localhost:1000/file/d77b13cb6e0c26bb65b6316f7d553782.webp','19765405897',NULL,'启用','河北省曲阳县永宁影院','激光厅,杜比全景声,交通便利','2025-03-11 15:16:11');

--
-- Table structure for table `movie`
--

DROP TABLE IF EXISTS `movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie` (
                         `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                         `main_img` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图',
                         `movie_type_id` int NOT NULL COMMENT '分类',
                         `movie_years_id` int NOT NULL COMMENT '电影年份',
                         `movie_region_id` int NOT NULL COMMENT '电影区域',
                         `release_date` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上映时间',
                         `duration` int NOT NULL COMMENT '时长',
                         `director` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '导演',
                         `protagonist` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主演',
                         `intro` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '简介',
                         `imgs` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图集',
                         `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `box_office` float NOT NULL DEFAULT '0' COMMENT '票房',
                         `score` int NOT NULL DEFAULT '5' COMMENT '评分',
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie`
--

INSERT INTO `movie` VALUES (1,'哪吒之魔童闹海','http://localhost:1000/file/ddb8824b4659206a0c7aa179526a1392.jpg',2,5,1,'2025-01-29 09:00',144,'饺子','吕艳婷,瀚墨,囧森瑟夫,绿绮,陈浩,张珈铭,杨卫,魔童哪吒,敖丙','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">天劫之后，哪吒、敖丙的灵魂虽保住了，但肉身很快会魂飞魄散。太乙真人打算用七色宝莲给二人重塑肉身。但是在重塑肉身的过程中却遇到重重困难，哪吒、敖丙的命运将走向何方？</span></p>','http://localhost:1000/file/b632f53af9bf91ba31b6f570c3e2f03a.jpg,http://localhost:1000/file/c7a9cda54c107c12af65b7f5c941fa8c.jpg,http://localhost:1000/file/533f955bfa01692a9eb0519ba47e05d6.jpg','2025-03-04 16:00:54',161,3);
INSERT INTO `movie` VALUES (2,'唐探1900','http://localhost:1000/file/3ce10b6efaf1cc8090dbed64c7c4d22a.jpg',8,5,1,'2025-01-29 09:00',136,'陈思诚','王宝强,刘昊然,周润发','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">十年情怀真诚打造，唐探宇宙口碑佳作！王宝强刘昊然唐人街神探逗笑登场，周润发名场面巅峰演绎神乎其技！欢乐包裹家国情，以“笑”见大有底蕴！ 1900年，在美国旧金山唐人街，美洲原始部落猎人阿鬼(王宝强 饰)与留美青年秦福(刘昊然 饰)因一场凶杀案偶然结识，误打误撞组成“唐人街神探”组合。开启了一场笑闹探案之旅.....</span></p>','http://localhost:1000/file/558ebeefb540761e042cc7ce6d88e4de.jpg,http://localhost:1000/file/6b9b4715424b7f0240fca3d7dcc0a386.jpg,http://localhost:1000/file/b0a3104437976be577dc6f68f7ed3249.jpg','2025-03-05 13:05:03',0,5);
INSERT INTO `movie` VALUES (3,'海上钢琴师','http://localhost:1000/file/41057e5ccc4f2f399a5147512b0ae741.jpg',26,5,1,'2019-11-15',125,'朱塞佩·托纳多雷','蒂姆·罗斯,比尔·努恩,克兰伦斯·威廉,普路特·泰勒·文斯,梅兰尼·蒂埃里,彼得·沃恩','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">1900年的第一天，往返于欧美两地的邮轮Virginian号上，负责邮轮上添加煤炭的工人丹尼·博德曼 （ 比尔·努恩 饰）在头等舱上欲捡拾有钱人残留下来的食物时，却意外的在钢琴上发现一个被遗弃的新生儿，装在TD牌柠檬的空纸箱内。由于坚信“TD”正代表了Thanks Danny的缩写，于是丹尼不顾其他工人的嘲笑，独立抚育这个婴儿，并为了纪念这特别的一天，将他取名为：1900。海上出生的1900 （ 蒂姆·罗斯 饰），在陆地上却是个从未存在的人，没有亲人、没有户籍，也没有国籍，大海便是他的摇篮，而他也随着Virginian号往返靠泊各个码头，逐渐长大。然而好景不常，一次的海上意外事件，造成抚养1900的丹尼意外丧生，幸而奇迹似的，某天深夜船上的众人被优美的琴声所惊醒，循着琴声而往，居然是无师自通的1900在钢琴前忘我的演奏着，动人的旋律打动了众人，从此，1900展开了在海上弹奏钢琴的旅程，也吸引了愈来愈多慕名而来的旅客。马克斯 （ 普路特·泰勒·文斯 饰）在因缘际会下来到Virginian号加入乐队伴奏，也因此见识到这位传说中的海上钢琴师1900，两人因此结为好友。而1900在与发明爵士乐的传奇钢琴手杰利同船竞技钢琴琴艺之后，声势更是如日中天。但尽管马克斯再三鼓励1900下船去向全世界展露他的天赋，1900却始终未曾踏足陆地一步。直到他在为唱片公司录制个人专辑时，意外见到船舱之外清秀动人的女孩，并在感动之余创作了《1900\'s Theme》。随着女孩的下船离去，1900的心开始波动 。</span></p>','http://localhost:1000/file/cd63c8195e86bcaf6f55a9db29fb462c.jpg,http://localhost:1000/file/cc5a8c80298361193211b3764633e220.jpg,http://localhost:1000/file/352c809420f90b81bc54095293ac57fc.jpg','2025-03-05 13:17:38',0,5);
INSERT INTO `movie` VALUES (4,'爱你很久很久','http://localhost:1000/file/c274cd394ae582716d63a13ca0f7a25a.jpg',2,6,1,'2024-11-01 17:20',113,'赖孟杰','李沐,曹佑宁,娄峻硕,林美秀,樊光耀,黄信赫,曾珮瑜,班铁翔,李璇,陈璇','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">你的青春里有没有一个忘不掉的人？阳光明媚的高中生王晓夏（李沐 饰）在一次偶然中对天才转校生程奕（曹佑宁 饰）一见钟情，无法克制的心跳，让晓夏开启高调追爱模式。而从小一起长大默默守护着她的柚子（娄峻硕 饰）也决定为自己争取一次。一边是天降男神，一边是竹马守护，十七岁的少年都怀揣着最炙热的爱意，晓夏又该如何抉择？ 爱是给勇敢者的奖励，2024年冬天第一部爱情电影，一定要和最最最爱的人一起看……</span></p>','http://localhost:1000/file/268e64bdf956b9e522a5c0c5737f8cc1.jpg,http://localhost:1000/file/664dd1b8bfc3bf727d40159012d65955.jpg,http://localhost:1000/file/78b7964426c8b7b54ba9e9cc71a7d209.jpg,http://localhost:1000/file/951b4f261e5045134f7ef59911ceebdb.jpg','2025-03-05 13:25:44',0,5);
INSERT INTO `movie` VALUES (5,'帕丁顿熊3：秘鲁大冒险','http://localhost:1000/file/baaba1e963dfc89d49866b9641b48213.jpg',11,5,1,'2025-01-01 10:00',107,'道格尔·威尔森','本·威士肖,杜江,休·博纳维尔,董浩,刘纯燕,艾米莉·莫迪默,马德琳·哈里斯,萨缪尔·乔斯林','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">生活在伦敦的帕丁顿（本·威士肖 配音）从退休熊之家修女院长（奥利维娅·科尔曼 饰演）的信件中得知，住在这里的露西婶婶非常思念他。于是帕丁顿与布朗一家决定动身秘鲁，一起回乡探望他心爱的露西婶婶。然而，露西婶婶的神秘失踪将帕丁顿与布朗一家带入意想不到的惊险旅程，他们将和船长（安东尼奥·班德拉斯 饰演）与船长女儿（卡拉·杜斯 饰演）共同闯入亚马逊的迷雾丛林和陡峭的秘鲁山巅，开启寻找“黄金国”的冒险。</span></p>','http://localhost:1000/file/bff2fb29113aab736a3803bbfb13bf5c.jpg,http://localhost:1000/file/ecb2fe053491ae1dd641326f75c88fc8.jpg,http://localhost:1000/file/061ed3a8b99cb160e825675230aff916.jpg,http://localhost:1000/file/8411ad3f37084866f46998d425383a96.jpg','2025-03-05 13:29:13',0,5);
INSERT INTO `movie` VALUES (6,'里斯本丸沉没','http://localhost:1000/file/2f6b7dfd71ed77b35d6442ba5bcf9555.jpg',24,6,1,'2024-09-06 18:00',122,'方励','方励,托尼·班纳姆,林阿根','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">1942年，经过日军武装的货船“里斯本丸”，载着1816名英军战俘经过中国浙江舟山海域时被美军潜艇击沉，危急关头，善良的中国渔民挺身而出，冒着日军的枪林弹雨出海救下了384名英军战俘…本片完全取材于真实历史事件，通过对沉船的探索与发掘，通过采访大量英军战俘后人和与事件相关的当事人，收集了大量详实的信息资料，全面深入透彻地讲述了“里斯本丸”沉船始末，以及背后更多惊心动魄、鲜为人知的故事，揭开了掩埋了八十二年的历史真相。本片所反映的内容是中英在第二次世界大战中作为盟友并肩作战、共同抗击法西斯侵略的重要见证，同时也是揭露日本法西斯在二战期间又一反人类罪行的铁证。</span></p>','http://localhost:1000/file/2f6b7dfd71ed77b35d6442ba5bcf9555.jpg,http://localhost:1000/file/82155a00153d4e3a72c309aa28708020.jpg,http://localhost:1000/file/2f1c57ab6003f69634bb9c432aff2787.jpg,http://localhost:1000/file/f118bb66dc873d80df67834d07403780.jpg','2025-03-05 13:31:58',0,5);
INSERT INTO `movie` VALUES (7,'胜券在握','http://localhost:1000/file/c8b3a824faf597a60f48fbcec9f747d2.jpg',4,6,1,'2024-11-15 14:00',135,'刘循子墨','邓超,邓家佳,郑云龙,喻恩泰,张本煜,柯达,李乃文','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">年度讽刺喜剧！看职场爽文，打工人组团斗翻资本！ 科技公司11号员工白胜（邓超 饰），不仅背负百万债务还惨遭公司裁员。已经没什么可以失去的他决定和公司斗一斗，如何才能在公司苟住100天并最终赢回属于自己的800万期权？ 第一步：重返公司。精心准备一个创业骗局，这一次，不做员工要当老板。 第二步：组建自己的团队。队友方方（邓家佳 饰）、哈雷（柯达 饰）、强尼（李乃文 饰）来相助，临时搭起的草台班子也能掀翻公司吗？ 第三步：披荆斩棘。负责裁员的张见（张本煜 饰）多番阻挠，还派来监事周望高（郑云龙 饰）时刻盯着白胜。笑面虎任远劳（喻恩泰 饰）怎么才能为白胜所用？催债的郝大哥（杨皓宇 饰）是阻力还是助力？ 第四步，暂时还没有思路......没关系，很多事装着装着就真成了。 吃瓜暂停，白胜究竟能否拿回800万？11月15日影院见分晓。 </span></p>','http://localhost:1000/file/c8b3a824faf597a60f48fbcec9f747d2.jpg,http://localhost:1000/file/81888a9478a27e1853f030219ad98939.jpg,http://localhost:1000/file/b19b5ba5fe9eb86effdb1700107d54a4.jpg','2025-03-05 13:35:58',0,5);
INSERT INTO `movie` VALUES (8,'疯狂的外星人','http://localhost:1000/file/9e400081c1a67824d39bb1be552e31f6.jpg',2,6,1,'2024-12-20 18:00',116,'宁浩','黄渤,沈腾,马修·莫里森,汤姆·派福瑞,凯特·纳尔逊,徐峥,于和伟,雷佳音,刘桦','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">耿浩 （ 黄渤 饰）对耍猴这项“国粹”全身心投入，每日西装革履，在世界公园里认真表演。然而，游客们对他的卖力演出兴趣不大，稀稀拉拉的几个人有的玩手机、有的打电话。他不仅面临着五指山要被改造为火锅城的威胁，唯一的猴子欢欢也因为骨折而无法表演。而开酒类专营店、一心想发大财的大飞 （ 沈腾 饰）看上去很“社会”，却屡屡投资失败，成为被各类骗局收割的“韭菜”。两个人经营着各自惨淡的“事业”，然而“天外来客”的意外降临，打破了二人平静又拮据的生活。神秘的西方力量也派出“哼哈二将”在全球寻找外星人踪影。啼笑皆非的跨物种对决，别开生面的“星战”，在中国某海边城市激情上演 。</span></p>','http://localhost:1000/file/430e768344e89bed4f949453318b20f7.jpg,http://localhost:1000/file/1d4f5d19d29bc609d92df53e14d2ffad.jpg,http://localhost:1000/file/7848e852ddbf96631ba4119aecc58cf5.jpg,http://localhost:1000/file/2e23d2fab70d7e0a8255ebac8141e0e8.jpg','2025-03-05 13:41:35',0,5);
INSERT INTO `movie` VALUES (9,'那个不为人知的故事','http://localhost:1000/file/a9a6e385a2d4be11ed53621a29c8d766.jpg',2,6,1,'2024-11-09 09:00',103,'张岩','张岩,郎月婷,李孝谦,邢佳栋,柯达,王道铁,董博睿,甘昀宸,尚馨','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">近百万人千呼万唤的超人气同名IP首登大银幕，心痛与希望交织，为国捐躯的缉毒警察顶级凄美虐恋爆泪片，带你大哭一场! 神秘的出租车司机陈铭生(邱泽 饰)与性格清冷的文物修复师杨昭(郎月婷 饰)偶然相遇，在一场暧昧、试探、你进我退的极致拉扯中，陈铭生渐渐卸下心防。在一次旅行中，陈铭生的卧底缉毒警身份暴露，二人的感情再次面临选择，这份汹涌悸动的爱能否抵挡住命运的捉弄？卧底缉毒警陈铭⽣孤勇奋战为国牺牲，又有多少⽆名英雄日日夜夜负重前⾏，守护着万家灯⽕？ 无名，却被无数人铭记； 陈铭生，我们永远都记得。</span></p>','http://localhost:1000/file/20607d1bb1ea406096972fb2f22eb057.jpg,http://localhost:1000/file/972d2926a5ce089ae658bdce5ef3d9f6.jpg,http://localhost:1000/file/bb6fa82c314caf928111607adacabaa5.jpg,http://localhost:1000/file/cf78fe00f2f7bb561547b972a969b974.jpg','2025-03-05 13:45:45',0,5);
INSERT INTO `movie` VALUES (10,'好运来','http://localhost:1000/file/7171d04665e5bb8f2054a83d9b08e2ad.jpg',2,6,1,'2024-11-30 09:00',101,'朱凌锋','白客,乔杉,王大陆,黄才伦,李嘉琦,米咪,孙竞争,魏翔,许君聪','<p><span style=\"color: rgb(51, 51, 51); background-color: rgb(255, 255, 255); font-size: 14px;\">成名！成功！发财！最靠谱喜剧联盟演绎不靠谱公司送好运！爆笑再现荒诞好梦！ 好运公司，一个帮你实现所有梦想的地方！姚客（白客 饰）与贾凌（黄才伦 饰）带头打造专业团队，无论多离谱的白日梦，都能化“荒诞”为神奇，让你美梦成真！在他们的帮助下，街头歌手赵清晨（李嘉琦 饰）“一夜成名”，助创业失败的阿伦（王大陆 饰）逆风翻盘，外卖小哥乔小杉（乔杉 饰）一夜暴富衣锦还乡！可天上真的会掉馅饼吗？他们究竟是被命运选中的“天选之子”，还是拿到了“好运一日体验卡”？梦醒之后，被“爆改”的人生轨迹又将通往何处……</span></p>','http://localhost:1000/file/88a078dbe8663ea23f5ccf1767fe38bf.jpg,http://localhost:1000/file/7171d04665e5bb8f2054a83d9b08e2ad.jpg,http://localhost:1000/file/5b6e9d9d8ce21a9574f846ceea925d24.jpg,http://localhost:1000/file/4f191e3f7b2809eca0d85b5e8cd0267e.jpg','2025-03-05 13:49:49',0,5);

--
-- Table structure for table `movie_browsing_history`
--

DROP TABLE IF EXISTS `movie_browsing_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_browsing_history` (
                                          `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                          `movie_id` int NOT NULL COMMENT '电影',
                                          `user_id` int NOT NULL COMMENT '用户',
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=424 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影浏览历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_browsing_history`
--

INSERT INTO `movie_browsing_history` VALUES (376,7,1,'2025-03-10 13:49:34');
INSERT INTO `movie_browsing_history` VALUES (380,4,1,'2025-03-10 14:19:05');
INSERT INTO `movie_browsing_history` VALUES (392,8,1,'2025-03-10 14:40:09');
INSERT INTO `movie_browsing_history` VALUES (417,3,1,'2025-03-10 16:23:54');
INSERT INTO `movie_browsing_history` VALUES (420,1,1,'2025-03-11 15:19:50');
INSERT INTO `movie_browsing_history` VALUES (421,9,1,'2025-03-11 15:28:37');
INSERT INTO `movie_browsing_history` VALUES (422,5,1,'2025-03-11 15:35:53');
INSERT INTO `movie_browsing_history` VALUES (423,2,1,'2025-03-11 15:36:06');

--
-- Table structure for table `movie_collect`
--

DROP TABLE IF EXISTS `movie_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_collect` (
                                 `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `movie_id` int NOT NULL COMMENT '电影',
                                 `user_id` int NOT NULL COMMENT '用户',
                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影收藏';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_collect`
--

INSERT INTO `movie_collect` VALUES (21,7,1,'2025-03-01 08:17:49');
INSERT INTO `movie_collect` VALUES (31,10,1,'2025-03-07 17:52:45');
INSERT INTO `movie_collect` VALUES (36,1,1,'2025-03-10 13:51:36');
INSERT INTO `movie_collect` VALUES (40,8,1,'2025-03-10 14:40:10');
INSERT INTO `movie_collect` VALUES (41,5,1,'2025-03-10 16:24:34');

--
-- Table structure for table `movie_order`
--

DROP TABLE IF EXISTS `movie_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_order` (
                               `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `cinema_id` int NOT NULL COMMENT '影院',
                               `movie_room_id` int NOT NULL COMMENT '影厅',
                               `movie_id` int NOT NULL COMMENT '电影',
                               `start_time` timestamp NOT NULL COMMENT '开始时间',
                               `end_time` timestamp NOT NULL COMMENT '结束时间',
                               `price` int NOT NULL COMMENT '价格',
                               `number_of_x` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '座位列数',
                               `number_of_y` int NOT NULL COMMENT '座位行数',
                               `screening_plan_id` int NOT NULL COMMENT '放映计划ID(用于查看已卖的座位)',
                               `order_evaluate_id` int DEFAULT NULL COMMENT '订单评价ID',
                               `payment_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
                               `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
                               `user_id` int NOT NULL COMMENT '用户ID',
                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影票订单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_order`
--

INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (1,1,1,1,'2025-03-09 17:25:42','2025-03-07 17:25:44',55,'2',9,2,NULL,'待取票',1,'2025-03-09 15:55:43');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (2,1,1,1,'2025-03-09 17:25:42','2025-03-07 17:25:44',55,'2',10,2,5,'已完成',1,'2025-03-09 15:55:43');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (3,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'3',7,3,9,'已完成',1,'2025-03-09 16:01:50');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (4,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'3',8,3,6,'已完成',1,'2025-03-09 16:01:50');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (5,1,1,1,'2025-03-09 17:25:42','2025-03-07 17:25:44',55,'3',6,2,8,'已完成',1,'2025-03-09 16:02:29');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (6,1,1,1,'2025-03-09 17:25:42','2025-03-07 17:25:44',55,'3',7,2,7,'已完成',1,'2025-03-09 16:02:29');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (7,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'4',7,3,NULL,'待取票',1,'2025-03-09 16:04:08');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (8,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'4',8,3,NULL,'待取票',1,'2025-03-09 16:04:08');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (9,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'4',9,3,NULL,'待取票',1,'2025-03-09 16:04:08');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (10,1,1,1,'2025-03-09 17:25:54','2025-03-07 17:25:55',12,'4',10,3,NULL,'已完成',1,'2025-03-09 16:04:08');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (11,1,1,1,'2025-03-13 17:25:54','2025-03-07 17:25:55',12,'2',6,3,NULL,'待支付',1,'2025-03-10 14:37:18');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (12,1,1,1,'2025-03-13 17:25:54','2025-03-07 17:25:55',12,'3',6,3,NULL,'待支付',1,'2025-03-10 14:37:18');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (13,1,1,1,'2025-03-13 17:25:54','2025-03-07 17:25:55',12,'4',6,3,NULL,'待支付',1,'2025-03-10 14:37:18');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (14,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'2',6,2,NULL,'待支付',1,'2025-03-10 16:18:25');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (15,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'2',7,2,NULL,'待支付',1,'2025-03-10 16:18:25');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (16,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'2',8,2,NULL,'待支付',1,'2025-03-10 16:18:25');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (17,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'3',8,2,NULL,'待取票',1,'2025-03-10 16:18:25');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (18,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'3',9,2,NULL,'待取票',1,'2025-03-10 16:18:25');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (19,3,4,1,'2025-03-12 16:00:00','2025-03-13 16:00:00',15,'4',7,5,NULL,'待取票',1,'2025-03-11 15:20:37');
INSERT INTO `movie_order` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`number_of_x`,`number_of_y`,`screening_plan_id`,`order_evaluate_id`,`status`,`user_id`,`create_time`) VALUES (20,3,4,1,'2025-03-12 16:00:00','2025-03-13 16:00:00',15,'4',8,5,NULL,'已完成',1,'2025-03-11 15:20:37');

--
-- Table structure for table `movie_order_evaluate`
--

DROP TABLE IF EXISTS `movie_order_evaluate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_order_evaluate` (
                                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                        `user_id` int NOT NULL COMMENT '用户',
                                        `movie_id` int NOT NULL COMMENT '电影',
                                        `movie_order_id` int NOT NULL COMMENT '订单',
                                        `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
                                        `rate` int NOT NULL COMMENT '评分',
                                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单评价';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_order_evaluate`
--

INSERT INTO `movie_order_evaluate` VALUES (3,1,2,4,'非常好看，很喜欢',5,'2025-03-01 08:17:11');
INSERT INTO `movie_order_evaluate` VALUES (4,1,1,11,'非常好',4,'2025-03-09 15:51:00');
INSERT INTO `movie_order_evaluate` VALUES (5,1,1,2,'非常好',3,'2025-03-09 15:55:53');
INSERT INTO `movie_order_evaluate` VALUES (6,1,1,4,'非查',3,'2025-03-09 16:02:12');
INSERT INTO `movie_order_evaluate` VALUES (7,1,1,6,'fff',4,'2025-03-09 16:02:35');
INSERT INTO `movie_order_evaluate` VALUES (8,1,1,5,'ff',5,'2025-03-09 16:02:38');
INSERT INTO `movie_order_evaluate` VALUES (9,1,1,3,'非常好',4,'2025-03-10 12:27:43');

--
-- Table structure for table `movie_region`
--

DROP TABLE IF EXISTS `movie_region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_region` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                                `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
                                `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影区域';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_region`
--

INSERT INTO `movie_region` VALUES (1,'大陆',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (2,'美国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (3,'韩国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (4,'日本',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (5,'中国香港',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (6,'中国澳门',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (7,'中国台湾',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (8,'泰国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (9,'印度',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (10,'法国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (11,'英国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (12,'俄罗斯',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (13,'意大利',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (14,'西班牙',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (15,'德国',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (16,'波兰',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (17,'澳大利亚',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (18,'伊朗',NULL,'2025-03-04 15:49:10');
INSERT INTO `movie_region` VALUES (19,'其他',NULL,'2025-03-04 15:49:10');

--
-- Table structure for table `movie_room`
--

DROP TABLE IF EXISTS `movie_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_room` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                              `label` text COLLATE utf8mb4_unicode_ci COMMENT '标签',
                              `cinema_id` int NOT NULL COMMENT '影院',
                              `number_of_x` int NOT NULL COMMENT '座位列数',
                              `number_of_y` int NOT NULL COMMENT '座位行数',
                              `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影影厅';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_room`
--

INSERT INTO `movie_room` VALUES (1,'1号沙发激光巨幕厅','4K,IMAX,全新设备',1,10,10,'2025-03-07 17:34:31');
INSERT INTO `movie_room` VALUES (2,'2号沙发激光巨幕厅','4K,IMAX,全新设备',1,10,10,'2025-03-07 17:34:40');
INSERT INTO `movie_room` VALUES (3,'1号激光厅','4K,IMAX,全新设备',2,10,10,'2025-03-07 18:25:54');
INSERT INTO `movie_room` VALUES (4,'1号激光厅','4K, IMAX, Dolby Atmos, VIP座椅, 3D',3,11,11,'2025-03-11 15:18:45');
INSERT INTO `movie_room` VALUES (5,'1号激光','环绕立体声, 4D体验, 高保真音响, 影院吧台, 舒适环境',3,12,13,'2025-03-11 15:19:08');

--
-- Table structure for table `movie_type`
--

DROP TABLE IF EXISTS `movie_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_type` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                              `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '备注',
                              `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_type`
--

INSERT INTO `movie_type` VALUES (1,'爱情',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (2,'喜剧',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (3,'动画',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (4,'剧情',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (5,'恐怖',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (6,'惊悚',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (7,'科幻',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (8,'动作',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (9,'悬疑',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (10,'犯罪',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (11,'冒险',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (12,'战争',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (13,'奇幻',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (14,'运动',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (15,'家庭',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (16,'古装',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (17,'武侠',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (18,'西部',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (19,'历史',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (20,'传记',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (21,'歌舞',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (22,'黑色电影',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (23,'短片',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (24,'纪录片',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (25,'戏曲',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (26,'音乐',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (27,'灾难',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (28,'青春',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (29,'儿童',NULL,'2025-03-04 15:51:31');
INSERT INTO `movie_type` VALUES (30,'其他',NULL,'2025-03-04 15:51:31');

--
-- Table structure for table `movie_years`
--

DROP TABLE IF EXISTS `movie_years`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie_years` (
                               `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
                               `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
                               `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='电影年代';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie_years`
--

INSERT INTO `movie_years` VALUES (1,'2029',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (2,'2028',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (3,'2027',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (4,'2026',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (5,'2025',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (6,'2024',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (7,'2023',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (8,'2022',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (9,'2021',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (10,'2020',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (11,'2019',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (12,'2018',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (13,'2017',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (14,'2016',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (15,'2015',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (16,'2014',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (17,'2013',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (18,'2012',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (19,'2011',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (20,'2000-2010',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (21,'90年代',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (22,'80年代',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (23,'70年代',NULL,'2025-03-04 15:48:57');
INSERT INTO `movie_years` VALUES (24,'更早',NULL,'2025-03-04 15:48:57');

--
-- Table structure for table `screening_plan`
--

DROP TABLE IF EXISTS `screening_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `screening_plan` (
                                  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                                  `cinema_id` int NOT NULL COMMENT '影院',
                                  `movie_room_id` int NOT NULL COMMENT '影厅',
                                  `movie_id` int NOT NULL COMMENT '电影',
                                  `start_time` timestamp NOT NULL COMMENT '开始时间',
                                  `end_time` timestamp NOT NULL COMMENT '结束时间',
                                  `price` int NOT NULL COMMENT '价格',
                                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='放映计划';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `screening_plan`
--

INSERT INTO `screening_plan` VALUES (1,1,1,2,'2025-03-09 17:24:24','2025-03-07 17:24:27',45,'2025-03-07 17:25:28');
INSERT INTO `screening_plan` VALUES (2,1,1,1,'2025-03-10 17:25:42','2025-03-07 17:25:44',55,'2025-03-07 17:25:46');
INSERT INTO `screening_plan` VALUES (3,1,1,1,'2025-03-13 17:25:54','2025-03-07 17:25:55',12,'2025-03-07 17:26:01');
INSERT INTO `screening_plan` VALUES (4,2,3,1,'2025-03-09 16:00:00','2025-03-03 16:00:00',56,'2025-03-07 18:26:15');
INSERT INTO `screening_plan` VALUES (5,3,4,1,'2025-03-12 16:00:00','2025-03-13 16:00:00',15,'2025-03-11 15:19:29');

--
-- Table structure for table `slideshow`
--

DROP TABLE IF EXISTS `slideshow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slideshow` (
                             `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
                             `main_img` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图',
                             `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链接',
                             `sort` int NOT NULL COMMENT '排序',
                             `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='轮播图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slideshow`
--

INSERT INTO `slideshow` VALUES (1,'哪吒','http://localhost:1000/file/fba87a4f650ce19880d4abc132f5f6a6.jpg','movieDetails/1',3,'2025-03-01 14:54:49');
INSERT INTO `slideshow` VALUES (4,'帕丁顿熊3','http://localhost:1000/file/b8aa8657d4097ffb61d5d9aee716d7b8.jpeg','movieDetails/5',2,'2025-03-01 07:27:35');

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
                        `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
                        `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '昵称',
                        `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '头像',
                        `tel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
                        `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
                        `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
                        `balance` int NOT NULL COMMENT '余额',
                        `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='普通用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

INSERT INTO `user` VALUES (1,'user1','123456','用户1','http://localhost:1000/file/e5764188ea16f355ffc2dfc84858b48f.png',NULL,NULL,'启用',8915,'2025-02-28 17:04:40');

-- ------------------------------------------------------
-- Delivery seed refresh (2026-04-07)
-- 统一修正客户反馈：影院基础信息、首页轮播、上映/即将上映数据、筛选数据与演示排片
-- ------------------------------------------------------
UPDATE `cinema` SET `username`='c1', `password`='123456', `nickname`='惠友影院', `tel`='13022506541', `email`=NULL, `status`='启用', `address`='河北省曲阳县惠友影院', `label`='激光厅,可选座,停车方便' WHERE `id`=1;
UPDATE `cinema` SET `username`='c2', `password`='123456', `nickname`='凯嘉影院', `tel`='18775612310', `email`=NULL, `status`='启用', `address`='河北省曲阳县凯嘉影院', `label`='杜比厅,情侣座,交通便利' WHERE `id`=2;
UPDATE `cinema` SET `username`='c3', `password`='123456', `nickname`='永宁影院', `tel`='19765405897', `email`=NULL, `status`='启用', `address`='河北省曲阳县永宁影院', `label`='IMAX厅,家庭友好,商圈周边' WHERE `id`=3;

UPDATE `slideshow` SET `title`='帕丁顿熊3', `link`='movieDetails/5' WHERE `id`=4;

UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 20 DAY), '%Y-%m-%d 09:00') WHERE `id`=1;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 18 DAY), '%Y-%m-%d 09:00') WHERE `id`=2;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 40 DAY), '%Y-%m-%d 19:00'), `movie_region_id`=13 WHERE `id`=3;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 10 DAY), '%Y-%m-%d 18:00'), `movie_region_id`=7 WHERE `id`=4;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 DAY), '%Y-%m-%d 10:00'), `movie_region_id`=11 WHERE `id`=5;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 25 DAY), '%Y-%m-%d 18:00') WHERE `id`=6;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 16 DAY), '%Y-%m-%d 19:00') WHERE `id`=7;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '%Y-%m-%d 18:00') WHERE `id`=8;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 23 DAY), '%Y-%m-%d 20:00'), `movie_region_id`=5 WHERE `id`=9;
UPDATE `movie` SET `release_date` = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 30 DAY), '%Y-%m-%d 18:30') WHERE `id`=10;

DELETE FROM `screening_plan`;
INSERT INTO `screening_plan` (`id`,`cinema_id`,`movie_room_id`,`movie_id`,`start_time`,`end_time`,`price`,`create_time`) VALUES
(1,1,1,1,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 13 HOUR), INTERVAL 144 MINUTE),55,NOW()),
(2,1,2,2,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 18 HOUR), INTERVAL 136 MINUTE),49,NOW()),
(3,2,3,3,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 15 HOUR), INTERVAL 125 MINUTE),38,NOW()),
(4,2,3,5,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 19 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 3 DAY), INTERVAL 19 HOUR), INTERVAL 107 MINUTE),52,NOW()),
(5,3,4,1,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 16 HOUR), INTERVAL 144 MINUTE),46,NOW()),
(6,3,5,8,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 20 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 2 DAY), INTERVAL 20 HOUR), INTERVAL 116 MINUTE),44,NOW()),
(7,1,1,6,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 19 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 4 DAY), INTERVAL 19 HOUR), INTERVAL 122 MINUTE),41,NOW()),
(8,2,3,2,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 13 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 13 HOUR), INTERVAL 136 MINUTE),48,NOW()),
(9,3,4,5,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 6 DAY), INTERVAL 14 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 6 DAY), INTERVAL 14 HOUR), INTERVAL 107 MINUTE),50,NOW()),
(10,1,2,3,DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 19 HOUR),DATE_ADD(DATE_ADD(DATE_ADD(CURDATE(), INTERVAL 5 DAY), INTERVAL 19 HOUR), INTERVAL 125 MINUTE),42,NOW());

DELETE FROM `movie_order_evaluate`;
DELETE FROM `movie_order`;
DELETE FROM `movie_collect`;
INSERT INTO `movie_collect` (`id`,`movie_id`,`user_id`,`create_time`) VALUES
(1,4,1,DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2,7,1,DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3,9,1,NOW());

DELETE FROM `movie_browsing_history`;
INSERT INTO `movie_browsing_history` (`id`,`movie_id`,`user_id`,`create_time`) VALUES
(1,1,1,DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(2,5,1,DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(3,8,1,DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4,2,1,DATE_SUB(NOW(), INTERVAL 1 HOUR));

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-11 23:57:56
