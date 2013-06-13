define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/metadata-field-select.html'],
       function($, _, Backbone, vent, Handlebars, template) {
  var MetadataFieldSelect = Backbone.View.extend({
    template: Handlebars.compile(template),
    
    initialize: function() {
      _.bindAll(this, 'render', 'getColumns');
      this.render();
    },
    
    render: function() {
      var self = this;
      this.$el.html(this.template);
      this.$('select').multiselect({
        onChange: function(element, checked) {
                    vent.trigger('metadata-field-select:change', self.getColumns());
                  }
      });
    },

    getColumns: function() {
      return this.$('option:selected').map(function(idx, el) {
                                             return $(el).val();
                                           });
    }
  });
  
  return MetadataFieldSelect;
});
