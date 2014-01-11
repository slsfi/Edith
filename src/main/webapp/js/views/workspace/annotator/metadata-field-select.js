define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'localize',
        'text!templates/workspace/annotator/metadata-field-select.html'],
       function($, _, Backbone, vent, Handlebars, localize, template) {
  var MetadataFieldSelect = Backbone.View.extend({
    template: Handlebars.compile(template),

    initialize: function() {
      this.defaultSelection = this.options.defaultSelection;
      _.bindAll(this, 'render', 'getColumns');
      this.render();
    },
    
    render: function() {
      var self = this;
      this.$el.html(this.template);

      _.each(this.defaultSelection, function(option) {
        self.$('option[value="' + option + '"]').attr('selected', 'selected');
      });
      this.$('select').multiselect({
        buttonText: function(options, select) {
                      if (options.length == 0) {
                        return localize('nothing-selected');
                      } else if (options.length > 4) {
                        return options.length + ' ' + localize('n-selected') + ' <b class="caret"></b>';
                      } else {
                        var selected = '';
                        options.each(function() {
                          selected += $(this).text() + ', ';
                        });
                        return selected.substr(0, selected.length -2) + ' <b class="caret"></b>';
                      }
                    },

        onChange: function(element, checked) {
                    self.columns = self.$('option:selected').map(function(idx, el) {
                                                                   return $(el).val();
                                                                 });

                    vent.trigger('metadata-field-select:change', self.columns);
                  }
      });

      this.columns = this.$('option:selected').map(function(idx, el) {
                                                     return $(el).val();
                                                   });
    },

    getColumns: function() {
      return this.columns;
    }
  });
  
  return MetadataFieldSelect;
});
