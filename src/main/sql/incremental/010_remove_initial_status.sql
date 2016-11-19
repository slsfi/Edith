UPDATE note SET status = 'DRAFT' WHERE status ='INITIAL';

ALTER TABLE note ADD new_status_column ENUM('DRAFT', 'FINISHED') CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL ;
UPDATE note SET new_status_column = status;
ALTER TABLE note DROP status;
ALTER TABLE note CHANGE new_status_column status ENUM('DRAFT', 'FINISHED') CHARACTER SET utf8 COLLATE utf8_swedish_ci DEFAULT NULL ;