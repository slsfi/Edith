package fi.finlit.edith.ui.components.note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.User;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.domain.UserRepository;
import fi.finlit.edith.ui.services.DocumentNoteSearchInfo;

public class NoteSearchForm {
        
    @Property
    private DocumentNoteSearchInfo searchInfo;
    
    @SuppressWarnings("unused")
    @Parameter
    private List<DocumentNote> documentNotes;
    
    @Parameter
    private Block notesList;
    
    @Property
    private NoteType type;
    
    @Property
    private NoteFormat format;
    
    @Property
    private UserInfo user;
    
    @Property
    private Document document;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private DocumentNoteRepository documentNoteRepository;
    
    @Inject
    private DocumentRepository documentRepository;
    
    void onPrepare(){
        searchInfo = new DocumentNoteSearchInfo();
    }
    
    Object onSuccessFromNoteSearchForm() throws IOException {
        documentNotes = documentNoteRepository.query(searchInfo);
        return notesList;
    }
    
    public Collection<UserInfo> getUsers(){
        Collection<User> users = userRepository.getAll(); 
        List<UserInfo> userIds = new ArrayList<UserInfo>(users.size());
        for (User u : users){
            userIds.add(new UserInfo(u.getUsername()));
        }
        return userIds;
    }
    
    public NoteType[] getTypes() {
        return NoteType.values();
    }
    
    public NoteFormat[] getFormats(){
        return NoteFormat.values();
    }

    public boolean isTypeSelected() {
        return searchInfo.getNoteTypes().contains(type);
    }
    
    public void setTypeSelected(boolean selected) {
        if (selected) {
            searchInfo.getNoteTypes().add(type);
        } else {
            searchInfo.getNoteTypes().remove(type);
        }
    }
    
    public boolean isFormatSelected(){
        return searchInfo.getNoteFormats().contains(format);
    }
    
    public void setFormatSelected(boolean selected){
        if (selected) {
            searchInfo.getNoteFormats().add(format);
        } else {
            searchInfo.getNoteFormats().remove(format);
        }
    }
    
    public boolean isUserSelected(){
        return searchInfo.getCreators().contains(user);
    }
    
    public void setUserSelected(boolean selected){
        if (selected) {
            searchInfo.getCreators().add(user);
        } else {
            searchInfo.getCreators().remove(user);
        }
    }
    
    public Collection<Document> getDocuments(){
        return documentRepository.getAll();
    }
    
    public boolean isDocumentSelected(){
        return searchInfo.getDocuments().contains(document);
    }
    
    public void setDocumentSelected(boolean selected){
        if (selected) {
            searchInfo.getDocuments().add(document);
        } else {
            searchInfo.getDocuments().remove(document);
        }
    }
    
    
}
