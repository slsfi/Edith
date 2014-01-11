define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!templates/header.html'],
        function($, _, Backbone, vent, Handlebars, template) {
  var Header = Backbone.View.extend({
    template: Handlebars.compile(template),
    
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },
   
    render: function() {
      this.$el.html(this.template());
      //var id = window.location.pathname.substr(1).split('.')[0];
      var id = _.last(window.location.pathname.split('/')).split('.')[0]
      this.$('#' + id).parent().addClass('active');
    }
  });
  
  return Header;
});k
