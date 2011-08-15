ALTER TABLE person DROP FOREIGN KEY FK_PERSON_NORMALIZEDFORMID;

ALTER TABLE place DROP FOREIGN KEY FK_PLACE_NORMALIZEDFORMID;

ALTER TABLE person
    DROP COLUMN normalizedForm_id;
    
ALTER TABLE place
    DROP COLUMN normalizedForm_id;

DROP TABLE place_nameform;

DROP TABLE person_nameform;
    
DROP TABLE nameform;

ALTER TABLE person
    ADD COLUMN description text COLLATE utf8_swedish_ci DEFAULT NULL,
    ADD COLUMN first       varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
    ADD COLUMN last        varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL;
               
ALTER TABLE place
    ADD COLUMN description text COLLATE utf8_swedish_ci DEFAULT NULL,
    ADD COLUMN first       varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
    ADD COLUMN last        varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL;

CREATE TABLE person_nameform ( 
               person_id bigint(20) NOT NULL,
               description text COLLATE utf8_swedish_ci DEFAULT NULL,
               first       varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
               last        varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
               KEY `FK_NAMEFORM_PERSONID` (`person_id`),
               CONSTRAINT `FK_NAMEFORM_PERSONID` FOREIGN KEY (`person_id`) REFERENCES `person` (`id`),
               INDEX person_nameform_idx (person_id),
               INDEX person_nameform_last_idx (last)
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;

CREATE TABLE place_nameform ( 
               place_id bigint(20) NOT NULL,
               description text COLLATE utf8_swedish_ci DEFAULT NULL,
               first       varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
               last        varchar(255) COLLATE utf8_swedish_ci DEFAULT NULL,
               KEY `FK_NAMEFORM_PLACEID` (`place_id`),
               CONSTRAINT `FK_NAMEFORM_PLACEID` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
               INDEX place_nameform_idx (place_id),
               INDEX place_nameform_last_idx (last)
               ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_swedish_ci;