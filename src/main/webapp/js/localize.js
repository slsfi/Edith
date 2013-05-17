define(['jquery', 'handlebars', 'json!/api/localizations', 'sprintf'],
  function($, Handlebars, localizations) {
    
    var localize = function(key) {
      var args = Array.prototype.slice.call(arguments);
      args[0] = localizations[key];
      return args[0] ? sprintf.apply(this, args) : key;      
    };
  
    Handlebars.registerHelper('localize', function() {
      return localize.apply(this, arguments);
    });
    
    return localize;
});
