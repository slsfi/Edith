window.rconfig = {
  paths: {
    jquery: 'libs/jquery/jquery.min',
    jqueryui: 'libs/jquery/jquery-ui',
    dynatree: 'libs/dynatree/jquery.dynatree-1.2.4',
    underscore: 'libs/underscore/underscore',
    backbone: 'libs/backbone/backbone.min',
    handlebars: 'libs/handlebars/handlebars.min',
    bootstrap: 'libs/bootstrap/bootstrap.min',
    moment: 'libs/moment/moment.min',
    text: 'libs/require/text',
    jqueryeventdrag: 'libs/jquery/jquery.event.drag',
    'slickgrid-core': 'libs/slickgrid/slick.core',
    slickgrid: 'libs/slickgrid/slick.grid',
    'slickgrid-pager': 'libs/slickgrid/slick.pager',
    'slickgrid-formatters': 'libs/slickgrid/slick.formatters',
    'slickgrid-rowselectionmodel': 'libs/slickgrid/slick.rowselectionmodel',
    slickback: 'libs/slickback/slickback.full',
    json: 'libs/require/json',
    sprintf: 'libs/sprintf/sprintf'
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
    'slickgrid-formatters': {
      deps: ['jquery']
    },
    'slickgrid-rowselectionmodel': {
      deps: ['jquery']
    },
    'slickgrid': {
      deps: ['jquery', 'jqueryeventdrag', 'jqueryui', 'slickgrid-pager', 'slickgrid-formatters',
             'slickgrid-rowselectionmodel'],
      exports: 'Slick'
    },
    'slickgrid-core': {
      deps: ['jquery']
    },
    'slickback': {
      deps: ['backbone', 'slickgrid', 'slickgrid-core'],
      exports: 'Slickback'
    },
    'sprintf': {
      exports: 'sprintf'
    },
    'bootstrap': { deps: ['jquery'] },
    'handlebars': { exports: 'Handlebars' },    
    'jqueryui': { deps: ['jquery'] },
    'moment': { exports: 'moment'}
  }
}