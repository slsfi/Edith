/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.domain.QDocument.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.commons.lang.Assert;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.dto.DocumentRevision;
import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteAdditionFailedException;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.ServiceException;
import fi.finlit.edith.ui.services.TimeService;
import fi.finlit.edith.ui.services.svn.FileItem;
import fi.finlit.edith.ui.services.svn.FileItemWithDocumentId;
import fi.finlit.edith.ui.services.svn.RevisionInfo;
import fi.finlit.edith.ui.services.svn.SubversionService;
import fi.finlit.edith.ui.services.svn.UpdateCallback;
import fi.finlit.edith.util.ElementContext;

public class DocumentDaoImpl {

}
