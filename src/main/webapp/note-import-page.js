require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'text!templates/header.html'],
          function($, _, Backbone, headerTemplate) {
    $('body').prepend(headerTemplate);
    
    var NoteImport = Backbone.View.extend({
      events: {'submit #import': 'submit'},
      
      initialize: function() {
        _.bindAll(this);
      },
      
      submit: function() {
        var self = this;        
        var formData = new FormData(this.$("#import").get(0));
        $.ajax('api/notes/import',
            {type: 'post', 
             processData: false,
             contentType: false,
             data: formData,
             success: function(data) {
               //console.log(data);
               self.$(".alert").html("Imported " + data + " notes").show();
             }});
        return false;
      }
    });
    
    new NoteImport({el: $('body')});
  });
});
