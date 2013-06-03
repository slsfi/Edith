define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/metadata-field-select.html'],
       function($, _, Backbone, vent, Handlebars, template) {
  var MetadataFieldSelect = Backbone.View.extend({
    template: Handlebars.compile(template),
    
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },
    
    render: function() {
      this.$el.html(this.template);
      this.$('select').multiselect({
        onChange:function(element, checked){
          var columns =  self.$('option:selected').map(function(idx, el) {
                                                         return $(el).val();
                                                       });
          vent.trigger('metadata-field-select:change', columns);
        }
      });
    }
  });
  
  return MetadataFieldSelect;
});
