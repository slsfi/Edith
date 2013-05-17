require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'localize', 'text!/templates/header.html',
           'text!/templates/note-import.html'],
          function($, _, Backbone, Handlebars, localize, headerTemplate, importTemplate) {
    var headerTmpl = Handlebars.compile(headerTemplate);
    $('body').prepend(headerTmpl());
    
    var importTmpl = Handlebars.compile(importTemplate);

    $('#content').html(importTmpl());
    
    var NoteImport = Backbone.View.extend({
      events: {'submit .import': 'submit'},

      initialize: function() {
        _.bindAll(this);
      },

      submit: function() {
        var self = this;
        var formData = new FormData(this.$(".import").get(0));
        $.ajax('api/notes',
            {type: 'post',
             processData: false,
             contentType: false,
             data: formData,
             success: function(data) {
               console.log(data);
               self.$(".alert").html("Imported " + data + " notes").show();
             }});
        return false;
      }
    });

    new NoteImport({el: $('#content')});
  });
});
