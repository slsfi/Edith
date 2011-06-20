ALTER TABLE `note_types` MODIFY COLUMN `types`
    ENUM('WORD_EXPLANATION', 'LITERARY','HISTORICAL','DICTUM','CRITIQUE',
    'TITLE','TRANSLATION','REFERENCE')  CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL;

CREATE INDEX note_types_idx ON note_types (types);

ALTER TABLE `note` MODIFY COLUMN `status`
    ENUM('INITIAL','DRAFT', 'FINISHED')  CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL;

CREATE INDEX note_status_idx ON note(status);

ALTER TABLE `note` MODIFY COLUMN `format`
    ENUM('NOTE','PLACE', 'PERSON')  CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL;

CREATE INDEX note_format_idx ON note(format);
    
ALTER TABLE `term` MODIFY COLUMN `language`
    ENUM('FINNISH', 'SWEDISH', 'FRENCH', 'LATIN', 'GERMAN', 'RUSSIAN', 'ENGLISH', 'ITALIAN', 'GREEK', 'OTHER')  
    CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL;
    
CREATE INDEX term_lang_idx ON term(language);
    