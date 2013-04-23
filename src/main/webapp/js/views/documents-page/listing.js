define(['jquery', 'underscore', 'backbone', 'js/vent',
        'js/views/documents-page/listing/documents',
        'js/views/documents-page/listing/comments'],
  function($, _, Backbone, vent, DocumentsView, CommentsView) {
  var ListingView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },
    
    render: function() {
      new DocumentsView({el: this.$('#documents')});
      new CommentsView({el: this.$('#comments')});
    }
  })
  
  return ListingView;
});