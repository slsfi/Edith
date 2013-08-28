package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.FullDocumentNoteTO;

public class ConverterPerfTest {
	
	private final Converter converter = new Converter();

	@Test
	public void test() {
		List<DocumentNote> documentNotes = new ArrayList<DocumentNote>();
		for (int i = 0; i < 1000; i++) {
			Document document = new Document();
			document.setId((long)i);
			document.setPath("/docs/" + i);
			document.setTitle("Document #" + i);
			
			User user = new User();			
			user.setFirstName("John" + i);
			user.setLastName("Doe" + i);
			user.setEmail("john.doe" + i + "@acme.com");
			
			Note note = new Note();
			note.setDescription(String.valueOf(i));
			note.setFormat(NoteFormat.NOTE);
			note.setLastEditedBy(user);
			
			DocumentNote documentNote = new DocumentNote();
			documentNote.setDocument(document);
			documentNote.setFullSelection(String.valueOf(i));
			documentNote.setLemmaPosition(String.valueOf(i));
			documentNote.setNote(note);
			documentNote.setShortenedSelection(String.valueOf(i));
			documentNotes.add(documentNote);
		}
		
		List<FullDocumentNoteTO> tos = new ArrayList<FullDocumentNoteTO>(documentNotes.size());
		long start = System.currentTimeMillis();
		for (DocumentNote documentNote : documentNotes) {
			tos.add(converter.convert(documentNote, FullDocumentNoteTO.class));
		}
		long end = System.currentTimeMillis();
		System.err.println(end - start);
	}
	
}
