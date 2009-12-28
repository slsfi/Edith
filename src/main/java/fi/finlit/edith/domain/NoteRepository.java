/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import org.springframework.transaction.annotation.Transactional;

/**
 * NoteRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface NoteRepository extends Repository<Note,String>{

}
