SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `path` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `documentnote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdOn` bigint(20) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `fullSelection` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `lemmaPosition` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `position` int(11) NOT NULL,
  `publishable` bit(1) NOT NULL,
  `revision` bigint(20) DEFAULT NULL,
  `shortenedSelection` varchar(1024) COLLATE utf8_swedish_ci DEFAULT NULL,
  `document_id` bigint(20) DEFAULT NULL,
  `note_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_DOCUMENTNOTE_NOTEID` (`note_id`),
  KEY `FK_DOCUMENTNOTE_DOCUMENTID` (`document_id`),
  CONSTRAINT `FK_DOCUMENTNOTE_NOTEID` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  CONSTRAINT `FK_DOCUMENTNOTE_DOCUMENTID` FOREIGN KEY (`document_id`) REFERENCES `document` (`id`),
  INDEX del_pub_idx (deleted, publishable),
  INDEX del_rev (deleted, revision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `nameform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `first` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `last` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX name_id (first, last)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text COLLATE utf8_swedish_ci,
  `documentNoteCount` int(11) NOT NULL,
  `editedOn` bigint(20) DEFAULT NULL,
  `format` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `lemma` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `lemmaMeaning` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `sources` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `subtextSources` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `lastEditedBy_id` bigint(20) DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `place_id` bigint(20) DEFAULT NULL,
  `term_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_NOTE_LASTEDITEDBYID` (`lastEditedBy_id`),
  KEY `FK_NOTE_PLACEID` (`place_id`),
  KEY `FK_NOTE_PERSONID` (`person_id`),
  KEY `FK_NOTE_TERMID` (`term_id`),
  CONSTRAINT `FK_NOTE_TERMID` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`),
  CONSTRAINT `FK_NOTE_LASTEDITEDBYID` FOREIGN KEY (`lastEditedBy_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTE_PERSONID` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
  CONSTRAINT `FK_NOTE_PLACEID` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`), 
  INDEX count_idx (documentNoteCount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `note_types` (
  `Note_id` bigint(20) NOT NULL,
  `types` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  KEY `FK_NOTETYPES_NOTEID` (`Note_id`),
  CONSTRAINT `FK_NOTETYPES_NOTEID` FOREIGN KEY (`Note_id`) REFERENCES `note` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `note_user` (
  `Note_id` bigint(20) NOT NULL,
  `allEditors_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Note_id`,`allEditors_id`),
  KEY `FK_NOTEUSER_NOTEID` (`Note_id`),
  KEY `FK_NOTEUSER_ALLEDITORSID` (`allEditors_id`),
  CONSTRAINT `FK_NOTEUSER_ALLEDITORSID` FOREIGN KEY (`allEditors_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_NOTEUSER_NOTEID` FOREIGN KEY (`Note_id`) REFERENCES `note` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `notecomment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdAt` datetime DEFAULT NULL,
  `message` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `note_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_NOTECOMMENT_NOTEID` (`note_id`),
  CONSTRAINT `FK_NOTECOMMENT_NOTEID` FOREIGN KEY (`note_id`) REFERENCES `note` (`id`),
  INDEX created_idx (createdAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `time_of_birth_end` datetime DEFAULT NULL,
  `time_of_birth_start` datetime DEFAULT NULL,
  `time_of_death_end` datetime DEFAULT NULL,
  `time_of_death_start` datetime DEFAULT NULL,
  `normalizedForm_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PERSON_NORMALIZEDFORMID` (`normalizedForm_id`),
  CONSTRAINT `FK_PERSON_NORMALIZEDFORMID` FOREIGN KEY (`normalizedForm_id`) REFERENCES `nameform` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `person_nameform` (
  `Person_id` bigint(20) NOT NULL,
  `otherForms_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Person_id`,`otherForms_id`),
  KEY `FK_PERSONNAMEFORM_PERSONID` (`Person_id`),
  KEY `FK_PERSONNAMEFORM_OTHERFORMSID` (`otherForms_id`),
  CONSTRAINT `FK_PERSONNAMEFORM_OTHERFORMSID` FOREIGN KEY (`otherForms_id`) REFERENCES `nameform` (`id`),
  CONSTRAINT `FK_PERSONNAMEFORM_PERSONID` FOREIGN KEY (`Person_id`) REFERENCES `person` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `place` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `normalizedForm_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PLACE_NORMALIZEDFORMID` (`normalizedForm_id`),
  CONSTRAINT `FK_PLACE_NORMALIZEDFORMID` FOREIGN KEY (`normalizedForm_id`) REFERENCES `nameform` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `place_nameform` (
  `Place_id` bigint(20) NOT NULL,
  `otherForms_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Place_id`,`otherForms_id`),
  KEY `FK_PLACENAMEFORM_PLACEID` (`Place_id`),
  KEY `FK_PLACENAMEFORM_OTHERFORMS_ID` (`otherForms_id`),
  CONSTRAINT `FK_PLACENAMEFORM_OTHERFORMS_ID` FOREIGN KEY (`otherForms_id`) REFERENCES `nameform` (`id`),
  CONSTRAINT `FK_PLACENAMEFORM_PLACEID` FOREIGN KEY (`Place_id`) REFERENCES `place` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `term` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `basicForm` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `language` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `meaning` text COLLATE utf8_swedish_ci DEFAULT NULL,
  `otherLanguage` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX basic_form_idx (basicForm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `firstName` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `lastName` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `profile` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

SET FOREIGN_KEY_CHECKS = 1;
