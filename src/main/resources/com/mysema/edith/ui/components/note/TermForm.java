package com.mysema.edith.ui.components.note;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Term;
import com.mysema.edith.services.TermDao;
import com.mysema.edith.ui.components.InfoMessage;
import com.sun.xml.internal.ws.api.PropertySet.Property;

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
