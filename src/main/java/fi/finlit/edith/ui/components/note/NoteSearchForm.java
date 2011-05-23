package fi.finlit.edith.ui.components.note;

import java.util.Collection;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.OrderBy;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.UserRepository;

@SuppressWarnings("unused")
public class NoteSearchForm {

    @InjectPage
    private Annotate page;

    @SessionState(create = false)
    private Collection<Document> selectedDocuments;

    @Property
    private NoteType type;

    @Property
    private NoteFormat format;

    @Property
    private UserInfo user;

    @Inject
    private UserRepository userRepository;

    @Property
    private OrderBy loopedOrderBy;

    public int getSelectedDocumentCount() {
        return selectedDocuments != null ? selectedDocuments.size() : 0;
    }

    Object onSuccessFromNoteSearchForm() {
        return page.getSearchResults().getBlock();
    }

    public Collection<UserInfo> getUsers() {
        return userRepository.getAllUserInfos();
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public NoteFormat[] getFormats() {
        return NoteFormat.values();
    }

    public boolean isTypeSelected() {
        return page.getSearchInfo().getNoteTypes().contains(type);
    }

    public void setTypeSelected(boolean selected) {
        if (selected) {
            page.getSearchInfo().getNoteTypes().add(type);
        } else {
            getSearchInfo().getNoteTypes().remove(type);
        }
    }

    public boolean isFormatSelected() {
        return getSearchInfo().getNoteFormats().contains(format);
    }

    public void setFormatSelected(boolean selected) {
        if (selected) {
            getSearchInfo().getNoteFormats().add(format);
        } else {
            getSearchInfo().getNoteFormats().remove(format);
        }
    }

    public boolean isUserSelected() {
        return getSearchInfo().getCreators().contains(user);
    }

    public void setUserSelected(boolean selected) {
        if (selected) {
            getSearchInfo().getCreators().add(user);
        } else {
            getSearchInfo().getCreators().remove(user);
        }
    }

    public void setOrderBy(OrderBy orderBy) {
        getSearchInfo().setOrderBy(orderBy);
    }

    public OrderBy getOrderBy() {
        return getSearchInfo().getOrderBy();
    }

    public OrderBy[] getOrderBys() {
        return OrderBy.values();
    }

    public boolean isReversed() {
        return !getSearchInfo().isAscending();
    }

    public void setReversed(boolean reversed) {
        getSearchInfo().setAscending(!reversed);
    }

    public boolean isOrphans() {
        return getSearchInfo().isOrphans();
    }

    public void setOrphans(boolean orphans) {
        getSearchInfo().setOrphans(orphans);
    }

    public DocumentNoteSearchInfo getSearchInfo() {
        return page.getSearchInfo();
    }

}
