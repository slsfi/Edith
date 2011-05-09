/**
 * 
 */
package fi.finlit.edith.ui.test.services;

import java.lang.annotation.Annotation;

import org.apache.tapestry5.PropertyConduit;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.beaneditor.PropertyModel;

import fi.finlit.edith.domain.DocumentNote;

final class PropertyModelMock implements PropertyModel {

    @Override
    public PropertyModel dataType(String dataType) {
        return null;
    }

    @Override
    public PropertyConduit getConduit() {
        return null;
    }

    @Override
    public String getDataType() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getPropertyName() {
        return "longText";
    }

    @Override
    public Class<String> getPropertyType() {
        return String.class;
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    @Override
    public PropertyModel label(String label) {
        return null;
    }

    @Override
    public BeanModel<DocumentNote> model() {
        return null;
    }

    @Override
    public PropertyModel sortable(boolean sortable) {
        return null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return null;
    }
}