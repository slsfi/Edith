window.rconfig = {
  paths: {
    jquery: 'http://code.jquery.com/jquery-1.8.2',
    jqueryui: 'http://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.9.1/jquery-ui.min',
    dynatree: 'js/libs/dynatree/jquery.dynatree-1.2.4',
    underscore: 'http://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.4.2/underscore-min',
    backbone: 'http://cdnjs.cloudflare.com/ajax/libs/backbone.js/0.9.2/backbone-min',
    handlebars: 'http://cdnjs.cloudflare.com/ajax/libs/handlebars.js/1.0.rc.1/handlebars.min',
    bootstrap: 'http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.2.1/bootstrap.min',
    moment: 'http://cdnjs.cloudflare.com/ajax/libs/moment.js/1.7.2/moment.min',
    text: 'js/libs/require/text',
    jqueryeventdrag: 'js/libs/jquery/jquery.event.drag',
    'slickgrid-core': 'js/libs/slickgrid/slick.core',
    slickgrid: 'js/libs/slickgrid/slick.grid',
    'slickgrid-pager': 'js/libs/slickgrid/slick.pager', 
    slickback: 'js/libs/slickback/slickback.full',
    json: 'js/libs/require/json'
  },
  shim: {
    'jquery': { exports: '$' },
    'dynatree': {
      deps: ['jquery', 'jqueryui']
    },
    'underscore': { exports: '_' },
    'backbone': {
      deps: ['underscore', 'jquery'],
      exports: 'Backbone'
    },
    'jqueryeventdrag': {
      deps: ['jquery']
    },
    'slickgrid-pager': {
      deps: ['jquery']
    },
    'slickgrid': {
      deps: ['jquery', 'jqueryeventdrag', 'jqueryui', 'slickgrid-pager'],
      exports: 'Slick'
    },
    'slickgrid-core': {
      deps: ['jquery']
    },
    'slickback': {
      deps: ['backbone', 'slickgrid', 'slickgrid-core'],
      exports: 'Slickback'
    },
    'bootstrap': { deps: ['jquery'] },
    'handlebars': { exports: 'Handlebars' },
    'jqueryui': { deps: ['jquery'] },
    'moment': { exports: 'moment'}
  }
}