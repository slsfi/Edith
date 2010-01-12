package fi.finlit.edith.ui.components;

import org.apache.tapestry5.annotations.Mixin;
import org.apache.tapestry5.corelib.components.Hidden;

import fi.finlit.edith.ui.mixins.InlineMixin;

@SuppressWarnings("unused")
public class InlineField extends Hidden {

    @Mixin
    private InlineMixin inlineMixin;
    
}
