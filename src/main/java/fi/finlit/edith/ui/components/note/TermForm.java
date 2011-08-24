package fi.finlit.edith.ui.components.note;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.ui.components.InfoMessage;
import fi.finlit.edith.ui.services.TermDao;

@SuppressWarnings("unused")
public class TermForm {
    @InjectComponent
    private InfoMessage infoMessage;

    @Parameter
    @Property
    private Block closeDialog;

    @Property
    @Parameter
    private Long termId;

    @Parameter
    @Property
    private Zone termZone;

    private Term term;

    @Inject
    private TermDao termDao;

    public void beginRender() {
        if (termId == null) {
            term = new Term();
        } else {
            term = termDao.getById(termId);
        }
    }

    public Term getTerm() {
        return term;
    }

    void onPrepareFromTermForm() {
        if (term == null) {
            term = new Term();
        }
    }

    void onPrepareFromTermForm(long id) {
        if (term == null) {
            term = termDao.getById(id);
        }
    }

    public Object onSuccessFromTermForm() {
        if (StringUtils.isNotBlank(term.getBasicForm())) {
            termDao.save(getTerm());
            termId = getTerm().getId();
            infoMessage.addInfoMsg("create-success");
            MultiZoneUpdate update = new MultiZoneUpdate("dialogZone", closeDialog).add(
                    "infoMessageZone", infoMessage.getBlock());
            if (termZone != null) {
                update = update.add("termZone", termZone.getBody());
            }
            return update;
        }
        return null;
    }
}
