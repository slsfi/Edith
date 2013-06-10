define(['jquery', 'spin', 'vent'],
       function($, Spinner, vent) {

  var options = {lines: 13, // The number of lines to draw
                 length: 5, // The length of each line
                 width: 5, // The line thickness
                 radius: 10, // The radius of the inner circle
                 corners: 1, // Corner roundness (0..1)
                 rotate: 0, // The rotation offset
                 direction: 1, // 1: clockwise, -1: counterclockwise
                 color: '#000', // #rgb or #rrggbb
                 speed: 1, // Rounds per second
                 trail: 60, // Afterglow percentage
                 shadow: false, // Whether to render a shadow
                 hwaccel: false, // Whether to use hardware acceleration
                 className: 'spinner', // The CSS class to assign to the spinner
                 zIndex: 2e9, // The z-index (defaults to 2000000000)
                 top: 'auto', // Top position relative to parent in px
                 left: 'auto' // Left position relative to parent in px
                };

  var sp = new Spinner(options);
  var $el = $('#spinner');

  var spinner = function() {
    sp.spin($el.show().get(0));
    var events = arguments;
    var n = 0;
    _.each(events, function(evt) {
                     vent.once(evt, function() {
                       ++n;
                       if (n >= events.length) {
                         $el.hide();
                         sp.stop();
                       }
                     });
                   });
  }

  return spinner;
});
